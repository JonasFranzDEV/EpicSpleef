package de.oppermann.bastian.spleef.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;


import javassist.NotFoundException;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import de.oppermann.bastian.spleef.SpleefMain;

public class PetMaker {
	
	private static boolean nmsFailed = false;
	
	private static Class<?> CLASS_ENTITY_INSENTIENT;		// should be final but java sucks
	private static Class<?> CLASS_PATHFINDER_GOAL;			// should be final but java sucks
	private static Class<?> CLASS_PATHFINDER_GOAL_FLOAT;	// should be final but java sucks
	private static Class<?> CLASS_PATHFINDER_GOAL_SELECTOR;	// should be final but java sucks
	private static Class<?> CLASS_CRAFT_LIVING_ENTITY;		// should be final but java sucks
	private static Class<?> CLASS_UNSAFE_LIST;				// should be final but java sucks
	
	private static String VERSION;	// the craftbukkit version (should also be final but ...)

	static {
		String path = Bukkit.getServer().getClass().getPackage().getName();
		VERSION = path.substring(path.lastIndexOf(".") + 1, path.length());
		
		try {
			CLASS_ENTITY_INSENTIENT = Class.forName("net.minecraft.server." + VERSION + ".EntityInsentient");
			CLASS_PATHFINDER_GOAL = Class.forName("net.minecraft.server." + VERSION + ".PathfinderGoal");
			CLASS_PATHFINDER_GOAL_FLOAT = Class.forName("net.minecraft.server." + VERSION + ".PathfinderGoalFloat");
			CLASS_PATHFINDER_GOAL_SELECTOR = Class.forName("net.minecraft.server." + VERSION + ".PathfinderGoalSelector");
			CLASS_CRAFT_LIVING_ENTITY = Class.forName("org.bukkit.craftbukkit." + VERSION + ".entity.CraftLivingEntity");
			CLASS_UNSAFE_LIST = Class.forName("org.bukkit.craftbukkit." + VERSION + ".util.UnsafeList");
		} catch (ClassNotFoundException e) {
			// incompatible version
			e.printStackTrace();
			SpleefMain.getInstance().log(Level.SEVERE, "Could not access NMS classes. Please use a plugin version which is compatible with your server version for full functionality.");
			nmsFailed = true;
		}

	}
	
	
	private static Class<?> CLASS_PATHFINDER_GOAL_WALK_TO_TILE;	
	static {
		try {
			CtClass clazz = ClassPool.getDefault().get("net.minecraft.server." + VERSION + ".PathfinderGoal");
			CtClass subClass = ClassPool.getDefault().makeClass("PathfinderGoalWalktoTile", clazz);
			
			CtField entityField = CtField.make("private net.minecraft.server." + VERSION + ".EntityInsentient entity;", subClass);
			CtField pathField = CtField.make("private net.minecraft.server." + VERSION + ".PathEntity path;", subClass);
			CtField playerField = CtField.make("private org.bukkit.entity.Player p;", subClass);
			
			subClass.addField(entityField);
			subClass.addField(pathField);
			subClass.addField(playerField);
			
			CtConstructor constructor = CtNewConstructor.make(
				"public PathfinderGoalWalktoTile(Object entitycreature, org.bukkit.entity.Player p) { " +
					"this.entity = (net.minecraft.server." + VERSION + ".EntityInsentient) entitycreature; " +
					"this.p = p; " +
				"} "
			, subClass);
			
			subClass.addConstructor(constructor);
			
			CtMethod aMethod = CtNewMethod.make(
				"public boolean a() { " +
			
					"org.bukkit.block.Block target = null; " +
					"try { " +
						"target = p.getTargetBlock((HashSet<Byte>) null, 20); " +
					"} catch (IllegalStateException e) { " +
						"target = p.getLocation().getBlock(); " +
					"}" +	
					"org.bukkit.Location targetLocation = target.getLocation(); " +
					
					"targetLocation.setY(p.getLocation().getY()); " +
					
					"if (targetLocation.getWorld() != entity.getBukkitEntity().getWorld()) { " +
						"entity.getBukkitEntity().teleport(targetLocation); " +
						"return false; " +
					"} " +
						
					"if (targetLocation.distanceSquared(entity.getBukkitEntity().getLocation()) > 25*25) { " +
						"entity.getBukkitEntity().teleport(targetLocation); " +
					"} " +
					
					"boolean flag = this.entity.getNavigation().m(); " +
					
					"this.path = this.entity.getNavigation().a(targetLocation.getX() + 1, targetLocation.getY(), targetLocation.getZ()); " +
					
					"if (this.path != null) { " +
						"this.c(); " +
					"} " +
					"return this.path != null; " +
				"} ",
			subClass);
			
			subClass.addMethod(aMethod);
			
			CtMethod cMethod = CtNewMethod.make(
				"public void c() { " +
					"this.entity.getNavigation().a(this.path, 1D); " +
				"} "
			, subClass);			
	
			subClass.addMethod(cMethod);
			CLASS_PATHFINDER_GOAL_WALK_TO_TILE = subClass.toClass();
		} catch (NotFoundException | CannotCompileException e) {
			// incompatible version
			e.printStackTrace();
			SpleefMain.getInstance().log(Level.SEVERE, "Could not access NMS classes. Please use a plugin version which is compatible with your server version for full functionality.");
			nmsFailed = true;
		}
	}

	private static Field gsa;
	private static Field goalSelector;
	private static Field targetSelector;

	static {
		try {
			gsa = CLASS_PATHFINDER_GOAL_SELECTOR.getDeclaredField("b");
			gsa.setAccessible(true);

			goalSelector = CLASS_ENTITY_INSENTIENT.getDeclaredField("goalSelector");
			goalSelector.setAccessible(true);

			targetSelector = CLASS_ENTITY_INSENTIENT.getDeclaredField("targetSelector");
			targetSelector.setAccessible(true);
		} catch (NoSuchFieldException | SecurityException e) {
			// incompatible version
			e.printStackTrace();
			SpleefMain.getInstance().log(Level.SEVERE, "Could not access NMS classes. Please use a plugin version which is compatible with your server version for full functionality.");
			nmsFailed = true;
		}
	}

	public static boolean setGuide(LivingEntity entity, Player guide) {
		if (nmsFailed) {
			return false;
		}
		try {
			Object nms_entity = CLASS_CRAFT_LIVING_ENTITY.getMethod("getHandle").invoke(entity);

			Object goal = goalSelector.get(nms_entity);
			Object target = targetSelector.get(nms_entity);
			
			gsa.set(goal, CLASS_UNSAFE_LIST.newInstance());			
			gsa.set(target, CLASS_UNSAFE_LIST.newInstance());			
			
			goal.getClass().getMethod("a", int.class, CLASS_PATHFINDER_GOAL).invoke(goal, 0, CLASS_PATHFINDER_GOAL_FLOAT.getConstructor(CLASS_ENTITY_INSENTIENT).newInstance(nms_entity));
			goal.getClass().getMethod("a", int.class, CLASS_PATHFINDER_GOAL).invoke(goal, 1, CLASS_PATHFINDER_GOAL_WALK_TO_TILE.getConstructor(Object.class, Player.class).newInstance(nms_entity, guide));
			return true;
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException | InstantiationException e) {
			// incompatible version
			e.printStackTrace();
			SpleefMain.getInstance().log(Level.SEVERE, "Could not access NMS classes. Please use a plugin version which is compatible with your server version for full functionality.");
			nmsFailed = true;
			return false;
		}
	}

}