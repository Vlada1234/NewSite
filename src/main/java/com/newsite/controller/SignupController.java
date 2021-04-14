package com.newsite.controller;

import com.newsite.model.User;
import com.newsite.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import javax.management.relation.RoleNotFoundException;
import javax.validation.Valid;

@Controller
@SessionAttributes("user")
public class SignupController {

    private final UserService userService;

    @Autowired
    public SignupController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/signup")
    public String getRegisterForm(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "registerForm";
    }

    @PostMapping("/register")
    public String registerNewUser(@Valid @ModelAttribute User user, BindingResult bindingResult, SessionStatus sessionStatus) throws RoleNotFoundException {
        if (userService.findByUsername(user.getUsername()).isPresent()) {
            bindingResult.rejectValue("username", "error.username", "Username is already registered try other one.");
            System.err.println("Username already taken error message");
        }
        this.userService.saveNewUser(user);
        Authentication auth = new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        sessionStatus.setComplete();
        return "redirect:/";
    }

}
