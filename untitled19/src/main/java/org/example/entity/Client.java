package org.example.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@EqualsAndHashCode(exclude = "tickets")
@ToString(exclude = "tickets")
@Entity
@Table(name = "Client")

public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String name;

    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY)
    private List<Ticket> tickets;
}
