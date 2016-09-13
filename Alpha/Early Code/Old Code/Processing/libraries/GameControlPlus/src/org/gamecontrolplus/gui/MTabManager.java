/*
  Part of the GUI for Processing library 
  	http://www.lagers.org.uk/g4p/index.html
	http://gui4processing.googlecode.com/svn/trunk/

  Copyright (c) 2008-12 Peter Lager

  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 2.1 of the License, or (at your option) any later version.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General
  Public License along with this library; if not, write to the
  Free Software Foundation, Inc., 59 Temple Place, Suite 330,
  Boston, MA  02111-1307  USA
 */

package org.gamecontrolplus.gui;

import java.util.LinkedList;

/**
 * Allows TABBING between text controls.
 * A tab manager allows the user to use the TAB key to move from one text control
 * (GTextField or GTextArea) to the another. This is useful when creating a 'form'
 * made from several text controls. <br>
 * The tab order is decided by the order the text controls are added to the tab 
 * manager. The TAB key move the focus forwards and SHIFT+TAB moves it backwards.
 * Note that tabbing is not cyclic so pressing TAB on the last control does not
 * set the focus on the first control, in fact it will be ignored. Similar
 * logic applies to SHIFT_TAB on the first control<br>
 * At least 2 controls must be added to the tab manager.
 * 
 * @author Peter Lager
 *
 */
public class MTabManager {
	
	private LinkedList<MEditableTextControl> textControls;
	
	public MTabManager(){
		textControls = new LinkedList<MEditableTextControl>();
	}
	
	/**
	 * Attempt to add multiple controls to the tab manager. The tab order is determined
	 * by their order as parameters to this method.
	 *  
	 * @param controls a comma separated list of text field or text area controls.
	 * @return true if any or all of the controls were added and false if none were added.
	 */
	public boolean addControls(MEditableTextControl... controls){
		boolean result = false;
		for(MEditableTextControl control : controls)
			result |= addControl(control);
		return result;
	}
	
	/**
	 * Add the next text control to this tab manager.
	 * 
	 * @param control to add
	 * @return true if added successfully
	 */
	public boolean addControl(MEditableTextControl control){
		if(!textControls.contains(control)){
			control.tabManager = this;
			textControls.addLast(control);
			return true;
		}
		return false;
	}
	
	/**
	 * Remove a control from the tab manager. This does not affect the tab
	 * order of the remaining controls.
	 * 
	 * @param control
	 * @return true if remove successfully
	 */
	public boolean removeControl(MEditableTextControl control){
		int index = textControls.lastIndexOf(control);
		if(index > 0){
			control.tabManager = null;
			textControls.remove(index);
			return true;
		}
		return false;
	}
	
	/**
	 * Used when the tab key is pressed to move to the next control
	 * @param control
	 * @return true if it found a next control else false
	 */
	boolean nextControl(MEditableTextControl control){
		int index = textControls.lastIndexOf(control);
		if(textControls.size() > 1 && index >= 0 && index < textControls.size() - 1){
			index++;
			MAbstractControl.controlToTakeFocus = textControls.get(index);;
			return true;
		}
		return false;
	}

	/**
	 * Used when the shift+tab key is pressed to move to the previous control
	 * @param control
	 * @return true if it found a previous control else false
	 */
	boolean prevControl(MEditableTextControl control){
		int index = textControls.lastIndexOf(control);
		if(textControls.size() > 1 && index > 0){
			index--;
			MAbstractControl.controlToTakeFocus = textControls.get(index);
			return true;
		}
		return false;
	}

}
