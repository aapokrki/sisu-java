package fi.tuni.prog3.sisu;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the functions and methods of DegreeProgramme.class
 */
class DegreeProgrammeTest {

    JSONLogic logic;

    @BeforeEach
    void setUp(){
        logic = new JSONLogic();
    }

    @ParameterizedTest
    @MethodSource("degreeProgrammeJsonObjProvider")
    void studyModuleConstructorTest(JsonObject degreeProgrammeInput) {

        String name;
        JsonObject nameObj = degreeProgrammeInput.get("name").getAsJsonObject();
        if (nameObj.get("fi") == null) {
            name = nameObj.get("en").getAsString();
        } else {
            name = nameObj.get("fi").getAsString();
        }

        String code = degreeProgrammeInput.get("code").getAsString();
        String id = degreeProgrammeInput.get("groupId").getAsString();

        DegreeProgramme degreeProgramme = new DegreeProgramme(degreeProgrammeInput);

        assertEquals(name, degreeProgramme.getName());
        assertEquals(code, degreeProgramme.getCode());
        assertEquals(id, degreeProgramme.getId());
        assertEquals("DegreeProgramme", degreeProgramme.getType());
        assertEquals(degreeProgrammeInput, degreeProgramme.getJsonObject());
        assertTrue(degreeProgramme.getStudyModules().isEmpty());
        assertEquals(0,degreeProgramme.getCurrentCredits());
    }

    @ParameterizedTest
    @MethodSource("degreeProgrammeProvider")
    void setStudyModules(DegreeProgramme degreeProgramme) throws IOException {
        JsonObject logopediayht = logic.requestJsonObjectFromUrl("https://sis-tuni.funidata.fi/kori/api/modules/by-group-id?groupId=otm-905f0726-c6c4-444c-8d04-c1c59007906e&universityId=tuni-university-root-id");
        StudyModule studyModuleToAdd = new StudyModule(logopediayht);

        ArrayList<StudyModule> newStudyModules = new ArrayList<>();
        ArrayList<StudyModule> oldStudyModules = degreeProgramme.getStudyModules();

        newStudyModules.add(studyModuleToAdd);
        degreeProgramme.setStudyModules(newStudyModules);

        assertNotEquals(newStudyModules,oldStudyModules);
        assertEquals(newStudyModules,degreeProgramme.getStudyModules());

    }

    @ParameterizedTest
    @MethodSource("degreeProgrammeJsonObjProvider")
    void studyModuleAddChildren(JsonObject degreeProgrammeInput) throws IOException {

        JsonObject module1 = logic.requestJsonObjectFromUrl("https://sis-tuni.funidata.fi/kori/api/modules/by-group-id?groupId=otm-905f0726-c6c4-444c-8d04-c1c59007906e&universityId=tuni-university-root-id");
        JsonObject module2 = logic.requestJsonObjectFromUrl("https://sis-tuni.funidata.fi/kori/api/modules/by-group-id?groupId=otm-41aab7e5-a1fc-4385-850e-40187c506b0e&universityId=tuni-university-root-id");

        StudyModule studyModule1 = new StudyModule(module1);
        StudyModule studyModule2 = new StudyModule(module2);


        // StudyModule to add children to
        DegreeProgramme degreeProgramme = new DegreeProgramme(degreeProgrammeInput);

        assertTrue(degreeProgramme.getStudyModules().isEmpty());

        degreeProgramme.addChild(studyModule1);
        assertEquals(1,degreeProgramme.getStudyModules().size());

        // Cannot add itself as child, Can only add studymodules
        degreeProgramme.addChild(degreeProgramme);
        assertEquals(1,degreeProgramme.getStudyModules().size());

        degreeProgramme.addChild(studyModule2);
        assertEquals(2,degreeProgramme.getStudyModules().size());

    }

    @Test
    void addCompletedCourseTrue() throws IOException {
        String tst = "otm-fa02a1e7-4fe1-43e3-818b-810d8e723531";
        String tietotek = "otm-e4a8addd-5944-4f94-9e56-d1b51d1f22ce";
        DegreeProgramme tietotekniikka = logic.readAPIData(tst,tietotek);

        // Signaalit ja mittaaminen belongs to TST
        JsonObject signjamit = logic.requestJsonObjectFromUrl("https://sis-tuni.funidata.fi/kori/api/course-units/by-group-id?groupId=tut-cu-g-45460&universityId=tuni-university-root-id");
        CourseUnit course1 = new CourseUnit(signjamit);
        course1.setCompleted();
        course1.setGrade(5);

        tietotekniikka.addCompletedCourse(course1);

        // Course that was just completed
        CourseUnit targetCourse = tietotekniikka.getStudyModules().get(0).getStudyModules().get(0).getCourseUnits().get(8);

        assertTrue(targetCourse.isCompleted());
        assertEquals(5, targetCourse.getGrade());

    }


    @Test
    void addCompletedCourseFalse() throws IOException {
        String tst = "otm-fa02a1e7-4fe1-43e3-818b-810d8e723531";
        String tietotek = "otm-e4a8addd-5944-4f94-9e56-d1b51d1f22ce";
        DegreeProgramme tietotekniikka = logic.readAPIData(tst,tietotek);
        DegreeProgramme tietotekniikkaCopy = logic.readAPIData(tst,tietotek);

        // Urban development does NOT belong to tst
        JsonObject urbdev = logic.requestJsonObjectFromUrl("https://sis-tuni.funidata.fi/kori/api/course-units/by-group-id?groupId=otm-69aad478-104a-4653-a8db-6a45446ab525&universityId=tuni-university-root-id");
        CourseUnit course2 = new CourseUnit(urbdev);
        course2.setCompleted();
        course2.setGrade(2);

        tietotekniikka.addCompletedCourse(course2);

        // Course cant be added as complete, so the degreeprogrammes must be identical
        // Ignoring wonky json that can sometimes just have values swapped around
        assertThat(tietotekniikka)
                .usingRecursiveComparison()
                .ignoringFieldsMatchingRegexes("parent.*",".*JsonObj")
                .isEqualTo(tietotekniikkaCopy);

    }

    static Stream<Arguments> degreeProgrammeProvider() throws IOException {
        JSONLogic logic = new JSONLogic();
        String tst = "otm-fa02a1e7-4fe1-43e3-818b-810d8e723531";
        String tietotek = "otm-e4a8addd-5944-4f94-9e56-d1b51d1f22ce";
        DegreeProgramme tietotekniikka = logic.readAPIData(tst,tietotek);

        String viestObj = "otm-82e1f1f8-f0ef-48f0-9854-f4f7837d9955";
        DegreeProgramme viest = logic.readAPIData(viestObj,null);

        return Stream.of(
                Arguments.arguments(tietotekniikka),
                Arguments.arguments(viest)
        );
    }

    static Stream<Arguments> degreeProgrammeJsonObjProvider() throws IOException {
        JSONLogic logic = new JSONLogic();

        JsonObject tst = logic.requestJsonObjectFromUrl("https://sis-tuni.funidata.fi/kori/api/modules/by-group-id?groupId=otm-fa02a1e7-4fe1-43e3-818b-810d8e723531&universityId=tuni-university-root-id");
        JsonObject viest = logic.requestJsonObjectFromUrl("https://sis-tuni.funidata.fi/kori/api/modules/by-group-id?groupId=otm-82e1f1f8-f0ef-48f0-9854-f4f7837d9955&universityId=tuni-university-root-id");


        return Stream.of(
                Arguments.arguments(tst),
                Arguments.arguments(viest)
        );
    }

}