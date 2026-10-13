package com.keywood.localpay.model;

import java.math.BigDecimal;
import java.util.UUID;

public record Transaction (
        UUID transactionId,
        UUID senderNodeId,
        UUID receiverNodeId,
        BigDecimal amount
) implements Message
{}
