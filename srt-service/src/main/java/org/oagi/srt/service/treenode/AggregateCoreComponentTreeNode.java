package org.oagi.srt.service.treenode;

import org.oagi.srt.repository.entity.AggregateCoreComponent;

public interface AggregateCoreComponentTreeNode extends CoreComponentTreeNode {

    public AggregateCoreComponent getAggregateCoreComponent();

    public AggregateCoreComponentTreeNode getBase();

}
