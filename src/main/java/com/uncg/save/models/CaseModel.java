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
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement( name = "caseStudy" )
public class CaseModel implements Serializable
{

    @XmlElement( name = "title" )
    private String      title;

    @XmlElement( name = "text" )
    private Paragraphs  paragraphs = new Paragraphs();

    @XmlElement( name = "metadata" )
    private Metadata    metadata   = new Metadata();

    public CaseModel()
    {
    }

    public CaseModel( String title, Paragraphs paragraphs, Metadata metadata )
    {
        this.title = title;
        this.paragraphs = paragraphs;
        this.metadata = metadata;
    }

    public Paragraphs getParagraphs()
    {
        return this.paragraphs;
    }

    public Metadata getMetadata()
    {
        return this.metadata;
    }

    public String getTitle()
    {
        return this.title;
    }

    public CaseModel getCaseModel()
    {
        return this;
    }

    @Override
    public String toString()
    {
        StringBuilder str = new StringBuilder();

        str.append( title ).append( "\n " );

        this.paragraphs.getParagraphs().forEach( ( paras ) ->
        {
            str.append( paras ).append( "\n" );
        } );

        this.metadata.getMetadata().forEach( ( data ) ->
        {
            str.append( data ).append( "\n" );
        } );

        return str.toString();
    }
}
    
@XmlRootElement( name="text" )
@XmlAccessorType(XmlAccessType.FIELD)
class Paragraphs
{

    @XmlElement( name="para")
    protected List<String> paragraphs;

    public List<String> getParagraphs()
    {
        return paragraphs;
    }

    public void setParagraphs( List<String> paragraphs )
    {
        this.paragraphs = paragraphs;
    }
    
    @Override
    public String toString()
    {
        StringBuilder paras = new StringBuilder();
        
        paragraphs.forEach( paras :: append );
        
        return paras.toString();
    }
}

@XmlRootElement( name="metadata" )
@XmlAccessorType(XmlAccessType.FIELD)
class Metadata
{

    @XmlElement( name="data")
    protected List<String> metadata;


    public void setMetadata( List<String> metadata )
    {
        this.metadata = metadata;
    }
 
    public List<String> getMetadata()
    {
        return metadata;
    }
    
    @Override
    public String toString()
    {
        StringBuilder metas = new StringBuilder();
        
        metadata.forEach( metas :: append );
        
        return metas.toString();
    }
}
