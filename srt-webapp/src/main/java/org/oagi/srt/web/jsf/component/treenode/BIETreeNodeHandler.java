package org.oagi.srt.web.jsf.component.treenode;

import org.oagi.srt.common.util.Utility;
import org.oagi.srt.model.BIENode;
import org.oagi.srt.model.BIENodeVisitor;
import org.oagi.srt.model.LazyNode;
import org.oagi.srt.model.Node;
import org.oagi.srt.model.bie.*;
import org.oagi.srt.model.bie.impl.BaseTopLevelNode;
import org.oagi.srt.repository.*;
import org.oagi.srt.repository.entity.*;
import org.oagi.srt.service.NodeService;
import org.oagi.srt.web.handler.UIHandler;
import org.oagi.srt.web.jsf.beans.bod.ProgressListener;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.List;

import static org.oagi.srt.repository.entity.AggregateBusinessInformationEntityState.Editing;

@Component
@Transactional(readOnly = true)
public class BIETreeNodeHandler extends UIHandler {

    private static final Logger logger = LoggerFactory.getLogger(BIETreeNodeHandler.class);

    @Autowired
    private NodeService nodeService;

    @Autowired
    private AggregateBusinessInformationEntityRepository abieRepository;

    @Autowired
    private AssociationBusinessInformationEntityRepository asbieRepository;

    @Autowired
    private AssociationBusinessInformationEntityPropertyRepository asbiepRepository;

    @Autowired
    private BasicBusinessInformationEntityRepository bbieRepository;

    @Autowired
    private BusinessDataTypePrimitiveRestrictionRepository bdtPriRestriRepository;

    @Autowired
    private BasicBusinessInformationEntityPropertyRepository bbiepRepository;

    @Autowired
    private BasicBusinessInformationEntitySupplementaryComponentRepository bbiescRepository;

    @Autowired
    private BusinessDataTypeSupplementaryComponentPrimitiveRestrictionRepository bdtScPriRestriRepository;

    @Autowired
    private TopLevelAbieRepository topLevelAbieRepository;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    private int batchSize = 25;

    private class SubmitBIENodeVisitor implements BIENodeVisitor {

        private User user;
        private ProgressListener progressListener;

        private TopLevelAbie topLevelAbie;
        private List<AggregateBusinessInformationEntity> abieList = new ArrayList();
        private List<AssociationBusinessInformationEntity> asbieList = new ArrayList();
        private List<AssociationBusinessInformationEntityProperty> asbiepList = new ArrayList();
        private List<BasicBusinessInformationEntity> bbieList = new ArrayList();
        private List<BasicBusinessInformationEntityProperty> bbiepList = new ArrayList();
        private List<BasicBusinessInformationEntitySupplementaryComponent> bbiescList = new ArrayList();

        public SubmitBIENodeVisitor(User user) {
            this.user = user;
        }

        public void setProgressListener(ProgressListener progressListener) {
            this.progressListener = progressListener;
        }

        @Override
        public void startNode(TopLevelNode topLevelNode) {
            topLevelAbie = new TopLevelAbie();
            topLevelAbie.setAbie(topLevelNode.getAbie());
            asbiepList.add(topLevelNode.getAsbiep());
        }

        @Override
        public void visitASBIENode(ASBIENode asbieNode) {
            abieList.add(asbieNode.getAbie());
            asbieList.add(asbieNode.getAsbie());
            asbiepList.add(asbieNode.getAsbiep());
        }

        @Override
        public void visitBBIENode(BBIENode bbieNode) {
            BasicBusinessInformationEntity bbie = handleBBIEBdtPriRestri(bbieNode);
            bbieList.add(bbie);
            bbiepList.add(bbieNode.getBbiep());
        }

        @Override
        public void visitBBIESCNode(BBIESCNode bbiescNode) {
            BasicBusinessInformationEntitySupplementaryComponent bbieSc = handleBBIEScBdtScPriRestri(bbiescNode);
            bbiescList.add(bbieSc);
        }

