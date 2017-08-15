package com.DarkKeks.drm;

import java.util.Properties;

public class ConfigDefaults {

    public static Properties getDefaultProperties(){
        Properties props = new Properties();

        props.setProperty("PORT", "42183");
        props.setProperty("SECRET_KEY", "DEFAULT_KEY");
        props.setProperty("SERVER_ADDRESS", "http://drm-web.ml");

        return props;
    }
}
