package com.mountblue.blogapplication.controller;

import com.mountblue.blogapplication.dto.RequestPostDto;
import com.mountblue.blogapplication.dto.ResponsePostDto;
import com.mountblue.blogapplication.entity.Comments;
import com.mountblue.blogapplication.entity.Post;
import com.mountblue.blogapplication.entity.Users;
import com.mountblue.blogapplication.security.CustomUserDetails;
import com.mountblue.blogapplication.service.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class PostController {
    private PostService postService;
    @Autowired
    public void setPostService(PostService postService) {
        this.postService = postService;
    }
    @GetMapping("/posts")
    public String showPost(@RequestParam(required = false,defaultValue = "1") Integer start,
                            @RequestParam(required = false, defaultValue = "10")  Integer limit,
                            @RequestParam(required = false) Long authorId,
                            @RequestParam(required = false, name="tagId") List<Long> tagIds,
                            @RequestParam(required = false)
                            @DateTimeFormat(pattern = "dd/MM/yyyy")
                               LocalDate publishedFrom,
                            @RequestParam(required = false)
                               @DateTimeFormat(pattern = "dd/MM/yyyy")
                               LocalDate publishedTo,
                            @RequestParam(required = false, defaultValue = "publishedAt") String sortField,
                            @RequestParam(defaultValue = "desc") String order,
                            @RequestParam(required = false) String search,
                            Model model) {
        Page<ResponsePostDto> posts=postService.findPost(
                start,limit,authorId,tagIds,publishedFrom,
                publishedTo,sortField,order,search);
        model.addAttribute("posts",posts);
        model.addAttribute("start",start);
        model.addAttribute("limit",limit);
        model.addAttribute("authorId",authorId);
        model.addAttribute("tagIds",tagIds==null?List.of():tagIds);
        model.addAttribute("publishedFrom",publishedFrom);
        model.addAttribute("publishedTo",publishedTo);
        model.addAttribute("sortField",sortField);
        model.addAttribute("order",order);
        model.addAttribute("search",search);
        model.addAttribute("allAuthors",postService.findAllAuthors());
        model.addAttribute("allTags",postService.findAllTags());
        return "posts";
    }
    @GetMapping("/posts/{id}")
    public String showPost(@PathVariable Long id, Model model) {
        RequestPostDto post=postService.findPostById(id);
        model.addAttribute("post",post);
        return "post";
    }
    @PreAuthorize("hasAnyRole('AUTHOR','ADMIN')")
    @GetMapping("/newpost")
    public String createPost(@AuthenticationPrincipal CustomUserDetails userDetails,
                             Model model) {
        model.addAttribute("post",new RequestPostDto());
        model.addAttribute("allAuthors",postService.findAllAuthors());
        model.addAttribute("allTags",postService.findAllTags());
        return "create-post";
    }
    @PreAuthorize("hasAnyRole('AUTHOR', 'ADMIN')")
    @PostMapping("/posts/create")
    public String savePost(@Valid @ModelAttribute("post") RequestPostDto post,
                           BindingResult theResult,
                           @RequestParam("tags") String tags,
                           @AuthenticationPrincipal CustomUserDetails userDetails,
                           Model model) {
        if(theResult.hasErrors()) {
            model.addAttribute("allAuthors",postService.findAllAuthors());
            model.addAttribute("allTags",postService.findAllTags());
            model.addAttribute("currentUser", userDetails.getUser());
            return "create-post";
        }
        post.setPublishedAt(LocalDateTime.now());
        postService.savePost(post, tags,userDetails.getUser());
        return "redirect:/posts";
    }
    @PreAuthorize("hasRole('ADMIN')or@postSecurity.isOwner(#id,authentication)")
    @GetMapping("/posts/{id}/edit")
    public String editPost(@PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
                           Model model) {
        RequestPostDto post = postService.findPostById(id);
        model.addAttribute("post", post);
        model.addAttribute("allAuthors",postService.findAllAuthors());
        model.addAttribute("allTags",postService.findAllTags());
        model.addAttribute("currentUser", userDetails.getUser());
        return "create-post";
    }
    @PreAuthorize("hasRole('ADMIN')or@postSecurity.isOwner(#id,authentication)")
    @PostMapping("/posts/{id}/edit")
    public String updatePost(@PathVariable Long id,
            @Valid @ModelAttribute("post") RequestPostDto post,
            BindingResult result,
            @RequestParam("tags") String tags,
                             @AuthenticationPrincipal CustomUserDetails userDetails,
                             Model model) {
        if(result.hasErrors()) {
            post.setId(id);
            model.addAttribute("allAuthors", postService.findAllAuthors());
            model.addAttribute("allTags", postService.findAllTags());
            return "create-post";
        }
        post.setId(id);
        postService.savePost(post, tags,userDetails.getUser());
        return "redirect:/posts/" + id;
    }
    @PreAuthorize("hasRole('ADMIN')or@postSecurity.isOwner(#id,authentication)")
    @PostMapping("/posts/{id}/delete")
    public String deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return "redirect:/posts";
    }
}
