package com.newsite.controller;

import com.newsite.model.Post;
import com.newsite.model.User;
import com.newsite.service.PostService;
import com.newsite.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Optional;

@Controller
public class PostController {

    private final PostService postService;
    private final UserService userService;

    @Autowired
    public PostController(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    @GetMapping("/post/{id}")
    public String getPost(@PathVariable Long id, Model model, Principal principal) {
        String authUsername = "anonymousUser";
        if (principal != null) {
            authUsername = principal.getName();
        }

        Optional<Post> optionalPost = this.postService.getById(id);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            model.addAttribute("post", post);
            model.addAttribute("isOwner", true);
            if (authUsername.equals(post.getUser().getUsername())) {
                model.addAttribute("isOwner", true);
            }
            return "post";
        } else {
            return "404";
        }
    }

    @GetMapping("/createNewPost")
    public String createNewPost(Model model) {
        return "postForm";
    }


    @PostMapping("/createNewPost")
    public String createNewPost(@Valid @ModelAttribute Post post, Principal principal) {
        User user = userService.findByUsername(principal.getName()).get();
        post.setUser(user);
        this.postService.save(post);
        return "redirect:/post/" + post.getId();
    }

    @Secured("USER")
    @GetMapping("editPost/{id}")
    public String editPost(@PathVariable Long id, Model model, Principal principal) {
        String authUsername = "anonymousUser";
        if (principal != null) {
            authUsername = principal.getName();
        }

        Optional<Post> optionalPost = this.postService.getById(id);

        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();

            if (authUsername.equals(post.getUser().getUsername())) {
                model.addAttribute("post", post);
                return "postForm";
            } else {
                return "403";
            }

        } else {
            return "error";
        }
    }

    @Secured("USER")
    @GetMapping("/deletePost/{id}")
    public String deletePost(@PathVariable Long id, Principal principal) {
        String authUsername = "anonymousUser";
        if (principal != null) {
            authUsername = principal.getName();
        }

        Optional<Post> optionalPost = this.postService.getById(id);

        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            if (authUsername.equals(post.getUser().getUsername())) {
                this.postService.delete(post);
                return "redirect:/";
            } else {
                return "403";
            }
    } else {
        return "error";
    }
}
}
