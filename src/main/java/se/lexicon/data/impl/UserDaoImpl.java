package se.lexicon.data.impl;

import se.lexicon.data.UserDao;
import se.lexicon.data.db.MeetingCalendarDBConnection;
import se.lexicon.exception.AuthenticationFieldsException;
import se.lexicon.exception.MySQLException;
import se.lexicon.exception.UserExpiredException;
import se.lexicon.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class UserDaoImpl implements UserDao {

    private Connection connection;

    public UserDaoImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public User createUser(String username) {
        String query = "INSERT INTO users(username, _password) VALUES(?,?)";
        try (
                PreparedStatement preparedStatement = connection.prepareStatement(query);
        ) {
            User user = new User(username);
            user.newPassword();
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new MySQLException("Creating user failed, no rows affected.");
            }
            return user;
        } catch (SQLException e) {
            throw new MySQLException("Error occured while creating user: " + username);
        }
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String query = "SELECT * FROM users WHERE username = ?";

        try (
                PreparedStatement preparedStatement = connection.prepareStatement(query);
        ) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String foundUsername = resultSet.getString("username");
                String foundPassword = resultSet.getString("_password");
                boolean foundExpired = resultSet.getBoolean("expired");

                User foundUser = new User(foundUsername, foundPassword, foundExpired);
                return Optional.of(foundUser);
            } else {
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new MySQLException("Error occured while finding the user by username: " + username, e);
        }
    }

    @Override
    public boolean authenticate(User user) throws UserExpiredException, AuthenticationFieldsException {
        //step1: define a select query
        String query = "SELECT * FROM users WHERE username = ? and _password = ?";
        //step2: prepared statement
        try (
                PreparedStatement preparedStatement = connection.prepareStatement(query);
        ) {

            //step3: set parameters to prepared statement
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            //step4: execute query
            ResultSet resultSet = preparedStatement.executeQuery();
            //step5: check the result set
            if (resultSet.next()) {
                //step6: if result set exists
                //step7: check if user expired -> throw exception
                boolean isExpired = resultSet.getBoolean("expired");
                if (isExpired) {
                    throw new UserExpiredException("User is expired. username: " + user.getUsername());
                }
            } else { //step8: else if the result set was null -> throw exception
                throw new AuthenticationFieldsException("Authentication failed. Invalid credentials.");
            }
            //step9: return true
            return true;
        } catch (SQLException e) {
            throw new MySQLException("Error occured while authenticationg user by username" + user.getUsername());
        }

    }
}
