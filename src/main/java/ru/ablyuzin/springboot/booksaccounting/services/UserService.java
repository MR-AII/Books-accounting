package ru.ablyuzin.springboot.booksaccounting.services;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ablyuzin.springboot.booksaccounting.models.User;
import ru.ablyuzin.springboot.booksaccounting.repo.UsersRepository;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserService {
    private final UsersRepository usersRepository;

    @Autowired
    public UserService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public List<User> findAll() {
        return usersRepository.findAll();
    }

    public User findById(int id) {
        User user = usersRepository.findById(id).orElse(null);
        Objects.requireNonNull(user).getBooks().stream()
                .filter(book -> (int) ((new Date().getTime() - book.getBookTaken().getTime()) / (24 * 60 * 60 * 1000)) > 10)
                .forEach(book -> book.setExpired(true));
        Hibernate.initialize(Objects.requireNonNull(user).getBooks());
        return user;
    }

    public Optional<User> findByFullName(String fullName) {
        return usersRepository.findByFullNameContainingIgnoreCase(fullName);
    }

    public Optional<User> findByEmail(String email) {
        return usersRepository.findByEmail(email);
    }

    @Transactional
    public void save(User user) {
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        usersRepository.save(user);
    }

    @Transactional
    public void update(User user, int id) {
        user.setId(id);
        user.setCreatedAt(getCreatedAt(id));
        user.setUpdatedAt(new Date());
        usersRepository.save(user);
    }

    @Transactional
    public void delete(int id) {
        usersRepository.deleteById(id);
    }

    private Date getCreatedAt(int id) {
        User user = usersRepository.findById(id).orElse(null);
        assert user != null;
        return user.getCreatedAt();
    }
}