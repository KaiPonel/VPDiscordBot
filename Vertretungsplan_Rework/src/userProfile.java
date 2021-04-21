import net.dv8tion.jda.core.entities.User;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class userProfile {
    private String DISCORD_ID;
    private User discordUser;
    private WeekPlan WEEK_PLAN;
    private String klasse;
    private String mode;

    public void setKlasse(String klasse){this.klasse = klasse; }
    public void setDISCORD_ID(String DISCORD_ID){this.DISCORD_ID = DISCORD_ID; }
    public void setWEEK_PLAN(WeekPlan Weekplan){ this.WEEK_PLAN = Weekplan; }
    public void setDiscordUser (User discordUser){ this.discordUser = discordUser; }
    public void setMode(String mode){ this.mode = mode; }

    public String getMode(){ return this.mode; }
    public String getKlasse(){ return klasse; }
    public String getDISCORD_ID(){ return this.DISCORD_ID; }
    public String getSubjectAtDayAndHour(int day, int hour){ return this.WEEK_PLAN.getDayPlanAtDay(day).getSubjectAtHour(hour); }
    public String getTeacherAtDayAndHour(int day, int hour){ return this.WEEK_PLAN.getDayPlanAtDay(day).getTeacherAtHour(hour); }
    public WeekPlan getWEEK_PLAN(){return this.WEEK_PLAN; }
}