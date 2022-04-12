package fi.tuni.prog3.sisu;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
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

            JsonElement degreeProgrammeJE = programmes.get(j);
            String degreeProgrammeGroupId = degreeProgrammeJE.getAsJsonObject().get("id").getAsString();

            if(degreeProgrammeGroupId.equals(inputDegreeProgramme)){
                System.err.println("Uusi degreeprogramme");
                String degreeProgrammeURL = "https://sis-tuni.funidata.fi/kori/api/modules/" + degreeProgrammeGroupId;
                rootobj = requestJsonRootObjectFromURL(degreeProgrammeURL);

                readAPIRec(rootobj,1);
            }


        }

    }

    //Goes through the api recursively object by object
    public void readAPIRec(JsonObject rootobj, int indent) throws IOException {


        String type = rootobj.get("type").getAsString();
        //System.out.println(type);

        if(type.equals("DegreeProgramme")){

            JsonObject ruleJsonObject = rootobj.get("rule").getAsJsonObject();
            try {
                if(rootobj.get("name").getAsJsonObject().get("fi") == null){
                    String name = rootobj.getAsJsonObject().get("name").getAsJsonObject().get("en").getAsString();
                    System.out.println( "-".repeat(indent) +name);
                }else{
                    String name = rootobj.getAsJsonObject().get("name").getAsJsonObject().get("fi").getAsString();
                    System.out.println( "-".repeat(indent)+name);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("PROBLEM WITH DEGREEPROGRAMME");
            }

//            DegreeProgramme newDegreeProgramme = new DegreeProgramme(rootobj);
//            degreeProgrammeArrayList.add(newDegreeProgramme);


            readAPIRec(ruleJsonObject,indent+1);
        }


        if(type.equals("StudyModule")){
            JsonObject ruleJsonObject = rootobj.get("rule").getAsJsonObject();
            try {
                if(rootobj.get("name").getAsJsonObject().get("fi") == null){
                    String name = rootobj.getAsJsonObject().get("name").getAsJsonObject().get("en").getAsString();
                    System.out.println( "-".repeat(indent)+name);
                }else{
                    String name = rootobj.getAsJsonObject().get("name").getAsJsonObject().get("fi").getAsString();
                    System.out.println( "-".repeat(indent)+name);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("PROBLEM WITH STUDYMODULE");
            }


            readAPIRec(ruleJsonObject,indent+1);
        }


        if(type.equals("CreditsRule")){
            JsonObject ruleJsonObject = rootobj.get("rule").getAsJsonObject();
            readAPIRec(ruleJsonObject,indent+1);
        }



        if(type.equals("CompositeRule")){

            JsonArray compositeRules = rootobj.get("rules").getAsJsonArray();
            for (int i = 0 ; i < compositeRules.size() ; ++i){
                JsonObject rule = compositeRules.get(i).getAsJsonObject();
                readAPIRec(rule,indent);
            }

        }

        if (type.equals("ModuleRule")){
            String moduleGroupId = rootobj.get("moduleGroupId").getAsString();

            //String moduleURL = "https://sis-tuni.funidata.fi/kori/api/modules/" + moduleGroupId;
            String moduleURL = "https://sis-tuni.funidata.fi/kori/api/modules/by-group-id?groupId="+ moduleGroupId +"&universityId=tuni-university-root-id";

            //System.out.println(moduleURL);
            JsonElement module = requestJsonElementFromURL(moduleURL);
            JsonObject moduleObj = module.getAsJsonArray().get(0).getAsJsonObject();
            readAPIRec(moduleObj, indent+1);

        }

        //Endpoint. The final point.
        if(type.equals("CourseUnitRule")){

            String courseUnitGroupId= rootobj.get("courseUnitGroupId").getAsString();
            //System.out.println(courseUnitGroupId);
            String courseUnitURL = "https://sis-tuni.funidata.fi/kori/api/course-units/by-group-id?groupId="+ courseUnitGroupId+"&universityId=tuni-university-root-id";

            JsonElement courseUnit = requestJsonElementFromURL(courseUnitURL);

            try {
                if(courseUnit.getAsJsonArray().get(0).getAsJsonObject().get("name").getAsJsonObject().get("fi") == null){
                    String courseName = courseUnit.getAsJsonArray().get(0).getAsJsonObject().get("name").getAsJsonObject().get("en").getAsString();
                    System.out.println( "-".repeat(indent)+courseName);
                }else{
                    String courseName = courseUnit.getAsJsonArray().get(0).getAsJsonObject().get("name").getAsJsonObject().get("fi").getAsString();
                    System.out.println( "-".repeat(indent)+courseName);
                }


            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("HELLO");
                System.out.println(courseUnitURL);
            }

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
    public JsonElement requestJsonElementFromURL(String sURL) throws IOException {
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
        logic.readAPIData("otm-df83fbbd-f82d-4fda-b819-78f6b2077fcb");
    }

}