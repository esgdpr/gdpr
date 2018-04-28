package ee.ut.gdpr.proto.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
public class Pool implements Serializable {

    @Id
    private String id;
    private String name;

    @OneToMany(orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Lane> lanes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="BPMNPROCESS_ID")
    private BPMNProcess bpmnProcess;

    public Pool() {
    }

    public Pool(String id, String name, List<Lane> lanes, BPMNProcess bpmnProcess) {
        this.id = id;
        this.name = name;
        this.lanes = lanes;
        this.bpmnProcess = bpmnProcess;
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

    public List<Lane> getLanes() {
        return lanes;
    }

    public void setLanes(List<Lane> lanes) {
        this.lanes = lanes;
    }

    public BPMNProcess getBpmnProcess() {
        return bpmnProcess;
    }

    public void setBpmnProcess(BPMNProcess bpmnProcess) {
        this.bpmnProcess = bpmnProcess;
    }
}
