package fi.tuni.prog3.sisu;
import com.google.gson.JsonObject;

import java.util.ArrayList;

/**
 * StudyModule class. Holds information about a studyModule in the DegreeProgramme
 * Can contain other subStudyModules and CourseUnits
 */
public class StudyModule extends Module{

    private final transient JsonObject studyModuleJsonObj;
    private transient Module parent;

    private final String name;
    private final String code;
    private final String id;
    private final int minCredits;
    private int currentCredits;
    private final ArrayList<StudyModule> studyModules;
    private final ArrayList<CourseUnit> courseUnits;

    /**
     * StudyModule constructor
     * Initialises StudyModule variables from the given JsonObject
     * @param studyModule - study module
     */
    public StudyModule(JsonObject studyModule){
        this.studyModuleJsonObj = studyModule;
        this.studyModules = new ArrayList<>();
        this.courseUnits = new ArrayList<>();

        // Code
        if(!studyModule.get("code").isJsonNull()){
            this.code = studyModule.get("code").getAsString();
        }else{
            this.code = null;
        }

        // Id
        this.id = studyModule.get("groupId").getAsString();

        // Credits
        if(studyModule.get("targetCredits") != null){
            this.minCredits = studyModule.get("targetCredits").getAsJsonObject().get("min").getAsInt();
        }else{
            this.minCredits = 0;
        }

        // Name
        // Prefers finnish
        JsonObject nameObj = studyModule.get("name").getAsJsonObject();
        if(nameObj.get("fi") == null){
            this.name = nameObj.get("en").getAsString();

        }else if(nameObj.get("fi") != null){
            this.name = nameObj.get("fi").getAsString();
        }else{
            this.name = null;
            System.err.println("Studymodule has no name");
        }
    }



    /**
     * Adds children Modules to this studyModule.
     * Is called by readAPIRec in JSONLogic.
     * Is used to construct the degreeProgramme treestructure
     * @param module CourseUnit or StudyModule to be added as a subModule(child)
     */
    @Override
    public void addChild(Module module){

        // Can't add itself
        if(!module.equals(this)){
            if(module.getType().equals("StudyModule")){
                studyModules.add((StudyModule) module);

            }else if(module.getType().equals("CourseUnit")){
                courseUnits.add((CourseUnit) module);
            }
        }

    }
    /**
     * Sets a parent for the StudyModule
     * Is called by readAPIRec in JSONLogic.
     * Is used to construct the degreeProgramme treestructure
     * @param parent StudyModule or DegreeProgramme to be added as parent Module
     */
    public void setParent(Module parent) {
        this.parent = parent;
    }

    /**
     * Adds a completed course to the degreeProgramme
     * Is called from Module parent
     * Finds the given course from the degreeProgramme from top to bottom
     * @param courseUnit Course to be completed
     * @return Boolean if course was successfully made set as completed
     */
    public Boolean addCompletedCourse(CourseUnit courseUnit){

        for(StudyModule studyModule : studyModules){
            studyModule.addCompletedCourse(courseUnit);
        }

        for(CourseUnit course : courseUnits){
            if(course.getId().equals(courseUnit.getId())){
                course.setCompleted();
                course.setGrade(courseUnit.getGrade());
                currentCredits += courseUnit.getCreditsInt();
                return true;
            }
        }
        return false;
    }

    /*
    Obvious getters

    */
    /**
     * Returns the studyModule's courses
     * @return ArrayList of all courses under this studyModule
     */
    public ArrayList<CourseUnit> getCourseUnits() {
        return courseUnits;
    }

    /**
     * Returns the studyModule's subStudyModules
     * @return ArrayList of all subStudyModules under this studyModule
     */
    public ArrayList<StudyModule> getStudyModules(){
        return studyModules;
    }

    /**
     * Gets studymodule's parent
     * @return Parent Module of this studyModule
     */
    public Module getParent(){return this.parent;}
    @Override
    public JsonObject getJsonObject(){return studyModuleJsonObj;}

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getType() {
        return "StudyModule";
    }

    @Override
    public String getCode(){
        return this.code;
    }

    @Override
    public String getId() {
        return id;
    }
}

