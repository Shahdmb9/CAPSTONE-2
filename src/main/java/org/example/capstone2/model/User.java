package org.example.capstone2.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty
    @Size(min = 3, message = "Name must be at least 3 characters long")
    @Column(columnDefinition = "VARCHAR(20)")
    private String name;

    @NotEmpty(message = "Phone cannot be empty")
//    @Column(columnDefinition = "VARCHAR(15) UNIQUE")
    @Column(columnDefinition = "VARCHAR(15) ")
    @Min(value = 10,message = "Phone number must be at least 10 digits")
    private String phone;

    @Email
    @NotEmpty(message = "Email cannot be empty")
    @Column(columnDefinition = "VARCHAR(40) UNIQUE")
    private String email;

    @NotEmpty(message = "Password cannot be empty")
    @Column(columnDefinition = "varchar(40)")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$",message = "password must contain small and capital" +
            " letters and numbers and must be more than 8 character")
    private String password;

    @NotEmpty(message = "District cannot be null")
    @Column(columnDefinition = "VARCHAR(40)")
    private String district;

    @NotEmpty(message = "apartment number cannot be null")
    @Column(columnDefinition = "VARCHAR(20)")
    private String apartment;

    @NotEmpty(message = "address cannot be null")
    @Column(columnDefinition = "VARCHAR(30)")
    private String address;


    @Pattern(regexp = "(ADMIN|USER)")
    @Column(columnDefinition = "VARCHAR(10)")
    private String role;

    @Pattern(regexp = "(FREE|PREMIUM)")
    @Column(columnDefinition = "VARCHAR(10)")
    @NotEmpty(message = "subscription type cannot be null")
    private String subscriptionType;

}
