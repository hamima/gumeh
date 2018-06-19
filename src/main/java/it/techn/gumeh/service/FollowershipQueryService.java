package it.techn.gumeh.service;


import io.github.jhipster.service.QueryService;
import it.techn.gumeh.domain.Followership;
import it.techn.gumeh.domain.metamodels.Followership_;
import it.techn.gumeh.domain.metamodels.Tag_;
import it.techn.gumeh.domain.metamodels.User_;
import it.techn.gumeh.repository.FollowershipRepository;
import it.techn.gumeh.repository.search.FollowershipSearchRepository;
import it.techn.gumeh.service.dto.FollowershipCriteria;
import it.techn.gumeh.service.dto.FollowershipDTO;
import it.techn.gumeh.service.mapper.FollowershipMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for executing complex queries for Followership entities in the database.
 * The main input is a {@link FollowershipCriteria} which get's converted to {@link Specifications},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link FollowershipDTO} or a {@link Page} of {@link FollowershipDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class FollowershipQueryService extends QueryService<Followership> {

    private final Logger log = LoggerFactory.getLogger(FollowershipQueryService.class);


    private final FollowershipRepository followershipRepository;

    private final FollowershipMapper followershipMapper;

    private final FollowershipSearchRepository followershipSearchRepository;

    public FollowershipQueryService(FollowershipRepository followershipRepository, FollowershipMapper followershipMapper, FollowershipSearchRepository followershipSearchRepository) {
        this.followershipRepository = followershipRepository;
        this.followershipMapper = followershipMapper;
        this.followershipSearchRepository = followershipSearchRepository;
    }

    /**
     * Return a {@link List} of {@link FollowershipDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<FollowershipDTO> findByCriteria(FollowershipCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specifications<Followership> specification = createSpecification(criteria);
        return followershipMapper.toDto(followershipRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link FollowershipDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<FollowershipDTO> findByCriteria(FollowershipCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specifications<Followership> specification = createSpecification(criteria);
        final Page<Followership> result = followershipRepository.findAll(specification, page);
        return result.map(followershipMapper::toDto);
    }

    /**
     * Function to convert FollowershipCriteria to a {@link Specifications}
     */
    private Specifications<Followership> createSpecification(FollowershipCriteria criteria) {
        Specifications<Followership> specification = Specifications.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Followership_.id));
            }
            if (criteria.getCreatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedAt(), Followership_.createdAt));
            }
            if (criteria.getReason() != null) {
                specification = specification.and(buildStringSpecification(criteria.getReason(), Followership_.reason));
            }
            if (criteria.getComment() != null) {
                specification = specification.and(buildStringSpecification(criteria.getComment(), Followership_.comment));
            }
            if (criteria.getUserId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getUserId(), Followership_.user, User_.id));
            }
            if (criteria.getTagId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getTagId(), Followership_.tag, Tag_.id));
            }
        }
        return specification;
    }

}
