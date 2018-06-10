package it.techn.gumeh.repository;

import it.techn.gumeh.domain.Resurce;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Resurce entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ResurceRepository extends JpaRepository<Resurce, Long> {

}
