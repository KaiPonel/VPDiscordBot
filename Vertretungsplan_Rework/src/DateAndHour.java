
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
public class DateAndHour {
    /** Class DateAndHour
     * Goal: get All data information into One Class
     * Note: DateAndHour is for Rows, and is only Meant for the Entries of the Plan
     * Class Finished (26/02)
     */

    private int day; //CalenderDayDate
    private int month; //CalenderMonthDate
    private int year; //Current Year
    private int calenderWeek;
    private int calenderDay; //  Montag == 1, Dienstag == 2, Mittwoch == 3, Donnerstag == 4, Freitag == 5
    private int[]hours;
    SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");

    public int[]getHours(){
        return this.hours;
    }
    public int getCalenderDay(){
        return this.calenderDay;
    }
    public String getCalenderDayAsString(){
        try {
            generateCalenderData();
        }catch(Exception E){
            E.printStackTrace();
        }
        if(getCalenderDay() == 1){
            return "Mo";
        }else if(getCalenderDay() == 2){
            return "Di";
        }
        else if(getCalenderDay() == 3){
            return "Mi";
        }
        else if(getCalenderDay() == 4){
            return "Do";
        }else if(getCalenderDay() == 5){
            return "Fr";
        }
       return null;
    }


    public final String getDateAsString(){
        String hour = "";
        for(int i = 0; i<hours.length; i++){
            hour = hour + String.valueOf(hours[i]);
            if(i+1<hours.length){
                hour = hour + "-";
            }
        }
        String date;
        date =  day + "." + month + " " + hour;
        return date;
    }
    public void generateCalenderData()throws Exception{
        Calendar c = Calendar.getInstance();
        year = Calendar.getInstance().get(Calendar.YEAR);
        Date dateOfDay = format.parse(dateAsString());
        c.setTime(dateOfDay);
        calenderDay = c.get(Calendar.DAY_OF_WEEK) -2;
        calenderWeek = c.get(Calendar.WEEK_OF_YEAR);
    }

    public void setHours(int startHour, int endHour){
        if(endHour == -1){
            hours = new int[1];
            hours[0] = startHour;
        }
        else{


            hours = new int[endHour-startHour+1];
            for(int i = 0; i<endHour-startHour+1; i++){
                hours[i] = startHour+i;
            }
        }
    }
    public void setDay(int day){
        this.day = day;
    }
    public void setMonth(int month){
        this.month = month;
    }



    private String dateAsString(){
        String day = String.valueOf(this.day);
        if(day.length() == 1){
            day = "0" + day;
        }
        String month = String.valueOf(this.month);
        if(month.length() == 1){
            month = "0" + month;
        }
        return (day + "." + month + "." + String.valueOf(year));
    }


}



