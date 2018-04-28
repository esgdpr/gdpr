package ee.ut.gdpr.proto.web.dto;

import ee.ut.gdpr.proto.domain.enums.LaneType;

public class LaneDTO {

    public String name;
    public String id;
    public LaneType type;

    public LaneDTO() {
    }

    public LaneDTO(String name, String id, LaneType laneType) {
        this.name = name;
        this.id = id;
        this.type = laneType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LaneType getType() {
        return type;
    }

    public void setType(LaneType type) {
        this.type = type;
    }
}
