package ee.ut.gdpr.proto.domain.enums;

public enum DataCategory {
    BIOMETRIC("Biometric data"),
    GENETIC("Genetic data"),
    HEALTH("Data concerning health"),
    ETHNIC_RACIAL("Ethnic/Racial Origin"),
    POLITICAL("Political "),
    RELIGIOUS("Political "),
    SEXUAL_ORIENTATION("Sexual orientation"),
    CRIMINAL("Criminal convictions and offences"),
    OTHER("Other");

    public String value;

    DataCategory(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
