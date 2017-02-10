package org.oagi.srt.service;

import org.oagi.srt.common.util.Utility;
import org.oagi.srt.model.BIENode;
import org.oagi.srt.model.LazyBIENode;
import org.oagi.srt.model.Node;
import org.oagi.srt.model.bie.ASBIENode;
import org.oagi.srt.model.bie.BBIENode;
import org.oagi.srt.model.bie.Fetcher;
import org.oagi.srt.model.bie.TopLevelNode;
import org.oagi.srt.model.bie.impl.*;
import org.oagi.srt.model.bie.visitor.BIENodeSortVisitor;
import org.oagi.srt.provider.CoreComponentProvider;
import org.oagi.srt.repository.*;
import org.oagi.srt.repository.entity.*;
import org.oagi.srt.service.bie.EagerFetchedDataContainerForProfileBODBuilder;
import org.oagi.srt.service.bie.ProfileBODBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.oagi.srt.repository.entity.BasicCoreComponentEntityType.Attribute;
import static org.oagi.srt.repository.entity.BasicCoreComponentEntityType.Element;
import static org.oagi.srt.repository.entity.CoreComponentState.Published;
import static org.oagi.srt.repository.entity.OagisComponentType.SemanticGroup;
import static org.oagi.srt.repository.entity.OagisComponentType.UserExtensionGroup;

