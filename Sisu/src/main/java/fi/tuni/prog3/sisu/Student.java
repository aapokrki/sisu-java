package fi.tuni.prog3.sisu;

import java.util.ArrayList;

/**
 * A class representing a student in Tampere University
 */
public class Student {

    // Basic information
    public String name;
    public String studentNumber;
    public int startYear;
    public int endYear;

    public int credits;

    public String degreeProgrammeId;
    public String mandatoryStudyModuleId;

    public transient DegreeProgramme degreeProgramme;
    public ArrayList<CourseUnit> completedCourses = new ArrayList<>();


    /**
     * Constructs an empty Student class
     */
    public Student(){}

    /**
     * Returns student name
     * @return student name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name for the student
     * @param name - student's name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns student number
     * @return student number
     */
    public String getStudentNumber() {
        return studentNumber;
    }

    /**
     * Sets student number
     * @param studentNumber - student number
     */
    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    /**
     * Returns the year student began with their studies
     * @return the year student began with their studies
     */
    public int getStartYear() {
        return startYear;
    }

    /**
     * Sets a start year for studies
     * @param startYear - start year for studies
     */
    public void setStartYear(int startYear) {
        this.startYear = startYear;
    }

    /**
     * Returns the year studies ended or the year studies are supposed to end
     * @return the year studies ended or the year studies are supposed to end
     */
    public int getEndYear() {
        return endYear;
    }

    /**
     * Sets the year studies ended or the year studies are supposed to end
     * @param endYear - the year studies ended or the year studies are supposed to end
     */
    public void setEndYear(int endYear) {
        this.endYear = endYear;
    }

    /**
     * Returns degree programme id
     * @return degree programme id
     */
    public String getDegreeProgrammeId() {
        return degreeProgrammeId;
    }

    /**
     * Sets degree programme id
     * @param degreeProgrammeId - Id for the degree program
     */
    public void setDegreeProgrammeId(String degreeProgrammeId){
        this.degreeProgrammeId = degreeProgrammeId;
    }

    /**
     * Returns the mandatory study module
     * @return mandatory study module
     */
    public String getMandatoryStudyModuleId() {
        return mandatoryStudyModuleId;
    }

    /**
     * Sets the mandatory study module selected in the registration screen
     * @param mandatoryStudyModuleId - mandatory study module
     */
    public void setMandatoryStudyModuleId(String mandatoryStudyModuleId) {
        this.mandatoryStudyModuleId = mandatoryStudyModuleId;
    }

    /**
     * Returns degree programme
     * @return degree programme
     */
    public DegreeProgramme getDegreeProgramme() {
        return degreeProgramme;
    }

    /**
     * Sets degree programme for student
     * @param degreeProgramme - degree programme
     */
    public void setDegreeProgramme(DegreeProgramme degreeProgramme) {
        this.degreeProgramme = degreeProgramme;

    }

    /**
     * Returns completed courses
     * @return completed courses
     */
    public ArrayList<CourseUnit> getCompletedCourses() {
        return completedCourses;
    }

    /**
     * Marks course as completed
     * @param course - course
     */
    public void addCompletedCourse(CourseUnit course){
        if(!completedCourses.contains(course)){
            completedCourses.add(course);
            this.credits += course.getCreditsInt();
        }

        // reinstates the degreeprogramme with completed courses when starting up
        degreeProgramme.addCompletedCourse(course);

    }

    /**
     * Returns the amount of study credits user has completed
     * @return study credits
     */
    public int getStudyCredits() {
        int credits = 0;

        for (CourseUnit courseUnit : completedCourses) {
            credits += courseUnit.getCreditsInt();
        }

        return credits;
    }

    /**
     * Returns the calculated GPA of the user
     * @return grade point average
     */
    public double getGPA() {
        double grades = 0;
        double courses = 0;

        for (CourseUnit courseUnit : completedCourses) {
            if (courseUnit.getGrade() == 0) {
                continue;
            }
            grades += courseUnit.getGrade();
            ++courses;
        }

        if (grades == 0) {
            return 0;
        }

        return grades/courses;
    }
}
