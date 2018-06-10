package it.techn.gumeh.service;

import it.techn.gumeh.service.dto.ResurceDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing Resurce.
 */
public interface ResurceService {

    /**
     * Save a resurce.
     *
     * @param resurceDTO the entity to save
     * @return the persisted entity
     */
    ResurceDTO save(ResurceDTO resurceDTO);

    /**
     * Get all the resurces.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<ResurceDTO> findAll(Pageable pageable);

    /**
     * Get the "id" resurce.
     *
     * @param id the id of the entity
     * @return the entity
     */
    ResurceDTO findOne(Long id);

    /**
     * Delete the "id" resurce.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the resurce corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<ResurceDTO> search(String query, Pageable pageable);
}
