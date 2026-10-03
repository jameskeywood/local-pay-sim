package com.keywood.localpay.services;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public class RandomSelectionService implements SelectionService {
    public UUID selectNode(UUID targetNodeId, List<UUID> nodeIdList) {

        if (nodeIdList == null || nodeIdList.isEmpty()) {
            throw new IllegalArgumentException("nodeIdList must not be empty");
        }

        if (nodeIdList.contains(targetNodeId)) {
            return targetNodeId;
        }

        Random random = new Random();
        int randomIndex = random.nextInt(nodeIdList.size());
        UUID selectedNodeId = nodeIdList.get(randomIndex);

        return selectedNodeId;
    }
}
