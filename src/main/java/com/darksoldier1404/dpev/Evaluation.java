package com.darksoldier1404.dpev;

import com.darksoldier1404.dpev.commands.DPEVCommand;
import com.darksoldier1404.dpev.events.DPEVEvent;
import com.darksoldier1404.dpev.functions.DPEVFunction;
import com.darksoldier1404.dppc.annotation.DPPCoreVersion;
import com.darksoldier1404.dppc.api.inventory.DInventory;
import com.darksoldier1404.dppc.data.DPlugin;
import com.darksoldier1404.dppc.utils.PluginUtil;
import com.darksoldier1404.dppc.utils.Tuple;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@DPPCoreVersion(
        since = "5.2.3"
)
public class Evaluation extends DPlugin {
    public static Evaluation plugin;
    public static DInventory items;
    public static final Map<UUID, Tuple<Integer, DInventory>> currentEdit = new HashMap();

    public Evaluation() {
        super(false);
        plugin = this;
        init();
    }

    public static Evaluation getInstance() {
        return plugin;
    }

    public void onEnable() {
        PluginUtil.addPlugin(plugin, 28442);
        items = (new DInventory(Evaluation.plugin.getLang().get("evaluation_item_settings_title"), 54, true, true, plugin)).deserialize(plugin.config);
        getCommand("dpev").setExecutor((new DPEVCommand()).getBuilder());
        getServer().getPluginManager().registerEvents(new DPEVEvent(), plugin);
    }

    public void onDisable() {
        DPEVFunction.saveItems(items);
    }
}
