package org.example.capstone2.service;

import lombok.RequiredArgsConstructor;
import org.example.capstone2.ApiResponse.ApiException;
import org.example.capstone2.model.MaintenanceRequest;
import org.example.capstone2.model.Material;
import org.example.capstone2.repository.MaintenanceRequestRepository;
import org.example.capstone2.repository.MaterialRepository;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MaterialService {

    private final MaterialRepository materialRepository;
    private final MaintenanceRequestRepository requestRepository;

    public List<Material> getAllMaterials() {
        return materialRepository.findAll();
    }

    public void addMaterials(Material material) {
        MaintenanceRequest request=requestRepository.findMaintenanceRequestById(material.getRequestId());
        if(request==null)
            throw new ApiException("Request not found: ");
        if(!request.getStatus().equalsIgnoreCase("IN_PROGRESS"))
            throw new ApiException("Materials can only be added to IN_PROGRESS requests");

        materialRepository.save(material);
    }

    public void deleteMaterials(Integer id) {
        Material material =materialRepository.findMaterialById(id);
        if(material ==null)
            throw new ApiException("Materials not found: " + id);
        materialRepository.delete(material);
    }

    public void update(Integer id, Material material) {
        Material oldMaterial =materialRepository.findMaterialById(id);
        if(material ==null)
            throw new ApiException("Materials not found: " + id);
        oldMaterial.setName(material.getName());
        oldMaterial.setQuantityUsed(material.getQuantityUsed());
        oldMaterial.setUnitCost(material.getUnitCost());
        materialRepository.save(oldMaterial);
        }

    // get all materials for a request

    public List<Material> getMaterialsByRequest(Integer requestId) {
        if(requestRepository.findMaintenanceRequestById(requestId)==null)
                throw new ApiException("Request not found: " );
        if(materialRepository.findByRequestId(requestId).isEmpty())
            throw new ApiException("No materials found for this request");
        return materialRepository.findByRequestId(requestId);
    }

}
