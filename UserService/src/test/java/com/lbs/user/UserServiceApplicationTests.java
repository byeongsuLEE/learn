package com.lbs.user;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;

@SpringBootTest
class UserServiceApplicationTests {

    @Test
    void contextLoads() {
    }

    @KafkaListener(topics = "test-topic", groupId = "user")
    public void testListener(String message) {
        System.out.println("Consumed: " + message);
    }
}
