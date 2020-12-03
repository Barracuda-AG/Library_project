package ua.gorbatov.library.spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ua.gorbatov.library.spring.dto.UserDTO;
import ua.gorbatov.library.spring.entity.Order;
import ua.gorbatov.library.spring.entity.Role;
import ua.gorbatov.library.spring.entity.User;
import ua.gorbatov.library.spring.repository.UserRepository;

import java.util.List;

@Service
public class UserService {

    private UserRepository userRepository;
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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

    public User findById(Long id) {
        return userRepository.findById(id).get();
    }

    public User findByName(String name) {
        return userRepository.findByEmail(name);
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

    public boolean isPresent(String email) {
        User user = userRepository.findByEmail(email);

        return user != null;
    }
    public void clearOrder(User user){
       user.setOrder(null);
       userRepository.save(user);
    }
    public User findByOrderID(Order order){
        User user = userRepository.findAll().stream().filter(a -> a.getOrder()==order).findFirst().get();
        return user;
    }
    public User getUser(String email) {
        return userRepository.findByEmail(email);
    }

    public User getUser(Long id) {
        return userRepository.getOne(id);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void delete(String email) {
        User user = userRepository.findByEmail(email);
        userRepository.delete(user);
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
