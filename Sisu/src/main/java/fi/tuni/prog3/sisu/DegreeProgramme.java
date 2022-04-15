package fi.tuni.prog3.sisu;

import com.google.gson.JsonElement;

import java.util.ArrayList;

public class DegreeProgramme extends Module{


    private transient JsonElement degreeProgramme;
    private transient JsonElement parent;

    public String name;
    public String id;
    public String code;
    public ArrayList<DegreeProgramme> degreeProgrammes;
    public ArrayList<StudyModule> studyModules;


    public DegreeProgramme(JsonElement degreeProgramme){

        this.degreeProgramme = degreeProgramme;
        this.id = degreeProgramme.getAsJsonObject().get("groupId").getAsString();
        this.code = degreeProgramme.getAsJsonObject().get("code").getAsString();
        this.studyModules = new ArrayList<>();


        // The name can be in finnish, english or both. Prefers finnish first if both are available
        try {
            if(degreeProgramme.getAsJsonObject().get("name").getAsJsonObject().get("fi") == null){

                this.name = degreeProgramme.getAsJsonObject().get("name").getAsJsonObject().get("en").getAsString();

            }else{
                this.name = degreeProgramme.getAsJsonObject().get("name").getAsJsonObject().get("fi").getAsString();
            }

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    public void setParent(JsonElement parent) {
        this.parent = parent;
    }

    public void setDegreeProgramme(JsonElement degreeProgramme) {
        this.degreeProgramme = degreeProgramme;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public JsonElement getJsonElement(){
        return degreeProgramme;
    }

    public ArrayList<StudyModule> getStudyModules() {
        return studyModules;
    }

    public void addStudyModule(StudyModule studyModule){
        studyModules.add(studyModule);
    }

    public void addDegreeProgramme(DegreeProgramme degreeProgramme){
        degreeProgrammes.add(degreeProgramme);
    }

    public void print(){

        System.out.println("  " + this.name + " - " + studyModules.size() + "\n");

        for (int i = 0; i < studyModules.size(); i++) {
            studyModules.get(i).print();

        }
    }

    @Override
    public String getType() {
        return "DegreeProgramme";
    }

    public String getName() {

        return this.name;
    }
    public String getCode() {

        return this.code;
    }

    @Override
    public String getId() {
        return id;
    }


    @Override
    public void addChild(Module module) {
        if(module.getType().equals("StudyModule")){
            studyModules.add((StudyModule) module);
        }else if(module.getType().equals("DegreeProgramme")){
            degreeProgrammes.add((DegreeProgramme) module);
        }

    }
}
