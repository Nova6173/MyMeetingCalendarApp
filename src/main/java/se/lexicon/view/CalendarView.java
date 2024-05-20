package se.lexicon.view;


import se.lexicon.model.Calendar;
import se.lexicon.model.Meeting;
import se.lexicon.model.User;
import se.lexicon.util.ConsoleColors;

import java.util.List;

public interface CalendarView {

    default void displayMenu() {
        System.out.println("Calendar Options");
        System.out.println("0. Resister");
        System.out.println("1. Login");
        System.out.println("2. Add Calendar");
        System.out.println("3. Add Meeting");
        System.out.println("4. Delete Calendar");
        System.out.println("5. Display Calendar Meeting");
        System.out.println("6. Logout");
        System.out.println("7. Exit");
        System.out.println("Enter your choice");
        // ...
    }

    void displayUser(User user);

    void displayCalendar(Calendar calendar);

    void displayMeeting(List<Meeting> meetings);

    default void displayErrorMessage(String message) {
        System.out.println(ConsoleColors.RED + message + ConsoleColors.RESET);
    }

    default void displaySuccessMessage(String message) {
        System.out.println(ConsoleColors.GREEN + message + ConsoleColors.RESET);
    }

    default void displayMessage(String message) {
        System.out.println(ConsoleColors.BLUE + message + ConsoleColors.RESET);
    }

    default void displayWarningMessage(String message) {
        System.out.println(ConsoleColors.YELLOW + message + ConsoleColors.RESET);

    }


    User promoteUserForm();

    String promoteString();

    Meeting promoteMeetingForm(int meetingId);

    String promoteCalendarForm();


    void displayMeetings (List<Meeting> meetings);
}