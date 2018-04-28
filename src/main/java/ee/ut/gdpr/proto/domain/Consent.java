package ee.ut.gdpr.proto.domain;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Consent implements Serializable {

    @Id
    private String id;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH, orphanRemoval = true)
    @JoinColumn(name="TASK_ID")
    private Task task;
    private boolean freely_given;
    private boolean specific;
    private boolean informed_of_withdrawal;
    private boolean unambiguous;
    private boolean given_with_affirmative_action;
    private boolean clearly_distinguishable;

    public Consent() {
    }

    public Consent(String id) {
        this.id = id;
    }

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

    public boolean isFreely_given() {
        return freely_given;
    }

    public void setFreely_given(boolean freely_given) {
        this.freely_given = freely_given;
    }

    public boolean isSpecific() {
        return specific;
    }

    public void setSpecific(boolean specific) {
        this.specific = specific;
    }

    public boolean isInformed_of_withdrawal() {
        return informed_of_withdrawal;
    }

    public void setInformed_of_withdrawal(boolean informed_of_withdrawal) {
        this.informed_of_withdrawal = informed_of_withdrawal;
    }

    public boolean isUnambiguous() {
        return unambiguous;
    }

    public void setUnambiguous(boolean unambiguous) {
        this.unambiguous = unambiguous;
    }

    public boolean isGiven_with_affirmative_action() {
        return given_with_affirmative_action;
    }

    public void setGiven_with_affirmative_action(boolean given_with_affirmative_action) {
        this.given_with_affirmative_action = given_with_affirmative_action;
    }

    public boolean isClearly_distinguishable() {
        return clearly_distinguishable;
    }

    public void setClearly_distinguishable(boolean clearly_distinguishable) {
        this.clearly_distinguishable = clearly_distinguishable;
    }
}
