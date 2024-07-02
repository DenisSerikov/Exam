package com.dataPg.rest.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmployeeRepositoryImpl implements EmployeeRepository {

    private List<Employee> employees = new ArrayList<>().reversed();
    private int nextId = 1;

    @Override
    public List<Employee> findAll() {
        return employees;
    }

    @Override
    public Optional<Employee> findById(int id) {
        return employees.stream()
                .filter(emp -> emp.getId() == id)
                .findFirst();
    }

    @Override
    public Employee save(Employee employee) {
        if (employee.getId() == 0) {
            employee.setId(nextId++);
            employees.add(employee);
        } else {
            employees.removeIf(emp -> emp.getId() == employee.getId());
            employees.add(employee);
        }
        return employee;
    }

    @Override
    public void deleteById(int id) {
        employees.removeIf(emp -> emp.getId() == id);
    }

    @Override
    public boolean existsById(int id) {
        return employees.stream()
                .anyMatch(emp -> emp.getId() == id);
    }
}
