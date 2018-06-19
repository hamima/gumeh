package it.techn.gumeh.web.rest;

import it.techn.gumeh.GumehApp;

import it.techn.gumeh.domain.Followership;
import it.techn.gumeh.domain.User;
import it.techn.gumeh.domain.Tag;
import it.techn.gumeh.repository.FollowershipRepository;
import it.techn.gumeh.service.FollowershipService;
import it.techn.gumeh.repository.search.FollowershipSearchRepository;
import it.techn.gumeh.service.dto.FollowershipDTO;
import it.techn.gumeh.service.mapper.FollowershipMapper;
import it.techn.gumeh.web.rest.errors.ExceptionTranslator;
import it.techn.gumeh.service.dto.FollowershipCriteria;
import it.techn.gumeh.service.FollowershipQueryService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static it.techn.gumeh.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the FollowershipResource REST controller.
 *
 * @see FollowershipResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GumehApp.class)
public class FollowershipResourceIntTest {

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_REASON = "AAAAAAAAAA";
    private static final String UPDATED_REASON = "BBBBBBBBBB";

    private static final String DEFAULT_COMMENT = "AAAAAAAAAA";
    private static final String UPDATED_COMMENT = "BBBBBBBBBB";

    @Autowired
    private FollowershipRepository followershipRepository;

    @Autowired
    private FollowershipMapper followershipMapper;

    @Autowired
    private FollowershipService followershipService;

    @Autowired
    private FollowershipSearchRepository followershipSearchRepository;

    @Autowired
    private FollowershipQueryService followershipQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restFollowershipMockMvc;

    private Followership followership;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final FollowershipResource followershipResource = new FollowershipResource(followershipService, followershipQueryService);
        this.restFollowershipMockMvc = MockMvcBuilders.standaloneSetup(followershipResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Followership createEntity(EntityManager em) {
        Followership followership = new Followership()
            .createdAt(DEFAULT_CREATED_AT)
            .reason(DEFAULT_REASON)
            .comment(DEFAULT_COMMENT);
        // Add required entity
        User user = UserResourceIntTest.createEntity(em);
        em.persist(user);
        em.flush();
        followership.setUser(user);
        // Add required entity
        Tag tag = TagResourceIntTest.createEntity(em);
        em.persist(tag);
        em.flush();
        followership.setTag(tag);
        return followership;
    }

    @Before
    public void initTest() {
        followershipSearchRepository.deleteAll();
        followership = createEntity(em);
    }

    @Test
    @Transactional
    public void createFollowership() throws Exception {
        int databaseSizeBeforeCreate = followershipRepository.findAll().size();

        // Create the Followership
        FollowershipDTO followershipDTO = followershipMapper.toDto(followership);
        restFollowershipMockMvc.perform(post("/api/followerships")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(followershipDTO)))
            .andExpect(status().isCreated());

        // Validate the Followership in the database
        List<Followership> followershipList = followershipRepository.findAll();
        assertThat(followershipList).hasSize(databaseSizeBeforeCreate + 1);
        Followership testFollowership = followershipList.get(followershipList.size() - 1);
        assertThat(testFollowership.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testFollowership.getReason()).isEqualTo(DEFAULT_REASON);
        assertThat(testFollowership.getComment()).isEqualTo(DEFAULT_COMMENT);

        // Validate the Followership in Elasticsearch
        Followership followershipEs = followershipSearchRepository.findOne(testFollowership.getId());
        assertThat(followershipEs).isEqualToIgnoringGivenFields(testFollowership);
    }

    @Test
    @Transactional
    public void createFollowershipWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = followershipRepository.findAll().size();

        // Create the Followership with an existing ID
        followership.setId(1L);
        FollowershipDTO followershipDTO = followershipMapper.toDto(followership);

        // An entity with an existing ID cannot be created, so this API call must fail
        restFollowershipMockMvc.perform(post("/api/followerships")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(followershipDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Followership in the database
        List<Followership> followershipList = followershipRepository.findAll();
        assertThat(followershipList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllFollowerships() throws Exception {
        // Initialize the database
        followershipRepository.saveAndFlush(followership);

        // Get all the followershipList
        restFollowershipMockMvc.perform(get("/api/followerships?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(followership.getId().intValue())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].reason").value(hasItem(DEFAULT_REASON.toString())))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT.toString())));
    }

