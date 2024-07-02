package com.dataPg.rest.control;

import com.dataPg.rest.model.Employee;
import com.dataPg.rest.model.Person;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/person-employee/*")
public class EmployeePersonController extends HttpServlet {

    private static final String API = "http://localhost:8080/person/";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        String token = req.getHeader("Authorization");

        if (pathInfo == null || pathInfo.equals("/")) {
            List<Person> accounts;
            try {
                accounts = fetchPersons(token);
                Employee employee = new Employee("Yugay", "9876543", null, accounts);
                writeResponse(resp, employee, HttpServletResponse.SC_OK);
            } catch (HttpClientErrorException ex) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        } else {
            int id = Integer.parseInt(pathInfo.substring(1));
            try {
                Person person = fetchPersonById(id, token);
                writeResponse(resp, person, HttpServletResponse.SC_OK);
            } catch (HttpClientErrorException ex) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, IOException {
        String token = req.getHeader("Authorization");
        Person person = new ObjectMapper().readValue(req.getInputStream(), Person.class);

        try {
            Person createdPerson = createPerson(person, token);
            writeResponse(resp, createdPerson, HttpServletResponse.SC_CREATED);
        } catch (HttpClientErrorException ex) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String token = req.getHeader("Authorization");
        Person person = new ObjectMapper().readValue(req.getInputStream(), Person.class);

        try {
            updatePerson(person, token);
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (HttpClientErrorException ex) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        String token = req.getHeader("Authorization");

        if (pathInfo != null && pathInfo.startsWith("/")) {
            int id = Integer.parseInt(pathInfo.substring(1));
            try {
                deletePerson(id, token);
                resp.setStatus(HttpServletResponse.SC_OK);
            } catch (HttpClientErrorException ex) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private List<Person> fetchPersons(String token) throws IOException, HttpClientErrorException {
        HttpURLConnection connection = createConnection(API, "GET", token);
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            return new ObjectMapper().readValue(connection.getInputStream(), new ObjectMapper().getTypeFactory().constructCollectionType(List.class, Person.class));
        } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
            throw new HttpClientErrorException("Unauthorized");
        }
        throw new IOException("Error fetching persons");
    }

    private Person fetchPersonById(int id, String token) throws IOException, HttpClientErrorException {
        HttpURLConnection connection = createConnection(API + id, "GET", token);
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            return new ObjectMapper().readValue(connection.getInputStream(), Person.class);
        } else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
            throw new HttpClientErrorException("Not Found");
        }
        throw new IOException("Error fetching person by ID");
    }

    private Person createPerson(Person person, String token) throws IOException, HttpClientErrorException {
        HttpURLConnection connection = createConnection(API + "sign-up", "POST", token);
        connection.setDoOutput(true);
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = new ObjectMapper().writeValueAsBytes(person);
            os.write(input, 0, input.length);
        }
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_CREATED) {
            return new ObjectMapper().readValue(connection.getInputStream(), Person.class);
        } else {
            throw new HttpClientErrorException("Bad Request");
        }
    }

    private void updatePerson(Person person, String token) throws IOException, HttpClientErrorException {
        HttpURLConnection connection = createConnection(API, "PUT", token);
        connection.setDoOutput(true);
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = new ObjectMapper().writeValueAsBytes(person);
            os.write(input, 0, input.length);
        }
        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new HttpClientErrorException("Bad Request");
        }
    }

    private void deletePerson(int id, String token) throws IOException, HttpClientErrorException {
        HttpURLConnection connection = createConnection(API + id, "DELETE", token);
        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new HttpClientErrorException("Not Found");
        }
    }

    private HttpURLConnection createConnection(String urlString, String method, String token) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("Authorization", token);
        connection.setRequestProperty("Content-Type", "application/json");
        return connection;
    }

    private void writeResponse(HttpServletResponse resp, Object object, int status) throws IOException {
        resp.setContentType("application/json");
        resp.setStatus(status);
        new ObjectMapper().writeValue(resp.getWriter(), object);
    }

    private static class HttpClientErrorException extends Exception {
        public HttpClientErrorException(String message) {
            super(message);
        }
    }
}
