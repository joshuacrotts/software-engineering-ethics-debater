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

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;

public class PropositionModel implements Serializable
{
    private String title;
    private String proposition;
    private String comment;
    private String definition;

    public PropositionModel()
    {
        this.proposition = "";
        this.comment     = "";
        this.title       = "";
        this.definition  = "";
    }
    
    public PropositionModel( String prop, String comment )
    {
        this.title       = "";
        this.definition  = "";
        this.proposition = prop;
        this.comment     = comment;
    }

    /*
     Getters and setters
     */

    public void setDefinition( String d ) 
    {
        this.definition = d;
    }
    
    public String getDefinition()
    {
        return this.definition;
    }
    
    public void setTitle( String t )
    {
        this.title = t;
    }
    
    public String getTitle()
    {
        return this.title;
    }
    
    public String getProposition()
    {
        return proposition;
    }

    public void setProposition( String proposition )
    {
        this.proposition = proposition;
    }

    public String getComment()
    {
        return comment;
    }

    public void setComment( String comment )
    {
        this.comment = comment;
    }
    
    @Override
    public String toString()
    {
        StringBuilder str = new StringBuilder();
        
        if ( this.title != null )
        {
            str.append( this.title ).append( ": " );
        
            if ( this.definition != null )
            {
                str.append( this.definition );
            }            
        } 
        else if ( this.definition != null )
        {
            str.append( this.definition );
        } 
        else
        {
            str.append( this.proposition );
        }

        
        return str.toString();
    }

    public String[] toArray()
    {
        LinkedList<String> strings = new LinkedList<>();
        LinkedList<String[]> stringArrays = new LinkedList<>();

        for ( int i = 0; i < stringArrays.size(); i ++ )
        {
            strings.addAll( Arrays.asList( stringArrays.get( i ) ) );
        }

        String[] stringArrayToReturn = new String[strings.size()];
        strings.toArray( stringArrayToReturn );

        return stringArrayToReturn;
    }
}
