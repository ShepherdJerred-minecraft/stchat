package com.shepherdjerred.stchat.files;

import com.shepherdjerred.stchat.Main;
import com.shepherdjerred.stchat.mysql.ErrorManager;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.*;


public class FileManager {

    private static FileManager instance;
    public FileConfiguration messages, storage;
    private File messagesFile, storageFile;

    private FileManager() {
        instance = this;
    }

    public static FileManager getInstance() {
        if (instance == null) {
            instance = new FileManager();
        }
        return instance;
    }

    // Load/reload files
    @SuppressWarnings("deprecation")
    void loadFiles() {

        messagesFile = new File(Main.getInstance().getDataFolder(), "messages.yml");
        storageFile = new File(Main.getInstance().getDataFolder(), "storage.yml");

        if (!messagesFile.exists()) {
            messagesFile.getParentFile().mkdirs();
            copy(Main.getInstance().getResource("messages.yml"), messagesFile);
        }

        if (!storageFile.exists()) {
            storageFile.getParentFile().mkdirs();
            copy(Main.getInstance().getResource("storage.yml"), storageFile);
        }

        messages = new YamlConfiguration();
        storage = new YamlConfiguration();

        try {

            messages.load(messagesFile);
            messages.setDefaults(YamlConfiguration.loadConfiguration(Main.getInstance().getResource("messages.yml")));
            messages.options().copyDefaults(true);
            saveFiles(FileName.MESSAGES);

            storage.load(storageFile);
            storage.setDefaults(YamlConfiguration.loadConfiguration(Main.getInstance().getResource("storage.yml")));
            storage.options().copyDefaults(true);
            saveFiles(FileName.STORAGE);

        } catch (Exception e) {
            e.printStackTrace();
            ErrorManager.logError(e);
        }

    }

    // Save files
    private void saveFiles(@NotNull FileName file) {
        Validate.notNull(file);
        try {
            switch (file) {
                case MESSAGES:
                    messages.save(messagesFile);
                    break;
                case STORAGE:
                    storage.save(storageFile);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
            ErrorManager.logError(e);
        }
    }

    // Copy default files
    private void copy(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0)
                out.write(buf, 0, len);
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            ErrorManager.logError(e);
        }
    }

    public enum FileName {
        MESSAGES, STORAGE
    }

}
