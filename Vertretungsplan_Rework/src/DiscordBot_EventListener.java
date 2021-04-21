import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.io.File;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.HashMap;

public class DiscordBot_EventListener extends ListenerAdapter {
    // ### Klassen-Namen wurden entfernt
    private final char[] unwated = {' ', '\n', '\t', '[', ']', ')', '(', '{', '}', '-', '_', '#', '.', ','};
    private final String[] classNumbers = {"Placeholder"};
    private String defaultPath;
    private Json_DefaultRequestHandler handler;
    private String input;
    private HashMap <String, Integer> weekDaysIntoNumbers = new HashMap<>();
    JDA jda;

    //New RelevantChecker to Check if there is new Information for the User upon changing Input
    relevantChecker relevantChecker = new relevantChecker();

    Json_HelpFunctions jsonFunction;

    DiscordBot bot;
    Launcher launcherObject;
    public DiscordBot_EventListener(JDA jda, Launcher launcherObject, String defaultPath, DiscordBot bot){
        this.jda = jda;
        this.launcherObject = launcherObject;
        this.defaultPath = defaultPath;
        this.bot = bot;
        weekDaysIntoNumbers.put("mo", 0);
        weekDaysIntoNumbers.put("di", 1);
        weekDaysIntoNumbers.put("mi", 2);
        weekDaysIntoNumbers.put("do", 3);
        weekDaysIntoNumbers.put("fr", 4);
        handler = new Json_DefaultRequestHandler(defaultPath);
        jsonFunction = new Json_HelpFunctions(defaultPath);
    }
    private String[] welcomeWords = {"Hallo", "Hi", "Moin"};
    private String firstStep = "Willkommen auf dem Discord-Server.\n" +
            "Ich bin ein einfacher, von Kai Ponel (@Kaipo) erstellter, Bot.\n" +
            "Damit du meine Dienste in Anspruch nehmen kannst sind nur wenige Schritte notwendig.\n" +
            "Hinweis: Die eckigen Klammern müssen nicht mitgeschrieben werden. \n \n" +
            "1. Melde dich mit den Anmeldedaten des Vertretungsplans an.\n" +
            "Schreibe dazu: \n\n`!verify [Nutzername]:[Passwort]`\n\n"
            + "Dieser Schritt ist einmalig jedoch aufgrund des Datenschutzes notwendig.\n";

    private String secondStep = "Gebe nun als nächstes deine Klasse an.\n" +
                                "Mach dies mit dem Befehl: \n\n" +
                                "`!setKlasse [Klasse]`\n\n" +
                                "Wenn du z.B. in die 13. gehst, schreibe: \n\n" +
                                "`!setKlasse 13`";
    private String thirdStep =  "\n" +
                                "\nFüge als nächstes die Einträge deines Stundenplans ein.\n" +
                                "Nutze dafür den Befehl: \n\n" +
                                "`!add [WochenTag] [Stunde(n)] [Lehrer Kürzel]`\n\n" +
                                "Beispiel für Einzelstunden:\n" +
                                "`!add Mo 1 ABC`\n" +
                                "Dieser Befehl würde für die erste Stunde am Montag das Kürzel 'ABC' eintragen\n\n" +
                                "Beispiel für Doppelstunden: \n" +
                                "`!add Di 3/4 DEF`\n" +
                                "`!add Mi 8-9 XYZ`\n\n" +
                                "Dieser Befehl trägt am Dienstag für die 3. und 4. Stunde das Kürzel 'DEF' bzw. am Mittwoch für die 8/9 Stunde das Kürzel 'XYZ' ein.\n\n" +
                                "Die Wochentage werden abgekürzt mit: \n `[Mo], [Di], [Mi], [Do], [Fr]`\n\n" +
                                "Doppelstunden müssen mit einem '-' oder einem '/' getrennt werden. \n" +
                                "Nutze diesen Befehl für all deine Einzel- bzw. Doppelstunden";

