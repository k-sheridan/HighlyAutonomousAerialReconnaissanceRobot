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
 * 
 * The {@link ValueReceiver} is used to synchronize values from an Arduino to the Processing sketch.
 * If you have a variable on your Arduino -- for example the reading from an analog pin --
 * you can use {@link ValueReceiver} to synchronize it with a variable in your sketch.
 *  
 * Have a look at the SimpleReceiver example for the minimal steps to get going.
 * 
 *
 */

public class ValueReceiver implements VSyncConstants 
{
	private StringBuffer messageBuffer = new StringBuffer();
	private Serial serial;
	private PApplet parent;
	private ArrayList<String> observedVariables = new ArrayList<String>();
	
	public final static String VERSION = "v0.1";
	


	/**
	 * Creates a new instance of {@link ValueReceiver} using a given {@link Serial} interface.
	 * 
	 * @param parent The {@link PApplet} you wan to attach to.
	 * @param serial The {@link Serial} interface to be used for communication.
	 */
	public ValueReceiver(PApplet parent, Serial serial) 
	{
		this.parent = parent;
		this.serial = serial;
		parent.registerMethod("pre", this);
	}
	
	/**
	 * Starts the observation of a new variable.
	 * Whenever the variable changed on the Arduino it is updated in the sketch too.
	 * The variable should be declared <code>public</code>and must be of type <code>int</code>!
	 * @param The name of the variable to observe.
	 */
	public ValueReceiver observe(String variable)
	{
		observedVariables.add(variable);
		return this;
	}
	
	public void pre()
	{
		while(serial.available() > 0)
		{
			char in = serial.readChar();
//			PApplet.print(in);
			if(in == MESSAGE_END)
			{
				analyzeMessage(messageBuffer.toString());
				messageBuffer = new StringBuffer();
			}
			else
			{
				messageBuffer.append(in);
			}
			
		}
	}
	
	private void analyzeMessage(String s)
	{
		try
		{
			if(s == null || s.length() == 0) return;
			String[] items = s.split("\\" + DELIMITER);
			if(items.length == 0) return;
		
			if(items[0].length() == 1 && items[0].charAt(0) == ALL_VALUES)
			{
				if(items.length != observedVariables.size() + 1)
				{
					return;
				}
				for(int i = 0; i < observedVariables.size(); i++)
				{
					setValue(i, Integer.parseInt(items[i+1]));
				}
			}
			else
			{
				if(items.length % 2 != 0) return;
				
				for(int i = 0; i < items.length / 2; i++)
				{
					int index = Integer.parseInt(items[i*2]);
					int value = Integer.parseInt(items[i*2 + 1]);
					setValue(index, value);
				}
			}
		}
		catch (Exception e)
		{
			
		}
		
	}
	
	private void setValue(int index, int value)
	{
		setValue(observedVariables.get(index), value);
	}
	
	private void setValue(String valueName, int value)
	{
		try {
			parent.getClass().getField(valueName).setInt(parent, value);
		} catch (IllegalArgumentException e) {
			System.err.printf("%s is not an integer variable in you sketch!\n Try declaring it as 'int %s;'", valueName, valueName);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			System.err.printf("%s is not acessible variable in you sketch! Try declaring it as public.\n", valueName);
		} catch (NoSuchFieldException e) {
			System.err.printf("%s is not a variable in you sketch!\n", valueName);
		}
	}
	
	
	//TODO: ist das kunst oder kann das weg? vvv
	
	private void welcome() 
	{
		System.out.println("VSync for Processing v0.1 by Maximilian Ernestus http://erniejunior.wordpress.com/");
	}
	
	/**
	 * return the version of the library.
	 * 
	 * @return String
	 */
	public static String version() {
		return VERSION;
	}

	public void setSerial(Serial serial)
	{
		serial.clear();
		this.serial = serial;
	}
	
	public void stopSerial()
	{
		serial.stop();
	}
}

