package fi.tuni.prog3.sisu;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the functions and methods of CourseUnit.class
 */
class CourseUnitTest {
    JSONLogic logic;

    @BeforeEach
    void setUp(){
        logic = new JSONLogic();
    }

    @ParameterizedTest
    @MethodSource("courseProvider")
    void courseUnitConstructorTest(JsonObject courseInput) {

        String name;
        JsonObject nameObj = courseInput.get("name").getAsJsonObject();

        if (nameObj.get("fi") == null) {
            name = nameObj.get("en").getAsString();
        } else {
            name = nameObj.get("fi").getAsString();
        }

        String code = courseInput.get("code").getAsString();
        String id = courseInput.get("groupId").getAsString();

        CourseUnit course = new CourseUnit(courseInput);

        assertEquals(name, course.getName());
        assertEquals(code, course.getCode());
        assertEquals(id, course.getId());
        assertFalse(course.isCompleted());

    }

    @ParameterizedTest
    @MethodSource("courseProvider")
    void courseSetCompleted(JsonObject courseInput){
        CourseUnit course = new CourseUnit(courseInput);
        assertFalse(course.isCompleted());
        course.setCompleted();
        assertTrue(course.isCompleted());
    }

    @ParameterizedTest
    @MethodSource("courseProvider")
    void courseSetGrade(JsonObject courseInput){
        CourseUnit course = new CourseUnit(courseInput);

        course.setGrade(6);
        assertEquals(0,course.getGrade());

        course.setGrade(3);
        assertEquals(3,course.getGrade());

        course.setGrade(-1);
        assertEquals(3,course.getGrade());

        course.setGrade(0);
        assertEquals(0,course.getGrade());

    }

    @ParameterizedTest
    @MethodSource("courseProvider")
    void courseGetCreditsInt(JsonObject courseInput){
        CourseUnit course = new CourseUnit(courseInput);

        if(course.minCredits > course.maxCredits){
            assertEquals(course.minCredits, course.getCreditsInt());
        }else if(course.minCredits == course.maxCredits){
            assertEquals(course.minCredits, course.getCreditsInt());
            assertEquals(course.maxCredits, course.getCreditsInt());

        }else{
            assertEquals(course.maxCredits, course.getCreditsInt());
        }
    }

    @ParameterizedTest
    @MethodSource("courseProvider")
    void courseSetParent(JsonObject courseInput) throws IOException {

        // Parent doesnt matter, but this is Logopedian yhteiset opinnot
        String url = "https://sis-tuni.funidata.fi/kori/api/modules/by-group-id?groupId=otm-905f0726-c6c4-444c-8d04-c1c59007906e&universityId=tuni-university-root-id";
        JsonObject parentObj = logic.requestJsonObjectFromUrl(url);
        StudyModule module = new StudyModule(parentObj);

        CourseUnit course = new CourseUnit(courseInput);

        assertNull(course.getParent());

        course.setParent(module);
        assertEquals(module, course.getParent());

    }


    @ParameterizedTest
    @MethodSource("courseProvider")
    void correctGetCreditsPrint(JsonObject courseInput){

        CourseUnit course = new CourseUnit(courseInput);
        JsonObject credits = courseInput.get("credits").getAsJsonObject();

        String minCredits;
        String maxCredits;

        if(!credits.get("max").isJsonNull()){
            maxCredits = credits.get("max").getAsString();
        }else{
            maxCredits = "0";
        }
        if (!credits.get("min").isJsonNull()){
            minCredits = credits.get("min").getAsString();
        }else{
            minCredits = "0";
        }
        if(Objects.equals(maxCredits, minCredits)){
            assertEquals(maxCredits,course.getCredits());
            assertEquals(minCredits,course.getCredits());

        }else{
            assertEquals(minCredits + "-" + maxCredits,course.getCredits() );

        }

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