    private String fourthStep = "Füge entweder mehr Einträge hinzu oder schließe den Vorgang mit `!finishplan` ab.\n" +
                                "Du kannst auch nach Abschließung des Vorgangs weitere Eintäge mit\n `!add` hinzufügen";
    private String publicMessage =  "Hallo, ich bin der Bot für den Vertretungsplan!\n" +
                                    "Um mit mir zu kommunizieren drücke auf meinem Namen und drücke auf 'Nachricht senden'";

    public void onMessageReceived(MessageReceivedEvent event){
        /** @param Message - Message from the MessageEvent */
        Message message = event.getMessage();
        /** @param contentOfMessage - content of the Message */
        String contentOfMessage = message.getContentRaw().toLowerCase();
        /** @param channel - The Channel the message has been posted */
        PrivateChannel channel = event.getPrivateChannel();
        /** @param author   - The Author of the message */
        User author = event.getAuthor();
        /** @param authorProfile    - The AuthorProfile of the Author */
        userProfile authorProfile = null;
        try {
            if(!author.isBot()) {
                authorProfile = jsonFunction.getUserProfileFromFileData(author.getId());
            }
        }catch(Exception E){
            System.out.println("Failed to Load profile of User");
        }
        /** @Feature: If the message is sent from the bot, it will get ignored. */
        /** @Command: Welcome (Hallo, Hi, Moin)
         *  @Usage: Say Hello to the bot
         *  @Response: Bot says hello aswell in PrivatChannel
         */
        if (event.getAuthor().isBot()) {
            return;
        }
        else if (MessageInlucdesArray(contentOfMessage, welcomeWords)) { //If User says WelcomeWords
            reactToWelcomeMessage(channel);
        }


        /** @Command: !kill
         *  @Usage: Kills the process / shuts down the Bot
         *  @Response: One message, after that the bot will shut down.
         *  @Notice: This Command can only be executed by Kaipo#0525 (!) */
        else if (contentOfMessage.startsWith("!kill") && event.getAuthor().getAsTag().equalsIgnoreCase("Kaipo#0525")){
            channel.sendMessage("Aye Aye Boss, The Process has been Killed, see you later").queue();
            System.exit(0);
        }
        else if (contentOfMessage.startsWith("!sendsetupmessage") && event.getAuthor().getAsTag().equalsIgnoreCase("Kaipo#0525")){
            bot.postSetup();
        }

        else if(contentOfMessage.startsWith("!help") || contentOfMessage.startsWith("!commands")){
            channel.sendMessage("\"Hallo\"\t\t\t\t-\tBot Schreibt Moin\n" +
                    "\"!verify[Nutzername]:[Passwort] -\tVerifiziert den Account für den Vertretungsplan (Nutzername und Password sind die, des Vertretungsplanes)\n" +
                    "\"!setKlasse [Klasse]\"\t\t-\tSetzt die Klasse\n" +
                    "\"!printKlasse\"\t\t\t-\tGibt die (aktuelle) Klasse zurück\n" +
                    "\"!setmode [Mode]\"\t\t-\tSetzt den Modus eines Benutzers (Schüler/Lehrer)\n" +
                    "\"!printmode\"\t\t\t-\tGibt den aktuellen Modus zurück\n" +
                    "\"!setkrz [Kürzel]\"\t-\tSetzt ein Kürzel für alle Stunden\n" +
                    "\"!add [Wochentag] [Stunde] [Lehrer]\t-\tSetzt einen Eintrag für eine Stunde\n" +
                    "\"!setfullplan [Plan]\"\t\t-\tSetzt den gesamten Vertretungsplan; erfordert eine syntaktisch anspruchsvolle Eingabe; aktuell eher schwer zu nutzen\n" +
                    "\"!printplan\"\t\t\t-\tGibt den Stundenplan zurück\n" +
                    "\"!help\" \"!commands\"\t\t-\tGibt alle möglichen Befehle zurück\n" +
                    "\"!finishplan\"\t\t\t-\tVollendet den Stundenplan\t").queue();
        }






        /** @Command !setKlasse
         *  @Usage: Sets Class of the User, can get changed everytime
         *  @Response:  a) The Class has been Saved
         *              b) invalid input, variable Class has not been changed */

        else if(contentOfMessage.toLowerCase().startsWith("!setklasse")){
            try {
                input = getInputAfterCommand(contentOfMessage, "!setklasse");
                input = removeUnwantedFromString(unwated, input);
                if (isLegitClass(input)) {
                    //Check if old-Class and new Class are identical
                    String userKlasse = jsonFunction.getKlasseFromFile((new File(defaultPath+author.getId())));
                    boolean isWeekPlanSet = jsonFunction.isWeekPlanSet((new File(defaultPath+author.getId())));
                    if(input.equalsIgnoreCase(userKlasse)){
                        channel.sendMessage("Deine Klasse wurde erfolgreich gesetzt!").queue();
                        return;
                    }else {
                        handler.changeKlasse(author.getId(), input);
                        authorProfile.setKlasse(input);
                        channel.sendMessage("Deine Klasse wurde erfolgreich gesetzt!").queue();
                        if(jsonFunction.checkUserReady(author.getId())){
                            authorProfile = jsonFunction.getUserProfileFromFileData(author.getId());
                            checkForNewEntries(authorProfile, null);
                        }

                        if(!isWeekPlanSet){
                            channel.sendMessage(thirdStep).queue();
                        }

                    }
                }
                else{
                    throw new IllegalArgumentException();
                }
            }catch(Exception E){
                channel.sendMessage("Fehler beim setzen deiner Klasse, achte darauf, dass du die Klasse korrekt eingegeben hast. Wenn du glaubst dies ist ein Fehler, schreibe @Kaipo an.").queue();
                E.printStackTrace();
            }
        }


        else if(contentOfMessage.toLowerCase().startsWith("!printklasse")){
            if(authorProfile == null){
                channel.sendMessage("Error (405), Contact @Kaipo").queue();
                return;
            }
            if (authorProfile.getKlasse() == null){
                channel.sendMessage("Du hast noch keine Klasse gesetzt. Wenn dies ein Fehler ist, schreibe bitte @Kaipo an (406)").queue();
            }
            else{
                channel.sendMessage("Du hast angegeben, du bist in der Klasse: " + authorProfile.getKlasse()).queue();
                channel.sendMessage("Wenn du dies ändern möchtest, schreibe !setklasse [klasse]").queue();
            }
        }
        /** @Command !setMode
         *  @Usage: Sets Mode of the User, can get changed everytime
         *  @Response:  a) The mode has been changed.
         *              b) invalid input, the mode will not get updated. */

        else if(contentOfMessage.toLowerCase().startsWith("!setmode")){
            input = getInputAfterCommand(contentOfMessage, "!setmode").toLowerCase();
            if(input.equals("student") || input.equals("teacher") || input.equals("lehrer") || input.equals("schüler")){
                if(input.equals("lehrer")){
                    input = "teacher";
                }
                if(input.equals("schüler")){
                    input = "student";
                }
                try {
                    handler.changeType(author.getId(), input);
                    channel.sendMessage("Dein Mode wurde gesetzt!").queue();
                    return;
                }catch(Exception E){
                    channel.sendMessage("Fehler beim setzen deines Mode's. Schreibe bitte @Kaipo an (408)").queue();
                    return;
                }
            }
            channel.sendMessage("Nicht gültige Eingabe! Akzeptierte Eingaben: schüler, lehrer bzw.  student, teacher").queue();
        }
        /** @Command !setKrz
         *  @Usage: Sets the shortcut of a Teacher Name
         *  @Response:  a) The permanent teacher shortcut has been saved.
         *              b) invalid input, the shortcut is not 3 digits long
         *              c) The User is not in the Teacher mode, which is needed for this command.
         *                  -> Users can get to teacher Mode with !setmode teacher
         *                          -> "Teachers" do not have special permission.*/

        else if (contentOfMessage.startsWith("!setkrz")){
            if(!authorProfile.getMode().equalsIgnoreCase("teacher")){
                channel.sendMessage("Du befindest dich für diesen Befehl im falschen Modus, ändere ihn mit '!setMode teacher'.").queue();
                return;
            }
            String content = getInputAfterCommand(contentOfMessage, "!setkrz");
            if(content.length()!=3){
                channel.sendMessage("Falsche eingabe. Die Eingabe muss aus 3 Buchstaben bestehen.").queue();
                return;
            }
            try {
                handler.changeEntireHourPlan(author.getId(), content, false);
                channel.sendMessage("Stundenplan gesetzt!").queue();
                if(jsonFunction.checkUserReady(author.getId())){
                    authorProfile = jsonFunction.getUserProfileFromFileData(author.getId());
                    checkForNewEntries(authorProfile, null);
                }
            }catch (Exception E){
                channel.sendMessage("Seltener Fehler. Bitte schreibe @Kaipo an (411)").queue();
            }
        }
        /** @Command !printMode
         *  @Usage: Prints current mode of User
         *  @Response:  Mode of user + Message that mode can be changed, however does not affect right in any way. */

        else if(contentOfMessage.toLowerCase().startsWith("!printmode")){
            if(authorProfile == null){
                channel.sendMessage("Error (405), Contact @Kaipo").queue();
                return;
            }
            if (authorProfile.getMode() == null){
                channel.sendMessage("Es ist ein sehr seltener Fehler aufgetreten, schreibe @Kaipo an (410)").queue();
            }
            else{
                channel.sendMessage("Du hast angegeben, dass du ein " + authorProfile.getMode() + " bist").queue();
                channel.sendMessage("Hinweis, der Modus hat keinen Einfluss auf Rechte etc. sonder nur darauf, welche Informationen betrachtet werden.\nWenn du als Schüler den Modus Lehrer aktivierst, passiert es oft, dass dir Falsche Informationen angezeigt werden.").queue();
            }
        }
        else if (contentOfMessage.toLowerCase().startsWith("!verify")) {
            String content = getInputAfterCommand(contentOfMessage, "!verify").toLowerCase();
            String userKlasse = null;
            try{
                userKlasse = jsonFunction.getKlasseFromFile(new File(defaultPath + author.getId()));
                if(jsonFunction.isUserVerified(new File(defaultPath + author.getId()))){
                    channel.sendMessage("Du bist bereits angemeldet.").queue();
                    return;
                }

        }catch(Exception E){}

            if(content.contains("vertretung:hls")){
                try {
                    handler.VerifyUser(author.getId());
                    channel.sendMessage("Dein Accounts ist nun verifiziert.\n\n").queue();
                    if(userKlasse == null){
                        channel.sendMessage(secondStep).queue();
                        return;
                    }
                    if(jsonFunction.checkUserReady(author.getId())){

                        //Loads User before
                        authorProfile = jsonFunction.getUserProfileFromFileData(author.getId());
                        checkForNewEntries(authorProfile, null);
                    }
                }catch(Exception E){
                    channel.sendMessage("Fehler beim verifizieren deines Accounts. Bitte schreibe @Kaipo an oder versuche es erneut (412)").queue();
                }
            }
            else{
                channel.sendMessage("Die Eingabe war nicht korrekt. Bitte gebe !verify [Nutzername]:[Password] ein. (Nutzer und Password von dem Vertretungsplan)").queue();
            }
        }
        else if(contentOfMessage.toLowerCase().startsWith("!finishplan")){
            try {
                handler.fillNullEntries(author.getId());
                channel.sendMessage("Dein Stundenplan wurde erfolgreich fertiggestellt. \nDu kannst ihn mit `!printplan` einsehen und mit `!add [Wochentag] [Stunde] [Lehrerkürzel]` ändern.\nWICHTIG: Du solltest für Discord PUSH-Nachrichten aktivieren. Alle Angaben ohne Gewähr.").queue();
                if(jsonFunction.checkUserReady(author.getId())){
                    //Loads User before
                    authorProfile = jsonFunction.getUserProfileFromFileData(author.getId());
                    System.out.println("Klasse: " + authorProfile.getKlasse());
                    System.out.println("Mo 1/2 Stunden: " + authorProfile.getWEEK_PLAN().getDayPlanAtDay(0).getTeacherAtHour(0));
                    System.out.println("Mode: " + authorProfile.getMode());
                    checkForNewEntries(authorProfile, null);
                }
            }catch(Exception E){
                E.printStackTrace();
                channel.sendMessage("Fehler beim fertigstellen des Stundeplanes, warte kurz und probier es dann erneut (421)").queue();
                return;
            }
        }
        /** @Command !add
         *  @Usage: Sets the a Entry in the "Stundenplan"
         *  @Response:  a) The Stundenplan will be updated
         *              b) The Stundenplan will not be safed due to a Syntax error */

        else if(contentOfMessage.startsWith("!add")){
            /*
                Syntax:
                        - !add [Tag] [Stunde(n)] [LehrerKürzel]

                Mögliche Eingaben sollen sein:
                        -   !add Mo 1 ABC
                        -   !add Di 1/2 ABC
                        -   !add Mi 1+2 ABC
                        -   !add Mi  1_2 ABC

                Regeln für die drei Eingaben:
                        -   Tag: Eingaben müssen {Mo, Di, Mi, Mo, Fr} sein, Kein Zahlen keine Leerzeichen keine Sonderzeichen
                        -   Stunde(n): Keine Buchstaben, nur: {0-9, '/', '+', '_'}  Länge max 3, Maximale Stunde = 9.
                        -   Kürzel: Drei Buchstaben, keine Zahlen keine Sonderzeichen

                Schritte:
                    1. Tabulatorabstände mit Space's austauschen
                    2. Mehrere Spaces durch ein Space ersetzen
                    3. ggf. Space am Ende der Eingabe entfernen
             */
            input = getInputAfterCommand(contentOfMessage, "!add");
            String[] sortedInput;
            try {
                sortedInput = solveUserInput(input);
            }catch(IllegalArgumentException E){
                channel.sendMessage(E.getMessage()).queue();
                return;
            }
            try {
                //Add Support for Instant relevant Entries Here!
                handler.changeSingleEntry(author.getId(), sortedInput);
                channel.sendMessage("Eintrag erfolgreich gesetzt.").queue();
                boolean isWeekPlanComplete = jsonFunction.isWeekPlanSet(new File(defaultPath+author.getId()));
                if(!isWeekPlanComplete){
                    channel.sendMessage(fourthStep).queue();
                }
                if(jsonFunction.checkUserReady(author.getId())){
                    //Updates User Profile before checking for new Entries
                    authorProfile = jsonFunction.getUserProfileFromFileData(author.getId());
                    checkForNewEntries(authorProfile, sortedInput);
                }

            }catch(Exception E){
                channel.sendMessage("Fehler beim setzen dieser Info. Schreibe bitte @Kaipo an. (412)").queue();
                E.printStackTrace();
            }
        }

        /** @Command !setFullPlan
         *  @Usage: Sets the entire "Stundenplan" for the User
         *  @Response:  a) The Stundenplan will be set and saved in the file.
         *              b) The Stundenplan will not be safed due to a Syntax error.
         *              */
        else if(contentOfMessage.startsWith("!setfullplan")){
            try {
                input = getInputAfterCommand(contentOfMessage, "!setfullplan");
                handler.changeEntireHourPlan(author.getId(), input, true);
                channel.sendMessage("Dein Stundenplan wurde gesetzt!").queue();
            }catch(Exception E){
                channel.sendMessage("crashed").queue();
                E.printStackTrace();
            }

        }
        /** @Command: !printPlan
         *  @Usage: Prints the Plan of the User.
         *  @Response: a) Prints the Plan of the User for each Day 5x
         *             b) Prints error message that plan isn't set yet. */

        else if(contentOfMessage.toLowerCase().startsWith("!printplan")){
            if (authorProfile == null){
                channel.sendMessage("Error! Message @Kaipo. (407)").queue();
                channel.sendMessage("This error might not happen again in 60 seconds!").queue();
                return;
            }
            if(authorProfile.getWEEK_PLAN() == null){
                channel.sendMessage("Du hast noch keinen Stundenplan angegeben! \nMit !setstunde [Wochentag] [Stunde] [Fach als Kürzel] [Lehrer als Kürzel] kannst du eine einzelne Stunde setzen \nMit !setfullplan [Plan] kannst du einen ganzen Plan setzen").queue();
                channel.sendMessage("Bitte beachte, dass !setfullplan eine genaue Syntax voraussetzt.").queue();
            }
            else{
                String prettyString = "";
                String [] weekDays = {"Mo", "Di", "Mi", "Do", "Fr"};
                for(int u = 0; u<5; u++){
                    prettyString = prettyString + "\t \t" +  weekDays[u];
                }
                prettyString = prettyString + "\n";
                for(int i = 0; i<authorProfile.getWEEK_PLAN().getDayPlanAtDay(0).getLength(); i++){


                  //  for(int j = 0; j<5; j++){
                    //    prettyString = prettyString + authorProfile.getWEEK_PLAN().getDayPlanAtDay(j).getSubjectAtHour(i) + "\t\t";
                   // }
                    //prettyString = prettyString + "\n  ";
                    for(int k = 0; k<5; k++){
                        prettyString = prettyString + authorProfile.getWEEK_PLAN().getDayPlanAtDay(k).getTeacherAtHour(i) + "\t\t";
                    }
                    prettyString = prettyString + "\n" + "\n";
                }
                channel.sendMessage(prettyString).queue();
            }
        }
        /** @Command: -Command not Found- /Any Comment not listed above
         *  @Usage: Tells the User the command entered does not exist.
         *  @Response: "The Command [Command] does not exist, type "[helpCommand]" for help" */
        else {
            printErrorCommandNotFound(channel,contentOfMessage);
        }
    }
    private boolean MessageInlucdesArray(String contentOfMessage, String[]possibleContent){
        for(int i = 0; i<possibleContent.length; i++){
            if(contentOfMessage.equalsIgnoreCase(possibleContent[i].toLowerCase())){
                return true;
            }
        }
        return false;
    }
    public void printErrorCommandNotFound(PrivateChannel userChannel, String contentOfMessage){
        userChannel.sendMessage("Der Befehl '" + contentOfMessage + "' ist mir unbekannt. Schreibe !Commands für alle Befehle").queue();
    }
    private void reactToWelcomeMessage(PrivateChannel channel){
        channel.sendMessage("Willkommen").queue();
    }
    /**
     * @Function: onGuildMemberJoin
     * @param event
     * @Time Of Execution: When a User joins Discord.
     */

