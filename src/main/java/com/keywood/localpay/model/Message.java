package com.keywood.localpay.model;

import java.util.UUID;

public sealed interface Message permits Transaction, Confirmation, Cancellation {
    UUID transactionId();
    UUID senderNodeId();
    UUID receiverNodeId();
}
