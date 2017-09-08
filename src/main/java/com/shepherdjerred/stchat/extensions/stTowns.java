package com.shepherdjerred.stchat.extensions;

import com.shepherdjerred.stchat.Main;
import com.shepherdjerred.stchat.objects.Channel;

public class stTowns {

    public static boolean enabled;
    public static boolean installed;
    public static Channel townChannel;
    public static Channel nationChannel;

    public static void checkDependency() {

        enabled = Main.getInstance().getConfig().getBoolean("stTowns.enabled");

        try {
            Class.forName("com.shepherdjerred.sttowns.Main");
            installed = true;
            Main.getInstance().getLogger().info("stTowns integration enabled");
        } catch (Exception e) {
            installed = false;
            Main.getInstance().getLogger().info("stTowns integration disabled");
        }

    }

}
