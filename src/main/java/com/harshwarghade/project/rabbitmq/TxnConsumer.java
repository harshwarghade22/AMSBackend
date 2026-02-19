// package com.harshwarghade.project.rabbitmq;
// import java.util.List;
// import org.springframework.amqp.rabbit.annotation.RabbitListener;
// import org.springframework.jdbc.core.JdbcTemplate;
// import org.springframework.stereotype.Component;

// import com.harshwarghade.project.dto.BankTxn;

// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

// @Component
// @RequiredArgsConstructor
// @Slf4j
// public class TxnConsumer {

//     private final JdbcTemplate jdbcTemplate;

//     private static final String INSERT_SQL = "INSERT IGNORE INTO transactions_copy " +
//             "(amount, timestamp, type, account_id, account_number, source_txn_id) " +
//             "VALUES (?, ?, ?, ?, ?, ?)";

//     @RabbitListener(queues = "bank.txn.queue")
//     public void consume(List<BankTxn> transactions) {

//         log.info("Received batch of size: {}", transactions.size());

//         jdbcTemplate.batchUpdate(
//                 INSERT_SQL,
//                 transactions,
//                 5000,
//                 (ps, txn) -> {
//                     ps.setDouble(1, txn.getAmount());
//                     ps.setObject(2, txn.getTimestamp());
//                     ps.setString(3, txn.getType());

//                     if (txn.getAccountId() != null) {
//                         ps.setLong(4, txn.getAccountId());
//                     } else {
//                         ps.setNull(4, java.sql.Types.BIGINT);
//                     }

//                     ps.setString(5, txn.getAccountNumber());
//                     ps.setLong(6, txn.getId());
//                 });

//         log.info("Batch inserted successfully.");
//     }
// }
