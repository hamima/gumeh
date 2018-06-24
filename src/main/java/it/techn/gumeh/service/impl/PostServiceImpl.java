package it.techn.gumeh.service.impl;

import it.techn.gumeh.domain.Post;
import it.techn.gumeh.domain.Resurce;
import it.techn.gumeh.domain.Tag;
import it.techn.gumeh.domain.User;
import it.techn.gumeh.repository.PostRepository;
import it.techn.gumeh.repository.ResurceRepository;
import it.techn.gumeh.repository.TagRepository;
import it.techn.gumeh.repository.search.PostSearchRepository;
import it.techn.gumeh.service.PostService;
import it.techn.gumeh.service.UserService;
import it.techn.gumeh.service.dto.PostDTO;
import it.techn.gumeh.service.mapper.PostMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

/**
 * Service Implementation for managing Post.
 */
@Service
@Transactional
public class PostServiceImpl implements PostService {

    private final Logger log = LoggerFactory.getLogger(PostServiceImpl.class);

    private final PostRepository postRepository;

    private final PostMapper postMapper;

    private final PostSearchRepository postSearchRepository;

    private final UserService userService;

    private final TagRepository tagRepository;

    private final ResurceRepository resurceRepository;

    public PostServiceImpl(PostRepository postRepository, PostMapper postMapper,
                           PostSearchRepository postSearchRepository, UserService userService,
                           TagRepository tagRepository, ResurceRepository resurceRepository) {
        this.postRepository = postRepository;
        this.postMapper = postMapper;
        this.postSearchRepository = postSearchRepository;
        this.userService = userService;
        this.tagRepository = tagRepository;
        this.resurceRepository = resurceRepository;
    }

    /**
     * Save a post.
     *
     * @param postDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public PostDTO save(PostDTO postDTO) {
        log.debug("Request to save Post : {}", postDTO);
        Post post = postMapper.toEntity(postDTO);
        Optional<User> currentUserOptional = userService.getUserWithAuthorities();
        User currentUser = null;
        if(currentUserOptional.isPresent()) currentUser = currentUserOptional.get();
        post.setUserBrief(currentUser.getFirstName() + " " + currentUser.getLastName());

        post = postRepository.save(post);

        String tagStr = post.getTagStr();
        String[] tags = tagStr.split(",");
        List<Tag> tagList = new ArrayList<>();
        for(String tag: tags) {
            Optional<Tag> tagOptional = tagRepository.findByTitle(tag);
            if(tagOptional.isPresent()){
                Tag tagObj = tagOptional.get();
                tagObj.setNoPosts(tagObj.getNoPosts() + 1);
                tagList.add(tagObj);
            }
        }
        tagRepository.save(tagList);

        Resurce category = post.getCategory();
        category.setNoPosts(category.getNoPosts()+1);
        resurceRepository.save(category);

        PostDTO result = postMapper.toDto(post);
        postSearchRepository.save(post);
        return result;
    }

    /**
     * Get all the posts.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<PostDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Posts");
        return postRepository.findAll(pageable)
            .map(postMapper::toDto);
    }

    /**
     * Get one post by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public PostDTO findOne(Long id) {
        log.debug("Request to get Post : {}", id);
        Post post = postRepository.findOne(id);
        return postMapper.toDto(post);
    }

    /**
     * Delete the post by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Post : {}", id);
        postRepository.delete(id);
        postSearchRepository.delete(id);
    }

    /**
     * Search for the post corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<PostDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Posts for query {}", query);
        Page<Post> result = postSearchRepository.search(queryStringQuery(query), pageable);
        return result.map(postMapper::toDto);
    }
}
