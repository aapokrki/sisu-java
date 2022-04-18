package fi.tuni.prog3.sisu;

import java.util.ArrayList;
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

        students = jsonData.studentsFromJsonToClass();
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

            String year1 = data.get(2);
            startYear = Integer.parseInt(year1);
            user.setStartYear(startYear);

            if (data.size() == 4) {
                String year2 = data.get(3);
                endYear = Integer.parseInt(year2);
                user.setEndYear(endYear);
            }
        }

        students.put(studentNumber, user);
        return true;
    }

    public void deleteAccount() {
        students.remove(user.studentNumber);
        user = null;
    }
}
