package com.lkjuhkmnop.textquest.story;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lkjuhkmnop.textquest.tools.Tools;

import java.util.HashMap;

public class TQCharacter {
    public HashMap<String, String> properties = new HashMap<>();
    public HashMap<String, String> parameters = new HashMap<>();

//    Default constructor - for Jackson
    public TQCharacter() {

    }

    public TQCharacter(HashMap<String, String> properties, HashMap<String, String> parameters) {
        this.properties = properties;
        this.parameters = parameters;
    }

    public TQCharacter(String propertiesJson, String parametersJson) throws JsonProcessingException {
        this.properties = Tools.mapper().readValue(propertiesJson, properties.getClass());
        this.parameters = Tools.mapper().readValue(parametersJson, parameters.getClass());
    }
}