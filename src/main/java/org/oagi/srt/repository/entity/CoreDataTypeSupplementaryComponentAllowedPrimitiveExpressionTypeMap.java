package org.oagi.srt.repository.entity;

import java.io.Serializable;

public class CoreDataTypeSupplementaryComponentAllowedPrimitiveExpressionTypeMap implements Serializable {

    private int cdtScAwdPriXpsTypeMapId;
    private int cdtScAwdPri;
    private int xbtId;

    public int getCdtScAwdPriXpsTypeMapId() {
        return cdtScAwdPriXpsTypeMapId;
    }

    public void setCdtScAwdPriXpsTypeMapId(int cdtScAwdPriXpsTypeMapId) {
        this.cdtScAwdPriXpsTypeMapId = cdtScAwdPriXpsTypeMapId;
    }

    public int getCdtScAwdPri() {
        return cdtScAwdPri;
    }

    public void setCdtScAwdPri(int cdtScAwdPri) {
        this.cdtScAwdPri = cdtScAwdPri;
    }

    public int getXbtId() {
        return xbtId;
    }

    public void setXbtId(int xbtId) {
        this.xbtId = xbtId;
    }

    @Override
    public String toString() {
        return "CoreDataTypeSupplementaryComponentAllowedPrimitiveExpressionTypeMap{" +
                "cdtScAwdPriXpsTypeMapId=" + cdtScAwdPriXpsTypeMapId +
                ", cdtScAwdPri=" + cdtScAwdPri +
                ", xbtId=" + xbtId +
                '}';
    }
}