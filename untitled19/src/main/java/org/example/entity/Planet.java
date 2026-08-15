package org.example.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Planet")
public class Planet {

    @Id
    @Column(length = 10)
    private String id;

    @Column(nullable = false, length = 500)
    private String name;
}