package org.oagi.srt.web.jsf.beans.bod;

import org.oagi.srt.model.bie.*;
import org.oagi.srt.repository.BusinessInformationEntityUserExtensionRevisionRepository;
import org.oagi.srt.repository.TopLevelAbieRepository;
import org.oagi.srt.repository.entity.*;
import org.oagi.srt.service.BusinessInformationEntityService;
import org.oagi.srt.service.ExtensionService;
import org.oagi.srt.web.handler.UIHandler;
import org.oagi.srt.web.jsf.component.treenode.BIETreeNodeHandler;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@Scope("view")
@ManagedBean
@ViewScoped
@Transactional(readOnly = true)
public class EditProfileBODBean extends UIHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private BIETreeNodeHandler bieTreeNodeHandler;
    @Autowired
    private BusinessInformationEntityService bieService;
    @Autowired
    private ExtensionService extensionService;
    @Autowired
    private TopLevelAbieRepository topLevelAbieRepository;
    @Autowired
    private BusinessInformationEntityUserExtensionRevisionRepository bieUserExtRevisionRepository;

    private TopLevelAbie topLevelAbie;
    private List<BusinessInformationEntityUserExtensionRevision> bieUserExtRevisionList;
    private TreeNode treeNode;
    private TreeNode selectedTreeNode;

    private String selectedCodeListName;

    @PostConstruct
    public void init() {
        Long topLevelAbieId = Long.parseLong(
                FacesContext.getCurrentInstance().getExternalContext()
                        .getRequestParameterMap().get("topLevelAbieId"));
        TopLevelAbie topLevelAbie = topLevelAbieRepository.findOne(topLevelAbieId);
        if (topLevelAbie == null) {
            return;
        }
        setTopLevelAbie(topLevelAbie);

        TreeNode treeNode = bieTreeNodeHandler.createLazyTreeNode(topLevelAbie);
        setTreeNode(treeNode);

        List<BusinessInformationEntityUserExtensionRevision> bieUserExtRevisionList =
                bieUserExtRevisionRepository.findByTopLevelAbieId(topLevelAbieId);
        setBieUserExtRevisionList(bieUserExtRevisionList);

        if (!bieUserExtRevisionList.isEmpty()) {
            RequestContext.getCurrentInstance().execute("PF('confirmExtensionUptake').show()");
        }
    }

    public TopLevelAbie getTopLevelAbie() {
        return topLevelAbie;
    }

    public void setTopLevelAbie(TopLevelAbie topLevelAbie) {
        this.topLevelAbie = topLevelAbie;
    }

    public List<BusinessInformationEntityUserExtensionRevision> getBieUserExtRevisionList() {
        return bieUserExtRevisionList;
    }

    public void setBieUserExtRevisionList(List<BusinessInformationEntityUserExtensionRevision> bieUserExtRevisionList) {
        this.bieUserExtRevisionList = bieUserExtRevisionList;
    }

    public String getBieUserExtRevisionListString() {
        List<BusinessInformationEntityUserExtensionRevision> bieUserExtRevisionList = getBieUserExtRevisionList();
        if (bieUserExtRevisionList == null || bieUserExtRevisionList.isEmpty()) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (BusinessInformationEntityUserExtensionRevision bieUserExtRevision : bieUserExtRevisionList) {
            sb.append(bieUserExtRevision.getExtAcc().getDen());
            sb.append(", ");
        }
        String str = sb.toString();
        return str.substring(0, str.length() - 2);
    }

    public TreeNode getTreeNode() {
        return treeNode;
    }

    public void setTreeNode(TreeNode treeNode) {
        this.treeNode = treeNode;
    }

    public TopLevelNode getTopLevelNode() {
        TreeNode treeNode = getTreeNode();
        return (TopLevelNode) treeNode.getChildren().get(0).getData();
    }

    public TreeNode getSelectedTreeNode() {
        return selectedTreeNode;
    }

    public void setSelectedTreeNode(TreeNode selectedTreeNode) {
        this.selectedTreeNode = selectedTreeNode;
    }

    /*
     * handle BBIE Type
     */
    public Map<BBIERestrictionType, BBIERestrictionType> getAvailablePrimitiveRestrictions(BBIENode node) {
        return bieService.getAvailablePrimitiveRestrictions(node);
    }

    private BBIENode getSelectedBBIENode() {
        TreeNode treeNode = getSelectedTreeNode();
        Object data = treeNode.getData();
        if (!(data instanceof BBIENode)) {
            return null;
        }
        return (BBIENode) data;
    }

    public String getBbieXbtName() {
        return bieService.getBdtPrimitiveRestrictionName(getSelectedBBIENode());
    }

    public void setBbieXbtName(String name) {
        bieService.setBdtPrimitiveRestriction(getSelectedBBIENode(), name);
    }

    public void onSelectBbieXbtName(SelectEvent event) {
        setBbieXbtName(event.getObject().toString());
    }

    public List<String> completeInputForBbieXbt(String query) {
        BBIENode node = getSelectedBBIENode();
        Map<String, BusinessDataTypePrimitiveRestriction> bdtPrimitiveRestrictions =
                bieService.getBdtPrimitiveRestrictions(node);
        if (StringUtils.isEmpty(query)) {
            return new ArrayList(bdtPrimitiveRestrictions.keySet());
        } else {
            return bdtPrimitiveRestrictions.keySet().stream()
                    .filter(e -> e.toLowerCase().contains(query.toLowerCase()))
                    .collect(Collectors.toList());
        }
    }

    public String getBbieCodeListName() {
        return bieService.getCodeListName(getSelectedBBIENode());
    }

    public void setBbieCodeListName(String name) {
        BBIENode node = getSelectedBBIENode();
        Map<String, CodeList> codeListMap = bieService.getCodeLists(node);
        CodeList codeList = codeListMap.get(name);
        if (codeList != null) {
            node.setCodeListId(codeList.getCodeListId());
        }
    }

    public void onSelectBbieCodeListName(SelectEvent event) {
        setBbieCodeListName(event.getObject().toString());
    }

    public List<String> completeInputForBbieCodeList(String query) {
        BBIENode node = getSelectedBBIENode();
        Map<String, CodeList> codeLists = bieService.getCodeLists(node);
        if (StringUtils.isEmpty(query)) {
            return new ArrayList(codeLists.keySet());
        } else {
            return codeLists.keySet().stream()
                    .filter(e -> e.toLowerCase().contains(query.toLowerCase()))
                    .collect(Collectors.toList());
        }
    }

    public String getBbieAgencyIdListName() {
        return bieService.getBbieAgencyIdListName(getSelectedBBIENode());
    }

    public void setBbieAgencyIdListName(String name) {
        BBIENode node = getSelectedBBIENode();
        Map<String, AgencyIdList> agencyIdListMap = bieService.getAgencyIdListIds(node);
        AgencyIdList agencyIdList = agencyIdListMap.get(name);
        if (agencyIdList != null) {
            node.setAgencyIdListId(agencyIdList.getAgencyIdListId());
        }
    }

    public void onSelectBbieAgencyIdListName(SelectEvent event) {
        setBbieAgencyIdListName(event.getObject().toString());
    }

    public List<String> completeInputForBbieAgencyIdList(String query) {
        BBIENode node = getSelectedBBIENode();
        Map<String, AgencyIdList> agencyIdListMap = bieService.getAgencyIdListIds(node);
        if (StringUtils.isEmpty(query)) {
            return new ArrayList(agencyIdListMap.keySet());
        } else {
            return agencyIdListMap.keySet().stream()
                    .filter(e -> e.toLowerCase().contains(query.toLowerCase()))
                    .collect(Collectors.toList());
        }
    }

    /*
     * handle BBIESC Type
     */
    public Map<BBIERestrictionType, BBIERestrictionType> getAvailableScPrimitiveRestrictions(BBIESCNode node) {
        return bieService.getAvailablePrimitiveRestrictions(node);
    }

    private BBIESCNode getSelectedBBIESCNode() {
        TreeNode treeNode = getSelectedTreeNode();
        Object data = treeNode.getData();
        if (!(data instanceof BBIESCNode)) {
            return null;
        }
        return (BBIESCNode) data;
    }

    public String getBbieScXbtName() {
        return bieService.getBdtScPrimitiveRestrictionName(getSelectedBBIESCNode());
    }

    public void setBbieScXbtName(String name) {
        bieService.setBdtScPrimitiveRestriction(getSelectedBBIESCNode(), name);
    }

    public void onSelectBbieScXbtName(SelectEvent event) {
        setBbieScXbtName(event.getObject().toString());
    }

    public List<String> completeInputForBbieScXbt(String query) {
        BBIESCNode node = getSelectedBBIESCNode();
        Map<String, BusinessDataTypeSupplementaryComponentPrimitiveRestriction> bdtScPrimitiveRestrictions =
                bieService.getBdtScPrimitiveRestrictions(node);
        if (StringUtils.isEmpty(query)) {
            return new ArrayList(bdtScPrimitiveRestrictions.keySet());
        } else {
            return bdtScPrimitiveRestrictions.keySet().stream()
                    .filter(e -> e.toLowerCase().contains(query.toLowerCase()))
                    .collect(Collectors.toList());
        }
    }

    public String getBbieScCodeListName() {
        return bieService.getCodeListName(getSelectedBBIESCNode());
    }

    public void setBbieScCodeListName(String name) {
        BBIESCNode node = getSelectedBBIESCNode();
        Map<String, CodeList> codeListMap = bieService.getCodeLists(node);
        CodeList codeList = codeListMap.get(name);
        if (codeList != null) {
            node.setCodeListId(codeList.getCodeListId());
        }
    }

    public void onSelectBbieScCodeListName(SelectEvent event) {
        setBbieScCodeListName(event.getObject().toString());
    }

    public List<String> completeInputForBbieScCodeList(String query) {
        BBIESCNode node = getSelectedBBIESCNode();
        Map<String, CodeList> codeLists = bieService.getCodeLists(node);
        if (StringUtils.isEmpty(query)) {
            return new ArrayList(codeLists.keySet());
        } else {
            return codeLists.keySet().stream()
                    .filter(e -> e.toLowerCase().contains(query.toLowerCase()))
                    .collect(Collectors.toList());
        }
    }

    public String getBbieScAgencyIdListName() {
        return bieService.getBbieAgencyIdListName(getSelectedBBIESCNode());
    }

    public void setBbieScAgencyIdListName(String name) {
        BBIESCNode node = getSelectedBBIESCNode();
        Map<String, AgencyIdList> agencyIdListMap = bieService.getAgencyIdListIds(node);
        AgencyIdList agencyIdList = agencyIdListMap.get(name);
        if (agencyIdList != null) {
            node.setAgencyIdListId(agencyIdList.getAgencyIdListId());
        }
    }

    public void onSelectBbieScAgencyIdListName(SelectEvent event) {
        setBbieScAgencyIdListName(event.getObject().toString());
    }

    public List<String> completeInputForBbieScAgencyIdList(String query) {
        BBIESCNode node = getSelectedBBIESCNode();
        Map<String, AgencyIdList> agencyIdListMap = bieService.getAgencyIdListIds(node);
        if (StringUtils.isEmpty(query)) {
            return new ArrayList(agencyIdListMap.keySet());
        } else {
            return agencyIdListMap.keySet().stream()
                    .filter(e -> e.toLowerCase().contains(query.toLowerCase()))
                    .collect(Collectors.toList());
        }
    }

    /*
     * handle command buttons.
     */
    public void expand(NodeExpandEvent expandEvent) {
        DefaultTreeNode treeNode = (DefaultTreeNode) expandEvent.getTreeNode();
        bieTreeNodeHandler.expandLazyTreeNode(treeNode);
    }

    @Transactional(readOnly = false, rollbackFor = Throwable.class)
    public void update() {
        try {
            bieTreeNodeHandler.update(getTopLevelNode());
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Updated successfully."));
        } catch (Throwable t) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", t.getMessage()));
            throw t;
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public String updateState(AggregateBusinessInformationEntityState state) {
        try {
            TopLevelNode topLevelNode = getTopLevelNode();
            long topLevelAbieId = topLevelNode.getAbie().getOwnerTopLevelAbieId();
            bieService.updateState(topLevelAbieId, state);

            return "/views/profile_bod/list.xhtml?faces-redirect=true";
        } catch (Throwable t) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", t.getMessage()));
            throw t;
        }
    }

    public String createABIEExtension(boolean isLocally) {
        TreeNode treeNode = getSelectedTreeNode();
        ASBIENode asbieNode = (ASBIENode) treeNode.getData();
        AssociationCoreComponentProperty asccp = asbieNode.getAsccp();
        User user = getCurrentUser();

        AggregateCoreComponent eAcc = extensionService.getExtensionAcc(asccp, isLocally);
        AggregateCoreComponent ueAcc = extensionService.getExistsUserExtension(eAcc);
        if (ueAcc != null) {
            CoreComponentState ueAccState = ueAcc.getState();

            if ( user.getAppUserId() == ueAcc.getOwnerUserId() ) {
                return redirectABIEExtension(isLocally, eAcc);
            }

            if ( (ueAccState == CoreComponentState.Candidate || ueAccState == CoreComponentState.Published) ) {
                return redirectABIEExtension(isLocally, eAcc);
            }

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "This user extension is already taken by other user."));
            return null;
        }

        try {
            eAcc = extensionService.appendUserExtension(asccp, user, isLocally);
        } catch (PermissionDeniedDataAccessException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "This user extension is already taken by other user."));
            throw e;
        } catch (Throwable t) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", t.getMessage()));
            throw t;
        }

        return redirectABIEExtension(isLocally, eAcc);
    }

    public CoreComponentState getABIEExtensionState(boolean isLocally) {
        TreeNode treeNode = getSelectedTreeNode();
        if (treeNode == null) {
            return null;
        }
        ASBIENode asbieNode = (ASBIENode) treeNode.getData();
        AssociationCoreComponentProperty asccp = asbieNode.getAsccp();

        AggregateCoreComponent eAcc = extensionService.getExtensionAcc(asccp, isLocally);
        AggregateCoreComponent ueAcc = extensionService.getExistsUserExtension(eAcc);
        return (ueAcc != null) ? ueAcc.getState() : null;
    }

    public String redirectABIEExtension(boolean isLocally) {
        return redirectABIEExtension(isLocally, null);
    }

    public String redirectABIEExtension(boolean isLocally, AggregateCoreComponent eAcc) {
        TreeNode treeNode = getSelectedTreeNode();
        ASBIENode asbieNode = (ASBIENode) treeNode.getData();
        AssociationCoreComponentProperty asccp = asbieNode.getAsccp();

        if (eAcc == null) {
            eAcc = extensionService.getExtensionAcc(asccp, isLocally);
        }

        TopLevelNode topLevelNode = getTopLevelNode();
        return "/views/core_component/extension.xhtml?accId=" + eAcc.getAccId() +
                "&rootAsccpId=" + topLevelNode.getAsccp().getAsccpId() + "&faces-redirect=true";
    }
}
