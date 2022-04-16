package fi.tuni.prog3.sisu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Implements a data structure for the program
 */
public class StudentData {

    public Student user;
    private Map<String, Student> students;
    private JSONLogic jsonData;

    /**
     * Constructs a data structure
     */
    public StudentData() {
        jsonData = new JSONLogic();

        // studentsFromJsonToClass() needs to be fixed in order to get the program to launch
        students = new HashMap<>();
        //students = jsonData.studentsFromJsonToClass();
    }

    /**
     * Calls JSONLogic to save data to JSON format
     */
    protected void finalize()
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
     * Handles accoun creation operation
     * @param data - data from the text fields
     * @return boolean value representing success of the operation
     */
    public boolean createAccount(ArrayList<String> data) {
        String name = data.get(0);
        String studentNumber = data.get(1);

        if (students.containsKey(studentNumber)) {
            return false;
        }

        user = new Student();
        user.setName(name);
        user.setStudentNumber(studentNumber);

        int startYear;
        int endYear;

        if (data.size() > 2) {
            startYear = Integer.parseInt(data.get(2));
            endYear = Integer.parseInt(data.get(3));
            user.setStartYear(startYear);
            user.setEndYear(endYear);
        }

        students.put(studentNumber, user);
        return true;
    }
}
