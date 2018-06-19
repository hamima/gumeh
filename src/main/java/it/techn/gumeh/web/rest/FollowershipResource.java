package it.techn.gumeh.web.rest;

import com.codahale.metrics.annotation.Timed;
import it.techn.gumeh.service.FollowershipService;
import it.techn.gumeh.web.rest.errors.BadRequestAlertException;
import it.techn.gumeh.web.rest.util.HeaderUtil;
import it.techn.gumeh.web.rest.util.PaginationUtil;
import it.techn.gumeh.service.dto.FollowershipDTO;
import it.techn.gumeh.service.dto.FollowershipCriteria;
import it.techn.gumeh.service.FollowershipQueryService;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Followership.
 */
@RestController
@RequestMapping("/api")
public class FollowershipResource {

    private final Logger log = LoggerFactory.getLogger(FollowershipResource.class);

    private static final String ENTITY_NAME = "followership";

    private final FollowershipService followershipService;

    private final FollowershipQueryService followershipQueryService;

    public FollowershipResource(FollowershipService followershipService, FollowershipQueryService followershipQueryService) {
        this.followershipService = followershipService;
        this.followershipQueryService = followershipQueryService;
    }

    /**
     * POST  /followerships : Create a new followership.
     *
     * @param followershipDTO the followershipDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new followershipDTO, or with status 400 (Bad Request) if the followership has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/followerships")
    @Timed
    public ResponseEntity<FollowershipDTO> createFollowership(@Valid @RequestBody FollowershipDTO followershipDTO) throws URISyntaxException {
        log.debug("REST request to save Followership : {}", followershipDTO);
        if (followershipDTO.getId() != null) {
            throw new BadRequestAlertException("A new followership cannot already have an ID", ENTITY_NAME, "idexists");
        }
        FollowershipDTO result = followershipService.save(followershipDTO);
        return ResponseEntity.created(new URI("/api/followerships/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /followerships : Updates an existing followership.
     *
     * @param followershipDTO the followershipDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated followershipDTO,
     * or with status 400 (Bad Request) if the followershipDTO is not valid,
     * or with status 500 (Internal Server Error) if the followershipDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/followerships")
    @Timed
    public ResponseEntity<FollowershipDTO> updateFollowership(@Valid @RequestBody FollowershipDTO followershipDTO) throws URISyntaxException {
        log.debug("REST request to update Followership : {}", followershipDTO);
        if (followershipDTO.getId() == null) {
            return createFollowership(followershipDTO);
        }
        FollowershipDTO result = followershipService.save(followershipDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, followershipDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /followerships : get all the followerships.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of followerships in body
     */
    @GetMapping("/followerships")
    @Timed
    public ResponseEntity<List<FollowershipDTO>> getAllFollowerships(FollowershipCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Followerships by criteria: {}", criteria);
        Page<FollowershipDTO> page = followershipQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/followerships");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /followerships/:id : get the "id" followership.
     *
     * @param id the id of the followershipDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the followershipDTO, or with status 404 (Not Found)
     */
    @GetMapping("/followerships/{id}")
    @Timed
    public ResponseEntity<FollowershipDTO> getFollowership(@PathVariable Long id) {
        log.debug("REST request to get Followership : {}", id);
        FollowershipDTO followershipDTO = followershipService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(followershipDTO));
    }

    /**
     * DELETE  /followerships/:id : delete the "id" followership.
     *
     * @param id the id of the followershipDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/followerships/{id}")
    @Timed
    public ResponseEntity<Void> deleteFollowership(@PathVariable Long id) {
        log.debug("REST request to delete Followership : {}", id);
        followershipService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/followerships?query=:query : search for the followership corresponding
     * to the query.
     *
     * @param query the query of the followership search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/followerships")
    @Timed
    public ResponseEntity<List<FollowershipDTO>> searchFollowerships(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Followerships for query {}", query);
        Page<FollowershipDTO> page = followershipService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/followerships");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
