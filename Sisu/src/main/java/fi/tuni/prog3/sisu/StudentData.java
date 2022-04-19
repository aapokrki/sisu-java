package fi.tuni.prog3.sisu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/**
 * Implements a data structure for the program
 */
public class StudentData {

    public Student user;
    private Map<String, Student> students;
    public JSONLogic jsonData;
    //public Map <String, String> degreeProgrammes;

    /**
     * Constructs a data structure
     */
    public StudentData() {
        jsonData = new JSONLogic();
        students = jsonData.studentsFromJsonToClass();
        //degreeProgrammes = jsonData.readAllDegreeprogrammes();
    }

    /**
     * Calls JSONLogic to save data to JSON format
     */
    protected void save()
    {
        ArrayList<Student> list = new ArrayList<Student>(students.values());
        jsonData.studentsToJson(list);
    }

    /**
     * Handles login operation
     * @param studentNumber - student number
     * @return boolean value representing success of the operation
     */
    public boolean login(String studentNumber) {
        if (!students.containsKey(studentNumber)) {
            return false;
        }
        user = students.get(studentNumber);
        return true;
    }

    /**
     * Handles account creation operation
     * @return boolean value representing success of the operation
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

        if (!startYear.isEmpty()) {
            user.setStartYear(Integer.parseInt(startYear));
        }

        if (!endYear.isEmpty()) {
            user.setEndYear(Integer.parseInt(endYear));
        }

        /*
        int startYear;
        int endYear;

        if (data.size() > 3) {

            String year1 = data.get(3);
            startYear = Integer.parseInt(year1);
            user.setStartYear(startYear);

            if (data.size() == 5) {
                String year2 = data.get(4);
                endYear = Integer.parseInt(year2);
                user.setEndYear(endYear);
            }
        }
        */

        students.put(studentNumber, user);
        return true;
    }

    public void deleteAccount() {
        students.remove(user.studentNumber);
        user = null;
    }
}
