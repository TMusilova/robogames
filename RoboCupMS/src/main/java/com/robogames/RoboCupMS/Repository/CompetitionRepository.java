package com.robogames.RoboCupMS.Repository;

import java.util.Optional;

import com.robogames.RoboCupMS.Entity.Competition;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repozitar pro souteze
 */
@Repository
public interface CompetitionRepository extends JpaRepository<Competition, Long> {

    Optional<Competition> findByYear(int year);

}
