package com.example.demo.webfluxdemo.controller;

import com.example.demo.webfluxdemo.model.Message;
import com.example.demo.webfluxdemo.repository.MessageRepository;
import java.time.Duration;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class MessageController {

  @Autowired
  private MessageRepository messageRepository;

  @GetMapping("/messages")
  public Flux<Message> getAllMessages() {
    return messageRepository.findAll();
  }

  @PostMapping("/messages")
  public Mono<Message> createMessages(
//      @Valid @RequestBody Message message
  ) {
    Message message = new Message();
    message.setText("TEXT");
    return messageRepository.save(message);
  }

  @GetMapping("/messages/{id}")
  public Mono<ResponseEntity<Message>> getMessageById(@PathVariable(value = "id") String messageId) {
    return messageRepository.findById(messageId)
        .map(savedMessage -> ResponseEntity.ok(savedMessage))
        .defaultIfEmpty(ResponseEntity.notFound().build());
  }

  @PutMapping("/messages/{id}")
  public Mono<ResponseEntity<Message>> updateMessage(@PathVariable(value = "id") String messageId,
      @Valid @RequestBody Message message) {
    return messageRepository.findById(messageId)
        .flatMap(existingMessage -> {
          existingMessage.setText(message.getText());
          return messageRepository.save(existingMessage);
        })
        .map(updatedMessage -> new ResponseEntity<>(updatedMessage, HttpStatus.OK))
        .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @DeleteMapping("/messages/{id}")
  public Mono<ResponseEntity<Void>> deleteMessage(@PathVariable(value = "id") String messageId) {

    return messageRepository.findById(messageId)
        .flatMap(existingMessage ->
            messageRepository.delete(existingMessage)
                .then(Mono.just(new ResponseEntity<Void>(HttpStatus.OK)))
        )
        .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @GetMapping(value = "/stream/messages", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<Message> streamAllMessages() {

    Flux<Message> messageFlux = messageRepository.findAll()
        .delayElements(
            Duration.ofSeconds(2)
        ).doOnNext(System.out::println);
    return messageFlux;
  }

}
