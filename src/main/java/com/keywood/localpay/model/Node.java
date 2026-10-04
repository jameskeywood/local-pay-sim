package com.keywood.localpay.model;

import com.keywood.localpay.exceptions.InsufficientFundsException;
import com.keywood.localpay.exceptions.UnreachableNodeException;
import com.keywood.localpay.routing.RoutingService;
import com.keywood.localpay.simulation.Network;
import com.keywood.localpay.routing.SelectionService;
import com.keywood.localpay.payment.TransactionService;

import java.util.*;

public class Node {

    // treat nodeId as it's IP address in the network
    private final UUID nodeId;
    private final Account account;
    private final Network network;

    private final TransactionService transactionService;
    private final RoutingService routingService;
    private final SelectionService selectionService;

    // cant be final, how can we populate nodeIdList
    // at initialisation, since we don't know any
    // other nodeIds
    private List<UUID> nodeIdList;

    private final Map<UUID, Integer> seenTransactions;

    public Node(UUID nodeId,
                Account account,
                Network network,
                TransactionService transactionService,
                RoutingService routingService,
                SelectionService selectionService,
                List<UUID> nodeIdList) {

        this.nodeId = nodeId;
        this.account = account;
        this.network = network;
        this.transactionService = transactionService;
        this.routingService = routingService;
        this.selectionService = selectionService;
        this.nodeIdList = nodeIdList;

        this.seenTransactions = new HashMap<>();

        network.openConnection(nodeId, this);
    }

    // public void receiveMessage(byte[] data)

    public void receiveTransaction(Transaction transaction) throws UnreachableNodeException, InsufficientFundsException {

        // if transaction is from one node to itself, let's just return
        // no action required! probably a better way to handle this
        if (transaction.senderNodeId().equals(transaction.receiverNodeId())) {
            return;
        }

        // update seenTransactions
        this.seenTransactions.merge(transaction.transactionId(), 1, Integer::sum);

        this.transactionService.processTransaction(
                transaction,
                this.nodeId,
                this.account
        );

        this.routingService.routeTransaction(
                transaction,
                this.nodeId,
                this.network,
                this.selectionService,
                this.nodeIdList,
                this.seenTransactions
        );
    }

    public void receiveConfirmation(Confirmation confirmation) throws UnreachableNodeException, InsufficientFundsException {

        this.transactionService.processConfirmation(
                confirmation,
                this.nodeId,
                this.account
        );

        this.routingService.routeConfirmation(
                confirmation,
                this.nodeId,
                this.network,
                this.selectionService,
                this.nodeIdList
        );

    }

    public void receiveCancellation(Cancellation cancellation) throws UnreachableNodeException, InsufficientFundsException {

        this.transactionService.processCancellation(
                cancellation,
                this.nodeId,
                this.account
        );

        this.routingService.routeCancellation(
                cancellation,
                this.nodeId,
                this.network,
                this.selectionService,
                this.nodeIdList
        );

    }

    public UUID getNodeId() {
        return this.nodeId;
    }

    public Account getAccount() {
        return this.account;
    }

    public void setNodeIdList(List<UUID> nodeIdList) {
        this.nodeIdList = nodeIdList;
    }
}
