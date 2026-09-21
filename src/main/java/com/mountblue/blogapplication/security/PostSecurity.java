package com.mountblue.blogapplication.security;

import com.mountblue.blogapplication.entity.Post;
import com.mountblue.blogapplication.entity.Users;
import com.mountblue.blogapplication.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component("postSecurity")
@RequiredArgsConstructor
public class PostSecurity {
    private final PostRepository postRepository;

    public boolean isOwner(Long postId, Authentication authentication) {
        if (authentication == null ||
                !authentication.isAuthenticated() ||
                !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            return false;
        }
        Users currentUser = userDetails.getUser();
        Post post=postRepository.findById(postId).orElse(null);
        if(post==null){
            return false;
        }
        return post.getAuthor().getId().equals(currentUser.getId());
    }
}
