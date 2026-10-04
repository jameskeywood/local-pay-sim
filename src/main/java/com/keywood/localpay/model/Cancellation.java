package com.keywood.localpay.model;

import java.util.UUID;

public record Cancellation(
        UUID cancellationId,
        UUID transactionId,
        UUID senderNodeId,
        UUID receiverNodeId
)
{}
