package com.keywood.localpay.payment;

import com.keywood.localpay.exceptions.InsufficientFundsException;
import com.keywood.localpay.model.Account;
import com.keywood.localpay.model.Cancellation;
import com.keywood.localpay.model.Confirmation;
import com.keywood.localpay.model.Transaction;

import java.util.UUID;

public class TransactionService {

    public void processTransaction(Transaction transaction, UUID nodeId, Account account) throws InsufficientFundsException {

        if (nodeId.equals(transaction.senderNodeId())) {
            account.createPendingOutgoing(transaction);
        }
        else if (nodeId.equals(transaction.receiverNodeId())) {
            account.acceptIncoming(transaction);
        }

    }

    public void processConfirmation(Confirmation confirmation, UUID nodeId, Account account) {
        if (nodeId.equals(confirmation.receiverNodeId())) {
            account.confirmPendingOutgoing(confirmation);
        }
    }

    public void processCancellation(Cancellation cancellation, UUID nodeId, Account account) {
        if (nodeId.equals(cancellation.receiverNodeId())) {
            account.cancelPendingOutgoing(cancellation);
        }
    }

}
