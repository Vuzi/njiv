package fr.njiv.UI;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

public class KeyActionListener implements KeyListener {
	
	// The binds between actions and KeyActions
	HashMap<String, KeyAction> binds = new HashMap<String, KeyAction>();
	Component c;

	/**
	 * The constructor of the KeyActions
	 * @param actions The map of key/action
	 * @param toexec The map of action (string) and corresponding interfaces
	 */
	public KeyActionListener(Component c, Map<String, String> actions, Map<String, KeyAction> toexec) {
		try {
			for(Map.Entry<String, String> entry: actions.entrySet()) {
				// If the action is listed
				if(toexec.containsKey(entry.getValue())) {
					// Add the action to the list
					binds.put(entry.getKey(), toexec.get(entry.getValue()));
				} else {
					System.out.println("[x] Error : '"+entry.getValue()+"' is not a registered action");
				}
			}
		} catch(Exception e) {
			System.out.println("[x] An error occured during the analyse of the key binds : "+e.getLocalizedMessage());
		}
		
		this.c = c;
	}

	/**
	 * This is basically a big switch on all the keys we handle, and the call of the
	 * KeyAction it is refearing to (if any)
	 */
	public void keyPressed(KeyEvent e) {
		
		StringBuilder action_key = new StringBuilder();

		switch(e.getKeyCode()) {
		case KeyEvent.VK_SHIFT:
		case KeyEvent.VK_CONTROL:
		case KeyEvent.VK_ALT:
			return;
		case KeyEvent.VK_PLUS:
			action_key.append("+");
			break;
		case KeyEvent.VK_MINUS:
			action_key.append("-");
			break;
		case KeyEvent.VK_LEFT:
			action_key.append("left");
			break;
		case KeyEvent.VK_RIGHT:
			action_key.append("right");
			break;
		case KeyEvent.VK_UP:
			action_key.append("up");
			break;
		case KeyEvent.VK_DOWN:
			action_key.append( "down");
			break;
		case KeyEvent.VK_ENTER:
			action_key.append("enter");
			break;
		case KeyEvent.VK_BACK_SPACE:
			action_key.append("back_space");
			break;
		case KeyEvent.VK_DELETE:
			action_key.append("delete");
			break;
		case KeyEvent.VK_SPACE:
			action_key.append("space");
			break;
		case KeyEvent.VK_F1:
			action_key.append("f1");
			break;
		case KeyEvent.VK_F2:
			action_key.append("f2");
			break;
		case KeyEvent.VK_F3:
			action_key.append("f3");
			break;
		case KeyEvent.VK_F4:
			action_key.append("f4");
			break;
		case KeyEvent.VK_F5:
			action_key.append("f5");
			break;
		case KeyEvent.VK_F6:
			action_key.append("f6");
			break;
		case KeyEvent.VK_F7:
			action_key.append("f7");
			break;
		case KeyEvent.VK_F8:
			action_key.append("f8");
			break;
		case KeyEvent.VK_F9:
			action_key.append("f9");
			break;
		case KeyEvent.VK_F10:
			action_key.append("f10");
			break;
		case KeyEvent.VK_F11:
			action_key.append("f11");
			break;
		case KeyEvent.VK_F12:
			action_key.append("f12");
			break;
		case KeyEvent.VK_ESCAPE:
			action_key.append("escape");
			break;
		}
		
		// If no key, try to convert it to char
		if(action_key.length() == 0) {
			if(e.getKeyCode() >= KeyEvent.VK_A && e.getKeyCode() <= KeyEvent.VK_Z)
				action_key.append(Character.toChars((e.getKeyCode() - KeyEvent.VK_A)+(int)('a')));
			else
				action_key.append(e.getKeyChar());
		}
		
		// Handle modifier
		if(e.isShiftDown()) 
			action_key.insert(0, "shift+");
		if(e.isAltDown())
			action_key.insert(0, "alt+");
		if(e.isControlDown())
			action_key.insert(0, "ctrl+");
		
		// Do the action
		KeyAction action = binds.get(action_key.toString());
		
		if(action != null)
			action.todo();
		else
			System.out.println("[w] Key unused '"+action_key+"'");
	}

	/**
	 * Unused
	 */
	public void keyReleased(KeyEvent e) {}
	public void keyTyped(KeyEvent arg0) {}
}
