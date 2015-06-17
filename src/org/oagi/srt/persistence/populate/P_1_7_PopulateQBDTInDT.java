package org.oagi.srt.persistence.populate;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.chanchan.common.persistence.db.DBAgent;
import org.oagi.srt.common.QueryCondition;
import org.oagi.srt.common.SRTConstants;
import org.oagi.srt.common.SRTObject;
import org.oagi.srt.common.util.BODSchemaHandler;
import org.oagi.srt.common.util.Utility;
import org.oagi.srt.common.util.XPathHandler;
import org.oagi.srt.persistence.dao.DAOFactory;
import org.oagi.srt.persistence.dao.SRTDAO;
import org.oagi.srt.persistence.dao.SRTDAOException;
import org.oagi.srt.persistence.dto.AgencyIDListVO;
import org.oagi.srt.persistence.dto.BCCPVO;
import org.oagi.srt.persistence.dto.BDTPrimitiveRestrictionVO;
import org.oagi.srt.persistence.dto.BDTSCPrimitiveRestrictionVO;
import org.oagi.srt.persistence.dto.CDTAllowedPrimitiveVO;
import org.oagi.srt.persistence.dto.CDTAllowedPrimitiveExpressionTypeMapVO;
import org.oagi.srt.persistence.dto.CDTPrimitiveVO;
import org.oagi.srt.persistence.dto.CDTSCAllowedPrimitiveExpressionTypeMapVO;
import org.oagi.srt.persistence.dto.CDTSCAllowedPrimitiveVO;
import org.oagi.srt.persistence.dto.CodeListVO;
import org.oagi.srt.persistence.dto.DTVO;
import org.oagi.srt.persistence.dto.DTSCVO;
import org.oagi.srt.persistence.dto.XSDBuiltInTypeVO;
import org.oagi.srt.web.startup.SRTInitializerException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
*
* @author Yunsu Lee
* @version 1.0
*
*/

public class P_1_7_PopulateQBDTInDT {
	
	private XPathHandler fields_xsd;
	private XPathHandler meta_xsd;
	
	private static Connection conn = null;
	
	DAOFactory df;
	SRTDAO aDTDAO;
	SRTDAO bccpDAO;
	SRTDAO aBDTPrimitiveRestrictionDAO;
	SRTDAO aCDTAllowedPrimitiveExpressionTypeMapDAO;
	SRTDAO aCDTAllowedPrimitiveDAO;
	SRTDAO aCDTSCAllowedPrimitiveDAO;
	SRTDAO aCodeListDAO;
	SRTDAO aDTSCDAO;
	SRTDAO aXSDBuiltInTypeDAO;
	SRTDAO cdtSCAPMapDAO;
	SRTDAO aCDTPrimitiveDAO;
	SRTDAO bdtSCPRDAO;
	
	public P_1_7_PopulateQBDTInDT() throws Exception {
		df = DAOFactory.getDAOFactory();
		aDTDAO = df.getDAO("DT");
		bccpDAO = df.getDAO("BCCP");
		bdtSCPRDAO = df.getDAO("BDTSCPrimitiveRestriction");
		aBDTPrimitiveRestrictionDAO = df.getDAO("BDTPrimitiveRestriction");
		aCDTAllowedPrimitiveExpressionTypeMapDAO = df.getDAO("CDTAllowedPrimitiveExpressionTypeMap");
		aCDTAllowedPrimitiveDAO = df.getDAO("CDTAllowedPrimitive");
		aCodeListDAO = df.getDAO("CodeList");
		aDTSCDAO = df.getDAO("DTSC");
		aCDTSCAllowedPrimitiveDAO = df.getDAO("CDTSCAllowedPrimitive");
		aXSDBuiltInTypeDAO = df.getDAO("XSDBuiltInType");
		cdtSCAPMapDAO = df.getDAO("CDTSCAllowedPrimitiveExpressionTypeMap");
		aCDTPrimitiveDAO = df.getDAO("CDTPrimitive");
		
		fields_xsd = new XPathHandler(SRTConstants.FILEDS_XSD_FILE_PATH);
		meta_xsd = new XPathHandler(SRTConstants.META_XSD_FILE_PATH);
	}
	
