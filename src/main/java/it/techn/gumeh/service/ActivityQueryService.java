package it.techn.gumeh.service;


import io.github.jhipster.service.QueryService;
import it.techn.gumeh.domain.Activity;
import it.techn.gumeh.domain.Activity_;
import it.techn.gumeh.domain.Post_;
import it.techn.gumeh.domain.User_;
import it.techn.gumeh.repository.ActivityRepository;
import it.techn.gumeh.repository.search.ActivitySearchRepository;
import it.techn.gumeh.service.dto.ActivityCriteria;
import it.techn.gumeh.service.dto.ActivityDTO;
import it.techn.gumeh.service.mapper.ActivityMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for executing complex queries for Activity entities in the database.
 * The main input is a {@link ActivityCriteria} which get's converted to {@link Specifications},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ActivityDTO} or a {@link Page} of {@link ActivityDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ActivityQueryService extends QueryService<Activity> {

    private final Logger log = LoggerFactory.getLogger(ActivityQueryService.class);


    private final ActivityRepository activityRepository;

    private final ActivityMapper activityMapper;

    private final ActivitySearchRepository activitySearchRepository;

    public ActivityQueryService(ActivityRepository activityRepository, ActivityMapper activityMapper, ActivitySearchRepository activitySearchRepository) {
        this.activityRepository = activityRepository;
        this.activityMapper = activityMapper;
        this.activitySearchRepository = activitySearchRepository;
    }

    /**
     * Return a {@link List} of {@link ActivityDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ActivityDTO> findByCriteria(ActivityCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specifications<Activity> specification = createSpecification(criteria);
        return activityMapper.toDto(activityRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link ActivityDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ActivityDTO> findByCriteria(ActivityCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specifications<Activity> specification = createSpecification(criteria);
        final Page<Activity> result = activityRepository.findAll(specification, page);
        return result.map(activityMapper::toDto);
    }

    /**
     * Function to convert ActivityCriteria to a {@link Specifications}
     */
    private Specifications<Activity> createSpecification(ActivityCriteria criteria) {
        Specifications<Activity> specification = Specifications.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Activity_.id));
            }
            if (criteria.getType() != null) {
                specification = specification.and(buildSpecification(criteria.getType(), Activity_.type));
            }
            if (criteria.getCreatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedAt(), Activity_.createdAt));
            }
            if (criteria.getComment() != null) {
                specification = specification.and(buildStringSpecification(criteria.getComment(), Activity_.comment));
            }
            if (criteria.getDeleted() != null) {
                specification = specification.and(buildSpecification(criteria.getDeleted(), Activity_.deleted));
            }
            if (criteria.getReportReason() != null) {
                specification = specification.and(buildStringSpecification(criteria.getReportReason(), Activity_.reportReason));
            }
            if (criteria.getUserBrief() != null) {
                specification = specification.and(buildStringSpecification(criteria.getUserBrief(), Activity_.userBrief));
            }
            if (criteria.getActivityDesc() != null) {
                specification = specification.and(buildStringSpecification(criteria.getActivityDesc(), Activity_.activityDesc));
            }
            if (criteria.getUserId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getUserId(), Activity_.user, User_.id));
            }
            if (criteria.getPostId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getPostId(), Activity_.post, Post_.id));
            }
        }
        return specification;
    }

}
