package com.keywood.localpay.routing;

import com.keywood.localpay.exceptions.UnreachableNodeException;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public class RandomSelectionService implements SelectionService {

    public UUID selectNode(UUID targetNodeId, List<UUID> nodeIdList) throws UnreachableNodeException {

        if (nodeIdList == null || nodeIdList.isEmpty()) {
            throw new UnreachableNodeException("nodeIdList is null or empty");
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
