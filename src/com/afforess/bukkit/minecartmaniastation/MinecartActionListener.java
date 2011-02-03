package com.afforess.bukkit.minecartmaniastation;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.afforess.bukkit.minecartmaniacore.ChatUtils;
import com.afforess.bukkit.minecartmaniacore.DirectionUtils;
import com.afforess.bukkit.minecartmaniacore.MinecartManiaMinecart;
import com.afforess.bukkit.minecartmaniacore.MinecartManiaWorld;
import com.afforess.bukkit.minecartmaniacore.event.MinecartActionEvent;
import com.afforess.bukkit.minecartmaniacore.event.MinecartIntersectionEvent;
import com.afforess.bukkit.minecartmaniacore.event.MinecartManiaListener;
import com.afforess.bukkit.minecartmaniacore.event.MinecartManiaMinecartDestroyedEvent;
import com.afforess.bukkit.minecartmaniacore.event.MinecartMotionStartEvent;

public class MinecartActionListener extends MinecartManiaListener{

	public void onMinecartActionEvent(MinecartActionEvent event) {
		MinecartManiaMinecart minecart = event.getMinecart();

		if (!minecart.isAtIntersection()) {
			if (minecart.getDataValue("old rail data") != null) {
				@SuppressWarnings("unchecked")
				ArrayList<Integer> blockData = (ArrayList<Integer>)minecart.getDataValue("old rail data");
				MinecartManiaWorld.setBlockData(blockData.get(0), blockData.get(1), blockData.get(2), blockData.get(3));
				minecart.setDataValue("old rail data", null);
			}
		}
		
		//stop moving, there is a queue ahead of us
		MinecartManiaMinecart minecartAhead = minecart.getMinecartAhead();
		while (true) {
			if (minecartAhead == null) {
				break;
			}
			if (minecartAhead.getMinecartAhead() == null) {
				break;
			}
			if (minecartAhead.isMoving()) {
				break;
			}
			minecartAhead = minecartAhead.getMinecartAhead();
		}
		if (minecartAhead != null) {
			if (minecartAhead.isAtIntersection()) {
				if (!minecartAhead.isMoving()) {
					minecart.setDataValue("queued velocity", minecart.minecart.getVelocity().clone());
					minecart.stopCart();
					if (minecart.hasPlayerPassenger())
						ChatUtils.sendMultilineMessage(minecart.getPlayerPassenger(), "You've entered a queue. Please be patient.", ChatColor.YELLOW.toString());
				}
			}
		}
	}
	
	public void onMinecartIntersectionEvent(MinecartIntersectionEvent event) {
		MinecartManiaMinecart minecart = event.getMinecart();
	
		if (minecart.getBlockIdBeneath() == MinecartUtil.getStationBlockID()) {
			SignCommands.processStation(event);
		}
		
		if (!event.isActionTaken() && minecart.hasPlayerPassenger() && (MinecartUtil.isAutoIntersectionPrompt()
				|| (minecart.getBlockIdBeneath() == MinecartUtil.getStationBlockID() && MinecartUtil.isStationIntersectionPrompt()))) {
			
			minecart.setDataValue("preintersection velocity", minecart.minecart.getVelocity().clone());
			minecart.stopCart();
			Player passenger = minecart.getPlayerPassenger();
			//set the track straight
			int data = DirectionUtils.getMinetrackRailDataForDirection(minecart.getPreviousFacingDir(), minecart.getPreviousFacingDir());
			Block oldBlock = MinecartManiaWorld.getBlockAt(minecart.getX(), minecart.getY(), minecart.getZ());
			ArrayList<Integer> blockData = new ArrayList<Integer>();
			blockData.add(new Integer(oldBlock.getX()));
			blockData.add(new Integer(oldBlock.getY()));
			blockData.add(new Integer(oldBlock.getZ()));
			blockData.add(new Integer(oldBlock.getData()));
			minecart.setDataValue("old rail data", blockData);
			if (data != -1) {
				MinecartManiaWorld.setBlockData(minecart.getX(), minecart.getY(), minecart.getZ(), data);
			}
			ChatUtils.sendMultilineMessage(passenger, "Tap your minecart in the desired direction", ChatColor.YELLOW.toString());
		}
		
	}
	
	public void onMinecartMotionStartEvent(MinecartMotionStartEvent event) {
		MinecartManiaMinecart minecart = event.getMinecart();
		if (minecart.isAtIntersection()) {
			MinecartUtil.updateQueue(minecart);
		}
	}
	
	public void onMinecartManiaMinecartDestroyedEvent(MinecartManiaMinecartDestroyedEvent event) {
		MinecartManiaMinecart minecart = event.getMinecart();
		MinecartUtil.updateQueue(minecart);
	}
}
