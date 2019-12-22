package com.space.service;

import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class ShipServiceImpl implements ShipService {

    @Autowired
    private ShipRepository shipRepository;

    @Override
    public ResponseEntity<?> getShipsList(
            String name,
            String planet,
            ShipType shipType,
            Long after,
            Long before,
            Boolean isUsed,
            Double minSpeed,
            Double maxSpeed,
            Integer minCrewSize,
            Integer maxCrewSize,
            Double minRating,
            Double maxRating,
            ShipOrder order,
            Integer pageNumber,
            Integer pageSize
    ) {
        Page<Ship> page = shipRepository.findAll((Specification<Ship>) (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (name != null) {
                predicates.add(criteriaBuilder.and(criteriaBuilder.like(root.get("name"), "%" + name + "%")));
            }
            if (planet != null) {
                predicates.add(criteriaBuilder.and(criteriaBuilder.like(root.get("planet"), "%" + planet + "%")));
            }
            if (shipType != null) {
                predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("shipType"), shipType)));
            }
            if (after != null) {
                Date dateAfter = new Date(after - 3600000);
                predicates.add(criteriaBuilder.and(criteriaBuilder.greaterThan(root.get("prodDate"), dateAfter)));
            }
            if (before != null) {
                Date dateBefore = new Date(before - 3600000);
                predicates.add(criteriaBuilder.and(criteriaBuilder.lessThan(root.get("prodDate"), dateBefore)));
            }
            if (isUsed != null) {
                predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("isUsed"), isUsed)));
            }
            if (minSpeed != null) {
                predicates.add(criteriaBuilder.and(criteriaBuilder.greaterThanOrEqualTo(root.get("speed"), minSpeed)));
            }
            if (maxSpeed != null) {
                predicates.add(criteriaBuilder.and(criteriaBuilder.lessThanOrEqualTo(root.get("speed"), maxSpeed)));
            }
            if (minCrewSize != null) {
                predicates.add(criteriaBuilder.and(criteriaBuilder.greaterThanOrEqualTo(root.get("crewSize"), minCrewSize)));
            }
            if (maxCrewSize != null) {
                predicates.add(criteriaBuilder.and(criteriaBuilder.lessThanOrEqualTo(root.get("crewSize"), maxCrewSize)));
            }
            if (minRating != null) {
                predicates.add(criteriaBuilder.and(criteriaBuilder.greaterThanOrEqualTo(root.get("rating"), minRating)));
            }
            if (maxRating != null) {
                predicates.add(criteriaBuilder.and(criteriaBuilder.lessThanOrEqualTo(root.get("rating"), maxRating)));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        }, new PageRequest(pageNumber, pageSize, Sort.by(order.getFieldName())));

        return new ResponseEntity<>(page.getContent(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getShipsCount(
            String name,
            String planet,
            ShipType shipType,
            Long after,
            Long before,
            Boolean isUsed,
            Double minSpeed,
            Double maxSpeed,
            Integer minCrewSize,
            Integer maxCrewSize,
            Double minRating,
            Double maxRating) {
        Integer count = Math.toIntExact(shipRepository.count((Specification<Ship>) (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (name != null) {
                predicates.add(criteriaBuilder.and(criteriaBuilder.like(root.get("name"), "%" + name + "%")));
            }
            if (planet != null) {
                predicates.add(criteriaBuilder.and(criteriaBuilder.like(root.get("planet"), "%" + planet + "%")));
            }
            if (shipType != null) {
                predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("shipType"), shipType)));
            }
            if (after != null) {
                Date dateAfter = new Date(after - 3600000);
                predicates.add(criteriaBuilder.and(criteriaBuilder.greaterThan(root.get("prodDate"), dateAfter)));
            }
            if (before != null) {
                Date dateBefore = new Date(before - 3600000);
                predicates.add(criteriaBuilder.and(criteriaBuilder.lessThan(root.get("prodDate"), dateBefore)));
            }
            if (isUsed != null) {
                predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("isUsed"), isUsed)));
            }
            if (minSpeed != null) {
                predicates.add(criteriaBuilder.and(criteriaBuilder.greaterThanOrEqualTo(root.get("speed"), minSpeed)));
            }
            if (maxSpeed != null) {
                predicates.add(criteriaBuilder.and(criteriaBuilder.lessThanOrEqualTo(root.get("speed"), maxSpeed)));
            }
            if (minCrewSize != null) {
                predicates.add(criteriaBuilder.and(criteriaBuilder.greaterThanOrEqualTo(root.get("crewSize"), minCrewSize)));
            }
            if (maxCrewSize != null) {
                predicates.add(criteriaBuilder.and(criteriaBuilder.lessThanOrEqualTo(root.get("crewSize"), maxCrewSize)));
            }
            if (minRating != null) {
                predicates.add(criteriaBuilder.and(criteriaBuilder.greaterThanOrEqualTo(root.get("rating"), minRating)));
            }
            if (maxRating != null) {
                predicates.add(criteriaBuilder.and(criteriaBuilder.lessThanOrEqualTo(root.get("rating"), maxRating)));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        }));

        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> createShip(Ship ship) {
        if (
                ship.getName() == null ||
                ship.getPlanet() == null ||
                ship.getShipType() == null ||
                ship.getProdDate() == null ||
                ship.getSpeed() == null ||
                ship.getCrewSize() == null
        ) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (ship.getProdDate().getTime() < 0) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        Calendar productionDate = new GregorianCalendar();
        productionDate.setTime(ship.getProdDate());
        BigDecimal speed = new BigDecimal(ship.getSpeed()).setScale(2, RoundingMode.HALF_UP);
        ship.setSpeed(speed.doubleValue());
        if (
                ship.getName().length() > 50 ||
                ship.getPlanet().length() > 50 ||
                ship.getName().isEmpty() ||
                ship.getPlanet().isEmpty() ||
                ship.getSpeed() < 0.01 ||
                ship.getSpeed() > 0.99 ||
                ship.getCrewSize() < 1 ||
                ship.getCrewSize() > 9999 ||
                productionDate.get(Calendar.YEAR) < 2800 ||
                productionDate.get(Calendar.YEAR) > 3019
        ) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (ship.getUsed() == null) ship.setUsed(false);
        BigDecimal rating = getRating(ship, productionDate);
        ship.setRating(rating.doubleValue());
        return new ResponseEntity<>(shipRepository.save(ship), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getShip(String id) {
        if (id == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (id.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Long longId;
        try {
            longId = Long.parseLong(id);
            if (longId < 1) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (NumberFormatException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (!shipRepository.existsById(longId)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(shipRepository.findById(longId).get(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> updateShip(String id, Ship ship) {
        if (id == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (id.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Long longId;
        try {
            longId = Long.parseLong(id);
            if (longId < 1) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (NumberFormatException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (!shipRepository.existsById(longId)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Ship dbShip = shipRepository.findById(longId).get();
        if (ship.getName() != null) {
            if (ship.getName().length() > 50 || ship.getName().isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            dbShip.setName(ship.getName());
        }
        if (ship.getPlanet() != null) {
            if (ship.getPlanet().length() > 50 || ship.getPlanet().isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            dbShip.setPlanet(ship.getPlanet());
        }
        if (ship.getShipType() != null) {
            dbShip.setShipType(ship.getShipType());
        }
        if (ship.getProdDate() != null) {
            if (ship.getProdDate().getTime() < 0) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            dbShip.setProdDate(ship.getProdDate());
        }
        if (ship.getUsed() != null) {
            dbShip.setUsed(ship.getUsed());
        }
        if (ship.getSpeed() != null) {
            if (ship.getSpeed() < 0.01 || ship.getSpeed() > 0.99) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            dbShip.setSpeed(ship.getSpeed());
        }
        if (ship.getCrewSize() != null) {
            if (ship.getCrewSize() < 1 || ship.getCrewSize() > 9999) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            dbShip.setCrewSize(ship.getCrewSize());
        }
        Calendar productionDate = new GregorianCalendar();
        productionDate.setTime(dbShip.getProdDate());
        if (productionDate.get(Calendar.YEAR) < 2800 || productionDate.get(Calendar.YEAR) > 3019) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        BigDecimal rating = getRating(dbShip, productionDate);
        dbShip.setRating(rating.doubleValue());
        return new ResponseEntity<>(shipRepository.save(dbShip), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> deleteShip(String id) {
        if (id == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (id.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Long longId;
        try {
            longId = Long.parseLong(id);
            if (longId < 1) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (NumberFormatException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (!shipRepository.existsById(longId)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        shipRepository.deleteById(longId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private BigDecimal getRating(Ship ship, Calendar productionDate) {
        return new BigDecimal((80 * ship.getSpeed()
                * (ship.getUsed() ? 0.5 : 1))
                / (3019 - productionDate.get(Calendar.YEAR) + 1))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
