package ee.ut.gdpr.proto.web.dto;

public class PurposeDTO {

    public String name;
    public boolean legitimate_interest;
    public boolean do_not_require_identification;
    public boolean public_interest;
    public boolean legal_obligation;
    public boolean vital_interest;
    public boolean contract_processing;

    public PurposeDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isLegitimate_interest() {
        return legitimate_interest;
    }

    public void setLegitimate_interest(boolean legitimate_interest) {
        this.legitimate_interest = legitimate_interest;
    }

    public boolean isDo_not_require_identification() {
        return do_not_require_identification;
    }

    public void setDo_not_require_identification(boolean do_not_require_identification) {
        this.do_not_require_identification = do_not_require_identification;
    }

    public boolean isPublic_interest() {
        return public_interest;
    }

    public void setPublic_interest(boolean public_interest) {
        this.public_interest = public_interest;
    }

    public boolean isLegal_obligation() {
        return legal_obligation;
    }

    public void setLegal_obligation(boolean legal_obligation) {
        this.legal_obligation = legal_obligation;
    }

    public boolean isVital_interest() {
        return vital_interest;
    }

    public void setVital_interest(boolean vital_interest) {
        this.vital_interest = vital_interest;
    }

    public boolean isContract_processing() {
        return contract_processing;
    }

    public void setContract_processing(boolean contract_processing) {
        this.contract_processing = contract_processing;
    }
}
