package ee.ut.gdpr.proto.repository;

import ee.ut.gdpr.proto.domain.TaskPersonalData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskPersonalDataRepository extends JpaRepository<TaskPersonalData, String> {
}
