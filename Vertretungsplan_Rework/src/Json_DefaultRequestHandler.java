import com.google.gson.JsonArray;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;

public class Json_DefaultRequestHandler {

    private String defaultPath;
    private Json_HelpFunctions function = new Json_HelpFunctions(defaultPath);
    private File userFile;

    public Json_DefaultRequestHandler(String defaultPath){
        this.defaultPath = defaultPath;
    }

    /**
     * Function: changeKlasse
     * @param FileID - Id Of File
     * @param inputKlasse - Klasse of User
     * @throws Exception
     *
     *
     * Notice: It is expected that the Class is checked before the input here, and that it contains correct syntax.
     */

    public void changeKlasse(String FileID, String inputKlasse)throws Exception{
        userFile = new File(defaultPath+FileID);
        //If the User (for whatever Reason) does not have a file, it will create a new One
        if(!function.isFileExisting(userFile)){
            Json_userJoinedHandler newUser = new Json_userJoinedHandler(defaultPath);
            newUser.createNewUserOnSignUP(FileID);
        }
        JSONObject userObject = function.loadJsonObject(userFile);
        //Got the current data of the User
        //Replace the Value
        userObject.replace("KLASSE", inputKlasse);
        //Write it down
        function.writeToJson(userFile, false,userObject);

        //Check if User is ready
       function.checkUserReady(FileID);
    }
    public void changeType(String FileID, String inputType)throws Exception{
        userFile = new File(defaultPath+FileID);
        //Check if user Does exists (to avoid any crash)
        if(!function.isFileExisting(userFile)){
            Json_userJoinedHandler newUser = new Json_userJoinedHandler(defaultPath);
            newUser.createNewUserOnSignUP(FileID);
        }

        //Get User Data
        JSONObject userObject = function.loadJsonObject(userFile);
        //Get Var Type and Update it
        userObject.replace("TYPE", inputType);
        //Update to Json
        function.writeToJson(userFile, false, userObject);


        //No Ready Check needed, User is ready Anyway (or not, but does not depend on Mode (Default = student).
    }
    public void VerifyUser(String FileID)throws Exception{
        userFile = new File(defaultPath+FileID);
        //Check if user Does exists (to avoid any crash)
        if(!function.isFileExisting(userFile)){
            Json_userJoinedHandler newUser = new Json_userJoinedHandler(defaultPath);
            newUser.createNewUserOnSignUP(FileID);
        }
        JSONObject userObject = function.loadJsonObject(userFile);
        //Get Var Type and Update it
        userObject.replace("VERIFIED", "TRUE");
        function.checkUserReady(FileID);
        function.writeToJson(userFile, false, userObject);
    }









    public void changeSingleEntry(String FileId, String[] input)throws Exception{
        /*  input:
            0: Day 0-4
            1: Hour 0-8
            2: Teacher
         */



        userFile = new File(defaultPath+FileId);

        //If the User (for whatever Reason) does not have a file, it will create a new One
        if(!function.isFileExisting(userFile)){
            Json_userJoinedHandler newUser = new Json_userJoinedHandler(defaultPath);
            newUser.createNewUserOnSignUP(FileId);
        }
        JSONObject userObject = function.loadJsonObject(userFile);
        //5 days a Week

        //Get The weekPlan
        JSONObject WeekPlanArray = (JSONObject) userObject.get("WeekPlan");
        JSONObject dayPlan = (JSONObject) WeekPlanArray.get("DAY" + (Integer.parseInt(input[0])+1));
        JSONObject dayInfo = (JSONObject) dayPlan.get("HOUR" + (Integer.parseInt(input[1].substring(0,1))));

        //If User has multiple hours set
        if(input[1].length() == 3){
            JSONObject dayInfo_2 = (JSONObject) dayPlan.get("HOUR" + (Integer.parseInt(input[1].substring(2,3))));
            dayInfo_2.replace("TEACHER", input[2]);
            dayPlan.put("HOUR" + (Integer.parseInt(input[1].substring(2,3))), dayInfo);
        }



        //Set Var's
        dayInfo.replace("TEACHER", input[2]);

        //Safe and update vars.
        dayPlan.put("HOUR" + (Integer.parseInt(input[1].substring(0,1))), dayInfo);            //Stunde muss nicht um +1 erhöht werden!
        WeekPlanArray.put("DAY" + (Integer.parseInt(input[0])+1), dayPlan);
        userObject.put("WeekPlan", WeekPlanArray);

        //Write to file
        function.writeToJson(userFile, false, userObject);
    }





