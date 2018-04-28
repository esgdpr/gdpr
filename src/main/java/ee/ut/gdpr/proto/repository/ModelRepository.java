package ee.ut.gdpr.proto.repository;

import ee.ut.gdpr.proto.domain.BPMNProcess;
import ee.ut.gdpr.proto.domain.Model;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModelRepository extends JpaRepository<Model, String> {

    Model findByBpmnProcesses(BPMNProcess bpmnProcess);
}
