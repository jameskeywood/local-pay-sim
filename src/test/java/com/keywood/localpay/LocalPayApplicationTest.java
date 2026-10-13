package com.keywood.localpay;

import com.keywood.localpay.model.Transaction;
import com.keywood.localpay.simulation.Network;
import com.keywood.localpay.simulation.P2PNetwork;
import com.keywood.localpay.user.Node;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

// we can create integration tests by running nodes on virtual threads
// and simulating our own transactions, instead of running User objects

public class LocalPayApplicationTest {

    private final BigDecimal ACCOUNT_BALANCE = BigDecimal.valueOf(1000);


    @Test
    void testSendTransactionSameSenderAndReceiver() {

        Network network = new P2PNetwork();

        Node node = TestUtil.createTestNode(network, ACCOUNT_BALANCE);

        UUID nodeId = node.getNodeId();

        Transaction transaction = new Transaction(
                UUID.randomUUID(),
                nodeId,
                nodeId,
                BigDecimal.TEN);

        ExecutorService executor = TestUtil.startNodes(node);

        try {
            node.deliver(transaction);
        }
        finally {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(2, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
            }
        }

        assertEquals(ACCOUNT_BALANCE, node.getAccount().getBalance());
    }

    /*

    we must think about the situation in which:

    Node A sends Node B a transaction
    Node B only has itself as a peer
    Node B sends transaction to itself 3 times
    Transaction becomes a Cancellation
    Node B infinitely sends the Cancellation to itself

    @Test
    void testSendTransactionNodeWithNoPeers() {

        Network network = new P2PNetwork();

        Node node = createTestNode(network);

        Transaction transaction = new Transaction(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                BigDecimal.TEN);

        node.receiveMessage(transaction);

        assertEquals(ACCOUNT_BALANCE, node1)
    }
     */


    @Test
    void testSendTransactionPartitionedNetwork() {
        Network network = new P2PNetwork();

        Node node1 = TestUtil.createTestNode(network, ACCOUNT_BALANCE);
        Node node2 = TestUtil.createTestNode(network, ACCOUNT_BALANCE);
        Node node3 = TestUtil.createTestNode(network, ACCOUNT_BALANCE);
        Node node4 = TestUtil.createTestNode(network, ACCOUNT_BALANCE);

        node1.addToNodeIdSet(node2.getNodeId());
        node2.addToNodeIdSet(node1.getNodeId());
        node3.addToNodeIdSet(node4.getNodeId());
        node4.addToNodeIdSet(node3.getNodeId());

        UUID node1Id = node1.getNodeId();
        UUID node3Id = node3.getNodeId();

        Transaction transaction = new Transaction(
                UUID.randomUUID(),
                node1Id,
                node3Id,
                BigDecimal.TEN);

        ExecutorService executor = TestUtil.startNodes(node1, node2, node3, node4);

        try {
            node1.deliver(transaction);
        }
        finally {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(2, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
            }
        }

        assertEquals(ACCOUNT_BALANCE, node1.getAccount().getBalance());
        assertEquals(ACCOUNT_BALANCE, node3.getAccount().getBalance());
    }


    @Test
    void testSendTransactionSequentiallyNodeHasInsufficientFunds() {
        Network network = new P2PNetwork();

        Node node1 = TestUtil.createTestNode(network, BigDecimal.TEN);
        Node node2 = TestUtil.createTestNode(network, BigDecimal.TEN);
        Node node3 = TestUtil.createTestNode(network, BigDecimal.TEN);

        node1.bulkAddToNodeIdSet(Set.of(node2.getNodeId(), node3.getNodeId()));
        node2.bulkAddToNodeIdSet(Set.of(node1.getNodeId(), node3.getNodeId()));
        node3.bulkAddToNodeIdSet(Set.of(node1.getNodeId(), node2.getNodeId()));

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

        ExecutorService executor = TestUtil.startNodes(node1, node2, node3);

        try {
            node1.deliver(transaction1);
            node1.deliver(transaction2);
        }
        finally {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(2, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
            }
        }

        assertEquals(BigDecimal.ZERO, node1.getAccount().getBalance());
        assertEquals(BigDecimal.valueOf(30), node2.getAccount().getBalance().add(node3.getAccount().getBalance()));
    }
}
