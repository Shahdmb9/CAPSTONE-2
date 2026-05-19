package org.example.capstone2.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.capstone2.ApiResponse.ApiException;
import org.example.capstone2.ApiResponse.ApiResponse;
import org.example.capstone2.model.Material;
import org.example.capstone2.service.MaterialService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/material")
@RequiredArgsConstructor
public class MaterialController {


    private final MaterialService materialService;

    @GetMapping("/get")
    public ResponseEntity<?> getAll(){
        return ResponseEntity.status(200).body(materialService.getAllMaterials());
    }

    @PostMapping("/add")
    public ResponseEntity<?> addMaterial(@RequestBody @Valid Material material){

        materialService.addMaterials(material);
        return ResponseEntity.status(200).body(new ApiResponse("Material added successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id,@RequestBody @Valid Material material){

        materialService.update(id,material);
        return ResponseEntity.status(200).body(new ApiResponse("Material updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id){
        materialService.deleteMaterials(id);
        return ResponseEntity.status(200).body(new ApiResponse("Material deleted successfully"));
    }

    @GetMapping("/get-materials-of-request/{requestid}")
    public ResponseEntity<?> getRequestMaterials(@PathVariable Integer requestid){
       return ResponseEntity.status(200).body(materialService.getMaterialsByRequest(requestid));
    }

    @GetMapping("/get-request-materials-cost/{requestid}")
    public ResponseEntity<?> getMaterialsRequestCost(@PathVariable Integer requestid){
        return ResponseEntity.status(200).body(materialService.getMaterialsCostForRequest(requestid));
    }
}
