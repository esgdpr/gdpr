package ee.ut.gdpr.proto.service;

import ee.ut.gdpr.proto.domain.*;
import ee.ut.gdpr.proto.domain.enums.*;
import ee.ut.gdpr.proto.repository.BPMNProcessRepository;
import ee.ut.gdpr.proto.repository.TaskRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GeneratorService {

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private CommonService commonService;
    @Autowired
    private ValidatorService validatorService;
    @Autowired
    private BPMNProcessRepository bpmnProcessRepository;


    public String generateModel(String procId, String pdId) {
        Map<String, String> idsAndNames = new HashMap();
        Task processing = taskRepository.findOne(procId);
        List<ValidationError> errors = validatorService.validateTask(procId);
        List<Note> notes = new ArrayList<>();

        Purpose purpose;
        if (errors.contains(ValidationError.PURPOSE_MISSING)) {
            purpose = new Purpose(UUID.randomUUID().toString(), "Purpose");
            notes.add(new Note(notes.size(), ValidationError.PURPOSE_MISSING.getModelError(), purpose.getId()));
        } else {
            purpose = processing.getPurpose();
        }

        Task record;
        if (errors.contains(ValidationError.LOGGING_MISSING)) {
            record = new Task(UUID.randomUUID().toString(), "Record", TaskType.LOGGING);
            notes.add(new Note(notes.size(), ValidationError.LOGGING_MISSING.getModelError(), record.getId()));
        } else {
            record = commonService.findRecordTask(processing);
        }

        Task consent;
        if (errors.contains(ValidationError.CONSENT_MISSING)) {
            consent = new Task(UUID.randomUUID().toString(), "Consent", TaskType.CONSENT);
            notes.add(new Note(notes.size(), ValidationError.CONSENT_MISSING.getModelError(), consent.getId()));
        } else {
            consent = commonService.findConsentTask(processing);
        }
        if (errors.contains(ValidationError.CONSENT_POORLY_COLLECTED)) {
            notes.add(new Note(notes.size(),
                    ValidationError.CONSENT_POORLY_COLLECTED.getModelError(),
                    consent.getId()));
        }

        Lane filingSystem = processing.getLane();

        TaskPersonalData personalData =
                processing.getTaskPersonalData().stream()
                        .filter(tpd -> tpd.getId().equals(pdId)).findFirst().get();

        if (personalData.getPersonalData() != null && personalData.getPersonalData().getDataSubject() == null) {
            DataSubject ds = commonService.findDataSubject(processing.getLane().getPool().getBpmnProcess().getId());
            personalData.getPersonalData().setDataSubject(ds);
        }

        Pair<ModelAuthority, ModelAuthority> cpPair = commonService.findControllerAndProcessor(processing);
        ModelAuthority controller = cpPair.getLeft();
        ModelAuthority processor = cpPair.getRight();

        Lane recipient = null;
        if (processing.isDisclosure() && personalData.getDirection().equals(DataFlowDirection.OUT)) {
            recipient = commonService.findRecipient(processing);
        }


        StringBuffer model = new StringBuffer();
        model.append("@startuml\n");
        model.append("skinparam shadowing false\n");

        /* classes */
        model.append(createTaskClass(processing, idsAndNames));
        model.append(createPurpose(purpose, idsAndNames));
        model.append(createTaskClass(record, idsAndNames));
        model.append(createTaskClass(consent, idsAndNames));
        if (personalData != null) {
            model.append(createPersonalDataClass(personalData, idsAndNames));
            model.append(createDataSubject(personalData, idsAndNames));
        }
        model.append(createAuthorityClass(controller, idsAndNames));
        if (!controller.equals(processor)) {
            model.append(createAuthorityClass(processor, idsAndNames));
        } else {
            idsAndNames.put(processor.getId(), controller.getAuthority().getName().replace(" ", "_"));
        }
        model.append(createActor(filingSystem, idsAndNames));
        if (recipient != null) {
            model.append(createActor(recipient, idsAndNames));
            model.append(createDisclosure(processing, idsAndNames));
        }
        /* rights */
        String rightsRels = null;
        if (purpose == null || !purpose.isDo_not_require_identification()) {
            Pair<String, String> rights = generateRights(processing, recipient, consent, personalData,
                    errors, notes, idsAndNames);
            rightsRels = rights.getRight();
            model.append(rights.getLeft());
        }

        /* relations */
        model.append(String.format("%s -- %s : set of >\n", idsAndNames.get(filingSystem.getId()),
                idsAndNames.get(personalData.getId())));
        model.append(String.format("%s -- %s : owns >\n",
                idsAndNames.get(personalData.getPersonalData().getDataSubject().getId()),
                idsAndNames.get(personalData.getId())));
        model.append(String.format("%s -- %s : authorizes >\n", idsAndNames.get(controller.getId()),
                idsAndNames.get(processor.getId())));
        model.append(String.format("%s -- %s : uses >\n", idsAndNames.get(processor.getId()),
                idsAndNames.get(filingSystem.getId())));
        model.append(String.format("%s -- %s : processes >\n", idsAndNames.get(processor.getId()),
                idsAndNames.get(personalData.getId())));
        model.append(String.format("%s -- %s : determines >\n", idsAndNames.get(controller.getId()),
                idsAndNames.get(purpose.getId())));
        model.append(String.format("%s -- %s : to process >\n", idsAndNames.get(purpose.getId()),
                idsAndNames.get(personalData.getId())));
        model.append(String.format("%s -- %s : uses >\n", idsAndNames.get(processor.getId()),
                idsAndNames.get(purpose.getId())));
        model.append(String.format("(%s, %s) .. %s \n", idsAndNames.get(processor.getId()),
                idsAndNames.get(personalData.getId()), idsAndNames.get(processing.getId())));
        model.append(String.format("%s -- %s : logs >\n", idsAndNames.get(processing.getId()),
                idsAndNames.get(personalData.getId())));
        model.append(String.format("(%s, %s) .. %s \n", idsAndNames.get(processing.getId()),
                idsAndNames.get(personalData.getId()), idsAndNames.get(record.getId())));
        model.append(String.format("%s -- %s : signifies >\n",
                idsAndNames.get(personalData.getPersonalData().getDataSubject().getId()),
                idsAndNames.get(consent.getId())));
        model.append(String.format("(%s, %s) .. Agreement \n",
                idsAndNames.get(personalData.getPersonalData().getDataSubject().getId()),
                idsAndNames.get(consent.getId()), idsAndNames.get(record.getId())));
        model.append(String.format("%s -- %s : given for >\n", idsAndNames.get(consent.getId()),
                idsAndNames.get(purpose.getId())));
        if (recipient != null) {
            model.append(String.format("%s -- %s : discolsed to >\n", idsAndNames.get(personalData.getId()),
                    idsAndNames.get(recipient.getId())));
            model.append(String.format("(%s, %s) .. %s \n", idsAndNames.get(personalData.getId()),
                    idsAndNames.get(recipient.getId()), idsAndNames.get(processing.getId()) + "_Disclosure"));
        }

        for (Note note : notes) {
            model.append(createNote(note, idsAndNames));
        }

        if (StringUtils.isNotEmpty(rightsRels)) {
            model.append(rightsRels);
        }

        model.append("@enduml");

        return model.toString();
    }

    private String createTaskClass(Task task, Map<String, String> idsAndNames) {
        String name = task.getName().replace(" ", "_");
        idsAndNames.put(task.getId(), name);
        StringBuffer line = new StringBuffer();
        line.append(String.format("class %s << %s >>", name, task.getType().getValue()));
        if (task.getType() == TaskType.PROCESSING) {
            line.append(String.format(" {\n\t-operation PROCESSING_OPERATION: %s\n\t-cross_border boolean: %s\n}",
                    task.getProcessingOperation(), task.isCrossBorder()));
        } else if (task.getType() == TaskType.CONSENT) {
            line.append(String.format(" {\n\t-is_freely_given boolean: %s\n\t-is_specific boolean: %s\n",
                    task.getConsent() == null ? false : task.getConsent().isFreely_given(),
                    task.getConsent() == null ? false : task.getConsent().isSpecific()));
            line.append(String.format("\t-is_informed_of_withdrawal boolean: %s\n\t-is_unambiguous boolean: %s\n",
                    task.getConsent() == null ? false : task.getConsent().isInformed_of_withdrawal(),
                    task.getConsent() == null ? false : task.getConsent().isUnambiguous()));
            line.append(String.format("\t-given_with_affirmative_action boolean: %s\n\t-is_clearly_distinguishable boolean: %s\n}",
                    task.getConsent() == null ? false : task.getConsent().isGiven_with_affirmative_action(),
                    task.getConsent() == null ? false : task.getConsent().isClearly_distinguishable()));
        }
        line.append("\n");
        return line.toString();
    }

    private String createPurpose(Purpose purpose, Map<String, String> idsAndNames) {
        String name = purpose.getName().replace(" ", "_");
        idsAndNames.put(purpose.getId(), name);
        StringBuffer line = new StringBuffer();
        line.append(String.format("class %s << Purpose >> {\n", name));
        line.append(String.format("\t-legitimate_interest boolean: %s\n", purpose.isLegitimate_interest()));
        line.append(String.format("\t-do_not_require_identification boolean: %s\n", purpose.isDo_not_require_identification()));
        line.append(String.format("\t-public_interest boolean: %s\n", purpose.isPublic_interest()));
        line.append(String.format("\t-legal_obligation boolean: %s\n", purpose.isLegal_obligation()));
        line.append(String.format("\t-vital_interest boolean: %s\n", purpose.isVital_interest()));
        line.append(String.format("\t-contract_processing boolean: %s\n}\n", purpose.isContract_processing()));
        return line.toString();
    }

    private String createActor(Lane lane, Map<String, String> idsAndNames) {
        String name = lane.getName().replace(" ", "_");
        idsAndNames.put(lane.getId(), name);
        return String.format("class %s << %s >> %s\n", name, lane.getType().getValue(),
                lane.getType().equals(LaneType.RECIPIENT) ? "#yellow" : "");
    }

    private String createDisclosure(Task task, Map<String, String> idsAndNames) {
        String name = task.getName().replace(" ", "_") + "_Disclosure";
        idsAndNames.putIfAbsent(task.getId(), name);
        return String.format("class %s << Disclosure >>\n", name);
    }

    private String createDataSubject(TaskPersonalData pd, Map<String, String> idsAndNames) {
        if (pd.getPersonalData().getDataSubject() != null) {
            String name = pd.getPersonalData().getDataSubject().getName().replace(" ", "_");
            idsAndNames.put(pd.getPersonalData().getDataSubject().getId(), name);
            return String.format("class %s << Data Subject >>\n", name);
        } else {
            return "";
        }
    }

    private String createAuthorityClass(ModelAuthority authority, Map<String, String> idsAndNames) {
        String name = authority.getAuthority().getName().replace(" ", "_");
        idsAndNames.put(authority.getId(), name);
        return String.format("class %s << %s >> #yellow {\n\t-type AUTHORITY_TYPE: %s\n}\n",
                name, authority.getProcessorType().getValue(), authority.getAuthorityType());
    }

    private String createPersonalDataClass(TaskPersonalData pd, Map<String, String> idsAndNames) {
        String name = pd.getPersonalData().getFullLabel().replace(" ", "_");
        idsAndNames.put(pd.getId(), name);
        StringBuffer line = new StringBuffer();
        line.append(String.format("class %s << Personal Data >> {\n", name));
        line.append(String.format("\t-direction FLOW_DIRECTION: %s\n", pd.getDirection()));
        line.append(String.format("\t-category DATA_CATEGORY: %s\n}\n", pd.getPersonalData().getCategory()));
        return line.toString();
    }

    private String createNote(Note note, Map<String, String> idsAndNames) {
        return String.format("note \"%s\" as %s #red\n%s .. %s\n", note.getContent(), note.getName(),
                note.getName(), idsAndNames.get(note.getConnectedTo()));
    }

    private Pair<String, String> generateRights(Task processing, Lane recipient, Task consent,
                                                TaskPersonalData personalData, List<ValidationError> errors,
                                                List<Note> notes, Map<String, String> idsAndNames) {
        Model model = processing.getLane().getPool().getBpmnProcess().getModel();
        StringBuffer rights = new StringBuffer();
        StringBuffer rels = new StringBuffer();
        rights.append("abstract class Right_to #pink\n");

        BPMNProcess erase;
        if (errors.contains(ValidationError.ERASURE_PROCESS_MISSING)) {
            erase = new BPMNProcess(ProcessType.RIGHT_TO_ERASE.getType(), ProcessType.RIGHT_TO_ERASE);
            notes.add(new Note(notes.size(), ValidationError.ERASURE_PROCESS_MISSING.getModelError(), erase.getId()));
        } else {
            erase = bpmnProcessRepository.getFirstByModelAndType(model, ProcessType.RIGHT_TO_ERASE);
        }

        BPMNProcess rectify;
        if (errors.contains(ValidationError.RECTIFICATION_PROCESS_MISSING)) {
            rectify = new BPMNProcess(ProcessType.RIGHT_TO_RECTIFY.getType(), ProcessType.RIGHT_TO_RECTIFY);
            notes.add(new Note(notes.size(), ValidationError.RECTIFICATION_PROCESS_MISSING.getModelError(),
                    rectify.getId()));
        } else {
            rectify = bpmnProcessRepository.getFirstByModelAndType(model, ProcessType.RIGHT_TO_RECTIFY);
        }

        BPMNProcess access;
        if (errors.contains(ValidationError.ACCESS_PROCESS_MISSING)) {
            access = new BPMNProcess(ProcessType.RIGHT_TO_ACCESS.getType(), ProcessType.RIGHT_TO_ACCESS);
            notes.add(new Note(notes.size(), ValidationError.ACCESS_PROCESS_MISSING.getModelError(), access.getId()));
        } else {
            access = bpmnProcessRepository.getFirstByModelAndType(model, ProcessType.RIGHT_TO_ACCESS);
        }

        BPMNProcess portability;
        if (errors.contains(ValidationError.PORTABILITY_PROCESS_MISSING)) {
            portability = new BPMNProcess(ProcessType.RIGHT_TO_PORTABILITY.getType(),
                    ProcessType.RIGHT_TO_PORTABILITY);
            notes.add(new Note(notes.size(), ValidationError.PORTABILITY_PROCESS_MISSING.getModelError(),
                    portability.getId()));
        } else {
            portability = bpmnProcessRepository.getFirstByModelAndType(model, ProcessType.RIGHT_TO_PORTABILITY);
        }

        BPMNProcess restriction;
        if (errors.contains(ValidationError.RESRICTION_PROCESS_MISSING)) {
            restriction = new BPMNProcess(ProcessType.RIGHT_TO_RESTRICT_PROCESSING.getType(),
                    ProcessType.RIGHT_TO_RESTRICT_PROCESSING);
            notes.add(new Note(notes.size(), ValidationError.RESRICTION_PROCESS_MISSING.getModelError(),
                    restriction.getId()));
        } else {
            restriction = bpmnProcessRepository.getFirstByModelAndType(model, ProcessType.RIGHT_TO_PORTABILITY);
        }

        rights.append(createRight(erase, idsAndNames));
        rights.append(createRight(rectify, idsAndNames));
        rights.append(createRight(access, idsAndNames));
        rights.append(createRight(portability, idsAndNames));
        rights.append(createRight(restriction, idsAndNames));

        BPMNProcess notification;
        if (errors.contains(ValidationError.NOTIFICATION_PROCESS_MISSING)) {
            notification = new BPMNProcess(ProcessType.RIGHT_OF_NOTIFICATION.getType(),
                    ProcessType.RIGHT_OF_NOTIFICATION);
            notes.add(new Note(notes.size(), ValidationError.NOTIFICATION_PROCESS_MISSING.getModelError(),
                    notification.getId()));
        } else {
            notification = bpmnProcessRepository.getFirstByModelAndType(model, ProcessType.RIGHT_TO_PORTABILITY);
        }
        rights.append(createRight(notification, idsAndNames));


        rels.append((String.format("%s -- Right_to : has >\n",
                idsAndNames.get(personalData.getPersonalData().getDataSubject().getId()))));
        rels.append(String.format("Right_to <|-- %s\nRight_to <|-- %s\nRight_to <|-- %s\nRight_to <|-- %s\nRight_to <|-- %s\n",
                idsAndNames.get(erase.getId()), idsAndNames.get(rectify.getId()),
                idsAndNames.get(access.getId()), idsAndNames.get(portability.getId()),
                idsAndNames.get(restriction.getId())));
        rels.append(String.format("%s -- %s : allows to correct >\n", idsAndNames.get(rectify.getId()),
                idsAndNames.get(personalData.getId())));
        rels.append(String.format("%s -- %s : allows to erase >\n", idsAndNames.get(erase.getId()),
                idsAndNames.get(personalData.getId())));
        rels.append(String.format("%s -- %s : allows export of >\n", idsAndNames.get(portability.getId()),
                idsAndNames.get(personalData.getId())));
        rels.append(String.format("%s -- %s : allows to get information about >\n", idsAndNames.get(access.getId()),
                idsAndNames.get(processing.getId())));
        rels.append(String.format("%s -- %s : allows to withdraw >\n", idsAndNames.get(restriction.getId()),
                idsAndNames.get(consent.getId())));
        rels.append(String.format("%s -- %s : allows to limit >\n", idsAndNames.get(restriction.getId()),
                idsAndNames.get(processing.getId())));
        rels.append(String.format("Right_to <|-- %s\n", idsAndNames.get(notification.getId())));
        rels.append(String.format("%s -- %s : triggers >\n", idsAndNames.get(rectify.getId()),
                idsAndNames.get(notification.getId())));
        rels.append(String.format("%s -- %s : triggers >\n", idsAndNames.get(access.getId()),
                idsAndNames.get(notification.getId())));
        rels.append(String.format("%s -- %s : triggers >\n", idsAndNames.get(restriction.getId()),
                idsAndNames.get(notification.getId())));
        if (recipient != null) {
            rels.append(String.format("%s -- %s : allows to get information about >\n",
                    idsAndNames.get(notification.getId()), idsAndNames.get(processing.getId()) + "_Disclosure"));
        }


        return Pair.of(rights.toString(), rels.toString());
    }

    private String createRight(BPMNProcess right, Map<String, String> idsAndNames) {
        String name = right.getName().replace(" ", "_");
        idsAndNames.put(right.getId(), name);
        return String.format("class %s << %s >> #pink\n", right.getName(), right.getType().getType());
    }

}

class Note {
    private String name;
    private String content;
    private String connectedTo;

    public Note() {
    }

    public Note(int name, String content, String connectedTo) {
        this.name = "N" + name;
        this.content = content;
        this.connectedTo = connectedTo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getConnectedTo() {
        return connectedTo;
    }

    public void setConnectedTo(String connectedTo) {
        this.connectedTo = connectedTo;
    }
}
