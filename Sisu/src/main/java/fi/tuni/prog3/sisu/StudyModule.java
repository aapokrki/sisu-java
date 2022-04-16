package fi.tuni.prog3.sisu;

import com.google.gson.JsonElement;

import java.util.ArrayList;

public class StudyModule extends Module{

    private transient JsonElement studyModule;
    private transient JsonElement parent;
    public String name;
    public String code;
    public String id;
    public ArrayList<StudyModule> studyModules;
    public ArrayList<CourseUnit> courseUnits;


    public StudyModule(JsonElement studyModule){
        this.studyModule = studyModule;
        this.code = studyModule.getAsJsonObject().get("code").getAsString();
        this.id = studyModule.getAsJsonObject().get("groupId").getAsString();

        // The name can be in finnish, english or both. Prefers finnish first if both are available
        try {
            if(studyModule.getAsJsonObject().get("name").getAsJsonObject().get("fi") == null){

                this.name = studyModule.getAsJsonObject().get("name").getAsJsonObject().get("en").getAsString();

            }else{
                this.name = studyModule.getAsJsonObject().get("name").getAsJsonObject().get("fi").getAsString();
            }
        } catch (Exception e) {
            e.printStackTrace();

        }


        this.studyModules = new ArrayList<>();
        this.courseUnits = new ArrayList<>();
    }

    @Override
    public String getType() {
        return "StudyModule";
    }

    public String getCode(){
        return this.code;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public JsonElement getJsonElement(){
        return studyModule;
    }

    public void setParent(JsonElement parent) {
        this.parent = parent;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addCourseUnits(CourseUnit courseUnit) {
        courseUnits.add(courseUnit);
    }

    public void setCode(String code) {
        this.code = code;
    }

    public JsonElement getParent() {
        return parent;
    }

    public ArrayList<CourseUnit> getCourseUnits() {
        return courseUnits;
    }

    public void addChild(Module module){

        if(module.getType().equals("StudyModule")){
            studyModules.add((StudyModule) module);

        }else if(module.getType().equals("CourseUnit")){
            courseUnits.add((CourseUnit) module);
            //System.err.println(courseUnits.size());
        }
    }

    public void print(){

        if(studyModules.isEmpty()){
            System.out.println(" -- " + this.name + "| - Courses: " + courseUnits.size());
            for (int i = 0; i < courseUnits.size(); i++) {
                courseUnits.get(i).print();
            }
        }else{
            System.out.println(this.name + "| - Submodules: " + studyModules.size() + " - Courses: " + courseUnits.size());

            for (int i = 0; i < studyModules.size(); i++) {
                studyModules.get(i).print();
            }

            for (int i = 0; i < courseUnits.size(); i++) {
                courseUnits.get(i).print();
            }
        }



    }


}

