package ua.gorbatov.library.spring.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import ua.gorbatov.library.spring.entity.Role;
import ua.gorbatov.library.spring.entity.User;
import ua.gorbatov.library.spring.service.UserService;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = PageController.class)
@ActiveProfiles(value = "test")
@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class})
public class PageControllerTest {
    @MockBean
    private UserService userService;

    @MockBean
    Authentication auth;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private User user;

    @Before
    public void setup(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        user = User.builder()
                .password("$2a$10$nvrapoY3tiywqLdt1TyEmOMoScT1w7sm5kQZr2uZEHstpM8vcFK8O")
                .email("user@mail.com")
                .firstName("user")
                .lastName("user")
                .role(Role.ROLE_USER)
                .accountNonLocked(true)
                .build();
    }
    @Test
    public void loginPage() throws Exception {

        mockMvc.perform(get("/login"))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }
    @Test
    @WithMockUser(value = "user@mail.com", roles = {"USER"},password = "user")
    public void correctLogin() throws Exception {
        Mockito.when(userService.findByName("user@mail.com")).thenReturn(user);
        mockMvc.perform(post("/login"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/cabinet"));
    }
}