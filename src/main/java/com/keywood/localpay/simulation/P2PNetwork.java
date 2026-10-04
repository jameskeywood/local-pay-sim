package com.keywood.localpay.simulation;

import com.keywood.localpay.exceptions.UnreachableNodeException;
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
    public void sendTransaction(UUID senderNodeId, UUID receiverNodeId, Transaction transaction) throws UnreachableNodeException {

        Node receiverNode = nodeIdMapping.get(receiverNodeId);
        receiverNode.processTransaction(transaction);

        // notice the order of the logs
        // I feel like the above line should be in a separate thread, rather than a function call

        logger.info("Node {} sent Node {} {}", senderNodeId, receiverNodeId, transaction);
    }
}
