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

/**
 * Base class for input elements of a controller.
 * 
 */
public abstract class ControlInput implements PCPconstants {
	/**
	 * The current state of the input
	 */
	protected float actualValue = 0f;

	/**
	 * Included for all inputs for convenience but only used
	 * by the ControlSlider. <br>
	 * Tolerance is minimum under which the input is set to
	 * zero.
	 */
	protected float tolerance = 0f;
	
	/**
	 * Included for all inputs for convenience but only used
	 * by the ControlSlider and ControlHat classes. <br>
	 * The value of a slider is a relative value between
	 * -1.0f and 1.0f with the multiplier you can increase 
	 * and decrease this range.
	 */
	protected float multiplier = 1f;
		

	/**
	 * JInput Component representing this Slider
	 */
	final Component component;
	
	/**
	 * The name of the Input component provided by JInput. This 
	 * maybe different for different OS.
	 */
	protected final String actualName;
	
	/**
	 * One of BUTTON_TYPE, HAT_TYPE or SLIDER_TYPE
	 */
	protected int inputType;
	
	/**
	 * Initializes a new Slider.
	 * @param i_component
	 */
	ControlInput(final Component i_component){
		component = i_component;
		actualName = component.getName();
	}
	
	/**
	 * Returns the name of the input.
	 * @return String, the name of the input element
	 * @usage application
	 */
	public String getName(){
		return actualName;
	}

	/**
	 * Gives you the current value of an input.
	 * @return float, the actual value of the slider
	 */
	public float getValue(){
		return actualValue;
	}
	
	/**
	 * This value has no significance except for the ControlSlider class.
	 */
	public float getTolerance(){
		return tolerance;
	}
	
	/**
	 * Tolerance is only used by the ControlSlider class but is placed 
	 * here for convenience of use. <br>
	 * If you not want a slider to react up to a certain value you can set 
	 * a tolerance value. Use this method to set the tolerance.
	 * By default this value is set to 0.
	 * @param i_tolerance float, the new tolerance for the slider
	 */
	public void setTolerance(final float i_tolerance){
		tolerance = i_tolerance;
	}
	
	/**
	 * This value has no significance except for the ControlSlider and
	 * ControlHat classes.
	 */
	public float getMultiplier(){
		return multiplier;
	}
	
	/**
	 * Multiplier is only used by the ControlSlider and ControlHat
	 * classes but is placed here for convenience of use. <br>
	 * The value of a slider, and the X and Y values of a hat normally 
	 * have a value in the range -1.0f and 1.0f. <br>
	 * You can change this range by setting a multiplier with a value not 
	 * equal to 1. <br>
	 * 
	 * By default the multiplier is 1.0.
	 * @param i_multiplier float, the new multiplier for a Slider or Hat
	 */
	public void setMultiplier(final float i_multiplier){
		multiplier = i_multiplier;
	}

	
	/**
	 * This method is called before each frame to update the slider values.
	 */
	abstract void update();
	
}
