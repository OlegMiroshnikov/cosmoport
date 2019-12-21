package com.space.controller;

import com.space.model.Ship;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/rest")
public class ShipController {

    @Autowired
    ShipService shipService;

    @GetMapping(value = "/ships", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Ship>> getShipsList(@RequestParam Map<String, String> allParams) {
        return new ResponseEntity<> (shipService.getShipsList (allParams), HttpStatus.OK);
    }

    @GetMapping(value = "/ships/count", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> getShipsCount(@RequestParam Map<String, String> allParams) {
        return new ResponseEntity<> (shipService.getShipsCount (allParams), HttpStatus.OK);
    }

    @PostMapping(value = "/ships", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Ship> createShip(@RequestBody Ship ship) {
        return new ResponseEntity<> (shipService.createShip (ship), HttpStatus.OK);
    }

    @GetMapping(value = "/ships/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Ship> getShipById(@PathVariable Long id) {
        return new ResponseEntity<> (shipService.getShipById (id), HttpStatus.OK);
    }

    @PostMapping(value = "/ships/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Ship> updateShip(@RequestBody Ship ship,  @PathVariable Long id) {
        return new ResponseEntity<> (shipService.updateShip (ship, id), HttpStatus.OK);
    }

    @DeleteMapping(value = "/ships/{id}")
    public void deleteShip(@PathVariable Long id) {
        shipService.removeShip (id);
    }
}
