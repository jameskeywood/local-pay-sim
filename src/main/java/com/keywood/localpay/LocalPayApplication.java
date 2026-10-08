package com.keywood.localpay;

import com.keywood.localpay.exceptions.InsufficientFundsException;
import com.keywood.localpay.user.Node;
import com.keywood.localpay.model.Transaction;
import com.keywood.localpay.simulation.Generator;
import com.keywood.localpay.simulation.Network;
import com.keywood.localpay.simulation.P2PNetwork;

import java.util.*;

import com.keywood.localpay.util.Randomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalPayApplication {

    private static final Logger logger = LoggerFactory.getLogger(LocalPayApplication.class);

    public static void main(String[] args) {

        logger.info("Starting LocalPay Simulation");

        Network network = new P2PNetwork();

        Generator generator = new Generator();

        Map<UUID, Node> totalNodeIdMapping = new HashMap<>();

        // generate random nodes

        for (int i = 0; i < 100; i++) {
            Node randomNode = generator.generateRandomNode(network);
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

        // for now, let's execute transactions sequentially
        // ideally we have some random ticker, generating transactions on the fly

        // tell the sender node of the random transaction to start processing

        int transactionAmount = 1000;

        for (int i = 0; i < transactionAmount; i++) {
            Transaction transaction = generator.generateRandomTransaction(totalNodeIdMapping.keySet());

            // output node account balances
            String balancesString = "\n";
            for (Node node : totalNodeIdMapping.values()) {
                balancesString += "Node " + node.getNodeId();
                balancesString += " $" + node.getAccount().getBalance();
                balancesString += "\n";
            }
            logger.info(balancesString);

            UUID senderNodeId = transaction.senderNodeId();

            try {
                network.sendMessage(senderNodeId, senderNodeId, transaction);
                logger.info("Node {} processed {}", senderNodeId, transaction);
            }
            catch (InsufficientFundsException e) {
                logger.info("Node {} has insufficient funds", transaction.receiverNodeId(), e);
            }

            //Thread.sleep(1000);
        }

    }
}
