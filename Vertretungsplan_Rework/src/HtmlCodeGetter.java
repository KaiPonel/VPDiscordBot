import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;
import java.util.*;
public class HtmlCodeGetter {                                       //Class Finalized, No futher Work intended - 26/02!
    // ### Die Adresse des Vertretungsplans wurde unkenntlich gemacht.
    private  String base = "Placeholder";
    public  List<String> getRawTexts() throws Exception{

        CookieManager cookieManager = new CookieManager();
        cookieManager.clearCookies();
        Calendar calendar = new GregorianCalendar();
        WebClient webClient = new WebClient();
        webClient.getOptions().setUseInsecureSSL(true);
        webClient.setJavaScriptTimeout(15000);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        DefaultCredentialsProvider autoLogin = (DefaultCredentialsProvider) webClient.getCredentialsProvider();
        // ### Die Login Daten für den Vertretungsplan wurden entfernt
        autoLogin.addCredentials("Placeholder", "Placeholder");
        String calenderWeek = String.valueOf(calendar.get(Calendar.WEEK_OF_YEAR));
        String nextCalenderWeek = String.valueOf(calendar.get(Calendar.WEEK_OF_YEAR) +1);
        calenderWeek = fixCalenderDate(calenderWeek);
        nextCalenderWeek = fixCalenderDate(nextCalenderWeek);
        HtmlPage currentWeekPage = webClient.getPage(base + calenderWeek + "/w/w00000.htm" );
        HtmlPage nextWeekPage = webClient.getPage(base + nextCalenderWeek + "/w/w00000.htm");
        ArrayList<String> listOfContent = new ArrayList<String>();
        listOfContent.add(currentWeekPage.asText());
        listOfContent.add(nextWeekPage.asText());
        return listOfContent;
    }
    private String fixCalenderDate(String date){
        if(date.length() == 1){
            date = "0" + date;
        }
        return date;
    }
}
