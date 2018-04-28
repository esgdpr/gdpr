package ee.ut.gdpr.proto.domain;

import ee.ut.gdpr.proto.domain.enums.DataCategory;
import ee.ut.gdpr.proto.domain.enums.DataFlowDirection;
import ee.ut.gdpr.proto.domain.enums.ProcessingOperation;
import ee.ut.gdpr.proto.domain.enums.TaskType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
public class Task implements Serializable {

    @Id
    private String id;
    private String name;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "task", cascade=CascadeType.ALL)
    private List<TaskPersonalData> taskPersonalData;
    @Enumerated(EnumType.STRING)
    private TaskType type;
    @Enumerated(EnumType.STRING)
    private ProcessingOperation processingOperation;
    private boolean crossBorder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="LANE_ID")
    private Lane lane;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH, orphanRemoval = true)
    @JoinColumn(name="PURPOSE_ID")
    private Purpose purpose;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH, orphanRemoval = true)
    @JoinColumn(name="CONSENT_ID")
    private Consent consent;

    private boolean disclosure = false;

    public Task() {
    }

    public Task(String id, String name, TaskType taskType) {
        this.id = id;
        this.name = name;
        this.type = taskType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TaskPersonalData> getTaskPersonalData() {
        return taskPersonalData;
    }

    public TaskType getType() {
        return type;
    }

    public void setType(TaskType type) {
        this.type = type;
    }

    public void setTaskPersonalData(List<TaskPersonalData> taskPersonalData) {
        this.taskPersonalData = taskPersonalData;
    }

    public Lane getLane() {
        return lane;
    }

    public void setLane(Lane lane) {
        this.lane = lane;
    }

    public boolean hasCollectionRelation() {
        return taskPersonalData.stream().anyMatch(o -> o.getPersonalData() != null && o.getPersonalData().isFromDB());
    }

    public boolean hasDataCategory(DataCategory category) {
        return taskPersonalData.stream().anyMatch(o -> o.getPersonalData() != null && category.equals(o.getPersonalData().getCategory()));
    }

    public boolean carefulProcessing(){
        return taskPersonalData.stream().filter(o -> o.getPersonalData() != null && o.getPersonalData().getCategory() != DataCategory.OTHER).findFirst().isPresent();
    }

    public ProcessingOperation getProcessingOperation() {
        return processingOperation;
    }

    public void setProcessingOperation(ProcessingOperation processingOperation) {
        this.processingOperation = processingOperation;
    }

    public boolean isCrossBorder() {
        return crossBorder;
    }

    public void setCrossBorder(boolean crossBorder) {
        this.crossBorder = crossBorder;
    }

    public Purpose getPurpose() {
        return purpose;
    }

    public void setPurpose(Purpose purpose) {
        this.purpose = purpose;
    }

    public Consent getConsent() {
        return consent;
    }

    public void setConsent(Consent consent) {
        this.consent = consent;
    }

    public boolean isDisclosure() {
        return disclosure;
    }

    public void setDisclosure(boolean disclosure) {
        this.disclosure = disclosure;
    }

    public boolean dataGoesOut() {
        return taskPersonalData.stream().anyMatch(o -> o.getDirection() == DataFlowDirection.OUT && o.getPersonalData()
                != null && !o.getPersonalData().isFromDB());
    }
}