    public void onGuildMemberJoin(GuildMemberJoinEvent event){ //Setup
        try {

            //Creating new User to File System with Default values
            Json_userJoinedHandler newUser = new Json_userJoinedHandler(defaultPath);
            newUser.createNewUserOnSignUP(event.getUser().getId());

            //If Exception happens (Chance <0.001%) print Fatal Error, ?maybe message Admin?
        }catch(Exception E) {
        }
        //Posting welcome message
        postWelcomeMessage(event.getUser());
    }
    private void postWelcomeMessage(User joinedUser){
        //Open privateChannel with user and send the setupMessage
        joinedUser.openPrivateChannel().queue((channel) ->
        {
            channel.sendMessage(firstStep).queue();
        });
    }
    private String[] solveUserInput(String rawInput){
        String[] information = new String[3];

        //Removes Tab's
        rawInput = rawInput.replaceAll("\t", " ");

        //Reduces Spaces to just one between each command
        while(rawInput.contains("  ")){
            rawInput = rawInput.replaceAll("  ", " ");
        }

        //removes space at end
        if(rawInput.charAt(rawInput.length()-1) == ' '){
            rawInput = rawInput.substring(0, rawInput.length()-1);
        }

        int count = 0;
        for(int i = 0; i<rawInput.length(); i++){
            if(rawInput.charAt(i) == ' '){
                count++;
            }
        }

        //If there too many or not enough spaces, the function will return an error.
        if(count != 2){
            throw new IllegalArgumentException("Eingabe nicht korrekt");
        }

        //Seperats entire line into 3 categories
        String currentInput = "";
        int inputNumber = 0;
        for(int i = 0; i<rawInput.length(); i++){
            if(rawInput.charAt(i) == ' '){
                information[inputNumber] = currentInput;
                currentInput = "";
                inputNumber++;
            }else{
                currentInput = currentInput + rawInput.charAt(i);
            }
        }
        information[inputNumber] = currentInput;

        //ABS1, Day:
        if(!weekDaysIntoNumbers.containsKey(information[0].toLowerCase())){
            throw new IllegalArgumentException("Ungültiger Wochentag");
        }

        //Changes into Number for futher processing
        information[0] = String.valueOf(weekDaysIntoNumbers.get(information[0]));

        //ABS2, Hour,
        if(information[1].length() == 1){
            if(!Character.isDigit(information[1].charAt(0))){
                throw new IllegalArgumentException("Ungültige Stundenangabe");
            }
        }else if(information[1].length() == 3){
            if(!Character.isDigit(information[1].charAt(0)) || !Character.isDigit(information[1].charAt(2))){
                throw new IllegalArgumentException("Ungültige Stundenangabe");
            }
            if(Character.isDigit(information[1].charAt(1)) || Character.isAlphabetic(information[1].charAt(1))){
                throw new IllegalArgumentException("Ungültige Stundenangabe");
            }
        }

        //ABS3, Teacher

        for(int i = 0; i<information[2].length(); i++){
            if(!Character.isAlphabetic(information[2].charAt(i))){
                throw new IllegalArgumentException("Ungültige Kürzelangabe");
            }
        }
        return information;
    }

