package org.example.service;

import org.example.dao.GenericDao;
import org.example.entity.Client;

public class ClientCrudService {

    private final GenericDao<Client> clientDao = new GenericDao<>(Client.class);

    public void saveClient(Client client) {
        clientDao.save(client);
    }

    public void updateClient(Client client) {
        clientDao.update(client);
    }

    public void deleteClient(Client client) {
        clientDao.delete(client);
    }

    public Client findClientById(int id) {
        return clientDao.findById(id);
    }
}