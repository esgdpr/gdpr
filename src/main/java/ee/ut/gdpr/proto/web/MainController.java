package ee.ut.gdpr.proto.web;

import ee.ut.gdpr.proto.domain.BPMNProcess;
import ee.ut.gdpr.proto.domain.Model;
import ee.ut.gdpr.proto.domain.enums.ProcessType;
import ee.ut.gdpr.proto.repository.BPMNProcessRepository;
import ee.ut.gdpr.proto.repository.ModelRepository;
import ee.ut.gdpr.proto.service.GeneratorService;
import ee.ut.gdpr.proto.service.ModelService;
import ee.ut.gdpr.proto.service.ParserService;
import ee.ut.gdpr.proto.web.dto.BPMNPRrocessDTO;
import ee.ut.gdpr.proto.web.form.ModelForm;
import net.sourceforge.plantuml.SourceStringReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class MainController {

    @Autowired
    private ParserService parserService;

    @Autowired
    private BPMNProcessRepository bpmnProcessRepository;

    @Autowired
    private ModelRepository modelRepository;

    @Autowired
    private GeneratorService generatorService;

    @Autowired
    private ModelService modelService;

    @GetMapping("/")
    public String index(org.springframework.ui.Model model) {
        List<Model> models = modelRepository.findAll();
        model.addAttribute("models", models);
        model.addAttribute("wrapper", new ModelForm());
        return "home";
    }

    @PostMapping("/")
    public String createModel(@ModelAttribute ModelForm wrapper) {
        Model m = modelService.createModel(wrapper);
        return "redirect:/model/" + m.getId();
    }

    @GetMapping("/model/{modelId}")
    public String system(@PathVariable String modelId, org.springframework.ui.Model model) {
        Model a = modelRepository.findOne(modelId);
        model.addAttribute("processes",
                a.getBpmnProcesses().stream().map(o -> new BPMNPRrocessDTO(o.getId(), o.getName(), o.getOrder(), o.getType())).collect(Collectors.toList()));
        model.addAttribute("title", a.getName());
        model.addAttribute("modelId", a.getId());
        return "model";
    }

    @GetMapping(path = "/model/remove/{modelId}")
    public String removeAnalysis(@PathVariable String modelId) {
        Model a = modelRepository.findOne(modelId);
        modelRepository.delete(a);
        return "redirect:/";
    }

    @GetMapping(path = "/process/remove/{processId}")
    public String removeProcess(@PathVariable String processId) {
        BPMNProcess m = bpmnProcessRepository.findOne(processId);
        String modelId = m.getModel().getId();
        m.getModel().setBpmnProcesses(m.getModel().getBpmnProcesses().stream()
                .filter(o -> !o.getId().equals(m.getId())).collect(Collectors.toList()));
        modelRepository.save(m.getModel());
        bpmnProcessRepository.delete(m);
        return "redirect:/model/" + modelId;
    }

    @GetMapping(path = "/gen/{procId}/{pdId}")
    public String generate(org.springframework.ui.Model model, @PathVariable String procId, @PathVariable String pdId) throws IOException {
        ByteArrayOutputStream bous = new ByteArrayOutputStream();
        String res = generatorService.generateModel(procId, pdId);
        SourceStringReader reader = new SourceStringReader(res);
        reader.generateImage(bous);
        byte[] data = bous.toByteArray();
        model.addAttribute("pic", Base64.getEncoder().encodeToString(data));
        model.addAttribute("code", res);
        return "gen";
    }

    @GetMapping("/model/try/{modelId}")
    public String exampleUpload(@PathVariable String modelId) throws IOException {
        parserService.parseExample(modelId);
        return "redirect:/model/" + modelId;
    }

    @PostMapping("/model")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   @RequestParam("modelId") String modelId,
                                   @RequestParam("order") Integer order,
                                   @RequestParam("type") ProcessType type) throws IOException {
        parserService.parseModel(file, modelId, order, type);
        return "redirect:/model/" + modelId;
    }
}
