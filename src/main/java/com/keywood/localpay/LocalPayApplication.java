package com.keywood.localpay;

import com.keywood.localpay.model.Node;
import com.keywood.localpay.model.Transaction;
import com.keywood.localpay.services.RandomSelectionService;
import com.keywood.localpay.services.SelectionService;
import com.keywood.localpay.services.TransactionService;
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
        TransactionService transactionService = new TransactionService();
        SelectionService selectionService = new RandomSelectionService();

        Generator generator = new Generator();

        Map<UUID, Node> totalNodeIdMapping = new HashMap<>();

        // generate random nodes

        for (int i = 0; i < 100; i++) {
            Node randomNode = generator.generateRandomNode(network, transactionService, selectionService);
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

        // generate 100 random transactions to be executed

        List<Transaction> transactionList = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            transactionList.add(generator.generateRandomTransaction(totalNodeIdList));
        }

        logger.info("Generated {} random Transactions", transactionList.size());

        // for now, let's execute transactions sequentially
        // ideally we have some random ticker, generating transactions on the fly

        // tell the sender node of the random transaction to start processing

        for (Transaction transaction : transactionList) {

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

            senderNode.processTransaction(transaction);

            logger.info("Node {} processed {}", senderNode.getNodeId(), transaction);

            //Thread.sleep(1000);
        }

    }
}
