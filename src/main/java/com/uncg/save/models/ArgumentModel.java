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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ArgumentModel
{
    protected SchemeModel               scheme;
    protected PremiseModel              conclusion;
    protected PremiseModel[]            premises;
    protected List<ArgumentModel>       cqArguments;
    protected HashMap<Integer, String>  criticalQuestions;
    
    protected boolean cq;

    public ArgumentModel()
    {
        this.scheme            = new SchemeModel();
        this.conclusion        = new PremiseModel();
        this.premises          = new PremiseModel[ 1 ];
        this.cqArguments       = new ArrayList<>();
        this.criticalQuestions = new HashMap<>();
        
        this.conclusion.addAsConclusionForArgument( this );

        for ( int i = 0; i < premises.length; i ++ )
        {
            PremiseModel premise = new PremiseModel();
            premise.addAsPremiseForArgument( this );
            premises[i] = premise;
        }
        
        cq = false;
    }

    public ArgumentModel( SchemeModel scheme )
    {
        this.scheme = scheme;
        this.cqArguments = new ArrayList<>();

        this.conclusion = new PremiseModel();
        this.conclusion.addAsConclusionForArgument( this );

        premises = new PremiseModel[scheme.getPremises().size()];
        for ( int i = 0; i < premises.length; i ++ )
        {
            PremiseModel premise = new PremiseModel();
            premise.addAsPremiseForArgument( this );
            premises[i] = premise;
        }
        criticalQuestions = new HashMap<>();
        cq = false;
    }

    public ArgumentModel( CounterArgumentModel counterArgument )
    {
        this.scheme = counterArgument.scheme;
        this.cqArguments = new ArrayList<>();

        this.conclusion = counterArgument.getConclusion();
        premises = counterArgument.premises;

        criticalQuestions = counterArgument.criticalQuestions;
        cq = counterArgument.cq;
    }


    public void setConclusion( PropositionModel conclusion )
    {
        this.conclusion.setProposition( conclusion );
    }

    public void removeConclusion()
    {
        conclusion.removeProposition();
    }

    public void addPremise( PropositionModel prop, int position )
    {
        premises[position].setProposition( prop );
    }

    public void removePremise( int position )
    {
        if ( containsPremise( position ) )
        {
            premises[position].removeProposition();
        }
    }

    public void removePremise( PropositionModel prop )
    {
        for ( int i = 0; i < premises.length; i ++ )
        {
            if ( premises[i] != null && premises[i].equals( prop ) )
            {
                premises[i] = null;
            }
        }
    }

    public int getPatchNumCQs()
    {
        return this.scheme.getCriticalQs().size();
    }

    public String getPatchCriticalQuestion( int i )
    {
        return this.scheme.getCriticalQs().get( i );
    }
    
    public String getCQs()
    {
        return this.scheme.getCriticalQs().stream().map( Object::toString ).collect( Collectors.joining(";") );
    }
    
    /**
     * Returns the definition of the conclusion.
     * @return 
     */
    public String getSchemeDefinition()
    {
        return this.scheme.getDefinition();
    }

    public String getSchemeConclusion()
    {
        return scheme.getConclusion();
    }
    
    /**
     * Returns a string representation of the scheme at 
     * position premiseNumber. 
     * 
     * In short, it just takes the name (title), puts a colon in between,
     * and then concatenates the definition. This is primarily useful
     * when spawning in the schemes, where both the definition and 
     * title need to be visible until the user clicks the pane.
     * 
     * @param premiseNumber
     * @return string representation of name and definition
     */
    public String getSchemePremise( int premiseNumber )
    {
        return scheme.getPremises().get( premiseNumber ).toString();
    }
    
    /**
     * Returns the name (title) of the premise at position premiseNumber
     * @param premiseNumber
     * @return 
     */
    public String getSchemePremiseName( int premiseNumber )
    {
        return scheme.getPremises().get( premiseNumber ).getName();
    }
    
    /**
     * Returns the definition of the premise at position premiseNumber
     * @param premiseNumber
     * @return 
     */
    public String getSchemePremiseDefinition( int premiseNumber )
    {
        return scheme.getPremises().get( premiseNumber ).getDefinition();
    }    
    
    public String getSchemePremiseText( int premiseNumber )
    {
        return scheme.getPremises().get( premiseNumber ).getText();
    }

    /**
     * @return the number of premises for this current scheme
     */
    public int getSchemeNumPremises()
    {
        return scheme.getPremises().size();
    }

    public String getSchemeTitle()
    {
        return scheme.getTitle();
    }

    public void addCQArgument( ArgumentModel arg )
    {
        cqArguments.add( arg );
    }

    public void removeCQArgument( ArgumentModel arg )
    {
        cqArguments.remove( arg );
    }

    public boolean hasCQ()
    {
        return cq;
    }

    public void setCQ( boolean value )
    {
        cq = value;
    }

    public String getTitle()
    {
        return scheme.getTitle();
    }

    public boolean containsPremise( int position )
    {
        return premises[position].getProposition() != null;
    }

    public PremiseModel getPremise( int position )
    {
        return premises[position];
    }

    public String getCriticalQuestion( int key )
    {
        return criticalQuestions.get( key );
    }

    public PremiseModel getConclusion()
    {
        return conclusion;
    }    

    public String[] toArray()
    {
        throw new UnsupportedOperationException( "Cannot convert ArgumentModel into array." );
    }
}
