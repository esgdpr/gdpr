package ee.ut.gdpr.proto.domain;

import javax.persistence.*;

@Entity
public class Purpose {

    @Id
    private String id;
    private String name;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH, orphanRemoval = true)
    @JoinColumn(name="TASK_ID")
    private Task task;
    public boolean legitimate_interest;
    public boolean do_not_require_identification;
    public boolean public_interest;
    public boolean legal_obligation;
    public boolean vital_interest;
    public boolean contract_processing;

    public Purpose() {
    }

    public Purpose(String id, String name) {
        this.id = id;
        this.name = name;
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

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public boolean isLegitimate_interest() {
        return legitimate_interest;
    }

    public void setLegitimate_interest(boolean legitimate_interest) {
        this.legitimate_interest = legitimate_interest;
    }

    public boolean isDo_not_require_identification() {
        return do_not_require_identification;
    }

    public void setDo_not_require_identification(boolean do_not_require_identification) {
        this.do_not_require_identification = do_not_require_identification;
    }

    public boolean isPublic_interest() {
        return public_interest;
    }

    public void setPublic_interest(boolean public_interest) {
        this.public_interest = public_interest;
    }

    public boolean isLegal_obligation() {
        return legal_obligation;
    }

    public void setLegal_obligation(boolean legal_obligation) {
        this.legal_obligation = legal_obligation;
    }

    public boolean isVital_interest() {
        return vital_interest;
    }

    public void setVital_interest(boolean vital_interest) {
        this.vital_interest = vital_interest;
    }

    public boolean isContract_processing() {
        return contract_processing;
    }

    public void setContract_processing(boolean contract_processing) {
        this.contract_processing = contract_processing;
    }
}
