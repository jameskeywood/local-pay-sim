package com.keywood.localpay.services;

import com.keywood.localpay.model.Node;

import java.util.List;
import java.util.UUID;

public interface SelectionService {
    public UUID selectNode(UUID nodeId, List<UUID> nodeIdList);
}
