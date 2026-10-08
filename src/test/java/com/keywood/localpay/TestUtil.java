package com.keywood.localpay;

import com.keywood.localpay.payment.TransactionService;
import com.keywood.localpay.routing.RandomSelectionService;
import com.keywood.localpay.routing.RoutingService;
import com.keywood.localpay.simulation.Network;
import com.keywood.localpay.user.Account;
import com.keywood.localpay.user.Node;

import java.math.BigDecimal;
import java.util.UUID;

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

}
