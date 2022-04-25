package fi.tuni.prog3.sisu;

import com.google.gson.JsonElement;

import java.util.ArrayList;

public class StudyModule extends Module{

    private transient JsonElement studyModule;
    private transient Module parent;



    public String name;
    public String code;
    public String id;
    public int minCredits;
    public int currentCredits;
    public ArrayList<StudyModule> studyModules;
    public ArrayList<CourseUnit> courseUnits;


    public StudyModule(JsonElement studyModule){
        this.studyModule = studyModule;
        if(!studyModule.getAsJsonObject().get("code").isJsonNull()){
            this.code = studyModule.getAsJsonObject().get("code").getAsString();

        }else{
            this.code = null;
        }
        this.id = studyModule.getAsJsonObject().get("groupId").getAsString();
        if(!(studyModule.getAsJsonObject().get("targetCredits") == null)){
            this.minCredits = studyModule.getAsJsonObject().get("targetCredits").getAsJsonObject().get("min").getAsInt();

        }else{
            this.minCredits = 0;
        }

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

    public void addCredits(int amount){
        this.currentCredits += amount;
        parent.addCredits(amount);
    }
    public void removeCredits(int amount){
        this.currentCredits -= amount;
        parent.removeCredits(amount);
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

    public ArrayList<CourseUnit> getCourseUnits() {
        return courseUnits;
    }

    public ArrayList<StudyModule> getStudyModules(){return studyModules; }


    public void addChild(Module module){

        if(module.getType().equals("StudyModule")){
            studyModules.add((StudyModule) module);

        }else if(module.getType().equals("CourseUnit")){
            courseUnits.add((CourseUnit) module);
            //System.err.println(courseUnits.size());
        }
    }

    public JsonElement getJsonElement(){
        return studyModule;
    }

    public void setParent(Module parent) {
        this.parent = parent;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void addCompletedCourse(CourseUnit courseUnit){
        currentCredits += courseUnit.getCreditsInt();
        for(StudyModule studyModule : studyModules){
            studyModule.addCompletedCourse(courseUnit);
        }

        for(CourseUnit course : courseUnits){
            if(course.getId().equals(courseUnit.getId())){
                course.setCompleted();
                course.setGrade(courseUnit.getGrade());
            }
        }
    }

    // Ei niin olennaisia
    public void setName(String name) {
        this.name = name;
    }

    public void addCourseUnits(CourseUnit courseUnit) {
        courseUnits.add(courseUnit);
    }

    public void setCode(String code) {
        this.code = code;
    }
    public void setId(String id) {
        this.id = id;
    }
    public void print(){

        if(studyModules.isEmpty()){
            System.out.println(" -- " + this.name + "| - Courses: " + courseUnits.size());
            for (CourseUnit courseUnit : courseUnits) {
                courseUnit.print();
            }
        }else{
            System.out.println(this.name + "| - Submodules: " + studyModules.size() + " - Courses: " + courseUnits.size());

            for (StudyModule module : studyModules) {
                module.print();
            }

            for (CourseUnit courseUnit : courseUnits) {
                courseUnit.print();
            }
        }



    }


}

