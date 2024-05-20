package se.lexicon.data;

import se.lexicon.exception.AuthenticationFieldsException;
import se.lexicon.exception.UserExpiredException;
import se.lexicon.model.User;

import java.util.Optional;

public interface UserDao {
    User createUser(String username);

    Optional<User> findByUsername(String username);

    boolean authenticate(User user) throws UserExpiredException, AuthenticationFieldsException;

    // add more methods according to the project
}
