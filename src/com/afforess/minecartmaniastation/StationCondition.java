package com.afforess.minecartmaniastation;

import com.afforess.minecartmaniacore.Item;
import com.afforess.minecartmaniacore.MinecartManiaMinecart;
import com.afforess.minecartmaniacore.MinecartManiaStorageCart;
import com.afforess.minecartmaniacore.MinecartManiaWorld;
import com.afforess.minecartmaniacore.utils.DirectionUtils.CompassDirection;
import com.afforess.minecartmaniacore.utils.ItemUtils;

public enum StationCondition implements Condition{
	Default {
		@Override
		public boolean result(MinecartManiaMinecart minecart, String str) {
			return str.toLowerCase().contains("default");
		}
	},
	Empty {
		@Override
		public boolean result(MinecartManiaMinecart minecart, String str) {
			return minecart.isStandardMinecart() && minecart.minecart.getPassenger() == null && str.toLowerCase().contains("empty");
		}
		
	},
	Player {
		@Override
		public boolean result(MinecartManiaMinecart minecart, String str) {
			return minecart.hasPlayerPassenger() && str.toLowerCase().contains("player");
		}
		
	},
	Mob {
		@Override
		public boolean result(MinecartManiaMinecart minecart, String str) {
			return minecart.minecart.getPassenger() != null && !minecart.hasPlayerPassenger() && str.toLowerCase().contains("mob");
		}
	},
	Pig {
		@Override
		public boolean result(MinecartManiaMinecart minecart, String str) {
			return minecart.minecart.getPassenger() instanceof org.bukkit.entity.Pig && str.toLowerCase().contains("pig");
		}
	},
	Chicken {
		@Override
		public boolean result(MinecartManiaMinecart minecart, String str) {
			return minecart.minecart.getPassenger() instanceof org.bukkit.entity.Chicken && str.toLowerCase().contains("chicken");
		}
	},
	Cow {
		@Override
		public boolean result(MinecartManiaMinecart minecart, String str) {
			return minecart.minecart.getPassenger() instanceof org.bukkit.entity.Cow && str.toLowerCase().contains("cow");
		}
	},
	Sheep {
		@Override
		public boolean result(MinecartManiaMinecart minecart, String str) {
			return minecart.minecart.getPassenger() instanceof org.bukkit.entity.Sheep && str.toLowerCase().contains("sheep");
		}
	},
	Creeper {
		@Override
		public boolean result(MinecartManiaMinecart minecart, String str) {
			return minecart.minecart.getPassenger() instanceof org.bukkit.entity.Creeper && str.toLowerCase().contains("creeper");
		}
	},
	Skeleton {
		@Override
		public boolean result(MinecartManiaMinecart minecart, String str) {
			return minecart.minecart.getPassenger() instanceof org.bukkit.entity.Skeleton && str.toLowerCase().contains("skeleton");
		}
	},
	Zombie {
		@Override
		public boolean result(MinecartManiaMinecart minecart, String str) {
			return minecart.minecart.getPassenger() instanceof org.bukkit.entity.Zombie && str.toLowerCase().contains("zombie");
		}
	},
	StationCommand {
		@Override
		public boolean result(MinecartManiaMinecart minecart, String str) {
			return minecart.hasPlayerPassenger() && SignCommands.processStationCommand(minecart, str);
		}
	},
	PlayerName {
		@Override
		public boolean result(MinecartManiaMinecart minecart, String str) {
			return minecart.hasPlayerPassenger() && str.equalsIgnoreCase(minecart.getPlayerPassenger().getName());
		}
	},
	ContainsItem {
		@Override
		public boolean result(MinecartManiaMinecart minecart, String str) {
			if (minecart.hasPlayerPassenger() && minecart.getPlayerPassenger().getItemInHand() != null) {
				Item itemInHand = Item.getItem(minecart.getPlayerPassenger().getItemInHand().getTypeId(), minecart.getPlayerPassenger().getItemInHand().getDurability());
				Item[] signData = ItemUtils.getItemStringToMaterial(str);
				for (Item item : signData) {
					if (item != null && item.equals(itemInHand)) {
						return true;
					}
				}		
			}
			else if (minecart.isStorageMinecart()) {
				Item[] signData = ItemUtils.getItemStringToMaterial(str);
				for (Item item : signData) {
					if (item != null && (((MinecartManiaStorageCart)minecart).amount(item) > (item.isInfinite() ? 0 : item.getAmount()))) {
						return true;
					}
				}	
			}
			return false;
		}
	},
	Cargo {
		@Override
		public boolean result(MinecartManiaMinecart minecart, String str) {
			return minecart.isStorageMinecart() && str.toLowerCase().contains("cargo") && ((MinecartManiaStorageCart)minecart).isEmpty();
		}
	},
	Storage {
		@Override
		public boolean result(MinecartManiaMinecart minecart, String str) {
			return minecart.isStorageMinecart() && str.toLowerCase().contains("storage");
		}
	},
	Powered {
		@Override
		public boolean result(MinecartManiaMinecart minecart, String str) {
			return minecart.isPoweredMinecart() && str.toLowerCase().contains("powered");
		}
	},
	West {
		@Override
		public boolean result(MinecartManiaMinecart minecart, String str) {
			return (str.equals("W") || str.toLowerCase().contains("west")) && minecart.getPreviousFacingDir() == CompassDirection.WEST;
		}
	},
	East {
		@Override
		public boolean result(MinecartManiaMinecart minecart, String str) {
			return (str.equals("E") || str.toLowerCase().contains("east")) && minecart.getPreviousFacingDir() == CompassDirection.EAST;
		}
	},
	North {
		@Override
		public boolean result(MinecartManiaMinecart minecart, String str) {
			return (str.equals("N") || str.toLowerCase().contains("north")) && minecart.getPreviousFacingDir() == CompassDirection.NORTH;
		}
	},
	South {
		@Override
		public boolean result(MinecartManiaMinecart minecart, String str) {
			return (str.equals("S") || str.toLowerCase().contains("south")) && minecart.getPreviousFacingDir() == CompassDirection.SOUTH;
		}
	},
	Redstone {
		@Override
		public boolean result(MinecartManiaMinecart minecart, String str) {
			return str.toLowerCase().contains("redstone") && (minecart.isPoweredBeneath() ||
					MinecartManiaWorld.isBlockIndirectlyPowered(minecart.minecart.getWorld(), minecart.getX(), minecart.getY() - 2, minecart.getZ()));
		}
	}
	
}
