package fi.tuni.prog3.sisu;


import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import javafx.util.Pair;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class JSONLogic {

    // Reads from the SISU API. Finds the given degreeprogramme by it's ID.
    // Prints out the degreeprogramme's submodules and courses etc in a nice way

    // TODO: Store person's degree choice to his/her data JSON
    // TODO: Create and implement a JSON system for persons.
    // TODO: Add proper Exceptionchecks!


    // Adds every student to the students.json file.
    // Is calles when the program closes. "Saving" the made changes
    public void studentsToJson(ArrayList students){
        GsonBuilder builder = new GsonBuilder().setPrettyPrinting();
        Gson gson = builder.create();

        try(FileWriter writer = new FileWriter("students")){

            gson.toJson(students, writer);


        }catch (IOException e){
            e.printStackTrace();
        }
    }

    // Reads the students json file and converts every student to a Student class.
    // Student classes can be easily edited from the GUI.
    public void studentsFromJsonToClass(){
        try{
            Gson gson = new Gson();

            Reader reader = Files.newBufferedReader(Paths.get("students"));
            List<studenttest> students = gson.fromJson(reader, new TypeToken<List<studenttest>>() {}.getType());

            // Test print of the first two students
            for (int i = 0; i < 2; i++) {
                System.out.println(students.get(i).getName() + " - " + students.get(i).getStudentNumber());

            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Reads the given degreeprogramme's information from the SISU api.
    // Returns null if no program with given id is found.
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

                // For testin all API data
                //readAPIRec(rootobj.getAsJsonObject(),1, new DegreeProgramme(rootobj));

            }

        }

        degreeProgramme.print();
        return degreeProgramme;

    }

    // Used for test printing during readAPIRec
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
    public DegreeProgramme readAPIRec(JsonObject rootobj, int indent, Module parent) throws IOException {

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
            return newDegreeProgramme;

        }


        if(type.equals("StudyModule")){
            JsonObject ruleJsonObject = rootobj.get("rule").getAsJsonObject();

            //getName(rootobj,indent, "StudyModule", parent);

            StudyModule studyModule = new StudyModule(rootobj);

            studyModule.setParent(parent.getJsonElement());
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

            course.setParent(parent.getJsonElement());
            parent.addChild(course);

            //getName(courseUnit,indent, "CourseUnit",parent);


        }


        if(type.equals("CreditsRule")){
            JsonObject ruleJsonObject = rootobj.get("rule").getAsJsonObject();
            readAPIRec(ruleJsonObject,indent,parent);
        }


        if(type.equals("CompositeRule")){

            JsonArray compositeRules = rootobj.get("rules").getAsJsonArray();

            // Jos täytyy valita esim Tieto- tai sähkötekniikan väliltä

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
        return null;
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
        DegreeProgramme degreeProgramme = logic.readAPIData("otm-d729cfc3-97ad-467f-86b7-b6729c496c82");

        // Esimerkkikeissi
//        ArrayList<studenttest> students = new ArrayList<>();
//        studenttest testiopiskelija = new studenttest();
//        testiopiskelija.setEndYear(2025);
//        testiopiskelija.setStartYear(2020);
//        testiopiskelija.setName("Aapo Kärki");
//        testiopiskelija.setStudentNumber("H292001");
//
//        studenttest testiopiskelija1 = new studenttest();
//        testiopiskelija1.setEndYear(2025);
//        testiopiskelija1.setStartYear(2020);
//        testiopiskelija1.setName("Kasperi Kouri");
//        testiopiskelija1.setStudentNumber("H292123");
//
//        //If degreeprogramme compositerule require max on 1
//
//
////        DegreeProgramme degreeProgramme =
////                new DegreeProgramme(logic.requestJsonElementFromURL("https://sis-tuni.funidata.fi/kori/api/modules/otm-4d4c4575-a5ae-427e-a860-2f168ad4e8ba"));
//
//        testiopiskelija.setDegreeProgramme(degreeProgramme);
//        testiopiskelija1.setDegreeProgramme(degreeProgramme);
//        students.add(testiopiskelija1);
//        students.add(testiopiskelija);


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

//        logic.studentsToJson(students);
//        logic.studentsFromJsonToClass();

    }

}