	private void populate() throws XPathExpressionException, SRTDAOException {
		NodeList elementsFromFieldsXSD = fields_xsd.getNodeList("/xsd:schema/xsd:element");
		NodeList elementsFromMetaXSD = meta_xsd.getNodeList("/xsd:schema/xsd:element");
		insertDTAndBCCP(elementsFromFieldsXSD, fields_xsd);
		insertDTAndBCCP(elementsFromMetaXSD, meta_xsd); // found that no QBDT from Meta.xsd, maybe because already imported in additional BDT
//		insertCDTSCAllowedPrimitive(elementsFromFieldsXSD, fields_xsd);
//		insertCDTSCAllowedPrimitive(elementsFromMetaXSD, meta_xsd);
//		insertCDTSCAllowedPrimitiveExpressionTypeMap(elementsFromFieldsXSD, fields_xsd);
//		insertCDTSCAllowedPrimitiveExpressionTypeMap(elementsFromMetaXSD, meta_xsd);
		
		//System.out.println("### elementsFromFieldsXSD.getLength() " + elementsFromFieldsXSD.getLength());
	}
	
	
	private void insertCDTSCAllowedPrimitive(NodeList elementsFromXSD, XPathHandler xHandler) throws XPathExpressionException, SRTDAOException {

		DAOFactory df = DAOFactory.getDAOFactory();
		SRTDAO dao = df.getDAO("CDTSCAllowedPrimitive");
		CDTSCAllowedPrimitiveVO cdt_sc_allowedVO = new CDTSCAllowedPrimitiveVO();

		for (int j = 0; j < elementsFromXSD.getLength(); j++) {
			Element ref_tmp = (Element) elementsFromXSD.item(j);
			NodeList result = xHandler.getNodeList("//xsd:complexType[@id = '"+ ref_tmp.getAttribute("id") + "']//xsd:attribute");
			for (int i = 0; i < result.getLength(); i++) {
				Element tmp = (Element) result.item(i);
				cdt_sc_allowedVO.setCDTSCID(getDTSCVO(tmp.getAttribute("id")).getDTSCID());
				ArrayList<SRTObject> cdtallowedprimitivelist = new ArrayList<SRTObject>();
				cdtallowedprimitivelist = getCDTAllowedPrimitiveIDs(getDTID(getRepresentationTerm(tmp.getAttribute("id"))));
				for(SRTObject dvo : cdtallowedprimitivelist){
					CDTSCAllowedPrimitiveVO svo = (CDTSCAllowedPrimitiveVO) dvo;
					cdt_sc_allowedVO.setCDTPrimitiveID(svo.getCDTPrimitiveID());
					cdt_sc_allowedVO.setisDefault(svo.getisDefault());
					dao.insertObject(cdt_sc_allowedVO);
				}
			}
		}
		//System.out.println("###END#####");
	}
	
	private List<SRTObject> getCdtSCAPMap(int cdtSCAllowedPrimitiveId) throws SRTDAOException {
		QueryCondition qc = new QueryCondition();
		qc.add("cdt_sc_allowed_primitive", cdtSCAllowedPrimitiveId);
		return cdtSCAPMapDAO.findObjects(qc, conn);
	}
	
	private void insertDTAndBCCP(NodeList elementsFromXSD, XPathHandler xHandler) throws XPathExpressionException, SRTDAOException {
		for(int i = 0; i < elementsFromXSD.getLength(); i++) {
			String bccp = ((Element)elementsFromXSD.item(i)).getAttribute("name");
			String guid = ((Element)elementsFromXSD.item(i)).getAttribute("id");
			String type = ((Element)elementsFromXSD.item(i)).getAttribute("type");
			String den = Utility.createDENFormat(type);

			Node simpleContent = xHandler.getNode("//xsd:complexType[@name = '" + type + "']/xsd:simpleContent");
			Node simpleType = xHandler.getNode("//xsd:simpleType[@name = '" + type + "']");
			if(simpleContent != null || simpleType != null) {
			
				//System.out.println("#### type " + type);
				Node documentationFromXSD = xHandler.getNode("/xsd:schema/xsd:element[@name = '" + bccp + "']/xsd:annotation/xsd:documentation");
				String definition = "";
				if(documentationFromXSD != null) {
					definition = ((Element)documentationFromXSD).getTextContent();
				}
				
				Node typeNode = xHandler.getNode("//xsd:complexType[@name = '" + type + "']");
				if(typeNode == null) {
					typeNode = xHandler.getNode("//xsd:simpleType[@name = '" + type + "']");
				}
				
				QueryCondition qc = new QueryCondition();
				String typeGuid = ((Element)typeNode).getAttribute("id");
				qc.add("dt_guid", typeGuid);
				DTVO dtVO = (DTVO)aDTDAO.findObject(qc, conn);
				if(dtVO == null) {
					// add new QBDT
					DTVO dVO = addToDT(typeGuid, type, typeNode, xHandler);
					
					// add DT_SC
					addToDTSC(xHandler, type, dVO);

					// add BCCP
					addToBCCP(guid, bccp, dVO, definition);
				} else {
					// add BCCP
					addToBCCP(guid, bccp, dtVO, definition);
				}
			}
		}
	}
	
	private List<SRTObject> getBDTSCPrimitiveRestriction(DTSCVO dtscVO) throws SRTDAOException {
		QueryCondition qc_00 = new QueryCondition();
		qc_00.add("bdt_sc_id", dtscVO.getBasedDTSCID());
		List<SRTObject> bdtscs = bdtSCPRDAO.findObjects(qc_00, conn);
		if(bdtscs.size() < 1) {
			QueryCondition qc_01 = new QueryCondition();
			qc_01.add("DT_SC_ID", dtscVO.getBasedDTSCID());
			DTSCVO vo = (DTSCVO)aDTSCDAO.findObject(qc_01, conn);
			bdtscs = getBDTSCPrimitiveRestriction(vo);
		}
		return bdtscs;
	}
	
