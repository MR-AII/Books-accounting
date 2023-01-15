package ru.ablyuzin.springboot.booksaccounting.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.ablyuzin.springboot.booksaccounting.models.User;
import ru.ablyuzin.springboot.booksaccounting.services.UserService;

@Component
public class UserValidator implements Validator {
    private final UserService userService;

    @Autowired
    public UserValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return User.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        User user = (User) o;
        if (userService.findByFullName(user.getFullName()).isPresent()) errors.rejectValue(
                "fullName", "", "Пользователь с таким ФИО уже используется");
        if (userService.findByEmail(user.getEmail()).isPresent()) errors.rejectValue(
                "email", "", "Пользователь с таким email уже используется");
    }
}
