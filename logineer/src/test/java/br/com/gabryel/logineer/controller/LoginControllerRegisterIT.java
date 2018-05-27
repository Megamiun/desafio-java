package br.com.gabryel.logineer.controller;

import org.junit.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class LoginControllerRegisterIT extends LoginControllerBaseIT {

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
        registerUserFromFile(userService, "joao-da-silva.json");

        mockMvc.perform(
            put("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getText("joao-da-silva.json")))
            .andExpect(status().isNotAcceptable());
    }

    @Test
    public void givenAnUserDtoWithRepeatedEmail_whenRequestedCreation_thenReturnsAlreadyExistingEmailMessage() throws Exception {
        registerUserFromFile(userService, "joao-da-silva.json");

        mockMvc.perform(
            put("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getText("joao-da-silva.json")))
            .andExpect(jsonPath("mensagem", is("E-mail já existente")));
    }
}
