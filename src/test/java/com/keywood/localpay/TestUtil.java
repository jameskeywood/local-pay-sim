package com.keywood.localpay;

import com.keywood.localpay.payment.TransactionService;
import com.keywood.localpay.routing.RandomSelectionService;
import com.keywood.localpay.routing.RoutingService;
import com.keywood.localpay.simulation.Network;
import com.keywood.localpay.user.Account;
import com.keywood.localpay.user.Node;
import com.keywood.localpay.user.User;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestUtil {

    public static Node createTestNode(Network network,
                        BigDecimal accountBalance) {

        return new Node(
                UUID.randomUUID(),
                new Account(UUID.randomUUID(), accountBalance),
                network,
                new TransactionService(),
                new RoutingService(),
                new RandomSelectionService()
        );
    }

    public static User createTestUser(Node node, Set<UUID> contacts) {

        return new User(
                UUID.randomUUID(),
                node,
                contacts
        );
    }


    public static ExecutorService startNodes(Node... nodes) {
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

        for (Node node : nodes) {
            executor.submit(node);
        }

        return executor;
    }

}
