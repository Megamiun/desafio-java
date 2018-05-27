package br.com.gabryel.logineer.controller;

import br.com.gabryel.logineer.entities.User;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.Month;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class LoginControllerProfileIT extends LoginControllerBaseIT {

    @Test
    public void givenAnIdOnPathAndNoToken_whenRequestedProfile_thenReturnsUnauthorized() throws Exception {
        User user = registerUserFromFile(userService, "joao-da-silva.json");

        mockMvc
            .perform(get("/api/user/" + user.getId()))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void givenAnIdOnPathAndNoToken_whenRequestedProfile_thenReturnsUnauthorizedMessage() throws Exception {
        User user = registerUserFromFile(userService, "joao-da-silva.json");

        mockMvc
            .perform(get("/api/user/" + user.getId()))
            .andExpect(jsonPath("mensagem", is("Não autorizado")));
    }

    @Test
    public void givenAnIdOnPathAndInvalidToken_whenRequestedProfile_thenReturnsUnauthorized() throws Exception {
        User user = registerUserFromFile(userService, "joao-da-silva.json");

        mockMvc.perform(
            get("/api/user/" + user.getId())
                .header("Authorization", "Bearer I-m-a-bear"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void givenAnIdOnPathAndInvalidToken_whenRequestedProfile_thenReturnsUnauthorizedMessage() throws Exception {
        User user = registerUserFromFile(userService, "joao-da-silva.json");

        mockMvc.perform(
            get("/api/user/" + user.getId())
                .header("Authorization", "Bearer I-m-a-bear"))
            .andExpect(jsonPath("mensagem", is("Não autorizado")));
    }

    @Test
    public void givenAnIdOnPathAndTokenWithLastLoginTooOld_whenRequestedProfile_thenReturnsUnauthorized() throws Exception {
        User user = registerUserFromFile(userService, "joao-da-silva.json");

        when(timeProvider.now())
            .thenReturn(LocalDateTime.of(2018, Month.MAY, 25, 12, 43));

        mockMvc.perform(
            get("/api/user/" + user.getId())
                .header("Authorization", "Bearer I-m-a-bear"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void givenAnIdOnPathAndTokenWithLastLoginTooOld_whenRequestedProfile_thenReturnsInvalidSessionMessage() throws Exception {
        User user = registerUserFromFile(userService, "joao-da-silva.json");

        when(timeProvider.now())
            .thenReturn(LocalDateTime.of(2018, Month.MAY, 25, 12, 43));

        mockMvc.perform(
            get("/api/user/" + user.getId())
                .header("Authorization", "Bearer " + user.getToken()))
            .andExpect(jsonPath("mensagem", is("Sessão inválida")));
    }

    @Test
    public void givenAnIdOnPathAndToken_whenRequestedProfile_thenReturnsProfile() throws Exception {
        User user = registerUserFromFile(userService, "joao-da-silva.json");

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
        User user = registerUserFromFile(userService, "joao-da-silva.json");

        when(timeProvider.now())
            .thenReturn(LocalDateTime.of(2018, Month.MAY, 25, 12, 20));

        mockMvc.perform(
            get("/api/user/" + user.getId())
                .header("Authorization", "Bearer " + user.getToken()))
            .andExpect(status().isOk());
    }
}
