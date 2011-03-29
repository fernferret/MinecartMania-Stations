package com.afforess.minecartmaniastation;

import java.util.ArrayList;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import com.afforess.minecartmaniacore.MinecartManiaMinecart;
import com.afforess.minecartmaniacore.MinecartManiaWorld;
import com.afforess.minecartmaniacore.event.MinecartEvent;
import com.afforess.minecartmaniacore.event.MinecartLaunchedEvent;
import com.afforess.minecartmaniacore.utils.DirectionUtils;
import com.afforess.minecartmaniacore.utils.DirectionUtils.CompassDirection;
import com.afforess.minecartmaniacore.utils.MinecartUtils;
import com.afforess.minecartmaniacore.utils.SignUtils;
import com.afforess.minecartmaniacore.utils.StringUtils;
import com.afforess.minecartmaniacore.utils.WordUtils;

public class SignCommands {

	public static void processStation(MinecartEvent event) {
		MinecartManiaMinecart minecart = event.getMinecart();
		
		ArrayList<Sign> signList = SignUtils.getAdjacentSignList(minecart, 2);
		for (Sign sign : signList) {
			convertCraftBookSorter(sign);
			for (int k = 0; k < 4; k++) {
				//Setup initial data
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
				//end of data setup
				
				for (StationCondition e : StationCondition.values()) {
					if (e.result(minecart, val[0])) {
						valid = true;
						break;
					}
				}
				
				if (valid) {
					CompassDirection direction = CompassDirection.NO_DIRECTION;
					
					for (StationDirection e : StationDirection.values()) {
						direction = e.direction(minecart, val[1]);
						if (direction != CompassDirection.NO_DIRECTION){
							break;
						}
					}
					
					//Special case - if we are at a launcher, set the launch speed as well
					if (event instanceof MinecartLaunchedEvent && direction != null && direction != CompassDirection.NO_DIRECTION) {
						minecart.setMotion(direction, 0.6D);
						((MinecartLaunchedEvent)event).setLaunchSpeed(minecart.minecart.getVelocity());
					}
					
					//setup sign formatting
					newLine = StringUtils.removeBrackets(newLine);
					char[] ch = {' ', ':'};
					newLine = WordUtils.capitalize(newLine, ch);
					newLine = StringUtils.addBrackets(newLine);
					
					boolean handled = false;
					//Handle minecart destruction
					if (direction == null) {
						minecart.kill();
						handled = true;
					}
					else if (MinecartUtils.validMinecartTrack(minecart.minecart.getWorld(), minecart.getX(), minecart.getY(), minecart.getZ(), 2, direction)) {
						int data = DirectionUtils.getMinetrackRailDataForDirection(direction, minecart.getPreviousFacingDir());
						if (data != -1) {
							handled = true;
							
							//Force the game to remember the old data of the rail we are on, and reset it once we are done
							Block oldBlock = MinecartManiaWorld.getBlockAt(minecart.minecart.getWorld(), minecart.getX(), minecart.getY(), minecart.getZ());
							ArrayList<Integer> blockData = new ArrayList<Integer>();
							blockData.add(new Integer(oldBlock.getX()));
							blockData.add(new Integer(oldBlock.getY()));
							blockData.add(new Integer(oldBlock.getZ()));
							blockData.add(new Integer(oldBlock.getData()));
							minecart.setDataValue("old rail data", blockData);
							
							//change the track dirtion
							MinecartManiaWorld.setBlockData(minecart.minecart.getWorld(), minecart.getX(), minecart.getY(), minecart.getZ(), data);
						}
						else if (DirectionUtils.getOppositeDirection(direction).equals(minecart.getPreviousFacingDir())) {
							//format the sign
							minecart.reverse();
							handled = true;
						}
					}
					
					if (handled){
						event.setActionTaken(true);
						//format the sign
						sign.setLine(k, newLine);
						sign.update(true);
						return;
					}
				}
			}
		}
	}

	protected static boolean processStationCommand(MinecartManiaMinecart minecart, String str) {
		boolean valid = false;
		if (!str.toLowerCase().contains("st-")) {
			return false;
		}
		String[] val = str.toLowerCase().split(":");
		String[] keys = val[0].split("-| ?: ?");
		String st = keys[1];
		String stp = st; //st pattern
		String station = MinecartManiaWorld.getMinecartManiaPlayer(minecart.getPlayerPassenger()).getLastStation().toLowerCase();
		int parseSetting = MinecartManiaWorld.getIntValue(MinecartManiaWorld.getConfigurationValue("StationSignParsingMethod"));
		switch(parseSetting){
			case 0: //default with no pattern matching
				valid = station.equalsIgnoreCase(st);break;
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
		if (valid && MinecartManiaWorld.getMinecartManiaPlayer(minecart.getPlayerPassenger()).getDataValue("Reset Station Data") == null) {
			if (!StationUtil.isStationCommandNeverResets()) {
				MinecartManiaWorld.getMinecartManiaPlayer(minecart.getPlayerPassenger()).setLastStation("");
			}
		}
		return valid;
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
