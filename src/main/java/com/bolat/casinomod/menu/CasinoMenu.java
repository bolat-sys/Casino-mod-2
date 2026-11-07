package com.bolat.casinomod.menu;

import com.bolat.casinomod.registry.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;

public class CasinoMenu extends AbstractContainerMenu {
    private final net.minecraft.world.SimpleContainer casino = new net.minecraft.world.SimpleContainer(2);
    private final Player owner;
    private int baseChance = 50;
    private int multiplier = 2;
    private int lastChance = 25;
    private String status = "Choose stake and prize";

    public CasinoMenu(int id, Inventory inventory, FriendlyByteBuf data) { this(id, inventory); }
    public CasinoMenu(int id, Inventory inventory) {
        super(ModMenus.CASINO_MENU.get(), id);
        this.owner = inventory.player;
        addSlot(new Slot(casino, 0, 47, 62) {
            @Override public boolean mayPlace(ItemStack stack) { return !stack.isEmpty(); }
        });
        addSlot(new Slot(casino, 1, 273, 62) {
            @Override public boolean mayPlace(ItemStack stack) { return !stack.isEmpty(); }
        });
        for (int row = 0; row < 3; row++) for (int col = 0; col < 9; col++)
            addSlot(new Slot(inventory, col + row * 9 + 9, 92 + col * 18, 151 + row * 18));
        for (int col = 0; col < 9; col++) addSlot(new Slot(inventory, col, 92 + col * 18, 209));
        addDataSlot(new DataSlot() {
            @Override public int get() { return baseChance; }
            @Override public void set(int value) { baseChance = value; }
        });
        addDataSlot(new DataSlot() {
            @Override public int get() { return multiplier; }
            @Override public void set(int value) { multiplier = value; }
        });
        addDataSlot(new DataSlot() {
            @Override public int get() { return lastChance; }
            @Override public void set(int value) { lastChance = value; }
        });
    }

    public void selectPrize(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return;
        ItemStack current = casino.getItem(1);
        if (!current.isEmpty()) return;
        casino.setItem(1, stack.copyWithCount(1));
        broadcastChanges();
    }

    public int getBaseChance() { return baseChance; }
    public int getMultiplier() { return multiplier; }
    public int getChance() { return baseChance; }
    public String getStatus() { return status; }

    @Override public boolean clickMenuButton(Player player, int id) {
        if (player != owner) return false;
        if (id == 0) multiplier = 2;
        else if (id == 1) multiplier = 4;
        else if (id == 2) multiplier = 8;
        else if (id == 3) baseChance = 30;
        else if (id == 4) baseChance = 50;
        else if (id == 5) baseChance = 70;
        else if (id == 6) { upgrade(); }
        broadcastChanges();
        return true;
    }

    private void upgrade() {
        if (!(owner instanceof ServerPlayer player)) return;
        ItemStack stake = casino.getItem(0);
        ItemStack prize = casino.getItem(1);
        if (stake.isEmpty() || prize.isEmpty()) {
            status = "Place both stake and prize first!";
            player.sendSystemMessage(Component.literal("§cPlace both a stake and a prize first."));
            return;
        }
        lastChance = Math.max(1, Math.min(95, baseChance));
        stake.shrink(1);
        if (stake.isEmpty()) casino.setItem(0, ItemStack.EMPTY);
        if (RandomSource.create().nextInt(100) < lastChance) {
            ItemStack reward = prize.copy();
            reward.setCount(multiplier);
            prize.shrink(1);
            if (prize.isEmpty()) casino.setItem(1, ItemStack.EMPTY);
            while (!reward.isEmpty()) {
                ItemStack part = reward.split(Math.min(reward.getMaxStackSize(), reward.getCount()));
                if (!player.getInventory().add(part)) player.drop(part, false);
            }
            status = "YOU WON!";
            player.sendSystemMessage(Component.literal("§aUPGRADE WON! §fChance: " + lastChance + "%"));
        } else {
            status = "YOU LOST!";
            player.sendSystemMessage(Component.literal("§cUPGRADE LOST! §7Chance: " + lastChance + "%"));
        }
        casino.setChanged();
    }

    @Override public boolean stillValid(Player player) { return player == owner && player.isAlive(); }

    @Override public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        if (index < 0 || index >= slots.size()) return result;
        Slot slot = slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack source = slot.getItem(); result = source.copy();
            if (index < 2) {
                if (!moveItemStackTo(source, 2, slots.size(), true)) return ItemStack.EMPTY;
            } else {
                int target = casino.getItem(0).isEmpty() ? 0 : 1;
                if (!moveItemStackTo(source, target, target + 1, false)) return ItemStack.EMPTY;
            }
            if (source.isEmpty()) slot.set(ItemStack.EMPTY); else slot.setChanged();
        }
        return result;
    }
}
