package fi.tuni.prog3.sisu;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class CourseUnitTest {

    @ParameterizedTest
    @MethodSource("courseProvider")
    void courseUnitConstructorTest(JsonObject courseInput){

        JsonObject courseObj = courseInput;
        String name;
        JsonObject nameObj = courseObj.get("name").getAsJsonObject();

        if(nameObj.get("fi") == null){
            name = nameObj.get("en").getAsString();
        }else{
            name = nameObj.get("fi").getAsString();
        }

        String code = courseObj.get("code").getAsString();
        String id = courseObj.get("groupId").getAsString();

        CourseUnit course = new CourseUnit(courseObj);

        assertEquals(name,course.getName());
        assertEquals(code,course.getCode());
        assertEquals(id,course.getId());
        assertFalse(course.isCompleted());
        course.setCompleted();
        assertTrue(course.isCompleted());
    }

    @ParameterizedTest
    @MethodSource("courseProvider")
    void correctGetCreditsPrint(JsonObject courseInput){

        JsonObject courseUnit = courseInput;
        CourseUnit course = new CourseUnit(courseInput);
        JsonObject credits = courseUnit.get("credits").getAsJsonObject();

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

        JsonObject signjamit = logic.requestJsonElementFromURL("https://sis-tuni.funidata.fi/kori/api/course-units/by-group-id?groupId=tut-cu-g-45460&universityId=tuni-university-root-id");
        JsonObject urbdev = logic.requestJsonElementFromURL("https://sis-tuni.funidata.fi/kori/api/course-units/by-group-id?groupId=otm-69aad478-104a-4653-a8db-6a45446ab525&universityId=tuni-university-root-id");


        return Stream.of(
                Arguments.arguments(signjamit),
                Arguments.arguments(urbdev)
        );
    }
}