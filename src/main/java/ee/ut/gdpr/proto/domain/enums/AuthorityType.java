package ee.ut.gdpr.proto.domain.enums;

public enum AuthorityType {
    PRIVATE("Private"),
    PUBLIC("Public"),
    NATURAL_PERSON("Natural Person"),
    LEGAL_PERSON("Legal Person"),
    OFFICIAL("Official Authority");

    public String value;

    AuthorityType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
