package org.example.capstone2.controller;


import lombok.RequiredArgsConstructor;
import org.example.capstone2.service.DistanceMatrixService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/distance")
@RequiredArgsConstructor
public class DMSC {

    private final DistanceMatrixService distanceMatrixService;

    @GetMapping("/get/{o}/{d}")
    public ResponseEntity<?> getDistance(@PathVariable String o, @PathVariable String d){
        return ResponseEntity.status(200).body(distanceMatrixService.getDistance(o,d));
    }
}
