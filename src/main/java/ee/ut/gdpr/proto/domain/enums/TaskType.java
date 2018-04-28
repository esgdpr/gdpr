package ee.ut.gdpr.proto.domain.enums;

public enum TaskType {
    LOGGING("Record"),
    CONSENT("Consent"),
    PROCESSING("Processing"),
    OTHER("OTHER");

    private String value;

    TaskType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
