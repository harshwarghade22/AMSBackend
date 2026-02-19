// package com.harshwarghade.project.rabbitmq;
// import java.util.List;
// import org.springframework.amqp.rabbit.core.RabbitTemplate;
// import org.springframework.stereotype.Service;

// import com.harshwarghade.project.dto.BankTxn;

// import lombok.RequiredArgsConstructor;

// @Service
// @RequiredArgsConstructor
// public class TxnProducer {

//     private final RabbitTemplate rabbitTemplate;

//     public void sendBatch(List<BankTxn> transactions) {
//         rabbitTemplate.convertAndSend("bank.txn.queue", transactions);
//     }
// }
