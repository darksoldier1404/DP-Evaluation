package com.darksoldier1404.dpev.functions;

import com.darksoldier1404.dpev.Evaluation;
import com.darksoldier1404.dppc.api.essentials.MoneyAPI;
import com.darksoldier1404.dppc.api.inventory.DInventory;
import com.darksoldier1404.dppc.api.inventory.DInventory.PageItemSet;
import com.darksoldier1404.dppc.utils.ConfigUtils;
import com.darksoldier1404.dppc.utils.NBT;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class DPEVFunction {
    public static void editItems(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§c플레이어만 사용 가능한 명령어입니다.");
        } else {
            Player p = (Player)sender;
            Evaluation.items.setChannel(1);
            Evaluation.items.openInventory(p);
        }
    }

    public static void editPrice(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§c플레이어만 사용 가능한 명령어입니다.");
        } else {
            Player p = (Player)sender;
            Evaluation.items.setChannel(2);
            Evaluation.items.openInventory(p);
        }
    }

    public static void setPrice(Player p, DInventory inv, int slot, String price) {
        int minPrice = Integer.parseInt(price.split("-")[0]);
        int maxPrice = Integer.parseInt(price.split("-")[1]);
        int page = inv.getCurrentPage();
        Evaluation.plugin.config.set("Prices." + page + "." + slot + ".MIN", minPrice);
        Evaluation.plugin.config.set("Prices." + page + "." + slot + ".MAX", maxPrice);
        saveItems(inv);
        Bukkit.getScheduler().runTask(Evaluation.plugin, () -> {
            inv.openInventory(p);
        });
    }

    public static void saveItems(DInventory inv) {
        inv.applyChanges();
        Evaluation.items = inv;
        ConfigUtils.savePluginConfig(Evaluation.plugin, Evaluation.items.serialize(Evaluation.plugin.config));
    }

    public static void openEvaluationGUI(Player p) {
        DInventory inv = new DInventory("감정", 27, Evaluation.plugin);
        inv.setChannel(101);
        ItemStack pane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = pane.getItemMeta();
        meta.setDisplayName(" ");
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        pane.setItemMeta(meta);
        pane = NBT.setStringTag(pane, "dppc_clickcancel", "true");

        for(int i = 0; i < 27; ++i) {
            inv.setItem(i, pane);
        }

        ItemStack confirm = new ItemStack(Material.ANVIL);
        ItemMeta im = confirm.getItemMeta();
        im.setDisplayName("§a감정하기");
        im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        confirm.setItemMeta(im);
        confirm = NBT.setStringTag(confirm, "dpevs_confirm", "true");
        confirm = NBT.setStringTag(confirm, "dppc_clickcancel", "true");
        inv.setItem(13, null);
        inv.setItem(22, confirm);
        inv.openInventory(p);
    }

    public static void evaluateItem(Player p, ItemStack item) {
        if (item != null && !item.getType().isAir()) {
            if (NBT.hasTagKey(item, "dpevs_price")) {
                p.sendMessage("§c이미 감정된 아이템입니다.");
            } else {
                int price = getRandomPrice(item);
                if (price <= 0) {
                    p.sendMessage("§c해당 아이템은 감정할 수 없습니다.");
                } else {
                    p.sendMessage("§a아이템 감정이 완료되었습니다. 가격: §e" + price + "원");
                    ItemMeta meta = item.getItemMeta();
                    List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList();
                    lore.add("§e감정가 : §e" + price + "원");
                    meta.setLore(lore);
                    item.setItemMeta(meta);
                    NBT.setIntTag(item, "dpevs_price", price);
                }

            }
        } else {
            p.sendMessage("§c감정할 아이템을 빈칸에 넣어주세요.");
        }
    }

    public static int getRandomPrice(ItemStack item) {
        PageItemSet pi = findIteminInventory(item);
        if (pi == null) {
            return 0;
        } else {
            int page = pi.getPage();
            int slot = pi.getSlot();
            int minPrice = Evaluation.plugin.config.getInt("Prices." + page + "." + slot + ".MIN", 0);
            int maxPrice = Evaluation.plugin.config.getInt("Prices." + page + "." + slot + ".MAX", 0);
            return (int)(Math.random() * (double)(maxPrice - minPrice + 1) + (double)minPrice);
        }
    }

    public static PageItemSet findIteminInventory(ItemStack item) {
        DInventory inv = Evaluation.items;
        Iterator var2 = inv.getAllPageItemSets().iterator();

        PageItemSet pi;
        do {
            if (!var2.hasNext()) {
                return null;
            }

            pi = (PageItemSet)var2.next();
        } while(!pi.getItem().isSimilar(item));

        return pi;
    }

    public static void openSellGUI(Player p) {
        DInventory inv = new DInventory("아이템 판매", 54, Evaluation.plugin);
        inv.setChannel(102);
        ItemStack pane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = pane.getItemMeta();
        meta.setDisplayName(" ");
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        pane.setItemMeta(meta);
        pane = NBT.setStringTag(pane, "dppc_clickcancel", "true");

        for(int i = 45; i < 54; ++i) {
            inv.setItem(i, pane);
        }

        ItemStack confirm = new ItemStack(Material.EMERALD);
        ItemMeta im = confirm.getItemMeta();
        im.setDisplayName("§a판매하기");
        im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        confirm.setItemMeta(im);
        confirm = NBT.setStringTag(confirm, "dpevs_sell", "true");
        confirm = NBT.setStringTag(confirm, "dppc_clickcancel", "true");
        inv.setItem(49, confirm);
        inv.openInventory(p);
    }

    public static void sellItems(Player p, DInventory inv) {
        List<ItemStack> toSell = new ArrayList();
        ItemStack[] var3 = inv.getContents();
        int var4 = var3.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            ItemStack item = var3[var5];
            if (item != null && !item.getType().isAir() && NBT.hasTagKey(item, "dpevs_price")) {
                toSell.add(item);
            }
        }

        if (toSell.isEmpty()) {
            p.sendMessage("§c판매할 아이템이 없습니다.");
        } else {
            int totalPrice = 0;

            ItemStack item;
            for(Iterator var8 = toSell.iterator(); var8.hasNext(); totalPrice += NBT.getIntegerTag(item, "dpevs_price") * item.getAmount()) {
                item = (ItemStack)var8.next();
            }

            p.getInventory().removeItem(toSell.toArray(new ItemStack[0]));
            MoneyAPI.addMoney(p, totalPrice);
            toSell.forEach((i) -> {
                i.setAmount(0);
            });
            inv.applyChanges();
            p.sendMessage("§a아이템 판매가 완료되었습니다. 총 판매 금액: §e" + totalPrice + "원");
        }
    }

    public static void returnItems(Player p, DInventory inv) {
        ItemStack[] var2 = inv.getContents();
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            ItemStack item = var2[var4];
            if (item != null && !item.getType().isAir() && !NBT.hasTagKey(item, "dppc_clickcancel")) {
                p.getInventory().addItem(item);
            }
        }

    }
}
