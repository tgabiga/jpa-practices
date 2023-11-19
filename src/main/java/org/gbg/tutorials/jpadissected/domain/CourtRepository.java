package org.gbg.tutorials.jpadissected.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CourtRepository extends JpaRepository<Court, UUID> {

}
