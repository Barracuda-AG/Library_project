package ua.gorbatov.library.spring.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.gorbatov.library.spring.entity.Order;
import ua.gorbatov.library.spring.entity.Role;
import ua.gorbatov.library.spring.entity.User;
import ua.gorbatov.library.spring.exception.UserNotFoundException;
import ua.gorbatov.library.spring.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private OrderService orderService;
    @InjectMocks
    private UserService userService;

    static List<User> users = new ArrayList<>();

    static {
        User admin = User.builder()
                .password("admin")
                .role(Role.ROLE_ADMIN)
                .email("admin@mail.com")
                .firstName("admin")
                .lastName("admin")
                .accountNonLocked(true)
                .build();
        User user = User.builder()
                .role(Role.ROLE_USER)
                .password("user")
                .email("user@mail.com")
                .firstName("user")
                .lastName("user")
                .accountNonLocked(true)
                .build();
        users.add(user);
        users.add(admin);
    }

    @Test
    public void testGetOnlyUsers() {
        when(userRepository.findAll()).thenReturn(users);
        assertEquals(users, userService.getAllUsers());
    }

    @Test
    public void testFindByName() {
        when(userRepository.findByEmail("user@mail.com")).thenReturn(Optional.of(users.get(1)));
        assertEquals(users.get(1), userService.findByName("user@mail.com"));
    }

    @Test
    public void testFindByOrderID() {
        when(userRepository.findAll()).thenReturn(users);
        assertThrows(UserNotFoundException.class, () -> userService.findByOrderID(new Order()));
    }

    @Test
    public void testBlockUser() {
        User user = users.get(1);
        when(userRepository.getOne(1L)).thenReturn(user);
        userService.blockUser(1L);
        assertFalse(users.get(1).isAccountNonLocked());
        Mockito.verify(userRepository, Mockito.times(1)).save(user);
    }
}