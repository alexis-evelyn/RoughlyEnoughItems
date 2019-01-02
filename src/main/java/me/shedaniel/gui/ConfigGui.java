package me.shedaniel.gui;

import me.shedaniel.ClientListener;
import me.shedaniel.Core;
import me.shedaniel.gui.widget.KeyBindButton;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resource.language.I18n;

import java.io.IOException;

public class ConfigGui extends Gui {
    
    private Gui parent;
    
    public ConfigGui(Gui parent) {
        this.parent = parent;
    }
    
    @Override
    protected void onInitialized() {
        addButton(new KeyBindButton(997, parent.width / 2 - 20, 30, 80, 20, Core.config.recipeKeyBind, key -> {
            Core.config.recipeKeyBind = key;
            ClientListener.recipeKeyBind.setKey(key);
            try {
                Core.saveConfig();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
        addButton(new KeyBindButton(997, parent.width / 2 - 20, 60, 80, 20, Core.config.usageKeyBind, key -> {
            Core.config.usageKeyBind = key;
            ClientListener.usageKeyBind.setKey(key);
            try {
                Core.saveConfig();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
        addButton(new KeyBindButton(997, parent.width / 2 - 20, 90, 80, 20, Core.config.hideKeyBind, key -> {
            Core.config.hideKeyBind = key;
            ClientListener.hideKeyBind.setKey(key);
            try {
                Core.saveConfig();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }
    
    @Override
    public void draw(int mouseX, int mouseY, float partialTicks) {
        drawBackground();
        super.draw(mouseX, mouseY, partialTicks);
        String text = I18n.translate("key.rei.recipe") + ": ";
        drawString(MinecraftClient.getInstance().fontRenderer, text, parent.width / 2 - 40 - MinecraftClient.getInstance().fontRenderer.getStringWidth(text), 30 + 6, -1);
        text = I18n.translate("key.rei.use") + ": ";
        drawString(MinecraftClient.getInstance().fontRenderer, text, parent.width / 2 - 40 - MinecraftClient.getInstance().fontRenderer.getStringWidth(text), 60 + 6, -1);
        text = I18n.translate("key.rei.hide") + ": ";
        drawString(MinecraftClient.getInstance().fontRenderer, text, parent.width / 2 - 40 - MinecraftClient.getInstance().fontRenderer.getStringWidth(text), 90 + 6, -1);
    }
    
    @Override
    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
        if (p_keyPressed_1_ == 256 && this.canClose()) {
            this.close();
            if (parent != null)
                MinecraftClient.getInstance().openGui(parent);
            return true;
        } else {
            return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
        }
    }
}