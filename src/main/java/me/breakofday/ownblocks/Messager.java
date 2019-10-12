package me.breakofday.ownblocks;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

public class Messager {

	private static final String defaultPrefix = ChatColor.translateAlternateColorCodes('&', "&3[&bOwnBlocks&3] &f");
	private static final ConsoleCommandSender console = Bukkit.getConsoleSender();

	private final String prefix;

	public Messager(String prefix) {
		if(prefix != null) {
			this.prefix = prefix;
		} else {
			this.prefix = "";
		}
	}

	public Messager() {
		this.prefix = defaultPrefix;
	}

	public void sendConsoleMessage(String msg) {
		console.sendMessage(prefix + msg);
	}

	public void broadcastMessage(String msg) {
		Bukkit.broadcastMessage(prefix + msg);
	}

	public void sendMessages(CommandSender cs, String... strs) {
		for(int c = 0; c < strs.length; c++) {
			strs[c] = prefix + strs[c];
		}
		cs.sendMessage(strs);
	}

}
