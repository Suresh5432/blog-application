package com.mountblue.blogapplication.controller;

import com.mountblue.blogapplication.entity.Comments;
import com.mountblue.blogapplication.entity.Users;
import com.mountblue.blogapplication.security.CustomUserDetails;
import com.mountblue.blogapplication.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
public class CommentsController {

    private final PostService postService;

    @GetMapping("/posts/{id}/comment")
    public String viewComments(@PathVariable Long id,
                               @AuthenticationPrincipal CustomUserDetails userDetails,
                               Model model) {
        model.addAttribute("id",id);
        if(userDetails!=null) {
            model.addAttribute("currentUser", userDetails.getUser());
        }
        return "comments";
    }

    @PostMapping("/posts/{id}/comments")
    public String saveComment(@PathVariable Long id,
                              @RequestParam(required = false) String name,
                              @RequestParam(required = false) String email,
                              @RequestParam String comment,
                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        if(userDetails!=null) {
            Users currentUser = userDetails.getUser();
            name=currentUser.getName();
            email=currentUser.getEmail();
        }
        Comments comments=new Comments(name,email,comment);
        postService.addComment(id, comments);
        return "redirect:/posts/" +id;
    }
    @PreAuthorize("hasRole('ADMIN')or@postSecurity.isOwner(#postId,authentication)")
    @GetMapping("/posts/{postId}/comments/{commentId}/edit")
    public String editComments(@PathVariable Long postId,
                               @PathVariable Long commentId,
                               Model model,
                               @AuthenticationPrincipal CustomUserDetails userDetails) {
        Comments comments=postService.findCommentById(commentId);
        model.addAttribute("comment",comments);
        model.addAttribute("postId",postId);
        return "edit-comment";
    }
    @PreAuthorize("hasRole('ADMIN')or@postSecurity.isOwner(#postId,authentication)")
    @PostMapping("/posts/{postId}/comments/{commentId}/edit")
    public String updateComment(@PathVariable Long postId,
                                @PathVariable Long commentId,
                                @RequestParam String comment) {
        postService.updateComment(commentId,comment);
        return "redirect:/posts/" +postId;
    }
    @PreAuthorize("hasRole('ADMIN')or@postSecurity.isOwner(#postId,authentication)")
    @PostMapping("/posts/{postId}/comments/{commentId}/delete")
    public String deleteComment(@PathVariable Long postId, @PathVariable Long commentId) {
        postService.deleteComment(commentId);
        return "redirect:/posts/" +postId;
    }
}
