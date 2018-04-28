package ee.ut.gdpr.proto.web.form;

public abstract class AbstractForm {
    public String processId;
    public String modelId;

    public AbstractForm() {
    }

    public AbstractForm(String processId, String modelId) {
        this.processId = processId;
        this.modelId = modelId;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }
}
