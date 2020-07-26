package com.upgrade.challenge.api.repository;


import com.upgrade.challenge.api.entity.ReservedDay;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservedDayRepository extends CrudRepository<ReservedDay, Long> {

    @Query("SELECT r FROM ReservedDay r WHERE r.date BETWEEN :initialDate AND :endDate")
    List<ReservedDay> findReservedDaysBetweenDates(@Param("initialDate") LocalDate initialDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT r FROM ReservedDay r WHERE r.date = :date")
    Optional<ReservedDay> findByDate(@Param("date") LocalDate date);
}