	private void insertBDTSCPrimitiveRestriction(DTSCVO dtscVO, int mode, String name, String type)  throws XPathExpressionException, SRTDAOException {
		List<SRTObject> cdtscallowedprimitivelist = new ArrayList<SRTObject>();
		// if (SC = inherit from the base BDT)
		if(mode == 1) {
			List<SRTObject> bdtscs = getBDTSCPrimitiveRestriction(dtscVO);
			for(SRTObject obj : bdtscs) {
				BDTSCPrimitiveRestrictionVO parent = (BDTSCPrimitiveRestrictionVO)obj;
				
				BDTSCPrimitiveRestrictionVO bdtSCPRVO = new BDTSCPrimitiveRestrictionVO();
				bdtSCPRVO.setBDTSCID(dtscVO.getDTSCID());
				bdtSCPRVO.setCDTSCAllowedPrimitiveExpressionTypeMapID(parent.getCDTSCAllowedPrimitiveExpressionTypeMapID());
				bdtSCPRVO.setCodeListID(parent.getCodeListID());
				bdtSCPRVO.setisDefault(parent.getisDefault());
				bdtSCPRVO.setAgencyIDListID(parent.getAgencyIDListID());
				bdtSCPRDAO.insertObject(bdtSCPRVO);
			}
		
		} else { // else if (new SC)
			BDTSCPrimitiveRestrictionVO bdtscprimitiverestionvo = new BDTSCPrimitiveRestrictionVO();
			bdtscprimitiverestionvo.setBDTSCID(dtscVO.getDTSCID());
			
			cdtscallowedprimitivelist = getCdtSCAllowedPrimitiveID(dtscVO.getDTSCID());
			for(SRTObject dvo : cdtscallowedprimitivelist) {
				CDTSCAllowedPrimitiveVO svo = (CDTSCAllowedPrimitiveVO) dvo;
				List<SRTObject> maps = getCdtSCAPMap(svo.getCDTSCAllowedPrimitiveID());
				for(SRTObject mapVO : maps) {
					CDTSCAllowedPrimitiveExpressionTypeMapVO vo = (CDTSCAllowedPrimitiveExpressionTypeMapVO)mapVO;
					bdtscprimitiverestionvo.setCDTSCAllowedPrimitiveExpressionTypeMapID(vo.getCTSCAllowedPrimitiveExpressionTypeMapID());
					
					if(type.equalsIgnoreCase("Number_B98233")) {
						if(svo.getCDTPrimitiveID() == getCDTPrimitiveID("Number") && vo.getXSDBuiltInTypeID() == getXSDBuiltInTypeID("xsd:integer"))
							bdtscprimitiverestionvo.setisDefault(true);
						else 
							bdtscprimitiverestionvo.setisDefault(false);
						
					} else if(type.equalsIgnoreCase("CodeType_1E7368") || type.equalsIgnoreCase("CodeContentType") || name.equalsIgnoreCase("countryCode")) {
						if(svo.getCDTPrimitiveID() == getCDTPrimitiveID("Token") && vo.getXSDBuiltInTypeID() == getXSDBuiltInTypeID("xsd:token"))
							bdtscprimitiverestionvo.setisDefault(true);
						else 
							bdtscprimitiverestionvo.setisDefault(false);
						
					} else if(type.equalsIgnoreCase("StringType")) {
						if(svo.getCDTPrimitiveID() == getCDTPrimitiveID("String") && vo.getXSDBuiltInTypeID() == getXSDBuiltInTypeID("xsd:string"))
							bdtscprimitiverestionvo.setisDefault(true);
						else 
							bdtscprimitiverestionvo.setisDefault(false);
						
					} else if(type.equalsIgnoreCase("NormalizedStringType")){
						if(svo.getCDTPrimitiveID() == getCDTPrimitiveID("String") && vo.getXSDBuiltInTypeID() == getXSDBuiltInTypeID("xsd:string"))
							bdtscprimitiverestionvo.setisDefault(true);
						else 
							bdtscprimitiverestionvo.setisDefault(false);
						
					} else if(name.equalsIgnoreCase("listID") || name.equalsIgnoreCase("listVersionID") || name.equalsIgnoreCase("unitCodeListVersionID")){
						if(svo.getCDTPrimitiveID() == getCDTPrimitiveID("NormalizedString") && vo.getXSDBuiltInTypeID() == getXSDBuiltInTypeID("xsd:normalizedString"))
							bdtscprimitiverestionvo.setisDefault(true);
						else 
							bdtscprimitiverestionvo.setisDefault(false);
						
					} else if(type.equalsIgnoreCase("DateTimeType")){
						if(svo.getCDTPrimitiveID() == getCDTPrimitiveID("TimePoint") && vo.getXSDBuiltInTypeID() == getXSDBuiltInTypeID("xsd:token"))
							bdtscprimitiverestionvo.setisDefault(true);
						else 
							bdtscprimitiverestionvo.setisDefault(false);
							
					}
					else if(type.equalsIgnoreCase("IndicatorType")){
						if(svo.getCDTPrimitiveID() == getCDTPrimitiveID("Boolean") && vo.getXSDBuiltInTypeID() == getXSDBuiltInTypeID("xsd:boolean"))
							bdtscprimitiverestionvo.setisDefault(true);
						else 
							bdtscprimitiverestionvo.setisDefault(false);
							
					}	
					else if(type.equalsIgnoreCase("ValueType_E7171E")){
						if(svo.getCDTPrimitiveID() == getCDTPrimitiveID("NormalizedString") && vo.getXSDBuiltInTypeID() == getXSDBuiltInTypeID("xsd:normalizedString"))
							bdtscprimitiverestionvo.setisDefault(true);
						else 
							bdtscprimitiverestionvo.setisDefault(false);
							
					} else if(name.equalsIgnoreCase("name")){
						if(svo.getCDTPrimitiveID() == getCDTPrimitiveID("NormalizedString") && vo.getXSDBuiltInTypeID() == getXSDBuiltInTypeID("xsd:normalizedString"))
							bdtscprimitiverestionvo.setisDefault(true);
						else 
							bdtscprimitiverestionvo.setisDefault(false);
						
					} else if(type.contains("CodeContentType")){
						if(svo.getCDTPrimitiveID() == getCDTPrimitiveID("Token") && vo.getXSDBuiltInTypeID() == getXSDBuiltInTypeID("xsd:token"))
							bdtscprimitiverestionvo.setisDefault(true);
						else 
							bdtscprimitiverestionvo.setisDefault(false);
						//bdtSCPRDAO.insertObject(bdtscprimitiverestionvo); 
						
//						// add code_list id for this case
//						bdtscprimitiverestionvo.setCodeListID(getCodeListID(type.substring(0, type.indexOf("CodeContentType"))));
//						bdtscprimitiverestionvo.setisDefault(false);
//						bdtSCPRDAO.insertObject(bdtscprimitiverestionvo);
//						continue;
						
					} else if(name.equalsIgnoreCase("listAgencyID")){
						if(svo.getCDTPrimitiveID() == getCDTPrimitiveID("Token") && vo.getXSDBuiltInTypeID() == getXSDBuiltInTypeID("xsd:token"))
							bdtscprimitiverestionvo.setisDefault(true);
						else 
							bdtscprimitiverestionvo.setisDefault(false);
						//bdtSCPRDAO.insertObject(bdtscprimitiverestionvo);
						
//						System.out.println("### Agency Id");
//						// add agency_id_list id for this case
//						bdtscprimitiverestionvo.setAgencyIDListID(getAgencyListID());
//						bdtscprimitiverestionvo.setisDefault(false);
//						bdtSCPRDAO.insertObject(bdtscprimitiverestionvo);
//						continue;
					}
					
					bdtSCPRDAO.insertObject(bdtscprimitiverestionvo);
					
				}
			}	
			if(type.contains("CodeContentType")){
				// add code_list id for this case
				bdtscprimitiverestionvo = new BDTSCPrimitiveRestrictionVO();
				bdtscprimitiverestionvo.setBDTSCID(dtscVO.getDTSCID());
				bdtscprimitiverestionvo.setCodeListID(getCodeListID(type.substring(0, type.indexOf("CodeContentType"))));
				bdtscprimitiverestionvo.setisDefault(false);
				bdtSCPRDAO.insertObject(bdtscprimitiverestionvo);
				
			} 
			if(name.equalsIgnoreCase("listAgencyID")){
				// add agency_id_list id for this case
				bdtscprimitiverestionvo = new BDTSCPrimitiveRestrictionVO();
				bdtscprimitiverestionvo.setBDTSCID(dtscVO.getDTSCID());
				bdtscprimitiverestionvo.setAgencyIDListID(getAgencyListID());
				bdtscprimitiverestionvo.setisDefault(false);
				bdtSCPRDAO.insertObject(bdtscprimitiverestionvo);
			}
		}		
	}
		
