package it.techn.gumeh.repository;

import it.techn.gumeh.domain.Activity;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;
import java.util.List;

/**
 * Spring Data JPA repository for the Activity entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long>, JpaSpecificationExecutor<Activity> {

    @Query("select activity from Activity activity where activity.user.login = ?#{principal.username}")
    List<Activity> findByUserIsCurrentUser();

}
