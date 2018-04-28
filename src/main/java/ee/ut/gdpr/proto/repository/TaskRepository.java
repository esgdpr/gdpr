package ee.ut.gdpr.proto.repository;

import ee.ut.gdpr.proto.domain.Lane;
import ee.ut.gdpr.proto.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, String> {

    @Query("select t from Task t where t.lane.pool.bpmnProcess.id = :processId and t.type = 'PROCESSING' and t.lane.type = 'FILING_SYSTEM'")
    List<Task> findAllProcessingTaskByProcessId(@Param("processId") String processId);

    @Query("select t from Task t where t.lane.pool.bpmnProcess.model.id = :modelId and t.type = 'CONSENT' " +
            "and t.lane.pool.bpmnProcess.ordr < :ordr order by t.lane.pool.bpmnProcess.ordr desc")
    List<Task> findByModelIdAndProcessOrderAndType(@Param("modelId") String processId, @Param("ordr") Integer ordr);

    @Query("select t.lane from Task t where t.id = :taskId")
    Lane findLaneByTaskId(@Param("taskId") String taskId);
}