	private DTVO addToDT(String guid, String type, Node typeNode, XPathHandler xHandler) throws XPathExpressionException, SRTDAOException {
			DTVO dVO = new DTVO();
			
			DTVO dtVO = new DTVO();
			dtVO.setDTGUID(guid);
			dtVO.setDTType(1);
			dtVO.setVersionNumber("1.0");
			dtVO.setRevisionType(0);
			
			//System.out.println("### type: " + type);
			
			Element extension = (Element)((Element)typeNode).getElementsByTagName("xsd:extension").item(0);
			String base = extension.getAttribute("base");
			
			if(base.endsWith("CodeContentType")) {
				dVO = getDTVOWithDEN("Code_1DEB05. Type");
			} else {
				String den = Utility.createDENFormat(base);
				dVO = getDTVOWithDEN(den);
				
				// QBDT is based on another QBDT
				if(dVO == null) {
					Node newTypeNode = xHandler.getNode("//xsd:complexType[@name = '" + base + "']");
					if(newTypeNode == null) {
						newTypeNode = xHandler.getNode("//xsd:simpleType[@name = '" + base + "']");
					}
					Element newType = (Element)newTypeNode;
					String newGuid = newType.getAttribute("id");
					dVO = getDTVOWithGUID(newGuid);
					if(dVO == null)
						dVO = addToDT(newGuid, base, newTypeNode, xHandler);
				}
			}
			
			dtVO.setBasedDTID(dVO.getDTID());
			dtVO.setDataTypeTerm(dVO.getDataTypeTerm());
			
			String tmp = Utility.spaceSeparator(type);
			String qualifier = tmp.substring(0, tmp.indexOf(" "));
			dtVO.setQualifier(qualifier);
			
			String den = qualifier + "_ " + dVO.getDEN();
			dtVO.setDEN(den);
			dtVO.setContentComponentDEN(den.substring(0, den.indexOf(".")) + ". Content");
			dtVO.setDefinition(null);
			dtVO.setContentComponentDefinition(null);
			dtVO.setRevisionState(1);
			dtVO.setCreatedByUserId(1);
			dtVO.setLastUpdatedByUserId(1);
			dtVO.setRevisionDocumentation("");
			
			aDTDAO.insertObject(dtVO);
			
			QueryCondition qc1 = new QueryCondition();
			qc1.add("dt_guid", guid);
			
			DTVO res = (DTVO)aDTDAO.findObject(qc1, conn);
			// add to BDTPrimitiveRestriction
			insertBDTPrimitiveRestriction(res, base);
			
			return res;
	}
	
