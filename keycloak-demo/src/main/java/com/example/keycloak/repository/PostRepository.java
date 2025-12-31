package com.example.keycloak.repository;

import com.example.keycloak.model.Post;
import org.springframework.data.repository.CrudRepository;

public interface PostRepository extends CrudRepository<Post, String> {
}
