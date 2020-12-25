package ua.gorbatov.library.spring.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import ua.gorbatov.library.spring.dto.BookDTO;
import ua.gorbatov.library.spring.entity.User;
import ua.gorbatov.library.spring.service.BookService;
import ua.gorbatov.library.spring.service.UserService;

import java.time.LocalDate;

@Component
public class OnStartRunner {//implements CommandLineRunner {

    private UserService userService;

    private BookService bookService;
    @Autowired
    public OnStartRunner(UserService userService, BookService bookService) {
        this.userService = userService;
        this.bookService = bookService;
    }


    public void run(String... args) throws Exception {
        User admin = User.builder()
                .password(bCryptPasswordEncoder().encode("admin"))
                .email("admin@mail.com")
                .firstName("admin")
                .lastName("admin")
                .build();
        userService.createAdmin(admin);

        User librarian = User.builder()
                .password(bCryptPasswordEncoder().encode("gorbatov"))
                .email("gorbatov2010@yahoo.com")
                .firstName("alex")
                .lastName("gorbatov")
                .build();
        userService.createLibrarian(librarian);
        User user = User.builder()
                .password(bCryptPasswordEncoder().encode("user"))
                .email("user@mail.com")
                .firstName("user")
                .lastName("user")
                .build();
        userService.createUser(user);
        BookDTO bookDTO1 = new BookDTO();
        bookDTO1.setTitle("Truly, madly, guilty");
        bookDTO1.setAuthor("Liane Moriarty");
        bookDTO1.setPublishDate(LocalDate.of(2018, 10, 15));
        bookDTO1.setPublisher("Penguin");
        bookDTO1.setQuantity(15);
        bookService.saveBookFromDTO(bookDTO1);

        BookDTO bookDTO2 = new BookDTO();
        bookDTO2.setTitle("Hobbit");
        bookDTO2.setAuthor("John Tolkien");
        bookDTO2.setPublishDate(LocalDate.of(1940, 5, 11));
        bookDTO2.setPublisher("London");
        bookDTO2.setQuantity(12);
        bookService.saveBookFromDTO(bookDTO2);

        BookDTO bookDTO3 = new BookDTO();
        bookDTO3.setTitle("1984");
        bookDTO3.setAuthor("George Orwell");
        bookDTO3.setPublishDate(LocalDate.of(1980, 3, 10));
        bookDTO3.setPublisher("AWT");
        bookDTO3.setQuantity(14);
        bookService.saveBookFromDTO(bookDTO3);

        BookDTO bookDTO4 = new BookDTO();
        bookDTO4.setTitle("Chasing the Dead");
        bookDTO4.setAuthor("Tim Weaver");
        bookDTO4.setPublishDate(LocalDate.of(2015, 1, 10));
        bookDTO4.setPublisher("Penguin");
        bookDTO4.setQuantity(20);
        bookService.saveBookFromDTO(bookDTO4);

        BookDTO bookDTO5 = new BookDTO();
        bookDTO5.setTitle("Vanished");
        bookDTO5.setAuthor("Tim Weaver");
        bookDTO5.setPublishDate(LocalDate.of(2015, 1, 10));
        bookDTO5.setPublisher("ReadMore");
        bookDTO5.setQuantity(20);
        bookService.saveBookFromDTO(bookDTO5);

        BookDTO bookDTO6 = new BookDTO();
        bookDTO6.setTitle("The girl with dragon tatoo");
        bookDTO6.setAuthor("Stieg Larsson");
        bookDTO6.setPublishDate(LocalDate.of(2010, 12, 11));
        bookDTO6.setPublisher("Maclehose Press");
        bookDTO6.setQuantity(15);
        bookService.saveBookFromDTO(bookDTO6);

        BookDTO bookDTO7 = new BookDTO();
        bookDTO7.setTitle("Doctor Sleep");
        bookDTO7.setAuthor("Stephen King");
        bookDTO7.setPublishDate(LocalDate.of(1990, 1, 10));
        bookDTO7.setPublisher("Simon and Shuster");
        bookDTO7.setQuantity(18);
        bookService.saveBookFromDTO(bookDTO7);

        BookDTO bookDTO8 = new BookDTO();
        bookDTO8.setTitle("Harry Potter and the Sorcerer's stone");
        bookDTO8.setAuthor("Joan Roaling");
        bookDTO8.setPublishDate(LocalDate.of(1995, 1, 10));
        bookDTO8.setPublisher("Penguin");
        bookDTO8.setQuantity(17);
        bookService.saveBookFromDTO(bookDTO8);

        BookDTO bookDTO9 = new BookDTO();
        bookDTO9.setTitle("Lord of the Rings");
        bookDTO9.setAuthor("John Tolkien");
        bookDTO9.setPublishDate(LocalDate.of(1940, 1, 10));
        bookDTO9.setPublisher("AWT");
        bookDTO9.setQuantity(17);
        bookService.saveBookFromDTO(bookDTO9);

        BookDTO bookDTO10 = new BookDTO();
        bookDTO10.setTitle("The dead tracks");
        bookDTO10.setAuthor("Tim Weaver");
        bookDTO10.setPublishDate(LocalDate.of(2018, 5, 11));
        bookDTO10.setPublisher("Penguin");
        bookDTO10.setQuantity(20);
        bookService.saveBookFromDTO(bookDTO10);

    }
    BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
