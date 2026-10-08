package com.keywood.localpay.user;

import com.keywood.localpay.exceptions.InsufficientFundsException;
import com.keywood.localpay.model.Message;
import com.keywood.localpay.model.Transaction;
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

    private final Set<UUID> nodeIdSet;

    private final Map<UUID, Integer> seenTransactions;

    public Node(UUID nodeId,
                Account account,
                Network network,
                TransactionService transactionService,
                RoutingService routingService,
                SelectionService selectionService) {

        this.nodeId = nodeId;
        this.account = account;
        this.network = network;
        this.transactionService = transactionService;
        this.routingService = routingService;
        this.selectionService = selectionService;

        this.nodeIdSet = new HashSet<>();
        this.nodeIdSet.add(this.nodeId); // the only node it knows about is itself, upon instantiation

        this.seenTransactions = new HashMap<>();

        network.openConnection(nodeId, this);
    }

    public void receiveMessage(Message message) throws InsufficientFundsException {

        if (message instanceof Transaction transaction) {
            this.seenTransactions.merge(transaction.transactionId(), 1, Integer::sum);
        }

        this.transactionService.processMessage(
                message,
                this.nodeId,
                this.account
        );

        this.routingService.routeMessage(
                message,
                this.nodeId,
                this.network,
                this.selectionService,
                this.nodeIdSet,
                this.seenTransactions
        );
    }

    public UUID getNodeId() {
        return this.nodeId;
    }

    public Account getAccount() {
        return this.account;
    }

    public Set<UUID> getNodeIdSet() {
        return this.nodeIdSet;
    }

    public void addToNodeIdSet(UUID nodeId) {
        this.nodeIdSet.add(nodeId);
    }

    public void bulkAddToNodeIdSet(Set<UUID> nodeIdSet) {
        this.nodeIdSet.addAll(nodeIdSet);
    }

    public void removeFromNodeIdSet(UUID nodeId) {
        this.nodeIdSet.remove(nodeId);
    }

    public void bulkRemoveFromNodeIdSet(Set<UUID> nodeIdSet) {
        this.nodeIdSet.removeAll(nodeIdSet);
    }
}
