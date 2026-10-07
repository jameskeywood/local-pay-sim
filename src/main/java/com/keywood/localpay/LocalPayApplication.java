package com.keywood.localpay;

import com.keywood.localpay.exceptions.InsufficientFundsException;
import com.keywood.localpay.exceptions.UnreachableNodeException;
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

        List<UUID> totalNodeIdList = new ArrayList<>(totalNodeIdMapping.keySet());

        // populate each node's nodeIdList such that they have
        // a partial view of all the nodes on the network

        double networkView = 0.1;
        int numberOfNodes = (int) Math.ceil(networkView * totalNodeIdMapping.size());
        for (Node node : totalNodeIdMapping.values()) {
            List<UUID> randomNodeIdList = Randomizer.randomNElementsFromList(totalNodeIdList, numberOfNodes);
            node.setNodeIdList(randomNodeIdList);
        }

        // for now, let's execute transactions sequentially
        // ideally we have some random ticker, generating transactions on the fly

        // tell the sender node of the random transaction to start processing

        int transactionAmount = 1000;

        for (int i = 0; i < transactionAmount; i++) {
            Transaction transaction = generator.generateRandomTransaction(totalNodeIdList);

            // output node account balances
            String balancesString = "\n";
            for (Node node : totalNodeIdMapping.values()) {
                balancesString += "Node " + node.getNodeId();
                balancesString += " $" + node.getAccount().getBalance();
                balancesString += "\n";
            }
            logger.info(balancesString);

            UUID senderNodeId = transaction.senderNodeId();

            Node senderNode = totalNodeIdMapping.get(senderNodeId);

            try {
                senderNode.receiveMessage(transaction);
                logger.info("Node {} processed {}", senderNode.getNodeId(), transaction);
            }
            catch (UnreachableNodeException e) {
                logger.info("Node {} is unreachable", transaction.receiverNodeId(), e);
            }
            catch (InsufficientFundsException e) {
                logger.info("Node {} has insufficient funds", transaction.receiverNodeId(), e);
            }

            //Thread.sleep(1000);
        }

    }
}
