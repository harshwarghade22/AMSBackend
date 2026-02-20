package com.harshwarghade.project.dto;

import com.harshwarghade.project.entity.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponse implements java.io.Serializable {

    private Double amount;
    private TransactionType type;
    private LocalDateTime timestamp;
    private String accountNumber;
    private Long accountId;
}
