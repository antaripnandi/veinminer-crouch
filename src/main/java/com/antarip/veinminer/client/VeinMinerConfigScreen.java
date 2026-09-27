/*
 * Credits - Antarip
 */
package com.antarip.veinminer.client;

import com.antarip.veinminer.config.VeinMinerConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class VeinMinerConfigScreen extends Screen {
    private final Screen parent;

    public VeinMinerConfigScreen(Screen parent) {
        super(Component.literal("Vein Miner Config - Credits - Antarip"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        boolean isOnServer = this.minecraft != null && !this.minecraft.isLocalServer() && this.minecraft.getCurrentServer() != null;

        // Add Enable/Disable Toggle
        this.addRenderableWidget(CycleButton.onOffBuilder(VeinMinerConfig.get().enabled)
            .create(this.width / 2 - 100, 50, 200, 20, Component.literal("Mod Enabled"), (button, value) -> {
                VeinMinerConfig.get().enabled = value;
                VeinMinerConfig.save();
            }));

        // Add Tool Damage Toggle (Greyed out if connected to a dedicated server)
        CycleButton<Boolean> toolDamageBtn = CycleButton.onOffBuilder(VeinMinerConfig.get().applyToolDamage)
            .create(this.width / 2 - 100, 80, 200, 20, Component.literal("Apply Tool Damage"), (button, value) -> {
                if (!isOnServer) {
                    VeinMinerConfig.get().applyToolDamage = value;
                    VeinMinerConfig.save();
                }
            });
        if (isOnServer) {
            toolDamageBtn.active = false;
        }
        this.addRenderableWidget(toolDamageBtn);

        // Add Max Ores limit (Greyed out if connected to a dedicated server)
        CycleButton<Integer> maxOresBtn = CycleButton.builder((Integer value) -> Component.literal(String.valueOf(value)), Integer.valueOf(VeinMinerConfig.get().maxOres))
            .withValues(16, 32, 64, 128)
            .create(this.width / 2 - 100, 110, 200, 20, Component.literal("Max Ores Limit"), (button, value) -> {
                if (!isOnServer) {
                    VeinMinerConfig.get().maxOres = value;
                    VeinMinerConfig.save();
                }
            });
        if (isOnServer) {
            maxOresBtn.active = false;
        }
        this.addRenderableWidget(maxOresBtn);

        // Back / Close Button
        this.addRenderableWidget(Button.builder(Component.literal("Done"), button -> onClose())
            .bounds(this.width / 2 - 100, 150, 200, 20)
            .build());
    }

    @Override
    public void onClose() {
        VeinMinerConfig.save();
        if (this.parent != null) {
            Minecraft.getInstance().gui.setScreen(this.parent);
        } else {
            super.onClose();
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);
        graphics.centeredText(this.font, this.title, this.width / 2, 20, 0xFFFFFF);

        boolean isOnServer = this.minecraft != null && !this.minecraft.isLocalServer() && this.minecraft.getCurrentServer() != null;
        if (isOnServer) {
            graphics.centeredText(this.font, Component.literal("Connected to server: Gameplay configs are server-side only!"), this.width / 2, 135, 0xFF5555);
        }
    }
}