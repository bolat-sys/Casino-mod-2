package com.bolat.casinomod.menu;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class CasinoScreen extends AbstractContainerScreen<CasinoMenu> {
    public CasinoScreen(CasinoMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 340;
        this.imageHeight = 242;
        this.inventoryLabelY = 145;
        this.titleLabelY = -1000;
    }

    @Override protected void init() {
        super.init();
        int x = leftPos, y = topPos;
        addRenderableWidget(Button.builder(Component.literal("x2"), b -> minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 0)).bounds(x + 91, y + 112, 32, 20).build());
        addRenderableWidget(Button.builder(Component.literal("x4"), b -> minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 1)).bounds(x + 126, y + 112, 32, 20).build());
        addRenderableWidget(Button.builder(Component.literal("x8"), b -> minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 2)).bounds(x + 161, y + 112, 32, 20).build());
        addRenderableWidget(Button.builder(Component.literal("30%"), b -> minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 3)).bounds(x + 197, y + 112, 38, 20).build());
        addRenderableWidget(Button.builder(Component.literal("50%"), b -> minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 4)).bounds(x + 238, y + 112, 38, 20).build());
        addRenderableWidget(Button.builder(Component.literal("70%"), b -> minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 5)).bounds(x + 279, y + 112, 38, 20).build());
        addRenderableWidget(Button.builder(Component.literal("✦ UPGRADE"), b -> minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 6)).bounds(x + 12, y + 112, 75, 20).build());
    }

    @Override protected void renderBg(GuiGraphics g, float partialTick, int mouseX, int mouseY) {
        int x = leftPos, y = topPos;
        g.fill(x, y, x + imageWidth, y + imageHeight, 0xFF10101D);
        g.fill(x + 3, y + 3, x + imageWidth - 3, y + imageHeight - 3, 0xFF24294E);
        g.fill(x + 8, y + 8, x + imageWidth - 8, y + 31, 0xFF343B70);
        g.drawCenteredString(font, "ITEM UPGRADE", x + imageWidth / 2, y + 15, 0xFFFFD76A);

        // Stake and prize panels
        g.fill(x + 12, y + 38, x + 88, y + 104, 0xFF202A50);
        g.fill(x + 252, y + 38, x + 328, y + 104, 0xFF202A50);
        g.fill(x + 12, y + 38, x + 88, y + 40, 0xFFFFC857);
        g.fill(x + 252, y + 38, x + 328, y + 40, 0xFF55E69A);
        g.drawCenteredString(font, "STAKE", x + 50, y + 44, 0xFFFFD36A);
        g.drawCenteredString(font, "PRIZE", x + 290, y + 44, 0xFF70F0A6);

        // Probability ring
        int cx = x + 170, cy = y + 72;
        int chance = menu.getChance();
        for (int i = 0; i < 48; i++) {
            double a = (Math.PI * 2 * i / 48.0) - Math.PI / 2;
            int px = cx + (int)(Math.cos(a) * 43);
            int py = cy + (int)(Math.sin(a) * 43);
            int color = i < Math.round(chance * 48.0 / 100.0) ? 0xFFFFC84D : 0xFF485783;
            g.fill(px - 1, py - 2, px + 2, py + 2, color);
        }
        g.drawCenteredString(font, chance + "%", cx, cy - 7, 0xFFFFFFFF);
        g.drawCenteredString(font, "CHANCE", cx, cy + 8, 0xFFB9C9F4);
        g.drawCenteredString(font, "Multiplier x" + menu.getMultiplier(), cx, y + 96, 0xFFFFD36A);
        g.drawCenteredString(font, "Base chance " + menu.getBaseChance() + "%", cx, y + 105, 0xFFB9C9F4);

        g.fill(x + 10, y + 139, x + 330, y + 161, 0xFF1A2040);
        g.drawString(font, "INVENTORY", x + 12, y + 132, 0xFFE7E9FF, false);
        g.drawCenteredString(font, menu.getStatus(), x + imageWidth / 2, y + 226, 0xFFE8E8FF);
    }

    @Override protected void renderLabels(GuiGraphics g, int mouseX, int mouseY) { }

    @Override public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        renderBackground(g);
        super.render(g, mouseX, mouseY, partialTick);
        renderTooltip(g, mouseX, mouseY);
    }
}
