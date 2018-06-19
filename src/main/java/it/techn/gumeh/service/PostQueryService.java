package it.techn.gumeh.service;


import io.github.jhipster.service.QueryService;
import it.techn.gumeh.domain.Post;
import it.techn.gumeh.domain.metamodels.Post_;
import it.techn.gumeh.domain.metamodels.Resurce_;
import it.techn.gumeh.repository.PostRepository;
import it.techn.gumeh.repository.search.PostSearchRepository;
import it.techn.gumeh.service.dto.PostCriteria;
import it.techn.gumeh.service.dto.PostDTO;
import it.techn.gumeh.service.mapper.PostMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for executing complex queries for Post entities in the database.
 * The main input is a {@link PostCriteria} which get's converted to {@link Specifications},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link PostDTO} or a {@link Page} of {@link PostDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PostQueryService extends QueryService<Post> {

    private final Logger log = LoggerFactory.getLogger(PostQueryService.class);


    private final PostRepository postRepository;

    private final PostMapper postMapper;

    private final PostSearchRepository postSearchRepository;

    public PostQueryService(PostRepository postRepository, PostMapper postMapper, PostSearchRepository postSearchRepository) {
        this.postRepository = postRepository;
        this.postMapper = postMapper;
        this.postSearchRepository = postSearchRepository;
    }

    /**
     * Return a {@link List} of {@link PostDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<PostDTO> findByCriteria(PostCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specifications<Post> specification = createSpecification(criteria);
        return postMapper.toDto(postRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link PostDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<PostDTO> findByCriteria(PostCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specifications<Post> specification = createSpecification(criteria);
        final Page<Post> result = postRepository.findAll(specification, page);
        return result.map(postMapper::toDto);
    }

    /**
     * Function to convert PostCriteria to a {@link Specifications}
     */
    private Specifications<Post> createSpecification(PostCriteria criteria) {
        Specifications<Post> specification = Specifications.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Post_.id));
            }
            if (criteria.getResourceBrief() != null) {
                specification = specification.and(buildStringSpecification(criteria.getResourceBrief(), Post_.resourceBrief));
            }
            if (criteria.getNoLikes() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getNoLikes(), Post_.noLikes));
            }
            if (criteria.getTagStr() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTagStr(), Post_.tagStr));
            }
            if (criteria.getUserBrief() != null) {
                specification = specification.and(buildStringSpecification(criteria.getUserBrief(), Post_.userBrief));
            }
            if (criteria.getLink() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLink(), Post_.link));
            }
            if (criteria.getText() != null) {
                specification = specification.and(buildStringSpecification(criteria.getText(), Post_.text));
            }
            if (criteria.getCategoryId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getCategoryId(), Post_.category, Resurce_.id));
            }
        }
        return specification;
    }

}
