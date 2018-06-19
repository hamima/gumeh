package it.techn.gumeh.web.rest;

import it.techn.gumeh.GumehApp;

import it.techn.gumeh.domain.Activity;
import it.techn.gumeh.domain.User;
import it.techn.gumeh.domain.Post;
import it.techn.gumeh.repository.ActivityRepository;
import it.techn.gumeh.service.ActivityService;
import it.techn.gumeh.repository.search.ActivitySearchRepository;
import it.techn.gumeh.service.dto.ActivityDTO;
import it.techn.gumeh.service.mapper.ActivityMapper;
import it.techn.gumeh.web.rest.errors.ExceptionTranslator;
import it.techn.gumeh.service.dto.ActivityCriteria;
import it.techn.gumeh.service.ActivityQueryService;

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

import it.techn.gumeh.domain.enumeration.ActivityType;
/**
 * Test class for the ActivityResource REST controller.
 *
 * @see ActivityResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GumehApp.class)
public class ActivityResourceIntTest {

    private static final ActivityType DEFAULT_TYPE = ActivityType.Like;
    private static final ActivityType UPDATED_TYPE = ActivityType.Comment;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_COMMENT = "AAAAAAAAAA";
    private static final String UPDATED_COMMENT = "BBBBBBBBBB";

    private static final Boolean DEFAULT_DELETED = false;
    private static final Boolean UPDATED_DELETED = true;

    private static final String DEFAULT_REPORT_REASON = "AAAAAAAAAA";
    private static final String UPDATED_REPORT_REASON = "BBBBBBBBBB";

    private static final String DEFAULT_USER_BRIEF = "AAAAAAAAAA";
    private static final String UPDATED_USER_BRIEF = "BBBBBBBBBB";

    private static final String DEFAULT_ACTIVITY_DESC = "AAAAAAAAAA";
    private static final String UPDATED_ACTIVITY_DESC = "BBBBBBBBBB";

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ActivitySearchRepository activitySearchRepository;

    @Autowired
    private ActivityQueryService activityQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restActivityMockMvc;

    private Activity activity;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ActivityResource activityResource = new ActivityResource(activityService, activityQueryService);
        this.restActivityMockMvc = MockMvcBuilders.standaloneSetup(activityResource)
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
    public static Activity createEntity(EntityManager em) {
        Activity activity = new Activity()
            .type(DEFAULT_TYPE)
            .createdAt(DEFAULT_CREATED_AT)
            .comment(DEFAULT_COMMENT)
            .deleted(DEFAULT_DELETED)
            .reportReason(DEFAULT_REPORT_REASON)
            .userBrief(DEFAULT_USER_BRIEF)
            .activityDesc(DEFAULT_ACTIVITY_DESC);
        // Add required entity
        User user = UserResourceIntTest.createEntity(em);
        em.persist(user);
        em.flush();
        activity.setUser(user);
        // Add required entity
        Post post = PostResourceIntTest.createEntity(em);
        em.persist(post);
        em.flush();
        activity.setPost(post);
        return activity;
    }

    @Before
    public void initTest() {
        activitySearchRepository.deleteAll();
        activity = createEntity(em);
    }

    @Test
    @Transactional
    public void createActivity() throws Exception {
        int databaseSizeBeforeCreate = activityRepository.findAll().size();

        // Create the Activity
        ActivityDTO activityDTO = activityMapper.toDto(activity);
        restActivityMockMvc.perform(post("/api/activities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(activityDTO)))
            .andExpect(status().isCreated());

        // Validate the Activity in the database
        List<Activity> activityList = activityRepository.findAll();
        assertThat(activityList).hasSize(databaseSizeBeforeCreate + 1);
        Activity testActivity = activityList.get(activityList.size() - 1);
        assertThat(testActivity.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testActivity.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testActivity.getComment()).isEqualTo(DEFAULT_COMMENT);
        assertThat(testActivity.isDeleted()).isEqualTo(DEFAULT_DELETED);
        assertThat(testActivity.getReportReason()).isEqualTo(DEFAULT_REPORT_REASON);
        assertThat(testActivity.getUserBrief()).isEqualTo(DEFAULT_USER_BRIEF);
        assertThat(testActivity.getActivityDesc()).isEqualTo(DEFAULT_ACTIVITY_DESC);

        // Validate the Activity in Elasticsearch
        Activity activityEs = activitySearchRepository.findOne(testActivity.getId());
        assertThat(activityEs).isEqualToIgnoringGivenFields(testActivity);
    }

    @Test
    @Transactional
    public void createActivityWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = activityRepository.findAll().size();

        // Create the Activity with an existing ID
        activity.setId(1L);
        ActivityDTO activityDTO = activityMapper.toDto(activity);

        // An entity with an existing ID cannot be created, so this API call must fail
        restActivityMockMvc.perform(post("/api/activities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(activityDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Activity in the database
        List<Activity> activityList = activityRepository.findAll();
        assertThat(activityList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = activityRepository.findAll().size();
        // set the field null
        activity.setType(null);

        // Create the Activity, which fails.
        ActivityDTO activityDTO = activityMapper.toDto(activity);

        restActivityMockMvc.perform(post("/api/activities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(activityDTO)))
            .andExpect(status().isBadRequest());

        List<Activity> activityList = activityRepository.findAll();
        assertThat(activityList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllActivities() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList
        restActivityMockMvc.perform(get("/api/activities?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(activity.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT.toString())))
            .andExpect(jsonPath("$.[*].deleted").value(hasItem(DEFAULT_DELETED.booleanValue())))
            .andExpect(jsonPath("$.[*].reportReason").value(hasItem(DEFAULT_REPORT_REASON.toString())))
            .andExpect(jsonPath("$.[*].userBrief").value(hasItem(DEFAULT_USER_BRIEF.toString())))
            .andExpect(jsonPath("$.[*].activityDesc").value(hasItem(DEFAULT_ACTIVITY_DESC.toString())));
    }

    @Test
    @Transactional
    public void getActivity() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get the activity
        restActivityMockMvc.perform(get("/api/activities/{id}", activity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(activity.getId().intValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.comment").value(DEFAULT_COMMENT.toString()))
            .andExpect(jsonPath("$.deleted").value(DEFAULT_DELETED.booleanValue()))
            .andExpect(jsonPath("$.reportReason").value(DEFAULT_REPORT_REASON.toString()))
            .andExpect(jsonPath("$.userBrief").value(DEFAULT_USER_BRIEF.toString()))
            .andExpect(jsonPath("$.activityDesc").value(DEFAULT_ACTIVITY_DESC.toString()));
    }

    @Test
    @Transactional
    public void getAllActivitiesByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where type equals to DEFAULT_TYPE
        defaultActivityShouldBeFound("type.equals=" + DEFAULT_TYPE);

        // Get all the activityList where type equals to UPDATED_TYPE
        defaultActivityShouldNotBeFound("type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    public void getAllActivitiesByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where type in DEFAULT_TYPE or UPDATED_TYPE
        defaultActivityShouldBeFound("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE);

        // Get all the activityList where type equals to UPDATED_TYPE
        defaultActivityShouldNotBeFound("type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    public void getAllActivitiesByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where type is not null
        defaultActivityShouldBeFound("type.specified=true");

        // Get all the activityList where type is null
        defaultActivityShouldNotBeFound("type.specified=false");
    }

    @Test
    @Transactional
    public void getAllActivitiesByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where createdAt equals to DEFAULT_CREATED_AT
        defaultActivityShouldBeFound("createdAt.equals=" + DEFAULT_CREATED_AT);

        // Get all the activityList where createdAt equals to UPDATED_CREATED_AT
        defaultActivityShouldNotBeFound("createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    public void getAllActivitiesByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where createdAt in DEFAULT_CREATED_AT or UPDATED_CREATED_AT
        defaultActivityShouldBeFound("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT);

        // Get all the activityList where createdAt equals to UPDATED_CREATED_AT
        defaultActivityShouldNotBeFound("createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    public void getAllActivitiesByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where createdAt is not null
        defaultActivityShouldBeFound("createdAt.specified=true");

        // Get all the activityList where createdAt is null
        defaultActivityShouldNotBeFound("createdAt.specified=false");
    }

    @Test
    @Transactional
    public void getAllActivitiesByCommentIsEqualToSomething() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where comment equals to DEFAULT_COMMENT
        defaultActivityShouldBeFound("comment.equals=" + DEFAULT_COMMENT);

        // Get all the activityList where comment equals to UPDATED_COMMENT
        defaultActivityShouldNotBeFound("comment.equals=" + UPDATED_COMMENT);
    }

    @Test
    @Transactional
    public void getAllActivitiesByCommentIsInShouldWork() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where comment in DEFAULT_COMMENT or UPDATED_COMMENT
        defaultActivityShouldBeFound("comment.in=" + DEFAULT_COMMENT + "," + UPDATED_COMMENT);

        // Get all the activityList where comment equals to UPDATED_COMMENT
        defaultActivityShouldNotBeFound("comment.in=" + UPDATED_COMMENT);
    }

    @Test
    @Transactional
    public void getAllActivitiesByCommentIsNullOrNotNull() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where comment is not null
        defaultActivityShouldBeFound("comment.specified=true");

        // Get all the activityList where comment is null
        defaultActivityShouldNotBeFound("comment.specified=false");
    }

    @Test
    @Transactional
    public void getAllActivitiesByDeletedIsEqualToSomething() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where deleted equals to DEFAULT_DELETED
        defaultActivityShouldBeFound("deleted.equals=" + DEFAULT_DELETED);

        // Get all the activityList where deleted equals to UPDATED_DELETED
        defaultActivityShouldNotBeFound("deleted.equals=" + UPDATED_DELETED);
    }

    @Test
    @Transactional
    public void getAllActivitiesByDeletedIsInShouldWork() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where deleted in DEFAULT_DELETED or UPDATED_DELETED
        defaultActivityShouldBeFound("deleted.in=" + DEFAULT_DELETED + "," + UPDATED_DELETED);

        // Get all the activityList where deleted equals to UPDATED_DELETED
        defaultActivityShouldNotBeFound("deleted.in=" + UPDATED_DELETED);
    }

    @Test
    @Transactional
    public void getAllActivitiesByDeletedIsNullOrNotNull() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where deleted is not null
        defaultActivityShouldBeFound("deleted.specified=true");

        // Get all the activityList where deleted is null
        defaultActivityShouldNotBeFound("deleted.specified=false");
    }

    @Test
    @Transactional
    public void getAllActivitiesByReportReasonIsEqualToSomething() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where reportReason equals to DEFAULT_REPORT_REASON
        defaultActivityShouldBeFound("reportReason.equals=" + DEFAULT_REPORT_REASON);

        // Get all the activityList where reportReason equals to UPDATED_REPORT_REASON
        defaultActivityShouldNotBeFound("reportReason.equals=" + UPDATED_REPORT_REASON);
    }

    @Test
    @Transactional
    public void getAllActivitiesByReportReasonIsInShouldWork() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where reportReason in DEFAULT_REPORT_REASON or UPDATED_REPORT_REASON
        defaultActivityShouldBeFound("reportReason.in=" + DEFAULT_REPORT_REASON + "," + UPDATED_REPORT_REASON);

        // Get all the activityList where reportReason equals to UPDATED_REPORT_REASON
        defaultActivityShouldNotBeFound("reportReason.in=" + UPDATED_REPORT_REASON);
    }

    @Test
    @Transactional
    public void getAllActivitiesByReportReasonIsNullOrNotNull() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where reportReason is not null
        defaultActivityShouldBeFound("reportReason.specified=true");

        // Get all the activityList where reportReason is null
        defaultActivityShouldNotBeFound("reportReason.specified=false");
    }

    @Test
    @Transactional
    public void getAllActivitiesByUserBriefIsEqualToSomething() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where userBrief equals to DEFAULT_USER_BRIEF
        defaultActivityShouldBeFound("userBrief.equals=" + DEFAULT_USER_BRIEF);

        // Get all the activityList where userBrief equals to UPDATED_USER_BRIEF
        defaultActivityShouldNotBeFound("userBrief.equals=" + UPDATED_USER_BRIEF);
    }

    @Test
    @Transactional
    public void getAllActivitiesByUserBriefIsInShouldWork() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where userBrief in DEFAULT_USER_BRIEF or UPDATED_USER_BRIEF
        defaultActivityShouldBeFound("userBrief.in=" + DEFAULT_USER_BRIEF + "," + UPDATED_USER_BRIEF);

        // Get all the activityList where userBrief equals to UPDATED_USER_BRIEF
        defaultActivityShouldNotBeFound("userBrief.in=" + UPDATED_USER_BRIEF);
    }

    @Test
    @Transactional
    public void getAllActivitiesByUserBriefIsNullOrNotNull() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where userBrief is not null
        defaultActivityShouldBeFound("userBrief.specified=true");

        // Get all the activityList where userBrief is null
        defaultActivityShouldNotBeFound("userBrief.specified=false");
    }

    @Test
    @Transactional
    public void getAllActivitiesByActivityDescIsEqualToSomething() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where activityDesc equals to DEFAULT_ACTIVITY_DESC
        defaultActivityShouldBeFound("activityDesc.equals=" + DEFAULT_ACTIVITY_DESC);

        // Get all the activityList where activityDesc equals to UPDATED_ACTIVITY_DESC
        defaultActivityShouldNotBeFound("activityDesc.equals=" + UPDATED_ACTIVITY_DESC);
    }

    @Test
    @Transactional
    public void getAllActivitiesByActivityDescIsInShouldWork() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where activityDesc in DEFAULT_ACTIVITY_DESC or UPDATED_ACTIVITY_DESC
        defaultActivityShouldBeFound("activityDesc.in=" + DEFAULT_ACTIVITY_DESC + "," + UPDATED_ACTIVITY_DESC);

        // Get all the activityList where activityDesc equals to UPDATED_ACTIVITY_DESC
        defaultActivityShouldNotBeFound("activityDesc.in=" + UPDATED_ACTIVITY_DESC);
    }

    @Test
    @Transactional
    public void getAllActivitiesByActivityDescIsNullOrNotNull() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where activityDesc is not null
        defaultActivityShouldBeFound("activityDesc.specified=true");

        // Get all the activityList where activityDesc is null
        defaultActivityShouldNotBeFound("activityDesc.specified=false");
    }

    @Test
    @Transactional
    public void getAllActivitiesByUserIsEqualToSomething() throws Exception {
        // Initialize the database
        User user = UserResourceIntTest.createEntity(em);
        em.persist(user);
        em.flush();
        activity.setUser(user);
        activityRepository.saveAndFlush(activity);
        Long userId = user.getId();

        // Get all the activityList where user equals to userId
        defaultActivityShouldBeFound("userId.equals=" + userId);

        // Get all the activityList where user equals to userId + 1
        defaultActivityShouldNotBeFound("userId.equals=" + (userId + 1));
    }


    @Test
    @Transactional
    public void getAllActivitiesByPostIsEqualToSomething() throws Exception {
        // Initialize the database
        Post post = PostResourceIntTest.createEntity(em);
        em.persist(post);
        em.flush();
        activity.setPost(post);
        activityRepository.saveAndFlush(activity);
        Long postId = post.getId();

        // Get all the activityList where post equals to postId
        defaultActivityShouldBeFound("postId.equals=" + postId);

        // Get all the activityList where post equals to postId + 1
        defaultActivityShouldNotBeFound("postId.equals=" + (postId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultActivityShouldBeFound(String filter) throws Exception {
        restActivityMockMvc.perform(get("/api/activities?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(activity.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT.toString())))
            .andExpect(jsonPath("$.[*].deleted").value(hasItem(DEFAULT_DELETED.booleanValue())))
            .andExpect(jsonPath("$.[*].reportReason").value(hasItem(DEFAULT_REPORT_REASON.toString())))
            .andExpect(jsonPath("$.[*].userBrief").value(hasItem(DEFAULT_USER_BRIEF.toString())))
            .andExpect(jsonPath("$.[*].activityDesc").value(hasItem(DEFAULT_ACTIVITY_DESC.toString())));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultActivityShouldNotBeFound(String filter) throws Exception {
        restActivityMockMvc.perform(get("/api/activities?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    @Transactional
    public void getNonExistingActivity() throws Exception {
        // Get the activity
        restActivityMockMvc.perform(get("/api/activities/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateActivity() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);
        activitySearchRepository.save(activity);
        int databaseSizeBeforeUpdate = activityRepository.findAll().size();

        // Update the activity
        Activity updatedActivity = activityRepository.findOne(activity.getId());
        // Disconnect from session so that the updates on updatedActivity are not directly saved in db
        em.detach(updatedActivity);
        updatedActivity
            .type(UPDATED_TYPE)
            .createdAt(UPDATED_CREATED_AT)
            .comment(UPDATED_COMMENT)
            .deleted(UPDATED_DELETED)
            .reportReason(UPDATED_REPORT_REASON)
            .userBrief(UPDATED_USER_BRIEF)
            .activityDesc(UPDATED_ACTIVITY_DESC);
        ActivityDTO activityDTO = activityMapper.toDto(updatedActivity);

        restActivityMockMvc.perform(put("/api/activities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(activityDTO)))
            .andExpect(status().isOk());

        // Validate the Activity in the database
        List<Activity> activityList = activityRepository.findAll();
        assertThat(activityList).hasSize(databaseSizeBeforeUpdate);
        Activity testActivity = activityList.get(activityList.size() - 1);
        assertThat(testActivity.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testActivity.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testActivity.getComment()).isEqualTo(UPDATED_COMMENT);
        assertThat(testActivity.isDeleted()).isEqualTo(UPDATED_DELETED);
        assertThat(testActivity.getReportReason()).isEqualTo(UPDATED_REPORT_REASON);
        assertThat(testActivity.getUserBrief()).isEqualTo(UPDATED_USER_BRIEF);
        assertThat(testActivity.getActivityDesc()).isEqualTo(UPDATED_ACTIVITY_DESC);

        // Validate the Activity in Elasticsearch
        Activity activityEs = activitySearchRepository.findOne(testActivity.getId());
        assertThat(activityEs).isEqualToIgnoringGivenFields(testActivity);
    }

    @Test
    @Transactional
    public void updateNonExistingActivity() throws Exception {
        int databaseSizeBeforeUpdate = activityRepository.findAll().size();

        // Create the Activity
        ActivityDTO activityDTO = activityMapper.toDto(activity);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restActivityMockMvc.perform(put("/api/activities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(activityDTO)))
            .andExpect(status().isCreated());

        // Validate the Activity in the database
        List<Activity> activityList = activityRepository.findAll();
        assertThat(activityList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteActivity() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);
        activitySearchRepository.save(activity);
        int databaseSizeBeforeDelete = activityRepository.findAll().size();

        // Get the activity
        restActivityMockMvc.perform(delete("/api/activities/{id}", activity.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean activityExistsInEs = activitySearchRepository.exists(activity.getId());
        assertThat(activityExistsInEs).isFalse();

        // Validate the database is empty
        List<Activity> activityList = activityRepository.findAll();
        assertThat(activityList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchActivity() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);
        activitySearchRepository.save(activity);

        // Search the activity
        restActivityMockMvc.perform(get("/api/_search/activities?query=id:" + activity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(activity.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT.toString())))
            .andExpect(jsonPath("$.[*].deleted").value(hasItem(DEFAULT_DELETED.booleanValue())))
            .andExpect(jsonPath("$.[*].reportReason").value(hasItem(DEFAULT_REPORT_REASON.toString())))
            .andExpect(jsonPath("$.[*].userBrief").value(hasItem(DEFAULT_USER_BRIEF.toString())))
            .andExpect(jsonPath("$.[*].activityDesc").value(hasItem(DEFAULT_ACTIVITY_DESC.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Activity.class);
        Activity activity1 = new Activity();
        activity1.setId(1L);
        Activity activity2 = new Activity();
        activity2.setId(activity1.getId());
        assertThat(activity1).isEqualTo(activity2);
        activity2.setId(2L);
        assertThat(activity1).isNotEqualTo(activity2);
        activity1.setId(null);
        assertThat(activity1).isNotEqualTo(activity2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ActivityDTO.class);
        ActivityDTO activityDTO1 = new ActivityDTO();
        activityDTO1.setId(1L);
        ActivityDTO activityDTO2 = new ActivityDTO();
        assertThat(activityDTO1).isNotEqualTo(activityDTO2);
        activityDTO2.setId(activityDTO1.getId());
        assertThat(activityDTO1).isEqualTo(activityDTO2);
        activityDTO2.setId(2L);
        assertThat(activityDTO1).isNotEqualTo(activityDTO2);
        activityDTO1.setId(null);
        assertThat(activityDTO1).isNotEqualTo(activityDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(activityMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(activityMapper.fromId(null)).isNull();
    }
}
