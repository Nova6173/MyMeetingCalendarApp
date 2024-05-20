package se.lexicon.data;

import se.lexicon.model.Calendar;
import se.lexicon.model.Meeting;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CalendarDao { //CRUD Operation

    Calendar createCalendar(String title, String username);


    Optional<Calendar> findById(int id);

    Collection<Calendar> findCalendarsByUsername(String username);

    Optional<Calendar> findByTitle(String title);

    boolean deleteCalendar(int id);

    Calendar getCalendar (String username);

    void createMeeting (String title, String description, String start, String end, String location, String username);

    List<Meeting> getMeetings (Calendar calendar);
}
