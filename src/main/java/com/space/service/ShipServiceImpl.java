package com.space.service;

import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class ShipServiceImpl implements ShipService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ShipRepepository shipRepepository;
    @Autowired
    private ShipValidator shipValidator;

    @Override
    @Transactional
    public List<Ship> getShipsList(Map<String, String> allParams) {
        int pageNumber = allParams.containsKey ("pageNumber") ? Integer.parseInt (allParams.get ("pageNumber")) : 0;
        int pageSize = allParams.containsKey ("pageSize") ? Integer.parseInt (allParams.get ("pageSize")) : 3;
        return entityManager.createQuery (createCriteriaQuery (allParams))
                .setFirstResult (pageNumber * pageSize)
                .setMaxResults (pageSize)
                .getResultList ();
    }

    @Override
    @Transactional
    public Long getShipsCount(Map<String, String> allParams) {
        return entityManager.createQuery (createCountCriteriaQuery (allParams))
                .getSingleResult ();
    }

    @Override
    @Transactional
    public Ship createShip(Ship ship) {
        shipValidator.validateShip (ship);
        if (ship.getUsed () == null) {
            ship.setUsed (false);
        }
        ship.setRating (calculateRating (ship));
        return shipRepepository.saveAndFlush (ship);
    }

    @Override
    @Transactional
    public Ship getShipById(Long id) {
        if (id <= 0) {
            throw new IllegalArgumentException ("Ship id must be greater then 0");
        }
        return shipRepepository.findById (id)
                .orElseThrow (() -> new ShipNotFoundException ("Ship by id is not exist"));
    }

    @Override
    @Transactional
    public Ship updateShip(Ship ship, Long id) {
        Ship original = this.getShipById (id);
        if (ship.getName () != null) {
            original.setName (ship.getName ());
        }
        if (ship.getPlanet () != null) {
            original.setPlanet (ship.getPlanet ());
        }
        if (ship.getShipType () != null) {
            original.setShipType (ship.getShipType ());
        }
        if (ship.getProdDate () != null) {
            original.setProdDate (ship.getProdDate ());
        }
        if (ship.getUsed () != null) {
            original.setUsed (ship.getUsed ());
        }
        if (ship.getSpeed () != null) {
            original.setSpeed (ship.getSpeed ());
        }
        if (ship.getCrewSize () != null) {
            original.setCrewSize (ship.getCrewSize ());
        }
        shipValidator.validateShip (original);
        original.setRating (calculateRating (original));
        return shipRepepository.saveAndFlush (original);
    }

    @Override
    @Transactional
    public void removeShip(Long id) {
        Ship original = this.getShipById (id);
        shipRepepository.deleteById (id);
    }

    private Double calculateRating(Ship ship) {
        Calendar calendar = Calendar.getInstance ();
        calendar.setTime (ship.getProdDate ());
        Double result = (80 * ship.getSpeed () * (ship.getUsed () ? 0.5 : 1.0)) /
                (3019 - calendar.get (Calendar.YEAR) + 1);
        return new BigDecimal (Double.toString (result))
                .setScale (2, RoundingMode.HALF_EVEN)
                .doubleValue ();
    }

    private CriteriaQuery<Ship> createCriteriaQuery(Map<String, String> allParams) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder ();
        CriteriaQuery<Ship> criteriaQuery = criteriaBuilder.createQuery (Ship.class);
        Root<Ship> root = criteriaQuery.from (Ship.class);
        List<Predicate> predicates = createQueryWhereClause (allParams, criteriaBuilder, root);
        criteriaQuery.where (criteriaBuilder.and (predicates.toArray (new Predicate[predicates.size ()])));
        String order = "id";
        if (allParams.containsKey ("order")) {
            order = ShipOrder.valueOf (allParams.get ("order")).getFieldName ();
        }
        criteriaQuery.orderBy (criteriaBuilder.asc (root.get (order)));
        return criteriaQuery;
    }

    private CriteriaQuery<Long> createCountCriteriaQuery(Map<String, String> allParams) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder ();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery (Long.class);
        Root<Ship> root = criteriaQuery.from (Ship.class);
        List<Predicate> predicates = createQueryWhereClause (allParams, criteriaBuilder, root);
        criteriaQuery.where (criteriaBuilder.and (predicates.toArray (new Predicate[predicates.size ()])));
        criteriaQuery.select (criteriaBuilder.count (root));
        return criteriaQuery;
    }

    private List<Predicate> createQueryWhereClause(Map<String, String> allParams, CriteriaBuilder criteriaBuilder, Root<Ship> root) {
        List<Predicate> predicates = new ArrayList<Predicate> ();
        Calendar calendar = Calendar.getInstance ();
        for (Map.Entry<String, String> e : allParams.entrySet ()) {
            String key = e.getKey ();
            String value = e.getValue ();
            if ((key != null) && (value != null)) {
                switch (key) {
                    case "name":
                        predicates.add (criteriaBuilder.like (root.get ("name"), "%" + value + "%"));
                        break;
                    case "planet":
                        predicates.add (criteriaBuilder.like (root.get ("planet"), "%" + value + "%"));
                        break;
                    case "shipType":
                        predicates.add (criteriaBuilder.equal (root.get ("shipType"), ShipType.valueOf (value)));
                        break;
                    case "after":
                        predicates.add (criteriaBuilder.greaterThanOrEqualTo
                                (root.get ("prodDate"), new Date (Long.parseLong (value))));
                        break;
                    case "before":
                        predicates.add (criteriaBuilder.lessThan
                                (root.get ("prodDate"), new Date (Long.parseLong (value))));
                        break;
                    case "isUsed":
                        predicates.add (criteriaBuilder.equal (root.get ("isUsed"), Boolean.parseBoolean (value)));
                        break;
                    case "minSpeed":
                        predicates.add (criteriaBuilder.greaterThanOrEqualTo (root.get ("speed"), Double.parseDouble (value)));
                        break;
                    case "maxSpeed":
                        predicates.add (criteriaBuilder.lessThanOrEqualTo (root.get ("speed"), Double.parseDouble (value)));
                        break;
                    case "minCrewSize":
                        predicates.add (criteriaBuilder.greaterThanOrEqualTo (root.get ("crewSize"), Integer.parseInt (value)));
                        break;
                    case "maxCrewSize":
                        predicates.add (criteriaBuilder.lessThanOrEqualTo (root.get ("crewSize"), Integer.parseInt (value)));
                        break;
                    case "minRating":
                        predicates.add (criteriaBuilder.greaterThanOrEqualTo (root.get ("rating"), Double.parseDouble (value)));
                        break;
                    case "maxRating":
                        predicates.add (criteriaBuilder.lessThanOrEqualTo (root.get ("rating"), Double.parseDouble (value)));
                        break;
                }
            }
        }
        return predicates;
    }

}