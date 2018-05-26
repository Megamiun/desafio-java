package br.com.gabryel.logineer.controller;

import br.com.gabryel.logineer.dto.UserDto;
import br.com.gabryel.logineer.entities.User;
import br.com.gabryel.logineer.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestContext.class})
@WebAppConfiguration
public class LoginControllerTest {

    @Autowired
    private WebApplicationContext webAppContext;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

    public void givenXmlContentHeader_whenRequestedCreation_thenReturnsUnsupportedMediaType() throws Exception {
        mockMvc.perform(
            put("/api/user")
                .contentType(MediaType.APPLICATION_XML)
                .content(getText("joao-da-silva.xml")))
            .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    public void givenAnUserDtoWithNewEmail_whenRequestedCreation_thenDontReturnPassword() throws Exception {
        mockMvc.perform(
            put("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getText("joao-da-silva.json")))
            .andExpect(jsonPath("password").doesNotExist());
    }

    @Test
    public void givenAnUserDtoWithNewEmail_whenRequestedCreation_thenCreateNewUserWithSameInfo() throws Exception {
        mockMvc.perform(
            put("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getText("joao-da-silva.json")))
            .andExpect(jsonPath("name", is("João da Silva")))
            .andExpect(jsonPath("email", is("joao@silva.org")))
            .andExpect(jsonPath("phones[1].number", is("987654321")))
            .andExpect(jsonPath("phones[1].ddd", is("21")));
    }

    @Test
    public void givenAnUserDtoWithNewEmail_whenRequestedCreation_thenReturnsOk() throws Exception {
        mockMvc.perform(
            put("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getText("joao-da-silva.json")))
            .andExpect(status().isOk());
    }

    @Test
    public void givenAnUserDtoWithRepeatedEmail_whenRequestedCreation_thenReturnsNotAcceptable() throws Exception {
        userRepository.save(createBaseUser("Jão", "joao@silva.org", "myPass"));

        mockMvc.perform(
            put("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getText("joao-da-silva.json")))
            .andExpect(status().isNotAcceptable());
    }

    @Test
    public void givenAnUserDtoWithRepeatedEmail_whenRequestedCreation_thenReturnsAlreadyExistingEmailMessage() throws Exception {
        userRepository.save(createBaseUser("Jão", "joao@silva.org", "myPass"));

        mockMvc.perform(
            put("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getText("joao-da-silva.json")))
            .andExpect(jsonPath("mensagem", is("E-mail já existente")));
    }

    public void givenXmlContentHeader_whenRequestedLogin_thenReturnsUnsupportedMediaType() throws Exception {
        mockMvc.perform(
            post("/api/login")
                .contentType(MediaType.APPLICATION_XML)
                .content(getText("joao-da-silva-login.xml")))
            .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    public void givenAnLoginDtoOfAnUser_whenRequestedLogin_thenReturnsUserInfo() throws Exception {
        LocalDate date = LocalDate.of(2018, Month.MAY, 26);
        LocalDateTime time = date.atStartOfDay();

        User user = createBaseUser("Jão", "joao@silva.org", "myPass");
        user.setToken("MyToken");
        user.setCreated(date);
        user.setModified(date);
        user.setLastLogin(time);
        userRepository.save(user);

        mockMvc.perform(
            post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getText("joao-da-silva-login.json")))
            .andExpect(jsonPath("name", is("João da Silva")))
            .andExpect(jsonPath("email", is("joao@silva.org")))
            .andExpect(jsonPath("phones[1].number", is("987654321")))
            .andExpect(jsonPath("phones[1].ddd", is("21")))
            .andExpect(jsonPath("token", is("MyToken")))
            .andExpect(jsonPath("created", is("2018-05-26")))
            .andExpect(jsonPath("modified", is("2018-05-26")))
            .andExpect(jsonPath("last_login", is("2018-05-26")));
    }

    @Test
    public void givenAnLoginDtoOfAnUser_whenRequestedLogin_thenDontReturnPassword() throws Exception {
        User user = createBaseUser("Jão", "joao@silva.org", "myPass");
        userRepository.save(user);

        mockMvc.perform(
            post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getText("joao-da-silva-login.json")))
            .andExpect(jsonPath("password").doesNotExist());
    }

    @Test
    public void givenAnLoginDtoOfAnUserWithWrongPassword_whenRequestedLogin_thenReturnsInvalidUserMessage() throws Exception {
        User user = createBaseUser("João", "joao@silva.org", "hunter2");
        userRepository.save(user);

        mockMvc.perform(
            post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getText("joao-da-silva-login.json")))
            .andExpect(jsonPath("message", is("Usuário e/ou senha inválidos")));
    }

    @Test
    public void givenAnLoginDtoOfAnUserWithWrongPassword_whenRequestedLogin_thenReturnsUnauthorized() throws Exception {
        User user = createBaseUser("João", "joao@silva.org", "hunter2");
        userRepository.save(user);

        mockMvc.perform(
            post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getText("joao-da-silva-login.json")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void givenAnLoginDtoOfANonUser_whenRequestedLogin_thenReturnsInvalidUserMessage() throws Exception {
        mockMvc.perform(
            post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getText("joao-da-silva-login.json")))
            .andExpect(jsonPath("message", is("Usuário e/ou senha inválidos")));
    }

    @Test
    public void givenAnLoginDtoOfANonUser_whenRequestedLogin_thenReturnsUnauthorized() throws Exception {
        mockMvc.perform(
            post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getText("joao-da-silva-login.json")))
            .andExpect(status().isUnauthorized());
    }

    private User createBaseUser(String name, String email, String password) {
        String encoded = passwordEncoder.encode(password);
        return new UserDto(name, email, encoded).toUser();
    }

    private String getText(String resourceName) throws IOException, URISyntaxException {
        Path path = Paths.get(getClass().getResource(resourceName).toURI());
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }
}
