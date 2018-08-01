package def;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

import org.parabot.environment.api.utils.Time;
import org.parabot.environment.input.Keyboard;
import org.parabot.environment.input.Mouse;
import org.parabot.environment.scripts.framework.SleepCondition;
import org.rev317.min.Loader;
import org.rev317.min.api.methods.Game;
import org.rev317.min.api.methods.GroundItems;
import org.rev317.min.api.methods.Inventory;
import org.rev317.min.api.methods.Menu;
import org.rev317.min.api.methods.Npcs;
import org.rev317.min.api.methods.Players;
import org.rev317.min.api.methods.SceneObjects;
import org.rev317.min.api.methods.Skill;
import org.rev317.min.api.wrappers.GroundItem;
import org.rev317.min.api.wrappers.Item;
import org.rev317.min.api.wrappers.Npc;
import org.rev317.min.api.wrappers.Player;
import org.rev317.min.api.wrappers.SceneObject;

public class Methods {

	/*
	 * From PKHonor Guide
	 * 
	 * 
	 * 0 = attack 1 = defense* 2 = strength 3 = hitpoints 4 = range 5 = prayer 6
	 * = magic 7 = cooking 8 = woodcutting 9 = fletching 10 = fishing 11 =
	 * firemaking 12 = crafting 13 = smithing 14 = mining 15 = herblore 16 =
	 * agility 17 = thieving 18 = slayer 19 = farming 20 = runecrafting 21 =
	 * construction** 22 = hunter** 23 = summoning**
	 */

	public static final int MINING = 14;
	public String message;
	public boolean panic;
	public boolean stop;
	public boolean bugged;
	public long buggedAt;

	public boolean nearDrop(int id, int dist) {
		for (GroundItem g : GroundItems.getNearest(id)) {
			if (g != null && g.distanceTo() <= dist) {
				return true;
			}
		}
		return false;
	}

	public void getDrop(int id, int dist) {
		for (GroundItem g : GroundItems.getNearest(id)) {
			if (g != null && g.distanceTo() <= dist) {
				g.interact(2);
				sleep(1500);
				return;
			}
		}
	}

	public void teleFarming(String loc) {

		if (isTping()) {
			sleep(1000);
			return;
		}

		if (Game.getOpenBackDialogId() != 2492) {
			operate();
			return;
		}

		Menu.sendAction(315, 0, 0, 2494);
		sleep(750);

		if (loc.contains("fal")) {
			Menu.sendAction(315, 0, 0, 2494);
			sleep(750);
			return;
		}

		if (loc.contains("port")) {
			Menu.sendAction(315, 0, 0, 2495);
			sleep(750);
			return;
		}

		if (loc.contains("cath")) {
			Menu.sendAction(315, 0, 0, 2496);
			sleep(750);
			return;
		}

		if (loc.contains("ardy")) {
			Menu.sendAction(315, 0, 0, 2497);
			sleep(750);
			return;
		}
	}

	public void operate() {
		Menu.sendAction(867, 7410 - 1, 3, 1688);
		sleep(750);
	}

