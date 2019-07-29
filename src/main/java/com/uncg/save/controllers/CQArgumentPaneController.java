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

import static com.uncg.save.MainApp.propositionModelDataFormat;
import com.uncg.save.argumentviewtree.ArgumentNode;
import com.uncg.save.argumentviewtree.ArgumentSchemeLabel;
import com.uncg.save.argumentviewtree.ArgumentViewTree;
import com.uncg.save.models.ArgumentModel;
import com.uncg.save.models.PropositionModel;
import com.uncg.save.util.LayoutUtils;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;

public class CQArgumentPaneController extends ConclusionPaneController implements Initializable
{
    //////////////////////// INSTANCE VARIABLES /////////////////////////////    
    
    //
    //
    //
    private ArgumentSchemeLabel parentSchemeLabel = null;
    private ArgumentModel       argument          = null;
    private ArgumentModel       parentArgument    = null;
    private ArgumentNode        connector         = null;
    private ArgumentNode        label             = null;

    //
    //
    //
    private String              text              = null;

    //
    //
    //
    private int                 position;

    //////////////////////// INSTANCE VARIABLES /////////////////////////////
    
    /**
      * Controls the ultimate pane of an argument supporting a critical question
      * 
      * Only exists as a conclusion to an argument, lone panes are CQPanes
      * 
      * Associated view node: CQArgumentNode
      */
    @Override
    public void initialize( URL url, ResourceBundle rb )
    {
        this.conclusionArgList = new ArrayList<>();
        this.mainPane.getStyleClass().add( "premise-pane" );
        this.propositionRectangle.getStyleClass().add( "premise-rectangle" );

        this.mainPane.addEventFilter( DragEvent.DRAG_DROPPED, ( DragEvent event ) ->
        {
            Dragboard db = event.getDragboard();
            if ( db.hasContent( propositionModelDataFormat ) || db.hasString() )
            {
                this.onDragDropped( event );
            }
        } );

        // Intercept drag detected so props are not draggable
        this.mainPane.addEventFilter( MouseEvent.DRAG_DETECTED, ( MouseEvent event ) ->
        {
            event.consume();
        } );

        // Create context menu for adding new premises
        this.contextMenu = new ContextMenu();
        this.setContextMenuItems();
        this.setContextMenuEventFilter();
    }

    /**
     * Sets the context menu items
     */
    private void setContextMenuItems()
    {
        MenuItem addCounterArg  = new MenuItem( "Add Counter Argument" );

        this.setHandlerForAddCounter( addCounterArg );
        
        this.contextMenu.getItems().addAll( addCounterArg );
    }

    /**
     * Detaches the supporting argument
     */
    private void setHandlerForDetachArg( MenuItem item )
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

    @Deprecated
    private void setHandlerForToggle( MenuItem item )
    {
        item.setOnAction( action ->
        {
            this.certaintyControl.toggleVisible();
            action.consume();
        } );
    }

    /**
     * Adds a counter argument attached to this pane
     */
    private void setHandlerForAddCounter( MenuItem item )
    {
        item.setOnAction( action ->
        {
            try
            {
                this.argTree.addCounterArgument( this.argument.getConclusion(), this.argNode );
            } 
            catch ( IOException ex )
            {
                Logger.getLogger( ConclusionPaneController.class.getName() ).log( Level.SEVERE, null, ex );
            }
        } );
    }

    /**
     * Removes the proposition from this pane and adds it to constructionarea
     */
    @Deprecated
    private void setHandlerForDetachProp( MenuItem item )
    {
        item.setOnAction( action ->
        {
            if ( this.prop != null )
            {
                this.extractProp();
                this.removeProp();
            }
            action.consume();
        } );
    }

    private void extractProp()
    {
        parentControl.createProp( prop, contextCoords );
    }

    @Deprecated
    private void setHandlerForDeleteProp( MenuItem delete )
    {
        delete.setOnAction( action ->
        {
            if ( this.prop != null )
            {
                this.removeProp();
            }
            action.consume();
        } );
    }

    /**
     * Removes the proposition from the view and model
     */
    private void removeProp()
    {
        if ( this.argument != null )
        {
            this.argument.removeConclusion();
        }        
        
        this.contextMenu.getItems().clear();
        this.setContextMenuItems();
        this.prop = null;
        this.mainPane.getChildren().remove( propBox );
        this.hasProp = false;
        this.deleteCommentPane();
    }

