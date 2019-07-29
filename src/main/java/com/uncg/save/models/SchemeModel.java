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

import com.uncg.save.Premise;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javafx.util.Pair;

public class SchemeModel implements Serializable
{
    //////////////////////// INSTANCE VARIABLES /////////////////////////////
    
    private List<Premise> premises       = new ArrayList();
    private List<String> criticalQs     = new ArrayList();
    
    //Only used for loading data in
    private List<String> conclusions    = new ArrayList<>();
    
    
    private final String conclusion;
    private String definition;
    private final String title;
    private String savedText;
    
    private int[] flags    = null;
    private int  treeDepth = 0;
    private Pair coordinates;
    
    //////////////////////// INSTANCE VARIABLES /////////////////////////////    

    public SchemeModel()
    {
        this.conclusion = "to be set";
        this.title      = "to be set";
    }
    
    public SchemeModel( String title, String conclusion, Pair coordinates, int treeDepth )
    {
        this.conclusion  = conclusion;
        this.title       = title;
        this.coordinates = coordinates;
        this.treeDepth   = treeDepth;
    }
    
    public SchemeModel( String title, String conclusion, Pair coordinates, int treeDepth, int[] flags )
    {
        this.conclusion  = conclusion;
        this.title       = title;
        this.coordinates = coordinates;
        this.treeDepth   = treeDepth;
        this.flags       = flags;
    }    
    
    public SchemeModel( String title, String definition, String conclusion, Pair coordinates, int treeDepth, int[] flags )
    {
        this.conclusion  = conclusion;
        this.title       = title;
        this.definition  = definition;
        this.coordinates = coordinates;
        this.treeDepth   = treeDepth;
        this.flags       = flags;
    }        
    
    public SchemeModel( String title, String conclusion )
    {
        this.conclusion = conclusion;
        this.title      = title;
    }

    public SchemeModel( List<Premise> premises, List<String> criticalQs,
                        String conclusion, String title )
    {
        this.premises   = premises;
        this.criticalQs = criticalQs;
        this.conclusion = conclusion;
        this.title      = title;
    }

    public List<Premise> getPremises()
    {
        return this.premises;
    }

    public List<String> getCriticalQs()
    {
        return this.criticalQs;
    }

    public void setCriticalQs( List<String> cq )
    {
        this.criticalQs = cq;
    }

    /**
     * Returns the actual saved text conclusion
     * @return 
     */
    public String getConclusion()
    {
        return conclusion;
    }

    public String getTitle()
    {
        return title;
    }

    public void clearPremise()
    {
        premises.clear();
    }

    public void addPremise( Premise s )
    {
        premises.add( s );
    }
    
    public void addConclusion( String s )
    {
        conclusions.add( s );
    }
    
    public Pair getCoordinates()
    {
        return this.coordinates;
    }
    
    public int getTreeDepth()
    {
        return this.treeDepth;
    }
    
    public void setTreeDepth( int t )
    {
        this.treeDepth = t;
    }
    
    public int getX()
    {
        try
        {
            return ( Integer ) this.coordinates.getKey();
        }
        catch( ClassCastException ex )
        {
            ex.printStackTrace();
        }
        
        return -1;
    }
    
    public int getY()
    {
        try
        {
            return ( Integer ) this.coordinates.getValue();
        }
        catch( ClassCastException ex )
        {
            ex.printStackTrace();
        }
        
        return -1;
    }
    
    /**
     * Returns the definition of the conclusion
     * @return 
     */
    public String getDefinition()
    {
        return this.definition;
    }
    
    public void setFlags( int[] flags )
    {
        this.flags = flags;
    }
    
    public boolean getCAFlag()
    {
        return this.flags[ 0 ] == 1;
    }
    
    public boolean getCQFlag()
    {
        return this.flags[ 0 ] == 3;
    }    
    
    public int getProConFlag()
    {
        return this.flags[ 1 ];
    }
}
