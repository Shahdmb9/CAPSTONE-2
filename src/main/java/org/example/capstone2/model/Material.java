package org.example.capstone2.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Request ID cannot be null")
    @Column(columnDefinition = "INT")
    private Integer requestId;

    @NotEmpty(message = "Material name cannot be empty")
    @Size(min = 3, message = "Material name must be at least 3 characters long")
    @Column(columnDefinition = "VARCHAR(40)")
    private String name;

    @PositiveOrZero
    @NotNull
    @Column(columnDefinition = "INT ")
    private Integer quantityUsed;


    @Positive
    @NotNull
    @Column(columnDefinition = "DOUBLE ")
    private Double unitCost;

}
