package com.keywood.localpay.model;

import java.math.BigDecimal;
import java.util.UUID;

public record Transaction(
        UUID id,
        UUID sender,
        UUID receiver,
        BigDecimal amount
)
{}
