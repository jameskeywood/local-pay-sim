package com.keywood.localpay.user;

import com.keywood.localpay.model.Transaction;
import com.keywood.localpay.util.Randomizer;

import java.util.Set;
import java.util.UUID;

public class User implements Runnable {

    private final UUID userId;

    private final Node node;

    private final Set<UUID> contacts;

    public User(UUID userId, Node node, Set<UUID> contacts) {
        this.userId = userId;
        this.node = node;
        this.contacts = contacts;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Thread.sleep(Randomizer.random.nextLong(0, 100001));

                Transaction transaction = Randomizer.randomTransactionFromNodeId(this.contacts, this.node.getNodeId());

                node.deliver(transaction);
            }
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public UUID getUserId() {
        return this.userId;
    }
}
