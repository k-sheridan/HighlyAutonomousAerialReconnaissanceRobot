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
import java.util.List;

import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

import org.gamecontrolplus.gui.LSelectUI;

import processing.core.PApplet;

/**
 * <p>
 * ControllIO is the base class for using controllers in Processing.
 * It provides methods to retrieve information about the connected 
 * devices and to get the input data from them.<br>
 * To get a ControllIO object you to use the getInstance() Method. To
 * get started you should use the deviceListToText(...) or the
 * devicesToText(...) to see if what control devices you have attached 
 * and their details.
 * </p>
 * <p>
 * To react on button events you can plug methods, that are called when
 * a button is pressed, released or while a button is pressed.
 * </p>
 */
public class ControlIO implements Runnable, PCPconstants {

	/**
	 * Ensures that there only exists one instance of ControllIO
	 */
	static private ControlIO instance;

	/**
	 * Used internally to identify the configured device
	 */
	public static ControlDevice configuredDevice = null;

	/**
	 * True when the library is being used to configure a device. Set
	 * to false when done.
	 */
	public static boolean configurating = false;

	/**
	 * Holds the environment of JInput
	 */
	public final ControllerEnvironment environment;

	/**
	 * Instance to the PApplet where ProControl Plus is running
	 */
	private final PApplet parent;

	/**
	 * List of the available devices
	 */
	private final List<ControlDevice> devices = new ArrayList<ControlDevice>();

	/**
	 * Thread to keep the devices updated and process any plugs created.
	 */
	private final Thread thread;

	/**
	 * Indicates whether the thread is active.
	 */
	private boolean active = true;

	/**
	 * Use this method to get a ControllIO instance. <br/>
	 * This must be called from the setup method immediately after the call to size(...)
	 * 
	 * @param i_parent PApplet, the application ProControl Plus is running in
	 * @return ControllIO, an instance of ControllIO
	 * @example ProControl Plus
	 * @usage application
	 * @related ControllIO
	 */
	static public ControlIO getInstance(final PApplet i_parent){
		if (instance == null)
			instance = new ControlIO(i_parent);
		return instance;
	}

	/**
	 * Private constructor for singleton class. <br/>
	 * Initialises the ControllIO instance
	 * @param i_parent
	 */
	private ControlIO(final PApplet i_parent){
		environment = ControllerEnvironment.getEnvironment();
		parent = i_parent;
		// Scan for devices
		final Controller[] controllers = environment.getControllers();
		for (int i = 0; i < controllers.length; i++){
			devices.add(new ControlDevice(controllers[i], parent));
		}
		// Set up applet
		parent.registerMethod("dispose", this);
		parent.registerMethod("pre", this);

		thread = new Thread(this);
		thread.start();
	}


	/**
	 * dispose method called by PApplet after closing. The update thread is deactivated here
	 */
	public void dispose(){
		active = false;
	}

	/**
	 * Creates a formatted text string listing the devices available to the the sketch.
	 * @param tab the indentation string
	 */
	public String deviceListToText(String tab){
		StringBuilder s = new StringBuilder();
		s.append(tab + "##########################################################################################\n");
		s.append(tab + "                   Game Control Plus  - available devices\n");
		s.append(tab + "                   --------------------------------------\n");
		for (int i = 0; i < devices.size(); i++){
			String id = String.valueOf(i + "    ").substring(0,3);
			s.append(tab + devices.get(i).toListText("  " + id) + "\n");
		}
		s.append(tab + "##########################################################################################\n");
		return s.toString();
	}

	/**
	 * Creates a formatted string of all available devices detailing all input controls (buttons, hats and sliders)
	 * for each device. 
	 * @param tab the indentation string
	 */
	public String devicesToText(String tab){
		StringBuilder s = new StringBuilder();
		s.append(tab + "##########################################################################################\n");
		s.append(tab + "                   Game Control Plus  - available devices\n");
		s.append(tab + "                   --------------------------------------\n");
		for (int i = 0; i < devices.size(); i++){
			s.append(devices.get(i).toText(tab + "  "));
		}
		s.append(tab + "##########################################################################################\n");
		return s.toString();
	}

