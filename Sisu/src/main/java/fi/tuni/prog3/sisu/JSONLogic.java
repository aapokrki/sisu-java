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
 * Handles everything related to JSON and pulling data from the SISU API and students.json
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

            System.out.println("Saving student information to students.json");
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
            //JsonArray studentsJsonArray = new Gson().fromJson(reader)

            for (Student student : students) {
                studentMap.put(student.getStudentNumber(), student);
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Getting student information from students.json");
        return studentMap;
    }


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
        if(inputDegreeProgramme == null){
            throw new IOException("inputDegreeProgrammeId is null - " + inputDegreeProgramme);
        }

        String degreeProgrammeURL = "https://sis-tuni.funidata.fi/kori/api/modules/by-group-id?groupId=" + inputDegreeProgramme+ "&universityId=tuni-university-root-id";
        JsonObject rootobj = requestJsonElementFromURL(degreeProgrammeURL);

        DegreeProgramme degreeProgramme = readAPIRec(rootobj, new DegreeProgramme(rootobj));

        // If a choice of studyModule is given, the useless studymodules are removed from the degreeProgramme
        // eq. Tieto- ja Sähkötekniikka
        if(inputMandatoryStudyModule != null){

            StudyModule mandatoryStudyModule;
            for (StudyModule studyModule : degreeProgramme.getStudyModules()) {

                if(studyModule.getId().equals(inputMandatoryStudyModule)){

                    mandatoryStudyModule = studyModule;
                    ArrayList<StudyModule> newStudyModules = new ArrayList<>();
                    newStudyModules.add(mandatoryStudyModule);
                    degreeProgramme.setStudyModules(newStudyModules);
                }
            }
        }
        return degreeProgramme;

    }

    /**
     * Reads all degreeprogrammes for when the student chooses his/her degreeprogramme
     * @return map of all degreeprogrammes <name, id> for easy handling
     */
    public Map<String, String> getAllDegreeProgrammes(){

        String sURL = "https://sis-tuni.funidata.fi/kori/api/module-search?curriculumPeriodId=uta-lvv-2021&universityId=tuni-university-root-id&moduleType=DegreeProgramme&limit=1000";
        JsonObject rootobj = null;

        try {
            rootobj = requestJsonElementFromURL(sURL);
        } catch (IOException e) {
            System.err.println("Bad Url from getAllDegreeProgrammes()");
            e.printStackTrace();
        }

        assert rootobj != null;
        JsonArray programmes = rootobj.get("searchResults").getAsJsonArray();

        Map<String, String> degreeProgrammes = new TreeMap<>();
        for (int i = 0; i < programmes.size(); i++) {
            JsonObject programme = programmes.get(i).getAsJsonObject();
            degreeProgrammes.put(programme.get("name").getAsString(),programme.get("groupId").getAsString());

        }
        return degreeProgrammes;
    }

    /**
     * Reads if the given Module has a mandatory selection for a studymodule
     * eg. Tietotekniikka and Sähkötekniikka. You have to choose one.
     * Returns a map of the studymodule choices

     * @param moduleId of the given degreeprogramme
     * @return a map of the studymodule choices / null if no mandatory choices
     */
    public Map<String, String> getStudyModuleSelection(String moduleId)throws IOException{

        String url = "https://sis-tuni.funidata.fi/kori/api/modules/by-group-id?groupId="+ moduleId +"&universityId=tuni-university-root-id";
        JsonObject degreeProgramme = null;

        try {
            degreeProgramme = requestJsonElementFromURL(url);
        } catch (IOException e) {
            System.err.println("Bad url at getStudyModuleSelection()");
            e.printStackTrace();
        }

        Map<String, String> studyModuleSelection = new TreeMap<>();

        JsonObject rule = degreeProgramme.get("rule").getAsJsonObject();
        String type = rule.get("type").getAsString();

        if(type.equals("CompositeRule")){
            if(!rule.get("require").isJsonNull()){
                JsonElement min = rule.get("require").getAsJsonObject().get("min");
                JsonElement max = rule.get("require").getAsJsonObject().get("max");

                // Scenario where you have to choose exactly one studyModule
                if(!min.isJsonNull()
                        && !max.isJsonNull()
                        && max.getAsInt() == min.getAsInt()
                        && min.getAsInt() == 1){

                    JsonArray studymodules = rule.get("rules").getAsJsonArray();

                    // If the SISU api is wonky and has two ComporiteRules on top of eachother
                    JsonObject tempModule = studymodules.get(0).getAsJsonObject();
                    if(tempModule.get("type").getAsString().equals("CompositeRule")){
                        if(!tempModule.get("require").isJsonNull()){

                            min = tempModule.get("require").getAsJsonObject().get("min");
                            max = tempModule.get("require").getAsJsonObject().get("max");

                            if(!min.isJsonNull()
                                    && !max.isJsonNull()
                                    && max.getAsInt() == min.getAsInt()
                                    && min.getAsInt() == 1){

                                studymodules = studymodules.get(0).getAsJsonObject().get("rules").getAsJsonArray();
                            }
                        }
                    }

                    // Iterate through the studyModules that the user has to choose from
                    for (int i = 0; i < studymodules.size(); i++) {
                        String moduleType = studymodules.get(i).getAsJsonObject().get("type").getAsString();

                        if(moduleType.equals("ModuleRule")){
                            String studyModuleId = studymodules.get(i).getAsJsonObject().get("moduleGroupId").getAsString();
                            String moduleUrl = "https://sis-tuni.funidata.fi/kori/api/modules/by-group-id?groupId="+ studyModuleId +"&universityId=tuni-university-root-id";

                            JsonObject studyModule = null;
                            try {
                                studyModule = requestJsonElementFromURL(moduleUrl);
                            } catch (IOException e) {
                                System.err.println("Bad url at getStudyModuleSelection()");
                                e.printStackTrace();
                            }

                            studyModuleSelection.put(getName(studyModule), studyModule.get("groupId").getAsString());
                        }
                    }

                    // there are one or two odd cases where the map doesn't get filled due to wonky API
                    if(!studyModuleSelection.isEmpty()){
                        return studyModuleSelection;
                    }
                }
            }
        }
        // If no mandatory selections are required
        return null;
    }

    /**
     * Helper function to clear nasty name getting code
     * Preferably returns the finnish name, but returns the english name if there is no finnish option
     * @param obj - JsonObject of which the name is needed
     * @return The name of obj preferably in finnish
     */
    public String getName(JsonObject obj){
        String name;
        JsonElement nameObj = obj.get("name");
        if (nameObj == null){
            return "No name";
        }
        if(nameObj.getAsJsonObject().get("fi") == null){
            name = nameObj.getAsJsonObject().get("en").getAsString();
        }else{
            name = nameObj.getAsJsonObject().get("fi").getAsString();
        }
        return name;
    }


    /**
     * Reads the given degreeprogramme's information from the SISU api
     * Advances through the studymodules and courses recursively, maintaining hierarchy.
     * @param rootobj rootobj - JsonObject which is about to be handled
     * @param parent the previous rootobj - where to store current rootobj's data etc.
     * @return Degreeprogramme student's new degreeprogramme in a neat class format
     */
    public DegreeProgramme readAPIRec(JsonObject rootobj, Module parent) {


        String type = rootobj.get("type").getAsString();

        // The first stop
        if(type.equals("DegreeProgramme")){

            JsonObject ruleJsonObject = rootobj.get("rule").getAsJsonObject();
            DegreeProgramme newDegreeProgramme = new DegreeProgramme(rootobj);
            readAPIRec(ruleJsonObject, newDegreeProgramme);

            // Filled with its studymodules and courses
            return newDegreeProgramme;

        }

        // StudyModules and GroupingModules can be handled as the same
        if(type.equals("StudyModule") || type.equals("GroupingModule")){

            JsonObject ruleJsonObject = rootobj.get("rule").getAsJsonObject();
            StudyModule studyModule = new StudyModule(rootobj);

            studyModule.setParent(parent);
            parent.addChild(studyModule);

            readAPIRec(ruleJsonObject,studyModule);
        }

        // Courses are the final point of the recursion.
        if(type.equals("CourseUnitRule")){

            String courseUnitGroupId= rootobj.get("courseUnitGroupId").getAsString();
            String courseUnitURL = "https://sis-tuni.funidata.fi/kori/api/course-units/by-group-id?groupId="+ courseUnitGroupId+"&universityId=tuni-university-root-id";

//            JsonObject courseUnit = requestJsonElementFromURL(courseUnitURL).getAsJsonArray().get(0).getAsJsonObject();
            JsonObject courseUnit = null;
            try {
                courseUnit = requestJsonElementFromURL(courseUnitURL);
            } catch (IOException e) {
                System.err.println("Bad url at readAPIRec() - CourseUnitRule");
                e.printStackTrace();
            }

            CourseUnit course = new CourseUnit(courseUnit);
            course.setParent(parent);
            parent.addChild(course);

        }

        // Rule of how many credits can or must be selected in total
        if(type.equals("CreditsRule")){
            JsonObject ruleJsonObject = rootobj.get("rule").getAsJsonObject();
            readAPIRec(ruleJsonObject,parent);
        }


        // Rule of how many of the following studymodules or courses can be selected
        // eg. Choosing between Tietotekniikka and Sähkötekniikka in the degreeprogramme
        if(type.equals("CompositeRule")){

            JsonArray compositeRules = rootobj.get("rules").getAsJsonArray();

            for (int i = 0 ; i < compositeRules.size() ; ++i){
                JsonObject rule = compositeRules.get(i).getAsJsonObject();
                readAPIRec(rule,parent);
            }

        }

        if (type.equals("ModuleRule")){
            String moduleGroupId = rootobj.get("moduleGroupId").getAsString();

            String moduleURL = "https://sis-tuni.funidata.fi/kori/api/modules/by-group-id?groupId="+ moduleGroupId +"&universityId=tuni-university-root-id";

            JsonObject moduleObj = null;
            try {
                moduleObj = requestJsonElementFromURL(moduleURL);
            } catch (IOException e) {
                System.err.println("Bad url at readAPIRec() - ModuleRule");
                e.printStackTrace();
            }

            assert moduleObj != null;
            readAPIRec(moduleObj, parent);
        }

        //TODO Check Groupingmodule and rules for additional features

        /*
        if(type.equals("AnyModuleRule")){
//            System.out.println(parent.getName() +" -- " +parent.getId());
//            System.out.println("AnyModuleRule");

        }

        if(type.equals("AnyCourseUnitRule")){
//            System.out.println(parent.getName()+" -- " +parent.getId());
//            System.out.println("AnyCourseUnitRule");

        }
        */

        // no Degreeprogramme to be found
        return null;
    }


    /**
     * Returns a JsonElement from the given URL
     * @param sURL given url to the JsonElement
     * @return the read JsonElement
     * @throws IOException //TODO create proper
     */
    public JsonObject requestJsonElementFromURL(String sURL) throws IOException {

        JsonElement root;
        URLConnection request;
        try {
            URL url = new URL(sURL);
            request = url.openConnection();
            request.connect();
            JsonParser jp = new JsonParser(); //from gson
            root = jp.parse(new InputStreamReader((InputStream) request.getContent())); //Convert the input stream to a json element

        } catch (IOException e) {
            //e.printStackTrace();
            System.err.println("Bad URL at requestJsonElementFromURL");
            throw e;
        }

        if(root.isJsonArray()){
            return root.getAsJsonArray().get(0).getAsJsonObject();

        }
        return root.getAsJsonObject();

    }

    // Main function to test the program.
    // TODO Delete before final
    public static void main(String[] args) throws IOException {

    }

}