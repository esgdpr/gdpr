package ee.ut.gdpr.proto.domain;


import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
public class Model implements Serializable {

    @Id
    private String id;

    private String name;

    @OneToMany(cascade = CascadeType.REMOVE)
    private List<BPMNProcess> bpmnProcesses;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "model", cascade = CascadeType.ALL)
    private List<ModelAuthority> authorities;

    public Model() {
    }

    public Model(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<BPMNProcess> getBpmnProcesses() {
        return bpmnProcesses;
    }

    public void setBpmnProcesses(List<BPMNProcess> bpmnProcesses) {
        this.bpmnProcesses = bpmnProcesses;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ModelAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<ModelAuthority> authorities) {
        this.authorities = authorities;
    }
}
