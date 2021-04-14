package com.newsite.service;

import com.newsite.model.Post;
import com.newsite.model.User;

import java.util.Collection;
import java.util.Optional;

public interface PostService {
    Optional<Post> getById(Long id);
    Collection<Post> getAll();
    Post save(Post post);
    void delete(Post post);
}
