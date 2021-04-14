package com.newsite.service;

import com.newsite.model.Authority;
import com.newsite.model.User;
import com.newsite.repository.AuthorityRepository;
import com.newsite.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.management.relation.RoleNotFoundException;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private static final String DEFAULT_ROLE = "USER";
    private final BCryptPasswordEncoder bcryptEncoder;
    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;

    @Autowired
    public UserServiceImpl(BCryptPasswordEncoder bcryptEncoder, UserRepository userRepository, AuthorityRepository authorityRepository) {
        this.bcryptEncoder = bcryptEncoder;
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        if(user.isPresent()) {
            return user.get();
        } else {
            throw new UsernameNotFoundException("No user found with username " + username);
        }
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User saveNewUser(User user) throws RoleNotFoundException {
        System.err.println("saveNewUser: " + user);

        user.setPassword(this.bcryptEncoder.encode(user.getPassword()));
        user.setEnabled(true);
        Optional<Authority> optionalAuthority = this.authorityRepository.findByAuthority(DEFAULT_ROLE);
        if (optionalAuthority.isPresent()) {
            Authority authority = optionalAuthority.get();
            Collection<Authority> authorities = Collections.singletonList(authority);
            user.setAuthorities(authorities);
            return this.userRepository.saveAndFlush(user);
        } else {
            throw new RoleNotFoundException("Default role not found for user with username " + user.getUsername());
        }
    }


}
