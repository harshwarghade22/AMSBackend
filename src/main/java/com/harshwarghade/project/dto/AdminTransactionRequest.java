package com.harshwarghade.project.dto;

import com.harshwarghade.project.entity.TransactionType;
import lombok.Data;

@Data
public class AdminTransactionRequest {

    private Long accountId;
    private Double amount;
    private TransactionType type; // DEPOSIT, WITHDRAW, TRANSFER
}
