package org.example.capstone2.repository;

import org.example.capstone2.model.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MaterialRepository extends JpaRepository<Material, Integer> {

    Material findMaterialById(Integer materialId);

    List<Material> findMaterialByRequestId(Integer requestId);

    List<Material> findByRequestId(Integer requestId);

    // Total cost of all materials used in a request
    @Query("SELECT SUM(m.quantityUsed * m.unitCost) FROM Material m WHERE m.requestId = ?1")
    Double getTotalCostByRequestId(Integer requestId);

    @Query("SELECT COALESCE(SUM(m.quantityUsed * m.unitCost), 0) FROM Material m JOIN MaintenanceRequest r ON r.id = m.requestId WHERE r.workerId = ?1 AND r.status = 'RESOLVED' AND MONTH(r.updatedAt) = MONTH(CURRENT_DATE) AND YEAR(r.updatedAt) = YEAR(CURRENT_DATE)")
    Double getTotalMaterialsCostThisMonth(Integer workerId);
}
