

import java.util.ArrayList;

/**@Class: relevantChecker
 * @Goal: Check if an entry is relevant for a User
 * @Information: Class Finished (26/02)
 */

public class relevantChecker {
    private int[] currentHours;
    private int currentCalendarDay;
    private String currentSubject;
    private String currentTeacher;
    public relevantChecker(){}

    /**
     * @Function: checkForRelevantChanges
     * @Goal: get all relevant Entries for a specific User
     * @param user
     * @param entries
     * @return relevantEntries (ArrayList<Row>)
     */

    public ArrayList<Row> checkForRelevantChanges(userProfile user, ArrayList<Row> entries){

        ArrayList<Row> possibleRelevantEntries;
        // Wenn der Benutzer ein Schüler ist, so sind nur die Einträge während der Stunden des Benutzers relevant, sonst alle.

        //Wenn der Mode des Nutzers "null" ist, so wird der Mode = student gesetzt.
        if(user.getMode() == null){
            user.setMode("student");
            System.out.println("User mode was Null, and had to be changed!");
        }

        if(user.getMode().equalsIgnoreCase("student")) {
            possibleRelevantEntries = getAllEntriesForClass(user.getKlasse(), entries);

        }else{
            possibleRelevantEntries = entries;
        }

        /** Actual Entries */
        ArrayList<Row> relevantEntries = new ArrayList<>();

        for(int i = 0; i<possibleRelevantEntries.size(); i++){
            /** (DE)
             * @param currentHours          -   Stunden auf dem Vertretungsplan
             * @param currentCalenderDay    -   Kalendertag auf dem Vertretungsplan
             * @param currentSubject        -   Fach was der Schüler gerade hat
             * @param currentTeacher        -   Lehrer, den der Schüler gerade hat
             */
            currentHours = possibleRelevantEntries.get(i).getDate().getHours();
            currentCalendarDay = possibleRelevantEntries.get(i).getDate().getCalenderDay();

            int cHour = currentHours[0]-1;
            if(cHour<0){
                cHour = 0;
            }

            currentSubject = user.getSubjectAtDayAndHour(currentCalendarDay, cHour);;
            currentTeacher = user.getTeacherAtDayAndHour(currentCalendarDay, cHour);                             // currentHours Seem to have an Offset by 1Hour+1 Fix -1
            /*  Erklärung der Überprüfung:
                - Stunden müssen nicht überprüft werden, da der Lehrer von der Stunde je abhängig ist, die Stunde wäre daher so oder so gleich.
                - Das Fach kann nicht direkt überprüft werden und fällt daher weg. Bei Ausfall steht auf dem Vertretungsplan "---", weshalb eine Überprüfung hier überflüssig ist.
                - Der Lehrer, ist somit die einzige Variable die zu der Stunde überprüft werden kann und muss, er kann nicht 2 Einträge gleichzeitig haben,
                - Die Chance das hier ein Fehler passiert, ist extrem gering. -> "Alle Angaben ohne Gewähr".
             */

            if(user.getMode().equalsIgnoreCase("student")) {
                System.out.println("currentTeacher:" + currentTeacher);
                System.out.println(currentTeacher.length());
                System.out.println("From Entry:" + possibleRelevantEntries.get(i).getOldTeacher());
                System.out.println(possibleRelevantEntries.get(i).getOldTeacher().length());
                if (currentTeacher.equalsIgnoreCase(possibleRelevantEntries.get(i).getOldTeacher())) {            //Hours are being checked earlier. SO no need here
                    System.out.println("Added to relevant List!");
                    relevantEntries.add(possibleRelevantEntries.get(i));
                }
            }else if(user.getMode().equalsIgnoreCase("teacher")){
                if(currentTeacher.equalsIgnoreCase(possibleRelevantEntries.get(i).getOldTeacher()) || currentTeacher.equalsIgnoreCase(possibleRelevantEntries.get(i).getNewTeacher())){
                    relevantEntries.add(possibleRelevantEntries.get(i));
                }
            }
        }
        return relevantEntries;
    }

    /**
     * @Function: getAllEntriesForClass
     * @Goal: returns all Entries for a Class
     * @param klasse
     * @param entries
     * @return classList
     * @Time of Execution: Everytime there is a new Entry.
     */


    private ArrayList<Row> getAllEntriesForClass(String klasse, ArrayList<Row> entries){
        ArrayList<Row> classList = new ArrayList<>();
        for(int i = 0; i<entries.size(); i++){
            for(int j = 0; j<entries.get(i).getKlassen().size(); j++){
                if(klasse.toLowerCase().contains(entries.get(i).getKlassen().get(j).toLowerCase()) || entries.get(i).getKlassen().get(j).toLowerCase().contains(klasse.toLowerCase())){
                    classList.add(entries.get(i));
                }
            }
        }
        return classList;
    }





}