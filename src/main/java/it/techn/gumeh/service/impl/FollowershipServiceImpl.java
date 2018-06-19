package it.techn.gumeh.service.impl;

import it.techn.gumeh.service.FollowershipService;
import it.techn.gumeh.domain.Followership;
import it.techn.gumeh.repository.FollowershipRepository;
import it.techn.gumeh.repository.search.FollowershipSearchRepository;
import it.techn.gumeh.service.dto.FollowershipDTO;
import it.techn.gumeh.service.mapper.FollowershipMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Followership.
 */
@Service
@Transactional
public class FollowershipServiceImpl implements FollowershipService {

    private final Logger log = LoggerFactory.getLogger(FollowershipServiceImpl.class);

    private final FollowershipRepository followershipRepository;

    private final FollowershipMapper followershipMapper;

    private final FollowershipSearchRepository followershipSearchRepository;

    public FollowershipServiceImpl(FollowershipRepository followershipRepository, FollowershipMapper followershipMapper, FollowershipSearchRepository followershipSearchRepository) {
        this.followershipRepository = followershipRepository;
        this.followershipMapper = followershipMapper;
        this.followershipSearchRepository = followershipSearchRepository;
    }

    /**
     * Save a followership.
     *
     * @param followershipDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public FollowershipDTO save(FollowershipDTO followershipDTO) {
        log.debug("Request to save Followership : {}", followershipDTO);
        Followership followership = followershipMapper.toEntity(followershipDTO);
        followership = followershipRepository.save(followership);
        FollowershipDTO result = followershipMapper.toDto(followership);
        followershipSearchRepository.save(followership);
        return result;
    }

    /**
     * Get all the followerships.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<FollowershipDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Followerships");
        return followershipRepository.findAll(pageable)
            .map(followershipMapper::toDto);
    }

    /**
     * Get one followership by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public FollowershipDTO findOne(Long id) {
        log.debug("Request to get Followership : {}", id);
        Followership followership = followershipRepository.findOne(id);
        return followershipMapper.toDto(followership);
    }

    /**
     * Delete the followership by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Followership : {}", id);
        followershipRepository.delete(id);
        followershipSearchRepository.delete(id);
    }

    /**
     * Search for the followership corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<FollowershipDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Followerships for query {}", query);
        Page<Followership> result = followershipSearchRepository.search(queryStringQuery(query), pageable);
        return result.map(followershipMapper::toDto);
    }
}
