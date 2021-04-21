import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.*;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.util.*;
import javax.security.auth.login.LoginException;
import java.lang.reflect.Array;

/** @Class DiscordBot
 *  @Goal: Control the Discord Bot, sync User Data Base from Client- and from Serverside, and "Connect" the 2 User Elements:
 *                  - userProfile
 *                  - User (from JDA)
 */
public class DiscordBot implements EventListener  {
    private DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private String date;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM HH:mm");
    private Launcher launcherObject;
    private JDA jda;
    private Json_DefaultRequestHandler handler;
    private final String logName = "log";



    private String defaultPath;      //###Der Pfad wurde entfernt.
    private SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

    public DiscordBot(String defaultPath){
        this.defaultPath = defaultPath;
        this.handler = new Json_DefaultRequestHandler(defaultPath);
    }



    /**@Function: launchBot
     * @param launcherObject
     * @Goal: launch the bot
     * @Time of Execution: Once, at the beginning of the program.
     */


    public void launchBot(Launcher launcherObject)throws Exception {
        DiscordBot_EventListener mother_Listener = new DiscordBot_EventListener(jda, launcherObject, defaultPath, this);
        Json_userJoinedHandler logHandler = new Json_userJoinedHandler(defaultPath);
        logHandler.createLogFile("log");
        this.launcherObject = launcherObject;
        try {
            /** @Achtung Bitte nicht den Bot-Token in irgendeiner Form weitergeben.
             */

            //### Token für Discord-Bot wurde entfernt
            jda = new JDABuilder(AccountType.BOT).setToken("Placeholder").build();  //Der Token des Bots wurde aus Sicherheitsgründen entfernt.
            jda.addEventListener(mother_Listener);
        } catch (Exception E) {
            E.printStackTrace();
        }
        /** Timeout at the start of the program for 5 Seconds to make sure, JDA boots up Before the rest of the program gets Execute
         * @Notice If this isnt done, the program will encounter several errors, e.g. JDA.getUsers() will always be 0.
         */
        try {
            System.out.println("Waiting for Full Bootup!");
            Thread.sleep(3000);
        }catch(Exception E){
        }
    }

    /**@Function: peformUserAction
     * @throws Exception
     * @Goal: Sync Users at the beginning of the program. Read the data in the File and add Users to Launcher's Userlists
     * @Time of Execution: Once, at the beginning of the program.
     */
    public void peformUserAction() throws Exception {
        //New System:

        /*
           At the start of exection, the following things will happen:
                - All users will get a File in the Filesystem
                    - If they already have one, it will not be overwritten
                - The File Name equals their DiscordID
                - The File contains the JSON format, and all parameters except the DiscordID are being set to either:
                    - null
                    - false
                - From there on the client will generate userProfiles to all File Accounts.
                - If the Attr. "USEREADY" is true, the user will get added to Ready Users
                        - False -> Unready
         */
        Json_HelpFunctions function = new Json_HelpFunctions(defaultPath);
        for (int i = 0; i < jda.getUsers().size(); i++) {
            if (!function.isFileExisting(new File(defaultPath + jda.getUsers().get(i).getId()))) {
                Json_userJoinedHandler newUser = new Json_userJoinedHandler(defaultPath);
                newUser.createNewUserOnSignUP(jda.getUsers().get(i).getId());
            }
        }
        launcherObject.readyUsers.clear();
        launcherObject.unreadyUsers.clear();
        launcherObject.allUsers.clear();
        for (int i = 0; i < jda.getUsers().size(); i++) {

            userProfile tempProfile = function.getUserProfileFromFileData(jda.getUsers().get(i).getId());

            tempProfile.setDiscordUser(jda.getUsers().get(i));
            launcherObject.allUsers.add(tempProfile.getDISCORD_ID());
           if(function.checkUserReady(jda.getUsers().get(i).getId())){


                launcherObject.readyUsers.add(tempProfile.getDISCORD_ID());
           }
           else{
               launcherObject.unreadyUsers.add(tempProfile.getDISCORD_ID());
           }

        }

    }


    /** @param: All Parameters used in:
     *  - Function: postNewEntries
     *  - Function: altertUser
     */
        private String klassenAsString;
        private String newTeacher;
        private String oldTeacher;
        private String kindOf;
        private String room;
        private String subject;
        private String dateAsString;
        /** @param: TextChannel of the message: */
        private TextChannel debugChannel = null;

