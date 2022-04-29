package fi.tuni.prog3.sisu;

import com.google.gson.JsonObject;

import java.util.ArrayList;

/**
 * DegreeProgramme class.
 * Holds information about a DegreeProgramme. Can contain StudyModules.
 */
public class DegreeProgramme extends Module{

    private final transient JsonObject degreeProgrammeJsonObj;

    private final String name;
    private final String id;
    private final String code;

    private final int minCredits;
    private int currentCredits;

    private ArrayList<StudyModule> studyModules;

    /**
     * DegreeProgramme constructor
     * Initialises DegreeProgramme variables from the given JsonObject
     * @param degreeProgramme - degree programme
     */
    public DegreeProgramme(JsonObject degreeProgramme){

        this.degreeProgrammeJsonObj = degreeProgramme;
        this.studyModules = new ArrayList<>();

        // Id
        this.id = degreeProgramme.get("groupId").getAsString();

        // Code
        if(!degreeProgramme.get("code").isJsonNull()){
            this.code = degreeProgramme.get("code").getAsString();
        }else{
            this.code = null;
        }

        // Credits
        if(degreeProgramme.get("targetCredits") != null){
            this.minCredits = degreeProgramme.get("targetCredits").getAsJsonObject().get("min").getAsInt();
        }else{
            this.minCredits = 0;
        }

        // Name
        // Prefers finnish
        JsonObject nameObj = degreeProgramme.get("name").getAsJsonObject();

        if(nameObj.get("fi") == null){

            this.name = nameObj.get("en").getAsString();

        }else if(nameObj.get("fi") != null){
            this.name = nameObj.get("fi").getAsString();
        }else{
            this.name = null;
            System.err.println("DegreeProgramme has no name");
        }

    }

    /**
     * Sets the given studymodules
     * Is called by readAPIData when there is a mandatoryStudyModule to be selected.
     * @param studyModules ArrayList of studyModules to replace the current studymodules
     */
    public void setStudyModules(ArrayList<StudyModule> studyModules) {
        this.studyModules = studyModules;
    }

    /**
     * Adds children Modules to this degreeProgramme
     * Is called by readAPIRec in JSONLogic.
     * Is used to construct the degreeProgramme treestructure
     * @param module StudyModule to be added as a subModule(child)
     */
    @Override
    public void addChild(Module module) {
        if(!module.equals(this)){
            if(module.getType().equals("StudyModule")){
                studyModules.add((StudyModule) module);
            }
        }
    }

    /**
     * Adds a completed course to the degreeProgramme
     * Finds the given course from the degreeProgramme from top to bottom
     * @param courseUnit Course to be completed
     */
    public void addCompletedCourse(CourseUnit courseUnit){

        for(StudyModule studyModule : studyModules){
            if(studyModule.addCompletedCourse(courseUnit)){
                currentCredits += courseUnit.getCreditsInt();
            }
        }
    }


    /*
    Obvious getters
    */
    public int getCurrentCredits() {
        return currentCredits;
    }

    @Override
    public String getType() {
        return "DegreeProgramme";
    }

    @Override
    public String getName() {return this.name;}

    @Override
    public String getCode() {return this.code;}

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public JsonObject getJsonObject(){return this.degreeProgrammeJsonObj;}

    public int getMinCredits() {
        return this.minCredits;
    }

    public ArrayList<StudyModule> getStudyModules() {
        return this.studyModules;
    }
}
