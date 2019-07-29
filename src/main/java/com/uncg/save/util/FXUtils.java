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

package com.uncg.save.util;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.paint.Color;

public abstract class FXUtils {

    /**
     * Converts the normalized double d to the 0-255 range,
     * giving the appropriate red color for the hex representation.
     * 
     * @param d
     * @return 
     */
    public static int ConvertToRed( double d )
    {
        return ( int ) ( ( 1 - d ) * 255 );
    }

    /**
     * Converts the normalized double d to the 0-255 range,
     * giving the appropriate blue color for the hex representation.
     * 
     * @param d
     * @return 
     */
    public static int ConvertToBlue( double d )
    {
        return ( int ) ( d * 255 );
    }
    
    /**
     * Converts the Color object into a String hexadecimal representation.
     * 
     * @param color
     * @return String
     */
    public static String toRGBCode( Color color ) 
    {
        return String.format( "#%02X%02X%02X",
                (int) ( color.getRed()   * 255 ),
                (int) ( color.getGreen() * 255 ),
                (int) ( color.getBlue()  * 255 ) );
    }
    
    /**
     * 
     * @param num
     * @param min
     * @param max 
     */
    public static void clamp( int num, int min, int max )
    {
        if ( min < min )
        {
            num = min;
        } 
        else if ( min > max )
        {
            num = max;
        }
    }
    
    public static boolean isEmpty( Object[] array )
    {
        for ( int i = 0; i < array.length; i++ )
        {
            if ( array[ i ] != null )
            {
                return false;
            }
        }
        
        return true;
    }
    
    public static Bounds nodePosition( Node node )
    {
        return node.localToScreen( node.getBoundsInLocal() );
    }
}
