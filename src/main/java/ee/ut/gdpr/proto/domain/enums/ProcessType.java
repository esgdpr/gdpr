package ee.ut.gdpr.proto.domain.enums;

public enum ProcessType {
    DEFAULT("Personal Data Processing", ""),
    RIGHT_TO_RECTIFY("Personal Data Rectification Process (Right to Rectify)", "Rectify"),
    RIGHT_TO_ERASE("Personal Data Erasure Process (Right to Erase [to be forgotten])", "Erase"),
    RIGHT_TO_ACCESS("Process for data subject to access information about Personal Data Processing (Right to Access)", "Access"),
    RIGHT_TO_RESTRICT_PROCESSING("Process to restrict Personal Data Processing (Right to Restrict Processing)", "Restrict_processing"),
    RIGHT_OF_NOTIFICATION("Process to notify Data Subject (Right of Notification)", "Notification"),
    RIGHT_TO_PORTABILITY("Process to export Personal Data (Right to Portability)", "Portability");

    String value;
    String type;
    ProcessType(String value, String type) {
        this.value = value;
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public String getType() {
        return type;
    }
}
