package com.afforess.bukkit.minecartmaniastation;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import com.afforess.bukkit.minecartmaniacore.MinecartManiaMinecart;
import com.afforess.bukkit.minecartmaniacore.DirectionUtils;
import com.afforess.bukkit.minecartmaniacore.MinecartManiaWorld;
import com.afforess.bukkit.minecartmaniacore.MinecartUtils;
import com.afforess.bukkit.minecartmaniacore.SignUtils;
import com.afforess.bukkit.minecartmaniacore.event.MinecartIntersectionEvent;

public class SignCommands {

	public static void processStation(MinecartIntersectionEvent event) {
		MinecartManiaMinecart minecart = event.getMinecart();
		
		ArrayList<Sign> signList = SignUtils.getAdjacentSignList(minecart, 2);
		for (Sign sign : signList) {
			for (int k = 0; k < 4; k++) {
				String str = sign.getLine(k);
				String newLine = "";
				String val[] = str.split(":");
				if (val.length != 2) {
					continue;
				}
				boolean valid = false;
				if (minecart.isStandardMinecart()) {
					if (!valid && str.toLowerCase().indexOf("mobs") > -1) {
						newLine = "[Mobs :";
						valid = minecart.minecart.getPassenger() != null && !minecart.hasPlayerPassenger();
					}
					if (!valid && str.toLowerCase().indexOf("player") > -1) {
						newLine = "[Player :";
						valid = minecart.hasPlayerPassenger();
					}
					if (!valid && str.toLowerCase().indexOf("empty") > -1) {
						newLine = "[Empty :";
						valid = minecart.minecart.getPassenger() == null;
					}
					if (!valid && minecart.hasPlayerPassenger()) {
						valid = str.toLowerCase().indexOf(minecart.getPlayerPassenger().getName().toLowerCase()) > -1;
						if (valid) {
							newLine = "[" + minecart.getPlayerPassenger().getName() + " :";
						}
					}
					if (!valid && minecart.hasPlayerPassenger() && minecart.getPlayerPassenger().getItemInHand() != null) {
						Material itemInHand = minecart.getPlayerPassenger().getItemInHand().getType();
						Material signData = ItemUtil.itemStringToMaterial(val[0].trim());
						valid = signData != null && signData.getId() == itemInHand.getId();
						if (valid) {
							if (val[0].trim().indexOf("[") > -1) {
								val[0] = val[0].trim().substring(val[0].trim().indexOf("[") + 1);
							}
							newLine = "[" + val[0].trim() + " :";
						}
					
					}
				}
				else if (minecart.isStorageMinecart()) {
					if (!valid && str.toLowerCase().indexOf("storage") > -1) {
						newLine = "[Storage :";
						valid = true;
					}
				}
				else if (minecart.isPoweredMinecart()) {
					if (!valid && str.toLowerCase().indexOf("powered") > -1) {
						newLine = "[Powered :";
						valid = true;
					}
				}
				
				//Note getDirectionOfMotion is unreliable on curves, use getPreviousFacingDir instead.
				if (!valid && (val[0].indexOf("W") > -1 || val[0].toLowerCase().indexOf("west") > -1)) {
					valid = minecart.getPreviousFacingDir() == DirectionUtils.CompassDirection.WEST;
					newLine = "[West :";
				}
				else if (!valid && (val[0].indexOf("E") > -1 || val[0].toLowerCase().indexOf("east") > -1)) {
					valid = minecart.getPreviousFacingDir() == DirectionUtils.CompassDirection.EAST;
					newLine = "[East :";
				}
				else if (!valid && (val[0].indexOf("N") > -1 || val[0].toLowerCase().indexOf("north") > -1)) {
					valid = minecart.getPreviousFacingDir() == DirectionUtils.CompassDirection.NORTH;
					newLine = "[North :";
				}
				else if (!valid && (val[0].indexOf("S") > -1 || val[0].toLowerCase().indexOf("south") > -1)) {
					valid = minecart.getPreviousFacingDir() == DirectionUtils.CompassDirection.SOUTH;
					newLine = "[South :";
				}
				
				if (valid) {
					DirectionUtils.CompassDirection direction = DirectionUtils.CompassDirection.NO_DIRECTION;
					
					//Process STR first because of overlapping characters
					if (val[1].indexOf("STR") > -1 || val[1].toLowerCase().indexOf("straight") > -1) {
						direction = minecart.getPreviousFacingDir();
						newLine += " STR]";
					}
					else if (val[1].indexOf("W") > -1 || val[1].toLowerCase().indexOf("west") > -1) {
						direction = DirectionUtils.CompassDirection.WEST;
						newLine += " West]";
					}
					else if (val[1].indexOf("E") > -1 || val[1].toLowerCase().indexOf("east") > -1) {
						direction = DirectionUtils.CompassDirection.EAST;
						newLine += " East]";
					}
					else if (val[1].indexOf("S") > -1 || val[1].toLowerCase().indexOf("south") > -1) {
						direction = DirectionUtils.CompassDirection.SOUTH;
						newLine += " South]";
					}
					else if (val[1].indexOf("N") > -1 || val[1].toLowerCase().indexOf("north") > -1) {
						direction = DirectionUtils.CompassDirection.NORTH;
						newLine += " North]";
					}
					else if (val[1].indexOf("L") > -1 || val[1].toLowerCase().indexOf("left") > -1) {
						direction = DirectionUtils.getLeftDirection(minecart.getPreviousFacingDir());
						newLine += " Left]";
					}
					else if (val[1].indexOf("R") > -1 || val[1].toLowerCase().indexOf("right") > -1) {
						direction = DirectionUtils.getRightDirection(minecart.getPreviousFacingDir());
						newLine += " Right]";
					}
					if (MinecartUtils.validMinecartTrack(minecart.getX(), minecart.getY(), minecart.getZ(), 2, direction)) {
						int data = DirectionUtils.getMinetrackRailDataForDirection(direction, minecart.getPreviousFacingDir());
						if (data != -1) {
							if (!newLine.equals(str)) {
								sign.setLine(k, newLine);
								sign.update(true);
							}
							
							Block oldBlock = MinecartManiaWorld.getBlockAt(minecart.getX(), minecart.getY(), minecart.getZ());
							ArrayList<Integer> blockData = new ArrayList<Integer>();
							blockData.add(new Integer(oldBlock.getX()));
							blockData.add(new Integer(oldBlock.getY()));
							blockData.add(new Integer(oldBlock.getZ()));
							blockData.add(new Integer(oldBlock.getData()));
							minecart.setDataValue("old rail data", blockData);
							
							MinecartManiaWorld.setBlockData(minecart.getX(), minecart.getY(), minecart.getZ(), data);
							event.setActionTaken(true);
							return;
						}
						else {
							if (DirectionUtils.getOppositeDirection(direction).equals(minecart.getPreviousFacingDir())) {
								if (!newLine.equals(str)) {
									sign.setLine(k, newLine);
									sign.update(true);
								}
								minecart.reverse();
								event.setActionTaken(true);
								return;
							}
						}
						
					}
				}
			}
		}
	}

}
