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

import com.uncg.save.MainApp;
import com.uncg.save.controllers.CqPickerController;
import com.uncg.save.models.ArgumentModel;
import com.uncg.save.util.AlertStage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ArgumentSchemeLabel extends ArgumentNode
{
    //
    //
    //
    private Pane             canvas        = null;
    
    //
    //
    //
    private ArgumentViewTree argTree       = null;
    
    //
    //
    //
    private ContextMenu      contextMenu   = new ContextMenu();
    
    //
    //
    //
    private Point2D         contextCoords  = null;
    private TextField       argSchemeLabel = null;
    private MenuItem        addCQ          = null;
    
    //
    //
    //
    private ArgumentModel    scheme        = null;

    public ArgumentSchemeLabel( ArgumentModel scheme, Point2D target, ArgumentViewTree avt, Pane canvas )
    {
        this.scheme  = scheme;
        this.canvas  = canvas;
        this.argTree = avt;
        
        this.setContextMenuItems();

        this.argSchemeLabel = new TextField( scheme.getTitle() );
        this.argSchemeLabel.getStyleClass().add( "text-field-label" );
        this.argSchemeLabel.setAlignment( Pos.CENTER_LEFT );
        this.argSchemeLabel.setPrefWidth( 400 );
        this.argSchemeLabel.setTooltip( new Tooltip( "Right Click to add Critical Questions" ) );
        
        this.argSchemeLabel.setLayoutX( target.getX() + 5 );
        this.argSchemeLabel.setLayoutY( target.getY() + 12 );
        this.argSchemeLabel.addEventFilter( ContextMenuEvent.CONTEXT_MENU_REQUESTED, ( ContextMenuEvent event ) ->
        {
            showContextMenu( event );
            event.consume();
        } );

        this.argSchemeLabel.setStyle("-fx-font-size: 16; -fx-background-color: transparent; -fx-padding: 0 0 0 0;");
        this.argTree.getRoot().getControl().setSchemeTitleLabel( this );
    }

    @Override
    public Node getView()
    {
        return argSchemeLabel;
    }
    
    public String getSchemeLabelText()
    {
        return this.argSchemeLabel.getText();
    }

    @Override
    public void setArgTree( ArgumentViewTree argTree )
    {
        this.argTree = argTree;
    }
    
    private void setContextMenuItems() {
        addCQ = new MenuItem( "Add Critical Question" );
        setHandlerForAddCQ( addCQ );
        contextMenu.getItems().addAll( addCQ );
}

    private void showContextMenu( ContextMenuEvent event )
    {
        /*
         call to hide() ensures that bugs arent encountered if multiple context
         menus are opened back to back
         */
        contextMenu.hide();
        contextMenu.show( argSchemeLabel, event.getScreenX(), event.getScreenY() );
        contextCoords = new Point2D( event.getScreenX(), event.getScreenY() );
        event.consume();
    }

    private void setHandlerForAddCQ( MenuItem item )
    {
        item.setOnAction( action ->
        {
            try
            {
                displayCQPicker( item );
                action.consume();
            } catch ( IOException ex )
            {
                Logger.getLogger( ArgumentSchemeLabel.class.getName() ).log( Level.SEVERE, null, ex );
            }
        } );
    }

    public Point2D getCoordinates()
    {
        Point2D layout = new Point2D( ( int ) ( argSchemeLabel.getLayoutX() ),
                                      ( int ) ( argSchemeLabel.getLayoutY() ) );
        return layout;
    }

    public void displayCQPicker( MenuItem item ) throws IOException
    {
        
        if ( scheme.getPatchCriticalQuestion( 0 ).equalsIgnoreCase( "NO CQs" ) )
        {
            AlertStage alert = new AlertStage( Alert.AlertType.INFORMATION, "No critical questions for this scheme.",
                                              MainApp.MainStage );
            return;
        }
        FXMLLoader loader = new FXMLLoader( getClass().getResource( "/fxml/CqPicker.fxml" ) );
        Parent cqPickerBox = loader.load();
        CqPickerController cqPickerController = loader.<CqPickerController>getController();
        cqPickerController.setArgModel( scheme );
        cqPickerController.setAVT( argTree );
        cqPickerController.setASL( this );
        cqPickerController.populateCQs( item );
        Scene scene = new Scene( cqPickerBox );
        Stage stage = new Stage();
        stage.initOwner( canvas.getScene().getWindow() );
        stage.setTitle( "Choose Critical Question" );
        stage.setScene( scene );
        stage.initModality( Modality.APPLICATION_MODAL );
        stage.resizableProperty().setValue( false );
        stage.getIcons().add( MainApp.icon );
        stage.showAndWait();
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