package com.dataPg.rest.control;

import com.dataPg.rest.model.Person;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@WebServlet("/person/*")
public class PersonController extends HttpServlet {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final String jdbcURL = "jdbc:mysql://localhost:3306/yourdatabase";
    private final String jdbcUsername = "yourusername";
    private final String jdbcPassword = "yourpassword";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            List<Person> persons = findAll();
            writeResponse(resp, persons, HttpServletResponse.SC_OK);
        } else {
            int id = Integer.parseInt(pathInfo.substring(1));
            Optional<Person> person = findById(id);
            if (person.isPresent()) {
                writeResponse(resp, person.get(), HttpServletResponse.SC_OK);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo != null && pathInfo.equals("/sign-up")) {
            Person person = new ObjectMapper().readValue(req.getInputStream(), Person.class);
            String passwordEncoded = encoder.encode(person.getPassword());
            person.setPassword(passwordEncoded);
            Person savedPerson = create(person);
            writeResponse(resp, savedPerson, HttpServletResponse.SC_CREATED);
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Person person = new ObjectMapper().readValue(req.getInputStream(), Person.class);
        String passwordEncoded = encoder.encode(person.getPassword());
        person.setPassword(passwordEncoded);
        update(person);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo != null && pathInfo.startsWith("/")) {
            int id = Integer.parseInt(pathInfo.substring(1));
            if (delete(id)) {
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private List<Person> findAll() {
        List<Person> persons = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM persons")) {
            while (resultSet.next()) {
                Person person = new Person();
                person.setId(resultSet.getInt("id"));
                person.setLogin(resultSet.getString("login"));
                person.setPassword(resultSet.getString("password"));
                persons.add(person);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return persons;
    }

    private Optional<Person> findById(int id) {
        try (Connection connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM persons WHERE id = ?")) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Person person = new Person();
                    person.setId(resultSet.getInt("id"));
                    person.setLogin(resultSet.getString("login"));
                    person.setPassword(resultSet.getString("password"));
                    return Optional.of(person);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private Person create(Person person) {
        try (Connection connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO persons (login, password) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, person.getLogin());
            statement.setString(2, person.getPassword());
            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        person.setId(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return person;
    }

    private void update(Person person) {
        try (Connection connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE persons SET login = ?, password = ? WHERE id = ?")) {
            statement.setString(1, person.getLogin());
            statement.setString(2, person.getPassword());
            statement.setInt(3, person.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean delete(int id) {
        try (Connection connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM persons WHERE id = ?")) {
            statement.setInt(1, id);
            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void writeResponse(HttpServletResponse resp, Object object, int status) throws IOException {
        resp.setContentType("application/json");
        resp.setStatus(status);
        new ObjectMapper().writeValue(resp.getWriter(), object);
    }
}

class BCryptPasswordEncoder {
    public String encode(String password) {
        // Implement your own password encoding logic or use a library like jBCrypt
        return password; // This is just a placeholder
    }
}
