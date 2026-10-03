package com.keywood.localpay.services;

import com.keywood.localpay.model.Account;
import com.keywood.localpay.model.Transaction;

import java.math.BigDecimal;
import java.util.UUID;

public class TransactionService {

    public boolean processTransaction(Transaction transaction, UUID nodeId, Account account) {

        // if transaction is from one node to itself, let's just return true
        // no action required!
        if (transaction.senderNodeId() == transaction.receiverNodeId()) {
            return true;
        }

        if (nodeId == transaction.senderNodeId()) {
            BigDecimal balance = account.getBalance();
            BigDecimal newBalance = balance.subtract(transaction.amount());
            account.setBalance(newBalance);
            return false;
        }
        else if (nodeId == transaction.receiverNodeId()) {
            BigDecimal balance = account.getBalance();
            BigDecimal newBalance = balance.add(transaction.amount());
            account.setBalance(newBalance);
            return true; // transaction is only finished once the receiver node processes it
        }
        else {
            return false;
        }
    }

}
