package fi.tuni.prog3.sisu;

import com.google.gson.JsonElement;

import java.util.ArrayList;

public class DegreeProgramme extends Module{


    private transient JsonElement degreeProgramme;
    private transient Module parent;

    public String name;
    public String id;
    public String code;

    public int minCredits;
    public int currentCredits;

    public ArrayList<DegreeProgramme> degreeProgrammes;
    public ArrayList<StudyModule> studyModules;


    public DegreeProgramme(JsonElement degreeProgramme){

        this.degreeProgramme = degreeProgramme;
        this.id = degreeProgramme.getAsJsonObject().get("groupId").getAsString();
        this.code = degreeProgramme.getAsJsonObject().get("code").getAsString();
        this.studyModules = new ArrayList<>();
        this.minCredits = degreeProgramme.getAsJsonObject().get("targetCredits").getAsJsonObject().get("min").getAsInt();


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

    public void addCredits(int amount){
        this.currentCredits += amount;
    }

    public void removeCredits(int amount){
        this.currentCredits -= amount;
    }

    public void setStudyModules(ArrayList<StudyModule> studyModules) {
        this.studyModules = studyModules;
    }

    public JsonElement getJsonElement(){
        return degreeProgramme;
    }

    public ArrayList<StudyModule> getStudyModules() {
        return studyModules;
    }

    public ArrayList<DegreeProgramme> getDegreeProgrammes() {return degreeProgrammes;}

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

    public int getMinCredits() {
        return this.minCredits;
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

    public void addCompletedCourse(CourseUnit courseUnit){
        currentCredits += courseUnit.getCreditsInt();
        for(StudyModule studyModule : studyModules){
            studyModule.addCompletedCourse(courseUnit);
        }
    }

    // Ei niin olennaisia ku kaikki tehdään rakentimessa
    public void setParent(Module parent) {
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

    public void print(){

        System.out.println("  " + this.name + " - " + studyModules.size() + "\n");

        for (int i = 0; i < studyModules.size(); i++) {
            studyModules.get(i).print();

        }
    }
}
