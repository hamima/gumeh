package it.techn.gumeh.web.rest;

import it.techn.gumeh.GumehApp;
import it.techn.gumeh.domain.Post;
import it.techn.gumeh.domain.Resurce;
import it.techn.gumeh.repository.PostRepository;
import it.techn.gumeh.repository.search.PostSearchRepository;
import it.techn.gumeh.service.PostQueryService;
import it.techn.gumeh.service.PostService;
import it.techn.gumeh.service.dto.PostDTO;
import it.techn.gumeh.service.mapper.PostMapper;
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
 * Test class for the PostResource REST controller.
 *
 * @see PostResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GumehApp.class)
public class PostResourceIntTest {

    private static final String DEFAULT_RESOURCE_BRIEF = "AAAAAAAAAA";
    private static final String UPDATED_RESOURCE_BRIEF = "BBBBBBBBBB";

    private static final Integer DEFAULT_NO_LIKES = 1;
    private static final Integer UPDATED_NO_LIKES = 2;

    private static final String DEFAULT_TAG_STR = "AAAAAAAAAA";
    private static final String UPDATED_TAG_STR = "BBBBBBBBBB";

    private static final String DEFAULT_USER_BRIEF = "AAAAAAAAAA";
    private static final String UPDATED_USER_BRIEF = "BBBBBBBBBB";

    private static final String DEFAULT_LINK = "AAAAAAAAAA";
    private static final String UPDATED_LINK = "BBBBBBBBBB";

    private static final String DEFAULT_TEXT = "AAAAAAAAAA";
    private static final String UPDATED_TEXT = "BBBBBBBBBB";

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private PostService postService;

    @Autowired
    private PostSearchRepository postSearchRepository;

    @Autowired
    private PostQueryService postQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restPostMockMvc;

    private Post post;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final PostResource postResource = new PostResource(postService, postQueryService);
        this.restPostMockMvc = MockMvcBuilders.standaloneSetup(postResource)
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
    public static Post createEntity(EntityManager em) {
        Post post = new Post()
            .resourceBrief(DEFAULT_RESOURCE_BRIEF)
            .noLikes(DEFAULT_NO_LIKES)
            .tagStr(DEFAULT_TAG_STR)
            .userBrief(DEFAULT_USER_BRIEF)
            .link(DEFAULT_LINK)
            .text(DEFAULT_TEXT);
        // Add required entity
        Resurce category = ResurceResourceIntTest.createEntity(em);
        em.persist(category);
        em.flush();
        post.setCategory(category);
        return post;
    }

    @Before
    public void initTest() {
        postSearchRepository.deleteAll();
        post = createEntity(em);
    }

