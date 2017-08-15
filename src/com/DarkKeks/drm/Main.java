package com.DarkKeks.drm;

public class Main {

    public static void main(String[] args) {
        Config.init();
        Controller.getInstance().run();
    }
}
