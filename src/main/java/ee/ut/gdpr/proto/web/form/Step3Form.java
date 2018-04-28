package ee.ut.gdpr.proto.web.form;

import ee.ut.gdpr.proto.web.dto.ConsentDTO;

public class Step3Form extends AbstractForm {

    public ConsentDTO consent;

    public ConsentDTO getConsent() {
        return consent;
    }

    public void setConsent(ConsentDTO consent) {
        this.consent = consent;
    }
}
