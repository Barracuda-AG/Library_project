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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ua.gorbatov.library.spring.entity.Book;
import ua.gorbatov.library.spring.entity.User;
import ua.gorbatov.library.spring.service.BookService;
import ua.gorbatov.library.spring.service.OrderService;
import ua.gorbatov.library.spring.service.UserService;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = {UserController.class})
@ActiveProfiles(value = "test")
@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class})
public class UserControllerTest {

    @MockBean
    private UserService userService;
    @MockBean
    private BookService bookService;
    @MockBean
    private OrderService orderService;

    @MockBean
    Authentication auth;

    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private WebApplicationContext webApplicationContext;

    private User user;
    private Principal principal;
    private Book book;
    private List<Book> books = new ArrayList<>();

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        user = User.builder()
                .password("$2a$10$nvrapoY3tiywqLdt1TyEmOMoScT1w7sm5kQZr2uZEHstpM8vcFK8O")
                .email("user@mail.com")
                .firstName("user")
                .lastName("user")
                .accountNonLocked(true)
                .build();
        book = Book.builder().title("Truly, madly, guilty")
                .author("Liane Moriarty")
                .publishDate(LocalDate.of(2018, 10, 15))
                .publisher("Penguin")
                .quantity(15).build();

        books.add(book);
    }

    @Test
    public void cabinet() throws Exception {
        principal = Mockito.mock(Principal.class);
        Mockito.when(principal.getName()).thenReturn("user@mail.com");
        Mockito.when(userService.findByName("user@mail.com")).thenReturn(user);
        ResultActions result = mockMvc.perform(get("/user/cabinet")
                .principal(principal));
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("user"))
                .andExpect(view().name("/user/cabinet"));
    }

    @Test
    public void pageNotExist() throws Exception {
        principal = Mockito.mock(Principal.class);
        Mockito.when(principal.getName()).thenReturn("user@mail.com");

        ResultActions result = mockMvc.perform(get("/user/notexistpage").principal(principal));
        result.andDo(print())
                .andExpect(status().is4xxClientError());
    }
}