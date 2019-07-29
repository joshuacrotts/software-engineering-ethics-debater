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

import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This file is to abstract what exactly defines a Premise object; we need
 * to differentiate between the premise's name and it's definition. This
 * will be implemented in the code later down the road.
 */
@XmlRootElement( name = "premise" )
public class Premise implements Serializable
{
    @XmlElement( name = "name" )
    private String name;

    @XmlElement( name = "definition" )
    private String definition;
    
    private String text;
    
    private int proConStatus = -1;

    public Premise()
    {
    }
    
    public Premise( String name, String definition )
    {
        this.name       = name;
        this.definition = definition;
    }
    
    public Premise( String name, String definition, String text )
    {
        this.name       = name;
        this.definition = definition;
        this.text       = text;
    }    
    
    public Premise( String name, String definition, int proConStatus )
    {
        this.name         = name;
        this.definition   = definition;
        this.proConStatus = proConStatus;
    }    
    
    /**
     * Returns the name (title) of the premise.
     * 
     * @return name of premise 
     */
    public String getName()
    {
        return this.name;
    }
    
    /**
     * Returns the definition of the premise.
     * 
     * @return definition of premise
     */
    public String getDefinition()
    {
        return this.definition;
    }
    
    public int getProConStatus()
    {
        return this.proConStatus;
    }
    
    @Override
    public String toString()
    {
        return this.name + ": " + this.definition;
    }
    
    public String getText()
    {
        return this.text;
    }
}

