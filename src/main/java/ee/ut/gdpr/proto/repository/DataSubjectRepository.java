package ee.ut.gdpr.proto.repository;

import ee.ut.gdpr.proto.domain.DataSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DataSubjectRepository extends JpaRepository<DataSubject, String> {

    DataSubject findFirstByName(String name);

    List<DataSubject> findAllByPool(@Param("poolId") String poolId);
}
