package com.keywood.localpay.user;

import com.keywood.localpay.exceptions.InsufficientFundsException;
import com.keywood.localpay.model.Message;
import com.keywood.localpay.model.Transaction;
import com.keywood.localpay.routing.RoutingService;
import com.keywood.localpay.simulation.Network;
import com.keywood.localpay.routing.SelectionService;
import com.keywood.localpay.payment.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Node implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(Node.class);

    // treat nodeId as it's IP address in the network
    private final UUID nodeId;
    private final Account account;
    private final Network network;

    private final TransactionService transactionService;
    private final RoutingService routingService;
    private final SelectionService selectionService;

    private final Set<UUID> nodeIdSet;

    private final Map<UUID, Integer> seenTransactions;

    private final BlockingQueue<Message> mailbox;

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

        this.mailbox = new LinkedBlockingQueue<>();

        network.openConnection(nodeId, this);
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Message message = mailbox.take();
                this.receiveMessage(message);
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            catch (InsufficientFundsException e) {
                // should log here
            }
        }
    }

    public void deliver(Message message) {
        this.logMessage(message);
        mailbox.offer(message);
    }

    private void receiveMessage(Message message) throws InsufficientFundsException {

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

    private <T> void logMessage(T message) {
        logger.info("Node {} received {}", this.nodeId, message);
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
