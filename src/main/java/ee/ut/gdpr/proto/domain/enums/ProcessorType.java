package ee.ut.gdpr.proto.domain.enums;

public enum ProcessorType {
    PROCESSOR("Processor"),
    THIRD_PARTY("Third Party"),
    PROCESSOR_CONTROLLER("Controller AND Processor"),
    CONTROLLER("Controller");

    String value;
    ProcessorType(String processor) {
        value = processor;
    }

    public String getValue() {
        return value;
    }
}
