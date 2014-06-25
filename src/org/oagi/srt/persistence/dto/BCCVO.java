package org.oagi.srt.persistence.dto;

import org.oagi.srt.common.SRTObject;

/**
*
* @version 1.0
* @author Jaehun Lee
*/

public class BCCVO extends SRTObject {

	private int BCCID;
	private int CardinalityMin;
	private int CardinalityMax;
	private int AssocToBCCPID;
	private int AssocFromACCID;
	private int Sequencingkey;
	private int EntityType;
	
	public int getBCCID() {
		return BCCID;
	}
	
	public void setBCCID(int bccID) {
		BCCID = bccID;
	}

	public int getCardinalityMin() {
		return CardinalityMin;
	}
	
	public void setCardinalityMin(int cardinalityMin) {
		CardinalityMin = cardinalityMin;
	}
	
	public int getCardinalityMax() {
		return CardinalityMax;
	}
	
	public void setCardinalityMax(int cardinalityMax) {
		CardinalityMax = cardinalityMax;
	}
	
	public int getAssocToBCCPID() {
		return AssocToBCCPID;
	}
	
	public void setAssocToBCCPID(int assocToBCCPID) {
		AssocToBCCPID = assocToBCCPID;
	}
	
	public int getAssocFromACCID() {
		return AssocFromACCID;
	}
	
	public void setAssocFromACCID(int assocFromACCID) {
		AssocFromACCID = assocFromACCID;
	}
	
	public int getSequencingkey() {
		return Sequencingkey;
	}
	
	public void setSequencingkey(int sequencingkey) {
		Sequencingkey = sequencingkey;
	}
	
	public int getEntityType() {
		return EntityType;
	}
	
	public void setEntityType(int entityType) {
		EntityType = entityType;
	}
}