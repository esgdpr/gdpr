package ee.ut.gdpr.proto.domain;

import ee.ut.gdpr.proto.domain.enums.AuthorityType;
import ee.ut.gdpr.proto.domain.enums.ProcessorType;

import javax.persistence.*;
import java.io.Serializable;

@AssociationOverrides({
        @AssociationOverride(name = "model",
                joinColumns = @JoinColumn(name = "MODEL_ID")),
        @AssociationOverride(name = "authority",
                joinColumns = @JoinColumn(name = "AUTHORITY_ID")) })
@Entity
public class ModelAuthority implements Serializable {

    @Id
    private String id;
    @ManyToOne
    private Model model;
    @ManyToOne
    private Authority authority;
    @Enumerated(EnumType.STRING)
    private AuthorityType authorityType;
    @Enumerated(EnumType.STRING)
    private ProcessorType processorType;

    public ModelAuthority() {
    }

    public ModelAuthority(String id, ProcessorType processorType, AuthorityType authorityType, String name) {
        this.id = id;
        this.authorityType = authorityType;
        this.processorType = processorType;
        this.authority = new Authority(name);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public Authority getAuthority() {
        return authority;
    }

    public void setAuthority(Authority authority) {
        this.authority = authority;
    }

    public AuthorityType getAuthorityType() {
        return authorityType;
    }

    public void setAuthorityType(AuthorityType authorityType) {
        this.authorityType = authorityType;
    }

    public ProcessorType getProcessorType() {
        return processorType;
    }

    public void setProcessorType(ProcessorType processorType) {
        this.processorType = processorType;
    }
}
