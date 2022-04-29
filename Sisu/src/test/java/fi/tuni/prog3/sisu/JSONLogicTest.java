package fi.tuni.prog3.sisu;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Stream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the functions and methods of JSONLogic.class
 * Checks that saving to students.json is indeed correct.
 */
class JSONLogicTest {

    JSONLogic logic;

    @BeforeEach
    void setUp(){
        logic = new JSONLogic();
    }



    @ParameterizedTest
    @MethodSource("studentProvider")
    void studentsJsonReadWriteCorrectly(Student student) throws IOException {

        if(Files.deleteIfExists(Paths.get("studentCopy"))){
            System.err.println("Copyfile was still here. Now it is deleted");
        }

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
        File src1 = new File("students");
        assertTrue(FileUtils.contentEquals(src1, copy));


        if(Files.deleteIfExists(Paths.get("studentCopy"))){
            System.err.println("Copyfile deleted");
        }else{
            System.err.println("Failed to delete copyfile");
        }
    }

    // When there is no mandatory selection of studymodules,
    // readAPIRec and readAPIData should returns the identical degreeprogramme
    @Test
    void readAPIRecRegular() throws IOException {
        String url = "https://sis-tuni.funidata.fi/kori/api/modules/by-group-id?groupId=uta-tohjelma-1777&universityId=tuni-university-root-id";

        DegreeProgramme logopedia = logic.readAPIData("uta-tohjelma-1777",null);
        JsonObject logopediaObj = logic.requestJsonObjectFromUrl(url);

        DegreeProgramme logopediaRec = logic.readAPIRec(logopediaObj,new DegreeProgramme(logopediaObj));


        // Ignores complex Json and parents
        assertThat(logopedia)
                .usingRecursiveComparison()
                .ignoringFieldsMatchingRegexes("parent.*",".*JsonObj")
                .isEqualTo(logopediaRec);

    }

    // When there is mandatory selection of studymodules,
    // readAPIRec and readAPIData should NOT return identical degreeprogrammes
    @Test
    void readAPIRecStudyModuleSelection() throws IOException {
        String url = "https://sis-tuni.funidata.fi/kori/api/modules/by-group-id?groupId=otm-b994335e-8759-4d7e-b3bf-ae505fd3935e&universityId=tuni-university-root-id";

        DegreeProgramme tst = logic.readAPIData("otm-fa02a1e7-4fe1-43e3-818b-810d8e723531","otm-b994335e-8759-4d7e-b3bf-ae505fd3935e");
        JsonObject tstObj = logic.requestJsonObjectFromUrl(url);

        DegreeProgramme logopediaRec = logic.readAPIRec(tstObj,new DegreeProgramme(tstObj));

        // Ignores complex Json
        assertThat(tst)
                .usingRecursiveComparison()
                .ignoringFieldsMatchingRegexes(".parent.",".*JsonObj")
                .isNotEqualTo(logopediaRec);

    }


    @Test
    void readAPIDataRegular() throws IOException {
        DegreeProgramme logopedia = logic.readAPIData("uta-tohjelma-1777",null);

        int expectedStudyModulesSize = 5;
        assertEquals(expectedStudyModulesSize,logopedia.getStudyModules().size());

        ArrayList<String> studyModuleNames = new ArrayList<>();
        studyModuleNames.add("otm-905f0726-c6c4-444c-8d04-c1c59007906e");
        studyModuleNames.add("uta-ok-ykoodi-41113");
        studyModuleNames.add("uta-ok-ykoodi-31011");
        studyModuleNames.add("otm-7e1327c8-46bd-458f-8d71-dc4f0de8ee97");
        studyModuleNames.add("otm-35759bcb-4d04-4302-ac8b-2deccc0cd780");

        for (int i = 0 ; i < 5 ; i++){
            assertEquals(studyModuleNames.get(i)
                    ,logopedia.getStudyModules().get(i).getId());
        }
    }

    @Test
    void readAPIDataStudyModuleChoice() throws IOException {
        DegreeProgramme tst = logic.readAPIData("otm-fa02a1e7-4fe1-43e3-818b-810d8e723531","otm-b994335e-8759-4d7e-b3bf-ae505fd3935e");
        int expectedStudyModulesSize = 1;
        JsonObject sahkotekniikka = logic
                .requestJsonObjectFromUrl("https://sis-tuni.funidata.fi/kori/api/modules/by-group-id?groupId=otm-b994335e-8759-4d7e-b3bf-ae505fd3935e&universityId=tuni-university-root-id");

        assertEquals(expectedStudyModulesSize,tst.getStudyModules().size());
        assertEquals("Sähkötekniikka", tst.getStudyModules().get(0).getName());
        assertEquals(sahkotekniikka,tst.getStudyModules().get(0).getJsonObject());
    }

