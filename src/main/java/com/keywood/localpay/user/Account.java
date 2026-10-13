package com.keywood.localpay.user;

import com.keywood.localpay.exceptions.InsufficientFundsException;
import com.keywood.localpay.model.Cancellation;
import com.keywood.localpay.model.Confirmation;
import com.keywood.localpay.model.Transaction;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Account {
    private final UUID accountId;
    private BigDecimal balance;

    private final Map<UUID, BigDecimal> pending;

    public Account(UUID accountId, BigDecimal balance) {
        this.accountId = accountId;
        this.balance = balance;
        this.pending = new HashMap<>();
    }

    public BigDecimal getBalance() {
        BigDecimal pendingPayments = this.pending.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal balanceWithPending = this.balance.subtract(pendingPayments);

        return balanceWithPending;
    }

    public void createPendingOutgoing(Transaction transaction) throws InsufficientFundsException {
        BigDecimal newBalance = this.getBalance().subtract(transaction.amount());

        if (newBalance.compareTo(BigDecimal.ZERO) >= 0) {
            this.pending.put(transaction.transactionId(), transaction.amount());
        }
        else {
            throw new InsufficientFundsException("Account does not have sufficient funds for the transaction");
        }
    }

    public void cancelPendingOutgoing(Cancellation cancellation) {
        this.pending.remove(cancellation.transactionId());
    }

    public void confirmPendingOutgoing(Confirmation confirmation) {
        BigDecimal amount = pending.get(confirmation.transactionId());
        this.balance = this.balance.subtract(amount);
        this.pending.remove(confirmation.transactionId());
    }

    public void acceptIncoming(Transaction transaction) {
        this.balance = this.balance.add(transaction.amount());
    }
}
