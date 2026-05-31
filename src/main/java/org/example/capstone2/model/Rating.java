package org.example.capstone2.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;


@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Min(1)
    @Max(5)
    @Column(columnDefinition = "INT")
    private Integer score;


    @Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime ratedAt;

    @Column(columnDefinition = "INT")
    private Integer workerId;

    @Column(columnDefinition = "INT")
    private Integer userId;

    @Column(unique = true)
    private Integer requestId;

}
