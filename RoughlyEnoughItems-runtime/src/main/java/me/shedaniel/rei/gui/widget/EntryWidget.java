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

package me.shedaniel.rei.gui.widget;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.shedaniel.clothconfig2.api.ModifierKeyCode;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.math.impl.PointHelper;
import me.shedaniel.rei.api.*;
import me.shedaniel.rei.api.widgets.Slot;
import me.shedaniel.rei.api.widgets.Tooltip;
import me.shedaniel.rei.gui.ContainerScreenOverlay;
import me.shedaniel.rei.impl.ScreenHelper;
import me.shedaniel.rei.utils.CollectionUtils;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EntryWidget extends Slot {
    
    protected static final ResourceLocation RECIPE_GUI = new ResourceLocation("roughlyenoughitems", "textures/gui/recipecontainer.png");
    protected static final ResourceLocation RECIPE_GUI_DARK = new ResourceLocation("roughlyenoughitems", "textures/gui/recipecontainer_dark.png");
    
    @ApiStatus.Internal
    private byte noticeMark = 0;
    protected boolean highlight = true;
    protected boolean tooltips = true;
    protected boolean background = true;
    protected boolean interactable = true;
    protected boolean interactableFavorites = true;
    protected boolean wasClicked = false;
    private Rectangle bounds;
    private List<EntryStack> entryStacks;
    
    protected EntryWidget(int x, int y) {
        this(new Point(x, y));
    }
    
    protected EntryWidget(Point point) {
        this.bounds = new Rectangle(point.x - 1, point.y - 1, 18, 18);
        this.entryStacks = new ArrayList<>();
    }
    
    /**
     * @see me.shedaniel.rei.api.widgets.Widgets#createSlot(me.shedaniel.math.Point)
     */
    @ApiStatus.ScheduledForRemoval
    @Deprecated
    @NotNull
    public static EntryWidget create(int x, int y) {
        return create(new Point(x, y));
    }
    
    /**
     * @see me.shedaniel.rei.api.widgets.Widgets#createSlot(me.shedaniel.math.Point)
     */
    @ApiStatus.ScheduledForRemoval
    @Deprecated
    @NotNull
    public static EntryWidget create(Point point) {
        return new EntryWidget(point);
    }
    
    @Override
    @NotNull
    public EntryWidget unmarkInputOrOutput() {
        noticeMark = 0;
        return this;
    }
    
    public EntryWidget markIsInput() {
        noticeMark = 1;
        return this;
    }
    
    public EntryWidget markIsOutput() {
        noticeMark = 2;
        return this;
    }
    
    @Override
    public byte getNoticeMark() {
        return noticeMark;
    }
    
    @Override
    public void setNoticeMark(byte noticeMark) {
        this.noticeMark = noticeMark;
    }
    
    @Override
    public void setInteractable(boolean interactable) {
        interactable(interactable);
    }
    
    @Override
    public boolean isInteractable() {
        return this.interactable;
    }
    
    @Override
    public void setInteractableFavorites(boolean interactableFavorites) {
        interactableFavorites(interactableFavorites);
    }
    
    @Override
    public boolean isInteractableFavorites() {
        return interactableFavorites;
    }
    
    public EntryWidget disableInteractions() {
        return interactable(false);
    }
    
    @NotNull
    @Override
    public EntryWidget interactable(boolean b) {
        interactable = b;
        interactableFavorites = interactableFavorites && interactable;
        return this;
    }
    
    public EntryWidget disableFavoritesInteractions() {
        return interactableFavorites(false);
    }
    
    @NotNull
    @Override
    public EntryWidget interactableFavorites(boolean b) {
        interactableFavorites = b && interactable;
        return this;
    }
    
    public EntryWidget noHighlight() {
        return highlight(false);
    }
    
    public EntryWidget highlight(boolean b) {
        highlight = b;
        return this;
    }
    
    @Override
    public boolean isHighlightEnabled() {
        return highlight;
    }
    
    @Override
    public void setHighlightEnabled(boolean highlights) {
        highlight(highlights);
    }
    
    public EntryWidget noTooltips() {
        return tooltips(false);
    }
    
    public EntryWidget tooltips(boolean b) {
        tooltips = b;
        return this;
    }
    
    @Override
    public void setTooltipsEnabled(boolean tooltipsEnabled) {
        tooltips(tooltipsEnabled);
    }
    
    @Override
    public boolean isTooltipsEnabled() {
        return tooltips;
    }
    
    public EntryWidget noBackground() {
        return background(false);
    }
    
    public EntryWidget background(boolean b) {
        background = b;
        return this;
    }
    
    @Override
    public void setBackgroundEnabled(boolean backgroundEnabled) {
        background(backgroundEnabled);
    }
    
    @Override
    public boolean isBackgroundEnabled() {
        return background;
    }
    
    public EntryWidget clearStacks() {
        entryStacks.clear();
        return this;
    }
    
    @NotNull
    @Override
    public Slot clearEntries() {
        return clearStacks();
    }
    
    @NotNull
    @Override
    public EntryWidget entry(EntryStack stack) {
        entryStacks.add(stack);
        return this;
    }
    
    @NotNull
    @Override
    public EntryWidget entries(Collection<EntryStack> stacks) {
        entryStacks.addAll(stacks);
        return this;
    }
    
    protected EntryStack getCurrentEntry() {
        if (entryStacks.isEmpty())
            return EntryStack.empty();
        if (entryStacks.size() == 1)
            return entryStacks.get(0);
        return entryStacks.get(Mth.floor((System.currentTimeMillis() / 500 % (double) entryStacks.size()) / 1f));
    }
    
    @NotNull
    @Override
    public List<EntryStack> getEntries() {
        return entryStacks;
    }
    
    public List<EntryStack> entries() {
        return entryStacks;
    }
    
    @NotNull
    @Override
    public Rectangle getBounds() {
        return bounds;
    }
    
    protected Rectangle getInnerBounds() {
        return new Rectangle(bounds.x + 1, bounds.y + 1, bounds.width - 2, bounds.height - 2);
    }
    
    @Override
    public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
        drawBackground(matrices, mouseX, mouseY, delta);
        drawCurrentEntry(matrices, mouseX, mouseY, delta);
        
        boolean highlighted = containsMouse(mouseX, mouseY);
        if (hasTooltips() && highlighted) {
            queueTooltip(matrices, mouseX, mouseY, delta);
        }
        if (hasHighlight() && highlighted) {
            drawHighlighted(matrices, mouseX, mouseY, delta);
        }
    }
    
    public final boolean hasTooltips() {
        return tooltips;
    }
    
    public final boolean hasHighlight() {
        return highlight;
    }
    
    protected void drawBackground(PoseStack matrices, int mouseX, int mouseY, float delta) {
        if (background) {
            minecraft.getTextureManager().bind(REIHelper.getInstance().isDarkThemeEnabled() ? RECIPE_GUI_DARK : RECIPE_GUI);
            blit(matrices, bounds.x, bounds.y, 0, 222, bounds.width, bounds.height);
        }
    }
    
    protected void drawCurrentEntry(PoseStack matrices, int mouseX, int mouseY, float delta) {
        EntryStack entry = getCurrentEntry();
        entry.setZ(100);
        entry.render(matrices, getInnerBounds(), mouseX, mouseY, delta);
    }
    
    protected void queueTooltip(PoseStack matrices, int mouseX, int mouseY, float delta) {
        Tooltip tooltip = getCurrentTooltip(new Point(mouseX, mouseY));
        if (tooltip != null) {
            if (interactableFavorites && ConfigObject.getInstance().doDisplayFavoritesTooltip() && !ConfigObject.getInstance().getFavoriteKeyCode().isUnknown()) {
                String name = ConfigObject.getInstance().getFavoriteKeyCode().getLocalizedName().getString();
                if (reverseFavoritesAction())
                    tooltip.getText().addAll(Stream.of(I18n.get("text.rei.remove_favorites_tooltip", name).split("\n"))
                            .map(TextComponent::new).collect(Collectors.toList()));
                else
                    tooltip.getText().addAll(Stream.of(I18n.get("text.rei.favorites_tooltip", name).split("\n"))
                            .map(TextComponent::new).collect(Collectors.toList()));
            }
            tooltip.queue();
        }
    }
    
    @Override
    public @Nullable Tooltip getCurrentTooltip(me.shedaniel.math.Point point) {
        return getCurrentEntry().getTooltip(point);
    }
    
    protected void drawHighlighted(PoseStack matrices, int mouseX, int mouseY, float delta) {
        RenderSystem.disableDepthTest();
        RenderSystem.colorMask(true, true, true, false);
        int color = REIHelper.getInstance().isDarkThemeEnabled() ? -1877929711 : -2130706433;
        setZ(300);
        Rectangle bounds = getInnerBounds();
        fillGradient(matrices, bounds.x, bounds.y, bounds.getMaxX(), bounds.getMaxY(), color, color);
        setZ(0);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.enableDepthTest();
    }
    
    @Override
    public List<? extends GuiEventListener> children() {
        return Collections.emptyList();
    }
    
    protected boolean wasClicked() {
        boolean b = this.wasClicked;
        this.wasClicked = false;
        return b;
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (containsMouse(mouseX, mouseY))
            this.wasClicked = true;
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (!interactable)
            return false;
        if (wasClicked() && containsMouse(mouseX, mouseY)) {
            if (interactableFavorites && ConfigObject.getInstance().isFavoritesEnabled() && containsMouse(PointHelper.ofMouse()) && !getCurrentEntry().isEmpty()) {
                ModifierKeyCode keyCode = ConfigObject.getInstance().getFavoriteKeyCode();
                EntryStack entry = getCurrentEntry().copy();
                entry.setAmount(127);
                if (keyCode.matchesMouse(button)) {
                    if (reverseFavoritesAction())
                        ConfigObject.getInstance().getFavorites().removeIf(entry::equalsIgnoreAmount);
                    else if (!CollectionUtils.anyMatchEqualsEntryIgnoreAmount(ConfigObject.getInstance().getFavorites(), entry))
                        ConfigObject.getInstance().getFavorites().add(entry);
                    ConfigManager.getInstance().saveConfig();
                    FavoritesListWidget favoritesListWidget = ContainerScreenOverlay.getFavoritesListWidget();
                    if (favoritesListWidget != null)
                        favoritesListWidget.updateSearch(ContainerScreenOverlay.getEntryListWidget(), ScreenHelper.getSearchField().getText());
                    return true;
                }
            }
            if ((ConfigObject.getInstance().getRecipeKeybind().getType() != InputConstants.Type.MOUSE && button == 0) || ConfigObject.getInstance().getRecipeKeybind().matchesMouse(button))
                return ClientHelper.getInstance().openView(ClientHelper.ViewSearchBuilder.builder().addRecipesFor(getCurrentEntry()).setOutputNotice(getCurrentEntry()).fillPreferredOpenedCategory());
            else if ((ConfigObject.getInstance().getUsageKeybind().getType() != InputConstants.Type.MOUSE && button == 1) || ConfigObject.getInstance().getUsageKeybind().matchesMouse(button))
                return ClientHelper.getInstance().openView(ClientHelper.ViewSearchBuilder.builder().addUsagesFor(getCurrentEntry()).setInputNotice(getCurrentEntry()).fillPreferredOpenedCategory());
        }
        return false;
    }
    
    @ApiStatus.Internal
    protected boolean cancelDeleteItems(EntryStack stack) {
        return false;
    }
    
    protected boolean reverseFavoritesAction() {
        return false;
    }
    
    @Override
    public boolean keyPressed(int int_1, int int_2, int int_3) {
        if (!interactable)
            return false;
        if (containsMouse(PointHelper.ofMouse())) {
            if (interactableFavorites && ConfigObject.getInstance().isFavoritesEnabled() && containsMouse(PointHelper.ofMouse()) && !getCurrentEntry().isEmpty()) {
                ModifierKeyCode keyCode = ConfigObject.getInstance().getFavoriteKeyCode();
                EntryStack entry = getCurrentEntry().copy();
                entry.setAmount(127);
                if (keyCode.matchesKey(int_1, int_2)) {
                    if (reverseFavoritesAction())
                        ConfigObject.getInstance().getFavorites().removeIf(entry::equalsIgnoreAmount);
                    else if (!CollectionUtils.anyMatchEqualsEntryIgnoreAmount(ConfigObject.getInstance().getFavorites(), entry))
                        ConfigObject.getInstance().getFavorites().add(entry);
                    ConfigManager.getInstance().saveConfig();
                    FavoritesListWidget favoritesListWidget = ContainerScreenOverlay.getFavoritesListWidget();
                    if (favoritesListWidget != null)
                        favoritesListWidget.updateSearch(ContainerScreenOverlay.getEntryListWidget(), ScreenHelper.getSearchField().getText());
                    return true;
                }
            }
            if (ConfigObject.getInstance().getRecipeKeybind().matchesKey(int_1, int_2))
                return ClientHelper.getInstance().openView(ClientHelper.ViewSearchBuilder.builder().addRecipesFor(getCurrentEntry()).setOutputNotice(getCurrentEntry()).fillPreferredOpenedCategory());
            else if (ConfigObject.getInstance().getUsageKeybind().matchesKey(int_1, int_2))
                return ClientHelper.getInstance().openView(ClientHelper.ViewSearchBuilder.builder().addUsagesFor(getCurrentEntry()).setInputNotice(getCurrentEntry()).fillPreferredOpenedCategory());
        }
        return false;
    }
}
