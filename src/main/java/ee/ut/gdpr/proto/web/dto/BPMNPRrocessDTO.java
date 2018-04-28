package ee.ut.gdpr.proto.web.dto;

import ee.ut.gdpr.proto.domain.enums.ProcessType;

public class BPMNPRrocessDTO {

    public String id;
    public String name;
    public Integer order;
    public ProcessType type;

    public BPMNPRrocessDTO(String id, String name, Integer order, ProcessType type) {
        this.id = id;
        this.name = name;
        this.order = order;
        this.type = type;
    }

    public BPMNPRrocessDTO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public ProcessType getType() {
        return type;
    }

    public void setType(ProcessType type) {
        this.type = type;
    }
}
