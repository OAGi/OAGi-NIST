package org.oagi.srt.repository.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "cdt_sc_awd_pri")
public class CoreDataTypeSupplementaryComponentAllowedPrimitive implements Serializable {

    public static final String SEQUENCE_NAME = "CDT_SC_AWD_PRI_ID_SEQ";

    @Id
    @GenericGenerator(
            name = SEQUENCE_NAME,
            strategy = "org.oagi.srt.repository.support.jpa.ByDialectIdentifierGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "sequence_name", value = SEQUENCE_NAME),
                    @org.hibernate.annotations.Parameter(name = "increment_size", value = "1"),
                    @org.hibernate.annotations.Parameter(name = "initial_value", value = "1")
            }
    )
    @GeneratedValue(generator = SEQUENCE_NAME, strategy = GenerationType.AUTO)
    private long cdtScAwdPriId;

    @Column(nullable = false)
    private long cdtScId;

    @Column(nullable = false)
    private long cdtPriId;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault;

    public long getCdtScAwdPriId() {
        return cdtScAwdPriId;
    }

    public void setCdtScAwdPriId(long cdtScAwdPriId) {
        this.cdtScAwdPriId = cdtScAwdPriId;
    }

    public long getCdtScId() {
        return cdtScId;
    }

    public void setCdtScId(long cdtScId) {
        this.cdtScId = cdtScId;
    }

    public long getCdtPriId() {
        return cdtPriId;
    }

    public void setCdtPriId(long cdtPriId) {
        this.cdtPriId = cdtPriId;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CoreDataTypeSupplementaryComponentAllowedPrimitive that = (CoreDataTypeSupplementaryComponentAllowedPrimitive) o;

        if (cdtScAwdPriId != 0L && cdtScAwdPriId == that.cdtScAwdPriId) return true;
        return false;
    }

    @Override
    public int hashCode() {
        int result = (int) (cdtScAwdPriId ^ (cdtScAwdPriId >>> 32));
        result = 31 * result + (int) (cdtScId ^ (cdtScId >>> 32));
        result = 31 * result + (int) (cdtPriId ^ (cdtPriId >>> 32));
        result = 31 * result + (isDefault ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CoreDataTypeSupplementaryComponentAllowedPrimitive{" +
                "cdtScAwdPriId=" + cdtScAwdPriId +
                ", cdtScId=" + cdtScId +
                ", cdtPriId=" + cdtPriId +
                ", isDefault=" + isDefault +
                '}';
    }
}
