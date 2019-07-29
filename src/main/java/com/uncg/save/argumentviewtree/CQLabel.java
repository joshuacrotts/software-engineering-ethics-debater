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
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public class CQLabel extends ArgumentNode
{
    //////////////////////// INSTANCE VARIABLES /////////////////////////////
    
    //
    //
    //
    private final Label             CQLabel;
    private final Pane              canvas;
    private       Point2D           contextCoords;
    private       ArgumentViewTree  argTree;
    private       ContextMenu       contextMenu;
    
    
    //////////////////////// INSTANCE VARIABLES /////////////////////////////

    public CQLabel( String cq, Point2D target, ArgumentViewTree avt, Pane canvas, ArgumentNode connector )
    {
        this.CQLabel = new Label();
        this.CQLabel.setMaxWidth( PREMISE_WIDTH );
        this.CQLabel.setWrapText( true );
        this.CQLabel.setText( cq );
        this.canvas  = canvas;
        this.argTree = avt;
        this.CQLabel.setLayoutX( ( int ) ( target.getX() - PREMISE_WIDTH ) );
        this.CQLabel.setLayoutY( ( int ) ( target.getY() + PREMISE_HEIGHT ) );
    }

    @Override
    public Node getView()
    {
        return CQLabel;
    }

    @Override
    public void setArgTree( ArgumentViewTree argTree )
    {
        this.argTree = argTree;
    }

    public Point2D getCoordinates()
    {
        Point2D layout = new Point2D( ( int ) ( CQLabel.getLayoutX() ),
                                      ( int ) ( CQLabel.getLayoutY() ) );
        return layout;
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
