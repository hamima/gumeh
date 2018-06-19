package it.techn.gumeh.service;

import it.techn.gumeh.service.dto.FollowershipDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing Followership.
 */
public interface FollowershipService {

    /**
     * Save a followership.
     *
     * @param followershipDTO the entity to save
     * @return the persisted entity
     */
    FollowershipDTO save(FollowershipDTO followershipDTO);

    /**
     * Get all the followerships.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<FollowershipDTO> findAll(Pageable pageable);

    /**
     * Get the "id" followership.
     *
     * @param id the id of the entity
     * @return the entity
     */
    FollowershipDTO findOne(Long id);

    /**
     * Delete the "id" followership.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the followership corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<FollowershipDTO> search(String query, Pageable pageable);
}
