package com.afforess.minecartmaniastation;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.afforess.minecartmaniacore.utils.ChatUtils;
import com.afforess.minecartmaniacore.utils.DirectionUtils;
import com.afforess.minecartmaniacore.utils.DirectionUtils.CompassDirection;
import com.afforess.minecartmaniacore.MinecartManiaMinecart;
import com.afforess.minecartmaniacore.MinecartManiaWorld;
import com.afforess.minecartmaniacore.event.MinecartActionEvent;
import com.afforess.minecartmaniacore.event.MinecartClickedEvent;
import com.afforess.minecartmaniacore.event.MinecartIntersectionEvent;
import com.afforess.minecartmaniacore.event.MinecartLaunchedEvent;
import com.afforess.minecartmaniacore.event.MinecartManiaListener;
import com.afforess.minecartmaniacore.event.MinecartManiaMinecartDestroyedEvent;
import com.afforess.minecartmaniacore.event.MinecartMotionStartEvent;

public class MinecartActionListener extends MinecartManiaListener{

	public void onMinecartActionEvent(MinecartActionEvent event) {
		MinecartManiaMinecart minecart = event.getMinecart();

		if (!minecart.isAtIntersection()) {
			if (minecart.getDataValue("old rail data") != null) {
				@SuppressWarnings("unchecked")
				ArrayList<Integer> blockData = (ArrayList<Integer>)minecart.getDataValue("old rail data");
				MinecartManiaWorld.setBlockData(minecart.minecart.getWorld(), blockData.get(0), blockData.get(1), blockData.get(2), blockData.get(3));
				minecart.setDataValue("old rail data", null);
			}
		}
		
		//stop moving, there is a queue ahead of us
		//TODO Fix this
		/*MinecartManiaMinecart minecartAhead = minecart.getMinecartAhead();
		while (true) {
			if (minecartAhead == null) {
				break;
			}
			if (minecartAhead.minecart.getEntityId() == minecart.minecart.getEntityId()) {
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
		}*/
	}
	
	public void onMinecartIntersectionEvent(MinecartIntersectionEvent event) {
		MinecartManiaMinecart minecart = event.getMinecart();
		
		if (event.isActionTaken()) {
			return;
		}
	
		if (StationUtil.isAtStationBlock(minecart)) {
			SignCommands.processStation(event);
		}
		
		if (event.isActionTaken()) {
			return;
		}
		
		if (StationUtil.shouldPromptUser(minecart)) {
			
			minecart.setDataValue("preintersection velocity", minecart.minecart.getVelocity().clone());
			minecart.stopCart();
			Player passenger = minecart.getPlayerPassenger();
			//set the track straight
			int data = DirectionUtils.getMinetrackRailDataForDirection(minecart.getPreviousFacingDir(), minecart.getPreviousFacingDir());
			Block oldBlock = MinecartManiaWorld.getBlockAt(minecart.minecart.getWorld(), minecart.getX(), minecart.getY(), minecart.getZ());
			ArrayList<Integer> blockData = new ArrayList<Integer>();
			blockData.add(new Integer(oldBlock.getX()));
			blockData.add(new Integer(oldBlock.getY()));
			blockData.add(new Integer(oldBlock.getZ()));
			blockData.add(new Integer(oldBlock.getData()));
			minecart.setDataValue("old rail data", blockData);
			if (data != -1) {
				MinecartManiaWorld.setBlockData(minecart.minecart.getWorld(), minecart.getX(), minecart.getY(), minecart.getZ(), data);
			}
			ChatUtils.sendMultilineMessage(passenger, "Tap your minecart in the desired direction", ChatColor.YELLOW.toString());
		}
		
	}
	
	public void onMinecartLaunchedEvent(MinecartLaunchedEvent event) {
		if (event.isActionTaken()) {
			return;
		}
		SignCommands.processStation(event);
	}
	
	public void onMinecartMotionStartEvent(MinecartMotionStartEvent event) {
		MinecartManiaMinecart minecart = event.getMinecart();
		if (minecart.isAtIntersection()) {
			StationUtil.updateQueue(minecart);
		}
	}
	
	public void onMinecartManiaMinecartDestroyedEvent(MinecartManiaMinecartDestroyedEvent event) {
		MinecartManiaMinecart minecart = event.getMinecart();
		StationUtil.updateQueue(minecart);
	}
	
	public void onMinecartClickedEvent(MinecartClickedEvent event) {
		if (event.isActionTaken()) {
			return;
		}
		MinecartManiaMinecart minecart = event.getMinecart();
		if (StationUtil.isInQueue(minecart)) {
			event.setActionTaken(true);
			return;
		}
		CompassDirection facingDir = DirectionUtils.getDirectionFromMinecartRotation((minecart.minecart.getPassenger().getLocation().getYaw() - 90.0F) % 360.0F);
		
		Vector velocity = (Vector)minecart.getDataValue("preintersection velocity");
		if (velocity == null) {
			return;
		}
		
		velocity = StationUtil.alterMotionFromDirection(facingDir, velocity);
		
		//responding to chat direction prompt
		if (minecart.isAtIntersection() && minecart.hasPlayerPassenger()) {
			if (StationUtil.isValidDirection(facingDir, minecart)) {
				int data = DirectionUtils.getMinetrackRailDataForDirection(facingDir, minecart.getPreviousFacingDir());
				if (data != -1) {
					MinecartManiaWorld.setBlockData(minecart.minecart.getWorld(), minecart.getX(), minecart.getY(), minecart.getZ(), data);
				}
				minecart.minecart.setVelocity(velocity);
				minecart.setDataValue("preintersection velocity", null);
			}
			event.setActionTaken(true);
		}
	}
}
