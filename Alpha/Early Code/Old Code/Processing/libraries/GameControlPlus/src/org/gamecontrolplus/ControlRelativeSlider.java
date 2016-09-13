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

import processing.core.PApplet;
import net.java.games.input.Component;

/**
 * 
 * @author Christian Riekoff
 *
 */
class ControlRelativeSlider extends ControlSlider{

	ControlRelativeSlider(Component i_component){
		super(i_component);
	}
	
	private float pollValue = 0;

	/**
	 * This method is called before each frame to update the slider values.
	 */
	void update(){
		if(PApplet.abs(actualValue) < component.getDeadZone()){
		}
		else {
			pollValue += component.getPollData()*multiplier;
		}
	}
	
	void updateRelative(){
		actualValue = pollValue;
		pollValue = 0;
	}
}
