package fi.tuni.prog3.sisu;

import com.google.gson.JsonElement;

public abstract class Module {

    public String getType() {


        return null;
    }

    public String getName() {


        return null;
    }
    public String getCode() {


        return null;
    }
    public String getId() {


        return null;
    }

    public void addChild(Module module){


    }

    public JsonElement getJsonElement(){
        return  null;
    }

    public void addCredits(int amount){

    }
    public void removeCredits(int amount){

    }
}
