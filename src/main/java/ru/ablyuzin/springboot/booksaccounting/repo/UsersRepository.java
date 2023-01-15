package ru.ablyuzin.springboot.booksaccounting.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ablyuzin.springboot.booksaccounting.models.User;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<User, Integer> {
    Optional<User> findByFullNameContainingIgnoreCase(String fullName);

    Optional<User> findByEmail(String email);
}