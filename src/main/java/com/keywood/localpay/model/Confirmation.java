package com.keywood.localpay.model;

import java.io.Serializable;
import java.util.UUID;

public record Confirmation(
        UUID confirmationId,
        UUID transactionId,
        UUID senderNodeId,
        UUID receiverNodeId
) implements Serializable
{}
