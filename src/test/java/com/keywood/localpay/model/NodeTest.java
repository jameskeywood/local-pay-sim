package com.keywood.localpay.model;

import com.keywood.localpay.exceptions.InsufficientFundsException;
import com.keywood.localpay.exceptions.UnreachableNodeException;
import com.keywood.localpay.routing.RandomSelectionService;
import com.keywood.localpay.routing.RoutingService;
import com.keywood.localpay.payment.TransactionService;
import com.keywood.localpay.simulation.Network;
import com.keywood.localpay.simulation.P2PNetwork;
import com.keywood.localpay.user.Account;
import com.keywood.localpay.user.Node;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class NodeTest {

    private final BigDecimal ACCOUNT_BALANCE = BigDecimal.valueOf(1000);


    @Test
    void testReceiveTransactionSameSenderAndReceiver() throws UnreachableNodeException, InsufficientFundsException {

        Network network = new P2PNetwork();

        Node node1 = createTestNode(network);
        Node node2 = createTestNode(network);

        node1.setNodeIdList(List.of(node2.getNodeId()));
        node2.setNodeIdList(List.of(node1.getNodeId()));

        UUID node1Id = node1.getNodeId();

        Transaction transaction = new Transaction(
                UUID.randomUUID(),
                node1Id,
                node1Id,
                BigDecimal.TEN);

        node1.receiveMessage(transaction);

        assertEquals(ACCOUNT_BALANCE, node1.getAccount().getBalance());

    }


    @Test
    void testReceiveTransactionWithNoPeers() {

        Network network = new P2PNetwork();

        Node node = createTestNode(network);

        Transaction transaction = new Transaction(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                BigDecimal.TEN);

        assertThrows(UnreachableNodeException.class, () -> {
            node.receiveMessage(transaction);
        });
    }


    @Test
    void testReceiveTransactionWithNetworkPartition() throws UnreachableNodeException, InsufficientFundsException {
        Network network = new P2PNetwork();

        Node node1 = createTestNode(network);
        Node node2 = createTestNode(network);
        Node node3 = createTestNode(network);
        Node node4 = createTestNode(network);

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
                BigDecimal.TEN);

        node1.receiveMessage(transaction);

        assertEquals(ACCOUNT_BALANCE, node1.getAccount().getBalance());
        assertEquals(ACCOUNT_BALANCE, node3.getAccount().getBalance());
    }


    @Test
    void testReceiveTransactionInsufficientFundsSequentially() {
        Network network = new P2PNetwork();

        Node node1 = createTestNode(network, BigDecimal.TEN);
        Node node2 = createTestNode(network, BigDecimal.TEN);
        Node node3 = createTestNode(network, BigDecimal.TEN);

        node1.setNodeIdList(List.of(node2.getNodeId(), node3.getNodeId()));
        node2.setNodeIdList(List.of(node1.getNodeId(), node3.getNodeId()));
        node3.setNodeIdList(List.of(node1.getNodeId(), node2.getNodeId()));

        UUID node1Id = node1.getNodeId();
        UUID node2Id = node2.getNodeId();
        UUID node3Id = node3.getNodeId();

        Transaction transaction1 = new Transaction(
                UUID.randomUUID(),
                node1Id,
                node2Id,
                BigDecimal.TEN);

        Transaction transaction2 = new Transaction(
                UUID.randomUUID(),
                node1Id,
                node3Id,
                BigDecimal.TEN);
        
        assertThrows(InsufficientFundsException.class, () -> {
            node1.receiveMessage(transaction1);
            node1.receiveMessage(transaction2);
        });

        assertEquals(BigDecimal.ZERO, node1.getAccount().getBalance());
        assertEquals(BigDecimal.valueOf(30), node2.getAccount().getBalance().add(node3.getAccount().getBalance()));
    }


    Node createTestNode(Network network) {

        return new Node(
                UUID.randomUUID(),
                new Account(UUID.randomUUID(), ACCOUNT_BALANCE),
                network,
                new TransactionService(),
                new RoutingService(),
                new RandomSelectionService(),
                new ArrayList<>()
        );
    }


    Node createTestNode(Network network,
                        BigDecimal accountBalance) {

        return new Node(
                UUID.randomUUID(),
                new Account(UUID.randomUUID(), accountBalance),
                network,
                new TransactionService(),
                new RoutingService(),
                new RandomSelectionService(),
                new ArrayList<>()
        );
    }

}
