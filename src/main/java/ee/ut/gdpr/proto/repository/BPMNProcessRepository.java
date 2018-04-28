package ee.ut.gdpr.proto.repository;

import ee.ut.gdpr.proto.domain.BPMNProcess;
import ee.ut.gdpr.proto.domain.Model;
import ee.ut.gdpr.proto.domain.enums.ProcessType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BPMNProcessRepository extends JpaRepository<BPMNProcess, String> {

    @Query("select p.model.id from BPMNProcess p where p.id = :id")
    String getModelIdByProcessId(@Param("id") String processId);

    Integer countBPMNProcessByModelAndType(@Param("model") Model model, @Param("type") ProcessType processType);
    BPMNProcess getFirstByModelAndType(@Param("model") Model model, @Param("type") ProcessType processType);
}
