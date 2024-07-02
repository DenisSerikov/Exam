import com.dataPg.rest.model.Person;
import com.dataPg.rest.repository.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PersonControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PersonRepository store;

    @Mock
    private BCryptPasswordEncoder encoder;

    @InjectMocks
    private PersonController personController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(personController).build();
    }

    @Test
    void whenFindAll() throws Exception {
        mockMvc.perform(get("/person/"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void whenFindById() throws Exception {
        Mockito.when(store.findById(1)).thenReturn(null);

        mockMvc.perform(get("/person/1"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"id\":0,\"login\":null,\"password\":null}"));
    }

    @Test
    void whenCreate() throws Exception {
        Person person = new Person("user", "root");
        Mockito.when(encoder.encode(anyString())).thenReturn("encodedPassword");

        mockMvc.perform(post("/person/sign-up")
                .content("{\"login\":\"user\",\"password\":\"root\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());

        ArgumentCaptor<Person> argument = ArgumentCaptor.forClass(Person.class);
        verify(store).save(argument.capture());
        assertThat(argument.getValue().getLogin(), is("user"));
        assertThat(argument.getValue().getPassword(), is("encodedPassword"));
    }

    @Test
    void whenUpdate() throws Exception {
        Person person = new Person(1, "user", "root");
        Mockito.when(store.save(any())).thenReturn(person);

        mockMvc.perform(put("/person/")
                .content("{\"id\":1,\"login\":\"user\",\"password\":\"root\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void whenDelete() throws Exception {
        mockMvc.perform(delete("/person/1"))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
