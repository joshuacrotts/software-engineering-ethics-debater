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

public class CounterArgumentModel extends ArgumentModel
{

    private List<PremiseModel> parentModelList;

    public CounterArgumentModel()
    {
        super();
        this.parentModelList = new ArrayList<>();
    }

    public CounterArgumentModel( ArgumentModel argModel )
    {
        this.parentModelList = new ArrayList<>();
        this.scheme = argModel.scheme;

        this.conclusion = argModel.getConclusion();
        premises = argModel.premises;

        criticalQuestions = argModel.criticalQuestions;
        cq = argModel.cq;
    }

    public void addToParentModelList( PremiseModel model )
    {
        if (  ! parentModelList.contains( model ) )
        {
            parentModelList.add( model );
        }
    }

    public void removeFromParentModelList( PremiseModel model )
    {
        parentModelList.remove( model );
    }

    public void setParentModelList( List<PremiseModel> newList )
    {
        parentModelList.clear();
        parentModelList.addAll( newList );
    }

    public List<PremiseModel> getParentModelList()
    {
        return parentModelList;
    }

    @Override
    public String toString()
    {
        StringBuilder s = new StringBuilder();

        for ( int i = 0; i < parentModelList.size(); i ++ )
        {
            s.append( parentModelList.get( i ) );
        }

        return s.toString();
    }
}
