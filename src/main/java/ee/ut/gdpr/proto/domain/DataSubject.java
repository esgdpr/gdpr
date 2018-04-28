package ee.ut.gdpr.proto.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
public class DataSubject implements Serializable {

    @Id
    private String id;
    private String name;
    @OneToMany(fetch = FetchType.LAZY)
    private List<PersonalData> personalData;
    @Column(name="POOL_ID")
    private String pool;

    public DataSubject() {
    }

    public DataSubject(String id, String name) {
        this.id = id;
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

    public List<PersonalData> getPersonalData() {
        return personalData;
    }

    public void setPersonalData(List<PersonalData> personalData) {
        this.personalData = personalData;
    }

    public String getPool() {
        return pool;
    }

    public void setPool(String pool) {
        this.pool = pool;
    }
}
