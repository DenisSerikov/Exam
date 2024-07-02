package com.dataPg.rest.auth;

import com.dataPg.rest.model.Person;
import com.dataPg.rest.repository.PersonRepository;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PersonDetailsServiceImpl {

    private PersonRepository personRepository;

    public PersonDetailsServiceImpl(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Person user = personRepository.findByLogin(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        return new User(user.getLogin(), user.getPassword(), new ArrayList<>());
    }

    // Example of PersonRepository without Spring
    public static class PersonRepository {
        private DataSource dataSource;

        public PersonRepository() {
            try {
                Context initContext = new InitialContext();
                Context envContext = (Context) initContext.lookup("java:/comp/env");
                dataSource = (DataSource) envContext.lookup("jdbc/YourDataSource");
            } catch (NamingException e) {
                throw new RuntimeException(e);
            }
        }

        public Person findByLogin(String login) {
            String query = "SELECT * FROM person WHERE login = ?";
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, login);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    return new Person(rs.getInt("id"), rs.getString("login"), rs.getString("password"));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return null;
        }
    }

    // Custom UserDetails implementation
    public static class User implements UserDetails {
        private String username;
        private String password;
        private List<String> roles;

        public User(String username, String password, List<String> roles) {
            this.username = username;
            this.password = password;
            this.roles = roles;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            List<GrantedAuthority> authorities = new ArrayList<>();
            for (String role : roles) {
                authorities.add(new SimpleGrantedAuthority(role));
            }
            return authorities;
        }

        @Override
        public String getPassword() {
            return password;
        }

        @Override
        public String getUsername() {
            return username;
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }

    // Custom UsernameNotFoundException
    public static class UsernameNotFoundException extends Exception {
        public UsernameNotFoundException(String message) {
            super(message);
        }
    }

    // Custom GrantedAuthority implementation
    public static class SimpleGrantedAuthority implements GrantedAuthority {
        private String authority;

        public SimpleGrantedAuthority(String authority) {
            this.authority = authority;
        }

        @Override
        public String getAuthority() {
            return authority;
        }
    }
}