    @Test
    @Transactional
    public void createPost() throws Exception {
        int databaseSizeBeforeCreate = postRepository.findAll().size();

        // Create the Post
        PostDTO postDTO = postMapper.toDto(post);
        restPostMockMvc.perform(post("/api/posts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(postDTO)))
            .andExpect(status().isCreated());

        // Validate the Post in the database
        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeCreate + 1);
        Post testPost = postList.get(postList.size() - 1);
        assertThat(testPost.getResourceBrief()).isEqualTo(DEFAULT_RESOURCE_BRIEF);
        assertThat(testPost.getNoLikes()).isEqualTo(DEFAULT_NO_LIKES);
        assertThat(testPost.getTagStr()).isEqualTo(DEFAULT_TAG_STR);
//        assertThat(testPost.getUserBrief()).isEqualTo(DEFAULT_USER_BRIEF);
        assertThat(testPost.getLink()).isEqualTo(DEFAULT_LINK);
        assertThat(testPost.getText()).isEqualTo(DEFAULT_TEXT);

        // Validate the Post in Elasticsearch
        Post postEs = postSearchRepository.findOne(testPost.getId());
        assertThat(postEs).isEqualToIgnoringGivenFields(testPost);
    }

    @Test
    @Transactional
    public void createPostWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = postRepository.findAll().size();

        // Create the Post with an existing ID
        post.setId(1L);
        PostDTO postDTO = postMapper.toDto(post);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPostMockMvc.perform(post("/api/posts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(postDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Post in the database
        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllPosts() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList
        restPostMockMvc.perform(get("/api/posts?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(post.getId().intValue())))
            .andExpect(jsonPath("$.[*].resourceBrief").value(hasItem(DEFAULT_RESOURCE_BRIEF.toString())))
            .andExpect(jsonPath("$.[*].noLikes").value(hasItem(DEFAULT_NO_LIKES)))
            .andExpect(jsonPath("$.[*].tagStr").value(hasItem(DEFAULT_TAG_STR.toString())))
            .andExpect(jsonPath("$.[*].userBrief").value(hasItem(DEFAULT_USER_BRIEF.toString())))
            .andExpect(jsonPath("$.[*].link").value(hasItem(DEFAULT_LINK.toString())))
            .andExpect(jsonPath("$.[*].text").value(hasItem(DEFAULT_TEXT.toString())));
    }

    @Test
    @Transactional
    public void getPost() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get the post
        restPostMockMvc.perform(get("/api/posts/{id}", post.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(post.getId().intValue()))
            .andExpect(jsonPath("$.resourceBrief").value(DEFAULT_RESOURCE_BRIEF.toString()))
            .andExpect(jsonPath("$.noLikes").value(DEFAULT_NO_LIKES))
            .andExpect(jsonPath("$.tagStr").value(DEFAULT_TAG_STR.toString()))
            .andExpect(jsonPath("$.userBrief").value(DEFAULT_USER_BRIEF.toString()))
            .andExpect(jsonPath("$.link").value(DEFAULT_LINK.toString()))
            .andExpect(jsonPath("$.text").value(DEFAULT_TEXT.toString()));
    }

    @Test
    @Transactional
    public void getAllPostsByResourceBriefIsEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where resourceBrief equals to DEFAULT_RESOURCE_BRIEF
        defaultPostShouldBeFound("resourceBrief.equals=" + DEFAULT_RESOURCE_BRIEF);

        // Get all the postList where resourceBrief equals to UPDATED_RESOURCE_BRIEF
        defaultPostShouldNotBeFound("resourceBrief.equals=" + UPDATED_RESOURCE_BRIEF);
    }

    @Test
    @Transactional
    public void getAllPostsByResourceBriefIsInShouldWork() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where resourceBrief in DEFAULT_RESOURCE_BRIEF or UPDATED_RESOURCE_BRIEF
        defaultPostShouldBeFound("resourceBrief.in=" + DEFAULT_RESOURCE_BRIEF + "," + UPDATED_RESOURCE_BRIEF);

        // Get all the postList where resourceBrief equals to UPDATED_RESOURCE_BRIEF
        defaultPostShouldNotBeFound("resourceBrief.in=" + UPDATED_RESOURCE_BRIEF);
    }

    @Test
    @Transactional
    public void getAllPostsByResourceBriefIsNullOrNotNull() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where resourceBrief is not null
        defaultPostShouldBeFound("resourceBrief.specified=true");

        // Get all the postList where resourceBrief is null
        defaultPostShouldNotBeFound("resourceBrief.specified=false");
    }

    @Test
    @Transactional
    public void getAllPostsByNoLikesIsEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where noLikes equals to DEFAULT_NO_LIKES
        defaultPostShouldBeFound("noLikes.equals=" + DEFAULT_NO_LIKES);

        // Get all the postList where noLikes equals to UPDATED_NO_LIKES
        defaultPostShouldNotBeFound("noLikes.equals=" + UPDATED_NO_LIKES);
    }

    @Test
    @Transactional
    public void getAllPostsByNoLikesIsInShouldWork() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where noLikes in DEFAULT_NO_LIKES or UPDATED_NO_LIKES
        defaultPostShouldBeFound("noLikes.in=" + DEFAULT_NO_LIKES + "," + UPDATED_NO_LIKES);

