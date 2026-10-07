package com.keywood.localpay.user;

import com.keywood.localpay.exceptions.InsufficientFundsException;
import com.keywood.localpay.exceptions.UnreachableNodeException;
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

    public void receiveMessage(Message message) throws UnreachableNodeException, InsufficientFundsException {

        if (message instanceof Transaction transaction) {
            // if transaction is from one node to itself, let's just return
            // no action required! probably a better way to handle this
            if (transaction.senderNodeId().equals(transaction.receiverNodeId())) {
                return;
            }

            // update seenTransactions
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
                this.nodeIdList,
                this.seenTransactions
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
