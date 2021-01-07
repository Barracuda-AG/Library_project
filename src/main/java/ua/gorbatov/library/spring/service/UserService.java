package ua.gorbatov.library.spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ua.gorbatov.library.spring.dto.UserDTO;
import ua.gorbatov.library.spring.entity.Book;
import ua.gorbatov.library.spring.entity.Order;
import ua.gorbatov.library.spring.entity.Role;
import ua.gorbatov.library.spring.entity.User;
import ua.gorbatov.library.spring.exception.UserNotFoundException;
import ua.gorbatov.library.spring.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final OrderService orderService;
    private static final Integer PENALTY = 50;

    @Autowired
    public UserService(UserRepository userRepository,
                       OrderService orderService) {
        this.userRepository = userRepository;
        this.orderService = orderService;
    }

    private void create(User user, Role role) {

        user.setRole(role);

        userRepository.save(user);
    }

    public void update(User user, Role role) {
        user.setRole(role);
        userRepository.save(user);
    }

    public void createUserOrder(User user, Order order) {
        user.setOrder(order);

    }

    public User findByName(String name) {
        return userRepository.findByEmail(name).orElseThrow(
                () -> new UserNotFoundException("User not found in DataBase")
        );
    }

    public Optional<Order> findUserOrder(String name) {
        User user = userRepository.findByEmail(name).get();
        return Optional.ofNullable(checkPenalty(user.getOrder()));


    }

    public void createUser(User user) {
        create(user, Role.ROLE_USER);
    }

    public void createAdmin(User user) {
        create(user, Role.ROLE_ADMIN);
    }

    public void createLibrarian(User user) {
        create(user, Role.ROLE_LIBRARIAN);
    }

    @Transactional
    public void clearOrder(User user) {
        returnBooks(user);
        orderService.delete(user.getOrder());
        user.setOrder(null);
        userRepository.save(user);
    }

    public User findByOrderID(Order order) {
        return userRepository.findAll().stream()
                .filter(a -> a.getOrder() == order).findFirst().orElseThrow(
                        UserNotFoundException::new
                );
    }

    public User getUser(Long id) {
        return userRepository.getOne(id);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getOnlyUsers() {
        return userRepository.findAll()
                .stream().filter(o -> o.getRole().equals(Role.ROLE_USER))
                .collect(Collectors.toList());
    }

    public List<User> getOnlyLibrarians() {
        return userRepository.findAll()
                .stream().filter(o -> o.getRole().equals(Role.ROLE_LIBRARIAN))
                .collect(Collectors.toList());
    }

    public List<User> getUsersExceptAdmin() {
        return userRepository.findAll()
                .stream()
                .filter(o -> !o.getRole().equals(Role.ROLE_ADMIN))
                .collect(Collectors.toList());
    }

    public List<User> findUserWithOrders() {
        return userRepository.findAll().stream()
                .filter(a -> a.getOrder() != null)
                .collect(Collectors.toList());
    }

    public void blockUser(Long id) {
        User user = userRepository.getOne(id);
        user.setAccountNonLocked(false);
        userRepository.save(user);
    }

    public void unblockUser(Long id) {
        User user = userRepository.getOne(id);
        user.setAccountNonLocked(true);
        userRepository.save(user);
    }

    public void delete(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        returnBooks(user.get());
        user.ifPresent(userRepository::delete);
    }

    public void returnBooks(User user) {
        Optional<Order> userOrder = findUserOrder(user.getEmail());
        List<Book> listBooks = new ArrayList<>();
        if (userOrder.isPresent())
            listBooks = userOrder.get().getBooks();
        for (Book book : listBooks)
            book.setQuantity(book.getQuantity() + 1);

    }

    public User createUserFromDTO(UserDTO userDTO) {
        BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
        User user = new User();

        user.setPassword(bcrypt.encode(userDTO.getPassword()));
        user.setEmail(userDTO.getEmail());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setAccountNonLocked(true);

        return user;
    }

    private Order checkPenalty(Order order) {
        LocalDate now = LocalDate.now();
        LocalDate returnDate = order.getReturnDate();
        if (returnDate.isBefore(now) && !order.isReturned()) {
            order.setPenalty(PENALTY);
            orderService.save(order);
        }
        return order;
    }

    public Page<User> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        return userRepository.findByRoleNotLike(Role.ROLE_ADMIN,pageable);

    }
}
