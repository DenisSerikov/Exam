package com.dataPg.rest.control;

import com.dataPg.rest.RestApplication;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import com.dataPg.rest.model.Person;
import com.dataPg.rest.repository.PersonRepository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(classes = RestApplication.class)
@AutoConfigureMockMvc
class PersonControllerTest {

    @MockBean
    private PersonRepository store;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Test
    @WithMockUser
    void whenFindAll() throws Exception {
        mockMvc.perform(get("/person/"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void whenFindById() throws Exception {
        mockMvc.perform(get("/person/1"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"id\":0,\"login\":null,\"password\":null}"));
    }

    @Test
    @WithMockUser
    void whenCreate() throws Exception {
        mockMvc.perform(post("/person/sign-up")
                .content("{\"login\":\"user\",\"password\":\"root\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
        ArgumentCaptor<Person> argument = ArgumentCaptor.forClass(Person.class);
        verify(store).save(argument.capture());
        assertThat(argument.getValue().getLogin(), is("user"));
        assertTrue(encoder.matches("root", argument.getValue().getPassword()));
    }

    @Test
    @WithMockUser
    void whenUpdate() throws Exception {
        mockMvc.perform(put("/person/")
                .content("{\"id\":1,\"login\":\"user\",\"password\":\"root\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void whenDelete() throws Exception {
        mockMvc.perform(delete("/person/1"))
                .andDo(print())
                .andExpect(status().isOk());
    }
}