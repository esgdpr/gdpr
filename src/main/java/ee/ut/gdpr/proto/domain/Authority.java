package ee.ut.gdpr.proto.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
public class Authority {

    @Id
    private String id;
    private String name;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "authority")
    private List<ModelAuthority> modelAuthorities;

    public Authority() {
    }

    public Authority(String name) {
        this.name = name;
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

    public List<ModelAuthority> getModelAuthorities() {
        return modelAuthorities;
    }

    public void setModelAuthorities(List<ModelAuthority> modelAuthorities) {
        this.modelAuthorities = modelAuthorities;
    }
}
