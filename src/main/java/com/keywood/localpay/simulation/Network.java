package com.keywood.localpay.simulation;

import com.keywood.localpay.model.Node;
import com.keywood.localpay.model.Transaction;

import java.util.UUID;

public interface Network {
    // public void sendMessage(byte[] data)

    public void openConnection(UUID nodeId, Node node);

    public void sendTransaction(UUID senderNodeId, UUID receiverNodeId, Transaction transaction);
}
