package org.oagi.srt.provider;

import org.oagi.srt.repository.entity.*;

import java.util.List;

public interface ImportedDataProvider {

    public AgencyIdList findAgencyIdList(int agencyIdListId);

    public List<CodeList> findCodeList();

    public CodeList findCodeList(int codeListId);

    public List<CodeListValue> findCodeListValueByCodeListId(int codeListId);

    public List<DataType> findDT();

    public DataType findDT(int dtId);

    public List<DataTypeSupplementaryComponent> findDtScByOwnerDtId(int ownerDtId);

    public List<BusinessDataTypePrimitiveRestriction> findBdtPriRestriListByDtId(int dtId);

    public List<BusinessDataTypeSupplementaryComponentPrimitiveRestriction> findBdtScPriRestriListByDtScId(int dtScId);

    public CoreDataTypeSupplementaryComponentAllowedPrimitiveExpressionTypeMap findCdtScAwdPriXpsTypeMap(int cdtScAwdPriXpsTypeMapId);

    public XSDBuiltInType findXbt(int xbtId);

    public List<AggregateCoreComponent> findACC();

    public AggregateCoreComponent findACC(int accId);

    public List<AssociationCoreComponentProperty> findASCCP();

    public AssociationCoreComponentProperty findASCCP(int asccpId);

    public AssociationCoreComponentProperty findASCCPByGuid(String guid);

    public List<BasicCoreComponentProperty> findBCCP();

    public BasicCoreComponentProperty findBCCP(int bccpId);

    public List<BasicCoreComponent> findBCCByToBccpIdAndEntityTypeIs1(int toBccpId);

    public List<BasicCoreComponent> findBCCByFromAccId(int fromAccId);

    public List<AssociationCoreComponent> findASCCByFromAccId(int fromAccId);
}