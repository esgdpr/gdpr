package ee.ut.gdpr.proto.web.form;

import ee.ut.gdpr.proto.domain.enums.AuthorityType;

public class ModelForm {
    public String name;
    public String controller;
    public AuthorityType contype;
    public String processor;
    public AuthorityType proctype;
    public boolean third;
    public boolean conproc;

    public ModelForm() {
    }

    public String getController() {
        return controller;
    }

    public void setController(String controller) {
        this.controller = controller;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProcessor() {
        return processor;
    }

    public void setProcessor(String processor) {
        this.processor = processor;
    }

    public AuthorityType getContype() {
        return contype;
    }

    public void setContype(AuthorityType contype) {
        this.contype = contype;
    }

    public AuthorityType getProctype() {
        return proctype;
    }

    public void setProctype(AuthorityType proctype) {
        this.proctype = proctype;
    }

    public boolean isThird() {
        return third;
    }

    public void setThird(boolean third) {
        this.third = third;
    }

    public boolean isConproc() {
        return conproc;
    }

    public void setConproc(boolean conproc) {
        this.conproc = conproc;
    }
}
