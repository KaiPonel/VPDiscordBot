import java.util.*;
public class xmlSorter {
    //### Klassen-Namen wurden entfernt.
    private String[] ClassNumbers = {"Placeholder"};
    List<String[]> listOfCases = new ArrayList<String[]>();
    public List<String[]> executeSorter(List<String> xmlPages){
        /** Class xmlSorter:
         *  Goal: get the HTML Texts and return the entries of the Plan
         *  Class Finished. (26/02)
         */

        String pageOne = xmlPages.get(0);
        String pageTwo = xmlPages.get(1);
        String[] pageOne_lines = pageOne.split("\\n");
        String[] pageTwo_lines = pageTwo.split("\\n");
        pageOne_lines = removeSpecialLetters(pageOne_lines);
        pageOne_lines = removeWrongStart(pageOne_lines);
        pageTwo_lines = removeSpecialLetters(pageTwo_lines);
        pageTwo_lines = removeWrongStart(pageTwo_lines);
        getMetaInformationfromText(pageOne_lines);
        getMetaInformationfromText(pageTwo_lines);
        return listOfCases;
    }
    private String[] removeSpecialLetters(String[] pageLines){
        for(int i = 0; i<pageLines.length; i++){                // if Brackets are removed aswell, then "Freisetzungen" will be shown!
            pageLines[i] = pageLines[i].replace(',', '\0');
            pageLines[i] = pageLines[i].replace('(', '\0');     //Experimental
            pageLines[i] = pageLines[i].replace(')', '\0');
        }
        return pageLines;
    }

    private boolean startsWithClass(String line){
        String currentInput = "";
        for(int i = 0; i<line.length(); i++){
            if(line.charAt(i) == ' ' || line.charAt(i) == '\0' || line.charAt(i) == '\t'){
                i = line.length();
            }
            else {
                currentInput = currentInput + line.charAt(i);
            }
        }
        for(int k = 0; k<ClassNumbers.length; k++){
            if (ClassNumbers[k].equalsIgnoreCase(currentInput)){
                return true;
            }
        }
        return false;
    }

    private List<String[]> getMetaInformationfromText(String [] text){
        for(int i = 0; i<text.length; i++){
            if(startsWithClass(text[i])){
                listOfCases.add(getAllSubInfo(text[i]));
            }
        }
        return listOfCases;
    }

    private String[] removeWrongStart(String[]lines){
        for(int i = 0; i<lines.length; i++){
            try {
                if (lines[i].charAt(0) == ' ' || lines[i].charAt(0) == '\0' || lines[i].charAt(0) == '\t' || lines[i].charAt(0) == '\n') {
                    lines[i] = lines[i].substring(1, lines[i].length());
                }
            }catch(Exception E){
            }
        }
        return lines;
    }

    private String[]getAllSubInfo(String line){
        String currentContext = "";
        String[] results = new String[8];
        int Counter = 0;
        for(int i = 0; i< line.length() && Counter <8; i++){
            if(line.charAt(i) == '\t'){
                results[Counter] = currentContext;
                currentContext = "";
                Counter++;
            }
            else{
                currentContext = currentContext + line.charAt(i);
            }

        }
        return results;
    }

}
