package com.keywood.localpay.model;

import com.keywood.localpay.simulation.Network;
import com.keywood.localpay.services.SelectionService;
import com.keywood.localpay.services.TransactionService;

import java.util.List;
import java.util.UUID;

public class Node {
    // treat nodeId as it's IP address in the network
    private final UUID nodeId;
    private final Account account;
    private final Network network;

    private final TransactionService transactionService;
    private final SelectionService selectionService;

    // cant be final, how can we populate nodeIdList
    // at initialisation, since we don't know any
    // other nodeIds
    private List<UUID> nodeIdList;

    public Node(UUID nodeId,
                Account account,
                Network network,
                TransactionService transactionService,
                SelectionService selectionService,
                List<UUID> nodeIdList) {

        this.nodeId = nodeId;
        this.account = account;
        this.network = network;
        this.transactionService = transactionService;
        this.selectionService = selectionService;
        this.nodeIdList = nodeIdList;

        network.openConnection(nodeId, this);
    }

    // public void processMessage(byte[] data)

    public void processTransaction(Transaction transaction) {
        boolean successful = this.transactionService.processTransaction(transaction, this.nodeId, this.account);

        if (!successful) {
            UUID targetNodeId = transaction.receiverNodeId();
            UUID nextNodeId = selectionService.selectNode(targetNodeId, nodeIdList);
            network.sendTransaction(this.nodeId, nextNodeId, transaction);
        }
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
