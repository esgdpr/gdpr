package ee.ut.gdpr.proto.service;

import ee.ut.gdpr.proto.domain.Lane;
import ee.ut.gdpr.proto.domain.*;
import ee.ut.gdpr.proto.domain.enums.*;
import ee.ut.gdpr.proto.repository.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.impl.instance.SubProcessImpl;
import org.camunda.bpm.model.bpmn.impl.instance.TaskImpl;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.Task;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.xml.ModelInstance;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ParserService {

    @Autowired
    private LaneRepository laneRepository;
    @Autowired
    private PoolRepository poolRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private TaskPersonalDataRepository taskPersonalDataRepository;
    @Autowired
    private BPMNProcessRepository bpmnProcessRepository;
    @Autowired
    private PersonalDataRepository personalDataRepository;
    @Autowired
    private FlowRepository flowRepository;
    @Autowired
    private ModelRepository modelRepository;
    @Autowired
    private DataSubjectRepository dataSubjectRepository;
    @Autowired
    private ResourceLoader resourceLoader;

    public String parseExample(String modelId) throws IOException {
        File file = resourceLoader.getResource("classpath:static/examples/diagram.bpmn").getFile();
        MultipartFile multipartFile = new MockMultipartFile("User Login", new FileInputStream(file));
        return parseModel(multipartFile, modelId, 1, ProcessType.DEFAULT);
    }

    public String parseModel(MultipartFile file, String modelId, Integer order, ProcessType type) throws IOException {
        ModelInstance modelInstance = Bpmn.readModelFromStream(file.getInputStream());
        Model model = modelRepository.findOne(modelId);

        Map<String, String> ids = new HashMap<>();

        BPMNProcess bpmnProcess = new BPMNProcess();
        bpmnProcess.setOrder(order);
        bpmnProcess.setType(type);
        bpmnProcess.setName(StringUtils.isEmpty(file.getOriginalFilename()) ? file.getName() : file.getOriginalFilename().replace(".bpmn", ""));
        bpmnProcess.setModel(model);
        bpmnProcessRepository.save(bpmnProcess);

        if (type == ProcessType.DEFAULT) {
            bpmnProcess.setPools(parsePools(modelInstance, bpmnProcess, ids));
            bpmnProcessRepository.save(bpmnProcess);
            parseMessageFlows(modelInstance, ids, bpmnProcess);
        }
        model.getBpmnProcesses().add(bpmnProcess);
        modelRepository.save(model);

        return bpmnProcess.getId();
    }

    private List<Pool> parsePools(ModelInstance modelInstance, BPMNProcess bpmnProcess, Map<String, String> ids) {
        Collection<Process> list = modelInstance.getModelElementsByType(Process.class);
        Collection<Participant> participants = modelInstance.getModelElementsByType(Participant.class);
        List<Pool> result = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            for (Process pr : list) {
                Pool pool = new Pool();
                if (pr.getName() == null) {
                    Participant participant = participants.stream().filter(o -> o.getProcess().equals(pr)).findFirst().get();
                    pool.setName(participant.getName().replace("\n", " "));
                } else {
                    pool.setName(pr.getName().replace("\n", " "));
                }
                pool.setId(UUID.randomUUID().toString());
                ids.putIfAbsent(pool.getId(), pr.getId());
                pool.setBpmnProcess(bpmnProcess);
                poolRepository.save(pool);
                Collection<Task> ts = pr.getChildElementsByType(Task.class);
                Collection<SubProcess> sp = pr.getChildElementsByType(SubProcess.class);
                List<org.camunda.bpm.model.bpmn.instance.Lane> lanes =
                        pr.getLaneSets().stream().flatMap(o -> o.getLanes().stream()).collect(Collectors.toList());
                Pair<List<Lane>, List<ee.ut.gdpr.proto.domain.Task>> pair = parseLanes(modelInstance, lanes, pool, ts, sp, ids);
                pool.setLanes(pair.getLeft());
                parseSequenceFlows(modelInstance, pair.getRight(), ids);
                result.add(pool);
            }
        }
        return poolRepository.save(result);
    }


    private Pair<List<Lane>, List<ee.ut.gdpr.proto.domain.Task>> parseLanes(ModelInstance modelInstance, List<org.camunda.bpm.model.bpmn.instance.Lane> lanes, Pool pool,
                                                                            Collection<Task> ts, Collection<SubProcess> sp, Map<String, String> ids) {
        List<Lane> result = new ArrayList<>();
        List<ee.ut.gdpr.proto.domain.Task> taskList = new ArrayList<>();
        if (lanes.isEmpty()) {
            Lane lane = new Lane();
            lane.setName(pool.getName() == null ? pool.getName() : pool.getName().replace("\n", " "));
            lane.setId(UUID.randomUUID().toString());
            ids.putIfAbsent(lane.getId(), pool.getId());
            lane.setPool(pool);
            laneRepository.save(lane);
            List<ee.ut.gdpr.proto.domain.Task> tasks = parseTasks(modelInstance, ts.stream().collect(Collectors.toList()), lane, ids);
            List<ee.ut.gdpr.proto.domain.Task> subp = parseTasks(modelInstance, sp.stream().collect(Collectors.toList()), lane, ids);
            tasks.addAll(subp);
            lane.setTasks(tasks);
            if (lane.isCandidateForFilingSystem()) {
                lane.setType(LaneType.FILING_SYSTEM);
            }
            result.add(lane);
            taskList.addAll(tasks);
        }
        for (org.camunda.bpm.model.bpmn.instance.Lane l : lanes) {
            List<String> filter = l.getFlowNodeRefs().stream().map(o -> o.getId()).collect(Collectors.toList());
            Lane lane = new Lane();
            lane.setName(l.getName() == null ? pool.getName() : l.getName().replace("\n", " "));
            lane.setId(UUID.randomUUID().toString());
            ids.putIfAbsent(lane.getId(), l.getId());
            lane.setPool(pool);
            laneRepository.save(lane);
            List<ee.ut.gdpr.proto.domain.Task> tasks = parseTasks(modelInstance, ts.stream().filter(o -> filter.contains(o.getId())).collect(Collectors.toList()), lane, ids);
            List<ee.ut.gdpr.proto.domain.Task> subp = parseTasks(modelInstance, sp.stream().filter(o -> filter.contains(o.getId())).collect(Collectors.toList()), lane, ids);
            tasks.addAll(subp);
            lane.setTasks(tasks);
            if (lane.isCandidateForFilingSystem()) {
                lane.setType(LaneType.FILING_SYSTEM);
            }
            result.add(lane);
            taskList.addAll(tasks);
        }

        return Pair.of(laneRepository.save(result), taskList);
    }

    private <T extends Activity> List<ee.ut.gdpr.proto.domain.Task> parseTasks(ModelInstance modelInstance, List<T> list, Lane lane, Map<String, String> ids) {
        List<ee.ut.gdpr.proto.domain.Task> tasks = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            for (T t : list) {
                ee.ut.gdpr.proto.domain.Task task = new ee.ut.gdpr.proto.domain.Task();
                task.setId(UUID.randomUUID().toString());
                ids.putIfAbsent(task.getId(), t.getId());
                task.setName(t.getName().replace("\n", " "));
                task.setLane(lane);
                taskRepository.save(task);
                List<TaskPersonalData> in = collectData(modelInstance, t.getDataInputAssociations(), task, DataFlowDirection.IN);
                List<TaskPersonalData> out = collectData(modelInstance, t.getDataOutputAssociations(), task, DataFlowDirection.OUT);
                in.addAll(out);
                task.setTaskPersonalData(in);
                task.setType(getTaskType(t.getProperties()));
                if (!in.isEmpty() || !out.isEmpty()) {
                    task.setType(TaskType.PROCESSING);
                }
                tasks.add(task);
            }
        }
        return taskRepository.save(tasks);
    }

    private List<TaskPersonalData> collectData(ModelInstance modelInstance, Collection<? extends DataAssociation> list,
                                               ee.ut.gdpr.proto.domain.Task task, DataFlowDirection direction) {

        List<TaskPersonalData> result = new ArrayList<>();
        boolean isCollection = false;
        if (!CollectionUtils.isEmpty(list)) {
            List<PersonalData> pds = new ArrayList<>();
            for (DataAssociation da : list) {
                if (DataFlowDirection.IN == direction) {
                    try {
                        for (ItemAwareElement itm : da.getSources()) {
                            DataObject dataObject = ((DataObjectReference) itm).getDataObject();
                            String content = dataObject.getName() == null ? ((DataObjectReference) itm).getName() : dataObject.getName();
                            pds.addAll(parsePersonalData(content, isCollection || dataObject.isCollection(), task.getLane()));
                        }
                    } catch (ClassCastException e) {
                        String refId = da.getTextContent();
                        ModelElementInstance a = modelInstance.getModelElementById(refId);
                        if (a != null) {
                            pds.addAll(parsePersonalData(a.getAttributeValue("name"), true, task.getLane()));
                        }
                    }
                }
                if (DataFlowDirection.OUT == direction) {
                    try {
                        DataObject dataObject = ((DataObjectReference) da.getTarget()).getDataObject();
                        String content = dataObject.getName();
                        pds.addAll(parsePersonalData(content, isCollection || dataObject.isCollection(), task.getLane()));
                    } catch (ClassCastException e) {
                        String refId = da.getTextContent();
                        ModelElementInstance a = modelInstance.getModelElementById(refId);
                        pds.addAll(parsePersonalData(a.getAttributeValue("name"), true, task.getLane()));
                    }
                }
            }
            for (PersonalData pd : pds) {
                TaskPersonalData taskPersonalData = new TaskPersonalData();
                taskPersonalData.setId(UUID.randomUUID().toString());
                taskPersonalData.setPersonalData(pd);
                taskPersonalData.setTask(task);
                taskPersonalData.setDirection(direction);
                result.add(taskPersonalDataRepository.save(taskPersonalData));
            }
        }
        return result;
    }

    private List<PersonalData> parsePersonalData(String content, boolean isCollection, Lane dataSubject) {
        List<PersonalData> personalData = new ArrayList<>();
        String name = content == null ? null : content.replace("\n", "");
        if (name != null) {
            String[] byObjects = name.split("\\)");
            for (String obj : byObjects) {
                DataSubject ds = null;
                String[] res = obj.split("\\(");
                String colLabel = res.length > 1 ? res[0].toLowerCase().trim() : null;
                String[] labels = res[res.length - 1].split(",");
                for (String label : labels) {
                    label = label.toLowerCase().trim();
                    PersonalData pd = personalDataRepository.findFirstByLabelAndCollectionLabel(label, colLabel);
                    if (pd == null) {
                        pd = new PersonalData();
                        pd.setId(UUID.randomUUID().toString());
                        pd.setLabel(label);
                        pd.setFromDB(isCollection);
                        pd.setCollectionLabel(colLabel);
                        if (!isCollection) {
                            boolean isDataSubject = StringUtils.isEmpty(colLabel) && !LaneType.FILING_SYSTEM.equals(dataSubject.getType());
                            ds = dataSubjectRepository.findFirstByName(isDataSubject ? dataSubject.getName() : colLabel);
                            if (ds == null) {
                                ds = new DataSubject();
                                ds.setId(UUID.randomUUID().toString());
                                ds.setName(isDataSubject ? dataSubject.getName() : colLabel);
                                if (isDataSubject) {
                                    ds.setPool(dataSubject.getId());
                                    dataSubject.setType(LaneType.DATA_SUBJECT);
                                    laneRepository.save(dataSubject);
                                }
                                ds.setPersonalData(new ArrayList<>());
                            }
                        }
                        personalDataRepository.save(pd);
                        if (ds != null) {
                            ds.getPersonalData().add(pd);
                            dataSubjectRepository.save(ds);
                            pd.setDataSubject(ds);
                            personalDataRepository.save(pd);
                        }
                    }
                    personalData.add(pd);
                }
            }
        }
        return personalData;
    }

    private void parseSequenceFlows(ModelInstance modelInstance, List<ee.ut.gdpr.proto.domain.Task> tasks, Map<String, String> ids) {

        Map<String, String> reverseMap = ids.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

        List<Flow> toSave = new ArrayList<>();
        for (ee.ut.gdpr.proto.domain.Task task : tasks) {
            Activity a = modelInstance.getModelElementById(ids.get(task.getId()));
            List<String> out = findOutgoing(a.getOutgoing());
            if (!CollectionUtils.isEmpty(out)) {
                out.forEach(o -> toSave.add(new Flow(UUID.randomUUID().toString(), task.getId(), reverseMap.get(o), FlowType.SEQUENCE)));
            }
        }
        flowRepository.save(toSave);
    }

    private void parseMessageFlows(ModelInstance modelInstance, Map<String, String> ids, BPMNProcess bpmnProcess) {
        Collection<MessageFlow> flows = modelInstance.getModelElementsByType(MessageFlow.class);
        Map<String, String> reverseMap = ids.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
        List<Flow> toSave = new ArrayList<>();
        for (MessageFlow flow : flows) {
            if (flow.getSource() instanceof TaskImpl || flow.getSource() instanceof SubProcessImpl) {
                String from = reverseMap.get(flow.getSource().getId());
                ee.ut.gdpr.proto.domain.Task taskFrom = from != null ? taskRepository.findOne(from) : null;
                if (taskFrom != null) {
                    Lane laneTo;
                    if (flow.getTarget() instanceof TaskImpl || flow.getTarget() instanceof SubProcessImpl) {
                        String to = reverseMap.get(flow.getTarget().getId());
                        laneTo = to != null ? taskRepository.findLaneByTaskId(to) : null;
                    } else {
                        Process p = getProcessOfElement(flow.getTarget());
                        if (p != null) {
                            String candTo = reverseMap.get(p.getId());
                            Pool poolTo = candTo != null ? poolRepository.findOne(candTo) : null;
                            laneTo = poolTo != null ? poolTo.getLanes().get(0) : null;
                        } else {
                            laneTo = new Lane(UUID.randomUUID().toString(), ((Participant) flow.getTarget()).getName());
                            List<Lane> lanes = new ArrayList<>();
                            lanes.add(laneTo);
                            Pool newPool = new Pool(UUID.randomUUID().toString(),
                                    ((Participant) flow.getTarget()).getName().replace("\n", " "), lanes, bpmnProcess);
                            laneRepository.save(lanes);
                            ids.putIfAbsent(laneTo.getId(), flow.getTarget().getId());
                            reverseMap = ids.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
                            poolRepository.save(newPool);
                            laneTo.setPool(newPool);
                            laneRepository.save(laneTo);
                            bpmnProcess.getPools().add(newPool);
                            bpmnProcessRepository.save(bpmnProcess);
                        }

                    }
                    if (laneTo != null) {
                        Flow fl = new Flow(UUID.randomUUID().toString(), taskFrom.getId(), laneTo.getId(), FlowType.MESSAGE);
                        toSave.add(fl);

                        if (taskFrom.getLane().getType().equals(LaneType.FILING_SYSTEM)
                                && laneTo.getType().equals(LaneType.OTHER) && taskFrom.dataGoesOut()) {
                            laneTo.setType(LaneType.RECIPIENT);
                            laneRepository.save(laneTo);
                        }
                        taskFrom.setDisclosure(true);
                        taskRepository.save(taskFrom);
                    }
                }
            }
        }
        flowRepository.save(toSave);
    }

    private Process getProcessOfElement(ModelElementInstance node) {
        if (node instanceof Process) {
            return (Process) node;
        } else if (node instanceof Participant) {
            return ((Participant) node).getProcess();
        } else {
            return getProcessOfElement(node.getParentElement());
        }
    }

    private List<String> findOutgoing(Collection<SequenceFlow> flows) {
        List<String> res = new ArrayList<>();
        if (!CollectionUtils.isEmpty(flows)) {
            for (SequenceFlow flow : flows) {
                if (flow.getTarget() != null && (flow.getTarget() instanceof TaskImpl || flow.getTarget() instanceof SubProcessImpl)) {
                    res.add(flow.getTarget().getId());
                } else if (flow.getTarget() != null) {
                    res.addAll(findOutgoing(flow.getTarget().getOutgoing()));
                }
            }
        }
        return res;
    }

    private TaskType getTaskType(Collection<Property> c) {
        for (Property p : c) {
            if (!StringUtils.isEmpty(p.getName().replace("\n", " "))) {
                switch (p.getName().toLowerCase()) {
                    case ("logging"):
                        return TaskType.LOGGING;
                    case ("consent"):
                        return TaskType.CONSENT;
                    default:
                        return TaskType.OTHER;
                }
            }
        }
        return TaskType.OTHER;
    }
}
