package com.keywood.localpay.simulation;

import com.keywood.localpay.model.Message;
import com.keywood.localpay.user.Node;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class P2PNetwork implements Network {

    private final Map<UUID, Node> nodeIdMapping = new HashMap<>();

    public void openConnection(UUID nodeId, Node node) {
        this.nodeIdMapping.put(nodeId, node);
    }

    public void sendMessage(UUID senderNodeId, UUID receiverNodeId, Message message) {
        Node receiverNode = nodeIdMapping.get(receiverNodeId);
        receiverNode.deliver(message);
    }

}
