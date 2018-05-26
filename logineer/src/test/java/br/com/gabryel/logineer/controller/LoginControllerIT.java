package br.com.gabryel.logineer.controller;

import br.com.gabryel.logineer.LogineerApplication;
import br.com.gabryel.logineer.dto.UserDto;
import br.com.gabryel.logineer.dto.UserTokenDto;
import br.com.gabryel.logineer.entities.User;
import br.com.gabryel.logineer.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.kotlin.KotlinModule;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = LogineerApplication.class)
@AutoConfigureMockMvc
public class LoginControllerIT {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository phoneRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Before
    public void clean() {
        phoneRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
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

    @Test
    public void givenXmlContentHeader_whenRequestedLogin_thenReturnsUnsupportedMediaType() throws Exception {
        mockMvc.perform(
            post("/api/login")
                .contentType(MediaType.APPLICATION_XML)
                .content(getText("joao-da-silva-login.xml")))
            .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    public void givenAnLoginDtoOfAnUser_whenRequestedLogin_thenReturnsUserInfo() throws Exception {
        LocalDate date = LocalDate.of(2018, Month.MAY, 25);
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
            .andExpect(jsonPath("created", is("2018-05-25")))
            .andExpect(jsonPath("modified", is("2018-05-25")))
            .andExpect(jsonPath("last_login", is("2018-05-25")));
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

    @Test
    public void givenAnIdOnPathAndNoToken_whenRequestedProfile_thenReturnsUnauthorized() throws Exception {
        User user = createBaseUser("João", "joao@silva.org", "hunter2");
        userRepository.save(user);

        mockMvc
            .perform(get("/api/user/" + user.getId()))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void givenAnIdOnPathAndNoToken_whenRequestedProfile_thenReturnsUnauthorizedMessage() throws Exception {
        User user = createBaseUser("João", "joao@silva.org", "hunter2");
        userRepository.save(user);

        mockMvc
            .perform(get("/api/user/" + user.getId()))
            .andExpect(jsonPath("message", is("Não autorizado")));
    }

    @Test
    public void givenAnIdOnPathAndInvalidToken_whenRequestedProfile_thenReturnsUnauthorized() throws Exception {
        User user = createBaseUser("João", "joao@silva.org", "hunter2");
        userRepository.save(user);

        mockMvc.perform(
            get("/api/user/" + user.getId())
                .header("access_token", "invalid_token")
                .header("token_type", "Bearer"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void givenAnIdOnPathAndInvalidToken_whenRequestedProfile_thenReturnsUnauthorizedMessage() throws Exception {
        User user = createBaseUser("João", "joao@silva.org", "hunter2");
        userRepository.save(user);

        mockMvc.perform(
            get("/api/user/" + user.getId())
                .header("access_token", "invalid_token")
                .header("token_type", "Bearer"))
            .andExpect(jsonPath("message", is("Não autorizado")));
    }

    @Test
    public void givenAnIdOnPathAndTokenWithLastLoginTooOld_whenRequestedProfile_thenReturnsUnauthorized() throws Exception {
        UserTokenDto token = registerUserFromFile("joao-da-silva.json");
        User user = userRepository.getOne(token.getId());
        user.setLastLogin(user.getLastLogin().minusMinutes(31));
        userRepository.save(user);

        mockMvc.perform(
            get("/api/user/" + token.getId())
                .header("access_token", token.getToken())
                .header("token_type", "Bearer"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void givenAnIdOnPathAndTokenWithLastLoginTooOld_whenRequestedProfile_thenReturnsInvalidSessionMessage() throws Exception {
        UserTokenDto token = registerUserFromFile("joao-da-silva.json");
        User user = userRepository.getOne(token.getId());
        user.setLastLogin(user.getLastLogin().minusMinutes(31));
        userRepository.save(user);

        mockMvc.perform(
            get("/api/user/" + user.getId())
                .header("access_token", user.getToken())
                .header("token_type", "Bearer"))
            .andExpect(jsonPath("message", is("Sessão Inválida")));
    }

    @Test
    public void givenAnIdOnPathAndToken_whenRequestedProfile_thenReturnsProfile() throws Exception {
        UserTokenDto token = registerUserFromFile("joao-da-silva.json");

        mockMvc.perform(
            get("/api/user/" + token.getId())
                .header("access_token", token.getToken())
                .header("token_type", "Bearer"))
            .andExpect(jsonPath("name", is("João da Silva")))
            .andExpect(jsonPath("email", is("joao@silva.org")))
            .andExpect(jsonPath("phones[1].number", is("987654321")))
            .andExpect(jsonPath("phones[1].ddd", is("21")))
            .andExpect(jsonPath("created", is("2018-05-25")))
            .andExpect(jsonPath("modified", is("2018-05-25")))
            .andExpect(jsonPath("last_login", is("2018-05-25")));
    }

    @Test
    public void givenAnIdOnPathAndToken_whenRequestedProfile_thenReturnsOk() throws Exception {
        UserTokenDto token = registerUserFromFile("joao-da-silva.json");

        mockMvc.perform(
            get("/api/user/" + token.getId())
                .header("access_token", token.getToken())
                .header("token_type", "Bearer"))
            .andExpect(status().isOk());
    }

    private UserTokenDto registerUserFromFile(String fileName) throws Exception {
        MvcResult result = mockMvc.perform(
            put("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getText(fileName)))
            .andReturn();

        ObjectMapper mapper = new ObjectMapper()
            .registerModule(new KotlinModule())
            .registerModule(new JavaTimeModule());
        return mapper.readValue(result.getResponse().getContentAsString(), UserTokenDto.class);
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
