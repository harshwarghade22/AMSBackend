package com.harshwarghade.project.dto;

import com.harshwarghade.project.entity.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TransactionResponse implements java.io.Serializable {

    private Double amount;
    private TransactionType type;
    private LocalDateTime timestamp;
    private String accountNumber;
}