    @Test
    @Transactional
    public void getFollowership() throws Exception {
        // Initialize the database
        followershipRepository.saveAndFlush(followership);

        // Get the followership
        restFollowershipMockMvc.perform(get("/api/followerships/{id}", followership.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(followership.getId().intValue()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.reason").value(DEFAULT_REASON.toString()))
            .andExpect(jsonPath("$.comment").value(DEFAULT_COMMENT.toString()));
    }

    @Test
    @Transactional
    public void getAllFollowershipsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        followershipRepository.saveAndFlush(followership);

        // Get all the followershipList where createdAt equals to DEFAULT_CREATED_AT
        defaultFollowershipShouldBeFound("createdAt.equals=" + DEFAULT_CREATED_AT);

        // Get all the followershipList where createdAt equals to UPDATED_CREATED_AT
        defaultFollowershipShouldNotBeFound("createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    public void getAllFollowershipsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        followershipRepository.saveAndFlush(followership);

        // Get all the followershipList where createdAt in DEFAULT_CREATED_AT or UPDATED_CREATED_AT
        defaultFollowershipShouldBeFound("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT);

        // Get all the followershipList where createdAt equals to UPDATED_CREATED_AT
        defaultFollowershipShouldNotBeFound("createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    public void getAllFollowershipsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        followershipRepository.saveAndFlush(followership);

        // Get all the followershipList where createdAt is not null
        defaultFollowershipShouldBeFound("createdAt.specified=true");

        // Get all the followershipList where createdAt is null
        defaultFollowershipShouldNotBeFound("createdAt.specified=false");
    }

    @Test
    @Transactional
    public void getAllFollowershipsByReasonIsEqualToSomething() throws Exception {
        // Initialize the database
        followershipRepository.saveAndFlush(followership);

        // Get all the followershipList where reason equals to DEFAULT_REASON
        defaultFollowershipShouldBeFound("reason.equals=" + DEFAULT_REASON);

        // Get all the followershipList where reason equals to UPDATED_REASON
        defaultFollowershipShouldNotBeFound("reason.equals=" + UPDATED_REASON);
    }

    @Test
    @Transactional
    public void getAllFollowershipsByReasonIsInShouldWork() throws Exception {
        // Initialize the database
        followershipRepository.saveAndFlush(followership);

        // Get all the followershipList where reason in DEFAULT_REASON or UPDATED_REASON
        defaultFollowershipShouldBeFound("reason.in=" + DEFAULT_REASON + "," + UPDATED_REASON);

        // Get all the followershipList where reason equals to UPDATED_REASON
        defaultFollowershipShouldNotBeFound("reason.in=" + UPDATED_REASON);
    }

    @Test
    @Transactional
    public void getAllFollowershipsByReasonIsNullOrNotNull() throws Exception {
        // Initialize the database
        followershipRepository.saveAndFlush(followership);

        // Get all the followershipList where reason is not null
        defaultFollowershipShouldBeFound("reason.specified=true");

        // Get all the followershipList where reason is null
        defaultFollowershipShouldNotBeFound("reason.specified=false");
    }

    @Test
    @Transactional
    public void getAllFollowershipsByCommentIsEqualToSomething() throws Exception {
        // Initialize the database
        followershipRepository.saveAndFlush(followership);

        // Get all the followershipList where comment equals to DEFAULT_COMMENT
        defaultFollowershipShouldBeFound("comment.equals=" + DEFAULT_COMMENT);

        // Get all the followershipList where comment equals to UPDATED_COMMENT
        defaultFollowershipShouldNotBeFound("comment.equals=" + UPDATED_COMMENT);
    }

    @Test
    @Transactional
    public void getAllFollowershipsByCommentIsInShouldWork() throws Exception {
        // Initialize the database
        followershipRepository.saveAndFlush(followership);

        // Get all the followershipList where comment in DEFAULT_COMMENT or UPDATED_COMMENT
        defaultFollowershipShouldBeFound("comment.in=" + DEFAULT_COMMENT + "," + UPDATED_COMMENT);

        // Get all the followershipList where comment equals to UPDATED_COMMENT
        defaultFollowershipShouldNotBeFound("comment.in=" + UPDATED_COMMENT);
    }

    @Test
    @Transactional
    public void getAllFollowershipsByCommentIsNullOrNotNull() throws Exception {
        // Initialize the database
        followershipRepository.saveAndFlush(followership);

        // Get all the followershipList where comment is not null
        defaultFollowershipShouldBeFound("comment.specified=true");

        // Get all the followershipList where comment is null
        defaultFollowershipShouldNotBeFound("comment.specified=false");
    }

    @Test
    @Transactional
    public void getAllFollowershipsByUserIsEqualToSomething() throws Exception {
        // Initialize the database
        User user = UserResourceIntTest.createEntity(em);
        em.persist(user);
        em.flush();
        followership.setUser(user);
        followershipRepository.saveAndFlush(followership);
        Long userId = user.getId();

        // Get all the followershipList where user equals to userId
        defaultFollowershipShouldBeFound("userId.equals=" + userId);

        // Get all the followershipList where user equals to userId + 1
        defaultFollowershipShouldNotBeFound("userId.equals=" + (userId + 1));
    }


    @Test
    @Transactional
    public void getAllFollowershipsByTagIsEqualToSomething() throws Exception {
        // Initialize the database
        Tag tag = TagResourceIntTest.createEntity(em);
        em.persist(tag);
        em.flush();
        followership.setTag(tag);
        followershipRepository.saveAndFlush(followership);
        Long tagId = tag.getId();

        // Get all the followershipList where tag equals to tagId
        defaultFollowershipShouldBeFound("tagId.equals=" + tagId);

        // Get all the followershipList where tag equals to tagId + 1
        defaultFollowershipShouldNotBeFound("tagId.equals=" + (tagId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultFollowershipShouldBeFound(String filter) throws Exception {
        restFollowershipMockMvc.perform(get("/api/followerships?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(followership.getId().intValue())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].reason").value(hasItem(DEFAULT_REASON.toString())))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT.toString())));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultFollowershipShouldNotBeFound(String filter) throws Exception {
        restFollowershipMockMvc.perform(get("/api/followerships?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    @Transactional
    public void getNonExistingFollowership() throws Exception {
        // Get the followership
        restFollowershipMockMvc.perform(get("/api/followerships/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateFollowership() throws Exception {
        // Initialize the database
        followershipRepository.saveAndFlush(followership);
        followershipSearchRepository.save(followership);
        int databaseSizeBeforeUpdate = followershipRepository.findAll().size();

        // Update the followership
        Followership updatedFollowership = followershipRepository.findOne(followership.getId());
        // Disconnect from session so that the updates on updatedFollowership are not directly saved in db
        em.detach(updatedFollowership);
        updatedFollowership
            .createdAt(UPDATED_CREATED_AT)
            .reason(UPDATED_REASON)
            .comment(UPDATED_COMMENT);
        FollowershipDTO followershipDTO = followershipMapper.toDto(updatedFollowership);

        restFollowershipMockMvc.perform(put("/api/followerships")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(followershipDTO)))
            .andExpect(status().isOk());

        // Validate the Followership in the database
        List<Followership> followershipList = followershipRepository.findAll();
        assertThat(followershipList).hasSize(databaseSizeBeforeUpdate);
        Followership testFollowership = followershipList.get(followershipList.size() - 1);
        assertThat(testFollowership.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testFollowership.getReason()).isEqualTo(UPDATED_REASON);
        assertThat(testFollowership.getComment()).isEqualTo(UPDATED_COMMENT);

        // Validate the Followership in Elasticsearch
        Followership followershipEs = followershipSearchRepository.findOne(testFollowership.getId());
        assertThat(followershipEs).isEqualToIgnoringGivenFields(testFollowership);
    }

    @Test
    @Transactional
    public void updateNonExistingFollowership() throws Exception {
        int databaseSizeBeforeUpdate = followershipRepository.findAll().size();

        // Create the Followership
        FollowershipDTO followershipDTO = followershipMapper.toDto(followership);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restFollowershipMockMvc.perform(put("/api/followerships")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(followershipDTO)))
            .andExpect(status().isCreated());

        // Validate the Followership in the database
        List<Followership> followershipList = followershipRepository.findAll();
        assertThat(followershipList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteFollowership() throws Exception {
        // Initialize the database
        followershipRepository.saveAndFlush(followership);
        followershipSearchRepository.save(followership);
        int databaseSizeBeforeDelete = followershipRepository.findAll().size();

        // Get the followership
        restFollowershipMockMvc.perform(delete("/api/followerships/{id}", followership.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean followershipExistsInEs = followershipSearchRepository.exists(followership.getId());
        assertThat(followershipExistsInEs).isFalse();

        // Validate the database is empty
        List<Followership> followershipList = followershipRepository.findAll();
        assertThat(followershipList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchFollowership() throws Exception {
        // Initialize the database
        followershipRepository.saveAndFlush(followership);
        followershipSearchRepository.save(followership);

        // Search the followership
        restFollowershipMockMvc.perform(get("/api/_search/followerships?query=id:" + followership.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(followership.getId().intValue())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].reason").value(hasItem(DEFAULT_REASON.toString())))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Followership.class);
        Followership followership1 = new Followership();
        followership1.setId(1L);
        Followership followership2 = new Followership();
        followership2.setId(followership1.getId());
        assertThat(followership1).isEqualTo(followership2);
        followership2.setId(2L);
        assertThat(followership1).isNotEqualTo(followership2);
        followership1.setId(null);
        assertThat(followership1).isNotEqualTo(followership2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(FollowershipDTO.class);
        FollowershipDTO followershipDTO1 = new FollowershipDTO();
        followershipDTO1.setId(1L);
        FollowershipDTO followershipDTO2 = new FollowershipDTO();
        assertThat(followershipDTO1).isNotEqualTo(followershipDTO2);
        followershipDTO2.setId(followershipDTO1.getId());
        assertThat(followershipDTO1).isEqualTo(followershipDTO2);
        followershipDTO2.setId(2L);
        assertThat(followershipDTO1).isNotEqualTo(followershipDTO2);
        followershipDTO1.setId(null);
        assertThat(followershipDTO1).isNotEqualTo(followershipDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(followershipMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(followershipMapper.fromId(null)).isNull();
    }
}
