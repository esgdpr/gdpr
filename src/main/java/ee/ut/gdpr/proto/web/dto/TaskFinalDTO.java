package ee.ut.gdpr.proto.web.dto;

import ee.ut.gdpr.proto.domain.enums.ValidationError;

import java.util.List;

public class TaskFinalDTO extends TaskMainDTO {

    public List<PersonalDataDTO> pd;
    public List<ValidationError> errors;

    public TaskFinalDTO() {
    }

    public TaskFinalDTO(String id, String name) {
        super(id, name);
    }

    public List<PersonalDataDTO> getPd() {
        return pd;
    }

    public void setPd(List<PersonalDataDTO> pd) {
        this.pd = pd;
    }

    public List<ValidationError> getErrors() {
        return errors;
    }

    public void setErrors(List<ValidationError> errors) {
        this.errors = errors;
    }
}
