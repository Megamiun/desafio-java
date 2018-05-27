package br.com.gabryel.logineer.controller;

import br.com.gabryel.logineer.LogineerApplication;
import br.com.gabryel.logineer.dto.UserDto;
import br.com.gabryel.logineer.entities.User;
import br.com.gabryel.logineer.exceptions.LogineerException;
import br.com.gabryel.logineer.repository.PhoneRepository;
import br.com.gabryel.logineer.repository.UserRepository;
import br.com.gabryel.logineer.service.TimeProvider;
import br.com.gabryel.logineer.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.kotlin.KotlinModule;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = LogineerApplication.class)
@AutoConfigureMockMvc
public class LoginControllerIT {

    @MockBean
    private TimeProvider timeProvider;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PhoneRepository phoneRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Before
    public void clean() {
        phoneRepository.deleteAll();
        userRepository.deleteAll();

        LocalDate date = LocalDate.of(2018, Month.MAY, 25);
        LocalDateTime time = date.atTime(12, 12);
        when(timeProvider.now()).thenReturn(time);
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
            .andExpect(jsonPath("phones[0].number", is("987654321")))
            .andExpect(jsonPath("phones[0].ddd", is("21")));
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
        registerUserFromFile("joao-da-silva.json");

        mockMvc.perform(
            put("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getText("joao-da-silva.json")))
            .andExpect(status().isNotAcceptable());
    }

    @Test
    public void givenAnUserDtoWithRepeatedEmail_whenRequestedCreation_thenReturnsAlreadyExistingEmailMessage() throws Exception {
        registerUserFromFile("joao-da-silva.json");

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
        User user = registerUserFromFile("joao-da-silva.json");

        when(timeProvider.now())
            .thenReturn(LocalDateTime.of(2018, Month.MAY, 25, 12, 43));

        mockMvc.perform(
            post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getText("joao-da-silva-login.json")))
            .andExpect(jsonPath("name", is("João da Silva")))
            .andExpect(jsonPath("email", is("joao@silva.org")))
            .andExpect(jsonPath("phones[0].number", is("987654321")))
            .andExpect(jsonPath("phones[0].ddd", is("21")))
            .andExpect(jsonPath("created", is("2018-05-25")))
            .andExpect(jsonPath("modified", is("2018-05-25")))
            .andExpect(jsonPath("last_login", is("2018-05-25T12:43:00")))
            .andExpect(jsonPath("token", is(user.getToken())))
            .andExpect(jsonPath("id", is(user.getId())));
    }

    @Test
    public void givenAnLoginDtoOfAnUser_whenRequestedLogin_thenDontReturnPassword() throws Exception {
        registerUserFromFile("joao-da-silva.json");

        mockMvc.perform(
            post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getText("joao-da-silva-login.json")))
            .andExpect(jsonPath("password").doesNotExist());
    }

