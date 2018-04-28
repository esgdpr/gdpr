package ee.ut.gdpr.proto.domain;

import ee.ut.gdpr.proto.domain.enums.ProcessType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Entity
public class BPMNProcess implements Serializable {

    @Id
    private String id;
    private String name;
    @OneToMany(orphanRemoval = true)
    private List<Pool> pools;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="MODEL_ID")
    private Model model;
    private Integer ordr;
    @Enumerated(EnumType.STRING)
    private ProcessType type = ProcessType.DEFAULT;

    public BPMNProcess() {
        this.id = UUID.randomUUID().toString();
    }

    public BPMNProcess(String name, ProcessType type) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.type = type;
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

    public List<Pool> getPools() {
        return pools;
    }

    public void setPools(List<Pool> pools) {
        this.pools = pools;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public Integer getOrder() {
        return ordr;
    }

    public void setOrder(Integer order) {
        this.ordr = order;
    }

    public ProcessType getType() {
        return type;
    }

    public void setType(ProcessType type) {
        this.type = type;
    }
}
