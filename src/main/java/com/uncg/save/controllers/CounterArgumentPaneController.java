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
import com.uncg.save.argumentviewtree.ChainNode;
import com.uncg.save.argumentviewtree.ConclusionNode;
import com.uncg.save.argumentviewtree.PremiseNode;
import com.uncg.save.models.CounterArgumentModel;
import com.uncg.save.models.PremiseModel;
import com.uncg.save.models.PropositionModel;
import com.uncg.save.util.LayoutUtils;
import com.uncg.save.util.MouseUtils;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
import javafx.scene.shape.Line;

public class CounterArgumentPaneController extends ConclusionPaneController implements Initializable
{
    //////////////////////// INSTANCE VARIABLES /////////////////////////////
    
    private ArgumentNode            connector = null;
    private CounterArgumentModel    argument  = null;
    private String                  text      = null;

    private int                     position  = -1;
    private boolean                 subFlag   = false;
    //
    //  Coordinates for mouse position 
    //  when dragging around the premise
    //  pane object
    //
    private double                  x         = 0.0;
    private double                  y         = 0.0;

    //////////////////////// INSTANCE VARIABLES /////////////////////////////
    
    /**
     Controls the ultimate pane of an argument supporting a counter argument

     Only applies to arguments, single panes controlled by
     CounterPropositionPaneControllers

     Associated view node: CounterArgumentNode

     @param url
     @param rb
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
        
        this.mainPane.addEventFilter( MouseEvent.MOUSE_ENTERED, ( MouseEvent event ) -> 
        {
            this.connector = this.argNode.getParent();
            this.parentControl.setConclusionPaneController( this );            
        });

        // intercept drag detected so props are not draggable
        this.mainPane.addEventFilter( MouseEvent.DRAG_DETECTED, ( MouseEvent event ) ->
        {
            event.consume();
        } );
        
        //******* TO DO: FIX THE EVENT LISTENERS **********//
        
        //
        //  MOUSE PRESSED EVENT
        //
        this.mainPane.addEventFilter( MouseEvent.MOUSE_PRESSED, ( MouseEvent mouseEvent ) -> 
        {
            this.dragging = true;
            this.x        = mouseEvent.getSceneX();
            this.y        = mouseEvent.getSceneY();  
        } );
        
