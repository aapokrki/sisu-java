package fi.tuni.prog3.sisu;

import com.google.gson.JsonElement;

public class CourseUnit{

    private transient JsonElement courseUnit;
    public String name;
    public int grade;
    public Boolean completed;
    public String id;
    public transient String type = "CourseUnit";

    public CourseUnit(JsonElement courseUnit){
        this.courseUnit = courseUnit;
        this.name = courseUnit.getAsJsonArray().get(0).getAsJsonObject().get("name").getAsJsonObject().get("en").getAsString();
        this.id = courseUnit.getAsJsonArray().get(0).getAsJsonObject().get("groupId").getAsString();

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
}
