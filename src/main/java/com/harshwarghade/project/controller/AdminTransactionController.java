package com.harshwarghade.project.controller;

import com.harshwarghade.project.dto.AdminTransactionRequest;
import com.harshwarghade.project.dto.TransactionResponse;
import com.harshwarghade.project.service.TransactionService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/transactions")
@RequiredArgsConstructor
public class AdminTransactionController {

    private final TransactionService transactionService;

    // 👑 ADMIN ONLY: Manual transaction entry
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String createTransaction(@RequestBody AdminTransactionRequest request) {

        transactionService.createManualTransaction(
                request.getAccountId(),
                request.getAmount(),
                request.getType());

        return "Transaction created successfully";
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public com.harshwarghade.project.dto.PageResponse<TransactionResponse> getAllTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return transactionService.getAllTransactions(page, size);
    }

}
