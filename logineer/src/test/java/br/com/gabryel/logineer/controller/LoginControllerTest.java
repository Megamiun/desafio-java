package br.com.gabryel.logineer.controller;

import br.com.gabryel.logineer.dto.UserDTO;
import br.com.gabryel.logineer.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestContext.class})
@WebAppConfiguration
public class LoginControllerTest {

    @Autowired
    private UserRepository phoneRepository;

    @Autowired
    private UserRepository userRepository;

    private MockMvc mockMvc;

    @Before
    public void clean() {
        phoneRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void givenAnUserDTOWithNewEmail_whenRequestedCreation_thenCreateNewUserWithSameInfo() throws Exception {
        mockMvc.perform(
            put("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getText("joao-da-silva.json")))
            .andExpect(jsonPath("name", is("João da Silva")))
            .andExpect(jsonPath("email", is("joao@silva.org")))
            .andExpect(jsonPath("password", is("hunter2")))
            .andExpect(jsonPath("phones[1].number", is("987654321")))
            .andExpect(jsonPath("phones[1].ddd", is("21")));
    }

    @Test
    public void givenAnUserDTOWithNewEmail_whenRequestedCreation_thenReturnsOk() throws Exception {
        mockMvc.perform(
            put("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getText("joao-da-silva.json")))
            .andExpect(status().isOk());
    }

    @Test
    public void givenAnUserDTOWithRepeatedEmail_whenRequestedCreation_thenReturnsNotAcceptable() throws Exception {
        userRepository.save(new UserDTO("Jão", "joao@silva.org", "myPass").toUser());

        mockMvc.perform(
            put("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getText("joao-da-silva.json")))
            .andExpect(status().isNotAcceptable());
    }

    private String getText(String resourceName) throws IOException, URISyntaxException {
        Path path = Paths.get(getClass().getResource(resourceName).toURI());
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }
}
