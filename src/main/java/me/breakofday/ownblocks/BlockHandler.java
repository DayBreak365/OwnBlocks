package me.breakofday.ownblocks;

import java.util.Iterator;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import me.breakofday.ownblocks.database.BlockDatabase;

class BlockHandler implements Listener {

	private final BlockDatabase database;
	private final Language language;

	BlockHandler(BlockDatabase database) {
		this.database = database;
		this.language = OwnBlocks.getPlugin().getLanguage();
	}

	@EventHandler
	private void onBlockPlace(BlockPlaceEvent e) {
		database.setOwner(e.getBlock().getLocation(), e.getPlayer().getUniqueId());
	}

	@EventHandler
	private void onBlockBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		Location loc = e.getBlock().getLocation();
		if (database.hasOwner(loc)) {
			if (p.getUniqueId().equals(database.getOwner(loc))) {
				database.deleteOwner(loc);
			} else {
				e.setCancelled(true);
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', language.get("block.unbreakable")));
			}
		}
	}

	@EventHandler
	private void onBlockExplode(BlockExplodeEvent e) {
		Iterator<Block> blocks = e.blockList().iterator();
		while (blocks.hasNext()) {
			Block block = blocks.next();
			if (database.hasOwner(block.getLocation())) {
				blocks.remove();
			}
		}
	}

	@EventHandler
	private void onEntityExplode(EntityExplodeEvent e) {
		Iterator<Block> blocks = e.blockList().iterator();
		while (blocks.hasNext()) {
			Block block = blocks.next();
			if (database.hasOwner(block.getLocation())) {
				blocks.remove();
			}
		}
	}

	@EventHandler
	private void onPistonExtend(BlockPistonExtendEvent e) {
		for (Block block : e.getBlocks()) {
			if (database.hasOwner(block.getLocation())) {
				e.setCancelled(true);
				break;
			}
		}
	}

}