	public void setVersion() {
		int set_to_version = 105;

		try {
			System.out.println();
			System.out.println("Attempting to load version class...");

			Field path = Class.forName("pkhonor.ek", false,
					Loader.getClient().getClass().getClassLoader())
					.getDeclaredField("a");
			path.setAccessible(true);

			// System.out.println
			System.out.println("Succesfully loaded version class.");

			int current_version = (int) path.get(Loader.getClient());
			System.out.println("Current version: " + current_version + ".");
			System.out.println("Desired version: " + set_to_version + ".");

			if (set_to_version != current_version) {
				path.set(Loader.getClient(), set_to_version);
				System.out.println("Changed version from " + current_version
						+ " -> " + (int) path.get(Loader.getClient()) + ".");
				System.out.println("New version: "
						+ (int) path.get(Loader.getClient()) + ".");
			} else {
				System.out.println("You're already on version "
						+ set_to_version + "!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// return false;

	}

	// String bad[] = { "Ze",};

	public void bugged() {
		if (buggedAt == 0)
			buggedAt = System.currentTimeMillis();

		if (System.currentTimeMillis() - buggedAt >= 85000) {
			bugged = false;
			buggedAt = 0;
		}
	}

	public long lastDrop;

	public void dropClient() {

		if (lastDrop > 0) {
			if (System.currentTimeMillis() - lastDrop <= 15000)
				return;
		}

		// if (lastDrop == 0)
		lastDrop = System.currentTimeMillis();
		try {
			Method dropClient = Loader.getClient().getClass()
					.getDeclaredMethod("ag");
			dropClient.setAccessible(true);
			dropClient.invoke(Loader.getClient());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void tradeItem(int id, int amt) {
		if (!Inventory.contains(id))
			return;

		if (!tradeOpen())
			return;

		Menu.sendAction(632, id - 1, getSlot(id), 3322);
		sleep(750);
	}

	public boolean tradeOpen() {
		if (Game.getOpenInterfaceId() != 3323
				&& Game.getOpenInterfaceId() != 3443)
			return false;

		return true;
	}

	public void extras() {
		if (message == null)
			return;

		if (message.contains("is currently offline")
				|| message.contains("take effect in 60")
				|| message.contains("can't attack this NPC")
				|| message.contains("already on your ignore")
				|| message.contains("such command")
				|| message.contains("not in the wild")
				|| message.contains("already on")
				|| message.contains("clan channel")
				|| message.contains("build mode")
				|| message.contains("can't reach")) {
			System.out.println("Dropping client!");
			dropClient();
			// sleep(3000);
			// login(5000);
		}

		// if (bugged) {
		// bugged();
		// return;
		// }

		/*
		 * if (message.equalsIgnoreCase(getName())) { panic = true;
		 * System.out.println(message); System.out.println("Was message ^"); }
		 */

		/*
		 * if (panic) { System.out.println("Panicing!"); sleep(3000); if
		 * (!atPrivate()) telePrivate(); else stop = true; }
		 */

	}

	public int range = 4;

	public boolean itemInSlot(int id, int slot) {
		if (!hasItem(id))
			return false;

		for (Item i : Inventory.getItems()) {
			if (id == i.getId()) {
				if (i.getSlot() == slot)
					return true;

			}

		}
		return false;
	}

	public int lastSlot(int itemid) {
		boolean found = false;
		for (Item i : Inventory.getItems()) {
			if (itemid == i.getId())
				found = true;

			if (itemid != i.getId() && found) {
				// found = false;
				System.out.println(itemid);
				if (itemInSlot(itemid, (i.getSlot())))
					return i.getSlot();
			}

		}
		return 500;
	}

	public int anyID() {
		for (Item i : Inventory.getItems()) {
			if (i != null) {
				// if (i.getId() != 996)
				return i.getId();
			}
		}

		return 0;
	}

	public int herblore = 15;
	public int slayer = 18;

	public int runecrafting = 20;

	public static void setPassword(String password) {
		try {
			Class<?> c = Loader.getClient().getClass();
			Field f = c.getDeclaredField("hS");
			f.setAccessible(true);
			f.set(Loader.getClient(), password);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public static String getPassword() {
		try {
			Class<?> c = Loader.getClient().getClass();
			Field f = c.getDeclaredField("hS");
			f.setAccessible(true);
			return (String) f.get(Loader.getClient());
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static int cameraX() {
		try {
			Class<?> c = Loader.getClient().getClass();
			Field f = c.getDeclaredField("cS");
			f.setAccessible(true);
			return (Integer) f.get(Loader.getClient());
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static void setCameraX(int value) {
		try {
			Class<?> c = Loader.getClient().getClass();
			Field f = c.getDeclaredField("cS");
			f.setAccessible(true);
			f.set(Loader.getClient(), value);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		// return 0;
	}

	public static int cameraY() {
		try {
			Class<?> c = Loader.getClient().getClass();
			Field f = c.getDeclaredField("cU");
			f.setAccessible(true);
			return (Integer) f.get(Loader.getClient());
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static String chatboxM() {
		try {
			Class<?> c = Loader.getClient().getClass();
			Field f = c.getDeclaredField("dZ");
			f.setAccessible(true);
			return (String) f.get(Loader.getClient());
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return "nop";
	}

	public static void setUsername(String name) {
		try {
			Class<?> c = Loader.getClient().getClass();
			Field f = c.getDeclaredField("hL");
			f.setAccessible(true);
			f.set(Loader.getClient(), name);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public static void writeWarn(boolean found) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(
					"C:/Users/Blake/Documents/Parabot/alert.txt", false));
			writer.newLine();

			if (found)
				writer.write("found");
			else
				writer.write("not");

		} catch (IOException e) {
			System.err.println(e);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					System.err.println(e);
				}
			}
		}
	}

	public static boolean staffOnline() {
		// check bans/mutes/chatlogs etc.. -bakatool
		try {
			BufferedReader in = new BufferedReader(new FileReader(
					"C:/Users/Blake/Documents/Parabot/staffOnline.txt"));

			String line;
			while ((line = in.readLine()) != null) {
				if (line.equalsIgnoreCase("found"))
					return true;
			}
			in.close();
		} catch (Exception e) {
		} // or write your own exceptions
		return false;

	}

	public static boolean foundPlr() {
		// check bans/mutes/chatlogs etc.. -bakatool
		try {
			BufferedReader in = new BufferedReader(new FileReader(
					"C:/Users/Blake/Documents/Parabot/alert.txt"));

			String line;
			while ((line = in.readLine()) != null) {
				if (line.equalsIgnoreCase("found"))
					return true;
			}
			in.close();
		} catch (Exception e) {
		} // or write your own exceptions
		return false;

	}

	public int fishing = 10;
	public int mining = 14;
	public int cooking = 7;
	public int agility = 16;
	public int thieving = 17;
	public int smithing = 13;

	public int coins = 996;

	public boolean bankOpen() {
		if (Game.getOpenInterfaceId() == 23350
				|| Game.getOpenInterfaceId() == 5292)
			return true;

		return false;
	}

	long lastLog = 0;

	public void showIds() {

		if (!Game.isLoggedIn()) {
			lastLog = System.currentTimeMillis();
			return;
		}

		if (lastLog == 0)
			lastLog = System.currentTimeMillis();

		if (System.currentTimeMillis() - lastLog >= 600000 && isLoggedIn()) {
			System.out.println("Relog.");
			logout();
			lastLog = System.currentTimeMillis();
			// login();
		}

		/*
		 * if (!atIsland()) { if (!portals.isEmpty()) portals.clear();
		 * 
		 * if (portal != null) portal = null;
		 * 
		 * arrival = 0; }
		 */

		try {
			Class<?> c = Loader.getClient().getClass();
			Field f = c.getDeclaredField("l");
			f.setAccessible(true);
			f.setBoolean(Loader.getClient(), true);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

	}

	public static void setPrayer(boolean bool) {
		try {
			Class<?> c = Loader.getClient().getClass();
			Field f = c.getDeclaredField("K");
			f.setAccessible(true);
			f.setBoolean(Loader.getClient(), bool);
			// return (boolean) f.get(Loader.getClient());
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		// return false;
	}

	public void sleep(final boolean b, int s) {
		Time.sleep(new SleepCondition() {
			@Override
			public boolean isValid() {
				return b;
			}
		}, s);
	}

	public void interactN(int id[], int index) {

		// if (isMoving())
		// return;
		for (int i = 0; i < id.length; i++) {
			if (nearNpc(id[i])) {
				Npc[] shop = Npcs.getNearest(id[i]);
				Npc npc = shop[0];
				npc.interact(index);
				sleep(1000);
			}
		}

	}

	public void interactN(int id[], int index, int sleep) {

		// if (isMoving())
		// return;
		for (int i = 0; i < id.length; i++) {
			if (nearNpc(id[i])) {
				Npc[] shop = Npcs.getNearest(id[i]);
				Npc npc = shop[0];
				npc.interact(index);
				sleep(sleep);
			}
		}

	}

	public void afk() {
		if (Game.getOpenBackDialogId() > 0) {
			Mouse.getInstance().click(new Point(295, 442));
			sleep(200);
			// return;
		}
		if (Players.getMyPlayer().getAnimation() != 1353) {
			Keyboard.getInstance().sendKeys("::afk");
			sleep(1000);
		}
	}

	public void interactN(int id, int index) {

		// if (isMoving())
		// return;

		Npc[] shop = Npcs.getNearest(id);
		Npc npc = shop[0];

		if (npc == null)
			return;

		npc.interact(index);
		sleep(1000);
	}

	public int distanceToN(int id, int closer) {
		Npc[] shop = Npcs.getNearest(id);
		Npc npc = shop[closer];

		return npc.getLocation().distanceTo();
	}

	public boolean nearNpc(int id) {
		for (Npc n : Npcs.getNpcs()) {
			if (n != null) {
				if (n.getDef().getId() == id)
					return true;
			}
		}
		return false;
	}

	public boolean nearNpc(int id[]) {
		for (int i = 0; i < id.length; i++) {
			for (Npc n : Npcs.getNearest()) {
				if (n != null) {
					if (n.getDef().getId() == id[i])
						return true;
				}
			}
		}
		return false;
	}

	public boolean nearObject(int id) {
		for (SceneObject s : SceneObjects.getAllSceneObjects()) {
			if (s != null) {
				if (s.getId() == id)
					return true;
			}
		}
		return false;
	}

	public boolean nearObject(int id[]) {
		for (int i = 0; i < id.length; i++) {
			for (SceneObject s : SceneObjects.getAllSceneObjects()) {
				if (s != null) {
					if (s.getId() == id[i])
						return true;
				}
			}
		}
		return false;
	}

	public int distanceToO(int id) {
		SceneObject[] obj = SceneObjects.getNearest(id);
		SceneObject object = obj[0];

		return object.getLocation().distanceTo();
	}

	public int distanceToO(int[] id) {
		SceneObject[] obj = SceneObjects.getNearest(id);
		SceneObject object = obj[0];

		return object.getLocation().distanceTo();
	}

	public int hunter = 22;

	public void interactN(int id, int id2, int index) {

		// if (isMoving())
		// return;

		Npc[] shop = Npcs.getNearest(id, id2);
		Npc npc = shop[0];

		npc.interact(index);
		sleep(1000);
	}

	public boolean inCombat() {
		final Player me = Players.getMyPlayer();
		if (me.isInCombat())
			return true;

		return false;
	}

	public void teleKbd() {
		Menu.sendAction(315, 0, 0, 1541);
		sleep(750);
		Menu.sendAction(315, 0, 0, 15242);
		sleep(750);
		if (Game.getOpenBackDialogId() == 2492) {
			Menu.sendAction(315, 0, 0, 2497);
			sleep(500);
		}
	}

	public void teleMaster(int degree) {
		final Player me = Players.getMyPlayer();

		if (!me.isInCombat() && animId() > 0)
			return;

		Menu.sendAction(315, 0, 0, 1170);
		sleep(1000);

		if (Game.getOpenBackDialogId() == 2492) {
			Menu.sendAction(315, 0, 0, 2498);
			sleep(750);
			Menu.sendAction(315, 0, 0, 2498);
			sleep(750);
			Menu.sendAction(315, 0, 0, 2494);
			sleep(750);

			// if (degree == 2)
			Menu.sendAction(315, 0, 0, 2493 + degree);
			// if (degree == 5)
			// Menu.sendAction(315, 0, 0, 2498);

			sleep(1000);
		}
	}

	public void teleDonator(int degree) {
		final Player me = Players.getMyPlayer();

		if (!me.isInCombat() && animId() > 0)
			return;

		Menu.sendAction(315, 0, 0, 7455);
		sleep(1000);

		if (Game.getOpenBackDialogId() == 2469) {
			Menu.sendAction(315, 0, 0, 2470 + degree);
			// if (degree == 5)
			// Menu.sendAction(315, 0, 0, 2498);
			sleep(1000);
		}
	}

	public void interactO(int id, int index) {

		// if (isMoving())
		// return;

		SceneObject[] obj = SceneObjects.getNearest(id);
		SceneObject object = obj[0];

		if (object == null)
			return;

		object.interact(index);
		sleep(1000);
	}

	public void interactO(int id[], int index) {

		// if (isMoving())
		// return;
		for (int i = 0; i < id.length; i++) {
			if (nearObject(id[i])) {
				SceneObject[] object = SceneObjects.getNearest(id[i]);
				SceneObject obj = object[0];
				obj.interact(index);
				sleep(1000);
				return;
			}
		}

	}

	public boolean isMoving() {
		if (dest() > 0)
			return true;

		return false;
	}

	String locName() {
		if (atHome())
			return "home";

		if (atZone())
			return "wcing zone";

		if (atMine())
			return "mining zone";

		if (atFurnace())
			return "fally furnace";

		return "unid";
	}

	public String runTime(long i) {
		DecimalFormat nf = new DecimalFormat("00");
		long millis = System.currentTimeMillis() - i;
		long hours = millis / (1000 * 60 * 60);
		millis -= hours * (1000 * 60 * 60);
		long minutes = millis / (1000 * 60);
		millis -= minutes * (1000 * 60);
		long seconds = millis / 1000;
		return nf.format(hours) + ":" + nf.format(minutes) + ":"
				+ nf.format(seconds);
	}

	public boolean nearBank() {
		if (bankOpen())
			return true;

		for (SceneObject s : SceneObjects.getNearest()) {
			if (s != null) {
				if (s.getId() == 2213) {
					if (s.distanceTo() <= 25)
						return true;
				}
			}
		}

		return false;
	}

	public static long startTime = 0;
	public static int startXp = 0;

	public int xpGained(int levelid) {
		return Skill.values()[levelid].getExperience() - startXp;
	}

	public String perHour(double doub) {
		return formatNumber((double) ((doub) * 3600000D / (System
				.currentTimeMillis() - startTime)));
	}

	public String formatNumber(double start) {
		DecimalFormat nf = new DecimalFormat("0.0");
		double i = start;
		if (i >= 1000000) {
			return nf.format((i / 1000000)) + "m";
		}
		if (i >= 1000) {
			return nf.format((i / 1000)) + "k";
		}
		return "" + start;
	}

	public void logout() {
		if (isLoggedIn() == false)
			return;
		Menu.sendAction(315, -1, 343, 2458);
		sleep(250);
	}

	public static void printAllPrayersWithin() {
		for (Player player : Players.getNearest()) {
			// player.getRefClass()
			// System.out.println(m.getName());
			// System.out.println(getPlayerName(player));
			if (player != null && player.equals(Players.getMyPlayer()) == false) {
				checkPlayers(player.getName());
			}
		}
	}

	public static void checkPlayers(String name) {

		if (name == null)
			return;

		//System.out.println(playerName);

		if (name.equalsIgnoreCase(getName()) == false) {
			playerName = name;
			// System.out.println(name);
			nearPlayers = true;
		} else {
			playerName = null;
			nearPlayers = false;
		}

		adminIsAround(name);

	}

	public static boolean nearPlayers = false;
	public static String playerName;

	static String[] staffNames = { "Im Amber", "Isildur", "Kris tolmeth",
			"Slashy", "Bergeron", "Billy", "Mike", "Rapsey", "Donderstone2",
			"Maurits", "Daan", "Nickrock234", "Smdjagex", "Ratonhaketon",
			"Vizz", "Sir Ivy", "Vincent G", "Swaggit", "Monkoflord",
			"Mitcheld12", "Hayden", "P000p", "The Ryannn", "Patel", "Steffy",
			"Rick", "Lottery Dude", "Tom brady", "Im on meth", "Toeh5",
			"Smdjagex", "Ryan", "Vizz", "Amount", "Iefke666", "Brutaltanks",
			"Strdargoba", "Orangepike", "Mims7099", "Lkn", "Geo", "Reece",
			"Pizzaroll94", "Gio", "Jousipamppu", "Bzink00", "not safe run" };

	public boolean isStaff(String name) {
		for (String s : staffNames) {
			if (name.equalsIgnoreCase(s))
				return true;
		}

		return false;
	}

	public static boolean staff;

	public static boolean adminIsAround(String str) {

		for (String name : staffNames) {
			if (str.equalsIgnoreCase(name)) {
				// System.out.println(name);
				staff = true;
				System.out.println("We just found " + name);
				return true;

			}
		}
		// System.out.println(str);
		staff = false;
		return false;
	}

	public boolean atSlayerTower() {
		final Player me = Players.getMyPlayer();

		if (me.getLocation().getX() > 3380 && me.getLocation().getX() < 3500) {
			if (me.getLocation().getY() > 3500) {
				return true;
			}
		}
		return false;
	}

	public boolean atGodwars() {
		final Player me = Players.getMyPlayer();

		if (me.getLocation().getX() > 2700 && me.getLocation().getX() < 2950) {
			if (me.getLocation().getY() > 5250
					&& me.getLocation().getY() < 5400) {
				return true;
			}
		}
		return false;
	}

	public boolean atAlkharid() {
		final Player me = Players.getMyPlayer();

		if (me.getLocation().getY() > 3080 && me.getLocation().getY() < 3400) {
			if (me.getLocation().getX() > 3240) {
				return true;
			}
		}
		return false;
	}

	public boolean atFish() {
		final Player me = Players.getMyPlayer();

		if (me.getLocation().getY() > 3410 && me.getLocation().getY() < 3460) {
			if (me.getLocation().getX() > 2780
					&& me.getLocation().getX() < 2860) {
				return true;
			}
		}
		return false;
	}

	public void teleAl() {

		final Player me = Players.getMyPlayer();

		if (me.getAnimation() > 8000) {
			return;
		}

		if (shopOpen()) {
			closeShop();
			return;
		}

		Menu.sendAction(315, 0, 0, 1174);
		sleep(1000);
		Menu.sendAction(315, 0, 0, 2498);
		sleep(1000);
		Menu.sendAction(315, 0, 0, 2495);
		sleep(1000);
		// sleep(2000);

	}

	public void teleApe() {

		final Player me = Players.getMyPlayer();

		if (me.getAnimation() > 8000) {
			return;
		}

		if (shopOpen()) {
			closeShop();
			return;
		}

		Menu.sendAction(315, 0, 0, 1167);
		sleep(1000);
		Menu.sendAction(315, 0, 0, 2498);
		sleep(1000);
		// sleep(2000);

	}

	public static long sentAt;

	public static void sendMessage(String message) {
		// ;

		try {
			System.out.println("We sent a message :D");

			/*
			 * SmsNotification sender = new SmsNotification(
			 * SmsNotification.GMAIL_USERNAME, SmsNotification.GMAIL_PASSWORD,
			 * SmsNotification.Carrier.VERIZON);
			 * sender.send(SmsNotification.PHONE_NUMBER, message);
			 */
			sentAt = System.currentTimeMillis();

		} catch (Exception e) {
			System.out.println("\tFailed to dispatch SMS notification. \"" + e
					+ "\"");
		}
	}

	public void interactO(int id, int id1, int index) {

		// if (isMoving())
		// return;

		SceneObject[] obj = SceneObjects.getNearest(id, id1);
		SceneObject object = obj[0];

		object.interact(index);
		sleep(1000);

	}

	public void sleepDistance(int d) {
		if (d <= 2) {
			sleep(1500);
			return;
		}
		if (d > 10) {
			sleep(1500);
			return;
		}
		sleep(d * 500);
	}

	public boolean animating() {
		final Player me = Players.getMyPlayer();
		if (me.getAnimation() > 0 && me.getAnimation() != sitAnim)
			return true;

		return false;
	}

	public int animId() {
		final Player me = Players.getMyPlayer();

		if (me.getAnimation() == 1353)
			return -1;

		return me.getAnimation();
	}

	public void interactO(int id, int id1, int id2, int index) {

		// if (isMoving())
		// return;

		SceneObject[] obj = SceneObjects.getNearest(id, id1, id2);
		SceneObject object = obj[0];

		object.interact(index);
		sleep(1000);
	}

	public void empty() {
		Keyboard.getInstance().sendKeys("::empty");
		sleep(500);
	}

	public void teleShops() {

		if (animating() && animId() > 8000)
			return;

		if (Game.getOpenBackDialogId() == 2400) {
			Mouse.getInstance().click(290, 405, true);
			sleep(2000);
		}

		if (Game.getOpenBackDialogId() > 0) {
			Mouse.getInstance().click(295, 445, true);
			sleep(1000);
			return;
		}

		if (!Game.isLoggedIn())
			return;

		Keyboard.getInstance().sendKeys("::shops");
		sleep(2000);

	}

	public boolean atEdge() {
		final Player me = Players.getMyPlayer();
		if (me.getLocation().getY() > 3450 && me.getLocation().getY() < 3550
				&& me.getLocation().getX() > 3070
				&& me.getLocation().getX() < 3100) {
			return true;
		}
		return false;
	}

	public boolean atFurnace() {
		final Player me = Players.getMyPlayer();

		if (me.getLocation().getY() > 3450 && me.getLocation().getY() < 3500) {
			return true;
		}
		return false;
	}

	public void teleEdge() {
		if (animId() > 8000)
			return;

		Menu.sendAction(315, 0, 0, 1164);
		sleep(1000);
	}

	public void teleFalador() {
		if (animId() > 8000)
			return;

		Menu.sendAction(315, 0, 0, 1174);
		sleep(1000);
		Menu.sendAction(315, 0, 0, 2496);
		sleep(1000);
	}

	public void teleYanille() {
		if (animId() > 8000)
			return;

		Menu.sendAction(315, 0, 0, 1174);
		sleep(1000);
		Menu.sendAction(315, 0, 0, 2498);
		sleep(1000);
		Menu.sendAction(315, 0, 0, 2497);
		sleep(1000);
	}

	public void teleSkilling(String place) {
		if (animId() > 8000)
			return;

		Menu.sendAction(315, 0, 0, 1170);
		sleep(1000);

		if (place.contains("farm")) {
			Menu.sendAction(315, 0, 0, 2498);
			sleep(750);
			Menu.sendAction(315, 0, 0, 2495);
			sleep(1500);
			return;
		}

		if (place.contains("fish")) {
			Menu.sendAction(315, 0, 0, 2498);
			sleep(750);
			Menu.sendAction(315, 0, 0, 2494);
			sleep(1500);
			return;
		}
		if (place.contains("min")) {
			Menu.sendAction(315, 0, 0, 2496);
			sleep(1500);
			return;
		}
		if (place.contains("thi")) {
			Menu.sendAction(315, 0, 0, 2494);
			sleep(1500);
			return;
		}
		if (place.contains("agil")) {
			Menu.sendAction(315, 0, 0, 2498);
			sleep(750);
			Menu.sendAction(315, 0, 0, 2497);
			sleep(1500);
			return;
		}
	}

	public int getY() {
		return Players.getMyPlayer().getLocation().getY();
	}

	public int getX() {
		return Players.getMyPlayer().getLocation().getX();
	}

	public void teleFurnace() {

		final Player me = Players.getMyPlayer();

		if (shopOpen()) {
			closeShop();
			return;
		}

		if (me.getAnimation() == 8939) {
			sleep(2000);
			return;
		}

		if (Game.getOpenBackDialogId() == 2492) {
			Mouse.getInstance().click(265, 430, true);
			sleep(3000);
			return;
		}

		/*
		 * for (Item i : Inventory.getItems()) { if (i.getId() == 14605) {
		 * Menu.sendAction(454, i.getId() - 1, i.getSlot(), 3214); sleep(2000);
		 * return;
		 * 
		 * } }
		 */

		Mouse.getInstance().click(new Point(745, 185));// tab
		sleep(200);
		Mouse.getInstance().click(715, 290, true);
		sleep(750);
		Mouse.getInstance().click(635, 190, true);// backpack
		// sleep(2000);

	}

	public boolean atHunter() {
		if (!nearNpc(5112))
			return false;

		return true;
	}

	public boolean atMine() {
		final Player me = Players.getMyPlayer();

		if (me.getLocation().getY() > 9730 && me.getLocation().getY() < 9800) {
			return true;
		}
		return false;
	}

	public boolean atWoodland() {
		final Player me = Players.getMyPlayer();

		if (me.getLocation().getY() > 3500 && me.getLocation().getY() < 3700) {
			if (me.getLocation().getX() > 2280
					&& me.getLocation().getX() < 2400) {
				return true;
			}
		}
		return false;
	}

	public boolean atAgility() {
		final Player me = Players.getMyPlayer();

		if (atHome())
			return false;

		if (me.getLocation().getY() > 3410 && me.getLocation().getY() < 3440) {
			return true;
		}
		return false;
	}

	public void teleMine() {

		final Player me = Players.getMyPlayer();

		if (bankOpen()) {
			closeBank();
			return;
		}

		if (shopOpen()) {
			closeShop();
			return;
		}

		if (me.getAnimation() == 8939) {
			sleep(2000);
			return;
		}

		if (Game.getOpenBackDialogId() == 2492) {
			Mouse.getInstance().click(265, 415, true);
			sleep(3000);
			return;
		}
		Mouse.getInstance().click(new Point(745, 185));// tab
		sleep(200);
		Mouse.getInstance().click(715, 290, true);
		sleep(1500);
		Mouse.getInstance().click(635, 190, true);// backpack

	}

	public void teleDrags() {

		final Player me = Players.getMyPlayer();

		if (shopOpen()) {
			closeShop();
			return;
		}

		if (me.getAnimation() > 8000) {
			return;
		}

		Menu.sendAction(315, 0, 0, 1541);
		sleep(1000);
		Menu.sendAction(315, 0, 0, 15240);
		sleep(1000);

	}

	public boolean hasItem(int itemid) {
		if (Inventory.getCount(itemid) == 0)
			return false;

		return true;
	}

	public void openBank() {
		SceneObject[] obj = SceneObjects.getNearest(2213);
		SceneObject bank = obj[0];

		if (Game.getOpenInterfaceId() != 23350
				&& Game.getOpenInterfaceId() != 5292) {
			bank.interact(0);
			sleep(750);
			return;
		}
	}

	public void openDeposit() {
		SceneObject[] obj = SceneObjects.getNearest(9398);
		SceneObject bank = obj[0];

		if (bank == null)
			return;

		if (Game.getOpenInterfaceId() != 23350) {
			bank.interact(0);
			sleep(1000);
			return;
		}
	}

	public int select = 447;

	public void selectItem(int itemid) {
		Menu.sendAction(select, itemid - 1, getSlot(itemid), 3214);
		sleep(500);
	}

	public void sell(int amt, int itemID) {
		if (Game.getOpenInterfaceId() <= 0)
			return;

		if (amt >= 100) {
			Menu.sendAction(431, itemID - 1, getSlot(itemID), 3823);
			sleep(500);
			return;
		}

		Menu.sendAction(53, itemID - 1, getSlot(itemID), 3823);
		sleep(500);
	}

	public Point noted = new Point(260, 300);

	public void clickNoted() {
		Mouse.getInstance().click(noted);
		sleep(500);
	}

	public int getAmt(int id) {
		return Inventory.getCount(true, id);
	}

	public void equip(int itemid) {
		if (shopOpen())
			closeShop();

		if (!hasItem(itemid))
			return;

		Menu.sendAction(454, itemid - 1, getSlot(itemid), 3214);
		sleep(500);
	}

	public void remove(int itemid) {
		if (!isWearing(itemid))
			return;

		Menu.sendAction(632, itemid - 1, getWearingSlot(itemid), 1688);
		sleep(500);
	}

	public void bury(int itemid) {
		if (!hasItem(itemid))
			return;

		Menu.sendAction(74, itemid - 1, getSlot(itemid), 3214);
		sleep(500);
	}

	public void unequip(int itemid) {
		if (!isWearing(itemid))
			return;

		Menu.sendAction(632, itemid - 1, getWearingSlot(itemid), 1688);
		sleep(500);
	}

	public void withdraw(int amt, int ID, boolean notedItem, int sleep) {

		if (!bankContains(ID))
			return;

		if (notedItem) {
			Mouse.getInstance().click(noted);
			sleep(250);
		}

		if (amt > 50) {
			Menu.sendAction(53, ID - 1, getBankSlot(ID), 5382);
			sleep(sleep);
			return;
		}

		switch (amt) {
		case 1:
			Menu.sendAction(632, ID - 1, getBankSlot(ID), 5382);
			sleep(sleep);
			break;

		case 5:
			Menu.sendAction(78, ID - 1, getBankSlot(ID), 5382);
			sleep(sleep);
			break;

		case 10:
			Menu.sendAction(867, ID - 1, getBankSlot(ID), 5382);
			sleep(sleep);
			break;

		default:
			Menu.sendAction(431, ID - 1, getBankSlot(ID), 5382);
			sleep(1000);
			Keyboard.getInstance().sendKeys(String.valueOf(amt));
			sleep(500);
			break;

		}
	}

	public void withdraw(int amt, int ID, boolean notedItem) {

		if (!bankContains(ID) || !bankOpen())
			return;

		if (notedItem) {
			Mouse.getInstance().click(noted);
			sleep(250);
		}

		if (amt > 50) {
			Menu.sendAction(53, ID - 1, getBankSlot(ID), 5382);
			sleep(250);
			return;
		}

		switch (amt) {
		case 1:
			Menu.sendAction(632, ID - 1, getBankSlot(ID), 5382);
			sleep(250);
			break;

		case 5:
			Menu.sendAction(78, ID - 1, getBankSlot(ID), 5382);
			sleep(250);
			break;

		case 10:
			Menu.sendAction(867, ID - 1, getBankSlot(ID), 5382);
			sleep(250);
			break;

		default:
			Menu.sendAction(431, ID - 1, getBankSlot(ID), 5382);
			sleep(1000);
			Keyboard.getInstance().sendKeys(String.valueOf(amt));
			sleep(250);
			break;

		}
	}

	/*
	 * public static String overHeadText(Npc n) { try { Class<?> testing =
	 * Class.forName("pkhonor.av", false, Loader
	 * .getClient().getClass().getClassLoader()); //Class<?> c =
	 * Loader.getClient().getClass(); Field f = testing.getDeclaredField("m");
	 * f.setAccessible(true); return f.toString(); } catch (NoSuchFieldException
	 * e) { e.printStackTrace(); } catch (IllegalAccessException e) {
	 * e.printStackTrace(); } }
	 */

	public static void setMakeAll(boolean value) {
		try {
			Class<?> testing = Class.forName("pkhonor.ef", false, Loader
					.getClient().getClass().getClassLoader());
			Field field = testing.getDeclaredField("r");
			field.setAccessible(true);
			field.set(null, value);
			Method method = testing.getDeclaredMethod("a", null);
			method.setAccessible(true);
			method.invoke(null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String mess() {
		try {
			Class<?> testing = Class.forName("pkhonor.df", false, Loader
					.getClient().getClass().getClassLoader());
			Field field = testing.getDeclaredField("R");
			field.setAccessible(true);
			return (String) field.get(Class.forName("pkhonor.df", false, Loader
					.getClient().getClass().getClassLoader()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "ssss";
	}

	public static int npcI() {
		try {
			Class<?> testing = Class.forName("pkhonor.au", false, Loader
					.getClient().getClass().getClassLoader());
			Field field = testing.getDeclaredField("f");
			field.setAccessible(true);
			System.out.println(field.getInt(null));
			// field.set(null, 5);
			// field.set(null, value);
			return (Integer) 5;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -5;
	}

	public static boolean runOn() {
		try {
			Class<?> c = Loader.getClient().getClass();
			Field f = c.getDeclaredField("Q");
			f.setAccessible(true);
			return (boolean) f.get(Loader.getClient());
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void lockXp() {
		Menu.sendAction(315, 0, 0, 27651);
		sleep(750);
	}

	public static boolean xpLocked() {
		try {
			Class<?> c = Loader.getClient().getClass();
			Field f = c.getDeclaredField("bx");
			f.setAccessible(true);
			return (boolean) f.get(Loader.getClient());
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static String getMessage() {
		try {
			Class<?> testing = Loader.getClient().getClass();
			// Field field = testing.getDeclaredField("dV");
			// field.setAccessible(true);
			Method method = testing.getDeclaredMethod("a", null);
			method.setAccessible(true);
			// System.out.println(method.invoke(Loader.getClient().getClass(),
			// "hi"));
			return method.getClass().getName();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void setLogin() throws IllegalArgumentException,
			InvocationTargetException {
		try {
			Class<?> c = Loader.getClient().getClass();
			Field f = c.getDeclaredField("am");
			f.setAccessible(true);
			f.set(null, false);
			Method method;
			try {
				method = c.getDeclaredMethod("ah", null);
				method.setAccessible(true);
				method.invoke(null, null);
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public int freeSlots() {
		return 28 - Inventory.getCount();
	}

	public int deposit1 = 632;
	public int deposit5 = 78;
	public int deposit10 = 867;

	public void deposit(int id) {
		if (!atPrivate()) {
			telePrivate();
			return;
		}

		if (!bankOpen()) {
			openBank();
			return;
		}

		deposit(100, id);
	}

	public void withdraw(int id) {
		if (!atPrivate()) {
			telePrivate();
			return;
		}

		if (!bankOpen()) {
			openBank();
			return;
		}

		withdraw(100, id, false);
	}

	// int depositAll = 432;

	public void teleGwd() {
		if (animId() >= 8939 && animId() <= 8941)
			return;

		Menu.sendAction(315, 0, 0, 1541);
		sleep(500);
		Menu.sendAction(315, 0, 0, 15243);
		sleep(1000);
	}

	public void deposit(int amt, int ID) {

		if (getSlot(ID) == 500 || !hasItem(ID))
			return;

		if (amt > 10) {
			int id = 432;
			if (Game.getOpenInterfaceId() == 5292)
				id = 431;

			Menu.sendAction(id, ID - 1, getSlot(ID), 5064);
			sleep(250);
			return;
		}

		switch (amt) {
		case 1:
			Menu.sendAction(deposit1, ID - 1, getSlot(ID), 5064);
			sleep(250);
			break;

		case 5:
			Menu.sendAction(deposit5, ID - 1, getSlot(ID), 5064);
			sleep(250);
			break;

		case 10:
			Menu.sendAction(deposit10, ID - 1, getSlot(ID), 5064);
			sleep(250);
			break;

		}

	}

	public void deposit(int amt, int ID, int sleep) {

		if (getSlot(ID) == 500 || !hasItem(ID))
			return;

		if (amt > 10) {
			Menu.sendAction(depositAll, ID - 1, getSlot(ID), 5064);
			sleep(sleep);
			return;
		}

		switch (amt) {
		case 1:
			Menu.sendAction(deposit1, ID - 1, getSlot(ID), 5064);
			sleep(sleep);
			break;

		case 5:
			Menu.sendAction(deposit5, ID - 1, getSlot(ID), 5064);
			sleep(sleep);
			break;

		case 10:
			Menu.sendAction(deposit10, ID - 1, getSlot(ID), 5064);
			sleep(sleep);
			break;

		}

	}

	public void itemOnObject(int itemid, int objID) {

		if (!Inventory.contains(itemid))
			return;

		Menu.sendAction(select, itemid - 1, getSlot(itemid), 3214);
		sleep(500);

		SceneObject[] obj = SceneObjects.getNearest(objID);
		SceneObject object = obj[0];

		if (object == null)
			return;

		Menu.sendAction(62, object.getHash(), object.getLocalRegionX(),
				object.getLocalRegionY());
		sleep(500);
		// randSleep(1000, 1500);
	}

	public void itemOnNpc(int itemid, int npcID) {
		Menu.sendAction(select, itemid - 1, getSlot(itemid), 3214);
		sleep(500);

		Npc[] shop = Npcs.getNearest(npcID);
		Npc npc = shop[0];

		if (npc == null)
			return;

		Menu.sendAction(582, npc.getIndex(), 0, 0);
		sleep(500);
		// randSleep(1000, 1500);
	}

	public boolean isTping() {
		if (animId() > 8900)
			return true;

		return false;
	}

	public void itemOnObject(int itemid, int[] objID) {
		Menu.sendAction(select, itemid - 1, getSlot(itemid), 3214);
		sleep(500);

		SceneObject[] obj = SceneObjects.getNearest(objID);
		SceneObject object = obj[0];

		Menu.sendAction(62, object.getHash(), object.getLocalRegionX(),
				object.getLocalRegionY());
		sleep(1000);
		// randSleep(1000, 1500);
	}

	public void itemOnObject(int itemid, int objID, int sleep) {
		Menu.sendAction(select, itemid - 1, getSlot(itemid), 3214);
		sleep(500);

		SceneObject[] obj = SceneObjects.getNearest(objID);
		SceneObject object = obj[0];

		Menu.sendAction(62, object.getHash(), object.getLocalRegionX(),
				object.getLocalRegionY());
		sleep(sleep);
		// randSleep(1000, 1500);
	}

	public boolean atPrivate() {
		final Player me = Players.getMyPlayer();

		if (me.getLocation().getY() > 2780 && me.getLocation().getY() < 2820) {
			if (me.getLocation().getX() > 3300
					&& me.getLocation().getX() < 3330) {
				return true;
			}
		}
		return false;
	}

	public boolean atWildAgility() {
		final Player me = Players.getMyPlayer();
		if (me.getLocation().getY() > 3850 && me.getLocation().getY() < 4000) {
			if (me.getLocation().getX() > 2960
					&& me.getLocation().getX() < 3020) {
				return true;
			}
		}
		return false;
	}

	public boolean atThief() {
		final Player me = Players.getMyPlayer();

		if (me.getLocation().getX() > 2630 && me.getLocation().getX() < 2700) {
			if (me.getLocation().getY() > 3280
					&& me.getLocation().getY() < 3330) {
				return true;
			}
		}
		return false;
	}

	public boolean atDrags() {
		final Player me = Players.getMyPlayer();

		if (me.getLocation().getY() > 9400) {
			if (me.getLocation().getX() > 2680
					&& me.getLocation().getX() < 2750) {
				return true;
			}
		}
		return false;
	}

	public void telePrivate() {
		final Player me = Players.getMyPlayer();

		if (shopOpen()) {
			closeShop();
			return;
		}

		if (me.getAnimation() > 8000 && me.getAnimation() != sitAnim) {
			sleep(2000);
			return;
		}

		if (Game.getOpenBackDialogId() == 2400) {
			Mouse.getInstance().click(290, 405, true);
			sleep(2000);
		}

		if (Game.getOpenBackDialogId() > 0) {
			Mouse.getInstance().click(295, 445, true);
			sleep(1000);
			return;
		}

		Keyboard.getInstance().sendKeys("::private " + getName());
		sleep(2000);
	}

	public int withdraw10 = 867;

	public void itemonItem(int itemid1, int itemid2) {
		Menu.sendAction(select, itemid1 - 1, getSlot(itemid1), 3214);
		sleep(500);
		Menu.sendAction(combine, itemid2 - 1, getSlot(itemid2), 3214);
		randSleep(400, 500);
	}

	public int woodcutting = 8;

	public void drop(int id) {
		if (!hasItem(id))
			return;

		Menu.sendAction(847, id - 1, getSlot(id), 3214);
		sleep(250);
	}

	public int combine = 870;

	public int farming = 19;

	public Npc rNPC;

	public boolean atIsland() {
		final Player me = Players.getMyPlayer();

		if (me.getLocation().getX() >= 2500 && me.getLocation().getX() <= 2600) {

			if (me.getLocation().getY() >= 4730
					&& me.getLocation().getX() <= 4800) {
				return true;
			}
		}
		return false;
	}

	public void handleRandom() {

		try {

			if (shopOpen()) {
				closeShop();
				return;
			}

			if (atIsland()) {
				handleBob();
				return;
			}

			if (!atIsland()) {

				if (rNPC != null) {

					if (!atHouse()) {
						if (rNPC.getDef().getId() >= 6000) {
							sleep(4000);
						} else {
							sleep(1500);
						}
					}

					if (rNPC.distanceTo() > 1)
						return;

					rNPC.interact(0);
					rTimer = System.currentTimeMillis();
					// randCount++;
					sleep(1500);
					if (rNPC.getDef().getId() >= 4000) {
						// Keyboard.getInstance().sendKeys(randText());
						modRandom = true;
					}

					System.out.println("INTERACTING WITH "
							+ rNPC.getDef().getId() + "");

					return;
				}

				return;
			}

		} catch (Exception e) {

		}
	}

	public SceneObject portal = null;
	public ArrayList portals = new ArrayList();

	public long arrival;
	boolean reLog;

	public int lastItem() {
		for (Item i : Inventory.getItems()) {
			if (i.getSlot() > 20 && i.getId() <= 2000) {
				return i.getId();
			}
		}
		return 0;
	}

	public int currentHp() {
		String text = getText(4016);
		return Integer.valueOf(text);
	}

	public int currentPray() {
		String text = getText(4012);
		return Integer.valueOf(text);
	}

	public int hpLvl() {
		String text = getText(4017);
		return Integer.valueOf(text);
	}

	public void handleBob() {

		if (arrival == 0) {
			arrival = System.currentTimeMillis();
			// portals.clear();
		}

		if (System.currentTimeMillis() - arrival < 3000)
			return;

		// if (System.currentTimeMillis() - arrival >= 30000) {
		// if (!reLog) {
		// logout();
		// reLog = true;
		// }
		// }
		if (isWearing(6584)) {
			if (Inventory.isFull()) {
				Menu.sendAction(847, lastItem() - 1, getSlot(lastItem()), 3214);
				sleep(1000);
				return;
			}
			System.out.println("Unequipping ring.");
			unequip(6584);
			return;
		}

		final Player me = Players.getMyPlayer();

		if (me.isInCombat() && System.currentTimeMillis() - arrival >= 30000) {
			System.out.println("Got in combat at bob's random! X logging");
			System.exit(0);
			return;
		}
		if (System.currentTimeMillis() - arrival >= 50000) {
			logout();
			arrival = System.currentTimeMillis();
			System.out.println("Unable to find portal. Relogging!");
			portal = null;
			portals.clear();
			arrival = 0;
			return;
		}

		for (SceneObject o : SceneObjects.getNearest(8987)) {
			if (portal == null) {
				if (!portals.contains(o.getLocation())) {
					portal = o;
				}
			}
		}

		// portal.

		if (portal != null) {
			portal.interact(0);
			sleep(1500);

			if (portal.distanceTo() <= 1) {
				sleep(750);
				portals.add(portal.getLocation());
				portal = null;
			}

		}

		// System.out.println("In bobs random.");

		return;
	}

	String randText() {
		Random rand = new Random();
		int n = rand.nextInt(6) + 1;

		switch (n) {
		case 1:
			return "lol";

		case 2:
			return "wat";

		case 3:
			return "hm";

		case 4:
			return "ok";

		case 5:
			return ":o";

		case 6:
			return "lul";

		case 7:
			return "yay free gp";
		}
		return "lol";
	}

	public boolean bankContains(int itemid) {
		if (getBankSlot(itemid) == 500)
			return false;

		return true;
	}

	public int withdrawAll = 53;
	public int withdraw5 = 78;
	public int withdraw1 = 632;
	public int goldLeaf = 8785;
	public int goldBar = 2358;

	public void useButler() {
		Npc[] shop = Npcs.getNearest(4241);
		Npc npc = shop[0];

		if (npc.getLocation().distanceTo() == 0)
			return;

		if (Game.getOpenInterfaceId() != 23350) {
			npc.interact(2);
			sleep(npc.getLocation().distanceTo() * 500);
			return;
		}
	}

	public void closeShop() {
		if (Game.getOpenInterfaceId() == 3824) {
			Mouse.getInstance().click(462, 40, true);
			sleep(400);
			return;
		}
	}

	public boolean shopOpen() {
		if (Game.getOpenInterfaceId() == 3824)
			return true;

		return false;
	}

	public int buy10 = 431;

	public long rTimer = 0;

	public int getSlot(int itemid) {
		if (!hasItem(itemid))
			return 500;

		for (Item i : Inventory.getItems()) {
			if (itemid == i.getId()) {
				return i.getSlot();

			}

		}
		return 500;
	}

	public static boolean usingPrayer() {
		try {
			Class<?> c = Loader.getClient().getClass();
			Field f = c.getDeclaredField("N");
			f.setAccessible(true);
			return (boolean) f.get(Loader.getClient());
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return false;
	}

	public int getSlot(int itemid[]) {
		for (int i = 1; i < itemid.length; i++) {
			if (!hasItem(itemid[i]))
				return 500;
		}

		for (Item it : Inventory.getItems()) {
			if (it != null) {
				return it.getSlot();
			}
		}

		return 500;
	}

	public int nonCoinID() {
		for (Item i : Inventory.getItems()) {
			if (i != null) {
				if (i.getId() != 996)
					return i.getId();
			}
		}

		return 0;
	}

	public int itemID(int exception) {
		for (Item i : Inventory.getItems()) {
			if (i != null) {
				if (i.getId() != exception)
					return i.getId();
			}
		}

		return 0;
	}

	public boolean atHouse() {
		final Player me = Players.getMyPlayer();

		if (me.getLocation().getX() >= 800 && me.getLocation().getX() < 1500) {
			return true;
		}
		return false;
	}

	public boolean atZone() {
		final Player me = Players.getMyPlayer();

		if (me.getLocation().getX() >= 2650 && me.getLocation().getX() <= 2750) {

			if (me.getLocation().getY() >= 3350
					&& me.getLocation().getY() <= 3425) {
				return true;
			}
		}
		return false;
	}

	public void teleZone() {

		if (shopOpen()) {
			closeShop();
			return;
		}

		if (bankOpen()) {
			closeBank();
			return;
		}

		final Player me = Players.getMyPlayer();

		if (me.getAnimation() > 0 && me.getAnimation() != sitAnim) {
			sleep(1000);
			return;
		}

		// System.out.println("int id " + Game.getOpenBackDialogId() + "");

		if (Game.getOpenBackDialogId() == 2492) {
			Mouse.getInstance().click(new Point(265, 400));
			sleep(2000);
			return;
		}
		Mouse.getInstance().click(new Point(745, 185));
		sleep(500);
		Mouse.getInstance().click(new Point(715, 290));// skilling zone
		sleep(750);
		Mouse.getInstance().click(635, 190, true);// backpack
	}

	public void teleCatherby() {

		if (shopOpen()) {
			closeShop();
			return;
		}

		final Player me = Players.getMyPlayer();

		if (me.getAnimation() > 0 && me.getAnimation() != sitAnim) {
			sleep(1000);
			return;
		}

		// System.out.println("int id " + Game.getOpenBackDialogId() + "");

		if (Game.getOpenBackDialogId() == 2492) {
			Mouse.getInstance().click(new Point(265, 450));
			sleep(750);
			Mouse.getInstance().click(new Point(265, 380));
			sleep(1000);
			return;
		}
		Mouse.getInstance().click(new Point(745, 185));
		sleep(500);
		Mouse.getInstance().click(new Point(640, 315));// skilling zone
		sleep(750);
		Mouse.getInstance().click(635, 190, true);// backpack
	}

	public boolean outsideCrafting() {
		final Player me = Players.getMyPlayer();
		if (me.getLocation().getY() >= 3289 && me.getLocation().getX() <= 2934
				&& me.getLocation().getX() >= 2932) {
			return true;
		}

		return false;
	}

	public int sitAnim = 1353;

	public boolean atCrafting() {
		final Player me = Players.getMyPlayer();
		if (me.getLocation().getX() >= 2915 && me.getLocation().getX() <= 2945) {
			return true;
		}

		return false;
	}

	public void enterCrafting() {
		SceneObject[] obj = SceneObjects.getNearest(2647);
		SceneObject door = obj[0];

		door.interact(0);
		sleep(2000);
	}

	public void teleCrafting() {

		if (shopOpen()) {
			closeShop();
			return;
		}

		final Player me = Players.getMyPlayer();

		if (me.getAnimation() > 0 && me.getAnimation() != sitAnim) {
			sleep(1000);
			return;
		}

		// System.out.println("int id " + Game.getOpenBackDialogId() + "");

		if (Game.getOpenBackDialogId() == 2492) {
			Mouse.getInstance().click(new Point(270, 450));
			sleep(750);
			Mouse.getInstance().click(new Point(265, 415));
			sleep(1000);
			return;
		}
		Mouse.getInstance().click(new Point(745, 185));
		sleep(500);
		Mouse.getInstance().click(new Point(715, 290));// skilling zone
		sleep(750);
		Mouse.getInstance().click(635, 190, true);// backpack
	}

	public int construction = 21;
	public int crafting = 12;
	public int prayer = 5;
	public int attack = 0;
	public int magic = 6;
	public int defence = 1;

	public int getLevel(int skill) {
		return Skill.getRealLevel(skill);
	}

	public void enterHouse() {

		if (!atHome() && !atHouse()) {
			teleHome();
			return;
		}

		SceneObject[] obj = SceneObjects.getNearest(15477);
		SceneObject portal = obj[0];

		if (Game.getOpenBackDialogId() == 2492) {
			Menu.sendAction(315, 0, 0, 2496);
			sleep(500);
			return;
		}

		portal.interact(0);
		sleep(2000);
	}

	public void enterHouse(String name) {

		// System.out.println("For sure2");

		if (!atHome() && !atHouse()) {
			teleHome();
			return;
		}

		// System.out.println("For sure1");

		if (Game.getOpenInterfaceId() == 26770)
			return;

		if (Game.getOpenBackDialogId() == 2492) {
			Menu.sendAction(315, 0, 0, 2497);
			sleep(2000);
			Keyboard.getInstance().sendKeys(name);
			sleep(1000);
			return;
		}

		// System.out.println("For sure");

		for (SceneObject s : SceneObjects.getAllSceneObjects()) {
			if (s != null) {
				if (s.getId() == 15477) {
					s.interact(0);
					sleep(2000);
					return;
				}
			}
		}
	}

	public boolean atWarriorsGuild() {
		if (!nearObject(15653))
			return false;

		return true;
	}

	public void teleCombat(int degree) {
		final Player me = Players.getMyPlayer();

		if (!me.isInCombat() && animId() > 0)
			return;

		Menu.sendAction(315, 0, 0, 1167);
		sleep(1000);

		if (Game.getOpenBackDialogId() == 2492) {
			Menu.sendAction(315, 0, 0, 2493 + degree);
			sleep(1000);
		}
	}

	public void teleMinigames(int degree) {
		final Player me = Players.getMyPlayer();

		if (!me.isInCombat() && animId() > 0)
			return;

		Menu.sendAction(315, 0, 0, 1540);
		sleep(1000);

		if (Game.getOpenBackDialogId() == 2492) {
			Menu.sendAction(315, 0, 0, 2493 + degree);
			sleep(1000);
		}
	}

	public void teleCity(int degree, int page) {
		final Player me = Players.getMyPlayer();

		if (!me.isInCombat() && animId() > 0)
			return;

		Menu.sendAction(315, 0, 0, 1174);
		sleep(1000);

		if (Game.getOpenBackDialogId() == 2492) {
			if (page == 2) {
				Menu.sendAction(315, 0, 0, 2498);
				sleep(750);
				Menu.sendAction(315, 0, 0, 2493 + degree);
				return;
			}
			Menu.sendAction(315, 0, 0, 2493 + degree);
			sleep(1000);

		}
	}

	public int hitpoints = 3;

	public boolean atBarrows() {
		final Player me = Players.getMyPlayer();

		if (me.getLocation().getX() > 3500 && me.getLocation().getX() < 3650) {
			if (me.getLocation().getY() > 3200
					&& me.getLocation().getY() < 3350) {
				return true;
			}
		}
		return false;
	}

	public boolean atCrabs() {
		final Player me = Players.getMyPlayer();

		if (me.getLocation().getX() > 2680 && me.getLocation().getX() < 2735) {
			if (me.getLocation().getY() > 3690
					&& me.getLocation().getY() < 3740) {
				return true;
			}
		}
		return false;
	}

	public boolean atTrolls() {
		final Player me = Players.getMyPlayer();

		if (me.getLocation().getX() > 2830 && me.getLocation().getX() < 2895) {
			if (me.getLocation().getY() > 3560
					&& me.getLocation().getY() < 3620) {
				return true;
			}
		}
		return false;
	}

	public boolean inRandom() {

		try {

			if (atIsland()) {
				return true;
			}

			// if (rTimer > 0)
			// return false;

			final Player me = Players.getMyPlayer();

			for (Npc n : Npcs.getNearest(randoms)) {
				if (n != null && n.getLocation().distanceTo() <= 1) {
					if (n.getLocation().getY() == me.getLocation().getY()
							|| n.getLocation().getX() == me.getLocation()
									.getX()) {
						rNPC = n;
						return true;
					}
				}

			}

			/*
			 * for (Npc n : Npcs.getNpcs()) { if (n != null &&
			 * n.getLocation().distanceTo() <= 1 && n.getDef().getId() >= 4000
			 * && !isNormal(n.getDef().getId())) {// 3597 toy mouse if
			 * (n.getLocation().getY() == me.getLocation().getY() ||
			 * n.getLocation().getX() == me.getLocation() .getX()) { rNPC = n;
			 * return true; } } }
			 */

		} catch (Exception e) {

		}

		return false;

	}

	public void depositWorn() {
		if (!bankOpen())
			return;

		Mouse.getInstance().click(new Point(440, 300));
		sleep(350);
	}

	public boolean inWild() {
		// if ()
		if (Players.getMyPlayer().getLocation().getY() >= 3519
				&& getX() >= 2850 && getX() <= 3550)
			return true;

		return false;
	}

	boolean isNormal(int i) {
		for (int n : normalNpcs) {
			if (n == i)
				return true;
		}

		return false;
	}

	Point togglePray = new Point(740, 80);

	public void toggle() {
		Mouse.getInstance().click(togglePray);
		sleep(500);
	}

	public int normalNpcs[] = { 6785, 6817, 6825, 6764, 6763, 6844, 6824, 6843, 6834,
			6845, 6823, 6836, 5116, 6760, 6759, 6757, 6758, 6761, 4251, 1777,
			4246, 4250, 5363, 4248, 6816, 5092, 5089, 5095, 5096, 5099, 6343,
			5080, 5082, 5072, 5113, 5104, 6060, 6054, 6340, 6059, 6053, 6341,
			6058, 6342, 6057, 6062, 6061, 6056, 6055, 6703, 3596, 3598, 6813,
			5590, 49, 6212, 430, 6078, 1107, 115, 2610, 4702, 1, 6703, 2862,
			6308, 2253, 561, 581, 494, 496, 5109, 3299, 1039, 594, 3595, 599,
			1526, 545, 4, 559, 584, 546, 2536, 1699, 522, 694, 2261, 4247,
			2568, 530, 2537, 5590, 3671, 2306, 945, 519, 548, 26, 21, 20, 1599,
			1626, 1648, 1612, 1637, 1616, 1621, 1624, 4241, 6814, 3686, 4250,
			6210, 6243, 6282, 6244, 6212, 6235, 6230, 6703, 6259, 6216, 6238,
			6241, 6236, 6269, 6229, 6246, 6221, 6222, 6240, 6223, 6225, 6227,
			6211, 6214, 6218, 6258, 6233, 6268, 6220, 6237, 6232, 6239, 6267,
			6270, 6276, 6254, 6215, 6217, 6283, 6231, 6274, 6275, 6256, 6219,
			6213, 6278, 6257, 6272, 6273, 6255, 6280, 6277, 6279, 6281, 1,
			2862, 6308, 2253, 561, 581, 494, 496, 6814, 6283, 6261, 6263, 6265,
			6260, 5112, 5074, 5079, 5111, 5085, 5079, 5076, 5100, 5096 };

	public boolean modRandom;

	private int[] randoms = { 410, 3117, 3022, 3351, 409, 18169, 13817 };

	public void login() {
		sleep(30000);

		if (isLoggedIn())
			return;

		/*
		 * if (getFirstLine().contains("Connecting") ||
		 * getFirstLine().contains("connecting")) { if
		 * (!getFirstLine().contains("Error") &&
		 * !getFirstLine().contains("error")) { sleep(1000); return; } }
		 */

		Mouse.getInstance().click(new Point(365, 300), true);
		System.out.println("Logging in...");
		sleep(5000);

	}

	long loginWait = 0;

	public void login(int wait) {
		// if (loginWait == 0)
		// loginWait = System.currentTimeMillis();

		// if (System.currentTimeMillis() - loginWait <= wait)
		// return;
		sleep(wait);

		if (Game.isLoggedIn())
			return;

		/*
		 * if (getFirstLine().contains("Connecting") ||
		 * getFirstLine().contains("connecting")) { if
		 * (!getFirstLine().contains("Error") &&
		 * !getFirstLine().contains("error")) { sleep(1000); return; } }
		 */

		Mouse.getInstance().click(new Point(365, 300), true);
		Mouse.getInstance().click(new Point(265, 315), true);
		System.out.println("Logging in...");
		loginWait = 0;
		sleep(wait);

	}

	public static String getName() {
		try {
			Class<?> c = Loader.getClient().getClass();
			Field f = c.getDeclaredField("hR");
			f.setAccessible(true);
			return (String) f.get(Loader.getClient());
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void setName(String name) {
		try {
			Class<?> c = Loader.getClient().getClass();
			Field f = c.getDeclaredField("hR");
			f.setAccessible(true);
			f.set(Loader.getClient(), name);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public static int destX;
	static long destCheck;

	public static int dest() {
		try {
			Class<?> c = Loader.getClient().getClass();
			Field f = c.getDeclaredField("jc");
			f.setAccessible(true);

			/*
			 * if (destCheck <= 0 || destX <= 0) { destX = (int)
			 * f.get(Loader.getClient()); destCheck =
			 * System.currentTimeMillis(); System.out.println("Dest is 0!"); }
			 * 
			 * if (destX > 0) { if (System.currentTimeMillis() - destCheck >=
			 * 10000) { if (destX == (int) f.get(Loader.getClient())) { destX =
			 * 0; destCheck = 0; System.out.println("Setting everything to 0!");
			 * return 0; }
			 * 
			 * } }
			 */

			return (int) f.get(Loader.getClient());
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static String getFirstLine() {
		try {
			Class<?> c = Loader.getClient().getClass();
			Field f = c.getDeclaredField("jr");
			f.setAccessible(true);
			return (String) f.get(Loader.getClient());
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getSecLine() {
		try {
			Class<?> c = Loader.getClient().getClass();
			Field f = c.getDeclaredField("jr");
			f.setAccessible(true);
			return (String) f.get(Loader.getClient());
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean banned() {
		 if (getFirstLine().contains("anned") ||
		 getSecLine().contains("anned")) {
		 return true;
		 }
		return false;
	}

	public boolean sentBanned;

	public static boolean isLoggedIn() {
		return Game.isLoggedIn();
	}

	public boolean atJail() {
		final Player me = Players.getMyPlayer();
		if (me.getLocation().getY() > 3000 && me.getLocation().getY() < 3040
				&& me.getLocation().getX() > 3265
				&& me.getLocation().getX() < 3300) {
			return true;
		}
		if (nearNpc(jailer))
			return true;

		return false;
	}

	public int jailer = 201;
	public int bronzePick = 1266;
	public int jailRock[] = { 2092, 2093 };
	public int jailOre = 2893;
	public int gp = 996;

	public void handleJail() {

		// if (!getName().contains("Sing")) {
		// System.out.println("Gtfo in jail");
		// System.exit(0);
		// }

		final Player me = Players.getMyPlayer();
		final Npc[] npc = Npcs.getNearest(jailer);
		Npc n = npc[0];

		/*
		 * for (Item i : Inventory.getItems()) { if (i.getSlot() == 1 ||
		 * i.getSlot() == 2) { if (i.getId() != jailOre && i.getId() != gp &&
		 * i.getId() != bronzePick) { Menu.sendAction(847, i.getId() - 1,
		 * i.getSlot(), 3214); sleep(1000); return; } }
		 * 
		 * }
		 */

		if (Inventory.getCount(bronzePick) == 0 && !isWearing(bronzePick)
				&& !hasItem(1276) && !isWearing(1276) || Inventory.isFull()) {
			n.interact(0);
			sleep(1000);
			return;
		}

		if (Inventory.isFull() == false) {
			SceneObject[] obj = SceneObjects.getNearest(jailRock);
			SceneObject rock = obj[0];

			if (animId() > 0)
				return;

			rock.interact(0);
			sleep(1500);

			return;
		}

	}

	public void teleHome() {

		if (bankOpen()) {
			closeBank();
			return;
		}

		if (shopOpen()) {
			closeShop();
			return;
		}

		final Player me = Players.getMyPlayer();

		if (me.getAnimation() > 6000 && me.getAnimation() != sitAnim) {
			Time.sleep(3000);
			return;
		}

		Mouse.getInstance().click(new Point(745, 185));// tab
		Time.sleep(200);
		Mouse.getInstance().click(570, 240, true);
		Time.sleep(1000);
		Mouse.getInstance().click(635, 190, true);// backpack

	}

	public int randNumber(int min, int max) {
		Random rand = new Random();
		int n = rand.nextInt(max) + min;
		System.out.println(n);
		return n;
	}

	public void randSleep(int minSleep, int maxSleep) {
		Random rand = new Random();
		int n = rand.nextInt(maxSleep) + minSleep;
		Time.sleep(n);
	}

	public void teleFarming() {

		if (shopOpen()) {
			closeShop();
			return;
		}

		final Player me = Players.getMyPlayer();

		if (me.getAnimation() > 6000 && me.getAnimation() != sitAnim) {
			Time.sleep(3000);
			return;
		}
		Menu.sendAction(315, 0, 0, 1170);
		sleep(1000);
		Menu.sendAction(315, 0, 0, 2498);
		sleep(1000);
		Menu.sendAction(315, 0, 0, 2495);
		sleep(1000);

	}

	public void teleCrabs() {

		if (shopOpen()) {
			closeShop();
			return;
		}

		final Player me = Players.getMyPlayer();

		if (me.getAnimation() > 6000 && me.getAnimation() != sitAnim) {
			return;
		}

		if (Game.getOpenBackDialogId() == 2492) {
			Mouse.getInstance().click(265, 415, true);
			sleep(1000);
			return;
		}

		Mouse.getInstance().click(new Point(745, 185));// tab
		Time.sleep(200);
		Mouse.getInstance().click(640, 290, true);
		Time.sleep(1000);
		Mouse.getInstance().click(635, 190, true);// backpack

	}

	public void teleTrolls() {

		if (shopOpen()) {
			closeShop();
			return;
		}

		final Player me = Players.getMyPlayer();

		if (me.getAnimation() > 6000 && me.getAnimation() != sitAnim) {
			return;
		}

		if (Game.getOpenBackDialogId() == 2492) {
			Menu.sendAction(315, -1, 504, 2497);
			sleep(1000);
			return;
		}

		Mouse.getInstance().click(new Point(745, 185));// tab
		Time.sleep(200);
		Mouse.getInstance().click(640, 290, true);
		Time.sleep(1000);
		Mouse.getInstance().click(635, 190, true);// backpack

	}

	public void enterPortal() {
		SceneObject[] obj = SceneObjects.getNearest(15477);
		SceneObject portal = obj[0];

		if (Game.getOpenBackDialogId() == 2492) {
			Mouse.getInstance().click(new Point(270, 400));
			sleep(500);
			return;
		}

		portal.interact(0);
		sleep(2000);
	}

	public void sleep(int time) {
		Time.sleep(time);
	}

	public int buy1 = 78;
	public int buy100 = 53;
	public int buy5 = 867;
	public int buyX = 431;

	public void buyItem(int amt, int ID) {

		if (!shopContains(ID)) {
			System.out.println("No item");
			return;
		}

		if (amt == buyX) {
			Menu.sendAction(buyX, ID - 1, getShopSlot(ID), 3900);
			sleep(750);
			return;
		}

		if (amt > 100) {
			Menu.sendAction(buy100, ID - 1, getShopSlot(ID), 3900);
			sleep(750);
			return;
		}

		// System.out.println(amt);

		switch (amt) {

		case 1:
			Menu.sendAction(buy1, ID - 1, getShopSlot(ID), 3900);
			sleep(750);
			break;

		case 5:
			Menu.sendAction(buy5, ID - 1, getShopSlot(ID), 3900);
			sleep(750);
			break;

		case 10:
			Menu.sendAction(buy10, ID - 1, getShopSlot(ID), 3900);
			sleep(750);
			break;

		case 100:
			Menu.sendAction(buy100, ID - 1, getShopSlot(ID), 3900);
			sleep(750);
			break;

		default:
			Menu.sendAction(432, ID - 1, getShopSlot(ID), 3900);
			sleep(1500);
			Keyboard.getInstance().sendKeys(String.valueOf(amt));
			sleep(750);
			break;
		}

	}

	public boolean atCatherby() {
		final Player me = Players.getMyPlayer();

		if (me.getLocation().getX() > 2760 && me.getLocation().getX() < 2830) {
			if (me.getLocation().getY() > 3400)
				return true;
		}
		return false;
	}

	public boolean atHome() {
		final Player me = Players.getMyPlayer();

		if (me.getLocation().getX() > 3200 && me.getLocation().getX() < 3240) {
			return true;
		}
		return false;
	}

	public boolean atSeers() {
		final Player me = Players.getMyPlayer();

		if (me.getLocation().getX() > 2700 && me.getLocation().getX() < 2740
				&& me.getLocation().getY() > 3450) {
			return true;
		}
		return false;
	}

	public int depositAll = 432;

	public int getBankSlot(int id) {

		int[] bankIds = Loader.getClient().getInterfaceCache()[5382].getItems();

		for (int i = 0; i < bankIds.length; i++) {
			if (bankIds[i] == id) {
				return i;
			}
		}
		return 500;
	}

	public int getBankStack(int id) {

		int[] bankIds = Loader.getClient().getInterfaceCache()[5382].getItems();

		for (int i = 0; i < bankIds.length; i++) {
			if (bankIds[i] == id) {
				return i;
			}
		}
		return 500;
	}

	public int getShopSlot(int id) {

		int[] bankIds = Loader.getClient().getInterfaceCache()[3900].getItems();

		for (int i = 0; i < bankIds.length; i++) {
			if (bankIds[i] == id) {
				return i;
			}
		}
		return 500;
	}

	public boolean isWearing(int id) {
		if (getWearingSlot(id) == 500)
			return false;

		return true;
	}

	public int getWearingSlot(int id) {

		int[] bankIds = Loader.getClient().getInterfaceCache()[1688].getItems();

		for (int i = 0; i < bankIds.length; i++) {
			if (bankIds[i] == id) {
				return i;
			}
		}
		return 500;
	}

	public int strength = 2;

	public boolean shopContains(int id) {
		if (getShopSlot(id) == 500)
			return false;

		return true;
	}

	public boolean nearJail() throws UnknownHostException {
		InetAddress IP = InetAddress.getLocalHost();

		if (IP.getHostName().contains("Blake")
				|| IP.getHostName().contains("Jacob")
				|| IP.getHostName().contains("Brady")) {
			return false;
		}

		return true;
	}

	public boolean atLumb() {
		final Player me = Players.getMyPlayer();

		// if (atHome())
		// return false;
		if (me.getLocation().getX() > 3200 && me.getLocation().getX() < 3280
				&& me.getLocation().getY() > 3100
				&& me.getLocation().getY() < 3270) {
			return true;
		}

		// System.out.println("Not at lumby.");
		return false;
	}

	public int getPrayer(int id) {

		int[] bankIds = Loader.getClient().getInterfaceCache()[25032]
				.getItems();

		for (int i = 0; i < bankIds.length; i++) {
			System.out.println(bankIds[i]);
			if (bankIds[i] == id) {
				return i;
			}
		}
		return 500;
	}

	public String getText(int id) {
		try {
			Field text = Loader.getClient().getInterfaceCache()[id].getClass()
					.getDeclaredField("R");
			text.setAccessible(true);

			return (String) text
					.get(Loader.getClient().getInterfaceCache()[id]);

		} catch (NoSuchFieldException | IllegalAccessException e) {
			return null;
		}
	}

	public boolean running = true;
	public long runOff = 0;

	public void toggleRun() {
		if (runOn())
			return;

		Mouse.getInstance().click(new Point(750, 115));
		sleep(500);
		System.out.println("toggling run...");
	}

	public int goldOre = 445;
	public long lastAnim = 0;
	public int fletching = 9;

	public void teleLumb() {

		if (bankOpen()) {
			closeBank();
			return;
		}

		if (shopOpen()) {
			closeShop();
			return;
		}

		final Player me = Players.getMyPlayer();

		if (me.getAnimation() > 6000 && me.getAnimation() != sitAnim) {
			return;
		}

		Menu.sendAction(315, 0, 0, 1174);
		sleep(1000);
		Menu.sendAction(315, 0, 0, 2494);
		sleep(1000);

	}

	public void interactObj(int id, int x, int y) {
		for (SceneObject s : SceneObjects.getNearest()) {
			if (s != null) {
				if (s.getId() == id && s.getLocation().getX() == x
						&& s.getLocation().getY() == y) {
					s.interact(0);
					sleep(1500);
					return;
				}
			}
		}
	}

	public void closeBank() {
		if (bankOpen()) {
			Mouse.getInstance().click(486, 27, true);
			sleep(400);
			return;
		}
	}

	public String extractDigits(String src) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < src.length(); i++) {
			char c = src.charAt(i);
			if (Character.isDigit(c)) {
				builder.append(c);
			}
		}
		return builder.toString();
	}

	public int atkLvl() {
		String text = getText(4004);

		// if (text == null)

		return Integer.valueOf(extractDigits(text));
	}

	public boolean onMorph() {
		String text = getText(4004);

		if (text.contains("@"))
			return true;

		return false;
	}

	public void switchWorld() {
		if (animId() > 8900)
			return;

		// if (atkLvl() == 0) {
		Menu.sendAction(315, 0, 0, 18470);
		sleep(1000);
		// } else {

		// }

	}

	public void teleHunter() {

		if (shopOpen()) {
			closeShop();
			return;
		}

		if (bankOpen()) {
			closeBank();
			return;
		}

		final Player me = Players.getMyPlayer();

		if (me.getAnimation() > 6000 && me.getAnimation() != sitAnim) {
			return;
		}

		if (Game.getOpenBackDialogId() == 2492) {
			Mouse.getInstance().click(270, 450, true);
			sleep(750);
			Mouse.getInstance().click(270, 450, true);
			sleep(750);
			Menu.sendAction(315, -1, 420, 2495);
			sleep(1500);
			return;
		}

		Mouse.getInstance().click(new Point(745, 185));// tab
		sleep(200);
		Mouse.getInstance().click(720, 285, true);
		sleep(750);
		Mouse.getInstance().click(635, 190, true);// backpack

	}

	public void teleJungle() {

		if (shopOpen()) {
			closeShop();
			return;
		}

		final Player me = Players.getMyPlayer();

		if (me.getAnimation() > 6000 && me.getAnimation() != sitAnim) {
			return;
		}

		if (Game.getOpenBackDialogId() <= 0) {
			interactN(5112, 0);
			return;
		}

		Mouse.getInstance().click(new Point(270, 400));
		sleep(500);
		Mouse.getInstance().click(new Point(270, 400));
		sleep(500);

	}

	public void teleWoods() {

		if (shopOpen()) {
			closeShop();
			return;
		}

		if (!atSnow() && !atWoods()) {
			teleHunter();
			return;
		}

		final Player me = Players.getMyPlayer();

		if (me.getAnimation() > 6000 && me.getAnimation() != sitAnim) {
			return;
		}

		if (Game.getOpenBackDialogId() <= 0) {
			interactN(5112, 0);
			return;
		}

		Mouse.getInstance().click(new Point(270, 400));
		sleep(500);
		Menu.sendAction(315, -1, 259, 2496);
		sleep(500);

	}

	public boolean atSnow() {
		final Player me = Players.getMyPlayer();

		if (me.getLocation().getX() >= 2670 && me.getLocation().getX() <= 2730) {

			if (me.getLocation().getY() >= 3790
					&& me.getLocation().getX() <= 3850) {
				return true;
			}
		}
		return false;
	}

	public boolean atJungle() {
		final Player me = Players.getMyPlayer();

		if (me.getLocation().getX() >= 2480 && me.getLocation().getX() <= 2600) {

			if (me.getLocation().getY() >= 2850
					&& me.getLocation().getX() <= 2950) {
				return true;
			}
		}
		return false;
	}

	public boolean atWoods() {
		final Player me = Players.getMyPlayer();

		if (me.getLocation().getX() >= 2290 && me.getLocation().getX() <= 2380) {

			if (me.getLocation().getY() >= 3530
					&& me.getLocation().getX() <= 3620) {
				return true;
			}
		}
		return false;
	}

	public void teleSeers() {

		if (shopOpen()) {
			closeShop();
			return;
		}

		final Player me = Players.getMyPlayer();

		if (me.getAnimation() > 6000 && me.getAnimation() != sitAnim) {
			sleep(3000);
			return;
		}

		if (Game.getOpenBackDialogId() == 2492) {
			Mouse.getInstance().click(260, 400, true);
			sleep(1000);
			return;
		}

		Menu.sendAction(315, 0, 0, 1174);
		sleep(1000);
		Menu.sendAction(315, 0, 0, 2495);
		sleep(1000);

	}

	public boolean atCorp() {
		final Player me = Players.getMyPlayer();

		if (me.getLocation().getX() > 3300 && me.getLocation().getX() < 3400) {
			if (me.getLocation().getY() > 9300
					&& me.getLocation().getY() < 9500) {
				return true;
			}
		}
		return false;
	}

	public int runEnergy() {

		return Integer.valueOf(extractDigits(getText(149)));
		// TODO Auto-generated method stub
		// return -1;
	}

	public boolean atRelleka() {
		final Player me = Players.getMyPlayer();

		if (me.getLocation().getX() > 2550 && me.getLocation().getX() < 2750) {
			if (me.getLocation().getY() > 3550
					&& me.getLocation().getY() < 3750) {
				return true;
			}
		}
		return false;
	}

	public boolean atFalador() {
		final Player me = Players.getMyPlayer();

		if (me.getLocation().getX() > 2900 && me.getLocation().getX() < 3100) {
			if (me.getLocation().getY() > 3300
					&& me.getLocation().getY() < 3500) {
				return true;
			}
		}
		return false;
	}

	public boolean atVarrock() {
		final Player me = Players.getMyPlayer();

		if (me.getLocation().getX() > 3050 && me.getLocation().getX() < 3300) {
			if (me.getLocation().getY() > 3350
					&& me.getLocation().getY() < 3500) {
				return true;
			}
		}
		return false;
	}

	public boolean atYanille() {
		final Player me = Players.getMyPlayer();

		if (me.getLocation().getX() > 2450 && me.getLocation().getX() < 2700) {
			if (me.getLocation().getY() > 3000
					&& me.getLocation().getY() < 3300) {
				return true;
			}
		}
		return false;
	}
}