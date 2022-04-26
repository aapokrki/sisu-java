package fi.tuni.prog3.sisu;

import com.google.gson.JsonObject;

/**
 * Abstract roof class for DegreeProgramme, StudyModule and CourseUnit
 * Is mainly used for easier readAPIRec() handling, so that rootobj can be any Module subclass
 */
public abstract class Module {

    /**
     * @return Module type (CourseUnit, StudyModule, DegreeProgramme)
     */
    public String getType() {
        return null;
    }

    /**
     * @return Module's name
     */
    public String getName() {
        return null;
    }

    /**
     * @return Module code
     */
    public String getCode() {
        return null;
    }

    /**
     * @return Module id
     */
    public String getId() {
        return null;
    }

    /**
     * Adds child to module.
     * Is called by readApiRec to build treestructure of degreeprogramme
     * @param module Given Module child to add
     */
    public void addChild(Module module){

    }

    /**
     * @return Module jsonObject
     */
    public JsonObject getJsonObject(){
        return  null;
    }


}
