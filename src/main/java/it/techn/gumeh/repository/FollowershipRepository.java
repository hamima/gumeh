package it.techn.gumeh.repository;

import it.techn.gumeh.domain.Followership;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;
import java.util.List;

/**
 * Spring Data JPA repository for the Followership entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FollowershipRepository extends JpaRepository<Followership, Long>, JpaSpecificationExecutor<Followership> {

    @Query("select followership from Followership followership where followership.user.login = ?#{principal.username}")
    List<Followership> findByUserIsCurrentUser();

}
