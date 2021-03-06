/*
 * This file is licensed under the MIT License, part of Roughly Enough Items.
 * Copyright (c) 2018, 2019, 2020 shedaniel
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.shedaniel.rei.gui.credits;

import com.mojang.blaze3d.vertex.PoseStack;
import me.shedaniel.clothconfig2.gui.widget.DynamicNewSmoothScrollingEntryListWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

@ApiStatus.Internal
public class CreditsEntryListWidget extends DynamicNewSmoothScrollingEntryListWidget<CreditsEntryListWidget.CreditsItem> {
    
    private boolean inFocus;
    
    public CreditsEntryListWidget(Minecraft client, int width, int height, int startY, int endY) {
        super(client, width, height, startY, endY, GuiComponent.BACKGROUND_LOCATION);
    }
    
    @Override
    public boolean changeFocus(boolean boolean_1) {
        if (!this.inFocus && this.getItemCount() == 0) {
            return false;
        } else {
            this.inFocus = !this.inFocus;
            if (this.inFocus && this.getFocused() == null && this.getItemCount() > 0) {
                this.moveSelection(1);
            } else if (this.inFocus && this.getFocused() != null) {
                this.moveSelection(0);
            }
            
            return this.inFocus;
        }
    }
    
    public void creditsClearEntries() {
        clearItems();
    }
    
    private CreditsItem rei_getEntry(int int_1) {
        return this.children().get(int_1);
    }
    
    public void creditsAddEntry(CreditsItem entry) {
        addItem(entry);
    }
    
    @Override
    public int getItemWidth() {
        return width - 80;
    }
    
    @Override
    protected int getScrollbarPosition() {
        return width - 40;
    }
    
    public static abstract class CreditsItem extends DynamicNewSmoothScrollingEntryListWidget.Entry<CreditsItem> {
        
    }
    
    public static class TextCreditsItem extends CreditsItem {
        private Component text;
        
        public TextCreditsItem(Component text) {
            this.text = text;
        }
        
        @Override
        public void render(PoseStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
            Minecraft.getInstance().font.drawShadow(matrices, text.getVisualOrderText(), x + 5, y + 5, -1);
        }
        
        @Override
        public int getItemHeight() {
            return 12;
        }
        
        @Override
        public boolean changeFocus(boolean boolean_1) {
            return false;
        }
    }
    
    public static class TranslationCreditsItem extends CreditsItem {
        private Component language;
        private List<FormattedCharSequence> translators;
        private int maxWidth;
        
        public TranslationCreditsItem(Component language, Component translators, int width, int maxWidth) {
            this.language = language;
            this.translators = Minecraft.getInstance().font.split(translators, width);
            this.maxWidth = maxWidth;
        }
        
        @Override
        public void render(PoseStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
            Minecraft.getInstance().font.drawShadow(matrices, language.getVisualOrderText(), x + 5, y + 5, -1);
            int yy = y + 5;
            for (FormattedCharSequence translator : translators) {
                Minecraft.getInstance().font.drawShadow(matrices, translator, x + 5 + maxWidth, yy, -1);
                yy += 12;
            }
        }
        
        @Override
        public int getItemHeight() {
            return 12 * translators.size();
        }
        
        @Override
        public boolean changeFocus(boolean boolean_1) {
            return false;
        }
    }
    
}
