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

import com.uncg.save.Draggable;
import static com.uncg.save.MainApp.propositionModelDataFormat;
import com.uncg.save.models.ArgumentModel;
import com.uncg.save.models.PremiseModel;
import com.uncg.save.models.PropositionModel;
import com.uncg.save.util.LayoutUtils;
import com.uncg.save.util.MouseUtils;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

/**
 FXML controller for FXML controller for conclusion of
 multiple-arguments-sharing-the-same-conclusion structure that are sub-arguments
 (chained).

 */
public class MultiArgChainPaneController extends ChainPaneController implements Initializable, Draggable
{
    @Override
    public void initialize( URL url, ResourceBundle rb )
    {
        this.premiseArgList    = new ArrayList<>();
        this.conclusionArgList = new ArrayList<>();
        this.mainPane.getStyleClass().add( "chain-pane" );
        this.propositionRectangle.getStyleClass().add( "chain-rectangle" );

        //
        // MOUSE ENTERED EVENT
        //
        this.mainPane.addEventFilter( MouseEvent.MOUSE_ENTERED, ( MouseEvent event ) -> 
        {
            this.parentControl.setConclusionPaneController( this );
        } );
        
        // 
        //  MOUSE DROPPED EVENT
        //
        this.mainPane.addEventFilter( DragEvent.DRAG_DROPPED, ( DragEvent event ) ->
        {
            Dragboard db = event.getDragboard();
            if ( db.hasContent( propositionModelDataFormat ) || db.hasString() )
            {
                try
                {
                    onDragDropped( event );
                    event.consume();
                } 
                catch ( IOException ex )
                {
                    Logger.getLogger( PremisePaneController.class.getName() ).log( Level.SEVERE, null, ex );
                }
            }
        } );
        
        //
        //  MOUSE PRESSED EVENT
        //
        this.mainPane.addEventFilter( MouseEvent.MOUSE_PRESSED, ( MouseEvent event ) ->
        {
            super.x = event.getSceneX();
            super.y = event.getSceneY();
        });        
        
        //
        //  MOUSE DRAGGED EVENT
        //
        this.mainPane.addEventFilter( MouseEvent.MOUSE_DRAGGED, ( MouseEvent event ) ->
        {
            if ( this.propBoxController != null && !this.propBoxController.text.getSelectedText().isEmpty() )
            {
                return;
            }
            
            super.onDragRelease( event );
            super.x = event.getSceneX();
            super.y = event.getSceneY();
        });  

        // intercept drag detected events so props are not draggable
        this.mainPane.addEventFilter( MouseEvent.DRAG_DETECTED, ( MouseEvent event ) ->
        {
            event.consume();
        } );

        // create context menu for adding new premises
        this.contextMenu = new ContextMenu();
        this.setContextMenuItems();
        this.setContextMenuEventFilter();
        
    }

    /**
     set menu items for context menu
     * 
     * 7/11/19 - REMOVED DETACH ARG
     */
    private void setContextMenuItems()
    {
        MenuItem addCounterArg = new MenuItem( "Add Counter Argument"  );
        MenuItem detachChain   = new MenuItem( "Detach Argument Chain" );
        MenuItem copy          = new MenuItem( "Copy (CTRL+C)"         );
        MenuItem paste         = new MenuItem( "Paste (CTRL+V)"        );
            detachChain.setDisable( true );
            addCounterArg.setDisable( true );            

        this.setHandlerForCounterArg ( addCounterArg  );
        this.setHandlerForDetachChain( detachChain     );
        this.setHandlerForCopy       ( copy            );
        this.setHandlerForPaste      ( paste           );        
        this.contextMenu.getItems().addAll( addCounterArg, detachChain, copy , paste );
    }
    private void setHandlerForCounterArg( MenuItem item )
    {
        item.setOnAction( action ->
        {
            try
            {
                List<PremiseModel> conclusionModels = new ArrayList<>();
                this.conclusionArgList.forEach( ( arg ) -> 
                {
                    conclusionModels.add( arg.getConclusion() );
                } );
                this.argTree.addCounterArgument( conclusionModels, argNode );
                this.argNode.caFlag = true;
                //item.setDisable( this.argNode.caFlag );
            } 
            catch ( IOException ex )
            {
                Logger.getLogger( ConclusionPaneController.class.getName() ).log( Level.SEVERE, null, ex );
            }
        } );
    }
    