    public void changeEntireHourPlan(String FileId, String inputString, boolean normal) throws Exception{
            userFile = new File(defaultPath+FileId);

            //If the User (for whatever Reason) does not have a file, it will create a new One
            if(!function.isFileExisting(userFile)){
              Json_userJoinedHandler newUser = new Json_userJoinedHandler(defaultPath);
               newUser.createNewUserOnSignUP(FileId);
              }
            WeekPlan tempPlan;
            //Can throw IllegalArgumentException, will get caught by EventListener
            if(normal) {
                tempPlan = new WeekPlan().createWeekPlanByDataString(inputString);
            }else{
                tempPlan = new WeekPlan().createWeekPlanTeacherOnly(inputString);
            }


            JSONObject userObject = function.loadJsonObject(userFile);
            //5 days a Week

            //Get The weekPlan
            JSONObject WeekPlanArray = (JSONObject) userObject.get("WeekPlan");

            for(int i = 0; i<5; i++){
                //Get The Day
                JSONObject dayPlan = (JSONObject) WeekPlanArray.get("DAY" + (i+1));

                JSONObject dayInfo;
                for(int j = 0; j<tempPlan.getDayPlanAtDay(i).getLength(); j++){

                    //Get The HourEntry
                    dayInfo = (JSONObject) dayPlan.get("HOUR" + (j+1));

                    //Sets Subject and Teacher
                    dayInfo.replace("SUBJECT", tempPlan.getDayPlanAtDay(i).getSubjectAtHour(j));
                    dayInfo.replace("TEACHER", tempPlan.getDayPlanAtDay(i).getTeacherAtHour(j));

                    //Sets it to dayPlans
                    dayPlan.put("HOUR" + (j+1), dayInfo);
                }
                //Sets it to WeekPlan
                WeekPlanArray.put("DAY" + (i+1), dayPlan);
            }
            //Sets it to the UserProfile (Replace)
            userObject.put("WeekPlan", WeekPlanArray);
            userObject.replace("SET_WEEK_PLAN", "TRUE");
            //Writes it down
            function.writeToJson(userFile, false, userObject);

          //check if User is ready
            function.checkUserReady(FileId);
    }
    public void fillNullEntries(String FileId)throws Exception{
        userFile = new File(defaultPath+FileId);

        //If the User (for whatever Reason) does not have a file, it will create a new One
        if(!function.isFileExisting(userFile)){
            Json_userJoinedHandler newUser = new Json_userJoinedHandler(defaultPath);
            newUser.createNewUserOnSignUP(FileId);
        }
        JSONObject userObject = function.loadJsonObject(userFile);
        //5 days a Week

        //Get The weekPlan
        JSONObject WeekPlanArray = (JSONObject) userObject.get("WeekPlan");

        for(int i = 0; i<5; i++){
            //Get The Day
            JSONObject dayPlan = (JSONObject) WeekPlanArray.get("DAY" + (i+1));

            JSONObject dayInfo;
            for(int j = 0; j<9; j++){
                //Get The HourEntry
                dayInfo = (JSONObject) dayPlan.get("HOUR" + (j+1));
                //Sets Subject and Teacher

                if(dayInfo.get("SUBJECT").toString().equalsIgnoreCase("null")){
                    dayInfo.replace("SUBJECT", "-----");
                }

                if(dayInfo.get("TEACHER").toString().equalsIgnoreCase("null")){
                    dayInfo.replace("TEACHER", "-----");
                }

                //Sets it to dayPlans
                dayPlan.put("HOUR" + (j+1), dayInfo);
            }
            //Sets it to WeekPlan
            WeekPlanArray.put("DAY" + (i+1), dayPlan);
        }
        //Sets it to the UserProfile (Replace)
        userObject.put("WeekPlan", WeekPlanArray);
        userObject.replace("SET_WEEK_PLAN", "TRUE");
        //Writes it down
        function.writeToJson(userFile, false, userObject);

        //check if User is ready
        function.checkUserReady(FileId);
    }
    public void addLogEntry(String logFileName, Row entry, userProfile user, String addedOrRemoved, String date)throws Exception{

        String hash = String.valueOf((int) (Math.random() * 10000000));

        File logFile = new File(defaultPath+logFileName);

        JSONObject logObject = (JSONObject) function.loadJsonObject(logFile);

        //Gets Messages-Sent-Log from Entire LogFile
       JSONObject sentMessagesObject = (JSONObject) logObject.get("SENT_NOTIFICATION_MESSAGES");

       //Creates Entry
       sentMessagesObject.put(user.getDISCORD_ID() + "_" + hash, "[" + date + "] " +  addedOrRemoved + "   " + entry.getDate().getDateAsString() + "   " + entry.getKlassen() + "   " + entry.getOldTeacher());

       //Saves Object
       logObject.put("SENT_NOTIFICATION_MESSAGES", sentMessagesObject);


       function.writeToJson(logFile, false, logObject);


    }









}
