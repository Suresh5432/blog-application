package com.mountblue.blogapplication.restController;

import com.mountblue.blogapplication.dto.RestCommentResponseDto;
import com.mountblue.blogapplication.entity.Comments;
import com.mountblue.blogapplication.entity.Users;
import com.mountblue.blogapplication.security.CustomUserDetails;
import com.mountblue.blogapplication.service.CommentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CommentsRestController {
    private final CommentsService commentsService;

    @Autowired
    public CommentsRestController(CommentsService commentsService) {
        this.commentsService = commentsService;
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<RestCommentResponseDto>> getComments(
            @PathVariable Long postId) {

        List<RestCommentResponseDto> comments =
                commentsService.findAllByPostId(postId);

        return ResponseEntity.ok(comments);
    }

    @GetMapping("/comments/{commentId}")
    public ResponseEntity<RestCommentResponseDto> getComment(
            @PathVariable Long commentId) {

        RestCommentResponseDto comment = commentsService.findCommentResponseById(commentId);

        return ResponseEntity.ok(comment);
    }

    // Add comment to a post
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<String> addComment(
            @PathVariable Long postId,
            @RequestBody Comments comment,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        commentsService.addComment(postId, comment,userDetails.getUser());

        return ResponseEntity.ok("Comment added successfully");
    }

    // Update comment
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<String> updateComment(
            @PathVariable Long commentId,
            @RequestBody String comment,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        commentsService.updateComment(commentId, comment,userDetails.getUser());

        return ResponseEntity.ok("Comment updated successfully");
    }

    // Delete comment
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<String> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        commentsService.deleteComment(commentId, userDetails.getUser());

        return ResponseEntity.ok("Comment deleted successfully");
    }
}
