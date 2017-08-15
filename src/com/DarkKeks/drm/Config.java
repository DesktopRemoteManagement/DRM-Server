package com.DarkKeks.drm;

import java.io.*;
import java.util.Properties;

public class Config {

    public static final String CONFIG_FILE = "server_config.ini";
    private static File configFile;
    private static Properties defaultProperties = ConfigDefaults.getDefaultProperties();
    private static Properties properties = new Properties(defaultProperties);

    public static void reload(){
        PORT = getInt("PORT");
        SECRET_KEY = getString("SECRET_KEY");
        SERVER_ADDRESS = getString("SERVER_ADDRESS");
    }

    public static void save(){
        putInt("PORT", PORT);
        putString("SECRET_KEY", SECRET_KEY);
        putString("SERVER_ADDRESS", SERVER_ADDRESS);
        store();
    }

    public static int PORT;

    public static String SECRET_KEY;
    public static String SERVER_ADDRESS;


    public static void init(){
        try {
            configFile = new File(CONFIG_FILE);
            if(configFile.exists() && !configFile.isDirectory()){
                properties.load(new InputStreamReader(new FileInputStream(configFile)));
            } else {
                loadDefaults();
            }

            reload();
        } catch (IOException e) {
            Log.logException(e);
        }
    }

    public static void loadDefaults() {
        properties = new Properties(defaultProperties);
        store();

        reload();
    }

    public static void store(){
        try{
            properties.store(new OutputStreamWriter(new FileOutputStream(CONFIG_FILE)), "");
        } catch (IOException e) {
            Log.logException(e);
        }
    }

    private static String getString(String key){
        return (String) properties.getOrDefault(key, defaultProperties.get(key));
    }

    private static int getInt(String key){
        return Integer.parseInt(getString(key));
    }

    private static boolean getBoolean(String key) {
        return Boolean.parseBoolean(getString(key));
    }

    private static void putString(String key, String value){
        properties.put(key, value);
    }

    private static void putInt(String key, int value){
        properties.put(key, Integer.toString(value));
    }

    private static void putBoolean(String key, boolean value) {
        properties.put(key, Boolean.toString(value));
    }
}
