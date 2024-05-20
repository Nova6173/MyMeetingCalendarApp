package se.lexicon.data;

import se.lexicon.model.Meeting;

import java.util.Collection;
import java.util.Optional;

public interface MeetingDao {

    Meeting createMeeting(Meeting meeting);

    Optional<Meeting> findById(int id);

    //select * from meeting where calendar_id = ?;
    Collection<Meeting> findAllMeetingsByCalendarId(int calendarId);

    boolean deleteMeeting(int meetingId);
    //Add more methods as needed
}
