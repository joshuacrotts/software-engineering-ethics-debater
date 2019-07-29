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

package com.uncg.save.argumentviewtree;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;

/**
 Class creates a node to be added to an ArgumentViewTree. Contains the
 specifications for a rectangle which serves as a connector between premise pane
 and a premise nexus
 */
public class CounterArgumentConnectionNode extends ArgumentNode
{

    private Rectangle   rect;
    private int         defaultLength = 100;

    /**
     Constructs a new PremiseConnectionNode that details the specifications for
     a rectangle that is the actual JavaFX Node used to draw the tree

     @param target Point2D detailing the coordinates the rectangle will be drawn
     at. Calculations are done to offset this value and accommodate the
     dimensions of the rectangle to make sure everything is centered.
     */
    public CounterArgumentConnectionNode( Point2D target )
    {
        super();
        double rectWidth  = defaultLength;
        double rectHeight = 3;
        this.rect         = new Rectangle( target.getX(), target.getY(),
                                           rectWidth    , rectHeight );
    }

    @Override
    public Node getView()
    {
        return rect;
    }

    @Override
    public int getWidth()
    {
        return ( int ) rect.getWidth();
    }

    public void addLength( int i )
    {
        rect.setWidth( rect.getWidth() + i );
    }

    public void resizeToDefaultWidth()
    {
        rect.setWidth( defaultLength );
    }

    @Override
    public void setArgTree( ArgumentViewTree argTree )
    {
    }

    public void remove()
    {
    }

    @Override
    public void moveComment( double x, double y )
    {
    }

    @Override
    public void deleteCommentPane()
    {
    }

}
