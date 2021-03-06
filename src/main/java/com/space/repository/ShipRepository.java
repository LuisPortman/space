package com.space.repository;

import com.space.model.Ship;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShipRepository extends PagingAndSortingRepository<Ship, Long>, JpaSpecificationExecutor<Ship> {
}
