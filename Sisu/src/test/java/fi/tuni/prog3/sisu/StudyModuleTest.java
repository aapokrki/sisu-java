package fi.tuni.prog3.sisu;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the functions and methods of StudyModule.class
 */
class StudyModuleTest {
    JSONLogic logic;

    @BeforeEach
    void setUp(){
        logic = new JSONLogic();
    }

    @ParameterizedTest
    @MethodSource("studyModuleProvider")
    void studyModuleConstructorTest(JsonObject studyModuleInput) {

        String name;
        JsonObject nameObj = studyModuleInput.get("name").getAsJsonObject();

        if (nameObj.get("fi") == null) {
            name = nameObj.get("en").getAsString();
        } else {
            name = nameObj.get("fi").getAsString();
        }

        String code = studyModuleInput.get("code").getAsString();
        String id = studyModuleInput.get("groupId").getAsString();

        StudyModule studyModule = new StudyModule(studyModuleInput);

        assertEquals(name, studyModule.getName());
        assertEquals(code, studyModule.getCode());
        assertEquals(id, studyModule.getId());
        assertEquals("StudyModule", studyModule.getType());
        assertEquals(studyModuleInput, studyModule.getJsonObject());
        assertTrue(studyModule.getStudyModules().isEmpty());
        assertTrue(studyModule.getCourseUnits().isEmpty());
    }

    @ParameterizedTest
    @MethodSource("studyModuleProvider")
    void studyModuleAddChildren(JsonObject studyModuleInput) throws IOException {
        JsonObject course1 = logic.requestJsonObjectFromUrl("https://sis-tuni.funidata.fi/kori/api/course-units/by-group-id?groupId=tut-cu-g-45460&universityId=tuni-university-root-id");
        JsonObject course2 = logic.requestJsonObjectFromUrl("https://sis-tuni.funidata.fi/kori/api/course-units/by-group-id?groupId=otm-69aad478-104a-4653-a8db-6a45446ab525&universityId=tuni-university-root-id");
        JsonObject module1 = logic.requestJsonObjectFromUrl("https://sis-tuni.funidata.fi/kori/api/modules/by-group-id?groupId=otm-d9fa2212-5c00-4c50-b8a9-50714cf4b5e9&universityId=tuni-university-root-id");


        CourseUnit courseunit1 = new CourseUnit(course1);
        CourseUnit courseunit2 = new CourseUnit(course2);
        StudyModule studyModule1 = new StudyModule(module1);

        // StudyModule to add children to
        StudyModule studyModule = new StudyModule(studyModuleInput);

        assertTrue(studyModule.getStudyModules().isEmpty());
        assertTrue(studyModule.getCourseUnits().isEmpty());


        studyModule.addChild(courseunit1);
        assertTrue(studyModule.getStudyModules().isEmpty());
        assertEquals(1,studyModule.getCourseUnits().size());

        // Cannot add itself as child
        studyModule.addChild(studyModule);
        assertTrue(studyModule.getStudyModules().isEmpty());
        assertEquals(1,studyModule.getCourseUnits().size());

        studyModule.addChild(courseunit2);
        studyModule.addChild(studyModule1);
        assertEquals(1,studyModule.getStudyModules().size());
        assertEquals(2,studyModule.getCourseUnits().size());

    }


    @ParameterizedTest
    @MethodSource("studyModuleProvider")
    void studyModuleSetParent(JsonObject studyModuleInput) throws IOException {

        // Parent doesnt matter, but this is Elinikäinen oppiminen ja kasvatus
        String url = "https://sis-tuni.funidata.fi/kori/api/modules/by-group-id?groupId=otm-24730a5d-e01b-4f91-9a2d-fbda7ce4477c&universityId=tuni-university-root-id";
        JsonObject parentObj = logic.requestJsonObjectFromUrl(url);
        StudyModule parentModule = new StudyModule(parentObj);
        StudyModule studyModule = new StudyModule(studyModuleInput);

        assertNull(studyModule.getParent());

        studyModule.setParent(parentModule);
        assertEquals(parentModule, studyModule.getParent());

        //Second test with degreeprogramme as parent, Kielten kanditaattiohjelma
        String url2 = "https://sis-tuni.funidata.fi/kori/api/modules/by-group-id?groupId=otm-d8575e77-1ee3-48a6-b3c1-4acb30c146ce&universityId=tuni-university-root-id";
        JsonObject parentObj2 = logic.requestJsonObjectFromUrl(url2);
        DegreeProgramme parentProgramme = new DegreeProgramme(parentObj2);

        studyModule.setParent(parentProgramme);
        assertEquals(parentProgramme, studyModule.getParent());

    }

