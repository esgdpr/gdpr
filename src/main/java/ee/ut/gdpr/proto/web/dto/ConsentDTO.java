package ee.ut.gdpr.proto.web.dto;

import java.io.Serializable;

public class ConsentDTO implements Serializable {

    public String id;
    public String taskId;
    public String name;
    public boolean freely_given;
    public boolean specific;
    public boolean informed_of_withdrawal;
    public boolean unambiguous;
    public boolean given_with_affirmative_action;
    public boolean clearly_distinguishable;

    public ConsentDTO() {
    }

    public ConsentDTO(String taskId, String name) {
        this.taskId = taskId;
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

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
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
