package ua.gorbatov.library.spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ua.gorbatov.library.spring.entity.Role;
import ua.gorbatov.library.spring.entity.User;
import ua.gorbatov.library.spring.repository.UserRepository;

import java.util.Arrays;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private void create(User user, Role role) {
        BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();

        user.setPassword(bcrypt.encode(user.getPassword()));
        user.setRole(role);

        userRepository.save(user);
    }
    public void update(String email, Role role){
        User userFromDB = userRepository.findByEmail(email);
        userRepository.delete(userFromDB);
        userFromDB.setRole(role);
        userRepository.save(userFromDB);
    }

    public void createUser(User user, Role role){
        create(user, Role.USER);
    }
    public void createAdmin(User user, Role role){
        create(user, Role.ADMIN);
    }
    public void createLibrarian(User user, Role role){
        create(user, Role.LIBRARIAN);
    }
    public boolean isPresent(String email){
        User user = userRepository.findByEmail(email);

        return user != null;
    }

    public User getUser(String email){
        return userRepository.findByEmail(email);
    }
    public User getUser(Long id){
        return userRepository.getOne(id);
    }
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }
    public void delete(String email){
        User user = userRepository.findByEmail(email);
        userRepository.delete(user);
    }
}
