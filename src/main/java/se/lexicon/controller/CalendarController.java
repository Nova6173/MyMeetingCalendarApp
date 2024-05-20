package se.lexicon.controller;

import javafx.event.ActionEvent;
import se.lexicon.data.CalendarDao;
import se.lexicon.data.UserDao;
import se.lexicon.exception.CalendarExceptionHandler;
import se.lexicon.model.Calendar;
import se.lexicon.model.Meeting;
import se.lexicon.model.User;
import se.lexicon.view.CalendarView;

import java.util.List;

public class CalendarController {

    // dependencies
    private CalendarView view;
    private UserDao userDao;
    private CalendarDao calendarDao;

    // fields
    private boolean isLoggedIn;
    private String username;

    public CalendarController(CalendarView view, UserDao userDao, CalendarDao calendarDao) {
        this.view = view;
        this.userDao = userDao;
        this.calendarDao = calendarDao;
    }

    public void run() {
        while (true) {
            view.displayMenu();
            int choice = getUserChoice();

            switch (choice) {
                case 0:
                    register();
                    break;
                case 1:
                    login();
                    break;
                case 2:
                    createCalendar();
                    break;
                case 3:
                    callMeeting();
                    break;
                case 4:
                    deleteCalendar();
                    break;
                case 5:
                    displayCalendar();
                    break;
                case 6:
                    isLoggedIn = false;
                    view.displayMessage("You are logged out.");
                    break;
                case 7:
                    System.exit(0);
                    break;

                default:
                    view.displayWarningMessage("Invalid choice. Please select a valid option");
            }
        }
    }

    private int getUserChoice() {
        String operationType = view.promoteString();
        int choice = -1;
        try {
            choice = Integer.parseInt(operationType);
        } catch (NumberFormatException e) {
            view.displayErrorMessage("Invalid input. Please enter a number.");
        }
        return choice;
    }

    private void register() {
        view.displayMessage("Enter your username: ");
        String username = view.promoteString();
        User registeredUser = userDao.createUser(username);
        view.displayUser(registeredUser);
    }

    private void login() {
        User user = view.promoteUserForm();

        try {
            isLoggedIn = userDao.authenticate(user);
            username = user.getUsername();
            view.displaySuccessMessage("Login successful. Welcome " + username);
        } catch (Exception e) {
            CalendarExceptionHandler.handleException(e);
        }
    }

    private void createCalendar() {
        if (!isLoggedIn) {
            view.displayWarningMessage("You need to login first.");
            return;
        }
        String calendarTitle = view.promoteCalendarForm();
        Calendar createdCalendar = calendarDao.createCalendar(calendarTitle, username);
        view.displaySuccessMessage("Calendar created successfully.");
        view.displayCalendar(createdCalendar);
    }

    private void callMeeting() {
        if (!isLoggedIn) {
            view.displayWarningMessage("You need to login first.");
            return;
        }
        view.displayMessage ("Enter meeting title: ");
        String title = view.promoteString ();

        view.displayMessage ("Enter meeting description: ");
        String description = view.promoteString ();

        view.displayMessage ("Enter start time (yyyy-MM-dd HH:mm): ");
        String start = view.promoteString ();

        view.displayMessage ("Enter end time (yyyy-MM-dd HH:mm): ");
        String end = view.promoteString ();

        view.displayMessage ("Enter location: ");
        String location = view.promoteString ();

        Calendar calendar = calendarDao.getCalendar(username);
        if (calendar == null) {
            view.displayErrorMessage("You have no calendar. Create one first.");
            return;
        }

        try {
            calendarDao.createMeeting(title, description, start, end, location, username);
            view.displaySuccessMessage("Meeting created successfully.");
        } catch (Exception e) {
            CalendarExceptionHandler.handleException(e);
        }



    }

    private void deleteCalendar() {
        if (!isLoggedIn) {
            view.displayWarningMessage("You need to login first.");
            return;
        }
        Calendar calendar = calendarDao.getCalendar(username);
        if (calendar == null) {
            view.displayErrorMessage("You have no calendar. Create one first.");
            return;
        }

        try {
            calendarDao.deleteCalendar(calendar.getId ());
            view.displaySuccessMessage("Calendar deleted successfully.");
        } catch (Exception e) {
            CalendarExceptionHandler.handleException(e);
        }

    }

    private void displayCalendar() {
        if (!isLoggedIn) {
            view.displayWarningMessage("You need to login first.");
            return;
        }
        Calendar calendar = calendarDao.getCalendar(username);
        if (calendar == null) {
            view.displayErrorMessage("You have no calendar. Create one first.");
            return;
        }

        try {
            List<Meeting> meetings = calendarDao.getMeetings(calendar);
            view.displayMeetings(meetings);
        } catch (Exception e) {
            CalendarExceptionHandler.handleException(e);
        }

    }
}