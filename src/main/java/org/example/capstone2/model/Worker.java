package org.example.capstone2.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Worker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty(message = "Name cannot be empty")
    @Column(columnDefinition = "VARCHAR(20)")
    @Size(min = 3, message = "Name must be at least 3 characters long")
    private String name;

    @NotEmpty(message = "Phone cannot be empty")
//    @Column(columnDefinition = "VARCHAR(15)")
    @Column(columnDefinition = "VARCHAR(15) UNIQUE")
    private String phone;

    @NotNull(message = "Email cannot be empty")
    @Column(columnDefinition = "VARCHAR(10)")
    private Integer specialtyAt; //foreign key to category

    @NotNull(message = "baseSalary cannot be empty")
    @Column(columnDefinition = "Double ")
    @Positive
    private Double baseSalary;

    @NotEmpty(message = "District cannot be null")
    @Column(columnDefinition = "VARCHAR(40)")
    private String district;

    @Column(columnDefinition = "Boolean")
    private boolean available;

}