        @Override
        public void endNode() {
            adjust();
            save();
        }

        private void adjust() {
            AggregateBusinessInformationEntity tAbie = topLevelAbie.getAbie();
            tAbie.setCreatedBy(user.getAppUserId());
            tAbie.setLastUpdatedBy(user.getAppUserId());
            tAbie.setState(Editing);
            tAbie.setOwnerTopLevelAbie(topLevelAbie);
            tAbie.addPersistEventListener(progressListener);

            abieList.stream().forEach(abie -> {
                abie.setCreatedBy(user.getAppUserId());
                abie.setLastUpdatedBy(user.getAppUserId());
                abie.setState(Editing);
                abie.setOwnerTopLevelAbie(topLevelAbie);
                abie.addPersistEventListener(progressListener);
            });
            asbieList.stream().forEach(asbie -> {
                asbie.setCreatedBy(user.getAppUserId());
                asbie.setLastUpdatedBy(user.getAppUserId());
                asbie.setOwnerTopLevelAbie(topLevelAbie);
                asbie.addPersistEventListener(progressListener);
            });
            asbiepList.stream().forEach(asbiep -> {
                asbiep.setCreatedBy(user.getAppUserId());
                asbiep.setLastUpdatedBy(user.getAppUserId());
                asbiep.setOwnerTopLevelAbie(topLevelAbie);
                asbiep.addPersistEventListener(progressListener);
            });
            bbieList.stream().forEach(bbie -> {
                bbie.setCreatedBy(user.getAppUserId());
                bbie.setLastUpdatedBy(user.getAppUserId());
                bbie.setOwnerTopLevelAbie(topLevelAbie);
                bbie.addPersistEventListener(progressListener);
            });
            bbiepList.stream().forEach(bbiep -> {
                bbiep.setCreatedBy(user.getAppUserId());
                bbiep.setLastUpdatedBy(user.getAppUserId());
                bbiep.setOwnerTopLevelAbie(topLevelAbie);
                bbiep.addPersistEventListener(progressListener);
            });
            bbiescList.stream().forEach(bbiesc -> {
                bbiesc.setOwnerTopLevelAbie(topLevelAbie);
                bbiesc.addPersistEventListener(progressListener);
            });

            if (progressListener != null) {
                int maxCount = abieList.size() + asbieList.size() + asbiepList.size() + bbieList.size() + bbiepList.size() + bbiescList.size();
                progressListener.setMaxCount(maxCount);
            }
        }

        private void save() {
            saveTopLevelAbie();
            saveAbieList();
            saveBbiepList();
            saveBbieList();
            saveBbieScList();
            saveAsbiepList();
            saveAsbieList();

//            EntityManager entityManager = null;
//            EntityTransaction txn = null;
//            try {
//                entityManager = entityManagerFactory.createEntityManager();
//                txn = entityManager.getTransaction();
//                txn.begin();
//
//                saveTopLevelAbie(entityManager);
//                saveBatch(entityManager, abieList);
//                saveBatch(entityManager, bbiepList);
//                saveBatch(entityManager, bbieList);
//                saveBatch(entityManager, bbiescList);
//                saveBatch(entityManager, asbiepList);
//                saveBatch(entityManager, asbieList);
//
//                txn.commit();
//            } catch (RuntimeException e) {
//                if (txn != null && txn.isActive()) txn.rollback();
//                throw e;
//            } finally {
//                entityManager.close();
//            }
        }

        private void saveTopLevelAbie() {
            AggregateBusinessInformationEntity abie = topLevelAbie.getAbie();
            topLevelAbie.setAbie(null);

            topLevelAbieRepository.saveAndFlush(topLevelAbie);
            abie.setOwnerTopLevelAbie(topLevelAbie);

            abieRepository.saveAndFlush(abie);

            topLevelAbie.setAbie(abie);
            topLevelAbieRepository.save(topLevelAbie);
        }

