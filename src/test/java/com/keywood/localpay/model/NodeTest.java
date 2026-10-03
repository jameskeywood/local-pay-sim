package com.keywood.localpay.model;

import com.keywood.localpay.services.RandomSelectionService;
import com.keywood.localpay.services.SelectionService;
import com.keywood.localpay.services.TransactionService;
import com.keywood.localpay.simulation.Network;
import com.keywood.localpay.simulation.P2PNetwork;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NodeTest {

    @Test
    void testProcessTransactionSameSenderAndReceiver() {

        Network network = new P2PNetwork();
        TransactionService transactionService = new TransactionService();
        SelectionService selectionService = new RandomSelectionService();

        Node node1 = createTestNode(network, transactionService, selectionService);
        Node node2 = createTestNode(network, transactionService, selectionService);

        node1.setNodeIdList(List.of(node2.getNodeId()));
        node2.setNodeIdList(List.of(node1.getNodeId()));

        UUID node1Id = node1.getNodeId();

        Transaction transaction = new Transaction(
                UUID.randomUUID(),
                node1Id,
                node1Id,
                BigDecimal.TEN);

        node1.processTransaction(transaction);

        assertEquals(BigDecimal.ZERO, node1.getAccount().getBalance());

    }

    Node createTestNode(Network network,
                        TransactionService transactionService,
                        SelectionService selectionService) {

        return new Node(
                UUID.randomUUID(),
                new Account(UUID.randomUUID()),
                network,
                transactionService,
                selectionService,
                new ArrayList<>()
        );
    }

}
