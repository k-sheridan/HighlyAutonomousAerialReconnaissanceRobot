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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gamecontrolplus.Configuration.InputConfig;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.Rumbler;
import processing.core.PApplet;

/**
 * <p>
 * The device class is for the communication with your input devices. A device
 * consists of buttons and sliders, sliders can be grouped to sticks.
 * </p>
 * <p>
 * To react on button events you can plug methods, that are called when
 * a button is pressed, released or while a button is pressed.
 * </p>
 * 
 * @author Peter Lager & Christian Riekoff
 */
public class ControlDevice implements Comparable<ControlDevice>, PCPconstants {

	/**
	 * The JInput controller instance for this device
	 */
	private final Controller controller;

	/**
	 * Instance to the PApplet where procontrol is running
	 */
	private final PApplet parent;

	/**
	 * This is used to show the device is available for selection by matching with
	 * a configurations file.
	 */
	public boolean available = true;
	
	/**
	 * list containing the sliders on the device
	 */
	private final List<ControlSlider> sliders = new ArrayList<ControlSlider>();

	/**
	 * list containing the buttons on the device
	 */
	private final List<ControlButton> buttons = new ArrayList<ControlButton>();

	/**
	 * list containing the rumblers on the device
	 */
	private Rumbler[] rumblers = new Rumbler[0];

	/**
	 * to map the device generated input names and Controller inputs
	 */
	private final Map<String, ControlInput> inputNameMap = 
			new HashMap<String, ControlInput>();

	/**
	 * A List with the buttons and sliders available by the device
	 */
	private final List<ControlInput> inputs = new ArrayList<ControlInput>();

	/**
	 * true if the device has been opened. Only opened devices are updated before
	 * a frame.
	 */
	private boolean open = false;

	/**
	 * The name of the device.
	 */
	private final String name;
	
	/**
	 * Initializes a new device by the given Controller
	 * 
	 * @param i_controller
	 */
	ControlDevice(final Controller i_controller, final PApplet i_parent){
		controller = i_controller;
		parent = i_parent;
		name = i_controller.getName();
		setupDevice();
	}

	/**
	 * Loads the available Sliders, Sticks and Buttons for a device
	 */
	private void setupDevice(){
		final Component[] components = controller.getComponents();
		// Now identify all buttons and sliders
		ControlInput input = null;
		for (int i = 0; i < components.length; i++){
			input = null;
			if(components[i].isAnalog()){
				if(components[i].isRelative()){
					input = new ControlRelativeSlider(components[i]);
				}else{
					input = new ControlSlider(components[i]);
				}
				sliders.add((ControlSlider)input);
			} else {
				if(components[i].getIdentifier() == Component.Identifier.Axis.POV){
					input = new ControlHat(components[i],parent);
				}else{
					input = new ControlButton(components[i],parent);
				}
				buttons.add((ControlButton)input);
			} 
			if(input != null){
				inputNameMap.put(input.getName(), input);
			}
		}  
		inputs.addAll(sliders);
		inputs.addAll(buttons);
		rumblers = controller.getRumblers();
	}

	/**
	 * If available will attempt to match this device to the required configuration.
	 * If a match occurs then <br>
	 * <ol>
	 * <li>the user defined keys are added to the device </li>
	 * <li>the config count is incremented </li>
	 * <li>the device is marked as unavailable for future matches </li>
	 * <li> the device is opened </li>
	 * </ol>
	 * @param config
	 * @return
	 */
	public boolean matches(Configuration config){
		Map<String, ControlInput> matchedInputs = 
				new HashMap<String, ControlInput>();
		for(InputConfig ic : config.gameInputs){
			// Get the input with the given device name
			ControlInput input = inputNameMap.get(ic.deviceInputName);
			// If it is the same type remember it
			if(input != null && input.inputType == ic.type){
				matchedInputs.put(ic.key, input);
				input.setMultiplier(ic.multiplier);
				input.setTolerance(ic.tolerance);
			}
			else 
				return false;
		}
		// Match found so add keys
		inputNameMap.putAll(matchedInputs);
		available = false;
		open();
		return true;
	}
	
	/**
	 * Returns the name of the device.
	 * @return String, the name of a device
	 * @example procontrol_printDevices
	 * @related ControllDevice
	 */
	public String getName(){
		return name;
	}

	/**
	 * Get the name for the type of device e.g. Gamepad, Mouse
	 * @return
	 */
	public String getTypeName(){
		return controller.getType().toString();
	}

	/**
	 * Get the name of the connection type e.g. USB, network etc.
	 */
	public String getPortTypeName(){
		return controller.getPortType().toString();
	}

	/**
	 * Returns the String representation of a device
	 * @return String, the String representation of a device
	 * @invisible
	 */
	public String toString(){
		return controller.getName();
	}

	/**
	 * This method is called on every thread loop 
	 */
	public void update(){
		if(open){
			controller.poll();
			for (int i = 0; i < inputs.size(); i++)
				inputs.get(i).update();
		}
	}