        private void saveTopLevelAbie(EntityManager entityManager) {
            AggregateBusinessInformationEntity abie = topLevelAbie.getAbie();
            topLevelAbie.setAbie(null);

            entityManager.persist(topLevelAbie);
            entityManager.flush();
            abie.setOwnerTopLevelAbie(topLevelAbie);

            entityManager.persist(abie);
            entityManager.flush();

            topLevelAbie.setAbie(abie);
            entityManager.persist(topLevelAbie);
            entityManager.flush();
        }

        private void saveBatch(EntityManager entityManager, List list) {
            for (int i = 0, len = list.size(); i < len; ++i) {
                entityManager.persist(list.get(i));

                if (i % batchSize == 0) {
                    // flush a batch of inserts and release memory
                    entityManager.flush();
                    entityManager.clear();
                }
            }
        }

        private void saveAbieList() {
            abieRepository.save(abieList);
        }

        private void saveBbiepList() {
            bbiepRepository.save(bbiepList);
        }

        private void saveBbieList() {
            bbieRepository.save(bbieList);
        }

        private void saveBbieScList() {
            bbiescRepository.save(bbiescList);
        }

        private void saveAsbiepList() {
            asbiepRepository.save(asbiepList);
        }

        private void saveAsbieList() {
            asbieRepository.save(asbieList);
        }
    }

    private BasicBusinessInformationEntity handleBBIEBdtPriRestri(BBIENode bbieNode) {
        BasicBusinessInformationEntity bbie = bbieNode.getBbie();
        BBIERestrictionType restrictionType = bbieNode.getRestrictionType();
        switch (restrictionType) {
            case Primitive:
                if (bbie.getBdtPriRestriId() > 0L) {
                    bbie.setCodeListId(null);
                    bbie.setAgencyIdListId(null);
                }
                break;
            case Code:
                if (bbie.getCodeListId() > 0L) {
                    bbie.setBdtPriRestriId(null);
                    bbie.setAgencyIdListId(null);
                }
                break;
            case Agency:
                if (bbie.getAgencyIdListId() > 0L) {
                    bbie.setBdtPriRestriId(null);
                    bbie.setCodeListId(null);
                }
                break;
        }
        return bbie;
    }

    private BasicBusinessInformationEntitySupplementaryComponent handleBBIEScBdtScPriRestri(BBIESCNode bbiescNode) {
        BasicBusinessInformationEntitySupplementaryComponent bbieSc = bbiescNode.getBbieSc();
        BBIERestrictionType restrictionType = bbiescNode.getRestrictionType();
        switch (restrictionType) {
            case Primitive:
                if (bbieSc.getDtScPriRestriId() > 0L) {
                    bbieSc.setCodeListId(null);
                    bbieSc.setAgencyIdListId(null);
                }
                break;
            case Code:
                if (bbieSc.getCodeListId() > 0L) {
                    bbieSc.setDtScPriRestriId(null);
                    bbieSc.setAgencyIdListId(null);
                }
                break;
            case Agency:
                if (bbieSc.getAgencyIdListId() > 0L) {
                    bbieSc.setDtScPriRestriId(null);
                    bbieSc.setCodeListId(null);
                }
                break;
        }
        return bbieSc;
    }

    public TreeNode createTreeNode(AssociationCoreComponentProperty asccp, BusinessContext bizCtx) {
        BIENode node = nodeService.createBIENode(asccp, bizCtx);

        long s = System.currentTimeMillis();
        TreeBIENodeVisitor treeNodeVisitor = new TreeBIENodeVisitor();
        node.accept(treeNodeVisitor);
        logger.info("TreeNodes are structured - elapsed time: " + (System.currentTimeMillis() - s) + " ms");

        return treeNodeVisitor.getRoot();
    }

