package com.keywood.localpay.routing;

import com.keywood.localpay.exceptions.InsufficientFundsException;
import com.keywood.localpay.exceptions.UnreachableNodeException;
import com.keywood.localpay.model.Cancellation;
import com.keywood.localpay.model.Confirmation;
import com.keywood.localpay.model.Message;
import com.keywood.localpay.model.Transaction;
import com.keywood.localpay.simulation.Network;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RoutingService {

    public void routeMessage(Message message,
                             UUID nodeId,
                             Network network,
                             SelectionService selectionService,
                             List<UUID> nodeIdList,
                             Map<UUID, Integer> seenTransactions) throws UnreachableNodeException, InsufficientFundsException {

        if (message instanceof Transaction transaction) {
            if (seenTransactions.get(transaction.transactionId()) > 3) {
                Cancellation cancellation = new Cancellation(
                        UUID.randomUUID(),
                        transaction.transactionId(),
                        transaction.receiverNodeId(), // flipped
                        transaction.senderNodeId()
                );

                network.sendMessage(nodeId, nodeId, cancellation);
                return;
            } else if (nodeId.equals(transaction.receiverNodeId())) {

                Confirmation confirmation = new Confirmation(
                        UUID.randomUUID(),
                        transaction.transactionId(),
                        transaction.receiverNodeId(), // flipped
                        transaction.senderNodeId()
                );

                network.sendMessage(nodeId, nodeId, confirmation);
                return;
            }
        }


        if (!nodeId.equals(message.receiverNodeId())) {
            UUID targetNodeId = message.receiverNodeId();
            UUID nextNodeId = selectionService.selectNode(targetNodeId, nodeIdList);
            network.sendMessage(nodeId, nextNodeId, message);
        }
    }
}
