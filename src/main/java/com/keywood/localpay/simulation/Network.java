package com.keywood.localpay.simulation;

import com.keywood.localpay.exceptions.InsufficientFundsException;
import com.keywood.localpay.model.Message;
import com.keywood.localpay.user.Node;

import java.util.UUID;

public interface Network {

    void openConnection(UUID nodeId, Node node);

    void sendMessage(UUID senderNodeId, UUID receiverNodeId, Message message) throws InsufficientFundsException;

}