	private void insertBDTPrimitiveRestriction(DTVO dVO, String base) throws SRTDAOException {
		DAOFactory df = DAOFactory.getDAOFactory();
		SRTDAO aBDTPrimitiveRestrictionDAO = df.getDAO("BDTPrimitiveRestriction");
		
		QueryCondition qc = new QueryCondition();
		qc.add("bdt_id", dVO.getBasedDTID());
		ArrayList<SRTObject> al = aBDTPrimitiveRestrictionDAO.findObjects(qc, conn);
		
		if(!dVO.getDataTypeTerm().equalsIgnoreCase("Code") || (dVO.getDataTypeTerm().equalsIgnoreCase("Code") && base.equalsIgnoreCase("CodeType"))) {
			for(SRTObject aSRTObject : al) {
				BDTPrimitiveRestrictionVO aBDTPrimitiveRestrictionVO = (BDTPrimitiveRestrictionVO)aSRTObject;
				BDTPrimitiveRestrictionVO theBDT_Primitive_RestrictionVO = new BDTPrimitiveRestrictionVO();
				theBDT_Primitive_RestrictionVO.setBDTID(dVO.getDTID());
				theBDT_Primitive_RestrictionVO.setCDTPrimitiveExpressionTypeMapID(aBDTPrimitiveRestrictionVO.getCDTPrimitiveExpressionTypeMapID());
				theBDT_Primitive_RestrictionVO.setisDefault(aBDTPrimitiveRestrictionVO.getisDefault());
				
				aBDTPrimitiveRestrictionDAO.insertObject(theBDT_Primitive_RestrictionVO);
			}
		} else {
			BDTPrimitiveRestrictionVO theBDT_Primitive_RestrictionVO = new BDTPrimitiveRestrictionVO();
			theBDT_Primitive_RestrictionVO.setBDTID(dVO.getDTID());
			if(base.endsWith("CodeContentType")) {
				theBDT_Primitive_RestrictionVO.setCodeListID(getCodeListID(base.substring(0, base.indexOf("CodeContentType"))));
			} else {
				for(SRTObject aSRTObject : al) {
					BDTPrimitiveRestrictionVO aBDTPrimitiveRestrictionVO = (BDTPrimitiveRestrictionVO)aSRTObject;
					if(aBDTPrimitiveRestrictionVO.getCodeListID() > 0) {
						theBDT_Primitive_RestrictionVO.setCodeListID(aBDTPrimitiveRestrictionVO.getCodeListID());
						break;
					}
				}
			}
			theBDT_Primitive_RestrictionVO.setisDefault(true);
			aBDTPrimitiveRestrictionDAO.insertObject(theBDT_Primitive_RestrictionVO);
		}
	}
	
	private void addToBCCP(String guid, String bccp, DTVO dtVO, String definition) throws SRTDAOException {
		
		BCCPVO bccpVO = new BCCPVO();
		bccpVO.setBCCPGUID(guid);
		
		String propertyTerm = Utility.spaceSeparator(bccp.replaceAll("ID", "Identifier"));
		bccpVO.setPropertyTerm(propertyTerm);
		bccpVO.setRepresentationTerm(dtVO.getDataTypeTerm());
		bccpVO.setBDTID(dtVO.getDTID());
		bccpVO.setDEN(Utility.firstToUpperCase(propertyTerm) + ". " + dtVO.getDataTypeTerm());
		bccpVO.setDefinition(definition);
		bccpVO.setCreatedByUserId(1);
		bccpVO.setLastUpdatedByUserId(1);
		
		bccpDAO.insertObject(bccpVO);
		
	}
	
