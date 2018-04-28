package ee.ut.gdpr.proto.repository;

import ee.ut.gdpr.proto.domain.Flow;
import ee.ut.gdpr.proto.domain.enums.FlowType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FlowRepository extends JpaRepository<Flow, String> {

    List<Flow> findAllByFAndType(String f, FlowType type);
    List<Flow> findAllByTAndType(String t, FlowType type);
}
