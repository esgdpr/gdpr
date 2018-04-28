package ee.ut.gdpr.proto.web.form;

import ee.ut.gdpr.proto.web.dto.TaskFinalDTO;

import java.util.List;

public class FinalForm extends AbstractForm {

    public List<TaskFinalDTO> tasks;

    public FinalForm() {
    }

    public List<TaskFinalDTO> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskFinalDTO> tasks) {
        this.tasks = tasks;
    }
}
