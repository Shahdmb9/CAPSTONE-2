package org.example.capstone2.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty(message = "Title cannot be empty")
    @Column(columnDefinition = "VARCHAR(40)")
    @Size(min = 3, message = "Title must be at least 3 characters long")
    private String title;

    @NotEmpty(message = "Description cannot be empty")
    @Column(columnDefinition = "TEXT")
    @Size(min = 10, message = "Description must be at least 10 characters long")
    private String description;

//    @NotNull(message = "categoryId cannot be empty")
    @Column(columnDefinition = "INT")
    private Integer categoryId;

    @Pattern(regexp = "(PENDING|ASSIGNED|IN_PROGRESS|RESOLVED|CANCELLED)",message = "Status must be PENDING, ASSIGNED, IN_PROGRESS, RESOLVED or CANCELLED")
    @Column(columnDefinition = "VARCHAR(20)")
    private String status ;


    @Column(columnDefinition = "VARCHAR(30)")
    @NotEmpty(message = "Assigning Method cannot be empty")
    @Pattern(regexp = "(Geographic Proximity|Highest Rating|User Selected|Open)",message = "Assigning Method must be Geographic Proximity, Highest Rating, USER CHOICE or Open")
    private String AssigningMethod;

    @Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime updatedAt;

    @Column(columnDefinition = "Boolean DEFAULT FALSE",insertable = false)
    private Boolean urgent = false;

    @Column(columnDefinition = "INT")
    private Integer userId;

    @Column(columnDefinition = "INT")
    private Integer workerId;

}
