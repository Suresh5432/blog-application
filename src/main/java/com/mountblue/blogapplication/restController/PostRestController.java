package com.mountblue.blogapplication.restController;

import com.mountblue.blogapplication.dto.RequestPostDto;
import com.mountblue.blogapplication.dto.ResponsePostDto;
import com.mountblue.blogapplication.dto.RestPostResponseDto;
import com.mountblue.blogapplication.entity.Post;
import com.mountblue.blogapplication.security.CustomUserDetails;
import com.mountblue.blogapplication.service.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
public class PostRestController {

    private final PostService postService;

    @Autowired
    public PostRestController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/posts")
    public Page<RestPostResponseDto> getPost(@RequestParam(required = false,defaultValue = "1")
                                                Integer start,
                                             @RequestParam(required = false, defaultValue = "10")
                                                    Integer limit,
                                             @RequestParam(required = false)
                                                    Long authorId,
                                             @RequestParam(required = false, name="tagId")
                                                    List<Long> tagIds,
                                             @RequestParam(required = false)
                                                 @DateTimeFormat(pattern = "dd/MM/yyyy")
                                                    LocalDate publishedFrom,
                                             @RequestParam(required = false)
                                                @DateTimeFormat(pattern = "dd/MM/yyyy")
                                                    LocalDate publishedTo,
                                             @RequestParam(required = false, defaultValue = "publishedAt")
                                                    String sortField,
                                             @RequestParam(defaultValue = "desc")
                                                    String order,
                                             @RequestParam(required = false)
                                                    String search) {
          return postService.findRestPosts(
                  start,limit,
                  authorId,
                  tagIds,
                  publishedFrom,
                  publishedTo,
                  sortField,
                  order,
                  search);

    }

    @GetMapping("/posts/{id}")
    public RestPostResponseDto showPost(@PathVariable Long id) {
        return postService.findRestPostById(id);
    }

    //@PreAuthorize("hasAnyRole('AUTHOR', 'ADMIN')")
    @PostMapping("/posts")
    public RestPostResponseDto createPost(
            @Valid @RequestBody RequestPostDto post,
                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        post.setPublishedAt(LocalDateTime.now());
         Post posts=postService.savePost(post,
                post.getTags()
                 ,userDetails.getUser());
        return postService.findRestPostById(posts.getId());
    }

    //@PreAuthorize("hasRole('ADMIN')or@postSecurity.isOwner(#id,authentication)")
    @PutMapping("/posts/{id}")
    public RestPostResponseDto updatePost(@PathVariable Long id,
                             @Valid @RequestBody RequestPostDto post,
                             @AuthenticationPrincipal CustomUserDetails userDetails) {

        post.setId(id);
        postService.savePost(post, post.getTags(),userDetails.getUser());

        return postService.findRestPostById(id);
    }
    //@PreAuthorize("hasRole('ADMIN')or@postSecurity.isOwner(#id,authentication)")
    @DeleteMapping("/posts/{id}")
    public void deletePost(@PathVariable Long id) {
        postService.deletePost(id);
    }
}