    public TreeNode createTreeNode(TopLevelAbie topLevelAbie) {
        BIENode node = nodeService.createBIENode(topLevelAbie);

        long s = System.currentTimeMillis();
        TreeBIENodeVisitor treeNodeVisitor = new TreeBIENodeVisitor();
        node.accept(treeNodeVisitor);
        logger.info("TreeNodes are structured - elapsed time: " + (System.currentTimeMillis() - s) + " ms");

        return treeNodeVisitor.getRoot();
    }

    public TreeNode createLazyTreeNode(TopLevelAbie topLevelAbie) {
        BIENode node = nodeService.createLazyBIENode(topLevelAbie);

        LazyTreeBIENodeVisitor lazyTreeNodeVisitor = new LazyTreeBIENodeVisitor();
        node.accept(lazyTreeNodeVisitor);
        return lazyTreeNodeVisitor.getParent();
    }

    public void expandLazyTreeNode(DefaultTreeNode treeNode) {
        LazyNode lazyNode = (LazyNode) treeNode.getData();
        if (!lazyNode.isFetched()) {
            lazyNode.fetch();

            LazyTreeBIENodeVisitor lazyTreeNodeVisitor = new LazyTreeBIENodeVisitor(treeNode);
            treeNode.setChildren(new ArrayList()); // clear children

            for (Node child : lazyNode.getChildren()) {
                ((BIENode) child).accept(lazyTreeNodeVisitor);
            }
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void submit(BaseTopLevelNode node, ProgressListener progressListener) {
        SubmitBIENodeVisitor submitNodeVisitor = new SubmitBIENodeVisitor(getCurrentUser());
        submitNodeVisitor.setProgressListener(progressListener);
        node.accept(submitNodeVisitor);
    }


    private class UpdateBIENodeVisitor implements BIENodeVisitor {

        private User user;
        private List<AggregateBusinessInformationEntity> abieList = new ArrayList();
        private List<AssociationBusinessInformationEntity> asbieList = new ArrayList();
        private List<AssociationBusinessInformationEntityProperty> asbiepList = new ArrayList();
        private List<BasicBusinessInformationEntity> bbieList = new ArrayList();
        private List<BasicBusinessInformationEntityProperty> bbiepList = new ArrayList();
        private List<BasicBusinessInformationEntitySupplementaryComponent> bbiescList = new ArrayList();

        public UpdateBIENodeVisitor(User user) {
            this.user = user;
        }

        @Override
        public void startNode(TopLevelNode topLevelNode) {
            AggregateBusinessInformationEntity abie = topLevelNode.getAbie();
            if (abie.isDirty()) {
                abieList.add(abie);
            }
            AssociationBusinessInformationEntityProperty asbiep = topLevelNode.getAsbiep();
            if (asbiep.isDirty()) {
                asbiepList.add(asbiep);
            }
        }

        @Override
        public void visitASBIENode(ASBIENode asbieNode) {
            AggregateBusinessInformationEntity abie = asbieNode.getAbie();
            if (abie.isDirty()) {
                abieList.add(abie);
            }
            AssociationBusinessInformationEntity asbie = asbieNode.getAsbie();
            if (asbie.isDirty()) {
                asbieList.add(asbie);
            }
            AssociationBusinessInformationEntityProperty asbiep = asbieNode.getAsbiep();
            if (asbiep.isDirty()) {
                asbiepList.add(asbiep);
            }
        }

        @Override
        public void visitBBIENode(BBIENode bbieNode) {
            BasicBusinessInformationEntity bbie = handleBBIEBdtPriRestri(bbieNode);
            if (bbie.isDirty()) {
                bbieList.add(bbie);
            }
            BasicBusinessInformationEntityProperty bbiep = bbieNode.getBbiep();
            if (bbiep.isDirty()) {
                bbiepList.add(bbiep);
            }
        }

        @Override
        public void visitBBIESCNode(BBIESCNode bbiescNode) {
            BasicBusinessInformationEntitySupplementaryComponent bbieSc = handleBBIEScBdtScPriRestri(bbiescNode);
            if (bbieSc.isDirty()) {
                bbiescList.add(bbieSc);
            }
        }

        @Override
        public void endNode() {
            adjust();
            save();
        }

        private void adjust() {
            abieList.stream().forEach(abie -> {
                abie.setLastUpdatedBy(user.getAppUserId());
            });
            asbieList.stream().forEach(asbie -> {
                asbie.setLastUpdatedBy(user.getAppUserId());
            });
            asbiepList.stream().forEach(asbiep -> {
                asbiep.setLastUpdatedBy(user.getAppUserId());
            });
            bbieList.stream().forEach(bbie -> {
                bbie.setLastUpdatedBy(user.getAppUserId());
            });
            bbiepList.stream().forEach(bbiep -> {
                bbiep.setLastUpdatedBy(user.getAppUserId());
            });
        }

        private void save() {
            abieRepository.save(abieList);
            asbieRepository.save(asbieList);
            asbiepRepository.save(asbiepList);
            bbieRepository.save(bbieList);
            bbiepRepository.save(bbiepList);
            bbiescRepository.save(bbiescList);
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void update(TopLevelNode node) {
        UpdateBIENodeVisitor updateNodeVisitor = new UpdateBIENodeVisitor(getCurrentUser());
        node.accept(updateNodeVisitor);
    }

    private class CopyBIENodeVisitor implements BIENodeVisitor {

        private User user;
        private BusinessContext bizCtx;
        private ProgressListener progressListener;

        private TopLevelAbie topLevelAbie;
        private List<AggregateBusinessInformationEntity> abieList = new ArrayList();
        private List<AssociationBusinessInformationEntity> asbieList = new ArrayList();
        private List<AssociationBusinessInformationEntityProperty> asbiepList = new ArrayList();
        private List<BasicBusinessInformationEntity> bbieList = new ArrayList();
        private List<BasicBusinessInformationEntityProperty> bbiepList = new ArrayList();
        private List<BasicBusinessInformationEntitySupplementaryComponent> bbiescList = new ArrayList();

        public CopyBIENodeVisitor(User user, BusinessContext bizCtx) {
            this.user = user;
            this.bizCtx = bizCtx;
        }

        public void setProgressListener(ProgressListener progressListener) {
            this.progressListener = progressListener;
        }

        @Override
        public void startNode(TopLevelNode topLevelNode) {
            topLevelAbie = new TopLevelAbie();
            topLevelAbie.setAbie(topLevelNode.getAbie());
            asbiepList.add(topLevelNode.getAsbiep());
        }

        @Override
        public void visitASBIENode(ASBIENode asbieNode) {
            abieList.add(asbieNode.getAbie());
            asbieList.add(asbieNode.getAsbie());
            asbiepList.add(asbieNode.getAsbiep());
        }

        @Override
        public void visitBBIENode(BBIENode bbieNode) {
            BasicBusinessInformationEntity bbie = handleBBIEBdtPriRestri(bbieNode);
            bbieList.add(bbie);
            bbiepList.add(bbieNode.getBbiep());
        }

        @Override
        public void visitBBIESCNode(BBIESCNode bbiescNode) {
            BasicBusinessInformationEntitySupplementaryComponent bbieSc = handleBBIEScBdtScPriRestri(bbiescNode);
            bbiescList.add(bbieSc);
        }

        @Override
        public void endNode() {
            adjust();
            save();
        }

        private void adjust() {
            AggregateBusinessInformationEntity tAbie = topLevelAbie.getAbie();
            tAbie.setAbieId(0L);
            tAbie.setGuid(Utility.generateGUID());
            tAbie.setCreatedBy(user.getAppUserId());
            tAbie.setLastUpdatedBy(user.getAppUserId());
            tAbie.setState(Editing);
            tAbie.setOwnerTopLevelAbie(topLevelAbie);
            tAbie.addPersistEventListener(progressListener);

            abieList.stream().forEach(abie -> {
                abie.setAbieId(0L);
                abie.setGuid(Utility.generateGUID());
                abie.setCreatedBy(user.getAppUserId());
                abie.setLastUpdatedBy(user.getAppUserId());
                abie.setState(Editing);
                abie.setOwnerTopLevelAbie(topLevelAbie);
                abie.addPersistEventListener(progressListener);
            });
            asbieList.stream().forEach(asbie -> {
                asbie.setAsbieId(0L);
                asbie.setGuid(Utility.generateGUID());
                asbie.setCreatedBy(user.getAppUserId());
                asbie.setLastUpdatedBy(user.getAppUserId());
                asbie.setOwnerTopLevelAbie(topLevelAbie);
                asbie.addPersistEventListener(progressListener);
            });
            asbiepList.stream().forEach(asbiep -> {
                asbiep.setAsbiepId(0L);
                asbiep.setGuid(Utility.generateGUID());
                asbiep.setCreatedBy(user.getAppUserId());
                asbiep.setLastUpdatedBy(user.getAppUserId());
                asbiep.setOwnerTopLevelAbie(topLevelAbie);
                asbiep.addPersistEventListener(progressListener);
            });
            bbieList.stream().forEach(bbie -> {
                bbie.setBbieId(0L);
                bbie.setGuid(Utility.generateGUID());
                bbie.setCreatedBy(user.getAppUserId());
                bbie.setLastUpdatedBy(user.getAppUserId());
                bbie.setOwnerTopLevelAbie(topLevelAbie);
                bbie.addPersistEventListener(progressListener);
            });
            bbiepList.stream().forEach(bbiep -> {
                bbiep.setBbiepId(0L);
                bbiep.setGuid(Utility.generateGUID());
                bbiep.setCreatedBy(user.getAppUserId());
                bbiep.setLastUpdatedBy(user.getAppUserId());
                bbiep.setOwnerTopLevelAbie(topLevelAbie);
                bbiep.addPersistEventListener(progressListener);
            });
            bbiescList.stream().forEach(bbiesc -> {
                bbiesc.setBbieScId(0L);
                bbiesc.setGuid(Utility.generateGUID());
                bbiesc.setOwnerTopLevelAbie(topLevelAbie);
                bbiesc.addPersistEventListener(progressListener);
            });

            if (progressListener != null) {
                int maxCount = abieList.size() + asbieList.size() + asbiepList.size() + bbieList.size() + bbiepList.size() + bbiescList.size();
                progressListener.setMaxCount(maxCount);
            }
        }

        private void save() {
            saveTopLevelAbie();
            saveAbieList();
            saveBbiepList();
            saveBbieList();
            saveBbieScList();
            saveAsbiepList();
            saveAsbieList();
        }

        private void saveTopLevelAbie() {
            AggregateBusinessInformationEntity abie = topLevelAbie.getAbie();
            topLevelAbie.setAbie(null);

            topLevelAbieRepository.saveAndFlush(topLevelAbie);
            abie.setOwnerTopLevelAbie(topLevelAbie);

            abieRepository.saveAndFlush(abie);

            topLevelAbie.setAbie(abie);
            topLevelAbieRepository.save(topLevelAbie);
        }

        private void saveAbieList() {
            abieRepository.save(abieList);
        }

        private void saveBbiepList() {
            bbiepRepository.save(bbiepList);
        }

        private void saveBbieList() {
            bbieRepository.save(bbieList);
        }

        private void saveBbieScList() {
            bbiescRepository.save(bbiescList);
        }

        private void saveAsbiepList() {
            asbiepRepository.save(asbiepList);
        }

        private void saveAsbieList() {
            asbieRepository.save(asbieList);
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void copy(BaseTopLevelNode node, BusinessContext bizCtx, ProgressListener progressListener) {
        CopyBIENodeVisitor copyNodeVisitor = new CopyBIENodeVisitor(getCurrentUser(), bizCtx);
        copyNodeVisitor.setProgressListener(progressListener);
        node.accept(copyNodeVisitor);
    }

}
