import java.util.ArrayList;

public class Row {
    /** Class Name Row:
     *  Goal: get Data from entries very sorted and easy to read and write
     *  Class finished (26/02)
     */


    private DateAndHour date;                   //Needed
    private String subject;                     //Needed
    private String room;                        //Not Needed
    private String oldTeacher; // Always        //Needed
    private String newTeacher; //Only if VER
    private String kindOf;
    private ArrayList<String> klassen;

    public void setDate(DateAndHour date){
        this.date = date;
    }
    public void setSubject(String subject){
        this.subject = subject;
    }
    public void setRoom(String room){
        this.room = room;
    }
    public void setOldTeacher(String oldTeacher){
        this.oldTeacher = oldTeacher;
    }
    public void setNewTeacher(String newTeacher){
        this.newTeacher = newTeacher;
    }
    public void setKindOf(String kindOf){
        this.kindOf = kindOf;
    }
    public void setKlassen(ArrayList<String> klassen){
        this.klassen = klassen;
    }

    public DateAndHour getDate(){
        return date;
    }
    public String getSubject(){
        return subject;
    }
    public String getRoom(){
        return room;
    }
    public String getOldTeacher(){
        return oldTeacher;
    }
    public String getNewTeacher(){
        return newTeacher;
    }
    public String getKindOf(){
        return kindOf;
    }
    public ArrayList<String> getKlassen(){
        return klassen;
    }

}