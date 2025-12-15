package com.darksoldier1404.dpev.commands;

import com.darksoldier1404.dpev.Evaluation;
import com.darksoldier1404.dpev.functions.DPEVFunction;
import com.darksoldier1404.dppc.builder.command.CommandBuilder;
import org.bukkit.entity.Player;

public class DPEVCommand {
    private final CommandBuilder builder;

    public DPEVCommand() {
        this.builder = new CommandBuilder(Evaluation.plugin);
        this.builder.addSubCommand("reload", "dpev.admin", "/dpev reload", false, (p, args) -> {
            if (args.length == 1) {
                Evaluation.plugin.reload();
                p.sendMessage(Evaluation.plugin.getPrefix() + "§a플러그인 설정이 리로드되었습니다.");
                return true;
            } else {
                return false;
            }
        });
        this.builder.addSubCommand("items", "dpev.admin", "/dpev items", false, (p, args) -> {
            if (args.length == 1) {
                DPEVFunction.editItems(p);
                return true;
            } else {
                return false;
            }
        });
        this.builder.addSubCommand("price", "dpev.admin", "/dpev price", true, (p, args) -> {
            if (args.length == 1) {
                DPEVFunction.editPrice(p);
                return true;
            } else {
                return false;
            }
        });
        this.builder.addSubCommand("evaluate", "/dpev evaluate", true, (p, args) -> {
            if (args.length == 1) {
                DPEVFunction.openEvaluationGUI((Player)p);
                return true;
            } else {
                return false;
            }
        });
        this.builder.addSubCommand("sell", "/dpev sell", true, (p, args) -> {
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
