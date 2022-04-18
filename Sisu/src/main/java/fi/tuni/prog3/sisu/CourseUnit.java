package fi.tuni.prog3.sisu;

import com.google.gson.JsonElement;

public class CourseUnit extends Module{

    private transient JsonElement courseUnit;
    private transient Module parent;

    public String name;
    public String code;
    public int grade;
    public Boolean completed = false;
    public int credits;
    public String id;

    public CourseUnit(JsonElement courseUnit){
        this.courseUnit = courseUnit;

        // The name can be in finnish, english or both. Prefers finnish first if both are available
        try {
            if(courseUnit.getAsJsonObject().get("name").getAsJsonObject().get("fi") == null){

                this.name = courseUnit.getAsJsonObject().get("name").getAsJsonObject().get("en").getAsString();

            }else{
                this.name = courseUnit.getAsJsonObject().get("name").getAsJsonObject().get("fi").getAsString();
            }
        } catch (Exception e) {
            e.printStackTrace();

        }

        this.id = courseUnit.getAsJsonObject().get("groupId").getAsString();
        this.code = courseUnit.getAsJsonObject().get("code").getAsString();
        this.credits = courseUnit.getAsJsonObject().get("credits").getAsJsonObject().get("max").getAsInt();

    }

    public void setCompleted() {
        if(!completed){
            this.completed = true;
            parent.addCredits(credits);
        }else{
            this.completed = false;
            parent.removeCredits(credits);
        }

    }

    public void setGrade(int grade) {
        if(grade <= 5 && grade >= 0){
            this.grade = grade;
        }
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

    public int getGrade() {
        return grade;
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

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCourseUnit(JsonElement courseUnit) {
        this.courseUnit = courseUnit;
    }

}