	/**
	 * Returns the number of available Devices
	 * @return the number of available devices
	 */
	public int getNumberOfDevices(){
		return devices.size();
	}

	/**
	 * Get the list of USB devices connected. This will not 'open' or 'close' 
	 * any of the devices for you.
	 * 
	 */
	public List<ControlDevice> getDevices(){
		return devices;
	}

	/**
	 * Use this method to get a Device based on its number. This method will 'open'
	 * the device so that you can use it. <br>
	 * Use printDevices to see what devices are available on your system.
	 * 
	 * @param i_deviceNumber int, number of the device to open
	 * @return ControllDevice, the device corresponding to the given number or name
	 */
	public ControlDevice getDevice(final int i_deviceNumber){
		if (i_deviceNumber >= getNumberOfDevices()){
			throw new RuntimeException("There is no device with the number " + i_deviceNumber + ".");
		}
		ControlDevice result = devices.get(i_deviceNumber);
		result.open();
		return result;
	}

	/**
	 * Use this method to get a Device based on its number. This method will 'open'
	 * the device so that you can use it. <br>
	 * Use printDevices to see what devices are available on your system.
	 * @param i_deviceName String, name of the device to open
	 */
	public ControlDevice getDevice(final String i_deviceName){
		for (int i = 0; i < getNumberOfDevices(); i++){
			ControlDevice device = devices.get(i);
			if (device.getName().equals(i_deviceName)){
				device.open();
				return device;
			}
		}
		throw new RuntimeException("There is no device with the name " + i_deviceName + ".");
	}

	/**
	 * Find and return the ControlDevice that matches the configuration described in the 
	 * specified file. <br/>
	 * If an exact match can't be found give the user an option to configure another device. <br/>
	 * 
	 * @param filename the name of the configuration file
	 * @return the device or null if no device is configured.
	 */
	public ControlDevice getMatchedDevice(final String filename){
		Configuration config = Configuration.makeConfiguration(parent, filename);
		return getMatchedDevice(config);
	}

