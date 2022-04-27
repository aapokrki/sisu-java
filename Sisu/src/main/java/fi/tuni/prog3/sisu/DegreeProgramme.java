package fi.tuni.prog3.sisu;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;

public class DegreeProgramme extends Module{


    private final transient JsonObject degreeProgramme;
    private transient Module parent;

    public String name;
    public String id;
    public String code;

    // Kantsiiko creditsit ja GPA tallettaa tänne vai studentiin?

    public int minCredits;
    //public int currentCredits;

    public ArrayList<StudyModule> studyModules;

    /**
     * DegreeProgramme constructor
     * Initialises DegreeProgramme variables from the given JsonObject
     * @param degreeProgramme - degree programme
     */
    public DegreeProgramme(JsonObject degreeProgramme){

        this.degreeProgramme = degreeProgramme;
        this.studyModules = new ArrayList<>();

        // Id
        this.id = degreeProgramme.get("groupId").getAsString();

        // Code
        if(!degreeProgramme.get("code").isJsonNull()){
            this.code = degreeProgramme.get("code").getAsString();
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

        }else{
            this.name = nameObj.get("fi").getAsString();
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
        if(module.getType().equals("StudyModule")){
            studyModules.add((StudyModule) module);
        }
    }

    /**
     * Adds a completed course to the degreeProgramme
     * Finds the given course from the degreeProgramme from top to bottom
     * @param courseUnit Course to be completed
     */
    public void addCompletedCourse(CourseUnit courseUnit){
        //currentCredits += courseUnit.getCreditsInt();
        for(StudyModule studyModule : studyModules){
            studyModule.addCompletedCourse(courseUnit);
        }
    }


    /*
    Obvious getters
    */
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
    public JsonObject getJsonObject(){return this.degreeProgramme;}

    public int getMinCredits() {
        return this.minCredits;
    }

    public ArrayList<StudyModule> getStudyModules() {
        return this.studyModules;
    }
}
