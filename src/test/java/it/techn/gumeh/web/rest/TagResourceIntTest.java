package it.techn.gumeh.web.rest;

import it.techn.gumeh.GumehApp;

import it.techn.gumeh.domain.Tag;
import it.techn.gumeh.repository.TagRepository;
import it.techn.gumeh.service.TagService;
import it.techn.gumeh.repository.search.TagSearchRepository;
import it.techn.gumeh.service.dto.TagDTO;
import it.techn.gumeh.service.mapper.TagMapper;
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

/**
 * Test class for the TagResource REST controller.
 *
 * @see TagResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GumehApp.class)
public class TagResourceIntTest {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final Integer DEFAULT_NO_POSTS = 1;
    private static final Integer UPDATED_NO_POSTS = 2;

    private static final Integer DEFAULT_NO_FOLLOWERS = 1;
    private static final Integer UPDATED_NO_FOLLOWERS = 2;

    private static final String DEFAULT_RELATED_TAGS = "AAAAAAAAAA";
    private static final String UPDATED_RELATED_TAGS = "BBBBBBBBBB";

    private static final String DEFAULT_ENCYCLOPEDIA_LINK = "AAAAAAAAAA";
    private static final String UPDATED_ENCYCLOPEDIA_LINK = "BBBBBBBBBB";

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private TagService tagService;

    @Autowired
    private TagSearchRepository tagSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restTagMockMvc;

    private Tag tag;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final TagResource tagResource = new TagResource(tagService);
        this.restTagMockMvc = MockMvcBuilders.standaloneSetup(tagResource)
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
    public static Tag createEntity(EntityManager em) {
        Tag tag = new Tag()
            .title(DEFAULT_TITLE)
            .noPosts(DEFAULT_NO_POSTS)
            .noFollowers(DEFAULT_NO_FOLLOWERS)
            .relatedTags(DEFAULT_RELATED_TAGS)
            .encyclopediaLink(DEFAULT_ENCYCLOPEDIA_LINK);
        return tag;
    }

    @Before
    public void initTest() {
        tagSearchRepository.deleteAll();
        tag = createEntity(em);
    }

    @Test
    @Transactional
    public void createTag() throws Exception {
        int databaseSizeBeforeCreate = tagRepository.findAll().size();

        // Create the Tag
        TagDTO tagDTO = tagMapper.toDto(tag);
        restTagMockMvc.perform(post("/api/tags")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tagDTO)))
            .andExpect(status().isCreated());

        // Validate the Tag in the database
        List<Tag> tagList = tagRepository.findAll();
        assertThat(tagList).hasSize(databaseSizeBeforeCreate + 1);
        Tag testTag = tagList.get(tagList.size() - 1);
        assertThat(testTag.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testTag.getNoPosts()).isEqualTo(DEFAULT_NO_POSTS);
        assertThat(testTag.getNoFollowers()).isEqualTo(DEFAULT_NO_FOLLOWERS);
        assertThat(testTag.getRelatedTags()).isEqualTo(DEFAULT_RELATED_TAGS);
        assertThat(testTag.getEncyclopediaLink()).isEqualTo(DEFAULT_ENCYCLOPEDIA_LINK);

        // Validate the Tag in Elasticsearch
        Tag tagEs = tagSearchRepository.findOne(testTag.getId());
        assertThat(tagEs).isEqualToIgnoringGivenFields(testTag);
    }

    @Test
    @Transactional
    public void createTagWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = tagRepository.findAll().size();

        // Create the Tag with an existing ID
        tag.setId(1L);
        TagDTO tagDTO = tagMapper.toDto(tag);

        // An entity with an existing ID cannot be created, so this API call must fail
        restTagMockMvc.perform(post("/api/tags")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tagDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Tag in the database
        List<Tag> tagList = tagRepository.findAll();
        assertThat(tagList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = tagRepository.findAll().size();
        // set the field null
        tag.setTitle(null);

        // Create the Tag, which fails.
        TagDTO tagDTO = tagMapper.toDto(tag);

        restTagMockMvc.perform(post("/api/tags")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tagDTO)))
            .andExpect(status().isBadRequest());

        List<Tag> tagList = tagRepository.findAll();
        assertThat(tagList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllTags() throws Exception {
        // Initialize the database
        tagRepository.saveAndFlush(tag);

        // Get all the tagList
        restTagMockMvc.perform(get("/api/tags?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tag.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())))
            .andExpect(jsonPath("$.[*].noPosts").value(hasItem(DEFAULT_NO_POSTS)))
            .andExpect(jsonPath("$.[*].noFollowers").value(hasItem(DEFAULT_NO_FOLLOWERS)))
            .andExpect(jsonPath("$.[*].relatedTags").value(hasItem(DEFAULT_RELATED_TAGS.toString())))
            .andExpect(jsonPath("$.[*].encyclopediaLink").value(hasItem(DEFAULT_ENCYCLOPEDIA_LINK.toString())));
    }

    @Test
    @Transactional
    public void getTag() throws Exception {
        // Initialize the database
        tagRepository.saveAndFlush(tag);

        // Get the tag
        restTagMockMvc.perform(get("/api/tags/{id}", tag.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(tag.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE.toString()))
            .andExpect(jsonPath("$.noPosts").value(DEFAULT_NO_POSTS))
            .andExpect(jsonPath("$.noFollowers").value(DEFAULT_NO_FOLLOWERS))
            .andExpect(jsonPath("$.relatedTags").value(DEFAULT_RELATED_TAGS.toString()))
            .andExpect(jsonPath("$.encyclopediaLink").value(DEFAULT_ENCYCLOPEDIA_LINK.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingTag() throws Exception {
        // Get the tag
        restTagMockMvc.perform(get("/api/tags/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTag() throws Exception {
        // Initialize the database
        tagRepository.saveAndFlush(tag);
        tagSearchRepository.save(tag);
        int databaseSizeBeforeUpdate = tagRepository.findAll().size();

        // Update the tag
        Tag updatedTag = tagRepository.findOne(tag.getId());
        // Disconnect from session so that the updates on updatedTag are not directly saved in db
        em.detach(updatedTag);
        updatedTag
            .title(UPDATED_TITLE)
            .noPosts(UPDATED_NO_POSTS)
            .noFollowers(UPDATED_NO_FOLLOWERS)
            .relatedTags(UPDATED_RELATED_TAGS)
            .encyclopediaLink(UPDATED_ENCYCLOPEDIA_LINK);
        TagDTO tagDTO = tagMapper.toDto(updatedTag);

        restTagMockMvc.perform(put("/api/tags")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tagDTO)))
            .andExpect(status().isOk());

        // Validate the Tag in the database
        List<Tag> tagList = tagRepository.findAll();
        assertThat(tagList).hasSize(databaseSizeBeforeUpdate);
        Tag testTag = tagList.get(tagList.size() - 1);
        assertThat(testTag.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testTag.getNoPosts()).isEqualTo(UPDATED_NO_POSTS);
        assertThat(testTag.getNoFollowers()).isEqualTo(UPDATED_NO_FOLLOWERS);
        assertThat(testTag.getRelatedTags()).isEqualTo(UPDATED_RELATED_TAGS);
        assertThat(testTag.getEncyclopediaLink()).isEqualTo(UPDATED_ENCYCLOPEDIA_LINK);

        // Validate the Tag in Elasticsearch
        Tag tagEs = tagSearchRepository.findOne(testTag.getId());
        assertThat(tagEs).isEqualToIgnoringGivenFields(testTag);
    }

    @Test
    @Transactional
    public void updateNonExistingTag() throws Exception {
        int databaseSizeBeforeUpdate = tagRepository.findAll().size();

        // Create the Tag
        TagDTO tagDTO = tagMapper.toDto(tag);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restTagMockMvc.perform(put("/api/tags")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tagDTO)))
            .andExpect(status().isCreated());

        // Validate the Tag in the database
        List<Tag> tagList = tagRepository.findAll();
        assertThat(tagList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteTag() throws Exception {
        // Initialize the database
        tagRepository.saveAndFlush(tag);
        tagSearchRepository.save(tag);
        int databaseSizeBeforeDelete = tagRepository.findAll().size();

        // Get the tag
        restTagMockMvc.perform(delete("/api/tags/{id}", tag.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean tagExistsInEs = tagSearchRepository.exists(tag.getId());
        assertThat(tagExistsInEs).isFalse();

        // Validate the database is empty
        List<Tag> tagList = tagRepository.findAll();
        assertThat(tagList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchTag() throws Exception {
        // Initialize the database
        tagRepository.saveAndFlush(tag);
        tagSearchRepository.save(tag);

        // Search the tag
        restTagMockMvc.perform(get("/api/_search/tags?query=id:" + tag.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tag.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())))
            .andExpect(jsonPath("$.[*].noPosts").value(hasItem(DEFAULT_NO_POSTS)))
            .andExpect(jsonPath("$.[*].noFollowers").value(hasItem(DEFAULT_NO_FOLLOWERS)))
            .andExpect(jsonPath("$.[*].relatedTags").value(hasItem(DEFAULT_RELATED_TAGS.toString())))
            .andExpect(jsonPath("$.[*].encyclopediaLink").value(hasItem(DEFAULT_ENCYCLOPEDIA_LINK.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Tag.class);
        Tag tag1 = new Tag();
        tag1.setId(1L);
        Tag tag2 = new Tag();
        tag2.setId(tag1.getId());
        assertThat(tag1).isEqualTo(tag2);
        tag2.setId(2L);
        assertThat(tag1).isNotEqualTo(tag2);
        tag1.setId(null);
        assertThat(tag1).isNotEqualTo(tag2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TagDTO.class);
        TagDTO tagDTO1 = new TagDTO();
        tagDTO1.setId(1L);
        TagDTO tagDTO2 = new TagDTO();
        assertThat(tagDTO1).isNotEqualTo(tagDTO2);
        tagDTO2.setId(tagDTO1.getId());
        assertThat(tagDTO1).isEqualTo(tagDTO2);
        tagDTO2.setId(2L);
        assertThat(tagDTO1).isNotEqualTo(tagDTO2);
        tagDTO1.setId(null);
        assertThat(tagDTO1).isNotEqualTo(tagDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(tagMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(tagMapper.fromId(null)).isNull();
    }
}
