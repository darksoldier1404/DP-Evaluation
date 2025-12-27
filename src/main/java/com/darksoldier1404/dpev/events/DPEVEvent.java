package com.darksoldier1404.dpev.events;

import com.darksoldier1404.dpev.Evaluation;
import com.darksoldier1404.dpev.functions.DPEVFunction;
import com.darksoldier1404.dppc.api.inventory.DInventory;
import com.darksoldier1404.dppc.utils.NBT;
import com.darksoldier1404.dppc.utils.Tuple;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class DPEVEvent implements Listener {
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        Player p = (Player)e.getPlayer();
        if (e.getInventory().getHolder() != null) {
            if (e.getInventory().getHolder() instanceof DInventory) {
                DInventory inv = (DInventory)e.getInventory().getHolder();
                if (inv.isValidHandler(Evaluation.plugin)) {
                    if (inv.isValidChannel(1)) {
                        DPEVFunction.saveItems(inv);
                        p.sendMessage(Evaluation.plugin.getLang().get("item_settings_saved"));
                        return;
                    }

                    if (inv.isValidChannel(101)) {
                        inv.applyChanges();
                        ItemStack item = inv.getItem(13);
                        if (item != null && !item.getType().isAir()) {
                            p.getInventory().addItem(item.clone());
                        }

                        return;
                    }

                    if (inv.isValidChannel(102)) {
                        inv.applyChanges();
                        DPEVFunction.returnItems(p, inv);
                        return;
                    }
                }
            }

        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getClickedInventory() != null && e.getClickedInventory().getHolder() != null) {
            DInventory inv;
            ItemStack item;
            if (e.getClickedInventory() instanceof PlayerInventory && e.getInventory().getHolder() instanceof DInventory) {
                inv = (DInventory)e.getInventory().getHolder();
                if (inv.isValidHandler(Evaluation.plugin) && inv.isValidChannel(101)) {
                    e.setCancelled(true);
                    if (inv.getItem(13) != null) {
                        return;
                    }
                    item = e.getCurrentItem();
                    if (item != null && !item.getType().isAir()) {
                        ItemStack sel = item.clone();
                        item.setAmount(item.getAmount() - 1);
                        e.setCurrentItem(item);
                        sel.setAmount(1);
                        inv.setItem(13, sel);
                        inv.applyChanges();
                    }
                    return;
                }
            }

            if (e.getClickedInventory().getHolder() instanceof DInventory) {
                inv = (DInventory)e.getClickedInventory().getHolder();
                Player p = (Player)e.getWhoClicked();
                if (inv.isValidHandler(Evaluation.plugin)) {
                    item = e.getCurrentItem();
                    if (item == null || item.getType().isAir()) {
                        return;
                    }

                    if (inv.isValidChannel(101)) {
                        if (e.getSlot() == 13) {
                            e.setCancelled(true);
                            p.getInventory().addItem(item.clone());
                            inv.setItem(13, null);
                            inv.applyChanges();
                            return;
                        }

                        if (NBT.hasTagKey(item, "dpevs_confirm")) {
                            e.setCancelled(true);
                            DPEVFunction.evaluateItem(p, inv.getItem(13));
                            inv.applyChanges();
                            return;
                        }
                    }

                    if (inv.isValidChannel(102) && NBT.hasTagKey(item, "dpevs_sell")) {
                        e.setCancelled(true);
                        DPEVFunction.sellItems(p, inv);
                        inv.applyChanges();
                        return;
                    }

                    if (NBT.hasTagKey(item, "dppc_prevpage")) {
                        inv.applyChanges();
                        inv.prevPage();
                        e.setCancelled(true);
                        return;
                    }

                    if (NBT.hasTagKey(item, "dppc_nextpage")) {
                        inv.applyChanges();
                        inv.nextPage();
                        e.setCancelled(true);
                        return;
                    }

                    if (NBT.hasTagKey(item, "dppc_clickcancel") || NBT.hasTagKey(item, "dpvs_barrier")) {
                        e.setCancelled(true);
                        return;
                    }

                    if (inv.isValidChannel(2)) {
                        p.closeInventory();
                        Evaluation.currentEdit.put(p.getUniqueId(), Tuple.of(e.getSlot(), inv));
                        p.sendMessage(Evaluation.plugin.getLang().get("set_price_via_chat"));
                    }
                }
            }

        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        if (Evaluation.currentEdit.containsKey(p.getUniqueId())) {
            int slot = (Integer)((Tuple)Evaluation.currentEdit.get(p.getUniqueId())).getA();
            DInventory inv = (DInventory)((Tuple)Evaluation.currentEdit.get(p.getUniqueId())).getB();
            if (inv != null && inv.isValidHandler(Evaluation.plugin) && inv.getChannel() == 2) {
                e.setCancelled(true);
                String message = e.getMessage();
                if (message.matches("\\d+-\\d+")) {
                    DPEVFunction.setPrice(p, inv, slot, message);
                }

                Evaluation.currentEdit.remove(p.getUniqueId());
            }
        }

    }
}
