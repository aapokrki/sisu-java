package fi.tuni.prog3.sisu;
import com.google.gson.JsonObject;

/**
 * CourseUnit class.
 * Holds information of one Course from the API.
 */
public class CourseUnit extends Module{

    private final transient JsonObject courseUnitJsonObj;
    private transient Module parent;

    public final String name;
    public final String id;
    public int grade;
    public Boolean completed = false;
    public final int minCredits;
    public final int maxCredits;

    private final String code;

    /**
     * Constructs a course from the given course JsonElement from the SISU API
     * @param courseUnit - Given JsonElement of the course from the SISU API
     */
    public CourseUnit(JsonObject courseUnit){
        this.courseUnitJsonObj = courseUnit;

        // Id
        this.id = courseUnit.get("groupId").getAsString();

        // Name
        // Prefers finnish
        JsonObject nameObj = courseUnit.get("name").getAsJsonObject();
        if(nameObj.get("fi") == null){
            this.name = nameObj.get("en").getAsString();
        }else if (nameObj.get("fi") != null){
            this.name = nameObj.get("fi").getAsString();
        }else{
            this.name = null;
            System.err.println("Course has no name");
        }


        // Code
        if(!courseUnit.get("code").isJsonNull()){
            this.code = courseUnit.get("code").getAsString();
        }else{
            this.code = null;
        }

        // Credits
        JsonObject creditsObj = courseUnit.get("credits").getAsJsonObject();
        if(!creditsObj.get("max").isJsonNull()){
            this.maxCredits = creditsObj.get("max").getAsInt();
        }else{
            this.maxCredits = 0;
        }
        if (!creditsObj.get("min").isJsonNull()){
            this.minCredits = creditsObj.get("min").getAsInt();
        }else{
            this.minCredits = 0;
        }
    }

    /**
     * Sets the course to be completed and vice versa
     */
    public void setCompleted() {
        this.completed = !completed;
    }

    /**
     * Gives a grade on the course. Only grades from 1-5 are accepted.
     * @param grade - Integer grade
     */
    public void setGrade(int grade) {
        if(grade <= 5 && grade >= 0){
            this.grade = grade;
        }
        //setCompleted();
    }

    /**
     * Returns the course credits as String
     * If the credit amount can vary, return "minCredits-maxCredits"
     * @return Credit amount as String
     */
    public String getCredits() {
        if (minCredits == maxCredits) {
            return String.valueOf(minCredits);
        }
        if (maxCredits == 0) {
            return String.valueOf(minCredits);
        }
        return minCredits + "-" + maxCredits;
    }

    /**
     * Returns the course credit as Int
     * minCredit > maxCredit if maxCredit was null in Json
     * Returns the larger credit
     * @return
     */
    public int getCreditsInt(){
        if (minCredits == maxCredits) {
            return maxCredits;
        }
        if (maxCredits == 0) {
            return minCredits;
        }
        return maxCredits;
    }

    /**
     * Sets parent Module for the course
     * Is called by readAPIRec in JSONLogic.
     * Is used to construct the degreeProgramme treestructure
     * @param parent The Module that is set to be Parent of this course
     */
    public void setParent(Module parent) {
        this.parent = parent;

    }

    /**
     * Checks if course is completed
     * @return True if course completed, False if not
     */
    public boolean isCompleted() {return this.completed;}


    /*
    Obvious getters
    */
    public Module getParent() {return this.parent;}

    public int getGrade() {return this.grade;}


    @Override
    public JsonObject getJsonObject() {return this.courseUnitJsonObj;}

    @Override
    public String getCode() {return code;}

    @Override
    public String getName() {return name;}

    @Override
    public String getType() {return "CourseUnit";}

    @Override
    public String getId() {return id;}
}
