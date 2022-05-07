package com.marcura.test.repository;

import com.marcura.test.model.Rates;
import com.marcura.test.model.Spread;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SpreadRepository extends JpaRepository<Spread, Long> {

    Optional<Spread> findByCurrency(String currency);
}