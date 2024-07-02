import com.dataPg.rest.model.Employee;
import com.dataPg.rest.model.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class EmployeePersonControllerTest {

    private EmployeePersonController empController;
    private RestTemplate restTemplate;

    @BeforeEach
    void setup() {
        restTemplate = new RestTemplate();
        empController = new EmployeePersonController(restTemplate);
    }

    @Test
    void whenGetEmployee() {
        ResponseEntity<List<Person>> responseEntity = new ResponseEntity<>(List.of(
                new Person(1, "person1", "person1"),
                new Person(2, "person2", "person2")
        ), HttpStatus.OK);

        Mockito.when(restTemplate.exchange(
                "http://localhost:8080/person/",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Person>>() {})
        ).thenReturn(responseEntity);

        ResponseEntity<Employee> resp = empController.getEmployee("token");
        assertThat(resp.getStatusCode(), is(HttpStatus.OK));
        assertThat(resp.getBody().getName(), is("Sergay")); 
        assertThat(resp.getBody().getTin(), is("9876543"));
        assertThat(resp.getBody().getAccounts().size(), is(2));
    }

    @Test
    void whenGetPersonByIdThenOk() {
        ResponseEntity<Person> responseEntity = new ResponseEntity<>(
                new Person(1, "person1", "password1"),
                HttpStatus.OK);
        Mockito.when(restTemplate.exchange(
                "http://localhost:8080/person/1",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Person>() {})
        ).thenReturn(responseEntity);

        ResponseEntity<Person> resp = empController.getPersonById(1, "token");
        assertThat(resp.getStatusCode(), is(HttpStatus.OK));
        assertThat(resp.getBody().getLogin(), is("person1"));
        assertThat(resp.getBody().getPassword(), is("password1"));
    }

    @Test
    void whenGetPersonByIdThenNotFound() {
        Mockito.when(restTemplate.exchange(
                "http://localhost:8080/person/1",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Person>() {})
        ).thenThrow(HttpClientErrorException.class);

        ResponseEntity<Person> resp = empController.getPersonById(1, "token");
        assertThat(resp.getStatusCode(), is(HttpStatus.NOT_FOUND));
        assertThat(resp.getBody().getId(), is(0));
        assertThat(resp.getBody().getLogin(), nullValue());
        assertThat(resp.getBody().getPassword(), nullValue());
    }

    @Test
    void whenCreatePerson() {
        Person newPerson = new Person("person1", "password1");
        Mockito.when(restTemplate.postForObject(
                "http://localhost:8080/person/sign-up",
                new HttpEntity<>(newPerson),
                Person.class)
        ).thenReturn(newPerson);

        ResponseEntity<Person> resp = empController.createPerson(new Person(), "token");
        assertThat(resp.getStatusCode(), is(HttpStatus.CREATED));
        assertThat(resp.getBody().getLogin(), is("person1")); 
        assertThat(resp.getBody().getPassword(), is("password1")); 
    }

    @Test
    void whenUpdatePerson() {
        Mockito.doNothing().when(restTemplate).put(
                "http://localhost:8080/person/",
                new HttpEntity<>(new Person())
        );

        ResponseEntity<Void> resp = empController.updatePerson(new Person(), "token");
        assertThat(resp.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    void whenDeletePerson() {
        Mockito.when(restTemplate.exchange(
                "http://localhost:8080/person/1",
                HttpMethod.DELETE,
                null,
                Void.class)
        ).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        ResponseEntity<Void> resp = empController.deletePerson(1, "token");
        assertThat(resp.getStatusCode(), is(HttpStatus.OK));
    }
}
