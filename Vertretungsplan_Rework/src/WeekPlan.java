public class WeekPlan {
    DayPlan[] DayOfSubjects;


    public WeekPlan() {
        DayOfSubjects = new DayPlan[5];
        for (int i = 0; i < DayOfSubjects.length; i++) {
            DayOfSubjects[i] = new DayPlan();
        }
    }

    public void setDayPlanAtDay(DayPlan plan, int day){
        this.DayOfSubjects[day] = plan;
    }
    public DayPlan getDayPlanAtDay(int day){
        return this.DayOfSubjects[day];
    }

    //?
    public void printPlan(){
        for(int i = 0; i<DayOfSubjects.length; i++){
            for(int j = 0; j<DayOfSubjects[i].getLength(); j++){
            }
        }
    }

    public String getPlanAsString(){
        String result = "";
            String [] days = {"Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag"};
            for(int i = 0; i<5; i++){       //Weekdays are Static, there are always 5 days
                result = result + days[i] + " ";
                for (int j = 0; j<this.getDayPlanAtDay(i).getLength(); j++){
                    result = result + this.getDayPlanAtDay(i).getSubjectAtHour(j) + "  Bei: " + this.getDayPlanAtDay(i).getTeacherAtHour(j) + " | ";
                }
                result = result + '\n';
            }
            return result;
    }
    public WeekPlan createWeekPlanTeacherOnly(String teacherString){

        for(int i = 0; i<5; i++){
            for(int j = 0; j<this.getDayPlanAtDay(0).getLength(); j++){
                this.getDayPlanAtDay(i).setTeacherAtHour(teacherString, j);
                this.getDayPlanAtDay(i).setSubjectAtHour("---", j);
            }
        }
        return this;
    }


    public WeekPlan createWeekPlanByDataString(String target){           //and check for Syntax Errors
        int currentDay = 0;
        int currentHour = 0;
        int amountOfSpaces = 0;
        boolean teacher = false;
        String currentSubject = "";
        String currentTeacher = "";
            for (int i = 0; i < target.length(); i++) {
                if (target.charAt(i) == '|') {            //if: StundenEintrag Ende
                    this.getDayPlanAtDay(currentDay).setSubjectAtHour(currentSubject, currentHour);
                    this.getDayPlanAtDay(currentDay).setTeacherAtHour(currentTeacher, currentHour);
                    currentHour++;
                    currentSubject = "";
                    currentTeacher = "";
                    teacher = false;
                } else if (target.charAt(i) == '/') {
                    teacher = true;
                } else if (target.charAt(i) == ' ') {           //IF: Tageseintrag Ende

                    //Wenn nicht alle Stunden an einem Tag angegeben sind -> Error
                    if(currentHour != 9 && currentHour != 0){
                        throw new IllegalArgumentException("Not all Hours mentioned!");
                    }

                    //Wenn zu viele Leerzeichen angegeben sind -> Error
                    if(amountOfSpaces > 4){
                        throw new IllegalArgumentException("Syntax Error Detected");
                    }
                    amountOfSpaces++;
                    currentDay++;
                    currentHour = 0;
                    teacher = false;
                } else {           //else: Fach
                    if (teacher) {
                        currentTeacher = currentTeacher + target.charAt(i);
                    } else {
                        currentSubject = currentSubject + target.charAt(i);
                    }
                }
            }

            //Wenn nicht alle Tage angegeben sind -> Error
            if(currentDay != 4){
                throw new IllegalArgumentException("Not everyday added");
            }
        return this;
    }


}
