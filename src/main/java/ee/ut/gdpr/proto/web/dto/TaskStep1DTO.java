package ee.ut.gdpr.proto.web.dto;

import ee.ut.gdpr.proto.domain.enums.ProcessingOperation;

public class TaskStep1DTO extends TaskMainDTO {

    public ProcessingOperation operation;
    public PurposeDTO purpose;
    public boolean crossborder;

    public TaskStep1DTO() {
    }

    public TaskStep1DTO(String id, String name) {
        super(id, name);
    }

    public ProcessingOperation getOperation() {
        return operation;
    }

    public void setOperation(ProcessingOperation operation) {
        this.operation = operation;
    }

    public PurposeDTO getPurpose() {
        return purpose;
    }

    public void setPurpose(PurposeDTO purpose) {
        this.purpose = purpose;
    }

    public boolean isCrossborder() {
        return crossborder;
    }

    public void setCrossborder(boolean crossborder) {
        this.crossborder = crossborder;
    }
}
