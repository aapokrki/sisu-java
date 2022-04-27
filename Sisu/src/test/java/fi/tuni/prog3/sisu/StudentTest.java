package fi.tuni.prog3.sisu;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.*;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import org.junit.jupiter.api.Order;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests Student.class functionality by checking getters and setter in order. Changing variables in setters
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StudentTest {
    static JSONLogic logic;
    static Student t;
    static DegreeProgramme degreeProgramme;

    @BeforeAll
    static void setUp() throws IOException {
        logic = new JSONLogic();
        t = new Student();
        t.setName("Testi Teppo");
        t.setStudentNumber("H111111");

        String degreeProgrammeId = "otm-82e1f1f8-f0ef-48f0-9854-f4f7837d9955";
        t.setDegreeProgrammeId(degreeProgrammeId);
        t.setMandatoryStudyModuleId(null);
        degreeProgramme = logic.readAPIData(degreeProgrammeId,null);
        t.setDegreeProgramme(degreeProgramme);

        t.setStartYear(2020);
        t.setEndYear(2025);

    }

    @Test
    @Order(1)
    void getName() {
        assertEquals("Testi Teppo", t.getName());
    }

    @Test
    @Order(2)
    void setName() {
        String name = "Testi Tuula";
        t.setName(name);
        assertEquals(name, t.getName());
    }

    @Test
    @Order(3)

    void getStudentNumber() {
        assertEquals("H111111", t.getStudentNumber());
    }

    @Test
    @Order(4)
    void setStudentNumber() {
        String studentNumber = "G222222";
        t.setStudentNumber(studentNumber);
        assertEquals(studentNumber, t.getStudentNumber());
    }

    @Test
    @Order(5)
    void getStartYear() {
        assertEquals(2020, t.getStartYear());

    }

    @Test
    @Order(6)
    void setStartYear() {
        int startYear = 3000;
        t.setStartYear(startYear);
        assertEquals(startYear, t.getStartYear());
    }

    @Test
    @Order(7)
    void getEndYear() {
        assertEquals(2025, t.getEndYear());

    }

    @Test
    @Order(8)
    void setEndYear() {
        int endYear = 3005;
        t.setEndYear(endYear);
        assertEquals(endYear, t.getEndYear());
    }

    @Test
    @Order(9)
    void getDegreeProgrammeId() {
        assertEquals("otm-82e1f1f8-f0ef-48f0-9854-f4f7837d9955", t.getDegreeProgrammeId());

    }

    @Test
    @Order(10)
    void setDegreeProgrammeId() {
        String tst = "otm-fa02a1e7-4fe1-43e3-818b-810d8e723531";
        t.setDegreeProgrammeId(tst);
        assertEquals(tst, t.getDegreeProgrammeId());
    }

    @Test
    @Order(11)
    void getMandatoryStudyModuleId() {
        assertNull(t.getMandatoryStudyModuleId());

    }

    @Test
    @Order(12)
    void setMandatoryStudyModuleId() {
        String tietotekniikka = "otm-e4a8addd-5944-4f94-9e56-d1b51d1f22ce";
        t.setMandatoryStudyModuleId(tietotekniikka);
        assertEquals(tietotekniikka,t.getMandatoryStudyModuleId());
    }

    @Test
    @Order(13)
    void getDegreeProgramme() {
        assertEquals(degreeProgramme,t.getDegreeProgramme());
    }

    @Test
    @Order(14)
    void setDegreeProgramme() throws IOException {
        DegreeProgramme degreeProgramme1 = logic.readAPIData(t.getDegreeProgrammeId(),t.getMandatoryStudyModuleId());
        t.setDegreeProgramme(degreeProgramme1);
    }

    @Test
    @Order(15)
    void getCompletedCourses() {
        assertTrue(t.getCompletedCourses().isEmpty());
    }

    @Test
    @Order(16)
    void addCompletedCourse() throws IOException {
        JsonObject signjamit = logic.requestJsonElementFromURL("https://sis-tuni.funidata.fi/kori/api/course-units/by-group-id?groupId=tut-cu-g-45460&universityId=tuni-university-root-id");
        CourseUnit course = new CourseUnit(signjamit);
        course.setGrade(5);
        course.setCompleted();
        t.addCompletedCourse(course);

        assertEquals(course,t.getCompletedCourses().get(0));

    }

    @Test
    @Order(17)
    void getStudyCredits() {
        assertEquals(5,t.getStudyCredits());
    }

    @Test
    @Order(18)
    void getGPA() {
        assertEquals(5,t.getGPA());
    }

    @Test
    @Order(19)
    void getGPAShouldBeThreePointFive() throws IOException {
        JsonObject tiraka = logic.requestJsonElementFromURL("https://sis-tuni.funidata.fi/kori/api/course-units/by-group-id?groupId=tut-cu-g-36938&universityId=tuni-university-root-id");

        CourseUnit course1 = new CourseUnit(tiraka);

        course1.setGrade(2);
        course1.setCompleted();

        t.addCompletedCourse(course1);

        assertEquals(3.5,t.getGPA());
    }

    @Test
    @Order(20)
    void getStudyCreditsShouldBeTen() {
        assertEquals(10,t.getStudyCredits());
    }
}