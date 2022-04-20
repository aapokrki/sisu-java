package fi.tuni.prog3.sisu;


import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

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
     * to a Degreeprogramme object. Used when creating a degreeProgramme for a student.
     * @param inputDegreeProgramme groupId of wanted degreeprogramme
     * @param inputMandatoryStudyModule groupId of possible mandatosyStudyModule selection, null if no selection
     * @return Degreeprogramme from the api in a class format
     * @throws IOException //TODO change to better
     */
    public DegreeProgramme readAPIData(String inputDegreeProgramme, String inputMandatoryStudyModule) throws IOException {

        //System.err.println("Uusi degreeprogramme -- " + inputDegreeProgramme);
        String degreeProgrammeURL = "https://sis-tuni.funidata.fi/kori/api/modules/by-group-id?groupId=" + inputDegreeProgramme+ "&universityId=tuni-university-root-id";
        JsonObject rootobj = requestJsonElementFromURL(degreeProgrammeURL).getAsJsonArray().get(0).getAsJsonObject();



        DegreeProgramme degreeProgramme = readAPIRec(rootobj,1, new DegreeProgramme(rootobj));

        // if a choice of studyModule is given, the useless studymodules are removed from the degreeProgramme
        // eq. Tieto- ja Sähkötekniikka
        if(inputMandatoryStudyModule != null){

            StudyModule mandatoryStudyModule = null;
            for (StudyModule studyModule : degreeProgramme.getStudyModules()) {

                if(studyModule.getId().equals(inputMandatoryStudyModule)){

                    mandatoryStudyModule = studyModule;
                    ArrayList<StudyModule> newStudyModules = new ArrayList<>();
                    newStudyModules.add(mandatoryStudyModule);
                    degreeProgramme.setStudyModules(newStudyModules);
                }
            }

        }

        if(degreeProgramme == null){
            System.out.println("ERROR: degreeProgramme is null");
        }

        return degreeProgramme;

    }

    /**
     * Reads all degreeprogrammes for when the student chooses his/her degreeprogramme
     * @return map of all degreeprogrammes <name, id> for easy handling
     * @throws IOException
     */
    public Map<String, String> getAllDegreeProgrammes() throws IOException {

        String sURL = "https://sis-tuni.funidata.fi/kori/api/module-search?curriculumPeriodId=uta-lvv-2021&universityId=tuni-university-root-id&moduleType=DegreeProgramme&limit=1000";

        JsonObject rootobj = requestJsonRootObjectFromURL(sURL);
        JsonArray programmes = rootobj.get("searchResults").getAsJsonArray();



        Map<String, String> degreeProgrammes = new TreeMap<>();
        for (int i = 0; i < programmes.size(); i++) {



            JsonObject programme = programmes.get(i).getAsJsonObject();

            //readAPIData(programme.get("groupId").getAsString(),null);

            degreeProgrammes.put(programme.get("name").getAsString(),programme.get("groupId").getAsString());

            //System.out.println(getStudyModuleSelection(programme.get("groupId").getAsString()));
            //System.out.println(programme.get("name").getAsString() + " -- "+programme.get("groupId").getAsString());


        }

//        degreeProgrammes.forEach((k,v) ->{
//            System.out.println(k + " -- " + v);
//        });
        return degreeProgrammes;
    }

    /**
     * Reads if the given degreeprogramme has a mandatory selection for a studymodule
     * eg. Tietotekniikka and Sähkötekniikka. You have to choose one.
     * Returns a map of the studymodule choices
     * @param id of the given degreeprogramme
     * @return a map of the studymodule choices / null if no mandatory choices
     * @throws IOException
     */
    public Map<String, String> getStudyModuleSelection(String id) throws IOException {

        String url = "https://sis-tuni.funidata.fi/kori/api/modules/by-group-id?groupId="+ id +"&universityId=tuni-university-root-id";
        JsonObject degreeProgramme = requestJsonElementFromURL(url).getAsJsonArray().get(0).getAsJsonObject();

        Map<String, String> studyModuleSelection = new TreeMap<>();

        JsonObject rule = degreeProgramme.get("rule").getAsJsonObject();
        String type = rule.get("type").getAsString();
        if(type.equals("CompositeRule")){
            if(!rule.get("require").isJsonNull()){
                JsonElement min = rule.get("require").getAsJsonObject().get("min");
                JsonElement max = rule.get("require").getAsJsonObject().get("max");

                if(!min.isJsonNull() && !max.isJsonNull() && max.getAsInt() == min.getAsInt() && min.getAsInt() == 1){
                    //System.out.println(degreeProgramme.get("name"));
                    //System.out.println(min + " -- " + max);
                    JsonArray studymodules = rule.get("rules").getAsJsonArray();

                    //System.err.println(degreeProgramme.get("name"));

                    // If the SISU api is wonky and has two useless ComporiteRules on top of eachother
                    JsonObject tempModule = studymodules.get(0).getAsJsonObject();
                    if(tempModule.get("type").getAsString().equals("CompositeRule")){
                        if(!tempModule.get("require").isJsonNull()){

                            JsonElement tMin = tempModule.get("require").getAsJsonObject().get("min");
                            JsonElement tMax = tempModule.get("require").getAsJsonObject().get("max");
                            if(!tMin.isJsonNull() && !tMax.isJsonNull() && tMax.getAsInt() == tMin.getAsInt() && tMin.getAsInt() == 1){
                                studymodules = studymodules.get(0).getAsJsonObject().get("rules").getAsJsonArray();
                            }
                        }
                    }

                    // go through the studymodules that the user has to choose from
                    for (int i = 0; i < studymodules.size(); i++) {
                        if(studymodules.get(i).getAsJsonObject().get("type").getAsString().equals("ModuleRule")){
                            String moduleId = studymodules.get(i).getAsJsonObject().get("moduleGroupId").getAsString();
                            String moduleUrl = "https://sis-tuni.funidata.fi/kori/api/modules/by-group-id?groupId="+ moduleId +"&universityId=tuni-university-root-id";
                            JsonObject studyModule = requestJsonElementFromURL(moduleUrl).getAsJsonArray().get(0).getAsJsonObject();

                            //System.out.println(studyModule.get("name"));

                            studyModuleSelection.put(getName(studyModule), studyModule.get("groupId").getAsString());
                        }
                    }

                    // there are one or two odd cases where there is an empty map
                    if(!studyModuleSelection.isEmpty()){
                        return studyModuleSelection;
                    }

                }

                //System.out.println(studyModuleSelection);
            }

        }

        // if no mandatory selections are required
        return null;
    }

    public String getName(JsonObject obj){

        String name = null;
        try {
            if(obj.get("name").getAsJsonObject().get("fi") == null){

                name = obj.get("name").getAsJsonObject().get("en").getAsString();

            }else{
                name = obj.get("name").getAsJsonObject().get("fi").getAsString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return name;
    }

    public void getName(JsonObject obj, int indent, String type, Module parent){

        try {
            if(obj.get("name").getAsJsonObject().get("fi") == null){

                String name = obj.get("name").getAsJsonObject().get("en").getAsString();
                System.out.println( "-".repeat(indent) + name + obj.get("groupId"));

            }else{
                String name = obj.get("name").getAsJsonObject().get("fi").getAsString();
                System.out.println( "-".repeat(indent) + name + " -- " + obj.get("groupId"));
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
     * @return Degreeprogramme student's new degreeprogramme in a neat class format
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


        if(type.equals("StudyModule") || type.equals("GroupingModule")){
            //getName(rootobj,indent, "StudyModule", parent);

            JsonObject ruleJsonObject = rootobj.get("rule").getAsJsonObject();
            StudyModule studyModule = new StudyModule(rootobj);



            studyModule.setParent(parent);
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

            course.setParent(parent);
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
        if(type.equals("AnyModuleRule")){
//            System.out.println(parent.getName() +" -- " +parent.getId());
//
//            System.out.println("AnyModuleRule -- " + rootobj.get("localId"));

        }

        if(type.equals("GroupingModule")){
//            System.out.println(parent.getName() +" -- " +parent.getId());
//            System.out.println(rootobj);
//
//            System.out.println("GroupingModule -- " + rootobj.get("groupId"));
        }

//        if(type.equals("AnyCourseUnitRule")){
//            System.out.println(parent.getName()+" -- " +parent.getId());
//            System.out.println("AnyCourseUnitRule");
//
//
//        }
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

        DegreeProgramme tietotekniikka = logic.readAPIData("otm-fa02a1e7-4fe1-43e3-818b-810d8e723531", "otm-e4a8addd-5944-4f94-9e56-d1b51d1f22ce");
        //logic.getAllDegreeprogrammes();

        //System.out.println(degreeProgramme1.getStudyModules());

        // tiiän.. näyttää vitun scuffed
        CourseUnit signjamit = tietotekniikka.getStudyModules().get(0).getStudyModules().get(0).getCourseUnits().get(0);
        CourseUnit jokutoine = tietotekniikka.getStudyModules().get(0).getStudyModules().get(0).getCourseUnits().get(1);

        CourseUnit analyysinperuskurssi = tietotekniikka.getStudyModules().get(0).getStudyModules().get(1).getCourseUnits().get(0);


        // mutta tää toimii!
        signjamit.setCompleted();
        signjamit.setGrade(5);

        jokutoine.setCompleted();
        jokutoine.setGrade(1);

        analyysinperuskurssi.setCompleted();
        analyysinperuskurssi.setGrade(5);

        Student aapo = new Student();
        aapo.setName("Aapo");
        aapo.setStudentNumber("H292001");
        aapo.setStartYear(2020);
        aapo.setEndYear(2025);
        aapo.setDegreeProgramme(tietotekniikka);


        DegreeProgramme sähkötekniikka = logic.readAPIData("tut-dp-g-1100", null);

        Student kappe = new Student();
        kappe.setName("Kasperi");
        kappe.setStudentNumber("H292044");
        kappe.setStartYear(2020);
        kappe.setEndYear(2025);
        kappe.setDegreeProgramme(sähkötekniikka);

        // Add to arraylist
        ArrayList<Student> studentsList = new ArrayList<>();
        studentsList.add(aapo);
        studentsList.add(kappe);

         //Demonstration of long term json storage of students

        // from arraylist, create students.json
        logic.studentsToJson(studentsList);
        // from students.json, create map structure for all students
        //Map<String, Student> students = logic.studentsFromJsonToClass();



        //logic.getAllDegreeProgrammes();
//        Map<String, String> progs = logic.getAllDegreeProgrammes();
//        progs.forEach((k,v) -> {
//            try {
//                logic.readAPIData(v, null);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        });

        //logic.studentsToJson((ArrayList<Student>) students.values());

    }

}