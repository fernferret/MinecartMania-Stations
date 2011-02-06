package com.afforess.bukkit.minecartmaniastation;

import org.bukkit.entity.Minecart;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.event.vehicle.VehicleListener;
import org.bukkit.util.Vector;

import com.afforess.bukkit.minecartmaniacore.DirectionUtils;
import com.afforess.bukkit.minecartmaniacore.MinecartManiaMinecart;
import com.afforess.bukkit.minecartmaniacore.MinecartManiaWorld;

public class MinecartManiaStationListener extends VehicleListener{
	
	public void onVehicleDamage(VehicleDamageEvent event) {
		
    	if (event.getVehicle() instanceof Minecart) {
    		MinecartManiaMinecart minecart = MinecartManiaWorld.getMinecartManiaMinecart((Minecart)event.getVehicle());
    		if (minecart.minecart.getPassenger() != null) {
    			if (minecart.isOnRails()) {
    				if(event.getAttacker() != null && event.getAttacker().getEntityId() == minecart.minecart.getPassenger().getEntityId()) {
    					
    					if (StationUtil.isInQueue(minecart)) {
    						event.setCancelled(true);
    						return;
    					}
    					
		    			DirectionUtils.CompassDirection facingDir = DirectionUtils.getDirectionFromMinecartRotation((minecart.minecart.getPassenger().getLocation().getYaw() - 90.0F) % 360.0F);
    					Vector velocity = (Vector)minecart.getDataValue("preintersection velocity");
    					if (velocity == null) {
    						return;
    					}
    					minecart.setDataValue("preintersection velocity", null);
    					velocity = StationUtil.alterMotionFromDirection(facingDir, velocity);
		    			
		    			//responding to chat direction prompt
    					if (minecart.isAtIntersection() && minecart.hasPlayerPassenger()) {
    						int data = DirectionUtils.getMinetrackRailDataForDirection(facingDir, minecart.getPreviousFacingDir());
    						if (data != -1) {
    							MinecartManiaWorld.setBlockData(minecart.getX(), minecart.getY(), minecart.getZ(), data);
    						}
    						minecart.minecart.setVelocity(velocity);
    						event.setCancelled(true);
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
