package ee.ut.gdpr.proto.repository;

import ee.ut.gdpr.proto.domain.Lane;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LaneRepository extends JpaRepository<Lane, String> {

    @Query("select l.id from Lane l where l.pool.bpmnProcess.id = :prId")
    List<String> getAllLaneIds(@Param("prId") String prId);
}
