package ee.ut.gdpr.proto.web.form;

import ee.ut.gdpr.proto.web.dto.PersonalDataDTO;

import java.util.List;

public class Step2Form extends AbstractForm {
    public List<PersonalDataDTO> data;

    public Step2Form() {
    }

    public List<PersonalDataDTO> getData() {
        return data;
    }

    public void setData(List<PersonalDataDTO> data) {
        this.data = data;
    }
}
