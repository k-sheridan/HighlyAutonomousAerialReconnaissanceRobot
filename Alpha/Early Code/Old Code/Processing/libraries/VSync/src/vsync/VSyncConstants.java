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

/**
 * An interface to store the constants (special characters of the protocol)
 */
public interface VSyncConstants 
{
	public final char DELIMITER = '|';
	public final char MESSAGE_END = '#';
	public final char ALL_VALUES = 'A';
}
