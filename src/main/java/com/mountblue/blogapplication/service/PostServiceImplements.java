package com.mountblue.blogapplication.service;

import com.mountblue.blogapplication.dto.RequestPostDto;
import com.mountblue.blogapplication.dto.ResponsePostDto;
import com.mountblue.blogapplication.entity.Comments;
import com.mountblue.blogapplication.entity.Post;
import com.mountblue.blogapplication.entity.Tags;
import com.mountblue.blogapplication.entity.Users;
import com.mountblue.blogapplication.enums.Role;
import com.mountblue.blogapplication.repository.CommentsRepository;
import com.mountblue.blogapplication.repository.PostRepository;
import com.mountblue.blogapplication.repository.TagsRepository;
import com.mountblue.blogapplication.repository.UsersRepository;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class PostServiceImplements implements PostService {

    private final PostRepository postRepository;
    private final TagsRepository tagsRepository;
    private final CommentsRepository commentsRepository;
    private final UsersRepository usersRepository;

    @Autowired
    public PostServiceImplements(PostRepository postRepository,
                                 TagsRepository tagsRepository,
                                 CommentsRepository commentsRepository,
                                 UsersRepository usersRepository) {
        this.postRepository = postRepository;
        this.tagsRepository = tagsRepository;
        this.commentsRepository = commentsRepository;
        this.usersRepository = usersRepository;
    }

    @Override
    public List<Users> findAllAuthors() {
        return usersRepository.findAll();
    }

    @Transactional
    @Override
    public void addComment(Long id, Comments comments) {
        Post post =postRepository.findById(id)
                        .orElseThrow(()-> new RuntimeException("Post not found"));
        post.addComments(comments);
        postRepository.save(post);
    }


    @Override
    public Post findById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(()->new RuntimeException("id not found"));
    }

    @Override
    public RequestPostDto findPostById(Long id) {
        Post post=postRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Post not found"));
        return toRequestDto(post);
    }

    @Override
    public Page<ResponsePostDto> findPost(
            Integer start, Integer limit, Long authorId,
            List<Long> tagIds, LocalDate publishedFrom,
            LocalDate publishedTo, String sortField,
            String order, String search) {

        validateSearchParameters(
                start,
                limit,
                sortField,
                order,
                publishedFrom,
                publishedTo
        );

        Pageable pageable=createPageable(start,limit,order);
        Specification<Post> specification=
                buildSpecification(
                    authorId,
                    tagIds,
                    publishedFrom,
                    publishedTo,
                    search
                );
        return postRepository.findAll(specification,pageable)
                .map(this::responseDto);
    }

    @Override
    public List<Tags> findAllTags() {
        return tagsRepository.findAllByOrderByIdAsc();
    }
    @Transactional
    @Override
    public void deletePost(Long id) {
        postRepository.findById(id).ifPresent(postRepository::delete);
    }

    @Override
    public Comments findCommentById(Long id) {
        return commentsRepository.findById(id).orElseThrow(()->new RuntimeException("comment not found"));
    }
    @Transactional
    @Override
    public void updateComment(Long commentId, String comments) {
        Comments comment=findCommentById(commentId);
        comment.setComment(comments);
        commentsRepository.save(comment);
    }
    @Transactional
    @Override
    public void deleteComment(Long id) {
        Comments comment=findCommentById(id);
        commentsRepository.delete(comment);
    }
    @Transactional
    @Override
    public void savePost(
            RequestPostDto dto,
            String tagName,
            Users currentUser) {

        Post post=dto.getId()==null
                    ? createPost(dto, currentUser)
                    :updatePost(dto,currentUser);

        updateTags(post,tagName);
        postRepository.save(post);
    }


    // helper methods

    private void validateSearchParameters(
            Integer start,
            Integer limit,
            String sortField,
            String order,
            LocalDate publishedFrom,
            LocalDate publishedTo){
        if (start==null||start<1) {
            start=1;
        }
        if (limit==null||limit<1||limit>10) {
            limit=10;
        }
        if(!"publishedAt".equals(sortField)){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Posts can only be sorted by publishedAt");
        }
        if (!"asc".equalsIgnoreCase(order)
                && !"desc".equalsIgnoreCase(order)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "order must be asc or desc"
            );
        }
        if(publishedFrom!=null&&publishedTo!=null&&publishedFrom.isAfter(publishedTo)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"publishedFrom must not be after publishedTo");
        }

    }

    private Pageable createPageable(Integer start, Integer limit, String order) {

        Sort.Direction direction=
                "asc".equalsIgnoreCase(order)
                            ?Sort.Direction.ASC
                            :Sort.Direction.DESC;
        int pageNo=(start-1)/limit;

        return PageRequest.
                of(pageNo,limit,Sort
                        .by(direction,"publishedAt"));
    }
    private Specification<Post> buildSpecification(
            Long authorId,
            List<Long> tagIds,
            LocalDate publishedFrom,
            LocalDate publishedTo,
            String search){

        Specification<Post> specification=
                (root,query,cb)->
                        cb.isTrue(root.get("isPublished"));

        if(authorId!=null){
            specification=
                    specification
                            .and(hasAuthor(authorId));
        }
        if(tagIds!=null && !tagIds.isEmpty()){
            specification=
                    specification
                            .and(hasTags(tagIds));
        }
        if(publishedFrom!=null){
            specification=
                    specification
                            .and(publishedAfterOrEqual(publishedFrom));
        }
        if(publishedTo !=null){
            specification=
                    specification
                            .and(publishedBeforeOrEqual(publishedTo));
        }
        if (search!=null && !search.isBlank()) {
            specification=
                    specification
                            .and(containsSearch(search));
        }

        return specification;
    }
    private Specification<Post> hasAuthor(Long authorId) {

        return (root, query, cb)->
                cb.equal(
                        root.get("author")
                                .get("id"), authorId);
    }
    private  Specification<Post> hasTags(List<Long> tagIds) {

        return (root, query, cb)-> {

            query.distinct(true);

            return  root.join("tags").get("id").in(tagIds);
        };
    }
    private Specification<Post> publishedAfterOrEqual(LocalDate publishedFrom) {
        LocalDateTime from = publishedFrom != null
                ? publishedFrom.atStartOfDay()
                : null;
        return (root, query, cb)->
                cb.greaterThanOrEqualTo(root.get("publishedAt"), from);
    }
    private Specification<Post> publishedBeforeOrEqual(LocalDate publishedTo) {
        LocalDateTime to = publishedTo != null
                ? publishedTo.atTime(LocalTime.MAX)
                : null;
        return (root, query, cb)->
                cb.greaterThanOrEqualTo(root.get("publishedAt"), to);
    }
    private Specification<Post> containsSearch(String search) {
        return (root, query, cb)->{

            query.distinct(true);

            String term="%"+search.trim().toLowerCase()+"%";

            Join<Post, Tags> join =
                    root.join("tags", JoinType.LEFT);

            return cb.or(
                    cb.like(cb.lower(root.get("title")),term),
                    cb.like(cb.lower(root.get("content")),term),
                    cb.like(cb.lower(root.get("author").get("name")),term),
                    cb.like(cb.lower(join.get("name")),term)
            );
        };
    }
    private Post createPost(
            RequestPostDto dto,
            Users currentUser) {
        Post post = requestDtoToPost(dto);
        post.setAuthor(resolveAuthor(dto,currentUser));
        post.setPublished(true);
        if (post.getPublishedAt()==null){
            post.setPublishedAt(LocalDateTime.now());
        }
        return post;
    }
    private Post updatePost(
            RequestPostDto dto,
            Users currentUser){
        Post post = postRepository.findById(dto.getId())
                    .orElseThrow(() ->
                            new ResponseStatusException(HttpStatus.NOT_FOUND));
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setPublishedAt(dto.getPublishedAt());
        post.setPublished(true);
        if (currentUser.getRole()==Role.ADMIN){
            post.setAuthor(resolveAuthor(dto,currentUser));
        }
        return post;
    }
    private Users resolveAuthor(
            RequestPostDto dto,
            Users currentUser) {
        if (currentUser.getRole()==Role.ADMIN){
            return currentUser;
        }
        return usersRepository.findByName(dto.getAuthor())
                .orElseThrow(()->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Author not found"));
    }

    private String normalizeTag(String tag){
        return Arrays.stream(tag.trim().split(" "))
                .map(word->
                        word.substring(0,1).toUpperCase()
                        +word.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }

    private void updateTags(Post post, String tagName){
        if(tagName==null||tagName.isBlank()){
            return;
        }
        for (String tag : tagName.split(",")) {
            String newTag = normalizeTag(tag);
            if(tag.isBlank()){
               continue;
            }
            Tags tags=tagsRepository.findByName(newTag)
                    .orElseGet(()->
                            tagsRepository.save(new Tags(newTag)));
            if (!post.getTags().contains(tags)) {
                post.addTags(tags);
            }
        }
    }

    private Post requestDtoToPost(RequestPostDto requestDto) {
        Post post = new Post();
        post.setId(requestDto.getId());
        post.setTitle(requestDto.getTitle());
        post.setExcerpt(requestDto.getExcerpt());
        post.setContent(requestDto.getContent());
        post.setPublishedAt(requestDto.getPublishedAt());
        post.setPublished(requestDto.isPublished());
        post.setCreatedAt(requestDto.getCreatedAt());
        post.setUpdatedAt(requestDto.getUpdatedAt());
        return post;
    }

    private RequestPostDto toRequestDto(Post post) {
        RequestPostDto requestDto = new RequestPostDto();
        requestDto.setId(post.getId());
        requestDto.setTitle(post.getTitle());
        requestDto.setExcerpt(post.getExcerpt());
        requestDto.setContent(post.getContent());
        requestDto.setAuthor(post.getAuthor().getName());
        requestDto.setPublishedAt(post.getPublishedAt());
        requestDto.setPublished(post.isPublished());
        requestDto.setCreatedAt(post.getCreatedAt());
        requestDto.setUpdatedAt(post.getUpdatedAt());
        requestDto.setComments(post.getComments());
        String tags=post.getTags()
                .stream()
                .map(Tags::getName)
                .collect(Collectors.joining(","));
        requestDto.setTags(tags);
        return requestDto;

    }
    private ResponsePostDto responseDto(Post  post) {
        ResponsePostDto responsePostDto =new ResponsePostDto();
        responsePostDto.setId(post.getId());
        responsePostDto.setTitle(post.getTitle());
        responsePostDto.setExcerpt(post.getExcerpt());
        responsePostDto.setContent(post.getContent());
        responsePostDto.setAuthor(post.getAuthor());
        responsePostDto.setPublishedAt(post.getPublishedAt());
        responsePostDto.setPublished(post.isPublished());
        responsePostDto.setCreatedAt(post.getCreatedAt());
        responsePostDto.setUpdatedAt(post.getUpdatedAt());
        String tags = post.getTags()
                .stream()
                .map(Tags::getName)
                .sorted()
                .collect(Collectors.joining(", "));
        responsePostDto.setTags(tags);
        responsePostDto.setComments(post.getComments());
        return responsePostDto;
    }
}
