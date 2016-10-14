package org.oagi.srt.repository.entity;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "asbiep")
@org.hibernate.annotations.Cache(region = "", usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class AssociationBusinessInformationEntityProperty implements Serializable, IdEntity, IGuidEntity {

    public static final String SEQUENCE_NAME = "ASBIEP_ID_SEQ";

    @Id
    @GeneratedValue(generator = SEQUENCE_NAME, strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 1)
    private long asbiepId;

    @Column(nullable = false, length = 41)
    private String guid;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "based_asccp_id", nullable = false)
    private AssociationCoreComponentProperty basedAsccp;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_of_abie_id", nullable = false)
    private AggregateBusinessInformationEntity roleOfAbie;

    @Lob
    @Column(length = 10 * 1024)
    private String definition;

    @Column(length = 225)
    private String remark;

    @Column(length = 225)
    private String bizTerm;

    @Column(nullable = false, updatable = false)
    private long createdBy;

    @Column(nullable = false)
    private long lastUpdatedBy;

    @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationTimestamp;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdateTimestamp;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_top_level_abie_id", nullable = false)
    private TopLevelAbie ownerTopLevelAbie;

    @PrePersist
    public void prePersist() {
        creationTimestamp = new Date();
        lastUpdateTimestamp = new Date();
    }

    @PreUpdate
    public void preUpdate() {
        lastUpdateTimestamp = new Date();
    }

    @Override
    public long getId() {
        return getAsbiepId();
    }

    @Override
    public void setId(long id) {
        setAsbiepId(id);
    }

    public long getAsbiepId() {
        return asbiepId;
    }

    public void setAsbiepId(long asbiepId) {
        this.asbiepId = asbiepId;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public AssociationCoreComponentProperty getBasedAsccp() {
        return basedAsccp;
    }

    public void setBasedAsccp(AssociationCoreComponentProperty basedAsccp) {
        this.basedAsccp = basedAsccp;
    }

    public AggregateBusinessInformationEntity getRoleOfAbie() {
        return roleOfAbie;
    }

    public void setRoleOfAbie(AggregateBusinessInformationEntity roleOfAbie) {
        this.roleOfAbie = roleOfAbie;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getBizTerm() {
        return bizTerm;
    }

    public void setBizTerm(String bizTerm) {
        this.bizTerm = bizTerm;
    }

    public long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(long createdBy) {
        this.createdBy = createdBy;
    }

    public long getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(long lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public Date getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(Date creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public Date getLastUpdateTimestamp() {
        return lastUpdateTimestamp;
    }

    public void setLastUpdateTimestamp(Date lastUpdateTimestamp) {
        this.lastUpdateTimestamp = lastUpdateTimestamp;
    }

    public TopLevelAbie getOwnerTopLevelAbie() {
        return ownerTopLevelAbie;
    }

    public void setOwnerTopLevelAbie(TopLevelAbie ownerTopLevelAbie) {
        this.ownerTopLevelAbie = ownerTopLevelAbie;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AssociationBusinessInformationEntityProperty that = (AssociationBusinessInformationEntityProperty) o;

        if (asbiepId != 0L && asbiepId == that.asbiepId) return true;
        if (guid != null) {
            if (guid.equals(that.guid)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = (int) (asbiepId ^ (asbiepId >>> 32));
        result = 31 * result + (guid != null ? guid.hashCode() : 0);
        result = 31 * result + (basedAsccp != null ? basedAsccp.hashCode() : 0);
        result = 31 * result + (roleOfAbie != null ? roleOfAbie.hashCode() : 0);
        result = 31 * result + (definition != null ? definition.hashCode() : 0);
        result = 31 * result + (remark != null ? remark.hashCode() : 0);
        result = 31 * result + (bizTerm != null ? bizTerm.hashCode() : 0);
        result = 31 * result + (int) (createdBy ^ (createdBy >>> 32));
        result = 31 * result + (int) (lastUpdatedBy ^ (lastUpdatedBy >>> 32));
        result = 31 * result + (creationTimestamp != null ? creationTimestamp.hashCode() : 0);
        result = 31 * result + (lastUpdateTimestamp != null ? lastUpdateTimestamp.hashCode() : 0);
        result = 31 * result + (ownerTopLevelAbie != null ? ownerTopLevelAbie.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AssociationBusinessInformationEntityProperty{" +
                "asbiepId=" + asbiepId +
                ", guid='" + guid + '\'' +
                ", basedAsccp=" + basedAsccp +
                ", roleOfAbie=" + roleOfAbie +
                ", definition='" + definition + '\'' +
                ", remark='" + remark + '\'' +
                ", bizTerm='" + bizTerm + '\'' +
                ", createdBy=" + createdBy +
                ", lastUpdatedBy=" + lastUpdatedBy +
                ", creationTimestamp=" + creationTimestamp +
                ", lastUpdateTimestamp=" + lastUpdateTimestamp +
                ", ownerTopLevelAbie=" + ownerTopLevelAbie +
                '}';
    }
}
