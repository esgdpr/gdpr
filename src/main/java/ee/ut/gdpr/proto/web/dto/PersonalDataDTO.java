package ee.ut.gdpr.proto.web.dto;

import ee.ut.gdpr.proto.domain.enums.DataCategory;

public class PersonalDataDTO {

    public String id;
    public String label;
    public DataCategory category;
    public String dataInId;
    public String dataOutId;

    public PersonalDataDTO() {
    }

    public PersonalDataDTO(String id, String label) {
        this.id = id;
        this.label = label;
    }

    public PersonalDataDTO(String id, String label, DataCategory category) {
        this.label = label;
        this.id = id;
        this.category = category;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DataCategory getCategory() {
        return category;
    }

    public void setCategory(DataCategory category) {
        this.category = category;
    }

    public String getDataInId() {
        return dataInId;
    }

    public void setDataInId(String dataInId) {
        this.dataInId = dataInId;
    }

    public String getDataOutId() {
        return dataOutId;
    }

    public void setDataOutId(String dataOutId) {
        this.dataOutId = dataOutId;
    }
}
