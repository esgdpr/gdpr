package ee.ut.gdpr.proto.repository;

import ee.ut.gdpr.proto.domain.Pool;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PoolRepository extends JpaRepository<Pool, String> {
}
