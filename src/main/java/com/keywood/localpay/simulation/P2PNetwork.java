package com.keywood.localpay.simulation;

import com.keywood.localpay.exceptions.InsufficientFundsException;
import com.keywood.localpay.exceptions.UnreachableNodeException;
import com.keywood.localpay.model.Cancellation;
import com.keywood.localpay.model.Confirmation;
import com.keywood.localpay.model.Node;
import com.keywood.localpay.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class P2PNetwork implements Network {

    private static final Logger logger = LoggerFactory.getLogger(P2PNetwork.class);

    private final Map<UUID, Node> nodeIdMapping = new HashMap<>();

    public void openConnection(UUID nodeId, Node node) {
        this.nodeIdMapping.put(nodeId, node);
    }

    // note that the receiver might not be the actual transaction receiver, same with sender!
    public void sendTransaction(UUID senderNodeId, UUID receiverNodeId, Transaction transaction) throws UnreachableNodeException, InsufficientFundsException {

        Node receiverNode = nodeIdMapping.get(receiverNodeId);
        receiverNode.receiveTransaction(transaction);

        // notice the order of the logs
        // I feel like the above line should be in a separate thread, rather than a function call

        this.logNetworkMessage(senderNodeId, receiverNodeId, transaction);
    }

    public void sendConfirmation(UUID senderNodeId, UUID receiverNodeId, Confirmation confirmation) throws UnreachableNodeException, InsufficientFundsException {

        Node receiverNode = nodeIdMapping.get(receiverNodeId);
        receiverNode.receiveConfirmation(confirmation);

        this.logNetworkMessage(senderNodeId, receiverNodeId, confirmation);
    }

    public void sendCancellation(UUID senderNodeId, UUID receiverNodeId, Cancellation cancellation) throws UnreachableNodeException, InsufficientFundsException {

        Node receiverNode = nodeIdMapping.get(receiverNodeId);
        receiverNode.receiveCancellation(cancellation);

        this.logNetworkMessage(senderNodeId, receiverNodeId, cancellation);
    }

    private <T> void logNetworkMessage(UUID senderNodeId, UUID receiverNodeId, T message) {
        logger.info("Node {} sent Node {} {}", senderNodeId, receiverNodeId, message);
    }
}
