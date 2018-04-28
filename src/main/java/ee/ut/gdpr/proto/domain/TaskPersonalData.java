package ee.ut.gdpr.proto.domain;

import ee.ut.gdpr.proto.domain.enums.DataFlowDirection;

import javax.persistence.*;
import java.io.Serializable;

@AssociationOverrides({
        @AssociationOverride(name = "task",
                joinColumns = @JoinColumn(name = "TASK_ID")),
        @AssociationOverride(name = "personalData",
                joinColumns = @JoinColumn(name = "PERSONAL_DATA_ID")) })
@Entity
public class TaskPersonalData implements Serializable {

    @Id
    private String id;
    @ManyToOne
    private Task task;
    @ManyToOne
    private PersonalData personalData;
    @Enumerated(EnumType.STRING)
    private DataFlowDirection direction;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public PersonalData getPersonalData() {
        return personalData;
    }

    public void setPersonalData(PersonalData personalData) {
        this.personalData = personalData;
    }

    public DataFlowDirection getDirection() {
        return direction;
    }

    public void setDirection(DataFlowDirection direction) {
        this.direction = direction;
    }
}
