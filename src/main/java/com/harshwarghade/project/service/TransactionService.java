package com.harshwarghade.project.service;

import com.harshwarghade.project.dto.PageResponse;
import com.harshwarghade.project.dto.TransactionResponse;
import com.harshwarghade.project.entity.*;
import com.harshwarghade.project.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    // 🔥 Cache paginated transactions (like users but paginated)
    @Cacheable(value = "allTransactions", key = "#page + '-' + #size")
    public PageResponse<TransactionResponse> getAllTransactions(int page, int size) {
        System.out.println("Fetching transactions from DATABASE...");

        Pageable pageable = PageRequest.of(page, size);
        Page<Transaction> transactionPage = transactionRepository.findAll(pageable);

        List<TransactionResponse> content = transactionPage.getContent()
                .stream()
                .map(tx -> new TransactionResponse(
                        tx.getAmount(),
                        tx.getType(),
                        tx.getTimestamp(),
                        tx.getAccount().getAccountNumber(),
                        tx.getAccount().getId()
                ))
                .collect(Collectors.toList());

        PageResponse<TransactionResponse> response = new PageResponse<>();
        response.setContent(content);
        response.setLast(transactionPage.isLast());
        response.setNumber(transactionPage.getNumber());
        response.setTotalPages(transactionPage.getTotalPages());
        response.setSize(transactionPage.getSize());

        return response;
    }

        // 🔥 Cache account-specific transaction history with pagination
        @Cacheable(value = "accountTransactions", key = "#accountId + '-' + #page + '-' + #size")
        public PageResponse<TransactionResponse> getTransactionsByAccount(Long accountId, int page, int size) {
        System.out.println("Fetching account transactions from DATABASE...");

        Pageable pageable = PageRequest.of(page, size);
        Page<Transaction> transactionPage = transactionRepository.findByAccountId(accountId, pageable);

        List<TransactionResponse> content = transactionPage.getContent()
            .stream()
            .map(tx -> new TransactionResponse(
                tx.getAmount(),
                tx.getType(),
                tx.getTimestamp(),
                tx.getAccount().getAccountNumber(),
                tx.getAccount().getId()
            ))
            .collect(Collectors.toList());

        PageResponse<TransactionResponse> response = new PageResponse<>();
        response.setContent(content);
        response.setLast(transactionPage.isLast());
        response.setNumber(transactionPage.getNumber());
        response.setTotalPages(transactionPage.getTotalPages());
        response.setSize(transactionPage.getSize());

        return response;
        }

    // Record a transaction (internal use)
    public void recordTransaction(Account account, Double amount, TransactionType type) {
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setType(type);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setAccount(account);

        transactionRepository.save(transaction);
    }

    // 🔥 Create transaction + smart cache eviction
    @Transactional
    @CacheEvict(value = {"allTransactions", "accountTransactions"}, allEntries = true)
    public void createManualTransaction(Long accountId, Double amount, TransactionType type) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // Update balance
        if (type == TransactionType.DEPOSIT) {
            account.setBalance(account.getBalance() + amount);
        } else if (type == TransactionType.WITHDRAW) {
            if (account.getBalance() < amount) {
                throw new RuntimeException("Insufficient balance");
            }
            account.setBalance(account.getBalance() - amount);
        }

        accountRepository.save(account);

        // Save transaction
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setType(type);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setAccount(account);

        transactionRepository.save(transaction);
    }
}