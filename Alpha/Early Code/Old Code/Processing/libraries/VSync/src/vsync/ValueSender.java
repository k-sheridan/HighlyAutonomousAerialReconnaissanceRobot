/**
 * VSync for Processing
 * Will magically synchronize variables among Arduinos and your Processing sketch.
 * http://erniejunior.github.io/VSync-for-Processing/
 *
 * Copyright (c) 2013 Maximilian Ernestus http://erniejunior.wordpress.com/
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA  02111-1307  USA
 * 
 * @author      Maximilian Ernestus http://erniejunior.wordpress.com/
 * @modified    03/16/2014
 * @version     v0.1 (1)
 */

package vsync;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.serial.Serial;


/**
 * The ValueSender is used to synchronize values from the Processing sketch to the Arduino.
 * If you have a variable in your sketch -- for example the x coordinate of your mouse <code>mouseX</code> --
 * you can use ValueSender to synchronize it to your Arduino. 
 * 
 * The SimpleSender example shows you the minimal steps to get going.
 *
 */
public class ValueSender implements VSyncConstants 
{

	private Serial serial;
	private PApplet parent;
	private long lastValueSent;
	private long creationTime;
	
	private ArrayList<String> observedVariables = new ArrayList<String>();
	private ArrayList<Integer> previousValues = new ArrayList<Integer>();

	/**
	 * Creates a new instance of a {@link ValueSender} using a given Serial interface.
	 * @param parent The {@link PApplet} you want to attach to.
	 * @param serial The {@link Serial} you want to use for communication.
	 */
	public ValueSender(PApplet parent, Serial serial) 
	{
		this.parent = parent;
		this.serial = serial;
		this.creationTime = parent.millis();
		
		parent.registerMethod("pre", this);
	}

	public void pre() 
	{
		sync();
	}

	/**
	 * Starts the observation of a new variable.
	 * Whenever the variable changes it is updated on the Arduino too.
	 * The variable should be declared <code>public</code>and must be of type <code>int</code>!
	 * @param The name of the variable to observe.
	 */
	public ValueSender observe(String variable) 
	{
		observedVariables.add(variable);
		
		//  Pick a previous value that is different from the actual value.
		//  That way the variable will be synced on the next call to sync().
		previousValues.add(getValue(observedVariables.size()-1) + 1); 
		return this;
	}

	/**
	 * Sends all values that have changed since the last call to <code>sync()</code>.
	 */
	private void sync() 
	{
		if(parent.millis() - creationTime < 900)
			return;
		
		int numValuesChanged = 0;
		for(int i = 0; i < observedVariables.size(); i++) 
		{
			if (variableChanged(i))
				numValuesChanged++;
		}
		
		if(numValuesChanged == 0)
			return; //TODO: maybe check for keepalives that need to be sent?
		

		if (allValuesMinPackageSize() < numValuesChanged * singleValueMinPackageSize())
		{
			sendAllValues();
		} 
		else 
		{
			boolean firstValueSent = false;
			for(int i = 0; i < observedVariables.size(); i++) 
			{
				if (variableChanged(i))
				{
					if(firstValueSent)
						serial.write(DELIMITER);
					sendValue(i);
					firstValueSent = true;
				}
			}
			serial.write(MESSAGE_END);
		}

		for(int i = 0; i < observedVariables.size(); i++) 
			previousValues.set(i, getValue(i));
		lastValueSent = System.currentTimeMillis();
	}

	/**
	 * Checks if a variable changed since the last sync.
	 * @param index The index (depending on the order of the observe() call) of the variable to check.
	 * @return Whether the variable changed or not.
	 */
	public boolean variableChanged(int index) 
	{
		return getValue(index) != previousValues.get(index);
	}

	/**
	 * Sends the x'st value you added to the sender. Returns true if sending was
	 * successful.
	 */
	private boolean sendValue(int index) {
		if (!(index < observedVariables.size() && index >= 0))
			return false;

		serial.write(index + "");
		serial.write(DELIMITER);
		serial.write(getValue(index) + "");

		return true;
	}

//	void sendKeepalive() {
//		serial.write(MESSAGE_END);
//		lastValueSent = System.currentTimeMillis();
//	}

	/**
	 * Sends all values that have been added.
	 */
	void sendAllValues() {
		serial.write(ALL_VALUES);
		for (int i = 0; i < observedVariables.size(); i++) {
			serial.write(DELIMITER);
			serial.write(getValue(i) + "");
		}
		serial.write(MESSAGE_END);
	}

	private int allValuesMinPackageSize() 
	{
		return 2 + 2 * observedVariables.size();
	}

	private int singleValueMinPackageSize() {
		return 4;
	}

	long timeSinceLastMessage() 
	{
		return System.currentTimeMillis() - lastValueSent;
	}
	
	private int getValue(int id)
	{
		return getValue(observedVariables.get(id));
	}

	private int getValue(String value) {
		try {
			return parent.getClass().getField(value).getInt(parent);
		} catch (IllegalArgumentException e) {
			System.err
					.printf("%s is not an integer variable in you sketch!\n Try declaring it as 'int %s;'",
							value, value);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			System.err
					.printf("%s is not acessible variable in you sketch! Try declaring it as public.\n",
							value);
		} catch (NoSuchFieldException e) {
			System.err.printf("%s is not an variable in you sketch!\n", value);
		}
		return -1;
	}
}
