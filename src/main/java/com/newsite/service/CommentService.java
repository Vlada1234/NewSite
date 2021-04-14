package com.newsite.service;

import com.newsite.model.Comment;
import org.springframework.boot.CommandLineRunner;

public interface CommentService {
    Comment save(Comment comment);

    void delete(Comment comment);
}
