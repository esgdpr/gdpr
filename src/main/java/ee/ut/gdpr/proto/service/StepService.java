package ee.ut.gdpr.proto.service;

import ee.ut.gdpr.proto.domain.*;
import ee.ut.gdpr.proto.domain.enums.DataFlowDirection;
import ee.ut.gdpr.proto.domain.enums.LaneType;
import ee.ut.gdpr.proto.repository.*;
import ee.ut.gdpr.proto.web.dto.*;
import ee.ut.gdpr.proto.web.form.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class StepService {

    @Autowired
    private BPMNProcessRepository bpmnProcessRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private PurposeRepository purposeRepository;
    @Autowired
    private PersonalDataRepository personalDataRepository;
    @Autowired
    private CommonService commonService;
    @Autowired
    private LaneRepository laneRepository;
    @Autowired
    private ConsentRepository consentRepository;
    @Autowired
    private DataSubjectRepository dataSubjectRepository;
    @Autowired
    private ValidatorService validatorService;
    @Autowired
    private FlowRepository flowRepository;

    public Step0Form prepareStep0(String processId) {
        Step0Form form = new Step0Form();
        BPMNProcess p = bpmnProcessRepository.findOne(processId);
        List<Lane> lanes = p.getPools().stream().flatMap(o->o.getLanes().stream()).collect(Collectors.toList());
        form.setLanes(lanes.stream().map(o -> new LaneDTO(o.getName(),
                o.getId(),
                o.getType()
                )).collect(Collectors.toList()));
        form.setProcessId(processId);
        form.setModelId(p.getModel().getId());
        return form;
    }

    public String saveStep0(Step0Form form) {
        List<DataSubject> dataSubjectsToChange = new ArrayList<>();
        Lane newDS = null;
        for (LaneDTO laneDTO : form.getLanes()) {
            Lane lane = laneRepository.findOne(laneDTO.getId());
            if (lane.getType().equals(LaneType.DATA_SUBJECT) && laneDTO.getType().equals(LaneType.FILING_SYSTEM)) {
                dataSubjectsToChange = dataSubjectRepository.findAllByPool(lane.getId());
            }
            if (!lane.getType().equals(LaneType.DATA_SUBJECT) && laneDTO.getType().equals(LaneType.DATA_SUBJECT)) {
                newDS = lane;
            }
            lane.setType(laneDTO.getType());
            laneRepository.save(lane);
        }
        if (newDS != null) {
            for (DataSubject ds : dataSubjectsToChange) {
                ds.setName(newDS.getName());
                ds.setPool(newDS.getId());
            }
            dataSubjectRepository.save(dataSubjectsToChange);
        }
        return form.getProcessId();
    }

    public Step1Form prepareStep1(String processId) {
        List<Task> processingTasks = taskRepository.findAllProcessingTaskByProcessId(processId);
        Step1Form form = new Step1Form();
        form.setTasks(processingTasks.stream().map(o -> {
            TaskStep1DTO taskStep1DTO = new TaskStep1DTO(o.getId(), o.getName());
            taskStep1DTO.setCrossborder(o.isCrossBorder());
            taskStep1DTO.setOperation(o.getProcessingOperation());
            if (o.getPurpose() != null) {
                PurposeDTO purposeDTO = new PurposeDTO();
                purposeDTO.setName(o.getPurpose().getName());
                purposeDTO.setContract_processing(o.getPurpose().isContract_processing());
                purposeDTO.setDo_not_require_identification(o.getPurpose().isDo_not_require_identification());
                purposeDTO.setLegal_obligation(o.getPurpose().isLegal_obligation());
                purposeDTO.setLegitimate_interest(o.getPurpose().isLegitimate_interest());
                purposeDTO.setPublic_interest(o.getPurpose().isPublic_interest());
                purposeDTO.setVital_interest(o.getPurpose().isVital_interest());
                taskStep1DTO.setPurpose(purposeDTO);
            }
            return taskStep1DTO;
        }).collect(Collectors.toList()));
        form.setModelId(bpmnProcessRepository.getModelIdByProcessId(processId));
        form.setProcessId(processId);
        return form;
    }

    public String saveStep1(Step1Form form) {
        for (TaskStep1DTO taskStep1DTO : form.getTasks()) {
            Task task = taskRepository.findOne(taskStep1DTO.getId());
            task.setProcessingOperation(taskStep1DTO.getOperation());
            task.setCrossBorder(taskStep1DTO.isCrossborder());

            Purpose purpose = new Purpose();
            purpose.setId(UUID.randomUUID().toString());
            purpose.setName(taskStep1DTO.getPurpose().getName());
            purpose.setContract_processing(taskStep1DTO.getPurpose().isContract_processing());
            purpose.setDo_not_require_identification(taskStep1DTO.getPurpose().isDo_not_require_identification());
            purpose.setLegal_obligation(taskStep1DTO.getPurpose().isLegal_obligation());
            purpose.setLegitimate_interest(taskStep1DTO.getPurpose().isLegitimate_interest());
            purpose.setPublic_interest(taskStep1DTO.getPurpose().isPublic_interest());
            purpose.setVital_interest(taskStep1DTO.getPurpose().isVital_interest());
            purpose.setTask(task);
            purposeRepository.save(purpose);
            task.setPurpose(purpose);
            taskRepository.save(task);
        }
        return form.getProcessId();
    }

    public Step2Form prepareStep2(String processId) {
        Step2Form form = new Step2Form();
        List<PersonalData> list = personalDataRepository.findProcessPersonalDataByProcessId(processId);
        form.setData(list.stream().map(o ->
                new PersonalDataDTO(o.getId(), o.getFullLabel(), o.getCategory())).collect(Collectors.toList()));

        form.setProcessId(processId);
        form.setModelId(bpmnProcessRepository.getModelIdByProcessId(processId));
        return form;
    }

    public String saveStep2(Step2Form form) {
        List<PersonalData> toSave = new ArrayList<>();
        for (PersonalDataDTO personalDataDTO : form.getData()) {
            PersonalData pd = personalDataRepository.findOne(personalDataDTO.getId());
            pd.setCategory(personalDataDTO.getCategory());
            toSave.add(pd);
        }
        personalDataRepository.save(toSave);
        return form.getProcessId();
    }

    public Step3Form prepareStep3(String processId) {
        Step3Form form = new Step3Form();
        BPMNProcess p = bpmnProcessRepository.findOne(processId);
        Task consent = commonService.findConsentTask(p);
        if (consent != null) {
            ConsentDTO consentDTO = new ConsentDTO(consent.getId(), consent.getName());
            if (consent.getConsent() != null) {
                consentDTO.setId(consent.getConsent().getId());
                consentDTO.setClearly_distinguishable(consent.getConsent().isClearly_distinguishable());
                consentDTO.setFreely_given(consent.getConsent().isFreely_given());
                consentDTO.setGiven_with_affirmative_action(consent.getConsent().isGiven_with_affirmative_action());
                consentDTO.setInformed_of_withdrawal(consent.getConsent().isInformed_of_withdrawal());
                consentDTO.setSpecific(consent.getConsent().isSpecific());
            }
            form.setConsent(consentDTO);
        }
        form.setProcessId(processId);
        form.setModelId(bpmnProcessRepository.getModelIdByProcessId(processId));
        return form;
    }

    public String saveStep3(Step3Form form) {
        Consent consent = consentRepository.findOne(form.getConsent().getId());
        Task task = taskRepository.findOne(form.getConsent().getTaskId());
        if (consent == null) {
            consent = new Consent();
            consent.setId(UUID.randomUUID().toString());
        }
        consent.setClearly_distinguishable(form.getConsent().isClearly_distinguishable());
        consent.setFreely_given(form.getConsent().isFreely_given());
        consent.setGiven_with_affirmative_action(form.getConsent().isGiven_with_affirmative_action());
        consent.setInformed_of_withdrawal(form.getConsent().isInformed_of_withdrawal());
        consent.setSpecific(form.getConsent().isSpecific());
        consent.setUnambiguous(form.getConsent().isUnambiguous());
        consent.setTask(task);
        consentRepository.save(consent);
        task.setConsent(consent);
        taskRepository.save(task);
        return form.getProcessId();
    }

    public FinalForm prepareFinal(String processId) {
        FinalForm form = new FinalForm();
        List<Task> processingTasks = taskRepository.findAllProcessingTaskByProcessId(processId);
        List<TaskFinalDTO> tasks = processingTasks.stream().map(o -> {
            TaskFinalDTO dto = new TaskFinalDTO(o.getId(), o.getName());
            if (!CollectionUtils.isEmpty(o.getTaskPersonalData())) {
                List<PersonalDataDTO> list = new ArrayList<>();
                o.getTaskPersonalData().forEach(z -> {
                    Optional<PersonalDataDTO> opt = list.stream().filter(j -> j.getId().equals(z.getPersonalData().getId())).findFirst();
                    PersonalDataDTO PDdto;
                    if (!opt.isPresent()) {
                        PDdto = new PersonalDataDTO(z.getPersonalData().getId(), z.getPersonalData().getFullLabel());
                        list.add(PDdto);
                    } else {
                        PDdto = opt.get();
                    }
                    if (z.getDirection().equals(DataFlowDirection.IN)) {
                        PDdto.setDataInId(z.getId());
                    } else if (z.getDirection().equals(DataFlowDirection.OUT)) {
                        PDdto.setDataOutId(z.getId());
                    }
                });
                dto.setPd(list);
            }
            dto.setErrors(validatorService.validateTask(o.getId()));
            return dto;
        }).collect(Collectors.toList());
        form.setTasks(tasks);
        form.setProcessId(processId);
        form.setModelId(bpmnProcessRepository.getModelIdByProcessId(processId));
        return form;
    }
}
