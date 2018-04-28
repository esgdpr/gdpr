package ee.ut.gdpr.proto.repository;

import ee.ut.gdpr.proto.domain.ModelAuthority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModelAuthorityRepository extends JpaRepository<ModelAuthority, String> {
}
