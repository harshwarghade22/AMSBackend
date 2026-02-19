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
        try {
            Transaction transaction = new Transaction();
            transaction.setAmount(amount);
            transaction.setType(type);
            transaction.setTimestamp(LocalDateTime.now());
            transaction.setAccount(account);
            transactionRepository.save(transaction);
        } catch (Exception e) {
            
            throw new RuntimeException("Failed to record transaction: " + e.getMessage(), e);
        }
    }

    // Get transaction history for an account
    public List<TransactionResponse> getTransactionsByAccount(Long accountId) {
        try {
            List<Transaction> transactions = transactionRepository.findByAccountId(accountId);
            return transactions.stream()
                    .map(tx -> new TransactionResponse(
                            tx.getAmount(),
                            tx.getType(),
                            tx.getTimestamp(),
                            tx.getAccount().getAccountNumber()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            
            throw new RuntimeException("Failed to fetch transactions: " + e.getMessage(), e);
        }
    }

    public com.harshwarghade.project.dto.PageResponse<TransactionResponse> getAllTransactions(int page, int size) {
        try {
            org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
            org.springframework.data.domain.Page<com.harshwarghade.project.entity.Transaction> transactionPage = transactionRepository.findAll(pageable);
            java.util.List<TransactionResponse> content = transactionPage.getContent().stream()
                    .map(tx -> new TransactionResponse(
                            tx.getAmount(),
                            tx.getType(),
                            tx.getTimestamp(),
                            tx.getAccount().getAccountNumber()))
                    .collect(java.util.stream.Collectors.toList());
            com.harshwarghade.project.dto.PageResponse<TransactionResponse> response = new com.harshwarghade.project.dto.PageResponse<>();
            response.setContent(content);
            response.setLast(transactionPage.isLast());
            response.setNumber(transactionPage.getNumber());
            response.setTotalPages(transactionPage.getTotalPages());
            response.setSize(transactionPage.getSize());
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch transactions: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void createManualTransaction(Long accountId, Double amount, TransactionType type) {
        try {
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
        } catch (Exception e) {
            // Log and rethrow or handle as needed
            throw new RuntimeException("Failed to create manual transaction: " + e.getMessage(), e);
        }
    }
}
