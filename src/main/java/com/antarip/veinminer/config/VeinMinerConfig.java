/*
 * Credits - Antarip
 */
package com.antarip.veinminer.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class VeinMinerConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File FILE = FabricLoader.getInstance().getConfigDir().resolve("veinminer.json").toFile();

    public boolean enabled = true;
    public boolean applyToolDamage = true;
    public int maxOres = 64;

    private static VeinMinerConfig instance;

    public static VeinMinerConfig get() {
        if (instance == null) {
            load();
        }
        return instance;
    }

    public static void load() {
        if (FILE.exists()) {
            try (FileReader reader = new FileReader(FILE)) {
                instance = GSON.fromJson(reader, VeinMinerConfig.class);
            } catch (Exception e) {
                instance = new VeinMinerConfig();
            }
        } else {
            instance = new VeinMinerConfig();
            save();
        }
    }

    public static void save() {
        if (instance == null) {
            instance = new VeinMinerConfig();
        }
        try (FileWriter writer = new FileWriter(FILE)) {
            GSON.toJson(instance, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}