package com.marcura.test.repository;

import com.marcura.test.model.Rates;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.time.LocalDate;
import java.util.Set;

@Repository
public interface RatesRepository extends JpaRepository<Rates, Long> {

    Set<Rates> findByDate(LocalDate parse);

    @Lock(value = LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "3000")})
    Rates findByCurrencyAndDate(String from, LocalDate date);

    @Lock(value = LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "3000")})
    @Query("select max(r.date) from Rates r")
    LocalDate findLatestDate();
}