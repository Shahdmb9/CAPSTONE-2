package org.example.capstone2.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "userId cannot be null")
    @Column(columnDefinition = "int")
    private Integer userId;

    @NotNull(message = "WorkerId cannot be null")
    @Column(columnDefinition = "int")
    private Integer workerId;

}
