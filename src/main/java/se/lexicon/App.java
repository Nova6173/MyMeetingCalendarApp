package se.lexicon;

import se.lexicon.controller.CalendarController;
import se.lexicon.data.CalendarDao;
import se.lexicon.data.UserDao;
import se.lexicon.data.db.MeetingCalendarDBConnection;
import se.lexicon.data.impl.CalendarDaoImpl;
import se.lexicon.data.impl.UserDaoImpl;
import se.lexicon.exception.AuthenticationFieldsException;
import se.lexicon.exception.CalendarExceptionHandler;
import se.lexicon.exception.UserExpiredException;
import se.lexicon.model.Calendar;
import se.lexicon.model.User;
import se.lexicon.view.CalendarConsoleUI;
import se.lexicon.view.CalendarView;

import java.sql.Connection;
import java.util.Optional;


public class App {
    public static void main(String[] args) {
        try {
            UserDao userDao = new UserDaoImpl(MeetingCalendarDBConnection.getConnection());

            try {
                userDao.authenticate (new User("admin", "admin"));
                System.out.println ("Login Successful");

            } catch (UserExpiredException | AuthenticationFieldsException e) {
                System.out.println ("Login Failed");
            }
        } catch (Exception e) {
            CalendarExceptionHandler.handleException(e);
        }


        Connection connection = MeetingCalendarDBConnection.getConnection();
        CalendarView view = new CalendarConsoleUI();
        UserDao userDao = new UserDaoImpl(connection);
        CalendarDao calendarDao = new CalendarDaoImpl(connection);
        CalendarController controller = new CalendarController(view, userDao, calendarDao);
        controller.run();


    }
}