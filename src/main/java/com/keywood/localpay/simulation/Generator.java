package com.keywood.localpay.simulation;

import com.keywood.localpay.routing.RandomSelectionService;
import com.keywood.localpay.user.Account;
import com.keywood.localpay.user.Node;
import com.keywood.localpay.model.Transaction;
import com.keywood.localpay.payment.TransactionService;
import com.keywood.localpay.routing.RoutingService;
import com.keywood.localpay.util.Randomizer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Generator {

    public Node generateRandomNode(Network network) {

        return new Node(
                UUID.randomUUID(),
                new Account(UUID.randomUUID(), BigDecimal.valueOf(1000)),
                network,
                new TransactionService(),
                new RoutingService(),
                new RandomSelectionService(),
                new ArrayList<>()
        );
    }

    public Transaction generateRandomTransaction(List<UUID> totalNodeIdList) {

        UUID randomSenderNodeId = Randomizer.randomElementFromList(totalNodeIdList);
        UUID randomReceiverNodeId = Randomizer.randomElementFromList(totalNodeIdList);

        BigDecimal randomAmount = Randomizer.randomBigDecimal(BigDecimal.valueOf(0), BigDecimal.valueOf(100));

        return new Transaction(
                UUID.randomUUID(),
                randomSenderNodeId,
                randomReceiverNodeId,
                randomAmount
        );
    }
}
