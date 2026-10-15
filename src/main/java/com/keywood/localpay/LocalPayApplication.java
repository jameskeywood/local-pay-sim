package com.keywood.localpay;

import com.keywood.localpay.user.Node;
import com.keywood.localpay.simulation.Network;
import com.keywood.localpay.simulation.P2PNetwork;

import java.util.*;
import java.util.concurrent.Executors;

import com.keywood.localpay.user.User;
import com.keywood.localpay.util.Randomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalPayApplication {

    private static final Logger logger = LoggerFactory.getLogger(LocalPayApplication.class);

    private static final int NUM_USERS = 1000;

    public static void main(String[] args) {

        logger.info("Starting LocalPay Simulation");

        Network network = new P2PNetwork();

        Map<UUID, Node> totalNodeIdMapping = new HashMap<>();

        // generate random nodes

        for (int i = 0; i < NUM_USERS; i++) {
            Node randomNode = Randomizer.randomNode(network);
            totalNodeIdMapping.put(randomNode.getNodeId(), randomNode);
        }

        logger.info("Generated {} random Nodes", totalNodeIdMapping.size());

        // populate each node's nodeIdList such that they have
        // a partial view of all the nodes on the network

        double networkView = 0.1;
        int numberOfNodes = (int) Math.ceil(networkView * totalNodeIdMapping.size());
        for (Node node : totalNodeIdMapping.values()) {
            Set<UUID> randomNodeIdSet = Randomizer.randomNElementsFromSet(totalNodeIdMapping.keySet(), numberOfNodes);
            node.bulkAddToNodeIdSet(randomNodeIdSet);
        }

        // now, let's create the users

        Map<UUID, User> totalUserIdMapping = new HashMap<>();

        for (Node node : totalNodeIdMapping.values()) {
            User randomUser = Randomizer.randomUser(node, totalNodeIdMapping.keySet());
            totalUserIdMapping.put(randomUser.getUserId(), randomUser);
        }

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {

            for (Node node : totalNodeIdMapping.values()) {
                executor.submit(node);
            }

            for (User user : totalUserIdMapping.values()) {
                executor.submit(user);
            }
        }
    }
}