	/**
	 * This method is called before each frame to update the controller values
	 */
	protected void updateRelative(){
		if(open){
			for (int i = 0; i < sliders.size(); i++)
				sliders.get(i).updateRelative();
		}
	}
	
	/**
	 * Get a short text description for this device
	 * @param tab
	 */
	public String toListText(String tab){
		return tab + name + "     [" + getTypeName() + "]  on  [" + getPortTypeName() + "]";
	}
	
	/**
	 * Get a detailed text description for this device
	 * @param tab
	 */
	public String toText(String tab){
		StringBuilder s = new StringBuilder(tab + "========================================================================\n" );
		s.append(tab + "NAME :     " + name + "\n");
		s.append(tab + "Type :     " + getTypeName() +"\n");
		s.append(tab + "Port :     " + getPortTypeName() +"\n");
		s.append(buttonsToText(tab));
		s.append(slidersToText(tab));
		s.append(tab + "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
		return s.toString();
	}

	/**
	 * Get a text description of the buttons on this device
	 * @param tab
	 */
	public String buttonsToText(String tab){
		StringBuilder s = new StringBuilder();
		if(buttons.size() > 0){
			s.append(tab + "  Buttons ("+buttons.size()+")\n");
			s.append(tab + "    Type     Name               Multiplier\n");
			for(int i = 0; i < buttons.size(); i++){
				s.append(buttons.get(i).toText(tab + "    ") + "\n");
			}
		}
		return s.toString();		
	}
	
	/**
	 * Get a text description of the sliders on this device
	 * @param tab
	 */
	public String slidersToText(String tab){
		StringBuilder s = new StringBuilder();
		if(sliders.size() > 0){
			s.append(tab + "  Sliders ("+sliders.size()+")\n");
			s.append(tab + "    Type     Name               Multiplier     Tolerance\n");
			for(int i = 0; i < sliders.size(); i++){
				s.append(sliders.get(i).toText(tab + "    ") + "\n");
			}
		}
		return s.toString();
	}
	
	/**
	 * Get a list of all inputs for this device
	 */
	public List<ControlInput> getInputs(){
		return inputs;
	}

	/**
	 * Returns the number of sliders of the device.
	 * @return the number of sliders on this device
	 */
	public int getNumberOfSliders(){
		return sliders.size();
	}

	/**
	 * Use this method to get a Slider based on its position in the list of inputs. Use
	 * printSliders to see what sliders are available for a device.
	 * @param i_sliderNumb int, the number of the slider to return
	 * @return ControllSlider, the Slider corresponding to the given number
	 */ 
	public ControlSlider getSlider(final int i_sliderNumb){ 
		return (ControlSlider)sliders.get(i_sliderNumb); 
	} 

	/**
	 * Use this method to get a Slider based on its name. The name could be the system-dependent name or 
	 * the name specified in a device configuration.
	 * Use printSliders to see what sliders are available for a device.
	 * @param i_sliderName String, the name of the slider to return
	 * @return ControllSlider, the Slider corresponding to the given name or null if not found.
	 */ 
	public ControlSlider getSlider(final String i_sliderName){
		try{
			return(ControlSlider)inputNameMap.get(i_sliderName);
		}catch (ClassCastException e){
		}

		throw new RuntimeException("There is no slider with the name " + i_sliderName + ".");
	}

	/**
	 * Tolerance is minimum under which the input is set to zero.
	 * Use this method to set the tolerance for all sliders of the device.
	 * @param i_tolerance float, the new tolerance for the device
	 */
	public void setTolerance(final float i_tolerance){
		for (int i = 0; i < sliders.size(); i++)
			((ControlSlider) sliders.get(i)).setTolerance(i_tolerance);
	}

	/**
	 * Returns the number of buttons of the device.
	 * @return int, the number of buttons available for a device
	 */
	public int getNumberOfButtons(){
		return buttons.size();
	}

	/**
	 * Use this method to get a Button based on its position in the list of inputs. Use
	 * the buttonsToText method to see what buttons are available for a device.
	 * @param i_buttonName int, the number of the button to return
	 * @return ControlButton, the Button corresponding to the given number
	 */ 
	public ControlButton getButton(final int i_buttonNumb){ 
		return buttons.get(i_buttonNumb); 
	} 

	/**
	 * Use this method to get a Button based on its name. The name could be the system-dependent name or 
	 * the name specified in a device configuration. Use the buttonsToText method to see what buttons 
	 * are available for a device.
	 * @param i_buttonName String, the name of the button to return
	 * @return ControlButton, the Button corresponding to the given name or null if not found.
	 */ 
	public ControlButton getButton(final String i_buttonName){
		try{
			return (ControlButton)inputNameMap.get(i_buttonName);
		}catch (ClassCastException e){
		}
		throw new RuntimeException("There is no button with the name " + i_buttonName + ".");
	}

	/**
	 * Use this method to get a Hat based on its position in the list of inputs. Use the buttonsToText 
	 * method to see what hats are available for a device.
	 * @param i_buttonName String, the name of the button to return
	 * @return ControlHat, the Hat corresponding to the given name or null if not found.
	 */ 
	public ControlHat getHat(final int i_hatNumb){ 
		return (ControlHat)buttons.get(i_hatNumb); 
	} 

	/**
	 * Use this method to get a Hat based on its name. The name could be the system-dependent name or 
	 * the name specified in a device configuration. Use the buttonsToText method to see what hats 
	 * are available for a device.
	 * @param i_hatName String, the name of the hat to return
	 * @return ControlHat, the Hat corresponding to the given name or null if not found.
	 */ 
	public ControlHat getHat(final String i_hatName){
		try{
			return(ControlHat)inputNameMap.get(i_hatName);
		}catch (ClassCastException e){
		}
		throw new RuntimeException("There is no hat with the name " + i_hatName + ".");
	}

	/**
	 * Use this method to open a device. A device is automatically opened by
	 * default, so you only need to call this when you have closed it with the
	 * close method.
	 */
	public void open(){
		open = true;
	}

	/**
	 * Use this method to close a device. A closed device does not to be updated
	 * to get values.
	 */
	public void close(){
		open = false;
	}

	/*
	 * JInput doesn't appear to detail any rumblers.
	 * 
	 * So these methods are not documented.
	 */
	public int getNumberOfRumblers(){
		return rumblers.length;
	}

	public void rumble(final float i_intensity, final int i_id){
		if(i_id >= rumblers.length) return;
		else rumblers[i_id].rumble(i_intensity);
	}

	public void rumble(final float i_intensity){
		rumble(i_intensity,0);
	}

	/**
	 * Plug is a handy method to handle incoming button events. To create a plug
	 * you have to implement a method that reacts on the events. To plug a method you
	 * need to give a device the method name, the event type you want to react on and
	 * the button. If your method is inside a class you have to give the plug
	 * a reference to it.
	 * @param i_object Object: the object with the method to plug
	 * @param i_methodName String: the name of the method that has to be plugged
	 * @param i_eventType constant: can be ControllIO.ON_PRESS, ControllIO.ON_RELEASE or ControllIO.WHILE_PRESS
	 * @param i_input int: the number of the button that triggers the plug
	 */
	public void plug(
			final Object i_object, 
			final String i_methodName, 
			final int i_eventType,
			final int i_input
			){
		open();
		getButton(i_input).plug(i_object,i_methodName,i_eventType);
	}

	/**
	 * Plug is a handy method to handle incoming button events. To create a plug
	 * you have to implement a method that reacts on the events. To plug a method you
	 * need to give a device the method name, the event type you want to react on and
	 * the button. If your method is inside a class you have to give the plug
	 * a reference to it.
	 * @param i_methodName String: the name of the method that has to be plugged
	 * @param i_eventType constant: can be ControllIO.ON_PRESS, ControllIO.ON_RELEASE or ControllIO.WHILE_PRESS
	 * @param i_input int: the number of the button that triggers the plug
	 */
	public void plug(
			final String i_methodName, 
			final int i_eventType,
			final int i_input
			){
		open();
		getButton(i_input).plug(parent,i_methodName,i_eventType);
	}

	/**
	 * Plug is a handy method to handle incoming button events. To create a plug
	 * you have to implement a method that reacts on the events. To plug a method you
	 * need to give a device the method name, the event type you want to react on and
	 * the button. If your method is inside a class you have to give the plug
	 * a reference to it.
	 * @param i_object Object: the object with the method to plug
	 * @param i_methodName String: the name of the method that has to be plugged
	 * @param i_eventType constant: can be ControllIO.ON_PRESS, ControllIO.ON_RELEASE or ControllIO.WHILE_PRESS
	 * @param i_input String: the name of the button that triggers the plug
	 */
	public void plug(
			final Object i_object, 
			final String i_methodName, 
			final int i_eventType,
			final String i_input
			){
		open();
		getButton(i_input).plug(i_object,i_methodName,i_eventType);
	}

	/**
	 * Plug is a handy method to handle incoming button events. To create a plug
	 * you have to implement a method that reacts on the events. To plug a method you
	 * need to give a device the method name, the event type you want to react on and
	 * the button. If your method is inside a class you have to give the plug
	 * a reference to it.
	 * @param i_methodName String: the name of the method that has to be plugged
	 * @param i_eventType constant: can be ControllIO.ON_PRESS, ControllIO.ON_RELEASE or ControllIO.WHILE_PRESS
	 * @param i_input String: the name of the button that triggers the plug
	 */
	public void plug(
			final String i_methodName, 
			final int i_eventType,
			final String i_input
			){
		open();
		getButton(i_input).plug(parent,i_methodName,i_eventType);
	}

	public boolean equals(Object d){
		boolean result = compareTo((ControlDevice)d) == 0;
		return result;
	}
	
	@Override
	public int compareTo(ControlDevice d) {
		String s = name + ((Integer)buttons.size()).toString() + ((Integer)sliders.size()).toString() + ((Integer)rumblers.length).toString(); 
		String ds = d.name + ((Integer)d.buttons.size()).toString() + ((Integer)d.sliders.size()).toString() + ((Integer)d.rumblers.length).toString(); 
		int result = s.compareToIgnoreCase(ds);
        return result;
	}
}
