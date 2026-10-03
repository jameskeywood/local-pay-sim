package com.keywood.localpay.model;

import java.math.BigDecimal;
import java.util.UUID;

public class Account {
    private UUID accountId;
    private BigDecimal balance;

    public Account(UUID accountId) {
        this.accountId = accountId;
        this.balance = BigDecimal.ZERO;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
