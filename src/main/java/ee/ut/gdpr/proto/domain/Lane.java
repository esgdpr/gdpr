package ee.ut.gdpr.proto.domain;

import ee.ut.gdpr.proto.domain.enums.LaneType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
public class Lane implements Serializable {

    @Id
    private String id;
    private String name;
    @OneToMany(orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Task> tasks;

    @Enumerated(EnumType.STRING)
    private LaneType type = LaneType.OTHER;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="POOL_ID")
    private Pool pool;

    public Lane() {
    }

    public Lane(String id, String name) {
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

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public boolean isCandidateForFilingSystem() {
        return tasks.stream().anyMatch(Task::hasCollectionRelation);
    }

    public Pool getPool() {
        return pool;
    }

    public void setPool(Pool pool) {
        this.pool = pool;
    }

    public LaneType getType() {
        return type;
    }

    public void setType(LaneType type) {
        this.type = type;
    }
}
