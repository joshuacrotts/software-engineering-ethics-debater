/*
===========================================================================
                        Software Engineering Ethics Debater (SWED) Source Code
                           Copyright (C) 2019 Nancy Green

Software Engineering Ethics Debater (SWED) is free software: you can redistribute it and/or 
modify it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

SWED Source Code is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with SWED Source Code.  If not, see <http://www.gnu.org/licenses/>.

If you have questions concerning this license or the applicable additional 
terms, you may contact Dr. Nancy Green at the University of North
Carolina at Greensboro.       
===========================================================================
*/

package com.uncg.save;

import javafx.scene.input.MouseEvent;

/**
 * This interface is a quick way of distinguishing between what objects/panes
 * are draggable, and those that are not. 
 * 
 * The user should add two eventFilters (in the initialize FX method) : one for 
 * MOUSE_DRAGGED, and MOUSE_PRESSED. Both of these should register the user's x 
 * and y scene coordinates, but within the MOUSE_DRAGGED event, the first call 
 * should be to onDragRelease(...), which takes the difference between 
 * event.getScreenX() - this.x, with the same applicable for the y coordinate. 
 * This value (a double) is what's actually APPLIED to the object itself (the 
 * translateX/Y).
 * 
 * @author Joshua Crotts
 */
public interface Draggable
{
    void onDragRelease( MouseEvent event );
}
