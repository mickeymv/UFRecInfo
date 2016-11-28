package ai.api.sample;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mickey on 11/27/16.
 */

public class FitnessClass {
    @SerializedName("Name")
    private String Name;
    @SerializedName("Type")
    private String Type;
    @SerializedName("Type_2")
    private String Type_2;
    @SerializedName("Day")
    private String Day;

    @Override
    public String toString() {
        return "FitnessClass{" +
                "Name='" + Name + '\'' +
                ", Type='" + Type + '\'' +
                ", Type_2='" + Type_2 + '\'' +
                ", Day='" + Day + '\'' +
                ", Begin_Time='" + Begin_Time + '\'' +
                ", End_Time='" + End_Time + '\'' +
                ", Duration='" + Duration + '\'' +
                ", Venue='" + Venue + '\'' +
                ", Instructor='" + Instructor + '\'' +
                ", Room='" + Room + '\'' +
                ", Description='" + Description + '\'' +
                '}';
    }

    @SerializedName("Begin_Time")
    private String Begin_Time;
    @SerializedName("End_Time")
    private String End_Time;
    @SerializedName("Duration")
    private String Duration;
    @SerializedName("Venue")
    private String Venue;
    @SerializedName("Instructor")
    private String Instructor;
    @SerializedName("Room")
    private String Room;
    @SerializedName("Description")
    private String Description;

    public FitnessClass(String name, String type, String type_2, String day, String begin_Time, String end_Time, String duration, String venue, String instructor, String room, String description) {
        Name = name;
        Type = type;
        Type_2 = type_2;
        Day = day;
        Begin_Time = begin_Time;
        End_Time = end_Time;
        Duration = duration;
        Venue = venue;
        Instructor = instructor;
        Room = room;
        Description = description;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getType_2() {
        return Type_2;
    }

    public void setType_2(String type_2) {
        Type_2 = type_2;
    }

    public String getDay() {
        return Day;
    }

    public void setDay(String day) {
        Day = day;
    }

    public String getBegin_Time() {
        return Begin_Time;
    }

    public void setBegin_Time(String begin_Time) {
        Begin_Time = begin_Time;
    }

    public String getEnd_Time() {
        return End_Time;
    }

    public void setEnd_Time(String end_Time) {
        End_Time = end_Time;
    }

    public String getDuration() {
        return Duration;
    }

    public void setDuration(String duration) {
        Duration = duration;
    }

    public String getVenue() {
        return Venue;
    }

    public void setVenue(String venue) {
        Venue = venue;
    }

    public String getInstructor() {
        return Instructor;
    }

    public void setInstructor(String instructor) {
        Instructor = instructor;
    }

    public String getRoom() {
        return Room;
    }

    public void setRoom(String room) {
        Room = room;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }
}
