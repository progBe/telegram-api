package com.example.telegram_rest.repository;

import com.example.telegram_rest.dto.CurrencyType;
import com.example.telegram_rest.entity.CurrencyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CurrencyRepo extends JpaRepository<CurrencyEntity, Long> {
    @Query(value = "SELECT count(*) FROM currency WHERE date >= :startDate AND date <= :endDate AND base = :base", nativeQuery = true)
    Long countAllBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, @Param("base") CurrencyType base);

    @Query(value = "SELECT * FROM currency WHERE date >= :startDate AND date <= :endDate AND base = :base", nativeQuery = true)
    List<CurrencyEntity> getAllBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, @Param("base") CurrencyType base);

    void deleteAllByBase(String base);

    @Query(value = "SELECT * FROM currency WHERE date >= :startDate AND date <= :startDate", nativeQuery = true)
    List<CurrencyEntity> getForDate(@Param("startDate") LocalDateTime startDate);

}
