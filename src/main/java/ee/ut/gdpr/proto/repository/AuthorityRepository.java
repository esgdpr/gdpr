package ee.ut.gdpr.proto.repository;

import ee.ut.gdpr.proto.domain.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, String> {
}
