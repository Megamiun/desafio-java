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
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = LogineerApplication.class)
@AutoConfigureMockMvc
public abstract class LoginControllerBaseIT {

    @MockBean
    protected TimeProvider timeProvider;

    @Autowired
    protected UserService userService;
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private PhoneRepository phoneRepository;
    @Autowired
    private UserRepository userRepository;

    @Before
    public void clean() {
        phoneRepository.deleteAll();
        userRepository.deleteAll();

        LocalDate date = LocalDate.of(2018, Month.MAY, 25);
        LocalDateTime time = date.atTime(12, 12);
        when(timeProvider.now()).thenReturn(time);
    }

    /**
     * Register user given on file on the day 2018-05-25 at 12:12, created to help
     * testing last login status.
     *
     * @param fileName Name of the file to load
     * @return User entity registered at the system
     * @throws IOException        see {@link LoginControllerBaseIT#getText}
     * @throws URISyntaxException see {@link LoginControllerBaseIT#getText}
     * @throws LogineerException  A system exception, should not happen
     */
    protected User registerUserFromFile(UserService userService, String fileName) throws IOException, URISyntaxException, LogineerException {
        ObjectMapper mapper = new ObjectMapper()
            .registerModule(new KotlinModule())
            .registerModule(new JavaTimeModule());

        UserDto userDto = mapper.readValue(getText(fileName), UserDto.class);
        return userService.register(userDto);
    }

    /**
     * @param resourceName Name of the resource to be loaded
     * @return String content of the resource
     * @throws IOException        In case there is an error reading the Stream
     * @throws URISyntaxException If the string to the file is not properly formatted
     */
    protected String getText(String resourceName) throws IOException, URISyntaxException {
        Path path = Paths.get(getClass().getResource(resourceName).toURI());
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }
}
