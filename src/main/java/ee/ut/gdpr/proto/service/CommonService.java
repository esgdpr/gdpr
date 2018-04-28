package ee.ut.gdpr.proto.service;

import ee.ut.gdpr.proto.domain.*;
import ee.ut.gdpr.proto.domain.enums.FlowType;
import ee.ut.gdpr.proto.domain.enums.LaneType;
import ee.ut.gdpr.proto.domain.enums.TaskType;
import ee.ut.gdpr.proto.repository.DataSubjectRepository;
import ee.ut.gdpr.proto.repository.FlowRepository;
import ee.ut.gdpr.proto.repository.LaneRepository;
import ee.ut.gdpr.proto.repository.TaskRepository;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class CommonService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private FlowRepository flowRepository;

    @Autowired
    private LaneRepository laneRepository;
    @Autowired
    private DataSubjectRepository dataSubjectRepository;

    public Task findRecordTask(Task processing) {
        List<Flow> candidates = flowRepository.findAllByFAndType(processing.getId(), FlowType.SEQUENCE);
        Task result = null;
        if (!CollectionUtils.isEmpty(candidates)) {
            for (Flow f: candidates) {
                Task a = taskRepository.findOne(f.getT());
                result = a.getType().equals(TaskType.LOGGING) ? a : null;
                if (result != null) {
                    break;
                }
            }
        }
        return result;
    }

    public Task findConsentTask(Task processing) {
        Task consent = findPrevConsentTask(processing.getId());
        if (consent != null) {
            return consent;
        }
        List<Task> consents = taskRepository.findByModelIdAndProcessOrderAndType(
                processing.getLane().getPool().getBpmnProcess().getModel().getId(),
                processing.getLane().getPool().getBpmnProcess().getOrder());

        return consents.isEmpty() ? null : consents.get(0);
    }

    public Task findConsentTask(BPMNProcess process) {
        List<Task> consents = taskRepository.findByModelIdAndProcessOrderAndType(
                process.getModel().getId(),process.getOrder() + 1);
        return consents.isEmpty() ? null : consents.get(0);
    }

    private Task findPrevConsentTask(String taskId) {
        List<Flow> candidates = flowRepository.findAllByTAndType(taskId, FlowType.SEQUENCE);
        Task result = null;
        if (!CollectionUtils.isEmpty(candidates)) {
            for (Flow f: candidates) {
                Task a = taskRepository.findOne(f.getF());
                if (a.getType().equals(TaskType.CONSENT)) {
                    return a;
                } else {
                   return findPrevConsentTask(a.getId());
                }
            }
        }
        return result;
    }

    public DataSubject findDataSubject(String processId) {
        List<String> allLaneIds = laneRepository.getAllLaneIds(processId);
        for (String i: allLaneIds) {
            List<DataSubject> ds = dataSubjectRepository.findAllByPool(i);
            if (!CollectionUtils.isEmpty(ds)) {
                return ds.get(0);
            }
        }
        return null;
    }

    public Lane findRecipient(Task disclosureTask) {
        if (disclosureTask.isDisclosure()) {
            List<Flow> flows = flowRepository.findAllByFAndType(disclosureTask.getId(), FlowType.MESSAGE);
            if (!CollectionUtils.isEmpty(flows)) {
                Lane oth = null;
                for (Flow f : flows) {
                    Lane a = laneRepository.findOne(f.getT());
                    if (LaneType.OTHER.equals(a.getType())) {
                        oth = a;
                    } else if (LaneType.RECIPIENT.equals(a.getType())){
                        return a;
                    }
                }
               return oth;
            }
        }
        return null;
    }

    public Pair<ModelAuthority, ModelAuthority> findControllerAndProcessor(Task task) {
        ModelAuthority controller = null;
        ModelAuthority processor = null;
        List<ModelAuthority> authorities = task.getLane().getPool().getBpmnProcess().getModel().getAuthorities();
        if (!CollectionUtils.isEmpty(authorities)) {
            for (ModelAuthority authority: authorities) {
                switch (authority.getProcessorType()) {
                    case CONTROLLER:
                        controller = authority;
                        break;
                    case PROCESSOR:
                    case THIRD_PARTY:
                        processor = authority;
                        break;
                    case PROCESSOR_CONTROLLER:
                        controller = authority;
                        processor = authority;
                        break;
                }
            }
        }
        return Pair.of(controller, processor);
    }
}