    /**@Function: postNewEntries
     * @param: ArrayList which contains all new Entries in Row-Format
     * @Goal: Post Debug messages in #DEBUG
     * @Time of Execution: Every time there is a new Entry added to the plan
     */
    public void postNewEntries(ArrayList<Row> entryList, String addedOrRemoved){
        for(int i = 0; i<entryList.size(); i++){
            /** @param: date is being set to the current Date | Debugchannel is the Textchannel of the message*/
            debugChannel = jda.getTextChannelById("547095551880200192"); //Main-Server: 547095551880200192      //Beta Server: 591611147996889089
            klassenAsString = "";
            for(int j = 0; j<entryList.get(i).getKlassen().size(); j++){

                /** @param: Values are being assigned */
                klassenAsString = klassenAsString + entryList.get(i).getKlassen().get(j) + " ";
            }
                newTeacher = entryList.get(i).getNewTeacher();
                oldTeacher = entryList.get(i).getOldTeacher();
                kindOf = entryList.get(i).getKindOf();
                room = entryList.get(i).getRoom();
                subject = entryList.get(i).getSubject();
                dateAsString = entryList.get(i).getDate().getDateAsString();





            final String message = "[" + this.date +  "] " + addedOrRemoved + " \n Datum: " + dateAsString + "\n Klasse(n): " + klassenAsString + "\n" + " Fach: " + subject + " \n (eig.) Lehrer: " + oldTeacher + "\n(aktueller) Lehrer: " + newTeacher + "\n ART: " + kindOf;
            /** @Notice Posting the message in #Debug and in the Console for DEBUG aswell (In case the Bot lost connection) */
            debugChannel.sendMessage(message).queue();

            /** @param klassenAsString is getting resetted*/
        }
    }
    /** @param: TextChannel for Error Messages */
    private TextChannel errorChannel = null;

    /**
     * @Function: postErrorMessage
     * @param kindOfError   - which contains the Errormessage
     * @Goal: Post the Error in #ErrorChannel
     * @Time of Execution: Everytime a error happens in Launcher
     */
    public void postErrorMessage(String kindOfError){
       errorChannel =  jda.getTextChannelById("547141178827735080");  //Main Channel: 547141178827735080        //Beta: 591611197644734505
        /** @Notice: Posting the message in #ErrorChannel and the Console for DEBUG aswell (In case the bot lost connection) */
        errorChannel.sendMessage("[" + date + "] ERROR:" + "\n" + "Launcher has thrown the following Error: \n" + kindOfError + "\n").queue();
        System.out.println("[" + this.date + "] ERROR:" + "\n" + "Launcher has thrown the following Error: \n" + kindOfError + "\n");
    }


    /** @param updateChannel    - Channel where updates are being posted in. */
    private Channel updateChannel = null;

    /**
     * @Function: postUpdateMessage
     * @param neueEntries   - Anzahl neuer Entries
     * @param amountOfUser  - Anzahl der aktuellen User
     * @param addMessage    - Ggf. message die Hinzugefügt wird
     * @Goal: Post an update message every Minute for debug
     * @Time Of Execution: Every 60 Seconds (Or other time intervall, depending on Settings in Launcher.java)
     */
    public void postStatusMessage(int neueEntries, int amountOfUser, String addMessage){
        updateChannel = jda.getTextChannelById("547093428006027264"); //Main 547093428006027264  //Beta: 591611234760130562
        /** @Information Status message does only get Posted in Discord. */
         ((TextChannel) updateChannel).sendMessage("[DEBUG " + date + "]: \nCurrent Users: " + amountOfUser + "\n" + "NewEntries: " + neueEntries + "\n" + "" + addMessage).queue();
    }

    private Channel setupChannel = null;

    private String publicMessage =  "Hallo, ich bin der Bot für den Vertretungsplan!\n" +
            "Um mit mir zu kommunizieren, drücke auf meinem Namen und drücke auf 'Nachricht senden'.\n" +
            "Ich habe dich bereits per Direktnachricht angeschrieben.";

    public void postSetup(){
        setupChannel = jda.getTextChannelById("547093428006027264"); //Main 547093428006027264  //Beta: 591611234760130562
        /** @Information Status message does only get Posted in Discord. */

        if(setupChannel == null){
            System.out.println("Channel Null!");
        }

        ((TextChannel) setupChannel).sendMessage(publicMessage).queue();
    }


