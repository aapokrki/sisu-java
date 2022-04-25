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

    public DegreeProgramme degreeProgramme;
    public ArrayList<CourseUnit> completedCourses = new ArrayList<>();

    /**
     * Sets degree programme for student
     * @param degreeProgramme - degree programme
     */
    public void setDegreeProgramme(DegreeProgramme degreeProgramme) {
        this.degreeProgramme = degreeProgramme;
    }

    public DegreeProgramme getDegreeProgramme() {
        return degreeProgramme;
    }

    /**
     * Marks course as completed
     * @param course - course
     */
    public void addCompletedCourse(CourseUnit course){
        completedCourses.add(course);
        degreeProgramme.addCompletedCourse(course);
    }

    public int getStudyCredits() {
        int credits = 0;

        for (CourseUnit courseUnit : completedCourses) {
            credits += courseUnit.getCreditsInt();
        }

        return credits;
    }

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

    /**
     * Constructs an empty Student class
     */
    public Student(){}

    // Getters and setters

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
}
