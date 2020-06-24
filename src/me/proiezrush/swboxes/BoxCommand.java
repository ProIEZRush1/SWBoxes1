package me.proiezrush.swboxes;

import me.proiezrush.swboxes.boxes.Box;
import me.proiezrush.swboxes.boxes.BoxManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

@SuppressWarnings("deprecation")
public class BoxCommand implements CommandExecutor {

    private Main m;
    public BoxCommand(Main m) {
        this.m = m;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (command.getName().equalsIgnoreCase("swboxes")) {
            if (commandSender instanceof Player) {
                Player p = (Player) commandSender;
                if (p.hasPermission("swboxes.command")) {
                    if (args.length == 1) {
                        String a = args[0];
                        if (a.equalsIgnoreCase("reload")) {
                            if (p.hasPermission("swboxes.admin")) {
                                m.getC().reload();
                                m.getBoxes().reload();
                                p.sendMessage(m.getC().get(p, "config-reloaded"));
                            }
                            else p.sendMessage(m.getC().get(p, "no-permission"));
                        }
                        else if (a.equalsIgnoreCase("wand")) {
                            if (p.hasPermission("swboxes.admin")) {
                                p.getInventory().addItem(m.getWand());
                                p.sendMessage(m.getC().get(p, "wand-received"));
                            }
                            else p.sendMessage(m.getC().get(p, "no-permission"));
                        }
                        else if (a.equalsIgnoreCase("menu")) {
                            m.removeBoxMenu(p);
                            m.addBoxMenu(m.createBoxMenu(m.getBoxPlayer(p.getName())));
                            p.openInventory(m.getBoxMenu(p.getName()).getInvBuilder().getInventory());
                            p.sendMessage(m.getC().get(p, "menu-opened"));
                        }
                        else h(p);
                    }
                    else if (args.length == 2) {
                        String a = args[0];
                        String b = args[1];
                        if (a.equalsIgnoreCase("create")) {
                            if (p.hasPermission("swboxes.admin")) {
                                if (m.getBox(b) != null) {
                                    p.sendMessage(m.getC().get(p, "box-already-exists"));
                                    return true;
                                }
                                BoxManager.create(m, p, b);
                                m.getBoxPlayer(p.getName()).setInEditorMode(true);
                                p.sendMessage(m.getC().get(p, "box-created"));
                            }
                            else p.sendMessage(m.getC().get(p, "no-permission"));
                        }
                        else if (a.equalsIgnoreCase("edit")) {
                            if (p.hasPermission("swboxes.admin")) {
                                if (m.getBox(b) == null) {
                                    p.sendMessage(m.getC().get(p, "box-not-exists"));
                                    return true;
                                }
                                if (m.getBoxPlayer(p.getName()).isInEditorMode()) {
                                    p.sendMessage(m.getC().get(p, "already-editing"));
                                    return true;
                                }
                                BoxManager.edit(m, p, b);
                                m.getBoxPlayer(p.getName()).setInEditorMode(true);
                                p.sendMessage(m.getC().get(p, "edit-box"));
                            }
                            else p.sendMessage(m.getC().get(p, "no-permission"));
                        }
                        else if (a.equalsIgnoreCase("save")) {
                            if (p.hasPermission("swboxes.admin")) {
                                if (!m.getBoxPlayer(p.getName()).isInEditorMode()) {
                                    p.sendMessage(m.getC().get(p, "not-editing"));
                                    return true;
                                }
                                if (m.getBoxPlayer(p.getName()).getPos1().getLocation() == null || m.getBoxPlayer(p.getName()).getPos2().getLocation() == null) {
                                    p.sendMessage(m.getC().get(p, "pos-not-set"));
                                    return true;
                                }
                                if (!p.getLocation().getWorld().getName().equals(b)) {
                                    p.sendMessage(m.getC().get(p, "not-editing"));
                                    return true;
                                }
                                BoxManager.save(m, p, b);
                                m.getBoxPlayer(p.getName()).setInEditorMode(false);
                                p.sendMessage(m.getC().get(p, "box-saved"));
                            }
                            else p.sendMessage(m.getC().get(p, "no-permission"));
                        }
                        else if (a.equalsIgnoreCase("delete")) {
                            if (p.hasPermission("swboxes.admin")) {
                                if (m.getBox(b) == null) {
                                    p.sendMessage(m.getC().get(p, "box-not-exists"));
                                    return true;
                                }
                                if (b.equalsIgnoreCase(m.getDefaultBox().getName())) {
                                    p.sendMessage(m.getC().get(p, "not-delete-default"));
                                    return true;
                                }
                                BoxManager.delete(m, b);
                                p.sendMessage(m.getC().get(p, "box-deleted"));
                            }
                            else p.sendMessage(m.getC().get(p, "no-permission"));
                        }
                        else if (a.equalsIgnoreCase("seticon")) {
                            if (p.hasPermission("swboxes.admin")) {
                                if (m.getBox(b) == null) {
                                    p.sendMessage(m.getC().get(p, "box-not-exists"));
                                    return true;
                                }
                                Box box = m.getBox(b);
                                box.setIcon(p.getItemInHand().getTypeId());
                                box.setIconData(p.getItemInHand().getDurability());
                                BoxManager.saveBox(m, box);
                                p.sendMessage(m.getC().get(p, "icon-set"));
                            }
                            else p.sendMessage(m.getC().get(p, "no-permission"));
                        }
                        else h(p);
                    }
                    else if (args.length == 3) {
                        String a = args[0];
                        String b = args[1];
                        String c = args[2];
                        if (a.equalsIgnoreCase("setdisplay")) {
                            if (p.hasPermission("swboxes.admin")) {
                                if (m.getBox(b) == null) {
                                    p.sendMessage(m.getC().get(p, "box-not-exists"));
                                    return true;
                                }
                                Box box = m.getBox(b);
                                box.setDisplay(c);
                                BoxManager.saveBox(m, box);
                            }
                            else p.sendMessage(m.getC().get(p, "no-permission"));
                        }
                        else h(p);
                    }
                    else h(p);
                }
                else p.sendMessage(m.getC().get(p, "no-permission"));
            }
            else commandSender.sendMessage(m.getC().get(null, "console-error"));
        }
        return false;
    }

    private void h(Player p) {
        List<String> list = m.getC().getList("command-help");
        for (String s : list) {
            p.sendMessage(m.getAdm().parsePlaceholders(p, s));
        }
    }

}