        //
        //  MOUSE DRAGGED EVENT
        //
        //  We can simulate movement by dropping and picking up the 
        //  pane over and over as the user moves the mouse while 
        //  clicking & dragging
        //
        this.mainPane.addEventFilter( MouseEvent.MOUSE_DRAGGED, ( MouseEvent mouseEvent ) ->
        {
            if ( this.propBoxController != null && !this.propBoxController.text.getSelectedText().isEmpty() )
            {
                return;
            }            
            // If the pane has a counter argument present, we need to translate
            // all its children in the appropriate direction. If not, we only need
            // to translate the parent.
            this.onDragRelease( mouseEvent );
            Iterator<ArgumentNode> children = argTree.getChildrenOfConclusion( this ).iterator();

            // The first child in the iterator is the node itself so we need
            // to pool it before we start 
            children.next();

            double deltaX = mouseEvent.getSceneX() - this.x;
            double deltaY = mouseEvent.getSceneY() - this.y;

            while ( children.hasNext() )
            {
                this.argTree.shiftNode( children.next(), ( int ) deltaX, ( int ) deltaY );
            }

            
            //
            // If we're currently dragging the pane, we don't want the tooltip
            // to arbitrarily appear because it's not clean.
            //
            this.dragging = true;
            this.x = mouseEvent.getSceneX();
            this.y = mouseEvent.getSceneY();  
            
        } );         

        
        // create context menu for adding new premises
        this.contextMenu = new ContextMenu();
        this.setContextMenuItems();
        this.setContextMenuEventFilter();
    }
    
    /**
     * We can continuously call this method to simulate movement
     * by dragging and dropping the pane very quickly. 
     * 
     * @param event 
     */
    public void onDragRelease( MouseEvent event )
    {
        double a = event.getSceneX() - this.x;
        double b = event.getSceneY() - this.y;
        
        this.translatePremiseNode( a, b );
        this.parentControl.constructionAreaSizeCheck();
    }    
    
    /**
     * Actual translation method for the pane and the line housing the pane.
     * 
     * @param _x
     * @param _y 
     */
    private void translatePremiseNode( double _x, double _y )
    {
        //this.certaintyControl.mainContainer.setTranslateX( this.mainPane.getTranslateX() );
        //this.certaintyControl.mainContainer.setTranslateY( this.mainPane.getTranslateY() );
        
        this.moveComment( _x, _y );

        Line translateLine = ( Line ) this.connector.getView();
        translateLine.setEndX( translateLine.getEndX() + _x );
        translateLine.setEndY( translateLine.getEndY() + _y );
    }
    

    /**
     set menu items for context menu
     */
    private void setContextMenuItems()
    {
        MenuItem addCounterArg         = new MenuItem( "Add Counter Argument" );
        MenuItem detachChain           = new MenuItem( "Detach Argument Chain" );
        MenuItem deleteCounterArgument = new MenuItem( "Delete Counter Argument" );
        MenuItem copy                  = new MenuItem( "Copy (CTRL+C)"          );
        MenuItem paste                 = new MenuItem( "Paste (CTRL+V)"         );
            detachChain.setDisable( true );
            addCounterArg.setDisable( true );

        this.setHandlerForCounterArg (  addCounterArg  );
        this.setHandlerForDetachChain(  detachChain    );
        this.setHandlerForDeleteCounterArgument( deleteCounterArgument );
        this.setHandlerForCopy       ( copy            );
        this.setHandlerForPaste      ( paste           );
        
        this.contextMenu.getItems().addAll( addCounterArg, detachChain, deleteCounterArgument,
                                            copy, paste );
    }
    
    private void extractProp()
    {
        this.parentControl.createProp( prop, contextCoords );
    }
    
    /*
     Removes proposition from view and model
     */
    private void removeProp()
    {
        if ( this.argument != null )
        {
            this.argument.removeConclusion();
        }
        this.prop = null;
        this.mainPane.getChildren().remove( propBox );
        this.contextMenu.getItems().clear();
        this.setContextMenuItems();
        this.deleteCommentPane();
    }

    /*
     Allows a counter argument to be added to the pane
     */
    private void setHandlerForCounterArg( MenuItem item )
    {
        item.setOnAction( action ->
        {
            try
            {
                this.argTree.addCounterArgument( argument.getConclusion(), argNode );
                this.argNode.caFlag = true;
            } 
            catch ( IOException ex )
            {
                Logger.getLogger( ConclusionPaneController.class.getName() ).log( Level.SEVERE, null, ex );
            }
        } );
    }

    private void setHandlerForDetachChain( MenuItem item )
    {
        item.setOnAction( action ->
        {
            Point2D localCoords = LayoutUtils.getLocalCoords( this.parentControl.getMainPane(),
                                                              this.contextCoords.getX(),
                                                              this.contextCoords.getY() );
            this.argTree.detachArgumentChain( this, localCoords );
            action.consume();
        } );
    }

    /*
     Deletes the counter argument
     */
    private void setHandlerForDeleteCounterArgument( MenuItem item )
    {
        item.setOnAction( action ->
        {
            ArgumentNode parentNode = this.argNode;
            
            // Traverses up the tree until it finds the node that is the 
            // actual parent of the CA pane
            do
            {
                parentNode = parentNode.getParent();
                
            } while( ! ( parentNode instanceof ConclusionNode ) && 
                     ! ( parentNode instanceof PremiseNode    ) &&
                     ! ( parentNode instanceof ChainNode      ) );
            
            parentNode.caFlag = false; 
            
            this.argTree.deleteCounterArgument( this, argument.getParentModelList(), argument );
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

    private void setContextMenuEventFilter()
    {
        this.mainPane.addEventFilter( ContextMenuEvent.CONTEXT_MENU_REQUESTED, ( ContextMenuEvent event ) ->
        {
            //this.contextMenu.getItems().get( 0 ).setDisable( this.argNode.caFlag );
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
    
    /**
     Handles a drag-dropped event based on the content of event

     @param event
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
        this.deleteProp();
        this.mainPane.getChildren().add( propositionRectangle );
        this.addPropositionAsConclusion( prop );
        this.propBox = loadNewPropPane( prop );
        this.mainPane.getChildren().add( propBox );
        this.hasProp = true;
    }

    private void addPropositionAsConclusion( PropositionModel prop )
    {
        this.argument.setConclusion( prop );
        this.prop = prop;
    }

    /**
     Loads a new proposition within the pane

     @param prop

     @return

     @throws IOException
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
        Pane tPropBox = loader.load();
        this.propBox = tPropBox;
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
        if ( db.hasContent( propositionModelDataFormat ) )
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

    public void shiftToSub()
    {
        this.argTree.translateNode( argNode, 102, 0 );
        this.argTree.translateNode( argNode, position, position );
    }

    public void addCounterArgumentToParentModels( CounterArgumentModel arg )
    {
        for ( PremiseModel model : argument.getParentModelList() )
        {
            model.addCounterArgument( arg );
        }
    }

    public void removeCounterArgumentFromParentModels( CounterArgumentModel arg )
    {
        for ( PremiseModel model : argument.getParentModelList() )
        {
            model.removeCounterArgument( arg );
        }
    }

    @Override
    public void moveComment( double x, double y )
    {
        this.propBoxController.moveComment( x, y );
    }

    @Override
    public void deleteCommentPane()
    {
        this.propBoxController.deleteComment();
    }

    public void setConnection( ArgumentNode connector )
    {
        this.connector = connector;
    }

    public ArgumentNode getConnection()
    {
        return this.connector;
    }

    public void setArgNode( ArgumentNode argNode )
    {
        this.argNode = argNode;
    }

    public void setParentModelList( List<PremiseModel> modelList )
    {
        argument.setParentModelList( modelList );
    }

    public List<PremiseModel> getParentModelList()
    {
        return argument.getParentModelList();
    }    

    public CounterArgumentModel getArgument()
    {
        return argument;
    }

    public void setArgumentModel( CounterArgumentModel arg )
    {
        argument = arg;
    }

    public int getPosition()
    {
        return position;
    }

    public void setPosition( int position )
    {
        this.position = position;
    }
    
}