	private void addToDTSC(XPathHandler xHandler, String typeName, DTVO qbdtVO) throws SRTDAOException, XPathExpressionException {
		
		// inherit from the base BDT
		QueryCondition qc = new QueryCondition();
		qc.add("owner_dt_iD", qbdtVO.getBasedDTID());
		
		int owner_dT_iD = qbdtVO.getDTID();
		
		ArrayList<SRTObject> dtsc_vos = aDTSCDAO.findObjects(qc, conn);
		for(SRTObject sObj : dtsc_vos) {
			DTSCVO dtsc_vo = (DTSCVO)sObj;
			
			DTSCVO vo = new DTSCVO();
			vo.setDTSCGUID(dtsc_vo.getDTSCGUID());
			vo.setPropertyTerm(dtsc_vo.getPropertyTerm());
			vo.setRepresentationTerm(dtsc_vo.getRepresentationTerm());
			vo.setOwnerDTID(owner_dT_iD);
			
			vo.setMinCardinality(dtsc_vo.getMinCardinality());
			vo.setMaxCardinality(dtsc_vo.getMaxCardinality());
			vo.setBasedDTSCID(dtsc_vo.getDTSCID());
			
			aDTSCDAO.insertObject(vo);	
			
			insertBDTSCPrimitiveRestriction(getDTSCVO(dtsc_vo.getDTSCGUID(), owner_dT_iD), 1, "", "");
		}
		
		// new SC
		NodeList attributeList = xHandler.getNodeList("//xsd:complexType[@id = '" + qbdtVO.getDTGUID() + "']/xsd:simpleContent/xsd:extension/xsd:attribute");
		
		if(attributeList == null || attributeList.getLength() == 0) {
			//System.out.println("##### " + "//xsd:"+"Type[@name = '" + typeName + "']/xsd:simpleContent/xsd:extension/xsd:attribute");
		} else {
			String dt_sc_guid = "";
			String property_term = "";
			String representation_term = "";
			
			String definition;
			int min_cardinality = 0;
			int max_cardinality = 0;
			
			for(int i = 0; i < attributeList.getLength(); i++) {
				Node attribute = attributeList.item(i);
				Element attrElement = (Element)attribute;
				dt_sc_guid = attrElement.getAttribute("id");

				if(attrElement.getAttribute("use") == null || attrElement.getAttribute("use").equalsIgnoreCase("optional") || attrElement.getAttribute("use").equalsIgnoreCase("prohibited"))
					min_cardinality = 0;
				else if(attrElement.getAttribute("use").equalsIgnoreCase("required"))
					min_cardinality = 1;
				
				String attrName = attrElement.getAttribute("name");
				
				// Property Term
				if(attrName.endsWith("Code"))  
					property_term = attrName.substring(0, attrName.indexOf("Code"));
				else if(attrName.endsWith("ID")) 
					property_term = attrName.substring(0, attrName.indexOf("ID"));
				else if(attrName.endsWith("Value"))	
					property_term = attrName.substring(0, attrName.indexOf("Value"));
				else
					property_term = Utility.spaceSeparator(attrName);
				
				if(property_term.trim().length() == 0)
					property_term = attrName;
				
				property_term = Utility.firstToUpperCase(property_term);
				
				// Representation Term
				if(attrName.endsWith("Code") || attrName.endsWith("code")) {
					representation_term = "Code";
				} else if(attrName.endsWith("Number")) {
					representation_term = "Number";
				} else if(attrName.endsWith("ID")) {
					representation_term = "Identifier";
				} else if(attrName.endsWith("DateTime")) {
					representation_term = "Date Time";
				} else if(attrName.endsWith("Value")) {
					representation_term = "Value";
				} else if(attrName.endsWith("Name") || attrName.endsWith("name")) {
					representation_term = "Name";
				} else {
					String attrType = attrElement.getAttribute("type");
					if(attrType.equals("StringType") || attrType.equals("NormalizedStringType"))
						representation_term = "Text";
					else if(attrType.equals("IndicatorType"))
						representation_term = "Indicator";
				}
					
				Node documentationNode = xHandler.getNode("//xsd:complexType[@id = '" + qbdtVO.getDTGUID() + "']/xsd:simpleContent/xsd:extension/xsd:attribute/xsd:annotation/xsd:documentation");
				if(documentationNode != null) {
					definition = ((Element)documentationNode).getTextContent();
				} else {
					definition = null;
				}
					
				DTSCVO vo = new DTSCVO();
				vo.setDTSCGUID(dt_sc_guid);
				vo.setPropertyTerm(Utility.spaceSeparator(property_term));
				vo.setRepresentationTerm(representation_term);
				vo.setDefinition(definition);
				vo.setOwnerDTID(owner_dT_iD);
				
				vo.setMinCardinality(min_cardinality);
				vo.setMaxCardinality(max_cardinality);
				
//				Both based dtsc and target dtsc have listAgencyID
//				target dtsc inherits all attrs from the base
//				since the attr name is the same, it just update the guid
//				in this case, the target dtsc is new? or not?
						
				DTSCVO duplicate = checkDuplicate(vo);
				if(duplicate == null) {
					aDTSCDAO.insertObject(vo);	
					
					// populate CDT_SC_Allowed_Primitives
					QueryCondition qc_01 = new QueryCondition();
					qc_01.add("owner_dt_iD", vo.getOwnerDTID());
					qc_01.add("dt_sc_guid", vo.getDTSCGUID());
					
					DTSCVO dtscVO = (DTSCVO)aDTSCDAO.findObject(qc_01, conn);
					String representationTerm = dtscVO.getRepresentationTerm();
					DTVO dtVO = getDTVOWithRepresentationTerm(representationTerm);
					
					CDTSCAllowedPrimitiveVO cdtSCAllowedVO = new CDTSCAllowedPrimitiveVO();
					cdtSCAllowedVO.setCDTSCID(dtscVO.getDTSCID());
					ArrayList<SRTObject> cdtallowedprimitivelist = getCDTAllowedPrimitiveIDs(dtVO.getDTID());
					for(SRTObject dvo : cdtallowedprimitivelist){
						CDTAllowedPrimitiveVO svo = (CDTAllowedPrimitiveVO) dvo;
						cdtSCAllowedVO.setCDTPrimitiveID(svo.getCDTPrimitiveID());
						cdtSCAllowedVO.setisDefault(svo.getisDefault());
						aCDTSCAllowedPrimitiveDAO.insertObject(cdtSCAllowedVO);
						
						// populate CDT_SC_Allowed_Primitive_Expression_Type_Map
						QueryCondition qc_02 = new QueryCondition();
						qc_02.add("cdt_sc_id", cdtSCAllowedVO.getCDTSCID());
						qc_02.add("cdt_primitive_id", cdtSCAllowedVO.getCDTPrimitiveID());
						int cdtSCAllowedPrimitiveId = ((CDTSCAllowedPrimitiveVO)aCDTSCAllowedPrimitiveDAO.findObject(qc_02, conn)).getCDTSCAllowedPrimitiveID();
						
						List<String> xsdbs = Types.getCorrespondingXSDBuiltType(getPrimitiveName(cdtSCAllowedVO.getCDTPrimitiveID()));
						for(String xbt : xsdbs) {
							CDTSCAllowedPrimitiveExpressionTypeMapVO mapVO = new CDTSCAllowedPrimitiveExpressionTypeMapVO();
							mapVO.setCDTSCAllowedPrimitive(cdtSCAllowedPrimitiveId);
							QueryCondition qc_03 = new QueryCondition();
							qc_03.add("builtin_type", xbt);
							int xdtBuiltTypeId = ((XSDBuiltInTypeVO)aXSDBuiltInTypeDAO.findObject(qc_03, conn)).getXSDBuiltInTypeID();
							mapVO.setXSDBuiltInTypeID(xdtBuiltTypeId);
							cdtSCAPMapDAO.insertObject(mapVO);
						}
					}
					
					insertBDTSCPrimitiveRestriction(getDTSCVO(dt_sc_guid, owner_dT_iD), 0, attrElement.getAttribute("name"), attrElement.getAttribute("type"));
				} else {
					vo.setDTSCID(duplicate.getDTSCID());
					vo.setBasedDTSCID(duplicate.getBasedDTSCID());
					aDTSCDAO.updateObject(vo);
				}
			}
		}
	}
	
