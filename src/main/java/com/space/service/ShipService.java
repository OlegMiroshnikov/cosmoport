package com.space.service;

import com.space.model.Ship;

import java.util.List;
import java.util.Map;

public interface ShipService {
    List<Ship> getShipsList(Map<String, String> allParams);
    Long getShipsCount(Map<String, String> allParams);
    Ship createShip (Ship ship);
    Ship getShipById(Long id);
    Ship updateShip (Ship ship, Long id);
    void removeShip (Long id);
}
