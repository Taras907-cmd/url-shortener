package org.example.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@EqualsAndHashCode(exclude = {"departureTickets", "arrivalTickets"})
@ToString(exclude = {"departureTickets", "arrivalTickets"})
@Entity
@Table(name = "Planet")
public class Planet {

    @Id
    @Column(length = 10)
    private String id;

    @Column(nullable = false, length = 500)
    private String name;

    @OneToMany(mappedBy = "fromPlanet", fetch = FetchType.LAZY)
    private List<Ticket> departureTickets;

    @OneToMany(mappedBy = "toPlanet", fetch = FetchType.LAZY)
    private List<Ticket> arrivalTickets;
}