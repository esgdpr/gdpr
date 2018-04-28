package ee.ut.gdpr.proto.repository;

import ee.ut.gdpr.proto.domain.Consent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsentRepository extends JpaRepository<Consent, String> {
}
