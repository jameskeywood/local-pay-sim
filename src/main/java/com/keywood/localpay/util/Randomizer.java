package com.keywood.localpay.util;

import com.keywood.localpay.model.Transaction;
import com.keywood.localpay.payment.TransactionService;
import com.keywood.localpay.routing.RandomSelectionService;
import com.keywood.localpay.routing.RoutingService;
import com.keywood.localpay.simulation.Network;
import com.keywood.localpay.user.Account;
import com.keywood.localpay.user.Node;
import com.keywood.localpay.user.User;

import java.math.BigDecimal;
import java.util.*;

public class Randomizer {
    public static final Random random = new Random();

    public static <T> T randomElementFromList(List<T> list) {
        int randomIndex = random.nextInt(list.size());
        T randomElement = list.get(randomIndex);

        return randomElement;
    }

    public static <T> List<T> randomNElementsFromList(List<T> list, int n) {
        List<T> copy = new ArrayList<T>(list);
        Collections.shuffle(copy);

        if (n > copy.size()) {
            return copy.subList(0, copy.size());
        } else {
            return copy.subList(0, n);
        }
    }

    public static <T> T randomElementFromSet(Set<T> set) {
        int randomIndex = random.nextInt(set.size());

        int i = 0;
        for (T item : set) {
            if (i == randomIndex) {
                return item;
            }
            i++;
        }

        return null;
    }

    public static <T> Set<T> randomNElementsFromSet(Set<T> set, int n) {
        Set<T> randomSet = new HashSet<>();

        while (randomSet.size() < n || randomSet.size() == set.size()) {
            randomSet.add(randomElementFromSet(set));
        }

        return randomSet;
    }

    // this needs work, should take a upper and lower bound!
    // placeholder / temp
    public static BigDecimal randomBigDecimal(BigDecimal lowerBound, BigDecimal upperBound) {
        int randomIntAmount = random.nextInt(100);
        BigDecimal randomBigDecimal = BigDecimal.valueOf(randomIntAmount);

        return randomBigDecimal;
    }

    public static Transaction randomTransaction(Set<UUID> totalNodeIdSet) {

        UUID randomSenderNodeId = randomElementFromSet(totalNodeIdSet);
        UUID randomReceiverNodeId = randomElementFromSet(totalNodeIdSet);

        BigDecimal randomAmount = randomBigDecimal(BigDecimal.valueOf(0), BigDecimal.valueOf(100));

        return new Transaction(
                UUID.randomUUID(),
                randomSenderNodeId,
                randomReceiverNodeId,
                randomAmount
        );
    }

    public static Transaction randomTransactionFromNodeId(Set<UUID> totalNodeIdSet, UUID nodeId) {

        UUID randomReceiverNodeId = randomElementFromSet(totalNodeIdSet);

        BigDecimal randomAmount = randomBigDecimal(BigDecimal.valueOf(0), BigDecimal.valueOf(100));

        return new Transaction(
                UUID.randomUUID(),
                nodeId,
                randomReceiverNodeId,
                randomAmount
        );
    }

    public static Node randomNode(Network network) {

        return new Node(
                UUID.randomUUID(),
                new Account(UUID.randomUUID(), BigDecimal.valueOf(1000)),
                network,
                new TransactionService(),
                new RoutingService(),
                new RandomSelectionService()
        );
    }

    public static User randomUser(Node node, Set<UUID> contacts) {

        return new User(
                UUID.randomUUID(),
                node,
                contacts
        );
    }
}