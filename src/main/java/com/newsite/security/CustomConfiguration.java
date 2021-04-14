package com.newsite.security;

import com.newsite.repository.AuthorityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class CustomConfiguration extends WebSecurityConfigurerAdapter {

    private final DataSource dataSource;

    private static final String USERS_SQL_QUERY = "select username, password, enabled from users where username = ?";

    private static final String AUTHORITIES_SQL_QUERY = "select users.username, authorities.authority\n" +
            "from users\n" +
            "inner join users_authorities on (users.id = users_authorities.user_id)\n" +
            "inner join authorities on (users_authorities.authority_id = authorities.id)\n" +
            "where users.username = ?;";

    @Autowired
    public CustomConfiguration(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers( "/createNewPost/*", "/editPost/", "/deletePost/").hasRole("USER")
                .antMatchers().hasRole("USER")
                .antMatchers("/*").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login").loginProcessingUrl("/login")
                .usernameParameter("username").passwordParameter("password")
                .defaultSuccessUrl("/").failureUrl("/login?error")
                .permitAll()
                .and()
                .rememberMe().rememberMeParameter("remember-me")
                .and()
                .logout().logoutUrl("/logout").logoutSuccessUrl("/login?logout")
                .permitAll()
                .and();

    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {

        authenticationManagerBuilder
                .jdbcAuthentication()
                .usersByUsernameQuery(USERS_SQL_QUERY)
                .authoritiesByUsernameQuery(AUTHORITIES_SQL_QUERY)
                .dataSource(dataSource)
                .passwordEncoder(bCryptPasswordEncoder());

    }






}
