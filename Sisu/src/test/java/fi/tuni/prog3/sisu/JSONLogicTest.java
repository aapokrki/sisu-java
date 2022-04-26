package fi.tuni.prog3.sisu;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class JSONLogicTest {


    @ParameterizedTest
    @MethodSource("studentProvider")
    void studentsJsonReadWriteCorrectly(Student student) throws IOException {

        if(Files.deleteIfExists(Paths.get("studentCopy"))){
            System.err.println("Copyfile was still here. Now it is deleted");
        }

        JSONLogic logic = new JSONLogic();

        File src = new File("students");
        File copy = new File("studentCopy");

        Files.copy(src.toPath(),copy.toPath());

        Map<String, Student> students = logic.studentsFromJsonToClass();
        ArrayList<Student> studentsArrayList = new ArrayList<>(students.values());


        studentsArrayList.add(student);
        logic.studentsToJson(studentsArrayList);

        // Added a student to students.json. Now it differs from the copy
        assertFalse(FileUtils.contentEquals(src, copy));

        // Read and remove testiteppo from students.
        students = logic.studentsFromJsonToClass();
        students.remove(student.getStudentNumber());

        // Changed info back to students
        studentsArrayList = new ArrayList<>(students.values());
        logic.studentsToJson(studentsArrayList);

        // Files should be exactly like in the beginning
        src = new File("students");
        assertTrue(FileUtils.contentEquals(src, copy));


        if(Files.deleteIfExists(Paths.get("studentCopy"))){
            System.err.println("Copyfile deleted");
        }else{
            System.err.println("Failed to delete copyfile");
        }
    }



    @Test
    void readAPIData() {
    }

    @Test
    void getAllDegreeProgrammes() {
    }

    @Test
    void getStudyModuleSelection() {

    }

    @Test
    void readAPIRec() {

    }

    @Test
    @CsvSource
    void requestCorrectUrl() throws IOException {
        JSONLogic logic = new JSONLogic();
        String groupId1 = "uta-tohjelma-1685";
        String groupId2 = "uta-tohjelma-1761";

        String url1 = "https://sis-tuni.funidata.fi/kori/api/modules/by-group-id?groupId=uta-tohjelma-1685&universityId=tuni-university-root-id";
        String url2 = "https://sis-tuni.funidata.fi/kori/api/modules/by-group-id?groupId=uta-tohjelma-1761&universityId=tuni-university-root-id";

        assertEquals(groupId1, logic.requestJsonElementFromURL(url1).get("groupId").getAsString());
        assertEquals(groupId2, logic.requestJsonElementFromURL(url2).get("groupId").getAsString());

    }

    @Test
    void requestFalseUrl(){
        JSONLogic logic = new JSONLogic();
        String falseUrl1 = "asd";
        String falseUrl2 = "https://sis-tuni.fundata.fi/kori/api/modules/otm-24b4aa6c-4533-40a8-8189-a51d83f5cc2b";

        assertThrows(IOException.class, () ->{
            logic.requestJsonElementFromURL(falseUrl1);

        });
        assertThrows(IOException.class, () ->{
            logic.requestJsonElementFromURL(falseUrl2);

        });
    }

    static Stream<Arguments> studentProvider() throws IOException {
        JSONLogic logic = new JSONLogic();

        Student testiteppo = new Student();
        testiteppo.setDegreeProgrammeId("otm-d8575e77-1ee3-48a6-b3c1-4acb30c146ce");
        testiteppo.setMandatoryStudyModuleId("otm-db8be475-b468-47d1-a2c1-b5d72da59bd7");
        testiteppo.setStudentNumber("123456");
        testiteppo.setName("Testi Teppo");
        testiteppo.setDegreeProgramme(logic.readAPIData("otm-d8575e77-1ee3-48a6-b3c1-4acb30c146ce","otm-db8be475-b468-47d1-a2c1-b5d72da59bd7"));

        Student testituula = new Student();
        testituula.setDegreeProgrammeId("uta-tohjelma-1761");
        testituula.setMandatoryStudyModuleId(null);
        testituula.setStudentNumber("654321");
        testituula.setName("Testi Tuula");
        testituula.setDegreeProgramme(logic.readAPIData("uta-tohjelma-1761",null));

        return Stream.of(
                Arguments.arguments(testiteppo),
                Arguments.arguments(testituula)
        );
    }
}