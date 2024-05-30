import java.sql.Date;
import java.sql.Time;

public class ExamSchedule {
    private String examId;
    private String courseId;
    private String courseTitle;
    private String academicYear;
    private String semester;
    private String examType;
    private Date examDate;
    private Time startTime;
    private Time endTime;

    public ExamSchedule(String examId, String courseId, String courseTitle, String academicYear, String semester, String examType, Date examDate, Time startTime, Time endTime) {
        this.examId = examId;
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.academicYear = academicYear;
        this.semester = semester;
        this.examType = examType;
        this.examDate = examDate;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getExamId() { return examId; }
    public String getCourseId() { return courseId; }
    public String getCourseTitle() { return courseTitle; }
    public String getAcademicYear() { return academicYear; }
    public String getSemester() { return semester; }
    public String getExamType() { return examType; }
    public Date getExamDate() { return examDate; }
    public Time getStartTime() { return startTime; }
    public Time getEndTime() { return endTime; }
}
