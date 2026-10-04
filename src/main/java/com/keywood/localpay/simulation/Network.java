package com.keywood.localpay.simulation;

import com.keywood.localpay.exceptions.UnreachableNodeException;
import com.keywood.localpay.model.Node;
import com.keywood.localpay.model.Transaction;

import java.util.UUID;

public interface Network {
    // public void sendMessage(byte[] data)

    void openConnection(UUID nodeId, Node node);

    void sendTransaction(UUID senderNodeId, UUID receiverNodeId, Transaction transaction) throws UnreachableNodeException;
}
