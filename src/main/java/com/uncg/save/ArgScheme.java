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

import com.uncg.save.models.SchemeModel;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement( name = "argScheme", namespace = "com.uncg.save" )
public class ArgScheme
{
    private Premises    premise;
    private Examples    example;
    private CQs         cq;
    
    private String      title;
    private String      conclusion;

    public ArgScheme()
    {
    }

    public ArgScheme( String title, Premises dat, Examples example, String conc, CQs dat2 )
    {
        super();
        this.title = title;
        this.premise = dat;
        this.example = example;
        this.conclusion = conc;
        this.cq = dat2;
    }

    public SchemeModel getSchemeModel()
    {
        if ( this.cq.getCQ().isEmpty() )
        {
            this.cq.getCQ().add( "NO CQs" );
        }
        return new SchemeModel(
                this.premise.getPremise(),
                this.cq.getCQ(),
                this.conclusion,
                this.title );
    }
    
    public String getTitle()
    {
        return this.title;
    }

    public void setTitle( String title )
    {
        this.title = title;
    }

    public Premises getPremises()
    {
        return this.premise;
    }

    public void setPremises( Premises premises )
    {
        this.premise = premises;
    }

    public Examples getExamples()
    {
        return this.example;
    }

    public void setExamples( Examples examples )
    {
        this.example = examples;
    }

    public String getConclusion()
    {
        return conclusion;
    }

    public void setConclusion( String c )
    {
        this.conclusion = c;
    }

    public CQs getCQs()
    {
        return cq;
    }

    public void setCQs( CQs datas2 )
    {
        this.cq = datas2;
    }
}

class Premises 
{
    @XmlElement( name = "premises" )
    protected List<Premise> premises;

    public List<Premise> getPremise()
    {
        return premises;
    }

    public void setPremise( List<Premise> premises )
    {
        this.premises = premises;
    }
}
class CQs
{
    private List<String> CQ = new ArrayList();

    public List<String> getCQ()
    {
        return CQ;
    }

    public void setCQ( List<String> cq )
    {
        this.CQ = cq;
    }
}

class Examples
{
    private List<String> example = new ArrayList();

    public List<String> getExample()
    {
        return example;
    }

    public void setExample( List<String> example )
    {
        this.example = example;
    }
}
