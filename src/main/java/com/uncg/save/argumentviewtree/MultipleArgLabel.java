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
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public class MultipleArgLabel extends ArgumentNode
{

    private Label label;
    private ArgumentViewTree argTree;
    private Pane canvas;

    public MultipleArgLabel(
            Point2D target,
            ArgumentViewTree avt,
            Pane canvas )
    {
        label = new Label();
        //label.setText( "Supported by multiple arguments" );
        this.canvas = canvas;
        argTree = avt;
        label.setLayoutX( target.getX() + 5 );
        label.setLayoutY( target.getY() + 120 / 2 );
    }

    @Override
    public Node getView()
    {
        return label;
    }

    @Override
    public void setArgTree( ArgumentViewTree argTree )
    {
        this.argTree = argTree;
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
