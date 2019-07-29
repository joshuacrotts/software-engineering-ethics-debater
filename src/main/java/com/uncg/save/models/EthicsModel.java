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
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement( name = "ethics" )
public class EthicsModel
{
    
    @XmlElement( name = "title" )
    private String title;
    
    @XmlElement( name = "section" )
    private List<EthicsSection> ethicsSections = new ArrayList<>();
    
    public EthicsModel()
    {
    }
    
    public EthicsModel( String title, List<EthicsSection> ethicsSections )
    {
        this.title = title;
        this.ethicsSections = ethicsSections;
    }
    
    public String getTitle()
    {
        return this.title;
    }
    
    public List<EthicsSection> getEthicsSections()
    {
        return this.ethicsSections;
    }
    
    @Override
    public String toString()
    {
        StringBuilder s = new StringBuilder( this.title );
        
        ethicsSections.forEach( ( str ) ->
        {
            s.append( str ).append( "\n\n\n" );
        } );
        
        return s.toString();
    }
    
}

@XmlRootElement( name = "section" )
@XmlAccessorType( XmlAccessType.FIELD )
class EthicsSection
{
    
    @XmlElement( name = "sectiontitle" )
    private String sectionTitle;
    
    @XmlElement( name = "paragraph" )
    private List<String> paragraphs;
    
    public List<String> getParagraphs()
    {
        return this.paragraphs;
    }
    
    public void setParagraphs( List<String> paragraphs )
    {
        this.paragraphs = paragraphs;
    }
    
    @Override
    public String toString()
    {
        StringBuilder s = new StringBuilder( sectionTitle ).append( "\n" );
        
        paragraphs.forEach( ( str ) ->
        {
            s.append( str ).append( "\n\n" );
        } );
        
        return s.toString();
    }
    
}
