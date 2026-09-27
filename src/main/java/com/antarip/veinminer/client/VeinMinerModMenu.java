/*
 * Credits - Antarip
 */
package com.antarip.veinminer.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class VeinMinerModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return VeinMinerConfigScreen::new;
    }
}