package com.newsite.controller;

import com.newsite.model.Comment;
import com.newsite.model.Post;
import com.newsite.model.User;
import com.newsite.service.CommentService;
import com.newsite.service.PostService;
import com.newsite.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Optional;

@Controller
@SessionAttributes("comment")
public class CommentController {

    private final PostService postService;
    private final UserService userService;
    private final CommentService commentService;


    @Autowired
    public CommentController(PostService postService, UserService userService, CommentService commentService) {
        this.postService = postService;
        this.userService = userService;
        this.commentService = commentService;
    }


    @GetMapping("/comment/{id}")
    public String showComment(@PathVariable Long id, Model model, Principal principal) {

        String authUsername = "anonymousUser";
        if(principal != null) {
            authUsername = principal.getName();
        }

        Optional<User> optionalUser = this.userService.findByUsername(authUsername);

        Optional<Post> optionalPost = this.postService.getById(id);

        if(optionalUser.isPresent() && optionalPost.isPresent()  ) {
            Comment comment = new Comment();
            comment.setPost(optionalPost.get());
            comment.setUser(optionalUser.get());
            model.addAttribute("comment", comment);
            return "commentForm";
        } else {
            return "404";
        }

    }


    @PostMapping("/comment")
    public String validateComment(@Valid @ModelAttribute Comment comment, Principal principal) {
        User user = userService.findByUsername(principal.getName()).get();
        comment.setUser(user);
        this.commentService.save(comment);
        return  "redirect:/post/" + comment.getPost().getId();
    }


}
