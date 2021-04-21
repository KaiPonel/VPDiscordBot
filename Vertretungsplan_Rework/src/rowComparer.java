import java.util.ArrayList;

public class rowComparer extends Row {
    /** Class: rowComparer
     *  Goal: Comparer new and old Rows, if there are new Rows, return them
     *  Class Finished (26/02)
     */
    private ArrayList<Row> oldPlan;
    private ArrayList<Row> neuerPlan;
    public rowComparer(ArrayList<Row> oldPlan, ArrayList<Row> neuerPlan){
        this.oldPlan = oldPlan;
        this.neuerPlan = neuerPlan;
}

    /**
     * Checks for added Rows to Plan.
     */


    public ArrayList<Row> comparePlansAddedEntries(){
        ArrayList<Row> newRowsToPlan = new ArrayList<>();
        boolean matchFound = false;
        for(int i = 0; i<neuerPlan.size(); i++){
            for(int j = 0; j<oldPlan.size(); j++){
                if(rowsEquals(neuerPlan.get(i), oldPlan.get(j))){                                                        //Obj1.equals(Obj2) does not Work with CollectionOfRows -> Rows! Therefore:
                    matchFound = true;
                }
            }
            if(!matchFound){
                newRowsToPlan.add(neuerPlan.get(i));
            }
            matchFound = false;
        }
        return newRowsToPlan;
    }

    /*
     * Checks for removed Rows from Plan.
     */


    public ArrayList<Row> comparePlansRemovedEntires(){
        ArrayList<Row> removedRowsFromPlan = new ArrayList<>();
        boolean matchFound = false;
        for(int i = 0; i<oldPlan.size(); i++){
            for(int j = 0; j<neuerPlan.size(); j++){
                if(rowsEquals(oldPlan.get(i), neuerPlan.get(j))){                                                        //Obj1.equals(Obj2) does not Work with CollectionOfRows -> Rows! Therefore:
                    matchFound = true;
                }
            }
            if(!matchFound){
                removedRowsFromPlan.add(oldPlan.get(i));
            }
            matchFound = false;
        }
        return removedRowsFromPlan;
    }
    private boolean rowsEquals(Row alpha, Row beta){
        if (klassenAsString(alpha.getKlassen()).equalsIgnoreCase(klassenAsString(beta.getKlassen())) && alpha.getOldTeacher().equalsIgnoreCase(beta.getOldTeacher()) && alpha.getDate().getHours()[0] == beta.getDate().getHours()[0] && alpha.getKindOf().equalsIgnoreCase(beta.getKindOf())){
            return true;
        }
        return false;
    }
    private String klassenAsString(ArrayList <String> klassen){
        String classes = "";
        for(int i = 0; i<klassen.size(); i++){
            classes = classes + klassen.get(i) + " ";
        }
        return classes;
    }
}