package org.example.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Entity
@Table(name = "Ticket")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "client_id")
    private int clientId;

    @Column(name = "from_planet_id")
    private int fromPlanetId;

    @Column(name = "to_planet_id")
    private int toPlanetId;
}