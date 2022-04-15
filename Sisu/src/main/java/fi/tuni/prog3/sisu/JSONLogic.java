package fi.tuni.prog3.sisu;


import com.google.gson.*;
import javafx.util.Pair;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


public class JSONLogic {


    public ArrayList<DegreeProgramme> degreeProgrammeArrayList;

    // Reads from the SISU API. Finds the given degreeprogramme by it's ID.
    // Prints out the degreeprogramme's submodules and courses etc in a nice way

    // TODO: Store person's degree choice to his/her data JSON
    // TODO: Create and implement a JSON system for persons.
    // TODO: Add proper Exceptionchecks!


    // Gsonin gsonbuilderilla luokasta automaattisesti Json muotoon
    public void createStudent(studenttest student){
        GsonBuilder builder = new GsonBuilder().setPrettyPrinting();
        Gson gson = builder.create();

        try(FileWriter writer = new FileWriter("testiopiskelija")){

            gson.toJson(student, writer);


        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void readPersonJson(){

    }

    // Sets to read
    public void readAPIData(String inputDegreeProgramme) throws IOException {

        String sURL = "https://sis-tuni.funidata.fi/kori/api/module-search?curriculumPeriodId=uta-lvv-2021&universityId=tuni-university-root-id&moduleType=DegreeProgramme&limit=1000";

        JsonObject rootobj = requestJsonRootObjectFromURL(sURL);

        JsonArray programmes = rootobj.get("searchResults").getAsJsonArray();


        degreeProgrammeArrayList = new ArrayList<>();

//        for (int i = 0 ; i < 0 ; i++){
//            JsonElement degreeProgrammeJE = programmes.get(i);
//            String degreeProgrammeGroupId = degreeProgrammeJE.getAsJsonObject().get("id").getAsString();
//            String groupIdURL = "https://sis-tuni.funidata.fi/kori/api/modules/" + degreeProgrammeGroupId;
//            rootobj = requestJsonRootObjectFromURL(groupIdURL);
//            DegreeProgramme degreeProgramme = new DegreeProgramme(degreeProgrammeJE);
//            degreeProgrammeArrayList.add(degreeProgramme);
//            System.out.println("normal " + degreeProgramme.getName());
//        }

        System.err.println("rec");
        for (int j = 0 ; j < programmes.size() ; j++){

            // jsonelements in the big cataloque
            JsonElement degreeProgrammeJE = programmes.get(j);
            String degreeProgrammeGroupId = degreeProgrammeJE.getAsJsonObject().get("id").getAsString();

            if(degreeProgrammeGroupId.equals(inputDegreeProgramme)){
                System.err.println("Uusi degreeprogramme");
                String degreeProgrammeURL = "https://sis-tuni.funidata.fi/kori/api/modules/" + degreeProgrammeGroupId;
                rootobj = requestJsonRootObjectFromURL(degreeProgrammeURL);

                readAPIRec(rootobj,1, new DegreeProgramme(rootobj));

                // For testin all API data
                //readAPIRec(rootobj.getAsJsonObject(),1, new DegreeProgramme(rootobj));


            }


        }


    }


    public DegreeProgramme getDegreeProgrammeClass(JsonElement degreeProgramme) throws IOException {

        return null;
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
    public void readAPIRec(JsonObject rootobj, int indent, Module parent) throws IOException {

        //System.out.println("parent = " + parent.getId());

        String type = rootobj.get("type").getAsString();
        //System.out.println(type);

        if(type.equals("DegreeProgramme")){

            JsonObject ruleJsonObject = rootobj.get("rule").getAsJsonObject();

            //getName(rootobj,indent, "DegreeProgramme",parent);

            //degreeProgrammeArrayList.add(newDegreeProgramme);
            DegreeProgramme newDegreeProgramme = new DegreeProgramme(rootobj);

            readAPIRec(ruleJsonObject,indent+1, newDegreeProgramme);

            // Tässä on nyt se tutkinto-ohjelma
            newDegreeProgramme.print();

        }


        if(type.equals("StudyModule")){
            JsonObject ruleJsonObject = rootobj.get("rule").getAsJsonObject();

            //getName(rootobj,indent, "StudyModule", parent);

            StudyModule studyModule = new StudyModule(rootobj);


            parent.addChild(studyModule);


            readAPIRec(ruleJsonObject,indent+1,studyModule);
        }

        //Endpoint. The final point.
        if(type.equals("CourseUnitRule")){

            String courseUnitGroupId= rootobj.get("courseUnitGroupId").getAsString();
            //System.out.println(courseUnitGroupId);
            String courseUnitURL = "https://sis-tuni.funidata.fi/kori/api/course-units/by-group-id?groupId="+ courseUnitGroupId+"&universityId=tuni-university-root-id";

            JsonObject courseUnit = requestJsonElementFromURL(courseUnitURL).getAsJsonArray().get(0).getAsJsonObject();
            CourseUnit course = new CourseUnit(courseUnit);

            parent.addChild(course);

            //getName(courseUnit,indent, "CourseUnit",parent);


        }


        if(type.equals("CreditsRule")){
            JsonObject ruleJsonObject = rootobj.get("rule").getAsJsonObject();
            readAPIRec(ruleJsonObject,indent,parent);
        }


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



        //Strange thing. idk about this
//        if(type.equals("GroupingModule")){
//            JsonObject ruleJsonObject = rootobj.get("rule").getAsJsonObject();
//
//            try {
//                if(rootobj.get("name").getAsJsonObject().get("fi") == null){
//                    String name = rootobj.getAsJsonObject().get("name").getAsJsonObject().get("en").getAsString();
//                    System.out.println( "-".repeat(indent)+name+ " GROUPINGMODULE");
//                }else{
//                    String name = rootobj.getAsJsonObject().get("name").getAsJsonObject().get("fi").getAsString();
//                    System.out.println( "-".repeat(indent)+name + " GROUPINGMODULE");
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//                System.out.println("PROBLEM WITH GROUPINGMODULE");
//            }
//            readAPIRec(ruleJsonObject,indent+1);
//        }

    }

    // Returns a JsonElement from the given URL
    public static JsonElement requestJsonElementFromURL(String sURL) throws IOException {
        URL url = new URL(sURL);
        URLConnection request = url.openConnection();
        request.connect();
        JsonParser jp = new JsonParser(); //from gson
        JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent())); //Convert the input stream to a json element

        return root;

    }

    // Returns a JsonObject from the given URL
    public JsonObject requestJsonRootObjectFromURL(String sURL) throws IOException {

        URL url = new URL(sURL);
        URLConnection request = url.openConnection();
        request.connect();
        JsonParser jp = new JsonParser(); //from gson
        JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent())); //Convert the input stream to a json element
        JsonObject rootobj = root.getAsJsonObject(); //May be an array, may be an object.

