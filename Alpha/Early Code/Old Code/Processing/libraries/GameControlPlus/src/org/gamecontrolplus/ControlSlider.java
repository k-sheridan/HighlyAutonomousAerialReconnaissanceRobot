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

import net.java.games.input.Component;
import processing.core.PApplet;

/**
 * The slider class is for analog input elements having a value
 * range. Normally this range goes from -1 to 1. You can set
 * a multiplier to increase this range, this is usefull so
 * that you do not have to change the values in your application.
 * You can get the actual value and the total value of a slider.
 * The actual value gives you the current state of the controller.
 * For the total value the actual values for each frame are add.
 * If you not want a slider to react upto a certain value you can set 
 * a tolerance value.
 * 
 * @author Christian Riekoff minor mods by Peter Lager
 */
public class ControlSlider extends ControlInput{


	/**
	 * The total Value of the slider
	 */
	protected float totalValue = 0f;

	/**
	 * Initializes a new Slider.
	 * @param i_component
	 */
	ControlSlider(final Component i_component){
		super(i_component);
		inputType = SLIDER_TYPE;
	}

	/**
	 * For the total value the values for each frame are add.
	 * Use this method to get the total value of a slider.
	 * @return float, the total value of a slider
	 */
	public float getTotalValue(){
		return totalValue;
	}

	/**
	 * For the total value the actual values for each frame are add.
	 * Use this method to set the totalvalue to 0.
	 */
	public void reset(){
		totalValue = 0;
	}

	/**
	 * Use this method to see if a slider is relative. A relative sliders
	 * value represents always the change between the current state and the last state.
	 * @return boolean, true if the slider is relative
	 */
	public boolean isRelative(){
		return component.isRelative();
	}

	/**
	 * This method is called before each frame to update the slider values.
	 */
	void update(){
		actualValue = component.getPollData();
		if(PApplet.abs(actualValue) < component.getDeadZone()+tolerance){
			actualValue = 0f;
		}else{
			actualValue = component.getPollData()*multiplier;
		}
		totalValue += actualValue;
	}

	void updateRelative(){	}

	/**
	 * Get a text description of this slider
	 * @param tab
	 * @return
	 */
	public String toText(String tab){
		String name = (actualName.length() > 20 ) ?
				actualName.substring(0, 17) + "..." : (actualName + "                    ").substring(0,  20);
		String mult = (String.valueOf(multiplier) + "            ").substring(0,15);
		String tol = (String.valueOf(tolerance) + "            ").substring(0,15);
		String type = isRelative() ? "(relative)" : ("absolute");
		return tab + "slider    " + name + mult + tol + type;
	}
}
