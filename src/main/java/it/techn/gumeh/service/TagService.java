package it.techn.gumeh.service;

import it.techn.gumeh.service.dto.TagDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing Tag.
 */
public interface TagService {

    /**
     * Save a tag.
     *
     * @param tagDTO the entity to save
     * @return the persisted entity
     */
    TagDTO save(TagDTO tagDTO);

    /**
     * Get all the tags.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<TagDTO> findAll(Pageable pageable);

    /**
     * Get the "id" tag.
     *
     * @param id the id of the entity
     * @return the entity
     */
    TagDTO findOne(Long id);

    /**
     * Delete the "id" tag.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the tag corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<TagDTO> search(String query, Pageable pageable);
}
