package br.com.gabryel.logineer.controller;

import br.com.gabryel.logineer.entities.User;
import org.junit.Test;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.time.Month;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class LoginControllerLoginIT extends LoginControllerBaseIT {

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
        User user = registerUserFromFile(userService, "joao-da-silva.json");

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
        registerUserFromFile(userService, "joao-da-silva.json");

        mockMvc.perform(
            post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getText("joao-da-silva-login.json")))
            .andExpect(jsonPath("password").doesNotExist());
    }

    @Test
    public void givenAnLoginDtoOfAnUserWithWrongPassword_whenRequestedLogin_thenReturnsInvalidUserMessage() throws Exception {
        registerUserFromFile(userService, "joao-da-silva.json");

        mockMvc.perform(
            post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getText("joao-da-silva-login-wrong-pass.json")))
            .andExpect(jsonPath("mensagem", is("Usuário e/ou senha inválidos")));
    }

    @Test
    public void givenAnLoginDtoOfAnUserWithWrongPassword_whenRequestedLogin_thenReturnsUnauthorized() throws Exception {
        registerUserFromFile(userService, "joao-da-silva.json");

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
}
