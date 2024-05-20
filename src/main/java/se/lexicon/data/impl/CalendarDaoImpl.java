package se.lexicon.data.impl;

import se.lexicon.data.CalendarDao;
import se.lexicon.exception.MySQLException;
import se.lexicon.model.Calendar;
import se.lexicon.model.Meeting;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class CalendarDaoImpl implements CalendarDao {

    //todo Implement methods

    private Connection connection;

    public CalendarDaoImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Calendar createCalendar(String title, String username) {

        String insertQuery = "INSERT INTO calendars (username, title) VALUES (?, ?)";

        try (
                PreparedStatement preparedStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, title);

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                String errorMessage = "Creating calendar failed, no rows affected.";
                throw new MySQLException(errorMessage);
            }

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int calendarId = generatedKeys.getInt(1);
                    return new Calendar(calendarId, title, username);
                } else {
                    String errorMessage = "Creating calendar failed, no ID obtained.";
                    throw new MySQLException(errorMessage);
                }
            }

        } catch (SQLException e) {
            String errorMessage = "An error occurred while creating a calendar.";
            throw new MySQLException(errorMessage, e);
        }
    }

    @Override
    public Optional<Calendar> findById(int id) {
        String selectQuery = "SELECT * FROM calendars WHERE id = ?";
        try (
                PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {

            preparedStatement.setInt(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    //getting username and title from result set and set it to a new Calendar object
                    String username = resultSet.getString("username");
                    String title = resultSet.getString("title");
                    return Optional.of(new Calendar(id, username, title));
                }
            }

        } catch (SQLException e) {
            String errorMessage = "Error occurred while finding MeetingCalendar by ID: " + id;
            throw new MySQLException(errorMessage, e);
        }

        return Optional.empty();
    }

    @Override
    public Collection<Calendar> findCalendarsByUsername(String username) {

        String selectQuery = "SELECT * FROM calendars WHERE username = ?";
        List<Calendar> calendars = new ArrayList<> ();

        try (

                PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)
        ) {

            preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                //Iterate over the result set:
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String title = resultSet.getString("title");
                    calendars.add(new Calendar(id, username, title));
                }
            }

        } catch (SQLException e) {
            String errorMessage = "Error occurred while finding Calendars by username: " + username;
            throw new MySQLException(errorMessage, e);
        }
        return calendars;
    }

    @Override
    public Optional<Calendar> findByTitle(String title) {
        String selectQuery = "SELECT * FROM calendars WHERE title = ?";
        Calendar calendar = null;

        try (
                PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {

            preparedStatement.setString(1, title);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String username = resultSet.getString("username");
                    calendar = new Calendar(id, username, title);
                }
            }

        } catch (SQLException e) {
            String errorMessage = ("Error occurred while finding Calendar by title: " + title);
            throw new MySQLException(errorMessage, e);
        }

        return Optional.ofNullable(calendar);
    }

    @Override
    public boolean deleteCalendar(int id) {
        String deleteQuery = "DELETE FROM calendars WHERE id = ?";

        try (
                PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)
        ) {
            preparedStatement.setInt(1, id);

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0; // Returns true if at least one row was deleted

        } catch (SQLException e) {
            String errorMessage = "Error occurred while deleting calendar with ID: " + id;
            throw new MySQLException(errorMessage, e);
        }
    }

    @Override
    public Calendar getCalendar (String username) {

        Optional<Calendar> calendar = findByTitle(username);
        if (calendar.isPresent()) {
            return calendar.get();
        }

        return null;
    }

    @Override
    public void createMeeting (String title, String description, String start, String end, String location, String username) {
        String insertQuery = "INSERT INTO meetings (title, description, start, end, location, username) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setString(1, title);
            preparedStatement.setString(2, description);
            preparedStatement.setString(3, start);
            preparedStatement.setString(4, end);
            preparedStatement.setString(5, location);
            preparedStatement.setString(6, username);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            String errorMessage = "An error occurred while creating a meeting.";
            throw new MySQLException(errorMessage, e);
        }
    }



    @Override
    public List<Meeting> getMeetings (Calendar calendar) {
        String selectQuery = "SELECT * FROM meetings WHERE calendarId = ?";
        List<Meeting> meetings = new ArrayList<> ();

        try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
            preparedStatement.setInt(1, calendar.getId());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String title = resultSet.getString("title");
                    String description = resultSet.getString("description");
                    String start = resultSet.getString("start");
                    String end = resultSet.getString("end");
                    String location = resultSet.getString("location");
                    String username = resultSet.getString("username");
                    Meeting meeting = new Meeting(id, title, description, start, end, location, username);
                    meetings.add(meeting);
                }
            }
        } catch (SQLException e) {
            String errorMessage = "An error occurred while retrieving meetings for calendar: " + calendar.getTitle();
            throw new MySQLException(errorMessage, e);
        }

        return meetings;
    }


}