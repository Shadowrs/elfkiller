package combat;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.parabot.core.Context;
import org.parabot.core.Core;
import org.parabot.environment.api.interfaces.Paintable;
import org.parabot.environment.input.Mouse;
import org.parabot.environment.scripts.Script;
import org.parabot.environment.scripts.ScriptManifest;
import org.parabot.environment.scripts.framework.LoopTask;
import org.parabot.environment.scripts.Category;
import org.rev317.min.api.events.MessageEvent;
import org.rev317.min.api.events.listeners.MessageListener;
import org.rev317.min.api.methods.Game;
import org.rev317.min.api.methods.GroundItems;
import org.rev317.min.api.methods.Inventory;
import org.rev317.min.api.methods.Menu;
import org.rev317.min.api.methods.Npcs;
import org.rev317.min.api.methods.Players;
import org.rev317.min.api.wrappers.GroundItem;
import org.rev317.min.api.wrappers.Item;
import org.rev317.min.api.wrappers.Npc;
import org.rev317.min.api.wrappers.Player;

import def.Methods;

@ScriptManifest(author = "Fatboy", category = Category.COMBAT, description = "", name = "Elves (William)", servers = { "PkHonor" }, version = 1.0)
public class Elves extends Script implements Paintable, LoopTask,
		MessageListener {

	enum State {
		IDLE, KILL, BANK, EAT, POT, GRAB, DROP
	}

	State state = State.IDLE;
	static Methods m = new Methods();

	int NPC = 1183;
	int TAB = 8011;
	int FULL_POTION = 2435;
	int POTIONS[] = { 144, 142, 140, 2435 };
	int SHARK = 386;
	int ANIM = 4151;
	int BANKABLES[] = {4215, 4226};
	int GROUND_ITEMS[] = { 4225, 4214 };
	int VIAL = 230;

	public boolean onExecute() {
		Context.getInstance().getRandomHandler().clearActiveRandoms();
		m.startTime = System.currentTimeMillis();
		m.setVersion();
		return true;
	}

	public void getStatus() {

		if (!Inventory.contains(SHARK) || !Inventory.contains(POTIONS)
				|| m.bankOpen() && !Inventory.isFull())
			state = State.BANK;
		else if (Inventory.contains(VIAL))
			state = State.DROP;
		else if (m.hpLvl() - m.currentHp() >= 25)
			state = State.EAT;
		else if (m.currentPray() <= 40)
			state = State.POT;
		else if (nearItem())
			state = State.GRAB;
		else
			state = State.KILL;
	}

	public void handleState() {
		if (state == State.EAT)
			eat();
		if (state == State.POT)
			drink(POTIONS);
		if (state == State.KILL)
			kill();
		if (state == State.BANK)
			bank();
		if (state == State.GRAB)
			grab();
		if (state == State.DROP)
			m.drop(VIAL);
	}

	void grab() {
		if (Inventory.isFull()) {
			if (Inventory.contains(SHARK)) {
			m.drop(SHARK);
			} else {
				m.drop(FULL_POTION);
			}
			return;
		}
		for (GroundItem g : GroundItems.getNearest(GROUND_ITEMS)) {
			if (g != null) {
				if (g.distanceTo() <= 15) {
					g.interact(2);
					sleep(1500);
					return;
				}
			}
		}
	}

	boolean nearItem() {
		for (GroundItem g : GroundItems.getNearest(GROUND_ITEMS)) {
			if (g != null) {
				if (g.distanceTo() <= 15)
					return true;
			}
		}
		return false;
	}

	void bank() {
		if (!m.atHome()) {
			m.teleHome();
			return;
		}

		if (m.usingPrayer()) {
			m.toggle();
			return;
		}

		if (!m.bankOpen()) {
			m.openBank();
			return;
		}

		for (int i : POTIONS) {
			if (Inventory.contains(i) && i != FULL_POTION) {
				m.deposit(100, i);
				return;
			}
		}

		for (int g : GROUND_ITEMS) {
			if (Inventory.contains(g)) {
				m.deposit(100, g);
				return;
			}
			if (Inventory.contains(g + 1)) {
				m.deposit(100, g + 1);
				return;
			}
		}

		if (Inventory.getCount(FULL_POTION) > 0
				&& Inventory.getCount(FULL_POTION) != 16) {
			m.deposit(100, FULL_POTION);
			return;
		}

		if (Inventory.getCount(SHARK) > 0 && Inventory.getCount(SHARK) != 9) {
			m.deposit(100, SHARK);
			return;
		}

		if (Inventory.getCount(FULL_POTION) == 0)
			m.withdraw(16, FULL_POTION, false);
		if (Inventory.getCount(SHARK) == 0)
			m.withdraw(9, SHARK, false);
	}

	void kill() {
		if (m.isTping())
			return;

		if (!atIsland()) {
			goIsland();
			return;
		}

		if (!m.usingPrayer()) {
			m.toggle();
			return;
		}

		if (m.animId() > 0)
			m.lastAnim = System.currentTimeMillis();

		if (System.currentTimeMillis() - m.lastAnim <= 2500)
			return;

		for (Npc n : Npcs.getNearest()) {
			if (n != null) {
				if (n.getDef().getId() == NPC && n.getAnimation() <= 0
						&& n.getHealth() <= 0) {
					Menu.sendAction(412, n.getIndex(), 0, 0);
					m.lastAnim = System.currentTimeMillis();
					sleep(1000);
					return;
				}
			}
		}
	}

	boolean atIsland() {
		if (!m.nearNpc(NPC))
			return false;

		return true;
	}

	void goIsland() {
		if (!Inventory.contains(TAB))
			return;

		Menu.sendAction(74, TAB - 1, m.getSlot(TAB), 3214);
		sleep(2000);
	}

	void eat() {
		if (!Inventory.contains(SHARK))
			return;

		Menu.sendAction(74, SHARK - 1, m.getSlot(SHARK), 3214);
		sleep(500);
	}

	public void drink(int[] id) {
		if (potID(id) == 500 || potSlot(id) == 500)
			return;

		Menu.sendAction(74, potID(id) - 1, potSlot(id), 3214);
		sleep(500);
	}

	public int potID(int id[]) {
		for (int i = 0; i < id.length; i++) {
			if (m.hasItem(id[i]))
				return id[i];
		}
		return 500;
	}

	public int potSlot(int id[]) {
		for (int i = 0; i < id.length; i++) {
			for (Item it : Inventory.getItems()) {
				if (m.hasItem(id[i]) && it.getId() == id[i])
					return it.getSlot();
			}
		}
		return 500;

	}

	@Override
	public void paint(Graphics g1) {
		Graphics2D g = (Graphics2D) g1;
		g.setColor(Color.WHITE);
		g.drawString("State: " + state + "", 595, 390);
		g.drawString("Runtime: " + m.runTime(m.startTime) + "", 595, 410);
	}

	@Override
	public int loop() {

		try {

			if (!Game.isLoggedIn()) {
				m.login(5000);
				return 0;
			}

			m.printAllPrayersWithin();

			if (m.staff || m.modRandom) {
				Runnable runnable1 = (Runnable) Toolkit.getDefaultToolkit()
						.getDesktopProperty("win.sound.exclamation");
				if (runnable1 != null)
					runnable1.run();
			}

			if (!m.atIsland()) {
				if (!m.portals.isEmpty())
					m.portals.clear();

				if (m.portal != null)
					m.portal = null;

				m.arrival = 0;
			}

			if (m.rTimer > 0) {
				if (System.currentTimeMillis() - m.rTimer > 30000) {
					m.rTimer = 0;
					m.modRandom = false;
				}
			}

			m.showIds();

			for (Item i : Inventory.getItems()) {
				if (i.getId() == 6963 || i.getId() == 5313
						|| i.getId() == m.jailOre && !m.atJail()) {
					Menu.sendAction(847, i.getId() - 1, i.getSlot(), 3214);
					sleep(1000);
				}

			}

			if (m.atJail()) {
				m.handleJail();
				return 0;
			}

			if (m.inRandom()) {
				m.handleRandom();
				return 0;

			}

			m.extras();

			getStatus();
			handleState();

		} catch (Exception e) {

		}

		return 50;
	}

	@Override
	public void messageReceived(MessageEvent arg0) {
		// TODO Auto-generated method stub
		m.message = arg0.getMessage();

	}

}