    /**
     add context menu event handler for mouse clicks
     */
    private void setContextMenuEventFilter()
    {
        this.mainPane.addEventFilter( ContextMenuEvent.CONTEXT_MENU_REQUESTED, ( ContextMenuEvent event ) ->
        {
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
        contextMenu.hide();
        contextMenu.show( mainPane, event.getScreenX(), event.getScreenY() );
        contextCoords = new Point2D( event.getScreenX(), event.getScreenY() );
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

    /**
     * Listens for dragdropped events on this pane and determines how to handle
     */
    private void onDragDropped( DragEvent event )
    {
        Dragboard db = event.getDragboard();
        boolean dropped = false;
        if ( db.hasContent( propositionModelDataFormat ) )
        {
            PropositionModel prop = ( PropositionModel ) db.getContent( propositionModelDataFormat );
            try
            {
                this.dropProp( event );
                dropped = true;
            } catch ( IOException ex )
            {
                Logger.getLogger( PremisePaneController.class.getName() ).log( Level.SEVERE, null, ex );
            }
        }
        event.setDropCompleted( dropped );
        event.consume();
    }

    private void dropProp( DragEvent event ) throws IOException
    {
        Dragboard db = event.getDragboard();
        PropositionModel prop = ( PropositionModel ) db.getContent( propositionModelDataFormat );
        this.mainPane.getChildren().clear();
        this.mainPane.getChildren().add( propositionRectangle );
        this.addProposition( prop );
    }

    @Override
    public void addProposition( PropositionModel prop ) throws IOException
    {
        this.prop = prop;
        this.addPropositionAsConclusion( prop );
        Pane tPropBox = loadNewPropPane( prop );
        this.mainPane.getChildren().add( tPropBox );
        this.hasProp = true;
    }

    private void addPropositionAsPremise( PropositionModel prop )
    {
        if ( this.argument != null )
        {
            this.argument.addPremise( prop, position );
        }
    }

    /**
      * Loads a new proposition within the pane
      */
    private Pane loadNewPropPane( PropositionModel prop ) throws IOException
    {
        this.contextMenu.getItems().clear();
        this.setContextMenuItems();
        if ( this.propBoxController != null )
        {
            this.propBoxController.deleteComment();
        }
        FXMLLoader loader = new FXMLLoader( getClass().getResource( "/fxml/PropositionBox.fxml" ) );
        Pane tPropBox     = loader.load();
        this.propBox      = tPropBox;
        
        PropositionBoxController propControl = loader.<PropositionBoxController>getController();
            propControl.setConstructionAreaControl( parentControl );
            propControl.setPropModel( prop );
            propControl.setParentContainer( mainPane );
            propControl.setContextMenu( contextMenu );
            
        this.propBoxController = propControl;
        
        return tPropBox;
    }

    @FXML
    private void dragOver( DragEvent event )
    {
        Dragboard db = event.getDragboard();
        if ( db.hasContent( propositionModelDataFormat ) || db.hasString() )
        {
            event.acceptTransferModes( TransferMode.MOVE );
        }
        event.consume();
    }

    public String getText()
    {
        return this.text;
    }

    public void setText( String s )
    {
        this.text = s;
    }

    public void checkHasProp()
    {
        if ( propBox != null )
        {
            hasProp = true;
        }
    }

    /**
      * Adds the proposition to the conclusion of the argumentmodel
      */
    private void addPropositionAsConclusion( PropositionModel prop )
    {
        if ( argument != null )
        {
            argument.setConclusion( prop );
        }
    }

    public void setParentArgument( ArgumentModel parentArg )
    {
        parentArgument = parentArg;
    }

    public ArgumentModel getParentArgument()
    {
        return parentArgument;
    }

    public void addArgumentToParentCQArgList( ArgumentModel arg )
    {
        parentArgument.addCQArgument( arg );
    }

    public void removeArgumentFromParentCQArgList( ArgumentModel arg )
    {
        parentArgument.removeCQArgument( arg );
    }

    @Override
    public void moveComment( double x, double y )
    {
        propBoxController.moveComment( x, y );
    }

    @Override
    public void deleteCommentPane()
    {
        propBoxController.deleteComment();
    }
    
    public PropositionModel getProposition()
    {
        return this.prop;
    }

    public void setParentControl( ConstructionAreaController control )
    {
        this.parentControl = control;
    }

    public Pane getMainPane()
    {
        return this.mainPane;
    }

    public ArgumentModel getArgument()
    {
        return this.argument;
    }

    public void setArgumentModel( ArgumentModel arg )
    {
        this.argument = arg;
    }

    /**
      * Sets the propositionmodel to the proposition added to this pane
      */
    public void setPropositionModel( PropositionModel pm )
    {
        Pane tPropBox;
        try
        {
            this.addProposition( pm );
        } catch ( IOException ex )
        {
            Logger.getLogger( PremisePaneController.class.getName() ).log( Level.SEVERE, null, ex );
        }
    }

    public void setArgumentViewTree( ArgumentViewTree argTree )
    {
        this.argTree = argTree;
    }
    
    public void setArgNode( ArgumentNode argNode )
    {
        this.argNode = argNode;
    }
}