   /**
     * Configures the menuitem copy event handler -- 
     * grabs the text from the proposition box controller text area,
     * and stores it in the "clipboard".
     * 
     * @param copy 
     */
    private void setHandlerForCopy( MenuItem copy )
    {
        copy.setOnAction( action ->
        {
            if ( this.propBoxController != null && this.propBoxController.text != null )
            {
                MouseUtils.ClipBoard = this.propBoxController.text.getSelectedText();
            }
        } );
    }
    
    /**
     * Configures the menuitem paste event handler -- 
     * grabs the text from the clipboard and inserts it into the selected 
     * text area wherever the user's cursor is located.
     * @param paste 
     */
    private void setHandlerForPaste( MenuItem paste )
    {
        paste.setOnAction( action ->
        {
            if ( this.propBoxController != null && this.propBoxController.text != null &&
                 MouseUtils.hasContent() )
            {
                this.propBoxController.text.insertText( this.propBoxController.text.getCaretPosition(), 
                                                        MouseUtils.ClipBoard );      
            }
        } );
    }        

    private void setHandlerForDetachChain( MenuItem item )
    {
        item.setOnAction( action ->
        {
            Point2D localCoords = LayoutUtils.getLocalCoords( parentControl.getMainPane(),
                                                              contextCoords.getX(),
                                                              contextCoords.getY() );
            this.argTree.detachArgumentChain( this, localCoords );
            action.consume();
        } );
    }

    /**
     add context menu event handler for mouse clicks
     */
    private void setContextMenuEventFilter()
    {
        this.mainPane.addEventFilter( ContextMenuEvent.CONTEXT_MENU_REQUESTED, ( ContextMenuEvent event ) ->
        {
            if ( !this.argNode.caFlag )
            {
                this.contextMenu.getItems().get( 0 ).setDisable( false );
            }
            this.showContextMenu( event );
            event.consume();
        } );
    }

    /**
     shows the context menu using the X and Y of the event to determine location

     @param event MouseEvent
     */
    private void showContextMenu( ContextMenuEvent event )
    {
        /*
         call to hide() ensures that bugs arent encountered if multiple context
         menus are opened back to back
         */
        this.contextMenu.hide();
        this.contextMenu.show( mainPane, event.getScreenX(), event.getScreenY() );
        this.contextCoords = new Point2D( event.getScreenX(), event.getScreenY() );
        event.consume();
    }

    /**
     hides the context menu
     */
    private void closeContextMenu( Event event )
    {
        this.contextMenu.hide();
        event.consume();
    }

    @Override
    public void addConclusionArgumentModel( ArgumentModel arg )
    {
        this.conclusionArgList.add( arg );
    }

    private void onDragDropped( DragEvent event ) throws IOException
    {
        Dragboard db = event.getDragboard();
        boolean dropped = false;
        if ( db.hasContent( propositionModelDataFormat ) )
        {
            PropositionModel tProp = ( PropositionModel ) db.getContent( propositionModelDataFormat );
            this.addProposition( tProp );
            dropped = true;
        } 
        else if ( db.hasString() )
        {
            String treeID = db.getString();
            if ( ! treeID.equals( argTree.getTreeID() ) )
            {
                this.argTree.addSupportToArgument( treeID, this );
                dropped = true;
            }
        }
        event.setDropCompleted( dropped );
        event.consume();
    }

    @Override
    public void addProposition( PropositionModel prop ) throws IOException
    {
        this.mainPane.getChildren().clear();
        this.mainPane.getChildren().add( propositionRectangle );
        this.addPropositionAsPremise( prop );
        this.addPropositionAsConclusion( prop );
        Pane tPropBox = loadNewPropPane( prop );
        this.mainPane.getChildren().add( tPropBox );
    }

    private void addPropositionAsPremise( PropositionModel prop )
    {
        this.premiseArgList.forEach( ( arg ) -> 
        {
            arg.addPremise( prop, position );
        } );
    }

    private void addPropositionAsConclusion( PropositionModel prop )
    {
        this.conclusionArgList.forEach( ( arg ) -> 
        {
            arg.setConclusion( prop );
        });
    }

    private Pane loadNewPropPane( PropositionModel prop ) throws IOException
    {
        FXMLLoader loader = new FXMLLoader( getClass().getResource( "/fxml/PropositionBox.fxml" ) );
        Pane tPropBox = loader.load();
        PropositionBoxController propControl = loader.<PropositionBoxController>getController();
        this.prop = prop;
        this.propBox = tPropBox;
        propControl.setConstructionAreaControl( parentControl );
        propControl.setPropModel( prop );
        propControl.setParentContainer( mainPane );
        propBoxController = propControl;
        return tPropBox;
    }
}
