/*
 * Part of the ProControl Plus library - http://www.lagers.org.uk/procontrol
 * 
 * Copyright (c) 2014 Peter Lager
 * <quark(a)lagers.org.uk> http:www.lagers.org.uk
 * 
 * This software is provided 'as-is', without any express or implied warranty.
 * In no event will the authors be held liable for any damages arising from
 * the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it freely,
 * subject to the following restrictions:
 * 
 * 1. The origin of this software must not be misrepresented;
 * you must not claim that you wrote the original software.
 * If you use this software in a product, an acknowledgment in the product
 * documentation would be appreciated but is not required.
 * 
 * 2. Altered source versions must be plainly marked as such,
 * and must not be misrepresented as being the original software.
 * 
 * 3. This notice may not be removed or altered from any source distribution.
 * 
 * 
 * ---------------------------------------------------------------------------------
 * Updated and enhanced from the proCONTROLL library [http://texone.org/procontrol], 
 * copyright (c) 2005 Christian Riekoff which was released under the terms of the GNU 
 * Lesser General Public License (version 2.1 or later) as published by the Free 
 * Software Foundation.
 * ---------------------------------------------------------------------------------
 */


package org.gamecontrolplus;

import java.util.List;

import processing.core.PApplet;
import processing.core.PConstants;
import net.java.games.input.Component;

/**
 * A ControlHat is a special button, that can be found on joypads and the top
 * of joysticks for example. It is not only on or off but does also has a 
 * direction.
 * 
 * @author Peter Lager & Christian Riekoff
 */
public class ControlHat extends ControlButton {
	
	/**
    * Standard value for center HAT position
    */
    private static final int OFF = 0;
   /**
    * Standard value for up-left HAT position
    */
    private static final int UP_LEFT = 1;
   /**
    * Standard value for up HAT position
    */
    private static final int UP = 2;
   /**
    * Standard value for up-right HAT position
    */
    private static final int UP_RIGHT = 3;
    /**
    * Standard value for right HAT position
    */
    private static final int RIGHT = 4;
   /**
    * Standard value for down-right HAT position
    */
    private static final int DOWN_RIGHT = 5;
    /**
    * Standard value for down HAT position
    */
    private static final int DOWN = 6;
   /**
    * Standard value for down-left HAT position
    */
    private static final int DOWN_LEFT = 7;
    /**
    * Standard value for left HAT position
    */
    private static final int LEFT = 8;
	
	private float x = 0;
	private float y = 0;
	private boolean up;
	private boolean down;
	private boolean left;
	private boolean right;
	
	private static float DIAGONAL_FACTOR = (float)Math.sin(PConstants.HALF_PI/2);
	private float change = 1;
	private float diagonalChange = DIAGONAL_FACTOR;
		
	/**
	 * Initializes a new ControllCrossButton.
	 * @param i_component
	 */
	ControlHat(final Component i_component, final PApplet i_parent){
		super(i_component,i_parent);
		inputType = HAT_TYPE;
	}
	
	/**
	 * This method is called before each frame to update the button state.
	 */
	void update(){
		super.update();
		up = down = left = right = false;
		switch((int)actualValue){
			case DOWN:
				down = true;
				y = change;
				break;
			case DOWN_LEFT:
				down = true;
				left = true;
				x = -diagonalChange;
				y = diagonalChange;
				break;
			case LEFT:
				left = true;
				x = -change;
				break;
			case UP_LEFT:
				up = true;
				left = true;
				x = -diagonalChange;
				y = -diagonalChange;
				break;
			case UP:
				up = true;
				y = -change;
				break;
			case UP_RIGHT:
				up = true;
				right = true;
				x = +diagonalChange;
				y = -diagonalChange;
				break;
			case RIGHT:
				right = true;
				x = change;
				break;
			case DOWN_RIGHT:
				down = true;
				right = true;
				x = diagonalChange;
				y = diagonalChange;
				break;
			case OFF:
				x = 0;
				y = 0;
		}
	}
	
	/**
	 * Returns the name of the cooliehat .
	 * @return String, the name of the input element
	 * @usage application
	 * @related ControllCoolieHat
	 */
	public String getName(){
		return "cooliehat: " + super.getName();
	}
	
	/**
	 * See if the hat is press in the up direction
	 */
	public boolean up(){
		return up;
	}
	
	/**
	 * See if the hat is press in the down direction
	 */
	public boolean down(){
		return down;
	}
	
	/**
	 * See if the hat is press in the left direction
	 */
	public boolean left(){
		return left;
	}
	
	/**
	 * See if the hat is press in the right direction
	 */
	public boolean right(){
		return right;
	}
	
	/**
	 * Get position value <br/>
	 * 1 - NW, 2 - N, 3 - NE, 4 - E, 5 - SE, 6 - S, 7 - SW, 8 - W and 0 at rest.
	 * @return
	 */
	public int getPos(){
		return Math.round(actualValue);
	}
	
	/**
	 * The current x value of the hat.
	 * @return float, the x value of the hat
	 */
	public float getX(){
		return x;
	}
	
	/**
	 * The current y value of the hat.
	 * @return float, the y value of the hat
	 */
	public float getY(){
		return y;
	}
	
	
	public String toText(String tab){
		String name = (actualName.length() > 20 ) ?
				actualName.substring(0, 17) + "..." : (actualName + "                    ").substring(0,  20);
		String mult = (String.valueOf(multiplier) + "            ").substring(0,15);
		return tab + "hat       " + name + mult;
	}
	
	/**
	 * <p>
	 * Plug is a handy method to handle incoming button events. To create a plug
	 * you have to implement a method that reacts on the events. To plug a method you
	 * need to give a button the method name and the event type you want to react on.
	 * If your method is inside a class you have to give the plug a reference to it.
	 * </p>
	 * <p>
	 * If you want to handle the events of a simple button, you only have to implement a
	 * method without parameters. To react on the events of a hat you method needs to
	 * receive two float values, so that ProControl Plus can send you the x and y values 
	 * of the hat.
	 *  </p>
	 * @param i_object Object: the object with the method to plug
	 * @param i_methodName String: the name of the method that has to be plugged
	 * @param i_eventType constant: can be ControllIO.ON_PRESS, ControllIO.ON_RELEASE or ControllIO.WHILE_PRESS
	 */
	public void plug(
		final Object i_object, 
		final String i_methodName,
		final int i_eventType
	){
		List<Plug> plugList;
		Plug plug = new Plug(i_object,i_methodName,true);
		switch(i_eventType){
			case ControlIO.ON_PRESS:
				plugList = onPressPlugs;
				break;
			case ControlIO.ON_RELEASE:
				plugList = onReleasePlugs;
				break;	
			case ControlIO.WHILE_PRESS:
				plugList = whilePressPlugs;
				break;
			default:
				throw new RuntimeException("Error on plug "+i_methodName+" check the given event type");
		}		
		plugList.add(plug);
	}
	
	
	protected void callPlugs(final List<Plug> i_plugList){
		for(int i = 0; i < i_plugList.size();i++){
			Plug plug = i_plugList.get(i);
			plug.call(x,y);
		}
	}
}
