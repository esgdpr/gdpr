package ee.ut.gdpr.proto.domain;

import ee.ut.gdpr.proto.domain.enums.FlowType;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class Flow implements Serializable {

    @Id
    private String id;
    private String f;
    private String t;
    @Enumerated(EnumType.STRING)
    private FlowType type;

    public Flow() {
    }

    public Flow(String id, String f, String t, FlowType type) {
        this.id = id;
        this.f = f;
        this.t = t;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getF() {
        return f;
    }

    public void setF(String f) {
        this.f = f;
    }

    public String getT() {
        return t;
    }

    public void setT(String t) {
        this.t = t;
    }

    public FlowType getType() {
        return type;
    }

    public void setType(FlowType type) {
        this.type = type;
    }
}
