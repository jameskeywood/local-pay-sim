package com.keywood.localpay.simulation;

import com.keywood.localpay.exceptions.InsufficientFundsException;
import com.keywood.localpay.model.Message;
import com.keywood.localpay.user.Node;
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

    public void sendMessage(UUID senderNodeId, UUID receiverNodeId, Message message) throws InsufficientFundsException {

        Node receiverNode = nodeIdMapping.get(receiverNodeId);
        receiverNode.receiveMessage(message);

        this.logNetworkMessage(senderNodeId, receiverNodeId, message);
    }

    private <T> void logNetworkMessage(UUID senderNodeId, UUID receiverNodeId, T message) {
        logger.info("Node {} sent Node {} {}", senderNodeId, receiverNodeId, message);
    }
}
