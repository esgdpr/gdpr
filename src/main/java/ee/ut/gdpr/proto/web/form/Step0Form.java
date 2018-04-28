package ee.ut.gdpr.proto.web.form;

import ee.ut.gdpr.proto.web.dto.LaneDTO;

import java.util.List;

public class Step0Form extends AbstractForm {

    public List<LaneDTO> lanes;

    public List<LaneDTO> getLanes() {
        return lanes;
    }

    public void setLanes(List<LaneDTO> lanes) {
        this.lanes = lanes;
    }
}
