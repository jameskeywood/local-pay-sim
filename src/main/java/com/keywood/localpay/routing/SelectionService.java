package com.keywood.localpay.routing;


import java.util.Set;
import java.util.UUID;

public interface SelectionService {

    UUID selectNode(UUID nodeId, Set<UUID> nodeIdSet);

}
