package ee.ut.gdpr.proto.service;

import ee.ut.gdpr.proto.domain.*;
import ee.ut.gdpr.proto.domain.enums.AuthorityType;
import ee.ut.gdpr.proto.domain.enums.DataCategory;
import ee.ut.gdpr.proto.domain.enums.ProcessType;
import ee.ut.gdpr.proto.domain.enums.ValidationError;
import ee.ut.gdpr.proto.repository.BPMNProcessRepository;
import ee.ut.gdpr.proto.repository.TaskRepository;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ValidatorService {

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private CommonService commonService;
    @Autowired
    private BPMNProcessRepository bpmnProcessRepository;

    public List<ValidationError> validateTask(String taskId) {
        List<ValidationError> result = new ArrayList<>();
        Task processing = taskRepository.findOne(taskId);

        Task logging = commonService.findRecordTask(processing);
        if (logging == null) {
            result.add(ValidationError.LOGGING_MISSING);
        }

        Purpose purpose = processing.getPurpose();
        if (purpose == null) {
            result.add(ValidationError.PURPOSE_MISSING);
        }

        Task consentTask = commonService.findConsentTask(processing);
        if (consentTask == null && !consentNotNeeded(purpose)) {
            result.add(ValidationError.CONSENT_MISSING);
        } else if (consentTask != null && consentTask.getConsent() != null) {
            Consent consent = consentTask.getConsent();
            if (!(consent.isClearly_distinguishable() && consent.isFreely_given() && consent.isGiven_with_affirmative_action()
                    && consent.isInformed_of_withdrawal() && consent.isSpecific() && consent.isUnambiguous())) {
                result.add(ValidationError.CONSENT_POORLY_COLLECTED);
            }
        } else {
            result.add(ValidationError.CONSENT_POORLY_COLLECTED);
        }

        if (processing.hasDataCategory(DataCategory.CRIMINAL)) {
            Pair<ModelAuthority, ModelAuthority> a = commonService.findControllerAndProcessor(processing);
            if (a.getRight().getAuthorityType() != AuthorityType.OFFICIAL) {
                result.add(ValidationError.CRIMINAL_ERROR);
            }
        }

        if (processing.carefulProcessing() && !notCarefulProcessing(purpose)) {
            result.add(ValidationError.CAREFUL_PROCESSING);
        }

        if (processing.isCrossBorder()) {
            result.add(ValidationError.CROSS_BORDER);
        }

        if (purpose == null || !purpose.isDo_not_require_identification()) {
            Model model = processing.getLane().getPool().getBpmnProcess().getModel();
            if (bpmnProcessRepository.countBPMNProcessByModelAndType(model, ProcessType.RIGHT_OF_NOTIFICATION) < 1) {
                result.add(ValidationError.NOTIFICATION_PROCESS_MISSING);
            }
            if (bpmnProcessRepository.countBPMNProcessByModelAndType(model, ProcessType.RIGHT_TO_ACCESS) < 1) {
                result.add(ValidationError.ACCESS_PROCESS_MISSING);
            }
            if (bpmnProcessRepository.countBPMNProcessByModelAndType(model, ProcessType.RIGHT_TO_ERASE) < 1) {
                result.add(ValidationError.ERASURE_PROCESS_MISSING);
            }
            if (bpmnProcessRepository.countBPMNProcessByModelAndType(model, ProcessType.RIGHT_TO_PORTABILITY) < 1) {
                result.add(ValidationError.PORTABILITY_PROCESS_MISSING);
            }
            if (bpmnProcessRepository.countBPMNProcessByModelAndType(model, ProcessType.RIGHT_TO_RECTIFY) < 1) {
                result.add(ValidationError.RECTIFICATION_PROCESS_MISSING);
            }
            if (bpmnProcessRepository.countBPMNProcessByModelAndType(model, ProcessType.RIGHT_TO_RESTRICT_PROCESSING) < 1) {
                result.add(ValidationError.RESRICTION_PROCESS_MISSING);
            }
        }

        return result;
    }

    private boolean consentNotNeeded(Purpose purpose) {
        return purpose != null && (purpose.isContract_processing() || purpose.isDo_not_require_identification()
                || purpose.isLegal_obligation() || purpose.isLegitimate_interest() || purpose.isPublic_interest()
                || purpose.isVital_interest());
    }

    private boolean notCarefulProcessing(Purpose purpose) {
        return purpose != null && (purpose.isLegal_obligation() || purpose.isLegitimate_interest()
                || purpose.isPublic_interest() || purpose.isVital_interest());
    }
}
