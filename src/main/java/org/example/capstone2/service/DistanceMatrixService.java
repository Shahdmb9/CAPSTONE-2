package org.example.capstone2.service;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixElement;
import lombok.RequiredArgsConstructor;
import org.example.capstone2.ApiResponse.ApiException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DistanceMatrixService {


    final private GeoApiContext geoApiContext;

     //get distance between two locations
    public Double getDistance(String origin, String destination) {
        try {
            DistanceMatrix distanceMatrix = DistanceMatrixApi
                    .newRequest(geoApiContext)
                    .origins("Riyadh "+origin)
                    .destinations("Riyadh "+destination)
                    .await();
            if (distanceMatrix.rows.length > 0 && distanceMatrix.rows[0].elements.length > 0) {
                DistanceMatrixElement element = distanceMatrix.rows[0].elements[0];

                // get distance in km as string
                String distance=element.distance.humanReadable;
                //returning the distance as double value
                return Double.parseDouble(distance.substring(0,distance.indexOf(" ")));
            }
            return null;
        } catch (Exception e) {
            throw new ApiException("Could not get distance: " + e.getMessage());
        }
    }
}
