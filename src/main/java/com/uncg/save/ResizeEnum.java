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

/*
 * This enum defines the index positions for the boolean array[s]
 * that determine which anchor the user wants to stretch
 */
public enum ResizeEnum
{
    LEFT ( 0 ),
    RIGHT ( 1 ),
    TOP ( 0 ),
    BOTTOM ( 1 );
    
    public final int ID;
    
    ResizeEnum( int id )
    {
        this.ID = id;
    }   
}