	/**
	 * Find and return the ControlDevice that matches the specified configuration. <br/>
	 * If an exact match can't be found give the user an option to configure another device. <br/>
	 * 
	 * @param config the configuration to match
	 * @return the device or null if no device is configured.
	 */
	public ControlDevice getMatchedDevice(final Configuration config){
		for(ControlDevice cd : devices){
			if(cd.available && cd.matches(config)){
				configuredDevice = cd;
				configuredDevice.available = false;
				configurating = false;
				return cd;
			}
		}
		// We have scanned the control devices and not found a match
		configurating = true;
		configuredDevice = null;
		new LSelectUI(parent, config);
		while(configurating){
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		ControlDevice selected = configuredDevice;
		configuredDevice = null;            
		return selected;	
	}

	/**
	 * Used internally to signal when a configuration has finished.
	 * @param dev the configured device or null if the configuration was cancelled.
	 */
	public void finishedConfig(ControlDevice dev){
		configuredDevice = dev;
		configurating = false;
	}


	/**
	 * Updates the devices, to get the actual data before a new
	 * frame is drawn
	 */
	public void pre(){
		for (int i = 0; i < devices.size(); i++)
			devices.get(i).updateRelative();
	}

	/**
	 * Controllers are now polled in a separate thread to get independent from
	 * the framerate of the sketch
	 */
	public void run(){
		while (active){
			for (int i = 0; i < devices.size(); i++)
				devices.get(i).update();
			try {
				Thread.sleep  ( 10 );
			} catch ( InterruptedException e ) { }
		}
	}

	/**
	 * <p>
	 * Plug is a handy method to handle incoming button events. To create a plug
	 * you have to implement a method that reacts on the events. To plug a method you
	 * need to give ControllIO the method name, the event type you want to react on and
	 * the device and button. If your method is inside a class you have to give ControllIO
	 * a reference to it.
	 * </p>
	 * <p>
	 * If you want to handle the events of a simple button, you only have to implement a
	 * method without parameters. To react on the events of a hat you method needs to
	 * receive two float values, so that ProControl Plus can send you the x and y values of the
	 * hat.
	 * </p>
	 * @param i_object Object: the object with the method to plug
	 * @param i_methodName String: the name of the method that has to be plugged
	 * @param i_eventType constant: can be ControllIO.ON_PRESS, ControllIO.ON_RELEASE or ControllIO.WHILE_PRESS
	 * @param i_intputDevice int: the number of the device that triggers the plug
	 * @param i_input int: the number of the button that triggers the plug
	 */
	public void plug(
			final Object i_object, 
			final String i_methodName, 
			final int i_eventType,
			final int i_intputDevice,
			final int i_input
			){
		ControlDevice device = getDevice(i_intputDevice);
		device.plug(i_object,i_methodName,i_eventType,i_input);
	}

	/**
	 * <p>
	 * Plug is a handy method to handle incoming button events. To create a plug
	 * you have to implement a method that reacts on the events. To plug a method you
	 * need to give ControllIO the method name, the event type you want to react on and
	 * the device and button. If your method is inside a class you have to give ControllIO
	 * a reference to it.
	 * </p>
	 * <p>
	 * If you want to handle the events of a simple button, you only have to implement a
	 * method without parameters. To react on the events of a hat you method needs to
	 * receive two float values, so that ProControl Plus can send you the x and y values of the
	 * hat.
	 * </p>
	 * @param i_methodName String: the name of the method that has to be plugged
	 * @param i_eventType constant: can be ControllIO.ON_PRESS, ControllIO.ON_RELEASE or ControllIO.WHILE_PRESS
	 * @param i_intputDevice int: the number of the device that triggers the plug
	 * @param i_input int: the number of the button that triggers the plug
	 */
	public void plug(
			final String i_methodName, 
			final int i_eventType,
			final int i_intputDevice,
			final int i_input
			){
		plug(parent,i_methodName,i_eventType,i_intputDevice,i_input);
	}

	/**
	 * <p>
	 * Plug is a handy method to handle incoming button events. To create a plug
	 * you have to implement a method that reacts on the events. To plug a method you
	 * need to give ControllIO the method name, the event type you want to react on and
	 * the device and button. If your method is inside a class you have to give ControllIO
	 * a reference to it.
	 * </p>
	 * <p>
	 * If you want to handle the events of a simple button, you only have to implement a
	 * method without parameters. To react on the events of a hat you method needs to
	 * receive two float values, so that ProControl Plus can send you the x and y values of the
	 * hat.
	 * </p>
	 * @param i_object Object: the object with the method to plug
	 * @param i_methodName String: the name of the method that has to be plugged
	 * @param i_eventType constant: can be ControllIO.ON_PRESS, ControllIO.ON_RELEASE or ControllIO.WHILE_PRESS
	 * @param i_intputDevice String: the name of the device that triggers the plug
	 * @param i_input String: the name of the input that triggers the plug. Can be system name or name from the configuration file.
	 */
	public void plug(
			final Object i_object, 
			final String i_methodName, 
			final int i_eventType,
			final String i_intputDevice,
			final String i_input
			){
		ControlDevice device = getDevice(i_intputDevice);
		device.plug(i_object,i_methodName,i_eventType,i_input);
	}

	/**
	 * <p>
	 * Plug is a handy method to handle incoming button events. To create a plug
	 * you have to implement a method that reacts on the events. To plug a method you
	 * need to give ControllIO the method name, the event type you want to react on and
	 * the device and button. If your method is inside a class you have to give ControllIO
	 * a reference to it.
	 * </p>
	 * <p>
	 * If you want to handle the events of a simple button, you only have to implement a
	 * method without parameters. To react on the events of a hat you method needs to
	 * receive two float values, so that ProControl Plus can send you the x and y values of the
	 * hat.
	 * </p>
	 * @param i_methodName String: the name of the method that has to be plugged
	 * @param i_eventType constant: can be ControllIO.ON_PRESS, ControllIO.ON_RELEASE or ControllIO.WHILE_PRESS
	 * @param i_intputDevice String: the name of the device that triggers the plug
	 * @param i_input String: the name of the input that triggers the plug. Can be system name or name from the configuration file.
	 */
	public void plug(
			final String i_methodName, 
			final int i_eventType,
			final String i_intputDevice,
			final String i_input
			){
		plug(parent,i_methodName,i_eventType,i_intputDevice,i_input);
	}
}
