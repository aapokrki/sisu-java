package fi.tuni.prog3.sisu;

import com.google.gson.JsonElement;

public class CourseUnit extends Module{

    private transient JsonElement courseUnit;
    private transient JsonElement parent;

    public String name;
    public String code;
    public int grade;
    public Boolean completed;
    public String id;
    public transient String type = "CourseUnit";

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


    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public void setGrade(int grade) {
        this.grade = grade;
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

    public void print(){
        System.out.println(" ---- "+this.name);
    }
    @Override
    public String getType() {
        return "CourseUnit";
    }

    @Override
    public String getId() {
        return id;
    }
}