        // Get all the postList where noLikes equals to UPDATED_NO_LIKES
        defaultPostShouldNotBeFound("noLikes.in=" + UPDATED_NO_LIKES);
    }

    @Test
    @Transactional
    public void getAllPostsByNoLikesIsNullOrNotNull() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where noLikes is not null
        defaultPostShouldBeFound("noLikes.specified=true");

        // Get all the postList where noLikes is null
        defaultPostShouldNotBeFound("noLikes.specified=false");
    }

    @Test
    @Transactional
    public void getAllPostsByNoLikesIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where noLikes greater than or equals to DEFAULT_NO_LIKES
        defaultPostShouldBeFound("noLikes.greaterOrEqualThan=" + DEFAULT_NO_LIKES);

        // Get all the postList where noLikes greater than or equals to UPDATED_NO_LIKES
        defaultPostShouldNotBeFound("noLikes.greaterOrEqualThan=" + UPDATED_NO_LIKES);
    }

    @Test
    @Transactional
    public void getAllPostsByNoLikesIsLessThanSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where noLikes less than or equals to DEFAULT_NO_LIKES
        defaultPostShouldNotBeFound("noLikes.lessThan=" + DEFAULT_NO_LIKES);

        // Get all the postList where noLikes less than or equals to UPDATED_NO_LIKES
        defaultPostShouldBeFound("noLikes.lessThan=" + UPDATED_NO_LIKES);
    }


    @Test
    @Transactional
    public void getAllPostsByTagStrIsEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where tagStr equals to DEFAULT_TAG_STR
        defaultPostShouldBeFound("tagStr.equals=" + DEFAULT_TAG_STR);

        // Get all the postList where tagStr equals to UPDATED_TAG_STR
        defaultPostShouldNotBeFound("tagStr.equals=" + UPDATED_TAG_STR);
    }

    @Test
    @Transactional
    public void getAllPostsByTagStrIsInShouldWork() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where tagStr in DEFAULT_TAG_STR or UPDATED_TAG_STR
        defaultPostShouldBeFound("tagStr.in=" + DEFAULT_TAG_STR + "," + UPDATED_TAG_STR);

        // Get all the postList where tagStr equals to UPDATED_TAG_STR
        defaultPostShouldNotBeFound("tagStr.in=" + UPDATED_TAG_STR);
    }

    @Test
    @Transactional
    public void getAllPostsByTagStrIsNullOrNotNull() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where tagStr is not null
        defaultPostShouldBeFound("tagStr.specified=true");

        // Get all the postList where tagStr is null
        defaultPostShouldNotBeFound("tagStr.specified=false");
    }

    @Test
    @Transactional
    public void getAllPostsByUserBriefIsEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where userBrief equals to DEFAULT_USER_BRIEF
        defaultPostShouldBeFound("userBrief.equals=" + DEFAULT_USER_BRIEF);

        // Get all the postList where userBrief equals to UPDATED_USER_BRIEF
        defaultPostShouldNotBeFound("userBrief.equals=" + UPDATED_USER_BRIEF);
    }

    @Test
    @Transactional
    public void getAllPostsByUserBriefIsInShouldWork() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where userBrief in DEFAULT_USER_BRIEF or UPDATED_USER_BRIEF
        defaultPostShouldBeFound("userBrief.in=" + DEFAULT_USER_BRIEF + "," + UPDATED_USER_BRIEF);

        // Get all the postList where userBrief equals to UPDATED_USER_BRIEF
        defaultPostShouldNotBeFound("userBrief.in=" + UPDATED_USER_BRIEF);
    }

    @Test
    @Transactional
    public void getAllPostsByUserBriefIsNullOrNotNull() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where userBrief is not null
        defaultPostShouldBeFound("userBrief.specified=true");

        // Get all the postList where userBrief is null
        defaultPostShouldNotBeFound("userBrief.specified=false");
    }

    @Test
    @Transactional
    public void getAllPostsByLinkIsEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where link equals to DEFAULT_LINK
        defaultPostShouldBeFound("link.equals=" + DEFAULT_LINK);

        // Get all the postList where link equals to UPDATED_LINK
        defaultPostShouldNotBeFound("link.equals=" + UPDATED_LINK);
    }

    @Test
    @Transactional
    public void getAllPostsByLinkIsInShouldWork() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where link in DEFAULT_LINK or UPDATED_LINK
        defaultPostShouldBeFound("link.in=" + DEFAULT_LINK + "," + UPDATED_LINK);

        // Get all the postList where link equals to UPDATED_LINK
        defaultPostShouldNotBeFound("link.in=" + UPDATED_LINK);
    }

    @Test
    @Transactional
    public void getAllPostsByLinkIsNullOrNotNull() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where link is not null
        defaultPostShouldBeFound("link.specified=true");

        // Get all the postList where link is null
        defaultPostShouldNotBeFound("link.specified=false");
    }

    @Test
    @Transactional
    public void getAllPostsByTextIsEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where text equals to DEFAULT_TEXT
        defaultPostShouldBeFound("text.equals=" + DEFAULT_TEXT);

        // Get all the postList where text equals to UPDATED_TEXT
        defaultPostShouldNotBeFound("text.equals=" + UPDATED_TEXT);
    }

    @Test
    @Transactional
    public void getAllPostsByTextIsInShouldWork() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where text in DEFAULT_TEXT or UPDATED_TEXT
        defaultPostShouldBeFound("text.in=" + DEFAULT_TEXT + "," + UPDATED_TEXT);

        // Get all the postList where text equals to UPDATED_TEXT
        defaultPostShouldNotBeFound("text.in=" + UPDATED_TEXT);
    }

    @Test
    @Transactional
    public void getAllPostsByTextIsNullOrNotNull() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where text is not null
        defaultPostShouldBeFound("text.specified=true");

        // Get all the postList where text is null
        defaultPostShouldNotBeFound("text.specified=false");
    }

    @Test
    @Transactional
    public void getAllPostsByCategoryIsEqualToSomething() throws Exception {
        // Initialize the database
        Resurce category = ResurceResourceIntTest.createEntity(em);
        em.persist(category);
        em.flush();
        post.setCategory(category);
        postRepository.saveAndFlush(post);
        Long categoryId = category.getId();

        // Get all the postList where category equals to categoryId
        defaultPostShouldBeFound("categoryId.equals=" + categoryId);

        // Get all the postList where category equals to categoryId + 1
        defaultPostShouldNotBeFound("categoryId.equals=" + (categoryId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultPostShouldBeFound(String filter) throws Exception {
        restPostMockMvc.perform(get("/api/posts?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(post.getId().intValue())))
            .andExpect(jsonPath("$.[*].resourceBrief").value(hasItem(DEFAULT_RESOURCE_BRIEF.toString())))
            .andExpect(jsonPath("$.[*].noLikes").value(hasItem(DEFAULT_NO_LIKES)))
            .andExpect(jsonPath("$.[*].tagStr").value(hasItem(DEFAULT_TAG_STR.toString())))
            .andExpect(jsonPath("$.[*].userBrief").value(hasItem(DEFAULT_USER_BRIEF.toString())))
            .andExpect(jsonPath("$.[*].link").value(hasItem(DEFAULT_LINK.toString())))
            .andExpect(jsonPath("$.[*].text").value(hasItem(DEFAULT_TEXT.toString())));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultPostShouldNotBeFound(String filter) throws Exception {
        restPostMockMvc.perform(get("/api/posts?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    @Transactional
    public void getNonExistingPost() throws Exception {
        // Get the post
        restPostMockMvc.perform(get("/api/posts/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePost() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);
        postSearchRepository.save(post);
        int databaseSizeBeforeUpdate = postRepository.findAll().size();

        // Update the post
        Post updatedPost = postRepository.findOne(post.getId());
        // Disconnect from session so that the updates on updatedPost are not directly saved in db
        em.detach(updatedPost);
        updatedPost
            .resourceBrief(UPDATED_RESOURCE_BRIEF)
            .noLikes(UPDATED_NO_LIKES)
            .tagStr(UPDATED_TAG_STR)
            .userBrief(UPDATED_USER_BRIEF)
            .link(UPDATED_LINK)
            .text(UPDATED_TEXT);
        PostDTO postDTO = postMapper.toDto(updatedPost);

        restPostMockMvc.perform(put("/api/posts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(postDTO)))
            .andExpect(status().isOk());

        // Validate the Post in the database
        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeUpdate);
        Post testPost = postList.get(postList.size() - 1);
        assertThat(testPost.getResourceBrief()).isEqualTo(UPDATED_RESOURCE_BRIEF);
        assertThat(testPost.getNoLikes()).isEqualTo(UPDATED_NO_LIKES);
        assertThat(testPost.getTagStr()).isEqualTo(UPDATED_TAG_STR);
//        assertThat(testPost.getUserBrief()).isEqualTo(UPDATED_USER_BRIEF);
        assertThat(testPost.getLink()).isEqualTo(UPDATED_LINK);
        assertThat(testPost.getText()).isEqualTo(UPDATED_TEXT);

        // Validate the Post in Elasticsearch
        Post postEs = postSearchRepository.findOne(testPost.getId());
        assertThat(postEs).isEqualToIgnoringGivenFields(testPost);
    }

    @Test
    @Transactional
    public void updateNonExistingPost() throws Exception {
        int databaseSizeBeforeUpdate = postRepository.findAll().size();

        // Create the Post
        PostDTO postDTO = postMapper.toDto(post);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restPostMockMvc.perform(put("/api/posts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(postDTO)))
            .andExpect(status().isCreated());

        // Validate the Post in the database
        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deletePost() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);
        postSearchRepository.save(post);
        int databaseSizeBeforeDelete = postRepository.findAll().size();

        // Get the post
        restPostMockMvc.perform(delete("/api/posts/{id}", post.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean postExistsInEs = postSearchRepository.exists(post.getId());
        assertThat(postExistsInEs).isFalse();

        // Validate the database is empty
        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchPost() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);
        postSearchRepository.save(post);

        // Search the post
        restPostMockMvc.perform(get("/api/_search/posts?query=id:" + post.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(post.getId().intValue())))
            .andExpect(jsonPath("$.[*].resourceBrief").value(hasItem(DEFAULT_RESOURCE_BRIEF.toString())))
            .andExpect(jsonPath("$.[*].noLikes").value(hasItem(DEFAULT_NO_LIKES)))
            .andExpect(jsonPath("$.[*].tagStr").value(hasItem(DEFAULT_TAG_STR.toString())))
            .andExpect(jsonPath("$.[*].userBrief").value(hasItem(DEFAULT_USER_BRIEF.toString())))
            .andExpect(jsonPath("$.[*].link").value(hasItem(DEFAULT_LINK.toString())))
            .andExpect(jsonPath("$.[*].text").value(hasItem(DEFAULT_TEXT.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Post.class);
        Post post1 = new Post();
        post1.setId(1L);
        Post post2 = new Post();
        post2.setId(post1.getId());
        assertThat(post1).isEqualTo(post2);
        post2.setId(2L);
        assertThat(post1).isNotEqualTo(post2);
        post1.setId(null);
        assertThat(post1).isNotEqualTo(post2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PostDTO.class);
        PostDTO postDTO1 = new PostDTO();
        postDTO1.setId(1L);
        PostDTO postDTO2 = new PostDTO();
        assertThat(postDTO1).isNotEqualTo(postDTO2);
        postDTO2.setId(postDTO1.getId());
        assertThat(postDTO1).isEqualTo(postDTO2);
        postDTO2.setId(2L);
        assertThat(postDTO1).isNotEqualTo(postDTO2);
        postDTO1.setId(null);
        assertThat(postDTO1).isNotEqualTo(postDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(postMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(postMapper.fromId(null)).isNull();
    }
}
