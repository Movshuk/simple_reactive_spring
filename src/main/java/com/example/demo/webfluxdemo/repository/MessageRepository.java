package com.example.demo.webfluxdemo.repository;

import com.example.demo.webfluxdemo.model.Message;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends ReactiveMongoRepository<Message, String> {

}
