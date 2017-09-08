package com.shepherdjerred.stchat.extensions;

import com.shepherdjerred.stchat.Main;

public class stTitles {

    public static boolean enabled;

    public static void checkDependency() {

        try {
            Class.forName("com.shepherdjerred.sttitles.Main");
            enabled = true;
            Main.getInstance().getLogger().info("stTitles integration enabled");
        } catch (Exception e) {
            enabled = false;
            Main.getInstance().getLogger().info("stTitles integration disabled");
        }

    }

}
