package com.dataPg.rest.control;

import com.dataPg.rest.RestApplication;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import com.dataPg.rest.model.Employee;
import com.dataPg.rest.model.Person;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(classes = RestApplication.class)
class EmployeePersonControllerTestMockito {
    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private EmployeePersonController empController = new EmployeePersonController();

    @Test
    @WithMockUser
    void whenGetEmployee() {
        Mockito
                .when(restTemplate.exchange(
                        ArgumentMatchers.eq("http://localhost:8080/person/"),
                        ArgumentMatchers.eq(HttpMethod.GET),
                        ArgumentMatchers.<HttpEntity<List<Void>>>any(),
                        ArgumentMatchers.<ParameterizedTypeReference<List<Person>>>any())
                )
          .thenReturn(new ResponseEntity<>(List.of(
                  new Person(1, "person1", "person1"),
                  new Person(2, "person2", "person2")
          ), HttpStatus.OK));

        ResponseEntity<Employee> resp = empController.getEmployee("token");
        assertThat(resp.getStatusCode(), is(HttpStatus.OK));
        assertThat(resp.getBody().getName(), is("Yugay"));
        assertThat(resp.getBody().getTin(), is("9876543"));
        assertThat(resp.getBody().getAccounts().size(), is(2));
    }

    @Test
    @WithMockUser
    void whenGetPersonByIdThenOk() {
        Mockito
                .when(restTemplate.exchange(
                        ArgumentMatchers.eq("http://localhost:8080/person/1"),
                        ArgumentMatchers.eq(HttpMethod.GET),
                        ArgumentMatchers.<HttpEntity<List<Void>>>any(),
                        ArgumentMatchers.<ParameterizedTypeReference<Person>>any())
                )
                .thenReturn(new ResponseEntity<>(
                        new Person(1, "person1", "password1"),
                        HttpStatus.OK)
                );

        ResponseEntity<Person> resp = empController.getPersonById(1, "token");
        assertThat(resp.getStatusCode(), is(HttpStatus.OK));
        assertThat(resp.getBody().getLogin(), is("person1"));
        assertThat(resp.getBody().getPassword(), is("password1"));
    }

    @Test
    @WithMockUser
    void whenGetPersonByIdThenNotFound() {
        Mockito
                .when(restTemplate.exchange(
                        ArgumentMatchers.eq("http://localhost:8080/person/1"),
                        ArgumentMatchers.eq(HttpMethod.GET),
                        ArgumentMatchers.<HttpEntity<List<Void>>>any(),
                        ArgumentMatchers.<ParameterizedTypeReference<Person>>any())
                )
                .thenThrow(HttpClientErrorException.class);

        ResponseEntity<Person> resp = empController.getPersonById(1, "token");
        assertThat(resp.getStatusCode(), is(HttpStatus.NOT_FOUND));
        assertThat(resp.getBody().getId(), is(0));
        assertThat(resp.getBody().getLogin(), nullValue());
        assertThat(resp.getBody().getPassword(), nullValue());
    }

    @Test
    void whenCreatePerson() {
        Mockito
                .when(restTemplate.postForObject(
                        ArgumentMatchers.eq("http://localhost:8080/person/sign-up"),
                        ArgumentMatchers.<HttpEntity<List<Void>>>any(),
                        ArgumentMatchers.eq(Person.class))
                )
                .thenReturn(new Person("person1", "password1"));

        ResponseEntity<Person> resp = empController.createPerson(new Person(), "token");
        assertThat(resp.getStatusCode(), is(HttpStatus.CREATED));
        assertThat(resp.getBody().getLogin(), is("person1"));
        assertThat(resp.getBody().getPassword(), is("password1"));
    }

    @Test
    void whenUpdatePerson() {
        Mockito.doNothing().when(restTemplate);
        ResponseEntity<Void> resp = empController.updatePerson(new Person(), "token");
        assertThat(resp.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    void whenDeletePerson() {
        Mockito
                .when(restTemplate.exchange(
                        ArgumentMatchers.eq("http://localhost:8080/person/1"),
                        ArgumentMatchers.eq(HttpMethod.DELETE),
                        ArgumentMatchers.<HttpEntity<List<Void>>>any(),
                        ArgumentMatchers.<ParameterizedTypeReference<Void>>any())
                )
                .thenReturn(
                        new ResponseEntity<>(HttpStatus.OK)
                );
        ResponseEntity<Void> resp = empController.deletePerson(1, "token");
        assertThat(resp.getStatusCode(), is(HttpStatus.OK));
    }
}