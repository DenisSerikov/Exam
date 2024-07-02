package com.dataPg.rest.control;

import com.dataPg.rest.RestApplication;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import com.dataPg.rest.model.Employee;
import com.dataPg.rest.repository.EmployeeRepository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = RestApplication.class)
@AutoConfigureMockMvc
class EmployeeContollerTest {
    @MockBean
    private EmployeeRepository store;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
    void whenFindAll() throws Exception {
        mockMvc.perform(get("/employee/"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void whenFindById() throws Exception {
        mockMvc.perform(get("/employee/1"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().json("{" +
                                "\"id\":0," +
                                "\"name\":null," +
                                "\"tin\":null," +
                                "\"hired\":null," +
                                "\"accounts\":[]" +
                        "}"));
    }

    @Test
    @WithMockUser
    void whenCreate() throws Exception {
        mockMvc.perform(post("/employee/")
                .content("{" +
                            "\"name\":\"Dmitry\"," +
                            "\"tin\":\"12345\"," +
                            "\"hired\":\"2021-01-01T11:52:00\"," +
                            "\"accounts\":[" +
                                "{\"login\":\"account1\",\"password\":\"root\"}," +
                                "{\"login\":\"account2\",\"password\":\"secret\"}" +
                            "]" +
                "}")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
        ArgumentCaptor<Employee> argument = ArgumentCaptor.forClass(Employee.class);
        verify(store).save(argument.capture());
        assertThat(argument.getValue().getName(), is("Dmitry"));
        assertThat(argument.getValue().getTin(), is("12345"));
    }

    @Test
    @WithMockUser
    void whenUpdate() throws Exception {
        mockMvc.perform(put("/employee/")
                .content("{" +
                            "\"id\":1," +
                            "\"name\":\"Dmitry\"," +
                            "\"tin\":\"12345\"," +
                            "\"hired\":\"2021-01-01T11:52:00\"," +
                            "\"accounts\":[" +
                                "{\"id\":1,\"login\":\"account1\",\"password\":\"root\"}," +
                                "{\"id\":2,\"login\":\"account2\",\"password\":\"secret\"}" +
                            "]" +
                "}")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void whenDelete() throws Exception {
        mockMvc.perform(delete("/employee/1"))
                .andDo(print())
                .andExpect(status().isOk());
    }
}