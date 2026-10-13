package com.keywood.localpay.routing;

import com.keywood.localpay.util.Randomizer;

import java.util.Set;
import java.util.UUID;

public class RandomSelectionService implements SelectionService {

    public UUID selectNode(UUID targetNodeId, Set<UUID> nodeIdSet) {

        // now, I know that nodeIdSet won't ever be empty
        // but is it worth throwing an exception anyway? unchecked

        if (nodeIdSet.contains(targetNodeId)) {
            return targetNodeId;
        }

        UUID selectedNodeId = Randomizer.randomElementFromSet(nodeIdSet);

        return selectedNodeId;
    }
}
