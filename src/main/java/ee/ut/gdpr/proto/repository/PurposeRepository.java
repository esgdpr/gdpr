package ee.ut.gdpr.proto.repository;

import ee.ut.gdpr.proto.domain.Purpose;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurposeRepository extends JpaRepository<Purpose, String> {
}
