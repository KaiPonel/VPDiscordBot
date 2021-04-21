
import java.io.File;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
public class Launcher {



    /**
     *
     *
     * @author Kai Ponel
     * @version 2.5
     *  Diese Version ist die Version, mit den Funktionen (und weiteren) welche in der Facharbeit beschrieben wurde.
     *  Nach der Fertigstellung der Schriftlichen Arbeit wurde ein Befehl hinzugefügt. !verfify [Nutzername]:[Password]     -   Verifiziert den Nutzer zum Benutzen des Bots
     *  Diese Funktion wurde in der schriftlichen Arbeit nicht genannt, da er da noch nicht fertig war.
     *
     *  @date 18/03
     *
     *  Wichtig:
     *  Ich werde dieses Programm auch nach der Entwicklung noch weiter verändern. Die Version des Bots und seine Funktionen werden sich nach Abgabe der Facharbeit weiterhin verändern
     *  und es ist möglich, dass manche Funktionen entfernt, verändert oder hinzugefügt werden.
     *  -   Die Version auf meinem Raspberry Pi wird fast täglich gegen ein Aktuelles dev-Build ausgetauscht und enthält manchmal starke Veränderungen.
     *  -   Wenn elementare Funktionen des Bots in der aktuellen Funktion nicht funktionieren ist dies Übergangsweise.
     *
     *  Ich versuche innerhalb der Nächsten 2 Wochen (18.03-1.04) stabile Builds zu benutzen, jedoch bastel ich aktuell an einem noch neueren File-System, welches evtl. in dieser Zeit getestet wird..
     */



    /**
     * @param neuerPlan     - Speichert die neu geladenen Einträge im Row Format
     */
    public ArrayList<Row> neuerPlan;
    /**
     * @param oldPlan       - Speichert die alten Einträge (die vom vorherigen Durchlauf) im Row Format
     */
    public ArrayList<Row> oldPlan;
    /**
     * @param newEntries    - Beschreibt die neuen Einträge; Differenz von oldPlan zu neuerPlan (auch im Row Format)
     */
    private ArrayList<Row> newEntries;
    /**
     * @param newEntreies   - Beschreibt die entfernten Einträge vom Plan
     */
    private ArrayList<Row> removedEntries = new ArrayList<>();
    /*
       Windows:   "D:\Coding\IdeaProjects\Vertretungsplan_Rework\src\Users\"
       Rasp. Pi:  "/home/pi/Desktop/Vertretungsplan/Users/"

     */
    /**
     * @param defaultPath       -   Der Pfad für die Dateein
     */
    private final String defaultPath = "/home/pi/Desktop/Vertretungsplan/Users/";
    /**
     * @param bot           - Die Instanz des aktiven Discordbots
     */
    private DiscordBot bot = new DiscordBot(defaultPath);
    /**
     * @param addMessage    - Eine Nachricht welche hinzugefügt werden kann (zu einer DEBUG-Nachricht)
     */
    private String addMessage;
    /**
     * @param SLEEP_AMOUNT  - Der Wert, um wie viel der nächste Durchgang verzögert wird
     */
    private int SLEEP_AMOUNT = 40000;
    /**
     * @param errorMessage  - Wenn ein Fehler auftritt, wird in diesem String die (ungefähre) Fehlernachricht gespeichert
     */
    private String errorMessage = "Unknown";
    /**
     * @param allUsers  -   Beschreibt alle Nutzer.
     */
    public ArrayList<String> allUsers = new ArrayList<>();
    /**
     * @param readyUsers    - Beschreibt die User (im userProfile Format), welche für die vollständige Nutzung des Bots geeignet sind
     */
    public ArrayList<String> readyUsers = new ArrayList<>();
    /**
     * @param unreadyUsers  - Beschreibt die user (""), welche (noch) nicht für die vollständige Nutzung des Bots geeignet sind
     */
    public ArrayList<String> unreadyUsers = new ArrayList<>();

    private Json_HelpFunctions jsonFunctions;





    /**@Function: main*/
    public static void main(String[]args)throws Exception{
        Launcher VertretungsplanBot = new Launcher();
        VertretungsplanBot.run();
    }


