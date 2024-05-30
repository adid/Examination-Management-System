import java.sql.Date;
import java.sql.Time;

public class Exam {
    private long examId;
    private String courseID;
    private String examType;
    private String academicYear;
    private String semester;
    private Date examDate;
    private Time startTime;
    private Time endTime;

    public Exam(long examId, String courseID, String academicYear, String semester, String exam_type, Date examDate, Time startTime, Time endTime) {
        this.examId = examId;
        this.courseID = courseID;
        this.academicYear = academicYear;
        this.semester = semester;
        this.examType = exam_type;
        this.examDate = examDate;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public long getExamId() {
        return examId;
    }

    public void setExamId(long examId) {
        this.examId = examId;
    }

    public String getExamType() {
        return examType;
    }

    public void setExamType(String examType) {
        this.examType = examType;
    }

    public Date getExamDate() {
        return examDate;
    }

    public void setExamDate(Date examDate) {
        this.examDate = examDate;
    }
}
