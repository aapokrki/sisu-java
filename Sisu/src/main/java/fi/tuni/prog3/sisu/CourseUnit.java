package fi.tuni.prog3.sisu;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class CourseUnit extends Module{

    private transient JsonObject courseUnit;
    private transient Module parent;

    public String name;
    public String code;
    public int grade;
    public Boolean completed = false;
    public int minCredits;
    public int maxCredits;
    public String id;

    /**
     * Constructs a course from the given course JsonElement from the SISU API
     * @param courseUnit - Given JsonElement of the course from the SISU API
     */
    public CourseUnit(JsonObject courseUnit){
        this.courseUnit = courseUnit;
        this.id = courseUnit.get("groupId").getAsString();

        // The name can be in finnish, english or both. Prefers finnish first if both are available
        JsonObject nameObj = courseUnit.get("name").getAsJsonObject();

        if(nameObj.get("fi") == null){
            this.name = nameObj.get("en").getAsString();
        }else if (nameObj.get("fi") != null){
            this.name = nameObj.get("fi").getAsString();
        }else{
            System.err.println("Course has no name");
        }


        // Code
        if(!courseUnit.get("code").isJsonNull()){
            this.code = courseUnit.get("code").getAsString();
        }

        // Credits
        JsonObject creditsObj = courseUnit.get("credits").getAsJsonObject();
        if(!creditsObj.get("max").isJsonNull()){
            this.maxCredits = creditsObj.get("max").getAsInt();
        }
        if (!creditsObj.get("min").isJsonNull()){
            this.minCredits = creditsObj.get("min").getAsInt();
        }

    }

    /**
     * Sets the course to be completed
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

    @Override
    public JsonElement getJsonElement() {
        return this.courseUnit;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getCredits() {
        if (minCredits == maxCredits) {
            return String.valueOf(minCredits);
        }
        if (maxCredits == 0) {
            return String.valueOf(minCredits);
        }
        return minCredits + "-" + maxCredits;
    }

    public int getCreditsInt(){
        if (minCredits == maxCredits) {
            return maxCredits;
        }
        if (maxCredits == 0) {
            return minCredits;
        }
        return maxCredits;

    }

    public int getGrade() {
        return this.grade;
    }

    @Override
    public String getType() {
        return "CourseUnit";
    }

    @Override
    public String getId() {
        return id;
    }

    public void setParent(Module parent) {
        this.parent = parent;

    }

    public void print(){
        System.out.println(" ---- "+this.name);
    }

    public boolean isCompleted() {
        return this.completed;
    }
}
