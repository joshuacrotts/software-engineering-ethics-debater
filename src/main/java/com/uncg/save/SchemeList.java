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

@XmlRootElement( name = "schemeList" )
public class SchemeList
{
    protected List<ArgScheme> argSchemes;

    @XmlElement( name = "argScheme" )
    public List<ArgScheme> getArgScheme()
    {
        return this.argSchemes;
    }

    public void setArgScheme( List<ArgScheme> as )
    {
        this.argSchemes = as;
    }

    public List<SchemeModel> getModels()
    {
        List<SchemeModel> schemeModelList = new ArrayList();
        for ( int i = 0; i < argSchemes.size(); i ++ )
        {
            schemeModelList.add( argSchemes.get( i ).getSchemeModel() );
        }
        return schemeModelList;
    }
}
