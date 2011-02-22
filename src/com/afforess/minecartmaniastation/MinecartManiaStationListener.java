package com.afforess.minecartmaniastation;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.entity.Minecart;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.event.vehicle.VehicleListener;
import org.bukkit.util.Vector;

import com.afforess.minecartmaniacore.utils.ChatUtils;
import com.afforess.minecartmaniacore.utils.DirectionUtils;
import com.afforess.minecartmaniacore.utils.DirectionUtils.CompassDirection;
import com.afforess.minecartmaniacore.utils.MinecartUtils;
import com.afforess.minecartmaniacore.MinecartManiaMinecart;
import com.afforess.minecartmaniacore.MinecartManiaWorld;

public class MinecartManiaStationListener extends VehicleListener{
	
	public void onVehicleDamage(VehicleDamageEvent event) {
    	if (event.getVehicle() instanceof Minecart) {
    		MinecartManiaMinecart minecart = MinecartManiaWorld.getMinecartManiaMinecart((Minecart)event.getVehicle());
    		if (minecart.minecart.getPassenger() != null) {
    			if (minecart.isOnRails()) {
    				if(event.getAttacker() != null && event.getAttacker().getEntityId() == minecart.minecart.getPassenger().getEntityId()) {
    					if (StationUtil.isInQueue(minecart)) {
    						event.setCancelled(true);
    						event.setDamage(0);
    						return;
    					}
		    			CompassDirection facingDir = DirectionUtils.getDirectionFromMinecartRotation((minecart.minecart.getPassenger().getLocation().getYaw() - 90.0F) % 360.0F);
		    			ArrayList<CompassDirection> restricted = SignCommands.getRestrictedDirections(minecart);
		    			//Check if the direction is valid
		    			if (!MinecartUtils.validMinecartTrack(minecart.minecart.getWorld(), minecart.getX(), minecart.getY(), minecart.getZ(), 2, CompassDirection.NORTH)) {
		    				if (!restricted.contains(facingDir)) {
		    					restricted.add(facingDir);
		    				}
		    			}
		    			if (!MinecartUtils.validMinecartTrack(minecart.minecart.getWorld(), minecart.getX(), minecart.getY(), minecart.getZ(), 2, CompassDirection.SOUTH)) {
		    				if (!restricted.contains(facingDir)) {
		    					restricted.add(facingDir);
		    				}
		    			}
		    			if (!MinecartUtils.validMinecartTrack(minecart.minecart.getWorld(), minecart.getX(), minecart.getY(), minecart.getZ(), 2, CompassDirection.EAST)) {
		    				if (!restricted.contains(facingDir)) {
		    					restricted.add(facingDir);
		    				}
		    			}
		    			if (!MinecartUtils.validMinecartTrack(minecart.minecart.getWorld(), minecart.getX(), minecart.getY(), minecart.getZ(), 2, CompassDirection.WEST)) {
		    				if (!restricted.contains(facingDir)) {
		    					restricted.add(facingDir);
		    				}
		    			}
		    			if (restricted.contains(facingDir)){
		    				//Not a valid direction
		    				event.setCancelled(true);
    						event.setDamage(0);
    						if (minecart.hasPlayerPassenger()) {
    							ChatUtils.sendMultilineMessage(minecart.getPlayerPassenger(), "Not a valid direction.", ChatColor.RED.toString());
    							String valid = "You can go " + StationUtil.buildValidDirectionString(restricted);
    							ChatUtils.sendMultilineMessage(minecart.getPlayerPassenger(), valid, ChatColor.YELLOW.toString());
    							
    						}
    						return;
    					}
		    			
		    			Vector velocity = (Vector)minecart.getDataValue("preintersection velocity");
    					if (velocity == null) {
    						event.setCancelled(true);
    						event.setDamage(0);
    						return;
    					}
    					minecart.setDataValue("preintersection velocity", null);
    					velocity = StationUtil.alterMotionFromDirection(facingDir, velocity);
		    			
		    			//responding to chat direction prompt
    					if (minecart.isAtIntersection() && minecart.hasPlayerPassenger()) {
    						int data = DirectionUtils.getMinetrackRailDataForDirection(facingDir, minecart.getPreviousFacingDir());
    						if (data != -1) {
    							MinecartManiaWorld.setBlockData(minecart.minecart.getWorld(), minecart.getX(), minecart.getY(), minecart.getZ(), data);
    						}
    						minecart.minecart.setVelocity(velocity);
    						event.setCancelled(true);
    						event.setDamage(0);
    					}
    					
    				}
    			}
    		}
    	}
    }
	
    public void onVehicleEntityCollision(VehicleEntityCollisionEvent event) {
    	
    	/*if (event.getVehicle() instanceof Minecart && event.getEntity() instanceof Minecart) {
    		MinecartManiaMinecart minecart = MinecartManiaWorld.getMinecartManiaMinecart((Minecart)event.getVehicle());
    		MinecartManiaMinecart victim = MinecartManiaWorld.getMinecartManiaMinecart((Minecart)event.getEntity());
    		if (MinecartUtil.isAvoidCollisions()) {
    			if (minecart.getDirectionOfMotion().equals(DirectionUtils.getOppositeDirection(victim.getDirectionOfMotion()))) {
	    			Location temp = minecart.minecart.getLocation().clone();
	    			minecart.minecart.teleportTo(victim.minecart.getLocation().clone()); 
	    			victim.minecart.teleportTo(temp);
	    			event.setCancelled(true);
	    			event.setCollisionCancelled(true);
    			}
    		}
    	}*/
    }
}
