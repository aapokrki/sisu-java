package fi.tuni.prog3.sisu;


import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO: Store person's degree choice to his/her data JSON
// TODO: Create and implement a JSON system for persons.
// TODO: Add proper Exceptionchecks!

/**
 * Handles everything related to JSON and pulling data from the SISU API
 */
public class JSONLogic {


    /**
     * Converts the given students to a jsonfile named students.
     * @param students list of all the students
     */
    public void studentsToJson(ArrayList<Student> students){
        GsonBuilder builder = new GsonBuilder().setPrettyPrinting();
        Gson gson = builder.create();

        try(FileWriter writer = new FileWriter("students")){

            gson.toJson(students, writer);


        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Reads the students.json file and converts all students to a Map<studentnumber, Student>
     * format, where handling data is easier.
     * @return map of all students in the students.json
     */
    public Map<String, Student> studentsFromJsonToClass(){
        Map<String, Student> studentMap = null;
        try{

            studentMap = new HashMap<>();

            Reader reader = Files.newBufferedReader(Paths.get("students"));
            List<Student> students = new Gson().fromJson(reader, new TypeToken<List<Student>>(){}.getType());

            // Test print of the first two students


            for (int i = 0; i < students.size(); i++) {
                studentMap.put(students.get(i).getStudentNumber(), students.get(i));
            }

            // test print
//            for (Map.Entry<String,Student> entry : studentMap.entrySet()){
//                System.out.println(entry.getKey() + " -- " + entry.getValue().getName());
//            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return studentMap;
    }

    // Reads the given degreeprogramme's information from the SISU api.
    // Returns null if no program with given id is found.

    /**
     * Finds the given degreeprogramme from the SISU API and calls readApiRec to read and store it's data
     * to a Degreeprogramme object
     * @param inputDegreeProgramme groupId of wanted degreeprogramme
     * @return Degreeprogramme from the api in a class format
     * @throws IOException //TODO change to better
     */
    public DegreeProgramme readAPIData(String inputDegreeProgramme) throws IOException {

        String sURL = "https://sis-tuni.funidata.fi/kori/api/module-search?curriculumPeriodId=uta-lvv-2021&universityId=tuni-university-root-id&moduleType=DegreeProgramme&limit=1000";

        JsonObject rootobj = requestJsonRootObjectFromURL(sURL);
        DegreeProgramme degreeProgramme = null;
        JsonArray programmes = rootobj.get("searchResults").getAsJsonArray();

        System.err.println("rec");
        for (int j = 0 ; j < programmes.size() ; j++){

            // jsonelements in the big cataloque
            JsonElement degreeProgrammeJE = programmes.get(j);
            String degreeProgrammeGroupId = degreeProgrammeJE.getAsJsonObject().get("id").getAsString();

            if(degreeProgrammeGroupId.equals(inputDegreeProgramme)){
                System.err.println("Uusi degreeprogramme");
                String degreeProgrammeURL = "https://sis-tuni.funidata.fi/kori/api/modules/" + degreeProgrammeGroupId;
                rootobj = requestJsonRootObjectFromURL(degreeProgrammeURL);

                degreeProgramme = readAPIRec(rootobj,1, new DegreeProgramme(rootobj));

                // For test reading all API data
                //readAPIRec(rootobj.getAsJsonObject(),1, new DegreeProgramme(rootobj));

            }

        }

        //Testprint of stored Degreeprogramme
        //degreeProgramme.print();
        return degreeProgramme;

    }




    public void getName(JsonObject obj, int indent, String type, Module parent){

        try {
            if(obj.get("name").getAsJsonObject().get("fi") == null){

                String name = obj.get("name").getAsJsonObject().get("en").getAsString();
                System.out.println( "-".repeat(indent) + name + type + ", Parent = " + parent.getCode());

            }else{
                String name = obj.get("name").getAsJsonObject().get("fi").getAsString();
                System.out.println( "-".repeat(indent)+name + " - "+type + ", Parent = " + parent.getCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("PROBLEM WITH " + type);
        }
    }

    //Goes through the api recursively object by object

    /**
     * Reads the given degreeprogramme's information from the SISU api
     * Advances through the studymodules and courses recursively, maintaining hierarchy.
     * @param rootobj rootobj - JsonObject which is about to be handled
     * @param indent helper parameter for testprinting of obj hierarchy
     * @param parent the previous rootobj - where to store current rootobj's data etc.
     * @return Degreeprogramme in a neat class format
     * @throws IOException //TODO Change to better
     */
    public DegreeProgramme readAPIRec(JsonObject rootobj, int indent, Module parent) throws IOException {


        String type = rootobj.get("type").getAsString();

        if(type.equals("DegreeProgramme")){
            //getName(rootobj,indent, "DegreeProgramme",parent);

            JsonObject ruleJsonObject = rootobj.get("rule").getAsJsonObject();
            DegreeProgramme newDegreeProgramme = new DegreeProgramme(rootobj);
            readAPIRec(ruleJsonObject,indent+1, newDegreeProgramme);

            // newDegreeprogramme filled with it's studymodules and courses
            return newDegreeProgramme;

        }


        if(type.equals("StudyModule")){
            //getName(rootobj,indent, "StudyModule", parent);

            JsonObject ruleJsonObject = rootobj.get("rule").getAsJsonObject();
            StudyModule studyModule = new StudyModule(rootobj);

            studyModule.setParent(parent.getJsonElement());
            parent.addChild(studyModule);

            readAPIRec(ruleJsonObject,indent+1,studyModule);
        }

        // Courses are the final point od the recursion.
        // Add course to parent
        if(type.equals("CourseUnitRule")){

            String courseUnitGroupId= rootobj.get("courseUnitGroupId").getAsString();
            String courseUnitURL = "https://sis-tuni.funidata.fi/kori/api/course-units/by-group-id?groupId="+ courseUnitGroupId+"&universityId=tuni-university-root-id";

            JsonObject courseUnit = requestJsonElementFromURL(courseUnitURL).getAsJsonArray().get(0).getAsJsonObject();
            CourseUnit course = new CourseUnit(courseUnit);

            course.setParent(parent.getJsonElement());
            parent.addChild(course);

            //getName(courseUnit,indent, "CourseUnit",parent);
        }

        // Rule of how many credits can or must be selected in total
        if(type.equals("CreditsRule")){
            JsonObject ruleJsonObject = rootobj.get("rule").getAsJsonObject();
            readAPIRec(ruleJsonObject,indent,parent);
        }


        // Rule of how many of the following studymodules or courses can be selected
        // eg. Choosing between Tietotekniikka and Sähkötekniikka in the degreeprogramme
        if(type.equals("CompositeRule")){

            JsonArray compositeRules = rootobj.get("rules").getAsJsonArray();

            for (int i = 0 ; i < compositeRules.size() ; ++i){
                JsonObject rule = compositeRules.get(i).getAsJsonObject();
                readAPIRec(rule,indent,parent);
            }

        }

        if (type.equals("ModuleRule")){
            String moduleGroupId = rootobj.get("moduleGroupId").getAsString();

            //String moduleURL = "https://sis-tuni.funidata.fi/kori/api/modules/" + moduleGroupId;
            String moduleURL = "https://sis-tuni.funidata.fi/kori/api/modules/by-group-id?groupId="+ moduleGroupId +"&universityId=tuni-university-root-id";

            //System.out.println(moduleURL);
            JsonElement module = requestJsonElementFromURL(moduleURL);
            JsonObject moduleObj = module.getAsJsonArray().get(0).getAsJsonObject();
            readAPIRec(moduleObj, indent+1,parent);

        }

        //TODO Check Groupingmodule and rules for additional features

        // no Degreeprogramme to be found
        return null;
    }


    /**
     * Returns a JsonElement from the given URL
     * @param sURL given url to the JsonElement
     * @return the read JsonElement
     * @throws IOException //TODO create proper
     */
    public static JsonElement requestJsonElementFromURL(String sURL) throws IOException {
        URL url = new URL(sURL);
        URLConnection request = url.openConnection();
        request.connect();
        JsonParser jp = new JsonParser(); //from gson
        JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent())); //Convert the input stream to a json element

        return root;

    }

    /**
     * Returns a JsonObject from the given URL
     * @param sURL given url to the JsonObject
     * @return the read JsonObject
     * @throws IOException //TODO create proper
     */
    public JsonObject requestJsonRootObjectFromURL(String sURL) throws IOException {

        URL url = new URL(sURL);
        URLConnection request = url.openConnection();
        request.connect();
        JsonParser jp = new JsonParser(); //from gson
        JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent())); //Convert the input stream to a json element
        JsonObject rootobj = root.getAsJsonObject(); //May be an array, may be an object.

        return rootobj;

    }

    // Main function to test the program.
    // TODO Delete before final
    public static void main(String[] args) throws IOException {
        JSONLogic logic = new JSONLogic();


        //logic.getDegreeProgrammeClass(logic.requestJsonElementFromURL("https://sis-tuni.funidata.fi/kori/api/modules/otm-4d4c4575-a5ae-427e-a860-2f168ad4e8ba"));
        DegreeProgramme degreeProgramme = logic.readAPIData("otm-d729cfc3-97ad-467f-86b7-b6729c496c82");

        //create two students
        Student aapo = new Student();
        aapo.setName("Aapo");
        aapo.setStudentNumber("H292001");
        aapo.setStartYear(2020);
        aapo.setEndYear(2025);
        aapo.setDegreeProgramme(degreeProgramme);

        Student kappe = new Student();
        kappe.setName("Kasperi");
        kappe.setStudentNumber("H123123");
        kappe.setStartYear(2020);
        kappe.setEndYear(2025);
        kappe.setDegreeProgramme(degreeProgramme);

        // Add to arraylist
        ArrayList<Student> studentsList = new ArrayList<>();
        studentsList.add(aapo);
        studentsList.add(kappe);

        // Demonstration of long term json storage of students

        // from arraylist, create students.json
        logic.studentsToJson(studentsList);

        // from students.json, create map structure for all students
        Map<String, Student> students = logic.studentsFromJsonToClass();

        //System.out.println(students.size());
        // testprint of students read from json and stored to student objects
        students.forEach((k, v) -> {
            System.out.println(k + " -- " + v.getName() + " -- " +  v);
        });
        //logic.studentsToJson((ArrayList<Student>) students.values());

    }

}