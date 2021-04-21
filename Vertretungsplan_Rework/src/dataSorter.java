import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class dataSorter {
    private String keyKlasse = "klasse";
    public ArrayList<Row> toRows(ArrayList<String>websiteText){
        //1. Sort document to single Lines:
            ArrayList<String> allLines = getSingleRows(websiteText);

        //2. Know which lines are relevant (entries)
            ArrayList<String> entries = getRelevantRows(allLines);
            System.out.println("Amount of relevant Rows Found: " + entries.size());

        //3. remove Illegal characters
            entries = removeIllegalChars(entries);

        //4. getCategories of each Row
        ArrayList<HashMap<String, String>> seperatedEntries = getCategories(entries);

        //5. convert each seperatedEntry into a Row:
        ArrayList<Row> rowList = convertToRows(seperatedEntries);
        System.out.println("Entries converted to Rows: " + rowList.size());

        return rowList;
    }
    public ArrayList<String> getRelevantRows(ArrayList<String>allLines){
        ArrayList<String>relevantEntries = new ArrayList<>();

        //Criteria:
             // -Uses Tabulators
             // -Must have numbers



        //For all lines...
        for(int i = 0; i<allLines.size(); i++){
            //Counts amount of Tabs used
            int amountOfTabs = 0;

            //if Line has a number -> True
            boolean hasNumber = false;

            //For each char in line [i]...
            for(int j = 0; j<allLines.get(i).length(); j++){
                if(Character.isDigit(allLines.get(i).charAt(j))){
                    hasNumber = true;
                }else{
                    if(allLines.get(i).charAt(j) == '\t'){
                        amountOfTabs++;
                    }
                }
            }
            if(hasNumber && amountOfTabs>8){
                relevantEntries.add(allLines.get(i));
            }
        }
        return relevantEntries;
    }

    private ArrayList<String>getSingleRows(ArrayList<String>websiteText){
        ArrayList<String>lines = new ArrayList<>();
        for(int i = 0; i<websiteText.size(); i++){
            String currentLine = "";
            for(int j = 0; j<websiteText.get(i).length(); j++){
                if(websiteText.get(i).charAt(j) == '\n'){
                    lines.add(currentLine);
                    currentLine = "";
                }else {
                    currentLine = currentLine + websiteText.get(i).charAt(j);
                }
            }
            lines.add(currentLine);
        }
        return lines;
    }

    private ArrayList<String>removeIllegalChars(ArrayList<String>entries){
        ArrayList<String> fixedEntries = new ArrayList<>();
        for(int i = 0; i<entries.size(); i++){
           String temp;
             temp = (entries.get(i).replaceAll("\\(", ""));
             temp = (temp.replaceAll("\\)", ""));
             temp = (temp.replaceAll(" ", ""));
           fixedEntries.add(temp);
        }
        return fixedEntries;
    }

    private ArrayList<HashMap <String, String>> getCategories(ArrayList<String>entries){
        ArrayList<HashMap<String, String>>seperatedEntries = new ArrayList<>();
        for(int i = 0; i<entries.size(); i++){
            //Creates new Row for Entry

            //CurrentText var:
            String temp = "";

            HashMap<String, String> singleStatements = new HashMap<>();

            int amountOfTabs = 0;
            for(int j = 0; j<entries.get(i).length(); j++){
                if (entries.get(i).charAt(j) == '\t') {
                    amountOfTabs++;
                    if(amountOfTabs == 1){
                        singleStatements.put(keyKlasse, temp);
                    }else  if(amountOfTabs == 2){
                        singleStatements.put("datum", temp);
                    }else  if(amountOfTabs == 3){
                        singleStatements.put("stunde", temp);
                    }else  if(amountOfTabs == 4){
                        singleStatements.put("fach", temp);
                    }else  if(amountOfTabs == 5){
                        singleStatements.put("newLehrer", temp);
                    }else  if(amountOfTabs == 6){
                        singleStatements.put("raum", temp);
                    }else  if(amountOfTabs == 7){
                        singleStatements.put("oldLehrer", temp);
                    } else if(amountOfTabs>7){
                        singleStatements.put("art", temp);
                        break;
                    }
                    temp = "";
                }
                else{
                    //Adds Up
                    temp = temp + entries.get(i).charAt(j);
                }
            }
            seperatedEntries.add(singleStatements);
        }
        return seperatedEntries;
    }

    private ArrayList<Row> convertToRows (ArrayList<HashMap <String, String>> seperatedEntries){
        ArrayList<Row> rowList = new ArrayList<>();
        for(int i = 0; i<seperatedEntries.size(); i++){
            try {
                Row entryRow = new Row();

                String temp = "";

                //Klassen:

                ArrayList<String> klassen = new ArrayList<>();
                String klassenEntry = seperatedEntries.get(i).get(keyKlasse);
                for (int j = 0; j < klassenEntry.length(); j++) {
                    if (klassenEntry.charAt(j) == ',') {
                        klassen.add(temp);
                        temp = "";
                    } else {
                        temp = temp + klassenEntry.charAt(j);
                    }
                }
                klassen.add(temp);
                temp = "";
                entryRow.setKlassen(klassen);
                klassen = null;
                klassenEntry = null;

                //Date (Stunden und Datum)

                DateAndHour datum = new DateAndHour();

                String datumEntry = seperatedEntries.get(i).get("datum");
                String stundeEntry = seperatedEntries.get(i).get("stunde");

                //Datum:
                int pointPosition = 0;
                for (int j = 0; j < datumEntry.length(); j++) {
                    if (datumEntry.charAt(j) == '.') {
                        pointPosition = j;
                        break;
                    }
                }
                datum.setDay(Integer.parseInt(datumEntry.substring(0, pointPosition)));
                datum.setMonth(Integer.parseInt(datumEntry.substring(pointPosition+1, datumEntry.length()-1)));
                datum.generateCalenderData();

                //Stunde:
                if(stundeEntry.length()>1){
                    datum.setHours(Integer.parseInt(stundeEntry.substring(0,1)), Integer.parseInt(stundeEntry.substring(2,3)));
                }else{
                    datum.setHours(Integer.parseInt(stundeEntry), -1);
                }

                entryRow.setDate(datum);
                datum = null;

                //Fach
                entryRow.setSubject(seperatedEntries.get(i).get("fach"));

                //NeuerLehrer
                entryRow.setNewTeacher(seperatedEntries.get(i).get("newLehrer"));

                //EigLehrer
                entryRow.setOldTeacher(seperatedEntries.get(i).get("oldLehrer"));

                //Raum
                entryRow.setRoom(seperatedEntries.get(i).get("raum"));

                //Set "Art"
                if(!entryRow.getNewTeacher().equalsIgnoreCase("EVA")){
                    entryRow.setKindOf(seperatedEntries.get(i).get("art"));
                }else{
                    entryRow.setKindOf("(Vertretung) - EVA");
                }
                rowList.add(entryRow);
            }catch(Exception E){
                E.printStackTrace();
                System.out.println("Failed to Sort an Entry (Posting Entry Detail):");
                System.out.println(seperatedEntries.get(i).get(keyKlasse));
                System.out.println(seperatedEntries.get(i).get("datum"));
                System.out.println(seperatedEntries.get(i).get("stunde"));
                System.out.println(seperatedEntries.get(i).get("fach"));
                System.out.println(seperatedEntries.get(i).get("newLehrer"));
                System.out.println(seperatedEntries.get(i).get("raum"));
                System.out.println(seperatedEntries.get(i).get("oldLehrer"));
                System.out.println(seperatedEntries.get(i).get("art"));
            }
        }
        return rowList;
    }
}
