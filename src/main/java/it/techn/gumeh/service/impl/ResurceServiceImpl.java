package it.techn.gumeh.service.impl;

import it.techn.gumeh.domain.Resurce;
import it.techn.gumeh.domain.User;
import it.techn.gumeh.repository.ResurceRepository;
import it.techn.gumeh.repository.search.ResurceSearchRepository;
import it.techn.gumeh.service.ResurceService;
import it.techn.gumeh.service.UserService;
import it.techn.gumeh.service.dto.ResurceDTO;
import it.techn.gumeh.service.mapper.ResurceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

/**
 * Service Implementation for managing Resurce.
 */
@Service
@Transactional
public class ResurceServiceImpl implements ResurceService {

    private final Logger log = LoggerFactory.getLogger(ResurceServiceImpl.class);

    private final ResurceRepository resurceRepository;

    private final ResurceMapper resurceMapper;

    private final ResurceSearchRepository resurceSearchRepository;

    private final UserService userService;

    public ResurceServiceImpl(ResurceRepository resurceRepository, ResurceMapper resurceMapper, ResurceSearchRepository resurceSearchRepository, UserService userService) {
        this.resurceRepository = resurceRepository;
        this.resurceMapper = resurceMapper;
        this.resurceSearchRepository = resurceSearchRepository;
        this.userService = userService;
    }

    /**
     * Save a resurce.
     *
     * @param resurceDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public ResurceDTO save(ResurceDTO resurceDTO) {
        log.debug("Request to save Resurce : {}", resurceDTO);
        Resurce resurce = resurceMapper.toEntity(resurceDTO);
        Optional<User> currentUserOptional = userService.getUserWithAuthorities();
        User currentUser = null;
        if(currentUserOptional.isPresent()) {
            currentUser = currentUserOptional.get();
//        else throw new NoLoginUserException("No user is log in right now");

            resurce.setCreator(currentUser.getFirstName() + "-" + currentUser.getLastName());
        } else resurce.setCreator("Anonymous");
        resurce = resurceRepository.save(resurce);
        ResurceDTO result = resurceMapper.toDto(resurce);
        resurceSearchRepository.save(resurce);
        return result;
    }

    /**
     * Get all the resurces.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ResurceDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Resurces");
        return resurceRepository.findAll(pageable)
            .map(resurceMapper::toDto);
    }

    /**
     * Get one resurce by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public ResurceDTO findOne(Long id) {
        log.debug("Request to get Resurce : {}", id);
        Resurce resurce = resurceRepository.findOne(id);
        return resurceMapper.toDto(resurce);
    }

    /**
     * Delete the resurce by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Resurce : {}", id);
        resurceRepository.delete(id);
        resurceSearchRepository.delete(id);
    }

    /**
     * Search for the resurce corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ResurceDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Resurces for query {}", query);
        Page<Resurce> result = resurceSearchRepository.search(queryStringQuery(query), pageable);
        return result.map(resurceMapper::toDto);
    }
}
