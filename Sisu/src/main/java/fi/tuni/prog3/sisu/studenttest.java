package fi.tuni.prog3.sisu;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;

/**
 * A class representing a student in Tampere University
 */
public class studenttest {

    // Basic information
    // Gsonin takia kaikki public
    public String name;
    public String studentNumber;
    public int startYear;
    public int endYear;


    // Aapon testimuutokset
    public DegreeProgramme degreeProgramme;
    public StudyModule studyModule;
    public ArrayList<CourseUnit> completedCourses = new ArrayList<>();


    public void setDegreeProgramme(DegreeProgramme degreeProgramme) {
        this.degreeProgramme = degreeProgramme;
    }

    public void setStudyModule(StudyModule studyModule) {
        this.studyModule = studyModule;
    }

    public void addCompletedCourse(CourseUnit course){
        completedCourses.add(course);
    }


    /*
    Needs a data structure containing degree data
     */


    /**
     * Constructs an empty Student class
     */
    public studenttest(){}

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
