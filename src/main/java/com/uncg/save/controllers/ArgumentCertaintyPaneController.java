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

package com.uncg.save.controllers;

import com.uncg.save.models.PremiseModel;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;

public class ArgumentCertaintyPaneController implements Initializable
{
    //////////////////////// INSTANCE VARIABLES /////////////////////////////    
    
    //
    //
    //
    @FXML protected Pane             mainContainer;
    
    //
    //
    //
    @FXML private TextField          certaintyTextField;

    //
    //
    //
    private PremiseModel             controllingModel;
    
    //
    //
    //
    private ConclusionPaneController concControl;
    private PremisePaneController    premControl;
    
    //
    //
    //
    private double    certainty;
    protected boolean visible = false;

    //////////////////////// INSTANCE VARIABLES /////////////////////////////    
    
    /**
      * Controls the certainty panes attached to conclusion, chain, counter
      * argument, and critical question panes.
      * 
      * Associated view node: ArgumentCertaintyNode
      */
    @Override
    public void initialize( URL url, ResourceBundle rb )
    {
        certainty = 0.00;

        certaintyTextField.setTextFormatter( new CertaintyFormatter() );
        certaintyTextField.setStyle("-fx-text-fill: #000000; -fx-control-inner-background: #eaeaea;");

        certaintyTextField.setBackground( new Background(
                                          new BackgroundFill(
                                          new Color( 0.918, 0.918, 0.918, 1.0), 
                                          CornerRadii.EMPTY, Insets.EMPTY ) ) );
        
        certaintyTextField.setOnAction( ( ActionEvent event ) -> 
        {
            Double parsed;
            try
            {
                parsed = Double.parseDouble( certaintyTextField.getText() );
            }
            catch ( NumberFormatException ex )
            {
                parsed = -1.0;
            }
            
            if ( parsed < 0.00 || parsed > 1.00 )
            {
                certaintyTextField.setText( Double.toString( certainty ) );
            } else
            {
                certainty = parsed;
                controllingModel.setCertainty( parsed );
            }
            
            if ( concControl != null )
            {
                concControl.setViewColor( certainty );
                certaintyTextField.setStyle("-fx-text-fill: #ffffff;");
                certaintyTextField.setBackground( new Background(
                                                  new BackgroundFill(
                                                  new Color( 1 - certainty, 0.0, certainty, 1.0 ), 
                                                  CornerRadii.EMPTY, Insets.EMPTY ) ) );                    
            } 
            else
            {
                premControl.setViewColor( certainty );    
                certaintyTextField.setStyle("-fx-text-fill: #ffffff;");
                certaintyTextField.setBackground( new Background(
                                                  new BackgroundFill(
                                                  new Color( 1 - certainty, 0.0, certainty, 1.0 ), 
                                                  CornerRadii.EMPTY, Insets.EMPTY ) ) );                    
            }
        });

        mainContainer.setVisible( false );
    }

    public void setConcControl( ConclusionPaneController pControl )
    {
        this.concControl = pControl;
    }

    public ConclusionPaneController getConcControl()
    {
        return this.concControl;
    }

    public void setPremControl( PremisePaneController pControl )
    {
        this.premControl = pControl;
    }

    public PremisePaneController getPremControl()
    {
        return this.premControl;
    }

    public void setControllingModel( PremiseModel model )
    {
        this.controllingModel = model;
    }

    public void toggleVisible()
    {
        mainContainer.setVisible(  ! mainContainer.isVisible() );
        this.visible = !this.visible;
        
        if ( !this.visible )
        {
            if ( concControl != null )
            {
                this.concControl.propBoxController.text.setStyle( "-fx-background-color: #eaeaea; -fx-text-fill: black;");
            } 
            else if ( premControl != null )
            {
                this.premControl.propBoxController.text.setStyle( "-fx-background-color: #eaeaea; -fx-text-fill: black;");                
            }
        }
        
    }

    public void killVisibility()
    {
        mainContainer.setVisible( false );
    }

    /**
     Private inner class meant to be attached to the text field to limit valid
     inputs
     */
    private static class CertaintyFormatter extends TextFormatter<Double>
    {
        private static final double        DEFAULT_VALUE = 0.00;
        private static final DecimalFormat DEC_FORM      = new DecimalFormat( "#0.00" );

        public CertaintyFormatter()
        {
            super(
                    new StringConverter<Double>()
            {
                @Override
                public String toString( Double value )
                {
                    return DEC_FORM.format( value );
                }

                @Override
                public Double fromString( String string )
                {
                    try
                    {
                        return DEC_FORM.parse( string ).doubleValue();
                    } catch ( ParseException ex )
                    {
                        return Double.NaN;
                    }
                }
            },
                    DEFAULT_VALUE,
                    change
                    ->
            {
                try
                {
                    String text = change.getText();
                    if (  ! change.isContentChange() )
                    {
                        return change;
                    }
                    DEC_FORM.parse( change.getControlNewText() );
                    return change;
                } catch ( ParseException ex )
                {
                    return null;
                }
            }
            );
        }
    }
}