    @Test
    public void givenAnLoginDtoOfAnUserWithWrongPassword_whenRequestedLogin_thenReturnsInvalidUserMessage() throws Exception {
        registerUserFromFile("joao-da-silva.json");

        mockMvc.perform(
            post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getText("joao-da-silva-login-wrong-pass.json")))
            .andExpect(jsonPath("mensagem", is("Usuário e/ou senha inválidos")));
    }

    @Test
    public void givenAnLoginDtoOfAnUserWithWrongPassword_whenRequestedLogin_thenReturnsUnauthorized() throws Exception {
        User user = registerUserFromFile("joao-da-silva.json");

        mockMvc.perform(
            post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getText("joao-da-silva-login-wrong-pass.json")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void givenAnLoginDtoOfANonUser_whenRequestedLogin_thenReturnsInvalidUserMessage() throws Exception {
        mockMvc.perform(
            post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getText("joao-da-silva-login.json")))
            .andExpect(jsonPath("mensagem", is("Usuário e/ou senha inválidos")));
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
        User user = registerUserFromFile("joao-da-silva.json");

        mockMvc
            .perform(get("/api/user/" + user.getId()))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void givenAnIdOnPathAndNoToken_whenRequestedProfile_thenReturnsUnauthorizedMessage() throws Exception {
        User user = registerUserFromFile("joao-da-silva.json");

        mockMvc
            .perform(get("/api/user/" + user.getId()))
            .andExpect(jsonPath("mensagem", is("Não autorizado")));
    }

    @Test
    public void givenAnIdOnPathAndInvalidToken_whenRequestedProfile_thenReturnsUnauthorized() throws Exception {
        User user = registerUserFromFile("joao-da-silva.json");

        mockMvc.perform(
            get("/api/user/" + user.getId())
                .header("Authorization", "Bearer I-m-a-bear"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void givenAnIdOnPathAndInvalidToken_whenRequestedProfile_thenReturnsUnauthorizedMessage() throws Exception {
        User user = registerUserFromFile("joao-da-silva.json");

        mockMvc.perform(
            get("/api/user/" + user.getId())
                .header("Authorization", "Bearer I-m-a-bear"))
            .andExpect(jsonPath("mensagem", is("Não autorizado")));
    }

    @Test
    public void givenAnIdOnPathAndTokenWithLastLoginTooOld_whenRequestedProfile_thenReturnsUnauthorized() throws Exception {
        User user = registerUserFromFile("joao-da-silva.json");

        when(timeProvider.now())
            .thenReturn(LocalDateTime.of(2018, Month.MAY, 25, 12, 43));

        mockMvc.perform(
            get("/api/user/" + user.getId())
                .header("Authorization", "Bearer I-m-a-bear"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void givenAnIdOnPathAndTokenWithLastLoginTooOld_whenRequestedProfile_thenReturnsInvalidSessionMessage() throws Exception {
        User user = registerUserFromFile("joao-da-silva.json");

        when(timeProvider.now())
            .thenReturn(LocalDateTime.of(2018, Month.MAY, 25, 12, 43));

        mockMvc.perform(
            get("/api/user/" + user.getId())
                .header("Authorization", "Bearer " + user.getToken()))
            .andExpect(jsonPath("mensagem", is("Sessão inválida")));
    }

    @Test
    public void givenAnIdOnPathAndToken_whenRequestedProfile_thenReturnsProfile() throws Exception {
        User user = registerUserFromFile("joao-da-silva.json");

        when(timeProvider.now())
            .thenReturn(LocalDateTime.of(2018, Month.MAY, 25, 12, 20));

        mockMvc.perform(
            get("/api/user/" + user.getId())
                .header("Authorization", "Bearer " + user.getToken()))
            .andExpect(jsonPath("name", is("João da Silva")))
            .andExpect(jsonPath("email", is("joao@silva.org")))
            .andExpect(jsonPath("phones[0].number", is("987654321")))
            .andExpect(jsonPath("phones[0].ddd", is("21")))
            .andExpect(jsonPath("created", is("2018-05-25")))
            .andExpect(jsonPath("modified", is("2018-05-25")))
            .andExpect(jsonPath("last_login", is("2018-05-25T12:12:00")));
    }

    @Test
    public void givenAnIdOnPathAndToken_whenRequestedProfile_thenReturnsOk() throws Exception {
        User user = registerUserFromFile("joao-da-silva.json");

        when(timeProvider.now())
            .thenReturn(LocalDateTime.of(2018, Month.MAY, 25, 12, 20));

        mockMvc.perform(
            get("/api/user/" + user.getId())
                .header("Authorization", "Bearer " + user.getToken()))
            .andExpect(status().isOk());
    }

    /**
     * Register user given on file on the day 2018-05-25 at 12:12, created to help
     * testing last login status.
     *
     * @param fileName Name of the file to load
     * @return User entity registered at the system
     * @throws IOException        see {@link LoginControllerIT#getText}
     * @throws URISyntaxException see {@link LoginControllerIT#getText}
     * @throws LogineerException A system exception, should not happen
     */
    private User registerUserFromFile(String fileName) throws IOException, URISyntaxException, LogineerException {
        ObjectMapper mapper = new ObjectMapper()
            .registerModule(new KotlinModule())
            .registerModule(new JavaTimeModule());

        UserDto userDto = mapper.readValue(getText(fileName), UserDto.class);
        return userService.register(userDto);
    }

    /**
     * @param resourceName Name of the resource to be loaded
     *
     * @return String content of the resource
     * @throws IOException In case there is an error reading the Stream
     * @throws URISyntaxException If the string to the file is not properly formatted
     */
    private String getText(String resourceName) throws IOException, URISyntaxException {
        Path path = Paths.get(getClass().getResource(resourceName).toURI());
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }
}
