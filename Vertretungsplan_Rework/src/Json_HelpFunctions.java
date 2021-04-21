import com.gargoylesoftware.htmlunit.javascript.host.canvas.ext.EXT_texture_filter_anisotropic;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;

public class Json_HelpFunctions {
    private FileWriter writeJson;
    private String defaultPath;

    public Json_HelpFunctions(String defaultPath){
        this.defaultPath = defaultPath;
    }

    public boolean isFileExisting(File userFile){

        //If File already Exists
        if(userFile.exists() && !userFile.isDirectory()){
            return true;
        }

        //If File does not exist -> Return false
        return false;
    }
    public JSONObject loadJsonObject (File userFile) throws  Exception{
        JSONParser parse = new JSONParser();
        JSONObject fileContent = (JSONObject) parse.parse(new InputStreamReader(new FileInputStream(userFile)));
        parse.reset();
        return fileContent;
    }

    public void writeToJson(File userFile,boolean append, JSONObject content) throws Exception{
        writeJson = new FileWriter(userFile,append);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String printString = gson.toJson(content);
          writeJson.append(printString);
        writeJson.close();
    }

    public boolean checkUserReady(String DiscordID)throws Exception{
        File userFile = new File(defaultPath + DiscordID);
        if(!isFileExisting(userFile)){
            Json_userJoinedHandler newUser = new Json_userJoinedHandler(defaultPath);
            newUser.createNewUserOnSignUP(DiscordID);
        }

        JSONObject userObject = (JSONObject) loadJsonObject(userFile);
        String weekPlanObject = (String) userObject.get("SET_WEEK_PLAN");
        String mode = (String) userObject.get("TYPE");
        String klasseObject = (String) userObject.get("KLASSE");
        String verifiedObject = (String) userObject.get("VERIFIED");
        /*  IF user is in "student" Mode, Klasse will be checked aswell */
        if(mode.equalsIgnoreCase("student")) {
            if (weekPlanObject.equalsIgnoreCase("TRUE") && !klasseObject.equalsIgnoreCase("null") && verifiedObject.equalsIgnoreCase("TRUE")) {
                userObject.replace("READYUSER", "TRUE");
                writeToJson(userFile, false, userObject);
                return true;
            }
        }
        /*  IF user is in "teacher" Mode, Klasse will NOT be checked aswell */
        else{
            if(weekPlanObject.equalsIgnoreCase("TRUE")&& verifiedObject.equalsIgnoreCase("TRUE")){
                userObject.replace("READYUSER", "TRUE");
                userObject.replace("KLASSE,", "undefined");
                writeToJson(userFile, false, userObject);
                return true;
            }
        }
        return false;
    }

    public userProfile getUserProfileFromFileData(String Discord_ID)throws Exception{
        File userFile = new File(defaultPath+Discord_ID);
        userProfile tempProfile = new userProfile();
        tempProfile.setDISCORD_ID(Discord_ID);
        tempProfile.setWEEK_PLAN(getWeekPlanFromFile(userFile));
        tempProfile.setKlasse(getKlasseFromFile(userFile));
        tempProfile.setMode(getModeFromFile(userFile));
        return tempProfile;


    }
    public WeekPlan getWeekPlanFromFile(File userFile)throws Exception{
        WeekPlan tempPlan = new WeekPlan();
        JSONObject userObject = (JSONObject) loadJsonObject(userFile);
        String weekPlanSetObject = (String) userObject.get("SET_WEEK_PLAN");

        if(weekPlanSetObject.equalsIgnoreCase("FALSE")){
            return null;
        }

        JSONObject weekPlanObject = (JSONObject) userObject.get("WeekPlan");
        for(int i = 0; i<5; i++){
            JSONObject dayObject = (JSONObject) weekPlanObject.get("DAY" + (i+1));
            for(int j = 0; j<9; j++){
                JSONObject hourObject = (JSONObject) dayObject.get("HOUR" + (j+1));
                String teacherObject = (String) hourObject.get("TEACHER");
                String subjectObject = (String) hourObject.get("SUBJECT");
                tempPlan.getDayPlanAtDay(i).setSubjectAtHour(subjectObject, j);
                tempPlan.getDayPlanAtDay(i).setTeacherAtHour(teacherObject, j);
            }
        }
        return tempPlan;
    }
    public String getKlasseFromFile(File userFile)throws Exception {
        JSONObject userObject = (JSONObject) loadJsonObject(userFile);
        String KlasseObject = (String) userObject.get("KLASSE");
        if(KlasseObject.equalsIgnoreCase("null")){
            return null;
        }
        return KlasseObject.toString();
    }
    private String getModeFromFile(File userFile)throws Exception{
        JSONObject userObject = (JSONObject) loadJsonObject(userFile);
        String TypeObject = (String) userObject.get("TYPE");
        return TypeObject;
    }
    public boolean isWeekPlanSet(File userFile)throws Exception{
        JSONObject userObject = (JSONObject) loadJsonObject(userFile);
        String weekPlanBooleanObject = (String) userObject.get("SET_WEEK_PLAN");
        if(weekPlanBooleanObject.equalsIgnoreCase("TRUE")){
            return true;
        }else{
            return false;
        }
    }
        public boolean isUserVerified(File userFile)throws Exception{
            JSONObject userObject = (JSONObject) loadJsonObject(userFile);
            String verifiedObject = (String) userObject.get("VERIFIED");
            if(verifiedObject.equalsIgnoreCase("TRUE")){
                return true;
            }else{
                return false;
            }


        }
}
