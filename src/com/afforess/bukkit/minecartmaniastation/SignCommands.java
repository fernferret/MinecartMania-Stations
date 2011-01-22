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
						valid = str.toLowerCase().indexOf(minecart.getPlayerPassenger().getName()) > -1;
						if (valid) {
							newLine = "[" + minecart.getPlayerPassenger().getName() + " :";
						}
					}
					if (!valid && minecart.hasPlayerPassenger() && minecart.getPlayerPassenger().getItemInHand() != null) {
						Material itemInHand = minecart.getPlayerPassenger().getItemInHand().getType();
						System.out.println("Item in Hand " + itemInHand);
						Material signData = ItemUtil.itemStringToMaterial(val[0].trim());
						System.out.println("Sign Text: " + val[0].trim() + " Sign Data: " + signData);
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
					System.out.println("Incoming West");
					newLine = "[West :";
				}
				else if (!valid && (val[0].indexOf("E") > -1 || val[0].toLowerCase().indexOf("east") > -1)) {
					valid = minecart.getPreviousFacingDir() == DirectionUtils.CompassDirection.EAST;
					System.out.println("Incoming East");
					newLine = "[East :";
				}
				else if (!valid && (val[0].indexOf("N") > -1 || val[0].toLowerCase().indexOf("north") > -1)) {
					valid = minecart.getPreviousFacingDir() == DirectionUtils.CompassDirection.NORTH;
					System.out.println("Incoming North");
					newLine = "[North :";
				}
				else if (!valid && (val[0].indexOf("S") > -1 || val[0].toLowerCase().indexOf("south") > -1)) {
					valid = minecart.getPreviousFacingDir() == DirectionUtils.CompassDirection.SOUTH;
					System.out.println("Incoming South");
					newLine = "[South :";
				}
				
				if (valid) {
					DirectionUtils.CompassDirection direction = DirectionUtils.CompassDirection.NO_DIRECTION;
					if (val[1].indexOf("W") > -1 || val[1].toLowerCase().indexOf("west") > -1) {
						direction = DirectionUtils.CompassDirection.WEST;
						System.out.println("Outgoing West");
						newLine += " West]";
					}
					else if (val[1].indexOf("E") > -1 || val[1].toLowerCase().indexOf("east") > -1) {
						direction = DirectionUtils.CompassDirection.EAST;
						System.out.println("Outgoing East");
						newLine += " East]";
					}
					else if (val[1].indexOf("S") > -1 || val[1].toLowerCase().indexOf("south") > -1) {
						direction = DirectionUtils.CompassDirection.SOUTH;
						System.out.println("Outgoing South");
						newLine += " South]";
					}
					else if (val[1].indexOf("N") > -1 || val[1].toLowerCase().indexOf("north") > -1) {
						direction = DirectionUtils.CompassDirection.NORTH;
						System.out.println("Outgoing North");
						newLine += " North]";
					}
					if (MinecartUtils.validMinecartTrack(minecart.getX(), minecart.getY(), minecart.getZ(), 2, direction)) {
						int data = DirectionUtils.getMinetrackRailDataForDirection(direction, minecart.getPreviousFacingDir());
						System.out.println("Data is " + data);
						if (data != -1) {
							
							System.out.println("Done! " + newLine + " " + str);
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
							System.out.println(direction + " " + minecart.getPreviousFacingDir());
							if (DirectionUtils.getOppositeDirection(direction).equals(minecart.getPreviousFacingDir())) {
								System.out.println("Reversing");
								if (!newLine.equals(str)) {
									sign.setLine(k, newLine);
									sign.update(true);
								}
								System.out.println(minecart.getMotionX() + " " + minecart.getMotionZ());
								minecart.reverse();
								System.out.println(minecart.getMotionX() + " " + minecart.getMotionZ());
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
