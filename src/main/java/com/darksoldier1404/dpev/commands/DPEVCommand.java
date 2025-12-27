package com.darksoldier1404.dpev.commands;

import com.darksoldier1404.dpev.Evaluation;
import com.darksoldier1404.dpev.functions.DPEVFunction;
import com.darksoldier1404.dppc.builder.command.CommandBuilder;
import org.bukkit.entity.Player;

public class DPEVCommand {
    private final CommandBuilder builder;

    public DPEVCommand() {
        this.builder = new CommandBuilder(Evaluation.plugin);
        this.builder.addSubCommand("reload", "dpev.admin", Evaluation.plugin.getLang().get("command_reload_description"), false, (p, args) -> {
            if (args.length == 1) {
                Evaluation.plugin.reload();
                p.sendMessage(Evaluation.plugin.getPrefix() + Evaluation.plugin.getLang().get("plugin_reload_success"));
                return true;
            } else {
                return false;
            }
        });
        this.builder.addSubCommand("items", "dpev.admin", Evaluation.plugin.getLang().get("command_items_description"), false, (p, args) -> {
            if (args.length == 1) {
                DPEVFunction.editItems(p);
                return true;
            } else {
                return false;
            }
        });
        this.builder.addSubCommand("price", "dpev.admin", Evaluation.plugin.getLang().get("command_price_description"), true, (p, args) -> {
            if (args.length == 1) {
                DPEVFunction.editPrice(p);
                return true;
            } else {
                return false;
            }
        });
        this.builder.addSubCommand("evaluate", Evaluation.plugin.getLang().get("command_evaluate_description"), true, (p, args) -> {
            if (args.length == 1) {
                DPEVFunction.openEvaluationGUI((Player)p);
                return true;
            } else {
                return false;
            }
        });
        this.builder.addSubCommand("sell", Evaluation.plugin.getLang().get("command_sell_description"), true, (p, args) -> {
            if (args.length == 1) {
                DPEVFunction.openSellGUI((Player)p);
                return true;
            } else {
                return false;
            }
        });
    }

    public CommandBuilder getBuilder() {
        return this.builder;
    }
}
