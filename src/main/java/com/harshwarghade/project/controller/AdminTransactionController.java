package com.harshwarghade.project.controller;

import com.harshwarghade.project.dto.AdminTransactionRequest;
import com.harshwarghade.project.service.TransactionService;
import lombok.RequiredArgsConstructor;
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
                request.getType()
        );

        return "Transaction created successfully";
    }
}
