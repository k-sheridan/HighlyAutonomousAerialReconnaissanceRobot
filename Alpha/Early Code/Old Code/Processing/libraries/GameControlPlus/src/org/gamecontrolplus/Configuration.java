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
 */

package org.gamecontrolplus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


import processing.core.PApplet;

/**
 * This class is used to store configuration details for a control device. It is used by the library
 * and there is no need to use it directly.
 * 
 * @author Peter Lager
 *
 */
public class Configuration implements PCPconstants {
	
	// If zero then we are inside Eclipse anything else we are inside Processing
	public static final int ECLIPSE = 0;
	public static final int PROCESSING = 1;
	public static int IDE = PROCESSING;


	/**
	 * Create a Configuration object from the information in the specified file.
	 */
	public static Configuration makeConfiguration(PApplet app, String filename){
		File file = (IDE == ECLIPSE) ? new File(filename) : new File(app.dataPath("") +"/" + filename);
		String[] configLines = PApplet.loadStrings(file);
		if(configLines == null){
			System.out.println("Unable to find configuration file " + filename);
			return null;
		}
		return new Configuration(app, configLines, filename);
	}

//	public static Configuration makeConfiguration(PApplet app, String pathToSketch, String filename){
//		File file = (IDE == ECLIPSE) ? new File(filename) : new File(pathToSketch +"/data/" + filename);
//		String[] configLines = PApplet.loadStrings(file);
//		if(configLines == null){
//			System.out.println("Unable to find configuration file " + filename);
//			return null;
//		}
//		return new Configuration(app, configLines, filename);
//	}

	/**
	 * Save the Configuration details in a file.
	 */
	public static void saveConfiguration(PApplet app, Configuration config){
		String[] lines = new String[config.gameInputs.length + 1];
		int index = 0;
		lines[index++] = config.usage;
		for(InputConfig input : config.gameInputs)
			lines[index++] = input.toString();
		File file = (IDE == ECLIPSE) ? new File(config.filename) : new File(app.dataPath("") +"/" + config.filename);
		PApplet.saveStrings(file, lines);
	}

	// ========================================================================================
	// Configuration stuff
	// ========================================================================================

	final String filename;
	final String usage;
	final InputConfig[] gameInputs;

	/**
	 * Private constructor to force factory constructor.
	 */
	private Configuration(PApplet app, String[] lines, String filename){
		List<InputConfig> inputConfigs = new ArrayList<InputConfig>();
		this.filename = filename;
		usage = lines[0];
		for(int i = 1; i < lines.length; i++){
			if(lines[i].length() > 0)
				inputConfigs.add(new InputConfig(lines[i]));
		}
		gameInputs = inputConfigs.toArray(new InputConfig[inputConfigs.size()]);
	}

	/**
	 * Get the text description for the role of this configuration e.g. Tank controller.
	 */
	public String getUsgae(){
		return usage;
	}
	
	/**
	 * Get the game input specifications.
	 */
	public InputConfig[] getGameInputs(){
		return gameInputs;
	}
	
	/**
	 * Get the number of inputs specified.
	 */
	public int nbrInputs(){
		return gameInputs.length;
	}

	/**
	 * Inner class to represent a single input and a single line in the configuration file.
	 * @author Peter Lager
	 *
	 */
	public class InputConfig {
		public String key;
		public String description;
		public int type;
		public String typeName;
		public String deviceInputName;
		public int inputConNo;
		public float multiplier = 1;
		public float tolerance = 0;

		/**
		 * Create an input specification from a line in the config file.
		 * @param line
		 */
		public InputConfig(String line){
			String[] part = PApplet.split(line, SEPARATOR);
			key = part[0];
			description = part[1];
			type = Integer.parseInt(part[2]);
			typeName = part[3];
			deviceInputName = part[4];
			inputConNo = Integer.parseInt(part[5]);
			if(part.length > 6)
				multiplier = Float.parseFloat(part[6]);
			if(part.length > 7)
				tolerance = Float.parseFloat(part[7]);
		}

		/**
		 * Create a single line description
		 */
		public String toString(){
			StringBuilder sb = new StringBuilder(key + SEPARATOR);
			sb.append(description + SEPARATOR);
			sb.append(type + SEPARATOR);
			sb.append(typeName + SEPARATOR);
			sb.append(deviceInputName + SEPARATOR);
			sb.append(inputConNo + SEPARATOR);
			sb.append(multiplier + SEPARATOR);
			sb.append(tolerance);
			return sb.toString();
		}
	}
}
