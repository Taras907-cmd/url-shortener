package org.example.service;

import org.example.dao.GenericDao;
import org.example.entity.Client;
import org.example.entity.Planet;
import org.example.entity.Ticket;

public class TicketCrudService {

    private final GenericDao<Ticket> ticketDao = new GenericDao<>(Ticket.class);
    private final GenericDao<Client> clientDao = new GenericDao<>(Client.class);
    private final GenericDao<Planet> planetDao = new GenericDao<>(Planet.class);

    public void saveTicket(Ticket ticket) {
        validateClient(ticket.getClient());
        validatePlanet(ticket.getFromPlanet(), "from planet");
        validatePlanet(ticket.getToPlanet(), "to planet");

        ticketDao.save(ticket);
    }

    public void updateTicket(Ticket ticket) {
        validateClient(ticket.getClient());
        validatePlanet(ticket.getFromPlanet(), "from planet");
        validatePlanet(ticket.getToPlanet(), "to planet");

        ticketDao.update(ticket);
    }

    public void deleteTicket(Ticket ticket) {
        ticketDao.delete(ticket);
    }

    public Ticket findTicketById(int id) {
        return ticketDao.findById(id);
    }

    private void validateClient(Client client) {
        if (client == null) {
            throw new IllegalArgumentException("Client must not be null");
        }
        Client existingClient = clientDao.findById(client.getId());
        if (existingClient == null) {
            throw new IllegalArgumentException("Client with id " + client.getId() + " does not exist");
        }
    }

    private void validatePlanet(Planet planet, String fieldName) {
        if (planet == null) {
            throw new IllegalArgumentException(fieldName + " must not be null");
        }
        Planet existingPlanet = planetDao.findById(planet.getId());
        if (existingPlanet == null) {
            throw new IllegalArgumentException(fieldName + " with id " + planet.getId() + " does not exist");
        }
    }
}