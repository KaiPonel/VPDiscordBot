import java.util.*;
public class RowConverter extends Row {     //Converts List entries to Row
    /** Class RowConverter:
     *  Goal: Convert Data into Rows
     *  Class Finished (26/02)
     */



    public ArrayList<Row> convertAllEntires(List<String[]> EntryList)throws Exception {
        String[]tempString;
        for (int i = 0; i < EntryList.size(); i++) {
            tempString =removeAllSpaces(EntryList.get(i));
            EntryList.remove(i);
            EntryList.add(i,tempString);
            for(int j = 0; j<EntryList.get(i).length; j++){
            }
        }
        ArrayList<Row> allRowsOfCurrentPlan = new ArrayList<Row>();


        Row TempRow;
        for(int i = 0; i<EntryList.size(); i++){
            TempRow = new Row();
            TempRow.setSubject(getSubject(EntryList.get(i)));
            TempRow.setKlassen(getKlassen(EntryList.get(i)));
            TempRow.setRoom(getRoom(EntryList.get(i)));
            TempRow.setOldTeacher(getOldTeacher(EntryList.get(i)));
            TempRow.setNewTeacher(getNewTeacher(EntryList.get(i)));
            TempRow.setKindOf(getKindOf(EntryList.get(i)));
            TempRow.setDate(getDateAndHour(EntryList.get(i)));

            //If new Teacher is Eva Kind of gets changed from "Vertretung" to "Vertretung (EVA)"
            if(TempRow.getNewTeacher().equalsIgnoreCase("eva")){
                TempRow.setKindOf("Vertretung (EVA)");
            }

            allRowsOfCurrentPlan.add(TempRow);
        }




        return allRowsOfCurrentPlan;
    }
    private String getKindOf(String[]DATA){
        return DATA[7];
    }
    private String getSubject(String[]DATA){ return DATA[3]; }
    private String getOldTeacher(String[]DATA){
        return DATA[6];
    }
    private String getNewTeacher(String[]DATA){
        return DATA[4];
    }
    private String getRoom(String[]DATA){
        return DATA[5];
    }

    private DateAndHour getDateAndHour(String[]DATA)throws Exception{
        DateAndHour date = new DateAndHour();
        date.setDay(getDay(DATA));
        date.setMonth(getMonth(DATA));
        System.out.println("StartHour: " + getStartHour(DATA));
        System.out.println("EndHour: " + getEndHour(DATA));

        date.setHours(getStartHour(DATA), getEndHour(DATA));
        date.generateCalenderData();
        return date;
    }
    /*

                *** Old Methodes, Bug with 2 digit Hours, solved ***

        private int r1(String[]DATA){
        char starHour = DATA[2].charAt(0);
        int startHour = Character.getNumericValue(starHour);
        return startHour;}
    private int r2(String[]DATA){
        try{
            char endHour = DATA[2].charAt(2);
            int endHour_ = Character.getNumericValue(endHour);
            return endHour_;
        }
        catch(StringIndexOutOfBoundsException Error)
        {
            return -1;
        }
    }
     */
    private int getStartHour(String[]DATA){
        String hours = DATA[2].replaceAll(" ", "");
        if(hours.length() <3){
            return Integer.parseInt(hours);
        }else{
            try {
                int i = 0;
                for (;hours.charAt(i) != '-'; i++) {
                }
                String sub = hours.substring(0, i);
                return Integer.parseInt(sub);
            }catch(Exception E){
                return -1;
            }
        }
    }
    private int getEndHour(String[]DATA){
        String hours = DATA[2].replaceAll(" ", "");
        int i = 0;
        try {
            for (; hours.charAt(i) != '-'; i++) {
            }
        }catch(Exception E){
            return -1;
        }
        String sub = hours.substring(i+1);
        return Integer.parseInt(sub);
    }



    private int getDay(String[]DATA){
        String day_String = "";
        for(int i = 0; DATA[1].charAt(i) != '.'; i++){
            day_String = day_String + DATA[1].charAt(i);
        }
        return Integer.valueOf(day_String);
    }

    private int getMonth(String[]DATA){
        int Count = 0;
        String month_String = "";
        for(int i = 0; DATA[1].charAt(i) != '.'; i++){
            Count = i;
        }
        for(int i = Count+2; DATA[1].charAt(i) != '.'; i++){
            month_String = month_String + DATA[1].charAt(i);
        }
        return Integer.valueOf(month_String);
    }

    private ArrayList<String> getKlassen(String[]DATA){
        ArrayList<String> Klassen = new ArrayList<>();
        if(DATA[0].length() < 4){
            Klassen.add(DATA[0]);
            return Klassen;
        }
        else{
            String current = "";
            for(int i = 0; i<DATA[0].length(); i++){
                current = current + DATA[0].charAt(i);
                if(isNaN(DATA[0].charAt(i))){
                    Klassen.add(current);
                    current = "";
                }
            }
            return Klassen;
        }
    }
    private boolean isNaN(char Char){
        if (Char != '0' && Char != '1' && Char != '2' && Char != '3' && Char != '5' && Char != '6' && Char != '7' && Char != '8' && Char != '9' && Char!= '4'){
            return true;
        }
        return false;
    }
    private String[] removeAllSpaces(String[] input) {
        for (int i = 0; i < input.length; i++) {
            input[i] = input[i].replace(" ", "");
            input[i] = input[i].replace("\t", "");
            input[i] = input[i].replace("\n", "");
            input[i] = input[i].replace("(", "");
            input[i] = input[i].replace(")", "");
            input[i] = input[i].replace("\u0000", "");
        }
        return input;
    }
}