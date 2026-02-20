package com.harshwarghade.project.controller;

import com.harshwarghade.project.dto.PageResponse;
import com.harshwarghade.project.dto.TransactionResponse;
import com.harshwarghade.project.entity.Account;
import com.harshwarghade.project.entity.User;
import com.harshwarghade.project.repository.AccountRepository;
import com.harshwarghade.project.repository.UserRepository;
import com.harshwarghade.project.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    // USER: View transactions of their own account
    @GetMapping("/{accountId}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public PageResponse<TransactionResponse> getTransactions(
            @PathVariable Long accountId,
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // 🔒 Security Check: user can only see their own account transactions
        if (!account.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied to this account");
        }

        return transactionService.getTransactionsByAccount(accountId, page, size);
    }
}
