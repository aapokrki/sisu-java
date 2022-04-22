package fi.tuni.prog3.sisu;

import com.google.gson.JsonElement;

public class CourseUnit extends Module{

    private transient JsonElement courseUnit;
    private transient Module parent;

    public String name;
    public String code;
    public int grade = 0;
    public Boolean completed = false;
    public int minCredits = 0;
    public int maxCredits = 0;
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

        if(!courseUnit.getAsJsonObject().get("code").isJsonNull()){
            this.code = courseUnit.getAsJsonObject().get("code").getAsString();
        }else{
            this.code = "null";
            System.err.println("Courseunit code is null");
        }

        if(courseUnit.getAsJsonObject().get("credits").getAsJsonObject().get("max").isJsonNull()){
            this.maxCredits = 0;
        }else{
            this.maxCredits = courseUnit.getAsJsonObject().get("credits").getAsJsonObject().get("max").getAsInt();
        }

        if (courseUnit.getAsJsonObject().get("credits").getAsJsonObject().get("min") != null){

            this.minCredits = courseUnit.getAsJsonObject().get("credits").getAsJsonObject().get("min").getAsInt();
        }else{
            this.minCredits = 0;
        }


    }

    // Temporary setup
    // TODO create different way to add point and credits. Rekursioo degreeprogrammesta
    public void setCompleted() {
        if(!completed){
            this.completed = true;
            if(maxCredits >= minCredits){
                parent.addCredits(maxCredits);
            }else{
                parent.addCredits(minCredits);

            }

        }else{
            this.completed = false;
            if(maxCredits >= minCredits){
                parent.removeCredits(maxCredits);
            }else{
                parent.removeCredits(minCredits);

            }
        }
    }

    public void setGrade(int grade) {
        if(grade <= 5 && grade >= 0){
            this.grade = grade;
        }
        setCompleted();
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
        return maxCredits + "-" + minCredits;
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

    public boolean isCompleted() {
        return this.completed;
    }
}
