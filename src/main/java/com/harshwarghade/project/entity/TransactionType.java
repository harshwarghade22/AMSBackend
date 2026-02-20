package com.harshwarghade.project.entity;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public enum TransactionType implements java.io.Serializable {
    DEPOSIT,
    WITHDRAW,
    TRANSFER
}
