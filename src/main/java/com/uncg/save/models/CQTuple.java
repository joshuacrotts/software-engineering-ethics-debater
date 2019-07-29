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

package com.uncg.save.models;

//
//  This class embeds the critical question number, pro/con flag
//  and proposition of a saved critical question. It's solely
//  for de-serialization purposes.
//
public class CQTuple
{
    private final int    cqNum;
    private final int    proConFlag;
    private final String proposition;
    
    public CQTuple( int cqNum, int proConFlag, String proposition )
    {
        this.cqNum          = cqNum;
        this.proConFlag     = proConFlag;
        this.proposition    = proposition;
    }
    
    public int getCQNum()
    {
        return this.cqNum;
    }
    
    public int getProConFlag()
    {
        return this.proConFlag;
    }
    
    public String getProposition()
    {
        return this.proposition;
    }
}
