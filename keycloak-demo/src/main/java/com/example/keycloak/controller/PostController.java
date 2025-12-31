package com.example.keycloak.controller;

import com.example.keycloak.model.Post;
import com.example.keycloak.repository.PostRepository;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostRepository postRepository;

    public PostController(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @PostMapping
    public Post createPost(@RequestBody PostDto postDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.info("User {} created a post", username);

        String id = UUID.randomUUID().toString();
        Post post = new Post(id, postDto.title(), postDto.content(), username, null);
        return postRepository.save(post);
    }

    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable String id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        boolean isManager = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_manager"));

        if (!post.author().equals(currentUsername) && !isManager) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You can only delete your own posts unless you are a manager");
        }

        postRepository.deleteById(id);
    }

    @GetMapping
    public Iterable<Post> listPosts() {
        return postRepository.findAll();
    }

    public record PostDto(String title, String content) {
    }
}
