package com.keywood.localpay.model;

import java.util.UUID;

public record Confirmation(
        UUID confirmationId,
        UUID transactionId,
        UUID senderNodeId,
        UUID receiverNodeId
) implements Message
{}