@Service
@Transactional(readOnly = true)
public class NodeService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private CoreComponentService coreComponentService;

    @Autowired
    private BusinessContextRepository businessContextRepository;

    @Autowired
    private DataTypeRepository dataTypeRepository;

    @Autowired
    private DataTypeSupplementaryComponentRepository dtScRepository;

    @Autowired
    private AggregateCoreComponentRepository accRepository;

    @Autowired
    private AssociationCoreComponentPropertyRepository asccpRepository;

    @Autowired
    private BasicCoreComponentRepository bccRepository;

    @Autowired
    private AssociationCoreComponentRepository asccRepository;

    @Autowired
    private BasicCoreComponentPropertyRepository bccpRepository;

    @Autowired
    private BusinessDataTypePrimitiveRestrictionRepository bdtPriRestriRepository;

    @Autowired
    private BusinessDataTypeSupplementaryComponentPrimitiveRestrictionRepository bdtScPriRestriRepository;

    @Autowired
    private AggregateBusinessInformationEntityRepository abieRepository;

    @Autowired
    private AssociationBusinessInformationEntityRepository asbieRepository;

    @Autowired
    private AssociationBusinessInformationEntityPropertyRepository asbiepRepository;

    @Autowired
    private BasicBusinessInformationEntityRepository bbieRepository;

    @Autowired
    private BasicBusinessInformationEntityPropertyRepository bbiepRepository;

    @Autowired
    private BasicBusinessInformationEntitySupplementaryComponentRepository bbieScRepository;

    private ExecutorService executorService;

    @PostConstruct
    public void init() {
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);
    }

    @PreDestroy
    public void destroy() {
        executorService.shutdownNow();
    }

    public BIENode createBIENode(AssociationCoreComponentProperty asccp, BusinessContext bizCtx) {
        long s = System.currentTimeMillis();
        EagerFetchedDataContainerForProfileBODBuilder dataContainer =
                applicationContext.getBean(EagerFetchedDataContainerForProfileBODBuilder.class);

        logger.info("DataContainerForProfileBODBuilder instantiated - elapsed time: " + (System.currentTimeMillis() - s) + " ms");

        ProfileBODBuilder profileBODBuilder = applicationContext.getBean(ProfileBODBuilder.class);
        profileBODBuilder.setDataContainer(dataContainer);

        return profileBODBuilder.createBIENode(asccp, bizCtx);
    }

    public BIENode createBIENode(BusinessInformationEntityUserExtensionRevision bieUserExtRevision) {
        long s = System.currentTimeMillis();
        EagerFetchedDataContainerForProfileBODBuilder dataContainer =
                applicationContext.getBean(EagerFetchedDataContainerForProfileBODBuilder.class);

        logger.info("DataContainerForProfileBODBuilder instantiated - elapsed time: " + (System.currentTimeMillis() - s) + " ms");

        ProfileBODBuilder profileBODBuilder = applicationContext.getBean(ProfileBODBuilder.class);
        profileBODBuilder.setDataContainer(dataContainer);

        return profileBODBuilder.createBIENode(bieUserExtRevision);
    }


    private class DataContainerForProfileBODLoader {
        private TopLevelAbie topLevelAbie;
        private List<BusinessContext> businessContextList;
        private List<BasicCoreComponent> bccList;
        private List<AssociationCoreComponentProperty> asccpList;
        private List<BasicCoreComponentProperty> bccpList;
        private List<DataType> dtList;
        private List<DataTypeSupplementaryComponent> dtScList;
        private List<BusinessDataTypePrimitiveRestriction> bdtPriRestriList;

        private List<AggregateBusinessInformationEntity> abieList;
        private List<AssociationBusinessInformationEntity> asbieList;
        private List<AssociationBusinessInformationEntityProperty> asbiepList;
        private List<BasicBusinessInformationEntity> bbieList;
        private List<BasicBusinessInformationEntityProperty> bbiepList;
        private List<BasicBusinessInformationEntitySupplementaryComponent> bbiescList;

        private Map<Long, BusinessContext> bizCtxIdMap;
        private Map<Long, BasicCoreComponent> bccIdMap;
        private Map<Long, AssociationCoreComponentProperty> asccpIdMap;
        private Map<Long, BasicCoreComponentProperty> bccpIdMap;
        private Map<Long, DataType> dtIdMap;
        private Map<Long, DataTypeSupplementaryComponent> dtScIdMap;
        private Map<Long, List<BusinessDataTypePrimitiveRestriction>> bdtPriRestriListByBdtIdMap;

        private Map<Long, AggregateBusinessInformationEntity> abieIdMap;
        private Map<Long, List<AssociationBusinessInformationEntity>> asbieByFromAbieIdMap;
        private Map<Long, AssociationBusinessInformationEntityProperty> asbiepIdMap;
        private Map<Long, AssociationBusinessInformationEntityProperty> asbiepByRoleOfAbieIdMap;
        private Map<Long, List<BasicBusinessInformationEntity>> bbieByFromAbieIdMap;
        private Map<Long, BasicBusinessInformationEntityProperty> bbiepIdMap;
        private Map<Long, List<BasicBusinessInformationEntitySupplementaryComponent>> bbieScListByBbieIdMap;


        public DataContainerForProfileBODLoader(TopLevelAbie topLevelAbie) {
            this.topLevelAbie = topLevelAbie;

            ExecutorCompletionService executorCompletionService = new ExecutorCompletionService(executorService);
            executorCompletionService.submit(() -> {
                businessContextList = businessContextRepository.findAll();
                bizCtxIdMap = businessContextList.stream()
                        .collect(Collectors.toMap(e -> e.getBizCtxId(), Function.identity()));
            }, null);

            executorCompletionService.submit(() -> {
                bccList = bccRepository.findAllWithRevisionNum(0);
                bccIdMap = bccList.stream()
                        .collect(Collectors.toMap(e -> e.getBccId(), Function.identity()));
            }, null);

            executorCompletionService.submit(() -> {
                asccpList = asccpRepository.findAllWithRevisionNum(0);
                asccpIdMap = asccpList.stream()
                        .collect(Collectors.toMap(e -> e.getAsccpId(), Function.identity()));
            }, null);

            executorCompletionService.submit(() -> {
                bccpList = bccpRepository.findAllWithRevisionNum(0);
                bccpIdMap = bccpList.stream()
                        .collect(Collectors.toMap(e -> e.getBccpId(), Function.identity()));
            }, null);

            executorCompletionService.submit(() -> {
                dtList = dataTypeRepository.findAll();
                dtIdMap = dtList.stream()
                        .collect(Collectors.toMap(e -> e.getDtId(), Function.identity()));
            }, null);

            executorCompletionService.submit(() -> {
                dtScList = dtScRepository.findAll();
                dtScIdMap = dtScList.stream()
                        .collect(Collectors.toMap(e -> e.getDtScId(), Function.identity()));
            }, null);

            executorCompletionService.submit(() -> {
                bdtPriRestriList = bdtPriRestriRepository.findAll();
                bdtPriRestriListByBdtIdMap = bdtPriRestriList.stream()
                        .collect(Collectors.groupingBy(e -> e.getBdtId()));
            }, null);

            executorCompletionService.submit(() -> {
                abieList = abieRepository.findByOwnerTopLevelAbieId(topLevelAbie.getTopLevelAbieId());
                abieIdMap = abieList.stream()
                        .collect(Collectors.toMap(e -> e.getAbieId(), Function.identity()));
            }, null);

            executorCompletionService.submit(() -> {
                asbieList = asbieRepository.findByOwnerTopLevelAbieId(topLevelAbie.getTopLevelAbieId());
                asbieByFromAbieIdMap = asbieList.stream()
                        .collect(Collectors.groupingBy(e -> e.getFromAbieId()));
            }, null);

            executorCompletionService.submit(() -> {
                asbiepList = asbiepRepository.findByOwnerTopLevelAbieId(topLevelAbie.getTopLevelAbieId());
                asbiepIdMap = asbiepList.stream()
                        .collect(Collectors.toMap(e -> e.getAsbiepId(), Function.identity()));
                asbiepByRoleOfAbieIdMap = asbiepList.stream()
                        .collect(Collectors.toMap(e -> e.getRoleOfAbieId(), Function.identity()));
            }, null);

            executorCompletionService.submit(() -> {
                bbieList = bbieRepository.findByOwnerTopLevelAbieId(topLevelAbie.getTopLevelAbieId());
                bbieByFromAbieIdMap = bbieList.stream()
                        .collect(Collectors.groupingBy(e -> e.getFromAbieId()));
            }, null);

            executorCompletionService.submit(() -> {
                bbiepList = bbiepRepository.findByOwnerTopLevelAbieId(topLevelAbie.getTopLevelAbieId());
                bbiepIdMap = bbiepList.stream()
                        .collect(Collectors.toMap(e -> e.getBbiepId(), Function.identity()));
            }, null);

            executorCompletionService.submit(() -> {
                bbiescList = bbieScRepository.findByOwnerTopLevelAbieId(topLevelAbie.getTopLevelAbieId());
                bbieScListByBbieIdMap = bbiescList.stream()
                        .collect(Collectors.groupingBy(e -> e.getBbieId()));
            }, null);

            for (int i = 0, taskSize = 13; i < taskSize; ++i) {
                try {
                    executorCompletionService.take().get();
                } catch (InterruptedException e) {
                    throw new IllegalStateException(e);
                } catch (ExecutionException e) {
                    throw new IllegalStateException(e.getCause());
                }
            }
        }

        public BusinessContext findBusinessContext(long bizCtxId) {
            return bizCtxIdMap.get(bizCtxId);
        }

        public AggregateBusinessInformationEntity findAbie(long abieId) {
            return abieIdMap.get(abieId);
        }

        public BasicCoreComponent findBcc(long bccId) {
            return bccIdMap.get(bccId);
        }

        public BasicCoreComponentProperty findBccp(long bccpId) {
            return bccpIdMap.get(bccpId);
        }

        public AssociationCoreComponentProperty findAsccp(long asccpId) {
            return asccpIdMap.get(asccpId);
        }

        public DataType findDt(long dtId) {
            return dtIdMap.get(dtId);
        }

        public DataTypeSupplementaryComponent findDtSc(long dtScId) {
            return dtScIdMap.get(dtScId);
        }

        public List<BusinessDataTypePrimitiveRestriction> findBdtPriRestriByBdtId(long bdtId) {
            List<BusinessDataTypePrimitiveRestriction> bdtPriRestriList = bdtPriRestriListByBdtIdMap.get(bdtId);
            return (bdtPriRestriList != null) ? bdtPriRestriList : Collections.emptyList();
        }

        public AssociationBusinessInformationEntityProperty findAsbiep(long asbiepId) {
            return asbiepIdMap.get(asbiepId);
        }

        public BasicBusinessInformationEntityProperty findBbiep(long bbiepId) {
            return bbiepIdMap.get(bbiepId);
        }

        public AssociationBusinessInformationEntityProperty findAsbiepByRoleOfAbie(AggregateBusinessInformationEntity abie) {
            return asbiepByRoleOfAbieIdMap.get(abie.getAbieId());
        }

        public List<BasicBusinessInformationEntity> findBbieByFromAbieId(AggregateBusinessInformationEntity abie) {
            List<BasicBusinessInformationEntity> bbieList = bbieByFromAbieIdMap.get(abie.getAbieId());
            return (bbieList != null) ? bbieList : Collections.emptyList();
        }

        public List<AssociationBusinessInformationEntity> findAsbieByFromAbieId(AggregateBusinessInformationEntity abie) {
            List<AssociationBusinessInformationEntity> asbieList = asbieByFromAbieIdMap.get(abie.getAbieId());
            return (asbieList != null) ? asbieList : Collections.emptyList();
        }

        public List<BasicBusinessInformationEntitySupplementaryComponent> findBbieScByBbieId(long bbieId) {
            List<BasicBusinessInformationEntitySupplementaryComponent> bbieScList = bbieScListByBbieIdMap.get(bbieId);
            return (bbieScList != null) ? bbieScList : Collections.emptyList();
        }

    }

    public BIENode createBIENode(TopLevelAbie topLevelAbie) {
        long s = System.currentTimeMillis();
        DataContainerForProfileBODLoader dataContainer = new DataContainerForProfileBODLoader(topLevelAbie);
        logger.info("DataContainerForProfileBODLoader instantiated - elapsed time: " + (System.currentTimeMillis() - s) + " ms");
        s = System.currentTimeMillis();

        AggregateBusinessInformationEntity abie = topLevelAbie.getAbie();
        BusinessContext bizCtx = dataContainer.findBusinessContext(abie.getBizCtxId());
        AssociationBusinessInformationEntityProperty asbiep = dataContainer.findAsbiepByRoleOfAbie(abie);
        AssociationCoreComponentProperty asccp = dataContainer.findAsccp(asbiep.getBasedAsccpId());
        TopLevelNode topLevelNode = new BaseTopLevelNode(asbiep, asccp, abie, bizCtx);
        createBIEChildren(dataContainer, abie, topLevelNode);
        logger.info("Nodes are structured - elapsed time: " + (System.currentTimeMillis() - s) + " ms");

        s = System.currentTimeMillis();
        topLevelNode.accept(new BIENodeSortVisitor());
        logger.info("Node sorted - elapsed time: " + (System.currentTimeMillis() - s) + " ms");

        return topLevelNode;
    }

    private void createBIEChildren(DataContainerForProfileBODLoader dataContainer,
                                   AggregateBusinessInformationEntity abie, Node parent) {
        List<BasicBusinessInformationEntity> bbieList = dataContainer.findBbieByFromAbieId(abie);
        List<AssociationBusinessInformationEntity> asbieList = dataContainer.findAsbieByFromAbieId(abie);

        Map<BusinessInformationEntity, Double> sequence = new HashMap();
        ValueComparator bvc = new ValueComparator(sequence);
        TreeMap<BusinessInformationEntity, Double> ordered_sequence = new TreeMap(bvc);

        for (BasicBusinessInformationEntity bbie : bbieList) {
            double sk = bbie.getSeqKey();
            if (getEntityType(dataContainer, bbie.getBasedBccId()) == Attribute)
                appendBBIENode(dataContainer, bbie, parent);
            else
                sequence.put(bbie, sk);
        }

        for (AssociationBusinessInformationEntity asbie : asbieList) {
            double sk = asbie.getSeqKey();
            sequence.put(asbie, sk);
        }

        ordered_sequence.putAll(sequence);
        Set set = ordered_sequence.entrySet();
        Iterator i = set.iterator();
        while (i.hasNext()) {
            Map.Entry me = (Map.Entry) i.next();
            if (me.getKey() instanceof BasicBusinessInformationEntity)
                appendBBIENode(dataContainer, (BasicBusinessInformationEntity) me.getKey(), parent);
            else
                appendASBIENode(dataContainer, (AssociationBusinessInformationEntity) me.getKey(), parent);
        }
    }

    class ValueComparator implements Comparator<BusinessInformationEntity> {

        Map<BusinessInformationEntity, Double> base;

        public ValueComparator(Map<BusinessInformationEntity, Double> base) {
            this.base = base;
        }

        // Note: this comparator imposes orderings that are inconsistent with equals.
        public int compare(BusinessInformationEntity a, BusinessInformationEntity b) {
            if (base.get(a) <= base.get(b)) {
                return -1;
            } else {
                return 1;
            } // returning 0 would merge keys
        }
    }

    public BasicCoreComponentEntityType getEntityType(DataContainerForProfileBODLoader dataContainer, long bccId) {
        BasicCoreComponent basicCoreComponent = dataContainer.findBcc(bccId);
        return basicCoreComponent.getEntityType();
    }

    private void appendBBIENode(DataContainerForProfileBODLoader dataContainer,
                                BasicBusinessInformationEntity bbie, Node parent) {
        BasicBusinessInformationEntityProperty bbiep = dataContainer.findBbiep(bbie.getToBbiepId());
        BasicCoreComponentProperty bccp = dataContainer.findBccp(bbiep.getBasedBccpId());

        long bdtId = bccp.getBdtId();
        DataType bdt = dataContainer.findDt(bdtId);
        BusinessDataTypePrimitiveRestriction bdtPriRestri = bdtPriRestriRepository.findOne(bbie.getBdtPriRestriId());

        int seqKey = (int) bbie.getSeqKey();
        BBIENode bbieNode = new BaseBBIENode(seqKey, parent, bbie, bdtPriRestri, bbiep, bccp, bdt);
        appendBBIESC(dataContainer, bbie, bbieNode);
    }

    private void appendBBIESC(DataContainerForProfileBODLoader dataContainer,
                              BasicBusinessInformationEntity bbie, BBIENode parent) {
        List<BasicBusinessInformationEntitySupplementaryComponent> bbiescList =
                dataContainer.findBbieScByBbieId(bbie.getBbieId());
        for (BasicBusinessInformationEntitySupplementaryComponent bbiesc : bbiescList) {
            long dtScId = bbiesc.getDtScId();
            DataTypeSupplementaryComponent dtsc = dataContainer.findDtSc(dtScId);
            BusinessDataTypeSupplementaryComponentPrimitiveRestriction bdtPriRestri =
                    bdtScPriRestriRepository.findOne(bbiesc.getDtScPriRestriId());

            new BaseBBIESCNode(parent, bbiesc, bdtPriRestri, dtsc);
        }
    }

    private void appendASBIENode(DataContainerForProfileBODLoader dataContainer,
                                 AssociationBusinessInformationEntity asbie, Node parent) {
        AssociationBusinessInformationEntityProperty asbiep = dataContainer.findAsbiep(asbie.getToAsbiepId());
        AssociationCoreComponentProperty asccp = dataContainer.findAsccp(asbiep.getBasedAsccpId());
        AggregateBusinessInformationEntity roleOfAbie = dataContainer.findAbie(asbiep.getRoleOfAbieId());

        int seqKey = (int) asbie.getSeqKey();
        ASBIENode asbieNode = new BaseASBIENode(seqKey, parent, asbie, asbiep, asccp, roleOfAbie);
        createBIEChildren(dataContainer, roleOfAbie, asbieNode);
    }

    /*
     * Lazy Node
     */
    public LazyBIENode createLazyBIENode(AssociationCoreComponentProperty asccp, BusinessContext bizCtx) {
        long roleOfAccId = asccp.getRoleOfAccId();
        AggregateCoreComponent acc = accRepository.findOneByAccIdAndRevisionNumAndState(
                roleOfAccId, 0, Published);
        AggregateBusinessInformationEntity abie = createABIE(acc, bizCtx);
        AssociationBusinessInformationEntityProperty asbiep = createASBIEP(asccp, abie);
        TopLevelNode topLevelNode = new BaseTopLevelNode(asbiep, asccp, abie, bizCtx);
        LazyBIEFetcher fetcher = new LazyBIEFetcher(acc, abie, bizCtx);
        return new LazyTopLevelNode(topLevelNode, fetcher, fetcher.getChildrenCount());
    }

    private AggregateBusinessInformationEntity createABIE(AggregateCoreComponent acc, BusinessContext bizCtx) {
        if (acc == null) {
            throw new IllegalArgumentException("'acc' argument must not be null.");
        }
        if (bizCtx == null) {
            throw new IllegalArgumentException("'bizCtx' argument must not be null.");
        }

        AggregateBusinessInformationEntity abie = new AggregateBusinessInformationEntity();
        String abieGuid = Utility.generateGUID();
        abie.setGuid(abieGuid);
        abie.setBasedAcc(acc);
        abie.setBizCtx(bizCtx);
        abie.setDefinition(acc.getDefinition());
        abie.afterLoaded();

        return abie;
    }

    private AssociationBusinessInformationEntityProperty createASBIEP(AssociationCoreComponentProperty asccp,
                                                                      AggregateBusinessInformationEntity abie) {
        if (asccp == null) {
            throw new IllegalArgumentException("'asccp' argument must not be null.");
        }
        if (abie == null) {
            throw new IllegalArgumentException("'abie' argument must not be null.");
        }

        AssociationBusinessInformationEntityProperty asbiep =
                new AssociationBusinessInformationEntityProperty();
        asbiep.setGuid(Utility.generateGUID());
        asbiep.setBasedAsccp(asccp);
        asbiep.setRoleOfAbie(abie);
        asbiep.setDefinition(asccp.getDefinition());
        asbiep.afterLoaded();

        return asbiep;
    }

    private class LazyBIEFetcher implements Fetcher {

        private AggregateCoreComponent acc;
        private AggregateBusinessInformationEntity abie;
        private BusinessContext bizCtx;

        public LazyBIEFetcher(AggregateCoreComponent acc, AggregateBusinessInformationEntity abie, BusinessContext bizCtx) {
            this.acc = acc;
            this.abie = abie;
            this.bizCtx = bizCtx;
        }

        public int getChildrenCount() {
            LinkedList<AggregateCoreComponent> accList = new LinkedList();
            AggregateCoreComponent acc = this.acc;
            while (acc != null) {
                accList.add(acc);
                acc = accRepository.findOneByAccIdAndRevisionNumAndState(acc.getBasedAccId(), 0, Published);
            }

            int childrenCount = 0;
            while (!accList.isEmpty()) {
                acc = accList.pollLast();

                List<CoreComponentRelation> childAssoc = queryNestedChildAssoc(acc);
                childrenCount += childAssoc.size();
            }

            return childrenCount;
        }

        @Override
        public void fetch(Node parent) {
            LinkedList<AggregateCoreComponent> accList = new LinkedList();
            AggregateCoreComponent acc = this.acc;
            while (acc != null) {
                accList.add(acc);
                acc = accRepository.findOneByAccIdAndRevisionNumAndState(acc.getBasedAccId(), 0, Published);
            }

            int seqKey = 1;
            while (!accList.isEmpty()) {
                acc = accList.pollLast();

                List<CoreComponentRelation> childAssoc = queryNestedChildAssoc(acc);
                for (int i = 0; i < childAssoc.size(); i++) {
                    CoreComponent assoc = childAssoc.get(i);
                    if (assoc instanceof BasicCoreComponent) {
                        BasicCoreComponent bcc = (BasicCoreComponent) assoc;
                        if (Attribute == bcc.getEntityType()) {
                            appendBBIELazyNode(parent, bcc, abie);
                        }
                    }
                }

                for (CoreComponent assoc : childAssoc) {
                    if (assoc instanceof BasicCoreComponent) {
                        BasicCoreComponent bcc = (BasicCoreComponent) assoc;
                        if (Element == bcc.getEntityType()) {
                            appendBBIELazyNode(parent, bcc, abie, seqKey++);
                        }
                    } else if (assoc instanceof AssociationCoreComponent) {
                        AssociationCoreComponent ascc = (AssociationCoreComponent) assoc;
                        appendASBIELazyNode(parent, ascc, abie, bizCtx, seqKey++);
                    }
                }
            }
        }

        private void appendBBIELazyNode(Node parent, BasicCoreComponent bcc,
                                        AggregateBusinessInformationEntity abie) {
            appendBBIELazyNode(parent, bcc, abie, 0);
        }

        private void appendBBIELazyNode(Node parent, BasicCoreComponent bcc,
                                        AggregateBusinessInformationEntity abie, int seqKey) {

            BasicBusinessInformationEntity bbie = null;
            BasicBusinessInformationEntityProperty bbiep = null;
            BasicCoreComponentProperty bccp = null;
            long abieId = abie.getAbieId();

            if (abieId > 0L) {
                long bccId = bcc.getBccId();
                long ownerTopLevelAbieId = abie.getOwnerTopLevelAbieId();

                bbie = bbieRepository.findOneByBasedBccIdAndFromAbieIdAndOwnerTopLevelAbieId(bccId, abieId, ownerTopLevelAbieId);
                if (bbie != null) {
                    long toBbiepId = bbie.getToBbiepId();
                    bbiep = bbiepRepository.findOne(toBbiepId);
                    bccp = bccpRepository.findOne(bbiep.getBasedBccpId());
                }
            }

            if (bbie == null) {
                long toBccpId = bcc.getToBccpId();
                bccp = bccpRepository.findOneByBccpIdAndRevisionNumAndState(toBccpId, 0, Published);
                bbiep = createBBIEP(bccp);
            }

            long bdtId = bccp.getBdtId();
            DataType bdt = dataTypeRepository.findOne(bdtId);
            BusinessDataTypePrimitiveRestriction bdtPriRestri =
                    bdtPriRestriRepository.findOneByBdtIdAndCodeListIdIsNotZero(bdtId);
            long codeListId = (bdtPriRestri != null) ? bdtPriRestri.getCodeListId() : 0L;
            bdtPriRestri = bdtPriRestriRepository.findOneByBdtIdAndDefault(bdtId, true);

            if (bbie == null) {
                bbie = createBBIE(bcc, abie, bbiep, bdtPriRestri, codeListId, seqKey);
            }

            BBIENode bbieNode = new BaseBBIENode(seqKey, null, bbie, bdtPriRestri, bbiep, bccp, bdt);
            LazyBBIESCFetcher fetcher = new LazyBBIESCFetcher(bdt, bbie);
            new LazyBBIENode(bbieNode, fetcher, fetcher.getChildrenCount(), parent);
        }

        private BasicBusinessInformationEntityProperty createBBIEP(BasicCoreComponentProperty bccp) {
            BasicBusinessInformationEntityProperty bbiep = new BasicBusinessInformationEntityProperty();
            bbiep.setGuid(Utility.generateGUID());
            bbiep.setBasedBccp(bccp);
            bbiep.setDefinition(bccp.getDefinition());
            bbiep.afterLoaded();
            return bbiep;
        }

        private BasicBusinessInformationEntity createBBIE(BasicCoreComponent bcc,
                                                          AggregateBusinessInformationEntity abie,
                                                          BasicBusinessInformationEntityProperty bbiep,
                                                          BusinessDataTypePrimitiveRestriction bdtPriRestri,
                                                          long codeListId,
                                                          int seqKey) {
            BasicBusinessInformationEntity bbie = new BasicBusinessInformationEntity();
            bbie.setGuid(Utility.generateGUID());
            bbie.setBasedBccId(bcc.getBccId());
            bbie.setFromAbie(abie);
            bbie.setToBbiep(bbiep);
            bbie.setNillable(false);
            bbie.setCardinalityMax(bcc.getCardinalityMax());
            bbie.setCardinalityMin(bcc.getCardinalityMin());
            bbie.setBdtPriRestri(bdtPriRestri);
//            if (codeListId > 0) {
//                bbie.setCodeListId(codeListId);
//            }
            bbie.setSeqKey(seqKey);
            bbie.afterLoaded();
            return bbie;
        }

        private void appendASBIELazyNode(Node parent, AssociationCoreComponent ascc,
                                         AggregateBusinessInformationEntity abie, BusinessContext bizCtx, int seqKey) {

            long abieId = abie.getAbieId();
            AssociationBusinessInformationEntity asbie = null;
            AssociationBusinessInformationEntityProperty asbiep = null;
            AssociationCoreComponentProperty asccp = null;
            AggregateBusinessInformationEntity roleOfAbie = null;
            AggregateCoreComponent basedAcc = null;

            if (abieId > 0L) {
                long asccId = ascc.getAsccId();
                long ownerTopLevelAbieId = abie.getOwnerTopLevelAbieId();

                asbie = asbieRepository.findOneByBasedAsccIdAndFromAbieIdAndOwnerTopLevelAbieId(asccId, abieId, ownerTopLevelAbieId);
                if (asbie != null) {
                    asbiep = asbiepRepository.findOne(asbie.getToAsbiepId());
                    asccp = asccpRepository.findOne(asbiep.getBasedAsccpId());
                    roleOfAbie = abieRepository.findOne(asbiep.getRoleOfAbieId());
                    basedAcc = accRepository.findOne(roleOfAbie.getBasedAccId());
                }
            }

            if (asbie == null) {
                long toAsccpId = ascc.getToAsccpId();
                asccp = asccpRepository.findOneByAsccpIdAndRevisionNumAndState(toAsccpId, 0, Published);
                long roleOfAccId = asccp.getRoleOfAccId();
                basedAcc = accRepository.findOneByAccIdAndRevisionNumAndState(roleOfAccId, 0, Published);

                roleOfAbie = createABIE(basedAcc, bizCtx);
                asbiep = createASBIEP(asccp, roleOfAbie);
                asbie = createASBIE(abie, asbiep, ascc, seqKey);
            }

            ASBIENode asbieNode = new BaseASBIENode(seqKey, null, asbie, asbiep, asccp, roleOfAbie);
            LazyBIEFetcher fetcher = new LazyBIEFetcher(basedAcc, roleOfAbie, bizCtx);
            new LazyASBIENode(asbieNode, fetcher, fetcher.getChildrenCount(), parent);
        }

        public AssociationBusinessInformationEntityProperty createASBIEP(AssociationCoreComponentProperty asccp,
                                                                         AggregateBusinessInformationEntity roleOfAbie) {
            AssociationBusinessInformationEntityProperty asbiep = new AssociationBusinessInformationEntityProperty();
            asbiep.setGuid(Utility.generateGUID());
            asbiep.setBasedAsccp(asccp);
            asbiep.setRoleOfAbie(roleOfAbie);
            asbiep.setDefinition(asccp.getDefinition());
            asbiep.afterLoaded();
            return asbiep;
        }

        public AssociationBusinessInformationEntity createASBIE(AggregateBusinessInformationEntity fromAbie,
                                                                AssociationBusinessInformationEntityProperty asbiep,
                                                                AssociationCoreComponent ascc, int seqKey) {
            AssociationBusinessInformationEntity asbie = new AssociationBusinessInformationEntity();
            asbie.setGuid(Utility.generateGUID());
            asbie.setFromAbie(fromAbie);
            asbie.setToAsbiep(asbiep);
            asbie.setBasedAsccId(ascc.getAsccId());
            asbie.setCardinalityMax(ascc.getCardinalityMax());
            asbie.setCardinalityMin(ascc.getCardinalityMin());
            asbie.setDefinition(ascc.getDefinition());
            asbie.setSeqKey(seqKey);
            asbie.afterLoaded();
            return asbie;
        }
    }

    private List<CoreComponentRelation> queryNestedChildAssoc(AggregateCoreComponent acc) {
        List<CoreComponentRelation> coreComponents = queryChildAssoc(acc);
        return getAssocList(coreComponents);
    }

    private List<CoreComponentRelation> getAssocList(List<CoreComponentRelation> list) {
        Map<Integer, Boolean> hashCodes = new HashMap();

        for (int i = 0; i < list.size(); i++) {
            CoreComponentRelation srt = list.get(i);
            if (srt instanceof AssociationCoreComponent) {
                AssociationCoreComponent ascc = (AssociationCoreComponent) srt;
                long toAsccpId = ascc.getToAsccpId();
                AssociationCoreComponentProperty asccp =
                        asccpRepository.findOneByAsccpIdAndRevisionNumAndState(toAsccpId, 0, Published);
                long roleOfAccId = asccp.getRoleOfAccId();
                AggregateCoreComponent acc = accRepository.findOneByAccIdAndRevisionNum(roleOfAccId, 0);
                if (groupcheck(acc)) {
                    list = handleNestedGroup(acc, list, i, hashCodes);
                }
            }
        }
        return list;
    }

    private boolean groupcheck(AggregateCoreComponent acc) {
        OagisComponentType oagisComponentType = acc.getOagisComponentType();
        return (oagisComponentType == SemanticGroup || oagisComponentType == UserExtensionGroup) ? true : false;
    }

    private boolean ensureCircularReference(AggregateCoreComponent acc, List<CoreComponentRelation> coreComponents,
                                            int gPosition, Map<Integer, Boolean> hashCodes) {
        int hashCode = acc.hashCode() + coreComponents.hashCode() + gPosition;
        Boolean check = hashCodes.get(hashCode);
        if (check == null) {
            check = false;
            hashCodes.put(hashCode, true);

        }
        return check;
    }

    private List<CoreComponentRelation> handleNestedGroup(AggregateCoreComponent acc,
                                                          List<CoreComponentRelation> coreComponents, int gPosition,
                                                          Map<Integer, Boolean> hashCodes) {
        /*
         * TODO: FIX ME
         * As of Nov 15th, 2016, When the User Extension is in Editing state
         * Circular Reference Problem occurred in this code.
         */
        if (ensureCircularReference(acc, coreComponents, gPosition, hashCodes)) {
            coreComponents.remove(gPosition);
            return coreComponents;
        }

        List<CoreComponentRelation> bList = queryChildAssoc(acc);
        if (!bList.isEmpty()) {
            coreComponents.addAll(gPosition, bList);
            coreComponents.remove(gPosition + bList.size());
        }

        long accId = acc.getAccId();
        for (int i = 0; i < coreComponents.size(); i++) {
            CoreComponentRelation coreComponent = coreComponents.get(i);
            if (coreComponent instanceof AssociationCoreComponent) {
                AssociationCoreComponent ascc = (AssociationCoreComponent) coreComponent;
                long toAsccpId = ascc.getToAsccpId();
                AssociationCoreComponentProperty asccp =
                        asccpRepository.findOneByAsccpIdAndRevisionNumAndState(toAsccpId, 0, Published);
                long roleOfAccId = asccp.getRoleOfAccId();
                AggregateCoreComponent roleOfAcc = accRepository.findOneByAccIdAndRevisionNum(roleOfAccId, 0);
                if (groupcheck(roleOfAcc)) {
                    coreComponents = handleNestedGroup(roleOfAcc, coreComponents, i, hashCodes);
                }
            }
        }

        return coreComponents;
    }

    private List<CoreComponentRelation> queryChildAssoc(AggregateCoreComponent acc) {
        long accId = acc.getAccId();
        List<BasicCoreComponent> bcc_tmp_assoc = bccRepository.findByFromAccIdAndRevisionNumAndState(accId, 0, Published);
        List<AssociationCoreComponent> ascc_tmp_assoc = asccRepository.findByFromAccIdAndRevisionNumAndState(accId, 0, Published);

        List<CoreComponentRelation> coreComponents = gatheringBySeqKey(bcc_tmp_assoc, ascc_tmp_assoc);
        return coreComponents;
    }

    private List<CoreComponentRelation> gatheringBySeqKey(
            List<BasicCoreComponent> bccList, List<AssociationCoreComponent> asccList
    ) {
        int size = bccList.size() + asccList.size();
        List<CoreComponentRelation> tmp_assoc = new ArrayList(size);
        tmp_assoc.addAll(bccList);
        tmp_assoc.addAll(asccList);
        Collections.sort(tmp_assoc, (a, b) -> a.getSeqKey() - b.getSeqKey());

        List<CoreComponentRelation> coreComponents = new ArrayList(size);
        for (BasicCoreComponent basicCoreComponent : bccList) {
            if (BasicCoreComponentEntityType.Attribute == basicCoreComponent.getEntityType()) {
                coreComponents.add(basicCoreComponent);
            }
        }

        for (CoreComponentRelation coreComponent : tmp_assoc) {
            if (coreComponent instanceof BasicCoreComponent) {
                BasicCoreComponent basicCoreComponent = (BasicCoreComponent) coreComponent;
                if (BasicCoreComponentEntityType.Element == basicCoreComponent.getEntityType()) {
                    coreComponents.add(basicCoreComponent);
                }
            } else {
                AssociationCoreComponent associationCoreComponent = (AssociationCoreComponent) coreComponent;
                coreComponents.add(associationCoreComponent);
            }
        }

        return coreComponents;
    }


    private class LazyBBIESCFetcher implements Fetcher {

        private DataType bdt;
        private BasicBusinessInformationEntity bbie;

        public LazyBBIESCFetcher(DataType bdt, BasicBusinessInformationEntity bbie) {
            this.bdt = bdt;
            this.bbie = bbie;
        }

        public int getChildrenCount() {
            long bdtId = bdt.getDtId();
            return (int) dtScRepository.findByOwnerDtId(bdtId)
                    .stream()
                    .filter(dtSc -> dtSc.getCardinalityMax() != 0)
                    .count();
        }

        @Override
        public void fetch(Node parent) {
            long bdtId = bdt.getDtId();

            List<DataTypeSupplementaryComponent> dtScList =
                    dtScRepository.findByOwnerDtId(bdtId)
                            .stream()
                            .filter(dtSc -> dtSc.getCardinalityMax() != 0)
                            .collect(Collectors.toList());

            long bbieId = bbie.getBbieId();
            long ownerTopLevelAbieId = bbie.getOwnerTopLevelAbieId();

            List<BasicBusinessInformationEntitySupplementaryComponent> bbieScList = new ArrayList();
            for (DataTypeSupplementaryComponent dtSc : dtScList) {
                BasicBusinessInformationEntitySupplementaryComponent bbieSc = null;
                if (bbieId > 0L) {
                    long dtScId = dtSc.getDtScId();
                    bbieSc = bbieScRepository.findOneByBbieIdAndDtScIdAndOwnerTopLevelAbieId(bbieId, dtScId, ownerTopLevelAbieId);
                }

                if (bbieSc == null) {
                    bbieSc = new BasicBusinessInformationEntitySupplementaryComponent();
                    long bdtScId = dtSc.getDtScId();
                    bbieSc.setBbie(bbie);
                    bbieSc.setDtSc(dtSc);
                    bbieSc.setGuid(Utility.generateGUID());

                    BusinessDataTypeSupplementaryComponentPrimitiveRestriction bdtScPriRestri =
                            bdtScPriRestriRepository.findOneByBdtScIdAndDefault(bdtScId, true);
                    if (bdtScPriRestri != null) {
                        bbieSc.setDtScPriRestri(bdtScPriRestri);
                    }

//                    long codeListId = dataContainer.getCodeListIdOfBdtScPriRestriId(bdtScId);
//                    if (codeListId > 0) {
//                        bbieSc.setCodeListId(codeListId);
//                    }

                    bbieSc.setCardinalityMax(dtSc.getCardinalityMax());
                    bbieSc.setCardinalityMin(dtSc.getCardinalityMin());
                    bbieSc.setDefinition(dtSc.getDefinition());
                    bbieSc.afterLoaded();
                }

                bbieScList.add(bbieSc);
            }

            for (BasicBusinessInformationEntitySupplementaryComponent bbieSc : bbieScList) {
                long dtScId = bbieSc.getDtScId();
                DataTypeSupplementaryComponent dtsc = dtScRepository.findOne(dtScId);
                BusinessDataTypeSupplementaryComponentPrimitiveRestriction bdtPriRestri =
                        bdtScPriRestriRepository.findOne(bbieSc.getDtScPriRestriId());

                new BaseBBIESCNode(parent, bbieSc, bdtPriRestri, dtsc);
            }
        }
    }


    public LazyBIENode createLazyBIENode(TopLevelAbie topLevelAbie) {
        AggregateBusinessInformationEntity abie = topLevelAbie.getAbie();
        BusinessContext bizCtx = businessContextRepository.findOne(abie.getBizCtxId());
        AssociationBusinessInformationEntityProperty asbiep = asbiepRepository.findOneByRoleOfAbieId(abie.getAbieId());
        AssociationCoreComponentProperty asccp = asccpRepository.findOne(asbiep.getBasedAsccpId());

        TopLevelNode topLevelNode = new BaseTopLevelNode(asbiep, asccp, abie, bizCtx);
        topLevelNode.setTopLevelAbie(topLevelAbie);

        long basedAccId = abie.getBasedAccId();
        AggregateCoreComponent acc = accRepository.findOne(basedAccId);
        LazyBIEFetcher fetcher = new LazyBIEFetcher(acc, abie, bizCtx);
        return new LazyTopLevelNode(topLevelNode, fetcher, fetcher.getChildrenCount());
    }

    private class BIEFetcher implements Fetcher {
        private AggregateBusinessInformationEntity abie;

        public BIEFetcher(AggregateBusinessInformationEntity abie) {
            this.abie = abie;
        }

        public int getChildrenCount() {
            long abieId = abie.getAbieId();
            int childrenCount = bbieRepository.countByFromAbieId(abieId) + asbieRepository.countByFromAbieId(abieId);
            return childrenCount;
        }

        @Override
        public void fetch(Node parent) {
            long abieId = abie.getAbieId();
            List<BasicBusinessInformationEntity> bbieList = bbieRepository.findByFromAbieId(abieId);
            List<AssociationBusinessInformationEntity> asbieList = asbieRepository.findByFromAbieId(abieId);

            Map<BusinessInformationEntity, Double> sequence = new HashMap();
            ValueComparator bvc = new ValueComparator(sequence);
            TreeMap<BusinessInformationEntity, Double> ordered_sequence = new TreeMap(bvc);

            for (BasicBusinessInformationEntity bbie : bbieList) {
                double sk = bbie.getSeqKey();
                if (getEntityType(bbie.getBasedBccId()) == Attribute)
                    appendBBIELazyNode(bbie, parent);
                else
                    sequence.put(bbie, sk);
            }

            for (AssociationBusinessInformationEntity asbie : asbieList) {
                double sk = asbie.getSeqKey();
                sequence.put(asbie, sk);
            }

            ordered_sequence.putAll(sequence);
            Set set = ordered_sequence.entrySet();
            Iterator i = set.iterator();
            while (i.hasNext()) {
                Map.Entry me = (Map.Entry) i.next();
                if (me.getKey() instanceof BasicBusinessInformationEntity)
                    appendBBIELazyNode((BasicBusinessInformationEntity) me.getKey(), parent);
                else
                    appendASBIELazyNode((AssociationBusinessInformationEntity) me.getKey(), parent);
            }
        }

        private BasicCoreComponentEntityType getEntityType(long bccId) {
            BasicCoreComponent basicCoreComponent = bccRepository.findOne(bccId);
            return basicCoreComponent.getEntityType();
        }

        private void appendBBIELazyNode(BasicBusinessInformationEntity bbie, Node parent) {
            BasicBusinessInformationEntityProperty bbiep = bbiepRepository.findOne(bbie.getToBbiepId());
            BasicCoreComponentProperty bccp = bccpRepository.findOne(bbiep.getBasedBccpId());

            long bdtId = bccp.getBdtId();
            DataType bdt = dataTypeRepository.findOne(bdtId);
            BusinessDataTypePrimitiveRestriction bdtPriRestri = bdtPriRestriRepository.findOne(bbie.getBdtPriRestriId());

            int seqKey = (int) bbie.getSeqKey();
            BBIENode bbieNode = new BaseBBIENode(seqKey, null, bbie, bdtPriRestri, bbiep, bccp, bdt);
            BBIESCFetcher fetcher = new BBIESCFetcher(bbie);
            new LazyBBIENode(bbieNode, fetcher, fetcher.getChildrenCount(), parent);
        }

        private void appendASBIELazyNode(AssociationBusinessInformationEntity asbie, Node parent) {
            AssociationBusinessInformationEntityProperty asbiep = asbiepRepository.findOne(asbie.getToAsbiepId());
            AssociationCoreComponentProperty asccp = asccpRepository.findOne(asbiep.getBasedAsccpId());
            AggregateBusinessInformationEntity roleOfAbie = abieRepository.findOne(asbiep.getRoleOfAbieId());

            int seqKey = (int) asbie.getSeqKey();
            ASBIENode asbieNode = new BaseASBIENode(seqKey, null, asbie, asbiep, asccp, roleOfAbie);
            BIEFetcher fetcher = new BIEFetcher(roleOfAbie);
            new LazyASBIENode(asbieNode, fetcher, fetcher.getChildrenCount(), parent);
        }
    }

    private class BBIESCFetcher implements Fetcher {

        private BasicBusinessInformationEntity bbie;

        public BBIESCFetcher(BasicBusinessInformationEntity bbie) {
            this.bbie = bbie;
        }

        public int getChildrenCount() {
            return bbieScRepository.countByBbieId(bbie.getBbieId());
        }

        @Override
        public void fetch(Node parent) {
            List<BasicBusinessInformationEntitySupplementaryComponent> bbiescList =
                    bbieScRepository.findByBbieId(bbie.getBbieId());
            for (BasicBusinessInformationEntitySupplementaryComponent bbiesc : bbiescList) {
                long dtScId = bbiesc.getDtScId();
                DataTypeSupplementaryComponent dtsc = dtScRepository.findOne(dtScId);
                BusinessDataTypeSupplementaryComponentPrimitiveRestriction bdtPriRestri =
                        bdtScPriRestriRepository.findOne(bbiesc.getDtScPriRestriId());

                new BaseBBIESCNode(parent, bbiesc, bdtPriRestri, dtsc);
            }
        }
    }

    private class DataContainerForCC implements CoreComponentProvider {

        private List<AggregateCoreComponent> accList;
        private List<AssociationCoreComponent> asccList;
        private List<AssociationCoreComponentProperty> asccpList;
        private List<BasicCoreComponent> bccList;
        private List<BasicCoreComponentProperty> bccpList;
        private List<DataType> dataTypes;
        private List<DataTypeSupplementaryComponent> dtScList;

        private Map<Long, AggregateCoreComponent> accMap;
        private Map<Long, AssociationCoreComponent> asccMap;
        private Map<Long, AssociationCoreComponentProperty> asccpMap;
        private Map<Long, BasicCoreComponent> bccMap;
        private Map<Long, BasicCoreComponentProperty> bccpMap;
        private Map<Long, DataType> bdtMap;
        private Map<Long, List<DataTypeSupplementaryComponent>> bdtScByOwnerDtIdMap;

        private Map<Long, List<BasicCoreComponent>> fromAccIdToBccMap;
        private Map<Long, List<BasicCoreComponent>> fromAccIdToBccWithoutAttributesMap;
        private Map<Long, List<AssociationCoreComponent>> fromAccIdToAsccMap;

        public DataContainerForCC() {
            accList = accRepository.findAllWithRevisionNum(0);
            asccList = asccRepository.findAllWithRevisionNum(0);
            asccpList = asccpRepository.findAllWithRevisionNum(0);
            bccList = bccRepository.findAllWithRevisionNum(0);
            bccpList = bccpRepository.findAllWithRevisionNum(0);
            dataTypes = dataTypeRepository.findAll();
            dtScList = dtScRepository.findAll();

            accMap = accList.stream()
                    .collect(Collectors.toMap(e -> e.getAccId(), Function.identity()));
            asccMap = asccList.stream()
                    .collect(Collectors.toMap(e -> e.getAsccId(), Function.identity()));
            asccpMap = asccpList.stream()
                    .collect(Collectors.toMap(e -> e.getAsccpId(), Function.identity()));
            bccMap = bccList.stream()
                    .collect(Collectors.toMap(e -> e.getBccId(), Function.identity()));
            bccpMap = bccpList.stream()
                    .collect(Collectors.toMap(e -> e.getBccpId(), Function.identity()));
            bdtMap = dataTypes.stream()
                    .collect(Collectors.toMap(e -> e.getDtId(), Function.identity()));
            bdtScByOwnerDtIdMap = dtScList.stream()
                    .collect(Collectors.groupingBy(e -> e.getOwnerDtId()));

            fromAccIdToBccMap = bccList.stream()
                    .collect(Collectors.groupingBy(e -> e.getFromAccId()));
            fromAccIdToBccWithoutAttributesMap = bccList.stream()
                    .filter(e -> e.getSeqKey() != 0)
                    .collect(Collectors.groupingBy(e -> e.getFromAccId()));
            fromAccIdToAsccMap = asccList.stream()
                    .collect(Collectors.groupingBy(e -> e.getFromAccId()));
        }

        public AggregateCoreComponent getACC(long accId) {
            return accMap.get(accId);
        }

        public AssociationCoreComponent getASCC(long asccId) {
            return asccMap.get(asccId);
        }

        public AssociationCoreComponentProperty getASCCP(long asccpId) {
            return asccpMap.get(asccpId);
        }

        public BasicCoreComponent getBCC(long bccId) {
            return bccMap.get(bccId);
        }

        public BasicCoreComponentProperty getBCCP(long bccpId) {
            return bccpMap.get(bccpId);
        }

        public DataType getBdt(long bdtId) {
            return bdtMap.get(bdtId);
        }

        public List<DataTypeSupplementaryComponent> getBdtScByBdtId(long bdtId) {
            List<DataTypeSupplementaryComponent> bdtScList = bdtScByOwnerDtIdMap.get(bdtId);
            return (bdtScList != null) ? bdtScList : Collections.emptyList();
        }

        @Override
        public List<BasicCoreComponent> getBCCs(long accId) {
            List<BasicCoreComponent> bccList = fromAccIdToBccMap.get(accId);
            return (bccList != null) ? bccList : Collections.emptyList();
        }

        @Override
        public List<BasicCoreComponent> getBCCsWithoutAttributes(long accId) {
            List<BasicCoreComponent> bccList = fromAccIdToBccWithoutAttributesMap.get(accId);
            return (bccList != null) ? bccList : Collections.emptyList();
        }

        @Override
        public List<AssociationCoreComponent> getASCCs(long accId) {
            List<AssociationCoreComponent> asccList = fromAccIdToAsccMap.get(accId);
            return (asccList != null) ? asccList : Collections.emptyList();
        }
    }
}
