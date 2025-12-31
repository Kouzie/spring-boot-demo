package com.example.keycloak.controller;

import com.example.keycloak.model.Post;
import com.example.keycloak.repository.PostRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostRepository postRepository;

    /**
     * 게시글 작성 - create_post 권한 필요
     * @PreAuthorize 어노테이션으로 권한 체크
     */
    @PostMapping
    @PreAuthorize("hasRole('create_post')")
    public Post createPost(@RequestBody PostDto postDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        log.info("User {} created a post", username);
        String id = UUID.randomUUID().toString();
        Post post = new Post(id, postDto.title(), postDto.content(), username, null);
        return postRepository.save(post);
    }

    /**
     * 게시글 삭제 - 세부 권한 체크
     * - delete_any_post: 모든 게시글 삭제 가능 (manager_group)
     * - delete_own_post: 자신의 게시글만 삭제 가능 (member_group)
     * 
     * @PreAuthorize로 기본 권한 체크 (delete_any_post 또는 delete_own_post 권한 필요)
     * 세부 로직(본인 게시글 여부)은 메서드 내부에서 처리
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('delete_any_post') or hasRole('delete_own_post')")
    public void deletePost(@PathVariable String id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        
        boolean hasDeleteAnyPost = hasAuthority(authentication, "ROLE_delete_any_post");
        boolean hasDeleteOwnPost = hasAuthority(authentication, "ROLE_delete_own_post");
        boolean isOwnPost = post.author().equals(currentUsername);
        
        if (!hasDeleteAnyPost && (!hasDeleteOwnPost || !isOwnPost)) {
            String reason = hasDeleteOwnPost 
                    ? "You can only delete your own posts (delete_own_post permission)"
                    : "You don't have permission to delete posts";
            log.warn("User {} attempted to delete post {} but was denied: {}", 
                    currentUsername, id, reason);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, reason);
        }

        log.info("User {} successfully deleted post {}", currentUsername, id);
        postRepository.deleteById(id);
    }

    private boolean hasAuthority(Authentication authentication, String authority) {
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(authority));
    }

    @GetMapping
    public Iterable<Post> listPosts() {
        return postRepository.findAll();
    }

    public record PostDto(String title, String content) {
    }
}
