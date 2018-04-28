package ee.ut.gdpr.proto.service;

import ee.ut.gdpr.proto.domain.Authority;
import ee.ut.gdpr.proto.domain.Model;
import ee.ut.gdpr.proto.domain.ModelAuthority;
import ee.ut.gdpr.proto.domain.enums.ProcessorType;
import ee.ut.gdpr.proto.repository.AuthorityRepository;
import ee.ut.gdpr.proto.repository.ModelAuthorityRepository;
import ee.ut.gdpr.proto.repository.ModelRepository;
import ee.ut.gdpr.proto.web.form.ModelForm;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ModelService {

    @Autowired
    private ModelRepository modelRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private ModelAuthorityRepository modelAuthorityRepository;

    public Model createModel(ModelForm modelForm) {
        Model model = new Model();
        model.setId(UUID.randomUUID().toString());
        model.setName(modelForm.getName());
        model = modelRepository.save(model);

        Authority controller = new Authority();
        controller.setId(UUID.randomUUID().toString());
        controller.setName(modelForm.getController());
        authorityRepository.save(controller);
        ModelAuthority controllerT = new ModelAuthority();
        controllerT.setId(UUID.randomUUID().toString());
        controllerT.setAuthority(controller);
        controllerT.setModel(model);
        controllerT.setAuthorityType(modelForm.getContype());
        controllerT.setProcessorType(modelForm.isConproc() ? ProcessorType.PROCESSOR_CONTROLLER : ProcessorType.CONTROLLER);
        modelAuthorityRepository.save(controllerT);

        if (!StringUtils.isEmpty(modelForm.getProcessor())) {
            Authority processor = new Authority();
            processor.setId(UUID.randomUUID().toString());
            processor.setName(modelForm.getProcessor());
            authorityRepository.save(processor);
            ModelAuthority processorT = new ModelAuthority();
            processorT.setId(UUID.randomUUID().toString());
            processorT.setAuthority(processor);
            processorT.setModel(model);
            processorT.setAuthorityType(modelForm.getProctype());
            processorT.setProcessorType(modelForm.isThird() ? ProcessorType.THIRD_PARTY : ProcessorType.PROCESSOR);
            modelAuthorityRepository.save(processorT);
        }

        return model;
    }
}
