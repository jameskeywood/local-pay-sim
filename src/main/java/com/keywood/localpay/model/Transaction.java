package com.keywood.localpay.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record Transaction (
        UUID transactionId,
        UUID senderNodeId,
        UUID receiverNodeId,
        LocalDateTime sendTimestamp,
        LocalDateTime receiveTimestamp,
        BigDecimal amount
) implements Serializable
{}