	private DTSCVO checkDuplicate(DTSCVO dtVO) throws SRTDAOException {
		QueryCondition qc = new QueryCondition();
		qc.add("representation_term", dtVO.getRepresentationTerm());
		qc.add("property_term", dtVO.getPropertyTerm());
		qc.add("owner_dt_id", dtVO.getOwnerDTID());
		return (DTSCVO)aDTSCDAO.findObject(qc, conn);
	}
	

	public int getCodeListID(String codeName) throws SRTDAOException{
    	QueryCondition qc = new QueryCondition();
		qc.addLikeClause("Name", "%" + codeName.trim() + "%");
		CodeListVO codelistVO = (CodeListVO)aCodeListDAO.findObject(qc, conn);
		return codelistVO.getCodeListID();
	}
	
	public int getAgencyListID() throws SRTDAOException{
		DAOFactory df = DAOFactory.getDAOFactory();
		SRTDAO dao = df.getDAO("AgencyIDList");
    	QueryCondition qc = new QueryCondition();
		qc.add("name", "Agency Identification");
		AgencyIDListVO agencyidlistVO = (AgencyIDListVO)dao.findObject(qc);
		return agencyidlistVO.getAgencyIDListID();
	}
	
	public DTSCVO getDTSCVO(String guid) throws SRTDAOException{
    	QueryCondition qc = new QueryCondition();
		qc.add("DT_SC_GUID", guid);
		return (DTSCVO)aDTSCDAO.findObject(qc);
	}
	
