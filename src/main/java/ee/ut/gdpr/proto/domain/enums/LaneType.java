package ee.ut.gdpr.proto.domain.enums;

public enum LaneType {
    OTHER ("Other"),
    FILING_SYSTEM ("Filing System"),
    DATA_SUBJECT ("Data Subject"),
    RECIPIENT ("Recipient");

    public String value;
    LaneType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
