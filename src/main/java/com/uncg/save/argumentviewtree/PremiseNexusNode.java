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

import java.util.Optional;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Pair;

/**
 Class creates a node to be added to an ArgumentViewTree. Contains the
 specifications for a rectangle which serves as the horizontal line connecting
 the premise and conclusion sections of an argument.

 TODO: this rectangle can be used to control spacing/repositioning of arguments
 when we start to worry about view collisions
 */
public class PremiseNexusNode extends ArgumentNode
{
     protected final Rectangle  rect;
     protected boolean          multiArg = false;
     protected ArgumentViewTree avt;
    /**
     Constructs a new ConclusionConnectionNode that details the specifications
     for a rectangle that is the actual JavaFX Node used to draw the tree

     @param numPremises
     @param target Point2D detailing the coordinates the rectangle will be drawn
     at. Calculations are done to offset this value and accommodate the
     dimensions of the rectangle to make sure everything is centered.
     */
    public PremiseNexusNode( int numPremises, Point2D target )
    {
        super();
        double rectWidth  = ( ( numPremises - 1 ) * ( PREMISE_WIDTH + PADDING) ) + 3;
        double rectHeight = 3;
        this.rect         = new Rectangle( target.getX() - rectWidth / 2, target.getY(),
                                           rectWidth, rectHeight );
        this.multiArg     = false;

    }
    
    public PremiseNexusNode( int numPremises, Point2D target, boolean isCounter )
    {
        super();
        
        double rectWidth  = ( ( numPremises - 1 ) * ( PREMISE_WIDTH + PADDING ) ) + 5;
        double rectHeight = 5;
        this.rect         = new Rectangle( target.getX() - rectWidth / 2, target.getY(),
                                           rectWidth, rectHeight );
        this.multiArg     = false;
    }
    
    public PremiseNexusNode()
    {
        this.rect = new Rectangle();
    }

    @Override
    public Node getView()
    {
        return rect;
    }

    @Override
    public void setArgTree( ArgumentViewTree argTree )
    {
        this.avt = argTree;
        
    }
    
    public void setHandlerForNexus()
    {

            //Creates the Dialog object for two integer fields
            //width and height
            Dialog<Pair<String, String>> dialog = new Dialog<>();
            dialog.setHeaderText( "Input Your new nexus " );
            dialog.setTitle( "Resize Pane " );

            ButtonType changeButton = new ButtonType( "Change", ButtonBar.ButtonData.OK_DONE );
            dialog.getDialogPane().getButtonTypes().addAll( changeButton, ButtonType.CANCEL );

            //Creates a grid pane to set up placeholders for the buttons 
            //and text fields
            GridPane grid = new GridPane();
            grid.setHgap( 10 );
            grid.setVgap( 10 );
            grid.setPadding( new Insets( 20, 150, 10, 10 ) );

            //Creates the text fields
            TextField widthField = new TextField();
            widthField.setPromptText( "Width" );

            //Adds them to the GridPane
            grid.add( new Label( "Width:" ), 0, 0 );
            grid.add( widthField, 1, 0 );
            
            dialog.getDialogPane().setContent( grid );

            //Sets up a lambda to grab the dimensions
            //if they are present.
            dialog.setResultConverter( dialogButton ->
            {
                if ( dialogButton == changeButton )
                {
                    return new Pair<>( widthField.getText(), "" );
                }

                return null;
            } );

            Optional<Pair<String, String>> _resizedPair = dialog.showAndWait();

            _resizedPair.ifPresent( resizedPair ->
            {
                int w = 0, h = 0;

                try
                {
                    w = Integer.parseInt( resizedPair.getKey().isEmpty() ? "325" : resizedPair.getKey() );
                }
                catch ( NumberFormatException | ClassCastException ex )
                {
                    ex.printStackTrace();
                }

                        this.avt.nexusMatrixAdjustment( w );
            } );
            
    }

    public void growWidth( double length )
    {
        rect.setWidth( rect.getWidth() + length );
    }

    public void resizeToDefaultWidth()
    {
        rect.setWidth(
                ( children.size() - 1 )
                * ( PREMISE_WIDTH + PADDING )
        );
    }

    @Override
    public void moveComment( double x, double y )
    {
    }

    @Override
    public void deleteCommentPane()
    {
    }

    @Override
    public int getWidth()
    {
        return ( int ) rect.getWidth();
    }

}
