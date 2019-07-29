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

import static com.uncg.save.MainApp.schemeModelDataFormat;
import com.uncg.save.models.SchemeModel;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

/*
============================================================
FXML controller for view elements meant to represent individual 
argument schemes
============================================================
*/
public class SchemePaneController implements Initializable
{

    //////////////////////// INSTANCE VARIABLES /////////////////////////////        
    
    //
    //  Booleans determining the collapsed/expanded state of 
    //  scheme/examples/critical questions within the 
    //  scheme panel.
    //
    protected boolean           schemeCollapsed   = true;
    protected boolean           exampleCollapsed  = true;
    protected boolean           cqCollapsed       = true;
    protected double            anchorPaneHeight  = 0.0;
    protected int               textSpace         = 0;

    //
    //  Panes
    //
    @FXML protected AnchorPane  superAnchorPane;
    @FXML protected AnchorPane  childAnchorPane;
    @FXML private   TitledPane  superTitledPane;    
    @FXML private   TitledPane  criticalQuestionsTitledPane;
    
    //
    //  Scheme List controller
    //
    private SchemeListController slc;

    //
    //
    //
    @FXML private   VBox         dataVBox;
    @FXML protected TextArea     criticalQuestionLabel;
    @FXML protected TextArea     premiseLabel;
    
    
    //
    //
    //
    private SchemeModel          scheme        = new SchemeModel();
    private int                  exampleHeight = 0;
    private int                  cqHeight      = 0;
    private boolean              heightsCalc   = false;
    
    //////////////////////// INSTANCE VARIABLES /////////////////////////////        

    @Override
    public void initialize( URL url, ResourceBundle rb )
    {
        initializeLabels();
        superAnchorPane.setPrefHeight( 40 );
        superTitledPane.getStyleClass().add( "titled-pane" );
        superAnchorPane.getStyleClass().add( "anchor-pane" );
        childAnchorPane.getStyleClass().add( "anchor-pane" );
        childAnchorPane.getStyleClass().add( "scheme-data-pane" );
        dataVBox.getStyleClass().add( "scheme-data-vbox" );
        premiseLabel.getStyleClass().add( "premise-text-field-label" );  //Modify this
        criticalQuestionLabel.getStyleClass().add( "internal-data-label" );
        criticalQuestionsTitledPane.getStyleClass().add( "internal-titled-pane" );
        criticalQuestionLabel.getStyleClass().add( "label" );
        premiseLabel.setEditable( false );
    }

    public TextArea getPremisesTextArea()
    {
        return this.premiseLabel;
    }

    public void setPremisesTextArea( String premises )
    {
        this.premiseLabel.setText( premises + "Conclusion:\n" + this.scheme.getConclusion() + "\n" + " " );
    }

    public TextArea getCriticalQuestionsTextArea()
    {
        return this.criticalQuestionLabel;
    }

    public void setCriticalQuestionsTextArea( String criticalQuestions )
    {
        this.criticalQuestionLabel.setText( criticalQuestions );
    }

    public void setScheme( SchemeModel sm )
    {
        this.scheme = sm;
    }

    public void setTitle()
    {
        superTitledPane.setText( this.scheme.getTitle() );
    }

    public void setParentController( SchemeListController slc )
    {
        this.slc = slc;
    }

    private void initializeLabels()
    {
        premiseLabel.setWrapText( true );
        criticalQuestionLabel.setWrapText( true );
        premiseLabel.setMinWidth( 292 );
        criticalQuestionLabel.setMinWidth( 288 );
        premiseLabel.setMaxWidth( 292 );
        criticalQuestionLabel.setMaxWidth( 288 );
        premiseLabel.setFont( new Font( "System Regular", 18 ) );
        criticalQuestionLabel.setFont( new Font( "System Regular", 18 ) );
    }

    @FXML
    private void dragDetected()
    {
        Dragboard db = superTitledPane.startDragAndDrop( TransferMode.ANY );
        ClipboardContent content = new ClipboardContent();
        content.put( schemeModelDataFormat, this.scheme );
        db.setContent( content );
    }

    @FXML
    private void dragDone( DragEvent event )
    {
        event.consume();
    }

    @FXML
    protected void expandScheme()
    {
        if (  ! heightsCalc )
        {
            criticalQuestionsTitledPane.setExpanded( true );
            cqHeight      = ( int ) criticalQuestionLabel.getHeight();
            heightsCalc   = true;
        }
        criticalQuestionsTitledPane.setExpanded( false );
        exampleCollapsed = true;
        cqCollapsed      = true;
        if ( schemeCollapsed )
        {
            superAnchorPane.setPrefHeight( premiseLabel.getHeight() + 176 );
            schemeCollapsed =  ! schemeCollapsed;
            superTitledPane.setExpanded( true );
            slc.getMainScrollPane().layout();
        } else
        {
            superAnchorPane.setPrefHeight( 40 );
            schemeCollapsed =  ! schemeCollapsed;
            superTitledPane.setExpanded( false );
            slc.getMainScrollPane().layout();
        }
    }
    
    @FXML
    protected void expandCQs()
    {
        if ( cqCollapsed )
        {
            superAnchorPane.setPrefHeight( superAnchorPane.getHeight() + cqHeight );
            cqCollapsed =  ! cqCollapsed;
            criticalQuestionsTitledPane.setExpanded( true );
        } else
        {
            superAnchorPane.setPrefHeight( superAnchorPane.getHeight() - cqHeight );
            cqCollapsed =  ! cqCollapsed;
            criticalQuestionsTitledPane.setExpanded( false );
        }
    }

    @FXML
    private void expandExample()
    {
        if ( exampleCollapsed )
        {
            superAnchorPane.setPrefHeight( superAnchorPane.getHeight() + exampleHeight );
            exampleCollapsed =  ! exampleCollapsed;
        } else
        {
            superAnchorPane.setPrefHeight( superAnchorPane.getHeight() - exampleHeight );
            exampleCollapsed =  ! exampleCollapsed;
        }
    }
}