    public void run()throws Exception {
        /** Setup, this will only Run Once, for more Information about the setup read DiscordBot.java and userConverter.java */
        jsonFunctions = new Json_HelpFunctions(defaultPath);
        bot.launchBot(this);
        bot.adjustDate();


        /** @Information: While(true) sorgt dafür, dass der Bot immer wieder bestimmte Abläufe wiederholt */
        while (true) {

            /**@Information: Der Teil, welcher immer wieder ausgeführt wird steht in einem Try und Catch Block.
             * @Information: Dies hilft der Fehlersuche.
             * @Information: Außerdem gibt es eine ErrorMessage, welche sich bei jedem Schritt ändert. Sollte das Programm an einem Punkt eine Exception haben, so kann man im nachhinein den Fehler schneller finden
             */
            if(this.hasInternet()) {
                try {
                    /** -0: Nutzerdaten refreshen */

                    bot.peformUserAction();
                    errorMessage = "Failed to perform User action";

                    /** -0.5: Zu alte Nachrichten Löschen */

                    bot.deleteOldMessages();
                    errorMessage = "Failed to delete old messages";

                    /** - 1: Daten von dem Vertretungsplan "Scrapen" und in Liste Speichern */

                    errorMessage = "Failed to scrape HTML-Content";
                    HtmlCodeGetter htmlSiteReader = new HtmlCodeGetter();
                    List<String> rawText = htmlSiteReader.getRawTexts();

                    /** - 2: Die Daten von der Website auswerten und in einem String[] zurückgeben */

                    errorMessage = "Failed to sort Data from HTML-Text";

                    dataSorter sort = new dataSorter();
                    neuerPlan = sort.toRows((ArrayList<String>) rawText);



                    /** 4: Neuen Plan und alten Plan abgleichen (Alter plan ist bei 1. Start = null) */

                    errorMessage = "Failed to compare new and old Plans";
                    if (oldPlan != null) {
                        rowComparer compareRows = new rowComparer(oldPlan, neuerPlan);
                        newEntries = compareRows.comparePlansAddedEntries();
                        removedEntries = compareRows.comparePlansRemovedEntires();
                    } else {
                        newEntries = neuerPlan;
                    }

                    /** 4.1: Alten Plan setzen */

                    errorMessage = "Failed to set old Plan";
                    oldPlan = neuerPlan;

                    /** 5: Bot Postet neue Entries in dem #Debug Discord Channel */

                    errorMessage = "Failed to post new Entries to #DEBUG";
                    if (newEntries.size() != 0) {
                        bot.postNewEntries(newEntries, "Eintrag Hinzugefügt");
                        /** Add Message setzen */
                        addMessage = "Neue Entries gefunden!";
                    }
                    if (removedEntries.size() != 0) {
                        errorMessage = "Failed to post removed Entries";
                        bot.postNewEntries(removedEntries, "Eintrag Entfernt!");
                        addMessage = "Es wurden Einträge entfernt!";
                    }

                    /** 6: Schauen welche Einträge für welche Nutzer relevant sind */

                    ArrayList<Row> relevantEntriesForUser;
                    relevantChecker check = new relevantChecker();

                    /** 6.1: Alle User per for-Schleife durchgehen */

                    for (int i = 0; i < readyUsers.size(); i++) {
                        //Hinzugefügte Objekte:

                        userProfile currentUser = jsonFunctions.getUserProfileFromFileData(readyUsers.get(i));
                        errorMessage = "Failed to get added Entries for specific User";
                        relevantEntriesForUser = check.checkForRelevantChanges(currentUser, newEntries);
                        if (relevantEntriesForUser.size() != 0) {
                            /** Wenn relevante Entries da sind (!=0), User benachrichtigen */
                            bot.alertUser(relevantEntriesForUser, currentUser, "Eintrag hinzugefügt!");
                        }
                        errorMessage = "Failed to get removed Entries for specific User";
                        //Entfernte Objekte:
                        relevantEntriesForUser = check.checkForRelevantChanges(currentUser, removedEntries);
                        if (relevantEntriesForUser.size() != 0) {
                            bot.alertUser(relevantEntriesForUser, currentUser, "Eintrag entfernt!");
                        }


                    }
                    /** 7: DEBUG Nachricht schreiben (Dieser Schritt wird in naher Zukunft entfernt) */

                    errorMessage = "Failed writing Status-Update";

                    bot.postStatusMessage(newEntries.size(), readyUsers.size(), addMessage);

                    /** 8: Nächsten Durchlauf Delayen mit Thread.Sleep(); */

                    //Adjust Date//
                    bot.adjustDate();
                    errorMessage = "Failed to Sleep :C";
                    Thread.sleep(SLEEP_AMOUNT);


                    /** 9: Arbeitsspeicher Temporär löschen, damit während des Sleeps kein Speicher falsch gebraucht wird */
                    clearRam();
                } catch (Exception E) {
                    /**
                     *  @Debug: Wenn während des Durchgangs ein Fehler auftritt, wird der Bot die Fehlernachricht in #Errormessages sowie in die Konsole schreiben
                     */
                    E.printStackTrace();
                    bot.postErrorMessage(errorMessage);
                    errorMessage = "";
                    addMessage = "";
                }
            }
        }   //While
    }   //run();
    private void clearRam(){
        allUsers.clear();
        unreadyUsers.clear();
        readyUsers.clear();
    }
    private boolean hasInternet(){
        // in case of Linux change the 'n' to 'c'
        boolean reachable = false;
        try {
            Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.com");
            int returnVal = p1.waitFor();
            //System.out.println("Latency: " + returnVal);
            reachable = (returnVal==0);
            return reachable;
        }catch(Exception E) {
            return false;
        }
    }



}//Launcher.java

