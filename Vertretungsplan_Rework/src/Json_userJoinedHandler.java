import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.IOException;

public class Json_userJoinedHandler {
    private String defaultPath;
    private Json_HelpFunctions functions = new Json_HelpFunctions(defaultPath);
    private JSONObject userData;
    private File userFile;
    public Json_userJoinedHandler(String defaultPath){
        this.defaultPath = defaultPath;
    }


    public void createNewUserOnSignUP(String DISCORDID) throws Exception {

        //Individual User File:
        Thread.sleep(500);
        userFile = new File(defaultPath + DISCORDID);

        //If the File does already Exist, it will return at this Point
        //Explination: If the file already exists, the user prob. rejoined
        if (functions.isFileExisting(userFile)) {
            return;
        }

        //If it does not Exist, it will create a new One.
        userFile.createNewFile();

        userData = new JSONObject();

        //With the info we got on SignUp!
        //DiscordID

        userData.put("DISCORD_ID", DISCORDID);


        //Klasse (value of Attribute gets set to null, since its unclear on registration:
        userData.put("KLASSE", "null");

        userData.put("TYPE", "student");

        userData.put("VERIFIED", "FALSE");
        //USER READY
        userData.put("READYUSER", "FALSE");


        //USER WEEKPLAN SET
        userData.put("SET_WEEK_PLAN", "FALSE");

        //WeekPlan
        JSONObject WeekPlan = new JSONObject();

        //Weekplan contains 5 dayPlans
        JSONObject dayPlan;

        for (int i = 0; i <5; i++) {
            //Int. DayPlans in the for loop
            dayPlan = new JSONObject();

            //Every Dayplan contains 9 Subjects and Teachers

            JSONObject DayInfo;
            //Those Objects get Int. in another For Loop
            for (int j = 0; j <9; j++) {
                DayInfo = new JSONObject();
                //Subject
                DayInfo.put("SUBJECT", "null");

                //Teacher:
                DayInfo.put("TEACHER", "null");

                //Adding the Subjects and Teachers to the DayPlans

                dayPlan.put("HOUR" + (j+1), DayInfo);
            }

            //Adding the DayPlans to the WeekPlan
            WeekPlan.put("DAY" + (i+1), dayPlan);
        }

        userData.put("WeekPlan",WeekPlan);
        writeToFile(userFile, userData);
    }

    private void writeToFile(File file, JSONObject data)throws Exception{
        //Writes it to the file, no Check for previous Entries needed, since the user is new.
        functions.writeToJson(file, false, data);
    }



    public void createLogFile(String name)throws Exception {
         File logFile = new File(defaultPath+name);
            //If LogFile already Exist ->> Return;
         if(functions.isFileExisting(logFile)){
             return;
         }

            //Creates new File
         logFile.createNewFile();

            JSONObject syntax = new JSONObject();

            syntax.put("AUTHOR", "Kai Ponel");

            //Puts Categories without content
            syntax.put("SENT_NOTIFICATION_MESSAGES", new JSONObject());

            //...Add more here if needed.


        //Writes to file
        writeToFile(logFile, syntax);
    }

}
