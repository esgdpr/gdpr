package ee.ut.gdpr.proto.repository;

import ee.ut.gdpr.proto.domain.PersonalData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PersonalDataRepository extends JpaRepository<PersonalData, String> {

    PersonalData findFirstByLabelAndCollectionLabel(String label, String collectionLabel);

    @Query("select distinct(pd.personalData) from TaskPersonalData pd where pd.task.lane.pool.bpmnProcess.id = :processId")
    List<PersonalData> findProcessPersonalDataByProcessId(@Param("processId") String processId);
}
