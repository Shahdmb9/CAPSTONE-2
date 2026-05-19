package org.example.capstone2.repository;

import jakarta.transaction.Transactional;
import org.example.capstone2.model.MaintenanceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MaintenanceRequestRepository extends JpaRepository<MaintenanceRequest, Integer> {


    MaintenanceRequest findMaintenanceRequestById(Integer id);

//    Integer countAll();
    Integer countMaintenanceRequestByWorkerIdAndStatus(Integer workerId, String status);
    List<MaintenanceRequest> findMaintenanceRequestByUrgentIsFalseAndStatus(String status);
    List<MaintenanceRequest> findMaintenanceRequestByUserId(Integer residentId);
    List<MaintenanceRequest> findMaintenanceRequestByWorkerId(Integer workerId);
    List<MaintenanceRequest> findMaintenanceRequestByStatus(String status);
    List<MaintenanceRequest> findMaintenanceRequestByUrgentIsTrue();
    List<MaintenanceRequest> findMaintenanceRequestByCategoryId(Integer categoryId);
    List<MaintenanceRequest> findMaintenanceRequestByUserIdAndStatus(Integer residentId, String status);
    List<MaintenanceRequest> findMaintenanceRequestByWorkerIdAndStatus(Integer workerId, String status);
    Integer countByStatus(String status);
    Integer countByWorkerId(Integer workerId);
    Integer countMaintenanceRequestByUrgentIsTrue();
    Integer countMaintenanceRequestByCategoryId(Integer categoryId);

    @Query("SELECT r FROM MaintenanceRequest r ORDER BY r.createdAt DESC")
    List<MaintenanceRequest> sortMaintenanceRequestByDate();

    @Query("SELECT r FROM MaintenanceRequest r ORDER BY r.createdAt")
    List<MaintenanceRequest> sortMaintenanceRequestByDateEarliest();

    @Query("SELECT r FROM MaintenanceRequest r ORDER BY r.urgent DESC, r.createdAt")
    List<MaintenanceRequest> sortMaintenanceRequestByUrgentAndDate();

    @Query("SELECT COALESCE(SUM(w.baseSalary), 0) FROM MaintenanceRequest r JOIN Worker w ON w.id = r.workerId WHERE r.workerId = ?1 AND r.status = 'RESOLVED' AND MONTH(r.updatedAt) = MONTH(CURRENT_DATE) AND YEAR(r.updatedAt) = YEAR(CURRENT_DATE)")
    Double getTotalSalaryThisMonth(Integer workerId);

    @Query("SELECT COALESCE(SUM(w.baseSalary), 0) FROM MaintenanceRequest r JOIN Worker w ON w.id = r.workerId WHERE r.workerId = ?1 AND r.status = 'RESOLVED' AND MONTH(r.updatedAt) between MONTH(?2) AND MONTH(?3)")
    Double getTotalSalaryBetweenMonths(Integer workerId, LocalDate startMonth, LocalDate endMonth);

    @Transactional
    @Modifying
    @Query("UPDATE MaintenanceRequest r SET r.workerId = null WHERE r.workerId = ?1 AND r.status='PENDING' ")
    Integer updateAllWorkerIdInRequest(Integer workerId);
}