        return rootobj;

    }

    //A stand-in function to test the program
    public static void main(String[] args) throws IOException {
        JSONLogic logic = new JSONLogic();


        //logic.getDegreeProgrammeClass(logic.requestJsonElementFromURL("https://sis-tuni.funidata.fi/kori/api/modules/otm-4d4c4575-a5ae-427e-a860-2f168ad4e8ba"));
        logic.readAPIData("otm-d729cfc3-97ad-467f-86b7-b6729c496c82");

        // Esimerkkikeissi
//        Student testiopiskelija = new Student();
//        testiopiskelija.setEndYear(2025);
//        testiopiskelija.setStartYear(2020);
//        testiopiskelija.setName("testiopiskelija");
//        testiopiskelija.setStudentNumber("H292001");

//        DegreeProgramme degreeProgramme =
//                new DegreeProgramme(logic.requestJsonElementFromURL("https://sis-tuni.funidata.fi/kori/api/modules/otm-4d4c4575-a5ae-427e-a860-2f168ad4e8ba"));
//
//        testiopiskelija.setDegreeProgramme(degreeProgramme);
//
//
//        String courseUnitURL1 = "https://sis-tuni.funidata.fi/kori/api/course-units/by-group-id?groupId=tut-cu-g-48277&universityId=tuni-university-root-id";
//        String courseUnitURL2 = "https://sis-tuni.funidata.fi/kori/api/course-units/by-group-id?groupId=tut-cu-g-48278&universityId=tuni-university-root-id";
//        String courseUnitURL3 = "https://sis-tuni.funidata.fi/kori/api/course-units/by-group-id?groupId=tut-cu-g-45510&universityId=tuni-university-root-id";
//
//        CourseUnit testCourse1 = new CourseUnit(requestJsonElementFromURL(courseUnitURL1));
//        testCourse1.setCompleted(Boolean.TRUE);
//        testCourse1.setGrade(5);
//
//        CourseUnit testCourse2 = new CourseUnit(requestJsonElementFromURL(courseUnitURL2));
//        testCourse2.setCompleted(Boolean.TRUE);
//        testCourse2.setGrade(2);
//
//        testiopiskelija.addCompletedCourse(testCourse1);
//        testiopiskelija.addCompletedCourse(testCourse2);

        //logic.createStudent(testiopiskelija);
    }

}