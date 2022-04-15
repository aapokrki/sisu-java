package fi.tuni.prog3.sisu;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;

public class DegreeProgramme implements Module{


    private transient JsonElement degreeProgramme;
    public String name;
    public String id;



    public DegreeProgramme(JsonElement degreeProgramme){

        this.degreeProgramme = degreeProgramme;
        id = degreeProgramme.getAsJsonObject().get("groupId").getAsString();
        name = degreeProgramme.getAsJsonObject().get("name").getAsJsonObject().get("en").getAsString();


    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getType() {
        return degreeProgramme.getAsJsonObject().get("type").getAsString();
    }

    @Override
    public String getId() {
        return id;
    }

}