    /**
     * @Function: alertUser
     * @param changes
     * @param user
     * @Goal: Directly message a user witch the changes relevant for him.
     * @Time Of Execution: Everytime an entry is relevant.
     */


    public void alertUser(ArrayList<Row> changes, userProfile user, String addedOrRemoved)throws Exception{
        /**@Information: Get the DiscordUser from the userProfile */
        User DiscordUser = null;

        for(int i = 0; i<launcherObject.allUsers.size(); i++){
            if(user.getDISCORD_ID().equalsIgnoreCase(launcherObject.allUsers.get(i))){
                DiscordUser = jda.getUserById(user.getDISCORD_ID());
            }
        }

        if(DiscordUser == null){
            return;
        }
        /**@Return: If the user does not have a DiscordID linked (error), the method will return */


        /**@param: klassenAsString needs to get set via loops*/
        for(int i = 0; i<changes.size(); i++){
            klassenAsString = "";
            for(int j = 0; j<changes.get(i).getKlassen().size(); j++){
                klassenAsString = klassenAsString + changes.get(i).getKlassen().get(j) + " ";
            }
            /**@Information: Setting the Attributes */
            newTeacher = changes.get(i).getNewTeacher();
            oldTeacher = changes.get(i).getOldTeacher();
            kindOf = changes.get(i).getKindOf();
            room = changes.get(i).getRoom();
            subject = changes.get(i).getSubject();
            dateAsString = changes.get(i).getDate().getDateAsString();


            final String message = "[" + date +  "] " + addedOrRemoved + " \nDatum: " + dateAsString + "\nART: " + kindOf + "\nLehrer: " + oldTeacher + "\nFach: " + subject + "\nKlasse(n): " + klassenAsString + "\nRaum: " + room + "\n\n";
            /**@Information: Opening a Privatchannel and sending the message */
            DiscordUser.openPrivateChannel().queue((channel)->{
                channel.sendMessage(message).queue();
            });
            klassenAsString = "";

            //Saving Log Data:
            handler.addLogEntry(logName, changes.get(i), user, addedOrRemoved, date);




        }
    }

    public void deleteOldMessages(){
        //List with all User Channels
        List<PrivateChannel> userChannel =  jda.getPrivateChannels();
        //For all Users on Discord:
        for(int i = 0; i<userChannel.size(); i++){
            //All messages in one Private Room
            List<Message> messagesOfUser = userChannel.get(i).getHistory().getRetrievedHistory();
            //For all messages:
            for(int j = 0; j<userChannel.get(i).getHistory().getRetrievedHistory().size(); j++){
                String singleMessage = messagesOfUser.get(i).getContentRaw();
                //The Programm only needs to check Bot messages
                if(messagesOfUser.get(i).getAuthor().isBot()){

                    //Only messages (from Bopt) which contain certain information start with "["
                    if(singleMessage.charAt(0) == '['){

                        //Gets the part with the DATE of the message

                        try {
                            int k = 0;
                            for (; singleMessage.charAt(k) != '\n'; k++) {
                            }
                            for (; singleMessage.charAt(k) != ' '; k++) {
                            }
                            k++;
                            int h = k;
                            for (; singleMessage.charAt(h) != ' '; h++) {
                            }
                            String dateMessage = singleMessage.substring(k, h);
                            if(dateIsOld(dateMessage)){
                                messagesOfUser.get(i).delete();
                            }
                        }catch(Exception E){
                            postErrorMessage("Error while deleting Messages " + E.toString());
                        }
                    }
                }
            }
        }
    }

    private boolean dateIsOld(String rawDate) throws Exception{
        String date = fixDate(rawDate);
        Calendar c = Calendar.getInstance();
        date = date + "/" + c.get(Calendar.YEAR);
        try {
            Date creationDate = format.parse(rawDate);
            if(creationDate.before(c.getTime())){
                return true;
            }
        }catch(Exception E){
            throw new Exception("Invalid Format");
        }

    return true;
    }
    private String fixDate(String date) {
        date = date.replaceAll(" ", "");
        String day = "";
        String month = "";
        int i;
        for (i = 0; date.charAt(i) != '.'; i++) {
            day = day + date.charAt(i);
        }
        i++;
        for(; i<date.length(); i++){
            month = month + date.charAt(i);
        }
        if(day.length() == 1){
            day = "0" + day;
        }
        if(month.length() == 1){
            month = "0" + month;
        }
        return (day + "/" + month);
    }
    public void adjustDate(){
        this.date = simpleDateFormat.format(new Date());
    }

}
