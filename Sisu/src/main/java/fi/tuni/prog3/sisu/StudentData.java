package fi.tuni.prog3.sisu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/**
 * Implements a data structure for the program
 */
public class StudentData {

    /**Current user in SISU*/
    public Student user;

    private final Map<String, Student> students;

    /**Access to JsonLogic*/
    public JSONLogic jsonData;

    /**
     * Constructs a data structure
     */
    public StudentData() {
        jsonData = new JSONLogic();
        students = jsonData.studentsFromJsonToClass();
    }

    /**
     * Calls JSONLogic to save data to JSON format
     */
    protected void save()
    {
        ArrayList<Student> list = new ArrayList<>(students.values());
        jsonData.studentsToJson(list);
    }

    /**
     * Handles login operation
     * @param studentNumber - student number
     * @return boolean value representing success of the operation
     */
    public boolean login(String studentNumber){
        if (!students.containsKey(studentNumber)) {
            return false;
        }
        user = students.get(studentNumber);

        // Set degreeProgramme and completed classes to it
        try {
            user.setDegreeProgramme(jsonData.readAPIData(user.getDegreeProgrammeId(),user.getMandatoryStudyModuleId()));
            for(CourseUnit course : user.getCompletedCourses()){
                user.addCompletedCourse(course);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Handles account creation operation
     * @param name - name
     * @param studentNumber - student number
     * @param inputDegreeProgramme - degree programme
     * @param inputMandatoryStudyModule - mandatory study module
     * @param startYear - start year
     * @param endYear - end year
     * @return boolean value representing success of the operation
     * @throws IOException - From readAPIData
     */
    public boolean createAccount(String name, String studentNumber, String inputDegreeProgramme, String inputMandatoryStudyModule,
                                 String startYear, String endYear) throws IOException {

        DegreeProgramme degreeProgramme = jsonData.readAPIData(inputDegreeProgramme, inputMandatoryStudyModule);

        if (students.containsKey(studentNumber)) {
            return false;
        }

        user = new Student();
        user.setDegreeProgramme(degreeProgramme);
        user.setName(name);
        user.setStudentNumber(studentNumber);
        user.setDegreeProgrammeId(inputDegreeProgramme);
        user.setMandatoryStudyModuleId(inputMandatoryStudyModule);

        if (!startYear.isEmpty()) {
            user.setStartYear(Integer.parseInt(startYear));
        }

        if (!endYear.isEmpty()) {
            user.setEndYear(Integer.parseInt(endYear));
        }

        students.put(studentNumber, user);
        return true;
    }

    /**
     * Deletes account
     */
    public void deleteAccount() {
        students.remove(user.studentNumber);
        user = null;
    }
}
