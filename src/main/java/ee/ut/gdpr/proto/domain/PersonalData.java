package ee.ut.gdpr.proto.domain;

import ee.ut.gdpr.proto.domain.enums.DataCategory;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
public class PersonalData implements Serializable {

    @Id
    private String id;
    private String label;
    private String collectionLabel;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "personalData")
    private List<TaskPersonalData> taskPersonalData;
    private boolean fromDB = false;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="DATA_SUBJECT_ID")
    private DataSubject dataSubject;
    @Enumerated(EnumType.STRING)
    private DataCategory category;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getCollectionLabel() {
        return collectionLabel;
    }

    public void setCollectionLabel(String collectionLabel) {
        this.collectionLabel = collectionLabel;
    }

    public List<TaskPersonalData> getTaskPersonalData() {
        return taskPersonalData;
    }

    public void setTaskPersonalData(List<TaskPersonalData> taskPersonalData) {
        this.taskPersonalData = taskPersonalData;
    }

    public String getFullLabel() {
        if (!StringUtils.isEmpty(collectionLabel)) {
            return String.join(".", collectionLabel, label);
        }
        return label;
    }

    public boolean isFromDB() {
        return fromDB;
    }

    public void setFromDB(boolean fromDB) {
        this.fromDB = fromDB;
    }

    public DataSubject getDataSubject() {
        return dataSubject;
    }

    public void setDataSubject(DataSubject dataSubject) {
        this.dataSubject = dataSubject;
    }

    public DataCategory getCategory() {
        return category;
    }

    public void setCategory(DataCategory category) {
        this.category = category;
    }
}
