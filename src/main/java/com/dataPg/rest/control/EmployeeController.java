package com.dataPg.rest.control;

import com.dataPg.rest.model.Employee;
import com.dataPg.rest.repository.EmployeeRepository;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/employee/*")
public class EmployeeController extends HttpServlet {

    private final EmployeeRepository employeeRepository;

    public EmployeeController(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            List<Employee> employees = (List<Employee>) employeeRepository.findAll();
            writeResponse(resp, employees, HttpServletResponse.SC_OK);
        } else {
            String[] splits = pathInfo.split("/");
            if (splits.length == 2) {
                int id = Integer.parseInt(splits[1]);
                Optional<Employee> employee = employeeRepository.findById(id);
                if (employee.isPresent()) {
                    writeResponse(resp, employee.get(), HttpServletResponse.SC_OK);
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Employee employee = new ObjectMapper().readValue(req.getInputStream(), Employee.class);
        Employee savedEmployee = employeeRepository.save(employee);
        writeResponse(resp, savedEmployee, HttpServletResponse.SC_CREATED);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Employee employee = new ObjectMapper().readValue(req.getInputStream(), Employee.class);
        employeeRepository.save(employee);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo != null && pathInfo.startsWith("/")) {
            String[] splits = pathInfo.split("/");
            if (splits.length == 2) {
                int id = Integer.parseInt(splits[1]);
                if (employeeRepository.existsById(id)) {
                    employeeRepository.deleteById(id);
                    resp.setStatus(HttpServletResponse.SC_OK);
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void writeResponse(HttpServletResponse resp, Object object, int status) throws IOException {
        resp.setContentType("application/json");
        resp.setStatus(status);
        PrintWriter out = resp.getWriter();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(out, object);
        out.flush();
    }
}