    @Test
    void addCompletedCourseTrue() throws IOException {

        // Signaalit ja mittaaminen belongs to Tietotekniikan yhteiset opinnot
        JsonObject tstyht = logic.requestJsonObjectFromUrl("https://sis-tuni.funidata.fi/kori/api/modules/by-group-id?groupId=otm-316ac8bf-ff36-4ec0-8997-617976500368&universityId=tuni-university-root-id");
        JsonObject signjamit = logic.requestJsonObjectFromUrl("https://sis-tuni.funidata.fi/kori/api/course-units/by-group-id?groupId=tut-cu-g-45460&universityId=tuni-university-root-id");
        StudyModule studyModule1 = new StudyModule(tstyht);
        CourseUnit course1 = new CourseUnit(signjamit);
        studyModule1.addChild(course1);

        // Course was set to complete succesfully
        assertTrue(studyModule1.addCompletedCourse(course1));
        assertTrue(studyModule1.getCourseUnits().get(0).isCompleted());



    }

    @Test
    void addCompletedCourseFalse() throws IOException {

        //Vuorovaikutus does not belong to suomenkirj.
        JsonObject suomenkirj= logic.requestJsonObjectFromUrl("https://sis-tuni.funidata.fi/kori/api/modules/by-group-id?groupId=otm-d9fa2212-5c00-4c50-b8a9-50714cf4b5e9&universityId=tuni-university-root-id");
        JsonObject vuorovaikutus = logic.requestJsonObjectFromUrl("https://sis-tuni.funidata.fi/kori/api/course-units/by-group-id?groupId=uta-ykoodi-48285&universityId=tuni-university-root-id");
        JsonObject signjamit = logic.requestJsonObjectFromUrl("https://sis-tuni.funidata.fi/kori/api/course-units/by-group-id?groupId=tut-cu-g-45460&universityId=tuni-university-root-id");

        StudyModule studyModule2 = new StudyModule(suomenkirj);
        CourseUnit course1 = new CourseUnit(signjamit);
        CourseUnit course2= new CourseUnit(vuorovaikutus);

        // Only course2 added
        studyModule2.addChild(course2);

        // Cand complete course1, because it isn't added
        assertFalse(studyModule2.addCompletedCourse(course1));

        // Added course2 is not completed
        assertFalse(studyModule2.getCourseUnits().get(0).isCompleted());

    }


    static Stream<Arguments> studyModuleProvider() throws IOException {
        JSONLogic logic = new JSONLogic();

        JsonObject logopediayht = logic.requestJsonObjectFromUrl("https://sis-tuni.funidata.fi/kori/api/modules/by-group-id?groupId=otm-905f0726-c6c4-444c-8d04-c1c59007906e&universityId=tuni-university-root-id");
        JsonObject filosofiayht = logic.requestJsonObjectFromUrl("https://sis-tuni.funidata.fi/kori/api/modules/by-group-id?groupId=otm-41aab7e5-a1fc-4385-850e-40187c506b0e&universityId=tuni-university-root-id");


        return Stream.of(
                Arguments.arguments(logopediayht),
                Arguments.arguments(filosofiayht)
        );
    }
    static Stream<Arguments> courseProvider() throws IOException {
        JSONLogic logic = new JSONLogic();

        JsonObject signjamit = logic.requestJsonObjectFromUrl("https://sis-tuni.funidata.fi/kori/api/course-units/by-group-id?groupId=tut-cu-g-45460&universityId=tuni-university-root-id");
        JsonObject urbdev = logic.requestJsonObjectFromUrl("https://sis-tuni.funidata.fi/kori/api/course-units/by-group-id?groupId=otm-69aad478-104a-4653-a8db-6a45446ab525&universityId=tuni-university-root-id");


        return Stream.of(
                Arguments.arguments(signjamit),
                Arguments.arguments(urbdev)
        );
    }
}