package it.techn.gumeh.web.rest;

import it.techn.gumeh.GumehApp;

import it.techn.gumeh.domain.Resurce;
import it.techn.gumeh.repository.ResurceRepository;
import it.techn.gumeh.service.ResurceService;
import it.techn.gumeh.repository.search.ResurceSearchRepository;
import it.techn.gumeh.service.dto.ResurceDTO;
import it.techn.gumeh.service.mapper.ResurceMapper;
import it.techn.gumeh.web.rest.errors.ExceptionTranslator;

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
import java.util.List;

import static it.techn.gumeh.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import it.techn.gumeh.domain.enumeration.ResourceType;
/**
 * Test class for the ResurceResource REST controller.
 *
 * @see ResurceResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GumehApp.class)
public class ResurceResourceIntTest {

    private static final ResourceType DEFAULT_TYPE = ResourceType.Poet;
    private static final ResourceType UPDATED_TYPE = ResourceType.Novel;

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final Integer DEFAULT_PUBLISH_YEAR = 1;
    private static final Integer UPDATED_PUBLISH_YEAR = 2;

    private static final Integer DEFAULT_NO_POSTS = 1;
    private static final Integer UPDATED_NO_POSTS = 2;

    private static final String DEFAULT_CREATOR = "AAAAAAAAAA";
    private static final String UPDATED_CREATOR = "BBBBBBBBBB";

    private static final String DEFAULT_LINK = "AAAAAAAAAA";
    private static final String UPDATED_LINK = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ACTIVATED = false;
    private static final Boolean UPDATED_ACTIVATED = true;

    private static final Boolean DEFAULT_CERTIFIED = false;
    private static final Boolean UPDATED_CERTIFIED = true;

    @Autowired
    private ResurceRepository resurceRepository;

    @Autowired
    private ResurceMapper resurceMapper;

    @Autowired
    private ResurceService resurceService;

    @Autowired
    private ResurceSearchRepository resurceSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restResurceMockMvc;

    private Resurce resurce;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ResurceResource resurceResource = new ResurceResource(resurceService);
        this.restResurceMockMvc = MockMvcBuilders.standaloneSetup(resurceResource)
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
    public static Resurce createEntity(EntityManager em) {
        Resurce resurce = new Resurce()
            .type(DEFAULT_TYPE)
            .title(DEFAULT_TITLE)
            .publishYear(DEFAULT_PUBLISH_YEAR)
            .noPosts(DEFAULT_NO_POSTS)
            .creator(DEFAULT_CREATOR)
            .link(DEFAULT_LINK)
            .activated(DEFAULT_ACTIVATED)
            .certified(DEFAULT_CERTIFIED);
        return resurce;
    }

    @Before
    public void initTest() {
        resurceSearchRepository.deleteAll();
        resurce = createEntity(em);
    }

    @Test
    @Transactional
    public void createResurce() throws Exception {
        int databaseSizeBeforeCreate = resurceRepository.findAll().size();

        // Create the Resurce
        ResurceDTO resurceDTO = resurceMapper.toDto(resurce);
        restResurceMockMvc.perform(post("/api/resurces")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(resurceDTO)))
            .andExpect(status().isCreated());

        // Validate the Resurce in the database
        List<Resurce> resurceList = resurceRepository.findAll();
        assertThat(resurceList).hasSize(databaseSizeBeforeCreate + 1);
        Resurce testResurce = resurceList.get(resurceList.size() - 1);
        assertThat(testResurce.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testResurce.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testResurce.getPublishYear()).isEqualTo(DEFAULT_PUBLISH_YEAR);
        assertThat(testResurce.getNoPosts()).isEqualTo(DEFAULT_NO_POSTS);
        assertThat(testResurce.getCreator()).isEqualTo(DEFAULT_CREATOR);
        assertThat(testResurce.getLink()).isEqualTo(DEFAULT_LINK);
        assertThat(testResurce.isActivated()).isEqualTo(DEFAULT_ACTIVATED);
        assertThat(testResurce.isCertified()).isEqualTo(DEFAULT_CERTIFIED);

        // Validate the Resurce in Elasticsearch
        Resurce resurceEs = resurceSearchRepository.findOne(testResurce.getId());
        assertThat(resurceEs).isEqualToIgnoringGivenFields(testResurce);
    }

    @Test
    @Transactional
    public void createResurceWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = resurceRepository.findAll().size();

        // Create the Resurce with an existing ID
        resurce.setId(1L);
        ResurceDTO resurceDTO = resurceMapper.toDto(resurce);

        // An entity with an existing ID cannot be created, so this API call must fail
        restResurceMockMvc.perform(post("/api/resurces")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(resurceDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Resurce in the database
        List<Resurce> resurceList = resurceRepository.findAll();
        assertThat(resurceList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = resurceRepository.findAll().size();
        // set the field null
        resurce.setType(null);

        // Create the Resurce, which fails.
        ResurceDTO resurceDTO = resurceMapper.toDto(resurce);

        restResurceMockMvc.perform(post("/api/resurces")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(resurceDTO)))
            .andExpect(status().isBadRequest());

        List<Resurce> resurceList = resurceRepository.findAll();
        assertThat(resurceList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = resurceRepository.findAll().size();
        // set the field null
        resurce.setTitle(null);

        // Create the Resurce, which fails.
        ResurceDTO resurceDTO = resurceMapper.toDto(resurce);

        restResurceMockMvc.perform(post("/api/resurces")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(resurceDTO)))
            .andExpect(status().isBadRequest());

        List<Resurce> resurceList = resurceRepository.findAll();
        assertThat(resurceList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkActivatedIsRequired() throws Exception {
        int databaseSizeBeforeTest = resurceRepository.findAll().size();
        // set the field null
        resurce.setActivated(null);

        // Create the Resurce, which fails.
        ResurceDTO resurceDTO = resurceMapper.toDto(resurce);

        restResurceMockMvc.perform(post("/api/resurces")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(resurceDTO)))
            .andExpect(status().isBadRequest());

        List<Resurce> resurceList = resurceRepository.findAll();
        assertThat(resurceList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkCertifiedIsRequired() throws Exception {
        int databaseSizeBeforeTest = resurceRepository.findAll().size();
        // set the field null
        resurce.setCertified(null);

        // Create the Resurce, which fails.
        ResurceDTO resurceDTO = resurceMapper.toDto(resurce);

        restResurceMockMvc.perform(post("/api/resurces")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(resurceDTO)))
            .andExpect(status().isBadRequest());

        List<Resurce> resurceList = resurceRepository.findAll();
        assertThat(resurceList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllResurces() throws Exception {
        // Initialize the database
        resurceRepository.saveAndFlush(resurce);

        // Get all the resurceList
        restResurceMockMvc.perform(get("/api/resurces?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(resurce.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())))
            .andExpect(jsonPath("$.[*].publishYear").value(hasItem(DEFAULT_PUBLISH_YEAR)))
            .andExpect(jsonPath("$.[*].noPosts").value(hasItem(DEFAULT_NO_POSTS)))
            .andExpect(jsonPath("$.[*].creator").value(hasItem(DEFAULT_CREATOR.toString())))
            .andExpect(jsonPath("$.[*].link").value(hasItem(DEFAULT_LINK.toString())))
            .andExpect(jsonPath("$.[*].activated").value(hasItem(DEFAULT_ACTIVATED.booleanValue())))
            .andExpect(jsonPath("$.[*].certified").value(hasItem(DEFAULT_CERTIFIED.booleanValue())));
    }

    @Test
    @Transactional
    public void getResurce() throws Exception {
        // Initialize the database
        resurceRepository.saveAndFlush(resurce);

        // Get the resurce
        restResurceMockMvc.perform(get("/api/resurces/{id}", resurce.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(resurce.getId().intValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE.toString()))
            .andExpect(jsonPath("$.publishYear").value(DEFAULT_PUBLISH_YEAR))
            .andExpect(jsonPath("$.noPosts").value(DEFAULT_NO_POSTS))
            .andExpect(jsonPath("$.creator").value(DEFAULT_CREATOR.toString()))
            .andExpect(jsonPath("$.link").value(DEFAULT_LINK.toString()))
            .andExpect(jsonPath("$.activated").value(DEFAULT_ACTIVATED.booleanValue()))
            .andExpect(jsonPath("$.certified").value(DEFAULT_CERTIFIED.booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingResurce() throws Exception {
        // Get the resurce
        restResurceMockMvc.perform(get("/api/resurces/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateResurce() throws Exception {
        // Initialize the database
        resurceRepository.saveAndFlush(resurce);
        resurceSearchRepository.save(resurce);
        int databaseSizeBeforeUpdate = resurceRepository.findAll().size();

        // Update the resurce
        Resurce updatedResurce = resurceRepository.findOne(resurce.getId());
        // Disconnect from session so that the updates on updatedResurce are not directly saved in db
        em.detach(updatedResurce);
        updatedResurce
            .type(UPDATED_TYPE)
            .title(UPDATED_TITLE)
            .publishYear(UPDATED_PUBLISH_YEAR)
            .noPosts(UPDATED_NO_POSTS)
            .creator(UPDATED_CREATOR)
            .link(UPDATED_LINK)
            .activated(UPDATED_ACTIVATED)
            .certified(UPDATED_CERTIFIED);
        ResurceDTO resurceDTO = resurceMapper.toDto(updatedResurce);

        restResurceMockMvc.perform(put("/api/resurces")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(resurceDTO)))
            .andExpect(status().isOk());

        // Validate the Resurce in the database
        List<Resurce> resurceList = resurceRepository.findAll();
        assertThat(resurceList).hasSize(databaseSizeBeforeUpdate);
        Resurce testResurce = resurceList.get(resurceList.size() - 1);
        assertThat(testResurce.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testResurce.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testResurce.getPublishYear()).isEqualTo(UPDATED_PUBLISH_YEAR);
        assertThat(testResurce.getNoPosts()).isEqualTo(UPDATED_NO_POSTS);
        assertThat(testResurce.getCreator()).isEqualTo(UPDATED_CREATOR);
        assertThat(testResurce.getLink()).isEqualTo(UPDATED_LINK);
        assertThat(testResurce.isActivated()).isEqualTo(UPDATED_ACTIVATED);
        assertThat(testResurce.isCertified()).isEqualTo(UPDATED_CERTIFIED);

        // Validate the Resurce in Elasticsearch
        Resurce resurceEs = resurceSearchRepository.findOne(testResurce.getId());
        assertThat(resurceEs).isEqualToIgnoringGivenFields(testResurce);
    }

    @Test
    @Transactional
    public void updateNonExistingResurce() throws Exception {
        int databaseSizeBeforeUpdate = resurceRepository.findAll().size();

        // Create the Resurce
        ResurceDTO resurceDTO = resurceMapper.toDto(resurce);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restResurceMockMvc.perform(put("/api/resurces")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(resurceDTO)))
            .andExpect(status().isCreated());

        // Validate the Resurce in the database
        List<Resurce> resurceList = resurceRepository.findAll();
        assertThat(resurceList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteResurce() throws Exception {
        // Initialize the database
        resurceRepository.saveAndFlush(resurce);
        resurceSearchRepository.save(resurce);
        int databaseSizeBeforeDelete = resurceRepository.findAll().size();

        // Get the resurce
        restResurceMockMvc.perform(delete("/api/resurces/{id}", resurce.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean resurceExistsInEs = resurceSearchRepository.exists(resurce.getId());
        assertThat(resurceExistsInEs).isFalse();

        // Validate the database is empty
        List<Resurce> resurceList = resurceRepository.findAll();
        assertThat(resurceList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchResurce() throws Exception {
        // Initialize the database
        resurceRepository.saveAndFlush(resurce);
        resurceSearchRepository.save(resurce);

        // Search the resurce
        restResurceMockMvc.perform(get("/api/_search/resurces?query=id:" + resurce.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(resurce.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())))
            .andExpect(jsonPath("$.[*].publishYear").value(hasItem(DEFAULT_PUBLISH_YEAR)))
            .andExpect(jsonPath("$.[*].noPosts").value(hasItem(DEFAULT_NO_POSTS)))
            .andExpect(jsonPath("$.[*].creator").value(hasItem(DEFAULT_CREATOR.toString())))
            .andExpect(jsonPath("$.[*].link").value(hasItem(DEFAULT_LINK.toString())))
            .andExpect(jsonPath("$.[*].activated").value(hasItem(DEFAULT_ACTIVATED.booleanValue())))
            .andExpect(jsonPath("$.[*].certified").value(hasItem(DEFAULT_CERTIFIED.booleanValue())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Resurce.class);
        Resurce resurce1 = new Resurce();
        resurce1.setId(1L);
        Resurce resurce2 = new Resurce();
        resurce2.setId(resurce1.getId());
        assertThat(resurce1).isEqualTo(resurce2);
        resurce2.setId(2L);
        assertThat(resurce1).isNotEqualTo(resurce2);
        resurce1.setId(null);
        assertThat(resurce1).isNotEqualTo(resurce2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ResurceDTO.class);
        ResurceDTO resurceDTO1 = new ResurceDTO();
        resurceDTO1.setId(1L);
        ResurceDTO resurceDTO2 = new ResurceDTO();
        assertThat(resurceDTO1).isNotEqualTo(resurceDTO2);
        resurceDTO2.setId(resurceDTO1.getId());
        assertThat(resurceDTO1).isEqualTo(resurceDTO2);
        resurceDTO2.setId(2L);
        assertThat(resurceDTO1).isNotEqualTo(resurceDTO2);
        resurceDTO1.setId(null);
        assertThat(resurceDTO1).isNotEqualTo(resurceDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(resurceMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(resurceMapper.fromId(null)).isNull();
    }
}
