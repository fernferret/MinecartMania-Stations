package com.afforess.minecartmaniastation;

import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import com.afforess.minecartmaniacore.MinecartManiaMinecart;
import com.afforess.minecartmaniacore.MinecartManiaStorageCart;
import com.afforess.minecartmaniacore.utils.DirectionUtils;
import com.afforess.minecartmaniacore.MinecartManiaWorld;
import com.afforess.minecartmaniacore.utils.DirectionUtils.CompassDirection;
import com.afforess.minecartmaniacore.utils.MinecartUtils;
import com.afforess.minecartmaniacore.utils.SignUtils;
import com.afforess.minecartmaniacore.utils.ItemUtils;
import com.afforess.minecartmaniacore.utils.StringUtils;
import com.afforess.minecartmaniacore.utils.WordUtils;
import com.afforess.minecartmaniacore.event.MinecartIntersectionEvent;

public class SignCommands {

	public static void processStation(MinecartIntersectionEvent event) {
		MinecartManiaMinecart minecart = event.getMinecart();
		
		ArrayList<Sign> signList = SignUtils.getAdjacentSignList(minecart, 2);
		for (Sign sign : signList) {
			convertCraftBookSorter(sign);
			for (int k = 0; k < 4; k++) {
				String str = sign.getLine(k);
				String newLine = str;
				String val[] = str.split(":");
				if (val.length != 2) {
					continue;
				}
				//Strip header and ending characters
				val[0] = StringUtils.removeBrackets(val[0]);
				val[1] = StringUtils.removeBrackets(val[1]);
				//Strip whitespace
				val[0] = val[0].trim();
				val[1] = val[1].trim();
				boolean valid = false;
				if (minecart.isStandardMinecart()) {
					if (!valid && str.toLowerCase().indexOf("mobs") > -1) {
						valid = minecart.minecart.getPassenger() != null && !minecart.hasPlayerPassenger();
					}
					if (!valid && str.toLowerCase().indexOf("player") > -1) {
						valid = minecart.hasPlayerPassenger();
					}
					if (!valid && str.toLowerCase().indexOf("empty") > -1) {
						valid = minecart.minecart.getPassenger() == null;
					}
					if (!valid && minecart.hasPlayerPassenger() && str.toLowerCase().contains("st-")) {
						String[] keys = val[0].split("-| ?: ?");
						String st = keys[1];
						String stp = st; //st pattern
						String station = MinecartManiaWorld.getMinecartManiaPlayer(minecart.getPlayerPassenger()).getLastStation();
						int parseSetting = MinecartManiaWorld.getIntValue(MinecartManiaWorld.getConfigurationValue("Station Sign Parsing Method"));
						switch(parseSetting){
							case 0: //default with no pattern matching
								valid = station.equals(st);break;
							case 1: //simple pattern matching
								stp = stp.replace("\\", "\\\\") //escapes backslashes in case people use them in station names
								.replace(".", "\\.") //escapes period
								.replace("*", ".*") //converts *
								.replace("?", ".") //converts ?
								.replace("#", "\\d") //converts #
								.replace("@", "[a-zA-Z]"); //converts @
							case 2: //full regex //note the lack of break before this, case 1 comes down here after converting
								valid = station.matches(stp); break;
						}
						if (valid & MinecartManiaWorld.getMinecartManiaPlayer(minecart.getPlayerPassenger()).getDataValue("Reset Station Data") == null) {
							MinecartManiaWorld.getMinecartManiaPlayer(minecart.getPlayerPassenger()).setLastStation("");
						}
					}
					if (!valid && minecart.hasPlayerPassenger()) {
						valid = str.toLowerCase().indexOf(minecart.getPlayerPassenger().getName().toLowerCase()) > -1;

					}
					if (!valid && minecart.hasPlayerPassenger() && minecart.getPlayerPassenger().getItemInHand() != null) {
						Material itemInHand = minecart.getPlayerPassenger().getItemInHand().getType();
						Material[] signData = ItemUtils.getItemStringToMaterial(val[0].trim());
						for (Material item : signData) {
							valid = item != null && item.getId() == itemInHand.getId();
							if (valid) break;
						}					
					}
				}
				else if (minecart.isStorageMinecart()) {
					if (!valid && str.toLowerCase().contains("storage")) {
						valid = true;
					}
					if (!valid && str.toLowerCase().contains("cargo")) {
						System.out.println(((MinecartManiaStorageCart)minecart).firstEmpty());
						valid = ((MinecartManiaStorageCart)minecart).isEmpty();
					}
					if (!valid) {
						Material[] signData = ItemUtils.getItemStringToMaterial(val[0].trim());
						for (Material item : signData) {
							valid = item != null && (((MinecartManiaStorageCart)minecart).contains(item));
							if (valid) break;
						}					
					}
				}
				else if (minecart.isPoweredMinecart()) {
					if (!valid && str.toLowerCase().indexOf("powered") > -1) {
						valid = true;
					}
				}
				
				//Note getDirectionOfMotion is unreliable on curves, use getPreviousFacingDir instead.
				if (!valid && (val[0].equals("W") || val[0].toLowerCase().indexOf("west") > -1)) {
					valid = minecart.getPreviousFacingDir() == DirectionUtils.CompassDirection.WEST;
				}
				else if (!valid && (val[0].equals("E") || val[0].toLowerCase().indexOf("east") > -1)) {
					valid = minecart.getPreviousFacingDir() == DirectionUtils.CompassDirection.EAST;
				}
				else if (!valid && (val[0].equals("N") || val[0].toLowerCase().indexOf("north") > -1)) {
					valid = minecart.getPreviousFacingDir() == DirectionUtils.CompassDirection.NORTH;
				}
				else if (!valid && (val[0].equals("S") || val[0].toLowerCase().indexOf("south") > -1)) {
					valid = minecart.getPreviousFacingDir() == DirectionUtils.CompassDirection.SOUTH;
				}
				
				if (valid) {
					DirectionUtils.CompassDirection direction = DirectionUtils.CompassDirection.NO_DIRECTION;
					
					//Process STR first because of overlapping characters
					if (val[1].equals("STR") || val[1].toLowerCase().indexOf("straight") > -1) {
						direction = minecart.getPreviousFacingDir();
					}
					else if (val[1].equals("W") || val[1].toLowerCase().indexOf("west") > -1) {
						direction = DirectionUtils.CompassDirection.WEST;
					}
					else if (val[1].equals("E") || val[1].toLowerCase().indexOf("east") > -1) {
						direction = DirectionUtils.CompassDirection.EAST;
					}
					else if (val[1].equals("S") || val[1].toLowerCase().indexOf("south") > -1) {
						direction = DirectionUtils.CompassDirection.SOUTH;
					}
					else if (val[1].equals("N") || val[1].toLowerCase().indexOf("north") > -1) {
						direction = DirectionUtils.CompassDirection.NORTH;
					}
					else if (val[1].equals("L") || val[1].toLowerCase().indexOf("left") > -1) {
						direction = DirectionUtils.getLeftDirection(minecart.getPreviousFacingDir());
					}
					else if (val[1].equals("R") || val[1].toLowerCase().indexOf("right") > -1) {
						direction = DirectionUtils.getRightDirection(minecart.getPreviousFacingDir());
					}
					if (MinecartUtils.validMinecartTrack(minecart.minecart.getWorld(), minecart.getX(), minecart.getY(), minecart.getZ(), 2, direction)) {
						int data = DirectionUtils.getMinetrackRailDataForDirection(direction, minecart.getPreviousFacingDir());
						if (data != -1) {
							newLine = StringUtils.removeBrackets(newLine);
							char[] ch = {' ', ':'};
							newLine = WordUtils.capitalize(newLine, ch);
							newLine = StringUtils.addBrackets(newLine);
							sign.setLine(k, newLine);
							sign.update(true);
							
							Block oldBlock = MinecartManiaWorld.getBlockAt(minecart.minecart.getWorld(), minecart.getX(), minecart.getY(), minecart.getZ());
							ArrayList<Integer> blockData = new ArrayList<Integer>();
							blockData.add(new Integer(oldBlock.getX()));
							blockData.add(new Integer(oldBlock.getY()));
							blockData.add(new Integer(oldBlock.getZ()));
							blockData.add(new Integer(oldBlock.getData()));
							minecart.setDataValue("old rail data", blockData);
							
							MinecartManiaWorld.setBlockData(minecart.minecart.getWorld(), minecart.getX(), minecart.getY(), minecart.getZ(), data);
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

	private static void convertCraftBookSorter(Sign sign) {
		if (sign.getLine(1).contains("[Sort]")) {
			if (!sign.getLine(2).trim().isEmpty()) {
				sign.setLine(2, "st-" + sign.getLine(2).trim().substring(1) + ": L");
			}
			if (!sign.getLine(3).trim().isEmpty()) {
				sign.setLine(3, "st-" + sign.getLine(3).trim().substring(1) + ": R");
			}
			sign.setLine(1, "");
			sign.update();
		}
	}

	public static ArrayList<CompassDirection> getRestrictedDirections(MinecartManiaMinecart minecart) {
		ArrayList<CompassDirection> restricted = new ArrayList<CompassDirection>(4);
		ArrayList<Sign> signList = SignUtils.getAdjacentSignList(minecart, 2);
		for (Sign sign : signList) {
			for (int i = 0; i < 4; i++) {
				if (sign.getLine(i).toLowerCase().contains("restrict")) {
					String[] directions = sign.getLine(i).split(":");
					if (directions.length > 1) {
						for (int j = 1; j < directions.length; j++) {
							if (directions[j].contains("N")) {
								restricted.add(CompassDirection.NORTH);
							}
							if (directions[j].contains("S")) {
								restricted.add(CompassDirection.SOUTH);
							}
							if (directions[j].contains("E")) {
								restricted.add(CompassDirection.EAST);
							}
							if (directions[j].contains("W")) {
								restricted.add(CompassDirection.WEST);
							}
						}
						return restricted;
					}
				}
			}
		}
		return restricted;
	}
}
