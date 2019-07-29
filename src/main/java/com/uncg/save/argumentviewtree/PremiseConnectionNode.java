/*
 * ===========================================================================
 * Software Engineering Ethics Debater (SWED) Source Code
 * Copyright (C) 2019 Nancy Green
 *
 * Software Engineering Ethics Debater (SWED) is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SWED Source Code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SWED Source Code.  If not, see <http://www.gnu.org/licenses/>.
 *
 * If you have questions concerning this license or the applicable additional
 * terms, you may contact Dr. Nancy Green at the University of North
 * Carolina at Greensboro.
 *
 * ===========================================================================
 */
package com.uncg.save.argumentviewtree;

import com.uncg.save.Draggable;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

/**
 * Class creates a node to be added to an ArgumentViewTree. Contains the
 * specifications for a rectangle which serves as a connector between premise
 * pane and a premise nexus
 */
public final class PremiseConnectionNode extends ArgumentNode implements Draggable
{

    //
    //
    //
    private final Line  line;

    //
    //
    //
    private boolean     selected = false;

    private double      transX;
    private double      transY;
    
    private final int   V_OFFSET = 10;

    /**
     * Constructs a new PremiseConnectionNode that details the specifications
     * for a rectangle that is the actual JavaFX Node used to draw the tree
     *
     * @param premiseNumber
     * @param target Point2D detailing the coordinates of the point where the
     * target line will be drawn.
     */
    public PremiseConnectionNode( int premiseNumber, Point2D target )
    {
        super();
        double rectHeight = 70;
        int vOffset       = premiseNumber * V_OFFSET;

        if ( premiseNumber == -1 )
        {
            this.line = new Line( target.getX(), target.getY() + PREMISE_HEIGHT / 2,
                                  target.getX() + PREMISE_WIDTH, target.getY() + PREMISE_HEIGHT / 2 );
        }
        else
        {
            this.line = new Line( target.getX(), target.getY(),
                                  target.getX(), target.getY() + rectHeight + vOffset );            
        }

        this.line.getStrokeDashArray().addAll( 7.0 );
        this.line.setStrokeWidth( 3 );

        this.getView().addEventFilter( MouseEvent.MOUSE_PRESSED, ( MouseEvent event ) ->
        {
            if ( selected )
            {
                this.transX = event.getSceneX();
                this.transY = event.getSceneY();
            }
        } );

        this.getView().addEventFilter( MouseEvent.MOUSE_DRAGGED, ( MouseEvent event ) ->
        {
            if ( selected ) 
            {
                this.onDragRelease( event );
                this.transX = event.getSceneX();
                this.transY = event.getSceneY();
            }
        } );
        
        this.getView().addEventFilter( MouseEvent.MOUSE_CLICKED, ( MouseEvent event ) -> 
        {
            if ( event.getButton() == MouseButton.SECONDARY )
            {                
                this.selected = !selected;
                this.line.setStroke( selected ? Color.GRAY : Color.BLACK );
            } 

        } );
    }

    public PremiseConnectionNode( Point2D target, boolean argument )
    {
        super();
        double rectWidth  = 0.0;
        double rectHeight = 0.0;

        // This is a little bit hacky, but it just allows for different angles
        // when creating counter argument premises
        if ( !argument )
        {
            rectWidth  = 3;
            rectHeight = 70;

            this.line = new Line( target.getX() + ( rectHeight + 20 ), target.getY(),
                                  target.getX() + ( rectHeight + 20 ), target.getY() + rectHeight );
        }
        else
        {
            rectWidth  = 30;
            rectHeight = 3;

            final int X_OFFSET = 100;
            final int Y_OFFSET = 75;

            this.line = new Line( target.getX() + ( X_OFFSET - 35 ), target.getY() - Y_OFFSET,
                                  target.getX() + rectWidth + X_OFFSET, target.getY() - Y_OFFSET );
        }

        this.line.getStrokeDashArray().addAll( 7.0 );
        this.line.setStrokeWidth( 3 );

        //
        //  Allows the user to reposition the premise connections if they jump
        //  around when connecting subtrees.
        //
        this.getView().addEventFilter( MouseEvent.MOUSE_PRESSED, ( MouseEvent event ) ->
        {
            if ( selected )
            {
                this.transX = event.getSceneX();
                this.transY = event.getSceneY();
            }
        } );

        this.getView().addEventFilter( MouseEvent.MOUSE_DRAGGED, ( MouseEvent event ) ->
        {
            if ( selected ) 
            {
                this.onDragRelease( event );
                this.transX = event.getSceneX();
                this.transY = event.getSceneY();
            }
        } );
        
        this.getView().addEventFilter( MouseEvent.MOUSE_CLICKED, ( MouseEvent event ) -> 
        {
            if ( event.getButton() == MouseButton.SECONDARY )
            {                
                this.selected = !selected;
                this.line.setStroke( selected ? Color.GRAY : Color.BLACK );
            } 

        } );
    }

    @Override
    public void onDragRelease( MouseEvent event )
    {
        double a = event.getSceneX() - this.transX;
        double b = event.getSceneY() - this.transY;

        this.line.setEndX( this.line.getEndX() + a );
        this.line.setEndY( this.line.getEndY() + b );
    }

    public boolean isSelected()
    {
        return selected;
    }

    public void setSelected( boolean s )
    {
        this.selected = s;
    }

    @Override
    public Node getView()
    {
        return line;
    }

    @Override
    public void setArgTree( ArgumentViewTree argTree )
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
