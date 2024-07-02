import com.dataPg.rest.model.Employee;
import com.dataPg.rest.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EmployeeControllerTest {

    private EmployeeRepository employeeRepository;
    private EmployeeController employeeController;

    @BeforeEach
    public void setup() {
        employeeRepository = new EmployeeRepositoryStub(); // Используем заглушку для репозитория
        employeeController = new EmployeeController(employeeRepository);
    }

    @Test
    void testFindAllEmployees() {
        List<Employee> employees = employeeController.findAll();
        assertEquals(2, employees.size());
    }

    @Test
    void testFindEmployeeById() {
        Optional<Employee> employee = employeeController.findById(1);
        assertTrue(employee.isPresent());
        assertEquals(1, employee.get().getId());
    }

    @Test
    void testFindEmployeeById_NotFound() {
        Optional<Employee> employee = employeeController.findById(3);
        assertFalse(employee.isPresent());
    }

    @Test
    void testCreateEmployee() {
        Employee newEmployee = new Employee("Dmitry", "12345", null, new ArrayList<>());
        Employee savedEmployee = employeeController.create(newEmployee);
        assertEquals(3, savedEmployee.getId());
        assertEquals("Dmitry", savedEmployee.getName());
    }

    @Test
    void testUpdateEmployee() {
        Employee updatedEmployee = new Employee(1, "UpdatedName", "54321", null, new ArrayList<>());
        Employee updated = employeeController.update(updatedEmployee);
        assertEquals("UpdatedName", updated.getName());
    }

    @Test
    void testDeleteEmployee() {
        employeeController.delete(1);
        assertEquals(1, employeeController.findAll().size());
    }

    // Заглушка для репозитория Employee
    private static class EmployeeRepositoryStub implements EmployeeRepository {

        private List<Employee> employees = new ArrayList<>();

        public EmployeeRepositoryStub() {
            employees.add(new Employee(1, "John Doe", "123456", null, new ArrayList<>()));
            employees.add(new Employee(2, "Jane Doe", "654321", null, new ArrayList<>()));
        }

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
                employee.setId(employees.size() + 1); // Простой способ генерации нового ID
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
}
