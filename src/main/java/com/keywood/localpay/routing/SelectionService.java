package com.keywood.localpay.routing;

import com.keywood.localpay.exceptions.UnreachableNodeException;

import java.util.List;
import java.util.UUID;

public interface SelectionService {

    UUID selectNode(UUID nodeId, List<UUID> nodeIdList) throws UnreachableNodeException;

}
