package it.techn.gumeh.web.rest;

import com.codahale.metrics.annotation.Timed;
import it.techn.gumeh.service.ResurceService;
import it.techn.gumeh.web.rest.errors.BadRequestAlertException;
import it.techn.gumeh.web.rest.util.HeaderUtil;
import it.techn.gumeh.web.rest.util.PaginationUtil;
import it.techn.gumeh.service.dto.ResurceDTO;
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
 * REST controller for managing Resurce.
 */
@RestController
@RequestMapping("/api")
public class ResurceResource {

    private final Logger log = LoggerFactory.getLogger(ResurceResource.class);

    private static final String ENTITY_NAME = "resurce";

    private final ResurceService resurceService;

    public ResurceResource(ResurceService resurceService) {
        this.resurceService = resurceService;
    }

    /**
     * POST  /resurces : Create a new resurce.
     *
     * @param resurceDTO the resurceDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new resurceDTO, or with status 400 (Bad Request) if the resurce has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/resurces")
    @Timed
    public ResponseEntity<ResurceDTO> createResurce(@Valid @RequestBody ResurceDTO resurceDTO) throws URISyntaxException {
        log.debug("REST request to save Resurce : {}", resurceDTO);
        if (resurceDTO.getId() != null) {
            throw new BadRequestAlertException("A new resurce cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ResurceDTO result = resurceService.save(resurceDTO);
        return ResponseEntity.created(new URI("/api/resurces/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /resurces : Updates an existing resurce.
     *
     * @param resurceDTO the resurceDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated resurceDTO,
     * or with status 400 (Bad Request) if the resurceDTO is not valid,
     * or with status 500 (Internal Server Error) if the resurceDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/resurces")
    @Timed
    public ResponseEntity<ResurceDTO> updateResurce(@Valid @RequestBody ResurceDTO resurceDTO) throws URISyntaxException {
        log.debug("REST request to update Resurce : {}", resurceDTO);
        if (resurceDTO.getId() == null) {
            return createResurce(resurceDTO);
        }
        ResurceDTO result = resurceService.save(resurceDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, resurceDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /resurces : get all the resurces.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of resurces in body
     */
    @GetMapping("/resurces")
    @Timed
    public ResponseEntity<List<ResurceDTO>> getAllResurces(Pageable pageable) {
        log.debug("REST request to get a page of Resurces");
        Page<ResurceDTO> page = resurceService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/resurces");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /resurces/:id : get the "id" resurce.
     *
     * @param id the id of the resurceDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the resurceDTO, or with status 404 (Not Found)
     */
    @GetMapping("/resurces/{id}")
    @Timed
    public ResponseEntity<ResurceDTO> getResurce(@PathVariable Long id) {
        log.debug("REST request to get Resurce : {}", id);
        ResurceDTO resurceDTO = resurceService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(resurceDTO));
    }

    /**
     * DELETE  /resurces/:id : delete the "id" resurce.
     *
     * @param id the id of the resurceDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/resurces/{id}")
    @Timed
    public ResponseEntity<Void> deleteResurce(@PathVariable Long id) {
        log.debug("REST request to delete Resurce : {}", id);
        resurceService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/resurces?query=:query : search for the resurce corresponding
     * to the query.
     *
     * @param query the query of the resurce search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/resurces")
    @Timed
    public ResponseEntity<List<ResurceDTO>> searchResurces(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Resurces for query {}", query);
        Page<ResurceDTO> page = resurceService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/resurces");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
