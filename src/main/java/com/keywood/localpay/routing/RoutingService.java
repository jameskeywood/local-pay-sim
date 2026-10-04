package com.keywood.localpay.routing;

import com.keywood.localpay.exceptions.InsufficientFundsException;
import com.keywood.localpay.exceptions.UnreachableNodeException;
import com.keywood.localpay.model.Cancellation;
import com.keywood.localpay.model.Confirmation;
import com.keywood.localpay.model.Transaction;
import com.keywood.localpay.simulation.Network;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RoutingService {

    public void routeTransaction(Transaction transaction,
                                 UUID nodeId,
                                 Network network,
                                 SelectionService selectionService,
                                 List<UUID> nodeIdList,
                                 Map<UUID, Integer> seenTransactions) throws UnreachableNodeException, InsufficientFundsException {

        if (seenTransactions.get(transaction.transactionId()) > 3) {
            Cancellation cancellation = new Cancellation(
                    UUID.randomUUID(),
                    transaction.transactionId(),
                    transaction.receiverNodeId(), // flipped
                    transaction.senderNodeId()
            );

            //routeCancellation(cancellation, nodeId, network, selectionService, nodeIdList);
            network.sendCancellation(nodeId, nodeId, cancellation);
        }
        else if (nodeId.equals(transaction.receiverNodeId())) {

            Confirmation confirmation = new Confirmation(
                    UUID.randomUUID(),
                    transaction.transactionId(),
                    transaction.receiverNodeId(), // flipped
                    transaction.senderNodeId()
            );

            //routeConfirmation(confirmation, nodeId, network, selectionService, nodeIdList);
            network.sendConfirmation(nodeId, nodeId, confirmation);
        }
        else {
            UUID targetNodeId = transaction.receiverNodeId();
            UUID nextNodeId = selectionService.selectNode(targetNodeId, nodeIdList);
            network.sendTransaction(nodeId, nextNodeId, transaction);
        }
    }

    public void routeConfirmation(Confirmation confirmation,
                                  UUID nodeId,
                                  Network network,
                                  SelectionService selectionService,
                                  List<UUID> nodeIdList) throws UnreachableNodeException, InsufficientFundsException {

        if (nodeId.equals(confirmation.receiverNodeId())) {
            return;
        }
        else {
            UUID targetNodeId = confirmation.receiverNodeId();
            UUID nextNodeId = selectionService.selectNode(targetNodeId, nodeIdList);
            network.sendConfirmation(nodeId, nextNodeId, confirmation);
        }

    }

    public void routeCancellation(Cancellation cancellation,
                                  UUID nodeId,
                                  Network network,
                                  SelectionService selectionService,
                                  List<UUID> nodeIdList) throws UnreachableNodeException, InsufficientFundsException {

        if (nodeId.equals(cancellation.receiverNodeId())) {
            return;
        }
        else {
            UUID targetNodeId = cancellation.receiverNodeId();
            UUID nextNodeId = selectionService.selectNode(targetNodeId, nodeIdList);
            network.sendCancellation(nodeId, nextNodeId, cancellation);
        }

    }
}
