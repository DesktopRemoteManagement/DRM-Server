package com.DarkKeks.drm;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;

public class Message {

    private static JsonParser parser = new JsonParser();

    private String action;
    private MessageId id;
    private String destination;
    private HashMap<String, Parameter> params;

    public Message() {
        this.params = new HashMap<>();
    }

    public Message(String message) {
        this(parser.parse(message).getAsJsonObject());
    }

    public Message(JsonObject obj){
        this.params = new HashMap<>();
        this.fromJson(obj);
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public MessageId getId() {
        return this.id;
    }

    public void setId(MessageId id) {
        this.id = id;
    }

    public void addParam(Parameter param){
        params.put(param.getName(), param);
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public boolean hasDestination() {
        return this.destination != null && !this.destination.isEmpty();
    }

    public String getDestination() {
        return this.hasDestination() ? destination : null;
    }

    public HashMap<String, Parameter> getParams() {
        return params;
    }

    private void fromJson(JsonObject obj){
        try {
            if(obj.has("destination")) {
                this.destination = obj.get("destination").getAsString();
            }

            this.id = new MessageId(obj.get("id").getAsString());

            obj = obj.getAsJsonObject("message");
            this.action = obj.get("action").getAsString();
            this.params.clear();
            for (JsonElement cur : obj.get("params").getAsJsonArray()) {
                addParam(new Parameter(cur.getAsJsonObject()));
            }
        } catch (Exception e) {
            Log.logException(e);
            throw new IllegalArgumentException("invalid message structure");
        }
    }

    public JsonObject toJson(){
        JsonObject obj = new JsonObject();
        JsonObject message = new JsonObject();
        message.addProperty("action", action);

        JsonArray pr = new JsonArray();
        for (String key : params.keySet()) {
            pr.add(params.get(key).toJson());
        }
        message.add("params", pr);

        obj.add("message", message);

        if(this.destination != null) {
            obj.addProperty("destination", this.destination);
        }

        obj.addProperty("id", this.id.toString());

        return obj;
    }

    @Override
    public String toString() {
        return this.toJson().toString();
    }

    public static Builder getBuilder() {
        return new Message().new Builder();
    }

    public class Builder {

        private Builder() {
        }

        public Builder setId(MessageId id) {
            Message.this.setId(id);
            return this;
        }

        public Builder setAction(String action) {
            Message.this.setAction(action);
            return this;
        }

        public Builder setDestination(String destination) {
            Message.this.setDestination(destination);
            return this;
        }

        public Builder addParam(String key, String type, String value) {
            Parameter pr = new Parameter(key, type, value);
            Message.this.params.put(key, new Parameter(key, type, value));
            return this;
        }

        public Builder addParam(Parameter param) {
            Message.this.params.put(param.getName(), param);
            return this;
        }

        public Builder addAll(HashMap<String, Parameter> map) {
            Message.this.params.putAll(map);
            return this;
        }

        public Message build(){
            return Message.this;
        }
    }
}
