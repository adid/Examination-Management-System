public class Course {
    private String courseId;
    private int courseCode;
    private String courseTitle;
    private int credit;

    public Course(String courseId, int courseCode, String courseTitle) {
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
    }
    public Course(String courseId, int courseCode, String courseTitle, int credit) {
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.credit= credit;
    }

    public String getCourseId() {
        return courseId;
    }

    public int getCourseCode() {
        return courseCode;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public int getCredit() {
        return credit;
    }
}
