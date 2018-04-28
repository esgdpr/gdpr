package ee.ut.gdpr.proto.web;

import ee.ut.gdpr.proto.service.StepService;
import ee.ut.gdpr.proto.web.form.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/steps")
public class StepController {

    @Autowired
    private StepService stepService;

    @GetMapping(path = "/step0/{processId}")
    public String step0(Model model, @PathVariable String processId) {
        model.addAttribute("form", stepService.prepareStep0(processId));
        return "steps/step0";
    }

    @PostMapping(path = "/step0")
    public String saveStep0(@ModelAttribute Step0Form form) {
        String modelId = stepService.saveStep0(form);
        return "redirect:/steps/step1/" + modelId;
    }

    @GetMapping(path = "/step1/{processId}")
    public String step1(Model model, @PathVariable String processId) {
        model.addAttribute("form", stepService.prepareStep1(processId));
        return "steps/step1";
    }

    @PostMapping(path = "/step1")
    public String saveStep1(@ModelAttribute Step1Form form) {
        String modelId = stepService.saveStep1(form);
        return "redirect:/steps/step2/" + modelId;
    }

    @GetMapping(path = "/step2/{processId}")
    public String step2(Model model, @PathVariable String processId) {
        model.addAttribute("form", stepService.prepareStep2(processId));
        return "steps/step2";
    }

    @PostMapping(path = "/step2")
    public String saveStep2(@ModelAttribute Step2Form form) {
        String modelId = stepService.saveStep2(form);
        return "redirect:/steps/step3/" + modelId;
    }

    @GetMapping(path = "/step3/{processId}")
    public String step3(Model model, @PathVariable String processId) {
        model.addAttribute("form", stepService.prepareStep3(processId));
        return "steps/step3";
    }

    @PostMapping(path = "/step3")
    public String saveStep3(@ModelAttribute Step3Form form) {
        String modelId = stepService.saveStep3(form);
        return "redirect:/steps/final/" + modelId;
    }

    @GetMapping(path = "/final/{processId}")
    public String finale(Model model, @PathVariable String processId) {
        FinalForm form = stepService.prepareFinal(processId);
        model.addAttribute("tasks", form.getTasks());
        model.addAttribute("processId", form.getProcessId());
        model.addAttribute("modelId", form.getModelId());
        return "steps/final";
    }
}
