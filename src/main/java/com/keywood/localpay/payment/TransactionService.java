package com.keywood.localpay.payment;

import com.keywood.localpay.exceptions.InsufficientFundsException;
import com.keywood.localpay.model.Message;
import com.keywood.localpay.user.Account;
import com.keywood.localpay.model.Cancellation;
import com.keywood.localpay.model.Confirmation;
import com.keywood.localpay.model.Transaction;

import java.util.UUID;

public class TransactionService {

    public void processMessage(Message message, UUID nodeId, Account account) throws InsufficientFundsException {

        switch (message) {
            case Transaction transaction ->
                processTransaction(transaction, nodeId, account);

            case Confirmation confirmation ->
                processConfirmation(confirmation, nodeId, account);

            case Cancellation cancellation ->
                processCancellation(cancellation, nodeId, account);
        }

    }

    private void processTransaction(Transaction transaction, UUID nodeId, Account account) throws InsufficientFundsException {

        if (nodeId.equals(transaction.senderNodeId())) {
            account.createPendingOutgoing(transaction);
        }
        else if (nodeId.equals(transaction.receiverNodeId())) {
            account.acceptIncoming(transaction);
        }

    }

    private void processConfirmation(Confirmation confirmation, UUID nodeId, Account account) {
        if (nodeId.equals(confirmation.receiverNodeId())) {
            account.confirmPendingOutgoing(confirmation);
        }
    }

    private void processCancellation(Cancellation cancellation, UUID nodeId, Account account) {
        if (nodeId.equals(cancellation.receiverNodeId())) {
            account.cancelPendingOutgoing(cancellation);
        }
    }

}
