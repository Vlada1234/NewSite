package com.newsite.service;

import com.newsite.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.management.relation.RoleNotFoundException;
import java.util.Optional;

public interface UserService extends UserDetailsService {
    Optional<com.newsite.model.User> findByUsername(String username);
    com.newsite.model.User saveNewUser(User user) throws RoleNotFoundException;

}