    /**
     * Function: getInputAfterCommand
     * @param inputString {Entire user Input}
     * @return String without command before it
     */

    private String getInputAfterCommand(String inputString, String command){
        command = command + " ";
        inputString = inputString.replaceAll("\\[", " ");
        inputString = inputString.replaceAll("]", " ");
        return inputString.replaceAll(command, "");
    }

    /**
     * Function: removeUnwantedFromString
     * @param unwanted  {All unwanted Chars}
     * @param input {InputString}
     * @return String wihout unwanted Chars
     */
    private String removeUnwantedFromString(char[]unwanted, String input){
        for(int i = 0; i<unwanted.length; i++){
            input = input.replace(unwanted[i], '\0');
        }
        return input;
    }
    private userProfile getUserProfileFromList(ArrayList<userProfile> userList, String discordID){
        for(int i = 0; i<userList.size(); i++){
            if(userList.get(i).getDISCORD_ID().equals(discordID)){
                return userList.get(i);
            }
        }
        return null;
    }




    private boolean isLegitClass(String klasse){
        if(klasse.length()>3){
            return false;
        }
        for(int i = 0; i<classNumbers.length; i++){
            if(klasse.toLowerCase().contains(classNumbers[i].toLowerCase())){
                return true;
            }
        }
        return false;
    }
    private void checkForNewEntries(userProfile authorProfile, String[] data)throws Exception{
        System.out.println("Before");
        ArrayList<Row> newEntries = relevantChecker.checkForRelevantChanges(authorProfile, launcherObject.neuerPlan);
        System.out.println("NewEntries Size: " + newEntries.size());
        for (int i = 0; i<newEntries.size(); i++) {
            System.out.println("into For...");
            if(data == null) {
                System.out.println("Data == null");
                bot.alertUser(newEntries, authorProfile, "Eintrag gefunden");
                return;
            }
            else{
                //Checks if changes made by User are relevant for new Changes, if not, no messages are send out!
                if(newEntries.get(i).getDate().getHours()[0] == Integer.parseInt(data[1].substring(0,1)) && newEntries.get(i).getDate().getCalenderDay() == Integer.parseInt(data[0])){
                    bot.alertUser(newEntries, authorProfile, "Eintrag gefunden");
                }
            }
        }
    }




}
