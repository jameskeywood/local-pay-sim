package com.keywood.localpay.user;

import com.keywood.localpay.TestUtil;
import com.keywood.localpay.simulation.Network;
import com.keywood.localpay.simulation.P2PNetwork;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NodeTest {

    private final BigDecimal ACCOUNT_BALANCE = BigDecimal.valueOf(1000);

    @ParameterizedTest
    @MethodSource("nodeIds")
    void testAddRemoveFromNodeIdSet(UUID nodeId) {
        Network network = new P2PNetwork();
        Node node = TestUtil.createTestNode(network, ACCOUNT_BALANCE);

        node.addToNodeIdSet(nodeId);
        node.removeFromNodeIdSet(nodeId);

        assertEquals(Set.of(node.getNodeId()), node.getNodeIdSet());
    }

    @ParameterizedTest
    @MethodSource("nodeIdSets")
    void testBulkAddRemoveFromNodeIdSet(Set<UUID> nodeIds) {
        Network network = new P2PNetwork();
        Node node = TestUtil.createTestNode(network, ACCOUNT_BALANCE);

        node.bulkAddToNodeIdSet(nodeIds);
        node.bulkRemoveFromNodeIdSet(nodeIds);

        assertEquals(Set.of(node.getNodeId()), node.getNodeIdSet());
    }

    static Stream<UUID> nodeIds() {
        return Stream.of(UUID.randomUUID(), UUID.randomUUID());
    }

    static Stream<Set<UUID>> nodeIdSets() {
        return Stream.of(
                Set.of(UUID.randomUUID()),
                Set.of(UUID.randomUUID(), UUID.randomUUID())
        );
    }
}