	public DTSCVO getDTSCVO(String guid, int ownerId) throws SRTDAOException{
    	QueryCondition qc = new QueryCondition();
		qc.add("DT_SC_GUID", guid);
		qc.add("owner_dt_id", ownerId);
		return (DTSCVO)aDTSCDAO.findObject(qc);
	}
	
	public int getDTID(String DataTypeTerm) throws SRTDAOException{
    	QueryCondition qc = new QueryCondition();
		qc.add("Data_Type_Term", new String(DataTypeTerm));
		qc.add("DT_Type", 0);
		DTVO dtVO = (DTVO)aDTDAO.findObject(qc);		
		int id = dtVO.getDTID();
		return id;
	}
	
	public int getCDTSCID(int DTSCID) throws SRTDAOException{
    	QueryCondition qc = new QueryCondition();
		qc.add("CDT_SC_ID", DTSCID);
		CDTSCAllowedPrimitiveVO cdtVO = (CDTSCAllowedPrimitiveVO)aCDTSCAllowedPrimitiveDAO.findObject(qc);
		int id = cdtVO.getCDTSCID();
		return id;
	}
	
	public String getRepresentationTerm(String DTSCGUID) throws SRTDAOException{
    	QueryCondition qc = new QueryCondition();
		qc.add("DT_SC_GUID", new String(DTSCGUID));
		DTSCVO dtscVO = (DTSCVO)aDTSCDAO.findObject(qc);
		String term = dtscVO.getRepresentationTerm();
		return term;
	}
	
	public String getPrimitiveName(int CDTPrimitiveID) throws SRTDAOException{
    	QueryCondition qc = new QueryCondition();
		qc.add("CDT_Primitive_ID", CDTPrimitiveID);
		return ((CDTPrimitiveVO)aCDTPrimitiveDAO.findObject(qc)).getName();
	}
	
	
	public int getCDTPrimitiveID(String name) throws SRTDAOException{
    	QueryCondition qc = new QueryCondition();
		qc.add("Name",  name);
		return ((CDTPrimitiveVO)aCDTPrimitiveDAO.findObject(qc)).getCDTPrimitiveID();
	}
	
	public ArrayList<SRTObject> getCDTAllowedPrimitiveIDs(int cdt_id) throws SRTDAOException{
    	QueryCondition qc = new QueryCondition();
		qc.add("CDT_ID", cdt_id);
		return aCDTAllowedPrimitiveDAO.findObjects(qc, conn);
	}
	
	public List<SRTObject> getCdtSCAllowedPrimitiveID(int dt_sc_id) throws SRTDAOException{
    	QueryCondition qc = new QueryCondition();
		qc.add("CDT_SC_ID", dt_sc_id);
		List<SRTObject> res = aCDTSCAllowedPrimitiveDAO.findObjects(qc, conn);
		if(res.size() < 1) {
			QueryCondition qc_01 = new QueryCondition();
			qc_01.add("DT_SC_ID", dt_sc_id);
			DTSCVO dtscVO = (DTSCVO)aDTSCDAO.findObject(qc_01);
			res = getCdtSCAllowedPrimitiveID(dtscVO.getBasedDTSCID());
		}
		return res;
	}
	
	public int getXSDBuiltInTypeID(String BuiltIntype) throws SRTDAOException{
    	QueryCondition qc = new QueryCondition();
		qc.add("BuiltIn_Type", new String(BuiltIntype));
		return ((XSDBuiltInTypeVO)aXSDBuiltInTypeDAO.findObject(qc)).getXSDBuiltInTypeID();
	}
	
	private DTVO getDTVOWithDEN(String den) throws SRTDAOException {
		QueryCondition qc = new QueryCondition();
		qc.add("den", den);
		qc.add("dt_type", 1);
		return (DTVO)aDTDAO.findObject(qc, conn);
	}
	
	private DTVO getDTVOWithRepresentationTerm(String representationTerm) throws SRTDAOException {
		QueryCondition qc = new QueryCondition();
		qc.add("data_type_term", representationTerm);
		qc.add("dt_type", 0);
		return (DTVO)aDTDAO.findObject(qc, conn);
	}
	
	private DTVO getDTVOWithGUID(String guid) throws SRTDAOException {
		QueryCondition qc = new QueryCondition();
		qc.add("dt_guid", guid);
		return (DTVO)aDTDAO.findObject(qc, conn);
	}
	
	public void run() throws Exception {
		System.out.println("### 1.7 Start");
		DBAgent tx = new DBAgent();
		conn = tx.open();
		
		populate();
		
		tx.close();
		conn.close();
		System.out.println("### 1.7 End");
	}
	
	public static void main(String[] args) throws Exception{
		Utility.dbSetup();
		
		P_1_7_PopulateQBDTInDT q = new P_1_7_PopulateQBDTInDT();
		q.run();
	}
}