package com.harshwarghade.project.service;

import com.harshwarghade.project.dto.TransactionResponse;
import com.harshwarghade.project.entity.*;
import com.harshwarghade.project.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    // Record a transaction (used internally)
    public void recordTransaction(Account account, Double amount, TransactionType type) {

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setType(type);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setAccount(account);

        transactionRepository.save(transaction);
    }

    // Get transaction history for an account
    public List<TransactionResponse> getTransactionsByAccount(Long accountId) {

        List<Transaction> transactions = transactionRepository.findByAccountId(accountId);

        return transactions.stream()
                .map(tx -> new TransactionResponse(
                        tx.getAmount(),
                        tx.getType(),
                        tx.getTimestamp()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void createManualTransaction(Long accountId, Double amount, TransactionType type) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // Optional: Update balance based on transaction type
        if (type == TransactionType.DEPOSIT) {
            account.setBalance(account.getBalance() + amount);
        } else if (type == TransactionType.WITHDRAW) {
            if (account.getBalance() < amount) {
                throw new RuntimeException("Insufficient balance");
            }
            account.setBalance(account.getBalance() - amount);
        }

        // Save updated account balance
        accountRepository.save(account);

        // Record transaction in ledger
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setType(type);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setAccount(account);

        transactionRepository.save(transaction);
    }
}
