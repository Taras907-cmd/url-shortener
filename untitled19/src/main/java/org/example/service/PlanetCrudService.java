package org.example.service;

import org.example.dao.GenericDao;
import org.example.entity.Planet;

public class PlanetCrudService {

    private final GenericDao<Planet> planetDao = new GenericDao<>(Planet.class);

    public void savePlanet(Planet planet) {
        planetDao.save(planet);
    }

    public void updatePlanet(Planet planet) {
        planetDao.update(planet);
    }

    public void deletePlanet(Planet planet) {
        planetDao.delete(planet);
    }

    public Planet findPlanetById(String id) {
        return planetDao.findById(id);
    }
}