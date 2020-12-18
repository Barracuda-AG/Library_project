package ua.gorbatov.library.spring.service;

import ua.gorbatov.library.spring.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ua.gorbatov.library.spring.dto.UserDTO;
import ua.gorbatov.library.spring.entity.Book;
import ua.gorbatov.library.spring.entity.Order;
import ua.gorbatov.library.spring.entity.Role;
import ua.gorbatov.library.spring.entity.User;
import ua.gorbatov.library.spring.repository.OrderRepository;
import ua.gorbatov.library.spring.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private UserRepository userRepository;
    private OrderRepository orderRepository;
    @Autowired
    public UserService(UserRepository userRepository,
                       OrderRepository orderRepository)
    {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
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
                ()-> new UserNotFoundException("User not found in DataBase")
        );
    }
    public Optional<Order> findUserOrder(String name){
        User user = userRepository.findByEmail(name).get();
        return Optional.ofNullable(user.getOrder());


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
    public void clearOrder(User user){
       orderRepository.delete(user.getOrder());
       user.setOrder(null);
       userRepository.save(user);
    }
    public User findByOrderID(Order order){
       return userRepository.findAll().stream()
                .filter(a -> a.getOrder()==order).findFirst().orElseThrow(
                       UserNotFoundException::new
               );
    }

    public User getUser(Long id) {
        return userRepository.getOne(id);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    public List<User> getOnlyUsers(){
        return userRepository.findAll()
                .stream().filter(o -> o.getRole().equals(Role.ROLE_USER))
                .collect(Collectors.toList());
    }
    public List<User> getOnlyLibrarians(){
        return userRepository.findAll()
                .stream().filter(o -> o.getRole().equals(Role.ROLE_LIBRARIAN))
                .collect(Collectors.toList());
    }
    public List<User> getUsersExceptAdmin(){
        return userRepository.findAll()
                .stream()
                .filter(o -> !o.getRole().equals(Role.ROLE_ADMIN))
                .collect(Collectors.toList());
    }
    public List<User> findUserWithOrders(){
        return userRepository.findAll().stream()
                .filter(a -> a.getOrder() != null)
                .collect(Collectors.toList());
    }
    public void delete(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        user.ifPresent(value -> userRepository.delete(value));
    }
    public void returnBooks(User user){
        Optional<Order> userOrder = findUserOrder(user.getEmail());
        List<Book> listBooks = new ArrayList<>();
        if(userOrder.isPresent())
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

        return user;
    }
}
