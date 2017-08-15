package com.DarkKeks.drm;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class Parameter {
    private String name;
    private String type;
    private boolean booleanContent;
    private String stringContent;
    private double doubleContent;
    private JsonObject jsonContent;
    private ContentType contentType;


    public Parameter(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public Parameter(JsonObject param) {
        try {
            setNameAndType(param.get("name").getAsString(), param.get("type").getAsString());
            if(param.get("value").isJsonObject()){
                setJsonContent(param.getAsJsonObject("value"));
            } else {
                JsonPrimitive value = param.getAsJsonPrimitive("value");
                if(value.isString()) {
                    setStringContent(value.getAsString());
                } else if(value.isNumber()) {
                    setDoubleContent(value.getAsDouble());
                } else if(value.isBoolean()) {
                    setBooleanContent(value.getAsBoolean());
                }
            }
        } catch (Exception e){
            throw new IllegalArgumentException("invalid parameter structure");
        }
    }

    public JsonObject toJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("name", this.name);
        obj.addProperty("type", this.type);
        switch (this.contentType) {
            case STRING:
                obj.addProperty("value", getStringContent());
                break;
            case DOUBLE:
                obj.addProperty("value", getDoubleContent());
                break;
            case BOOLEAN:
                obj.addProperty("value", getBooleanContent());
                break;
            case JSON:
                obj.add("value", getJsonContent());
        }
        return obj;
    }

    public Parameter(String name, String type, boolean booleanContent) {
        this(name, type);
        setBooleanContent(booleanContent);
    }

    public Parameter(String name, String type, String stringContent) {
        this(name, type);
        setStringContent(stringContent);
    }

    public Parameter(String name, String type, double doubleContent) {
        this(name, type);
        setDoubleContent(doubleContent);
    }

    public Parameter(String name, String type, JsonObject jsonContent) {
        this(name, type);
        setJsonContent(jsonContent);
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    private void setNameAndType(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public void setStringContent(String stringContent) {
        this.stringContent = stringContent;
        this.contentType = ContentType.STRING;
    }

    public void setDoubleContent(double doubleContent) {
        this.doubleContent = doubleContent;
        this.contentType = ContentType.DOUBLE;
    }

    public void setJsonContent(JsonObject jsonContent) {
        this.jsonContent = jsonContent;
        this.contentType = ContentType.JSON;
    }

    public void setBooleanContent(boolean booleanContent) {
        this.booleanContent = booleanContent;
        this.contentType = ContentType.BOOLEAN;
    }

    public boolean getBooleanContent() {
        if(this.contentType != ContentType.BOOLEAN) throw new IllegalArgumentException("no boolean content");
        return booleanContent;
    }

    public String getStringContent() {
        if(this.contentType != ContentType.STRING) throw new IllegalArgumentException("no string content");
        return stringContent;
    }

    public double getDoubleContent() {
        if(this.contentType != ContentType.DOUBLE) throw new IllegalArgumentException("no double content");
        return doubleContent;
    }

    public JsonObject getJsonContent() {
        if(this.contentType != ContentType.JSON) throw new IllegalArgumentException("no json content");
        return jsonContent;
    }

    private enum ContentType {
        STRING, DOUBLE, JSON, BOOLEAN
    }
}
