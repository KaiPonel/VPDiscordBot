public class DayPlan {
    private String SubjectAndTeacher[][] = new String [9][2];
       public int getLength(){
        return SubjectAndTeacher.length;
    }
    public void setSubjectAtHour(String subject, int hour){
        this.SubjectAndTeacher[hour][0] = subject;
    }
    public void setTeacherAtHour(String teacher, int hour){
        this.SubjectAndTeacher[hour][1] = teacher;
    }
    public String getSubjectAtHour(int hour){
        return SubjectAndTeacher[hour][0];
    }
    public String getTeacherAtHour(int hour){
        return SubjectAndTeacher[hour][1];
    }
}
