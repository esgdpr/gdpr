package ee.ut.gdpr.proto.web.form;

import ee.ut.gdpr.proto.web.dto.TaskStep1DTO;

import java.util.List;

public class Step1Form extends AbstractForm {
    public List<TaskStep1DTO> tasks;

    public Step1Form() {
    }

    public List<TaskStep1DTO> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskStep1DTO> tasks) {
        this.tasks = tasks;
    }
}
