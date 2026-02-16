// package com.harshwarghade.project.service;

// import com.harshwarghade.project.entity.Account;
// import com.harshwarghade.project.repository.AccountRepository;
// import lombok.RequiredArgsConstructor;
// import org.springframework.jdbc.core.JdbcTemplate;
// import org.springframework.stereotype.Service;

// import java.sql.PreparedStatement;
// import java.time.LocalDateTime;
// import java.util.List;
// import java.util.Random;

// @Service
// @RequiredArgsConstructor
// public class BulkTransactionSeederService {

//     private final JdbcTemplate jdbcTemplate;
//     private final AccountRepository accountRepository;

//     private static final int BATCH_SIZE = 5000; // OPTIMAL for 16GB RAM

//     public String seedLargeTransactions(int transactionsPerAccount) {

//         List<Account> accounts = accountRepository.findAll();

//         if (accounts.isEmpty()) {
//             throw new RuntimeException("No accounts found");
//         }

//         Random random = new Random();

//         String sql = "INSERT INTO project_db.transactions (amount, type, timestamp, account_id) VALUES (?, ?, ?, ?)";

//         long totalInserted = 0;
//         long startTime = System.currentTimeMillis();

//         for (Account account : accounts) {

//             for (int i = 0; i < transactionsPerAccount; i += BATCH_SIZE) {

//                 int currentBatchSize = Math.min(BATCH_SIZE, transactionsPerAccount - i);

//                 jdbcTemplate.batchUpdate(sql,
//                         new org.springframework.jdbc.core.BatchPreparedStatementSetter() {

//                             @Override
//                             public void setValues(PreparedStatement ps, int index) throws java.sql.SQLException {

//                                 double amount = 10 + (1000 - 10) * random.nextDouble();
//                                 String type = random.nextBoolean() ? "DEPOSIT" : "WITHDRAW";

//                                 ps.setDouble(1, amount);
//                                 ps.setString(2, type);
//                                 ps.setObject(3, LocalDateTime.now());
//                                 ps.setLong(4, account.getId());
//                             }

                            
//                             @Override
//                             public int getBatchSize() {
//                                 return currentBatchSize;
//                             }
//                         });
                
                

//                 totalInserted += currentBatchSize;
                

//                 // Progress log (VERY USEFUL)
//                 if (totalInserted % 100000 == 0) {
//                     System.out.println("Inserted: " + totalInserted + " transactions...");
//                 }
//             }
//         }

//         long endTime = System.currentTimeMillis();
//         long duration = (endTime - startTime) / 1000;

//         return "SUCCESS: Inserted " + totalInserted + " transactions in " + duration + " seconds";
//     }
// }



package com.harshwarghade.project.service;

import com.harshwarghade.project.entity.Account;
import com.harshwarghade.project.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class BulkTransactionSeederService {

    private final JdbcTemplate jdbcTemplate;
    private final AccountRepository accountRepository;

    // Optimized for 16GB RAM + Local MySQL
    private static final int BATCH_SIZE = 1000;
    private static final int THREAD_COUNT = 100;
    private static final LocalDateTime FIXED_TIME = LocalDateTime.now();


    private static final String SQL =
            "INSERT INTO transactions (amount, type, timestamp, account_id) VALUES (?, ?, ?, ?)";

    public String seedLargeTransactions(int transactionsPerAccount) throws InterruptedException {

        List<Account> accounts = accountRepository.findAll();

        if (accounts.isEmpty()) {
            throw new RuntimeException("No accounts found");
        }

        long startTime = System.currentTimeMillis();
        System.out.println("Starting bulk transaction seeding...");
        System.out.println("Total Accounts: " + accounts.size());
        System.out.println("Transactions per Account: " + transactionsPerAccount);

        // Create thread pool (optimized for 16GB machine)
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

        int chunkSize = Math.max(1, accounts.size() / THREAD_COUNT);

        for (int i = 0; i < accounts.size(); i += chunkSize) {
            int end = Math.min(i + chunkSize, accounts.size());
            List<Account> subList = accounts.subList(i, end);

            executor.submit(() -> processAccounts(subList, transactionsPerAccount));
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.HOURS);

        long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime) / 1000;

        return "SUCCESS: Bulk seeding completed in " + duration + " seconds";
    }

    private void processAccounts(List<Account> accounts, int transactionsPerAccount) {

        long localCount = 0;

        for (Account account : accounts) {

            for (int i = 0; i < transactionsPerAccount; i += BATCH_SIZE) {

                int currentBatchSize = Math.min(BATCH_SIZE, transactionsPerAccount - i);

                // Cache timestamp per batch (huge CPU optimization)
                LocalDateTime batchTimestamp = LocalDateTime.now();

                jdbcTemplate.batchUpdate(SQL,
                        new org.springframework.jdbc.core.BatchPreparedStatementSetter() {

                            @Override
                            public void setValues(PreparedStatement ps, int index) throws java.sql.SQLException {

                                // ThreadLocalRandom is MUCH faster than Random
                                ThreadLocalRandom random = ThreadLocalRandom.current();

                                double amount = random.nextDouble(10, 1000);
                                String type = random.nextBoolean() ? "DEPOSIT" : "WITHDRAW";

                                ps.setDouble(1, amount);
                                ps.setString(2, type);
                                ps.setObject(3, FIXED_TIME);
                                ps.setLong(4, account.getId());
                            }

                            @Override
                            public int getBatchSize() {
                                return currentBatchSize;
                            }
                        });

                localCount += currentBatchSize;

                // Progress logging per thread (lightweight)
                if (localCount % 100000 == 0) {
                    System.out.println(Thread.currentThread().getName() +
                            " inserted: " + localCount + " transactions");
                }
            }
        }
    }
}
