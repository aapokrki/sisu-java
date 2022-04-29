package fi.tuni.prog3.sisu;

import org.junit.After;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the life-cycle of the current user.
 * Create account, login, account changes and delete.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StudentDataTest {

    static StudentData studentData;

    @BeforeAll
    static void setUp(){
        studentData = new StudentData();
    }

    @Test
    @Order(1)
    void createAccount() throws IOException {
        String name = "Testi Tanja";
        String studentNumber = "789testi987";
        String inputDegreeProgramme = "uta-tohjelma-1759";
        String inputMandatoryStudyModule = "otm-1f0d364a-e115-468f-90ee-bc2eebd64072";
        String startYear = "1000";
        String endYear = "1050";

        // Lets assume that Testi tanja isn't someone real
        assertTrue(studentData.createAccount(name,studentNumber,inputDegreeProgramme,inputMandatoryStudyModule,startYear,endYear));
        assertFalse(studentData.createAccount(name,studentNumber,inputDegreeProgramme,inputMandatoryStudyModule,startYear,endYear));

    }

    @Test
    @Order(2)
    void login(){
        assertFalse(studentData.login("tämäeioleopiskelijanumero"));
        assertTrue(studentData.login("789testi987"));
        assertNotNull(studentData.user);
        studentData.user.setName("Testi Terhi");
        assertEquals("Testi Terhi", studentData.user.getName());

    }

    @Test
    @Order(3)
    void deleteAccount() {
        assertTrue(studentData.login("789testi987"));
        studentData.deleteAccount();
        assertNull(studentData.user);
    }


    @AfterEach
    void reset(){
        studentData.user = null;
    }

}