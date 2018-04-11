package model;

/**
 * Created by mac on 2/27/18.
 */

public class Schedule {
    String name, from,to;
   String flight_no;
    String time_from, time_to;


    public Schedule() {
    }

    public Schedule(String name, String from, String to, String time_from,String time_to, String flight_no) {
        this.name = name;
        this.from = from;
        this.time_from = time_from;
        this.time_to = time_to;
        this.flight_no = flight_no;
        this.to = to;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getTime_from() {
        return time_from;
    }

    public void setTime_from(String time_from) {
        this.time_from = time_from;
    }

    public String getTime_to() {
        return time_to;
    }

    public void setTime_to(String time_to) {
        this.time_to = time_to;
    }

    public String getFlight_no() {
        return flight_no;
    }

    public void setFlight_no(String flight_no) {
        this.flight_no = flight_no;
    }
}
