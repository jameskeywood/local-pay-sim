package com.keywood.localpay.model;

import com.keywood.localpay.exceptions.UnreachableNodeException;
import com.keywood.localpay.services.RandomSelectionService;
import com.keywood.localpay.services.SelectionService;
import com.keywood.localpay.services.TransactionService;
import com.keywood.localpay.simulation.Network;
import com.keywood.localpay.simulation.P2PNetwork;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class NodeTest {

    @Test
    void testProcessTransactionSameSenderAndReceiver() throws UnreachableNodeException {

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
                LocalDateTime.now(),
                null,
                BigDecimal.TEN);

        node1.processTransaction(transaction);

        assertEquals(BigDecimal.ZERO, node1.getAccount().getBalance());

    }

    // we really need to think about the case where
    // a transaction occurs, money leaves the senders account
    // but then the message never gets to the receiver, due
    // to a network partition
    //
    // in this case, how do we return to the sender?
    //
    // i think the system should be improved, such that
    // the payment between sender and receiver happens
    // only once the receiver has been found!

    @Test
    void testProcessTransactionWithNoPeers() {

        Network network = new P2PNetwork();
        TransactionService transactionService = new TransactionService();
        SelectionService selectionService = new RandomSelectionService();

        Node node = createTestNode(network, transactionService, selectionService);

        Transaction transaction = new Transaction(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                LocalDateTime.now(),
                null,
                BigDecimal.TEN);

        assertThrows(UnreachableNodeException.class, () -> {
            node.processTransaction(transaction);
        });
    }

    @Test
    void testProcessTransactionWithNetworkPartition() {
        Network network = new P2PNetwork();
        TransactionService transactionService = new TransactionService();
        SelectionService selectionService = new RandomSelectionService();

        Node node1 = createTestNode(network, transactionService, selectionService);
        Node node2 = createTestNode(network, transactionService, selectionService);
        Node node3 = createTestNode(network, transactionService, selectionService);
        Node node4 = createTestNode(network, transactionService, selectionService);

        node1.setNodeIdList(List.of(node2.getNodeId()));
        node2.setNodeIdList(List.of(node1.getNodeId()));
        node3.setNodeIdList(List.of(node4.getNodeId()));
        node4.setNodeIdList(List.of(node3.getNodeId()));

        UUID node1Id = node1.getNodeId();
        UUID node3Id = node3.getNodeId();

        Transaction transaction = new Transaction(
                UUID.randomUUID(),
                node1Id,
                node3Id,
                LocalDateTime.now(),
                null,
                BigDecimal.TEN);

        assertThrows(UnreachableNodeException.class, () -> {
            node1.processTransaction(transaction);
        });
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
