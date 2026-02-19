// // import java.util.Queue;
// package com.harshwarghade.project.config;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.amqp.core.Queue;
// @Configuration
// public class RabbitConfig {

//     public static final String TXN_QUEUE = "bank.txn.queue";

//     @Bean
//     public Queue txnQueue() {
//         return new Queue(TXN_QUEUE, true); // Durable queue
//     }
// }