    @Test
    void readAPIDataIOException(){

        assertThrows(IOException.class, () ->{
            logic.readAPIData(null,"otm-b994335e-8759-4d7e-b3bf-ae505fd3935e");
        });

    }


    @Test
    void getAllDegreeProgrammes() throws IOException {
        Map<String,String> degreeProgrammes = logic.getAllDegreeProgrammes();
        String sURL = "https://sis-tuni.funidata.fi/kori/api/module-search?curriculumPeriodId=uta-lvv-2021&universityId=tuni-university-root-id&moduleType=DegreeProgramme&limit=1000";
        JsonObject rootobj = logic.requestJsonObjectFromUrl(sURL);
        JsonArray degreeProgrammesJsonArray = rootobj.get("searchResults").getAsJsonArray();

        // In total 270 degreeProgrammes expected
        assertEquals(270,degreeProgrammes.size(),degreeProgrammesJsonArray.size());
    }

    @Test
    void getStudyModuleSelectionNull() throws IOException {
        String businessBachelors = "uta-tohjelma-1684";
        Map<String,String> studyModules = logic.getStudyModuleSelection(businessBachelors);

        // DegreeProgramme has no studyModules to choose from
        assertNull(studyModules);
    }

    @Test
    void getStudyModuleSelectionCorrect() throws IOException {
        String languageBachelors = "otm-d8575e77-1ee3-48a6-b3c1-4acb30c146ce";
        Map<String,String> studyModules = logic.getStudyModuleSelection(languageBachelors);
        int expectedAmountOfStudyModules = 5;

        // DegreeProgramme has 5 studyModules to choose from (languages)
        assertEquals(expectedAmountOfStudyModules, studyModules.size());
    }


    @Test
    void requestCorrectUrl() throws IOException {
        String groupId1 = "uta-tohjelma-1685";
        String groupId2 = "uta-tohjelma-1761";

        String url1 = "https://sis-tuni.funidata.fi/kori/api/modules/by-group-id?groupId=uta-tohjelma-1685&universityId=tuni-university-root-id";
        String url2 = "https://sis-tuni.funidata.fi/kori/api/modules/by-group-id?groupId=uta-tohjelma-1761&universityId=tuni-university-root-id";

        assertEquals(groupId1, logic.requestJsonObjectFromUrl(url1).get("groupId").getAsString());
        assertEquals(groupId2, logic.requestJsonObjectFromUrl(url2).get("groupId").getAsString());

    }

    @Test
    void requestFalseUrl(){
        String falseUrl1 = "asd";
        String falseUrl2 = "https://sis-tuni.fundata.fi/kori/api/modules/otm-24b4aa6c-4533-40a8-8189-a51d83f5cc2b";

        assertThrows(IOException.class, () ->{
            logic.requestJsonObjectFromUrl(falseUrl1);

        });
        assertThrows(IOException.class, () ->{
            logic.requestJsonObjectFromUrl(falseUrl2);

        });
    }

    static Stream<Arguments> studentProvider() throws IOException {
        JSONLogic logic = new JSONLogic();

        Student testiteppo = new Student();
        testiteppo.setDegreeProgrammeId("otm-d8575e77-1ee3-48a6-b3c1-4acb30c146ce");
        testiteppo.setMandatoryStudyModuleId("otm-db8be475-b468-47d1-a2c1-b5d72da59bd7");
        testiteppo.setStudentNumber("123testi321");
        testiteppo.setName("Testi Teppo");
        testiteppo.setDegreeProgramme(logic.readAPIData("otm-d8575e77-1ee3-48a6-b3c1-4acb30c146ce","otm-db8be475-b468-47d1-a2c1-b5d72da59bd7"));

        Student testituula = new Student();
        testituula.setDegreeProgrammeId("uta-tohjelma-1761");
        testituula.setMandatoryStudyModuleId(null);
        testituula.setStudentNumber("456testi654");
        testituula.setName("Testi Tuula");
        testituula.setDegreeProgramme(logic.readAPIData("uta-tohjelma-1761",null));

        return Stream.of(
                Arguments.arguments(testiteppo),
                Arguments.arguments(testituula)
        );
    }
}