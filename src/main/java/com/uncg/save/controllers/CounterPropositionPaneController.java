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
import com.uncg.save.argumentviewtree.ArgumentNode;
import com.uncg.save.argumentviewtree.ArgumentViewTree;
import com.uncg.save.models.CounterArgumentModel;
import com.uncg.save.models.PremiseModel;
import com.uncg.save.models.PropositionModel;
import com.uncg.save.util.FXUtils;
import com.uncg.save.util.MouseUtils;
import java.io.IOException;
import java.net.URL;
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
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class CounterPropositionPaneController extends ConclusionPaneController implements Initializable, Draggable
{
    //////////////////////// INSTANCE VARIABLES /////////////////////////////
    
    //
    //
    //
    private CounterArgumentModel    argument;
    private ArgumentNode            connector;
    private ArgumentNode            parent;
    private String                  text;
    

    //
    //
    //
    private double                  x = 0;
    private double                  y = 0;
    private int                     position;
    private boolean                 subFlag  = false;

    //////////////////////// INSTANCE VARIABLES /////////////////////////////    
    
    /**
     Controls single proposition answers to counter arguments

     Controls only single propositions, CounterArgumentPaneController manages
     extended arguments

     Associated view node: CounterPropositionNode

     @param url
     @param rb
     */
    @Override
    public void initialize( URL url, ResourceBundle rb )
    {
        this.mainPane.getStyleClass().add( "premise-pane" );
        this.propositionRectangle.getStyleClass().add( "premise-rectangle" );

        this.mainPane.addEventFilter( DragEvent.DRAG_DROPPED, ( DragEvent event ) ->
        {
            Dragboard db = event.getDragboard();
            if ( db.hasContent( propositionModelDataFormat ) || db.hasString() )
            {
                onDragDropped( event );
            }
        } );
        
        //
        //  CONTEXT MENU REQUESTED EVENT
        //
        //  Index 1 in the CM item list is the add counter argument button
        //
        this.mainPane.addEventFilter( ContextMenuEvent.CONTEXT_MENU_REQUESTED, ( ContextMenuEvent event ) ->
        {
            this.contextMenu.getItems().get( 0 ).setDisable( this.argNode.caFlag );
            this.showContextMenu( event );
            event.consume();
        } );        
        
        //
        //  MOUSE DRAGGED EVENT
        //
        this.mainPane.addEventFilter( MouseEvent.MOUSE_DRAGGED, ( MouseEvent event ) ->
        {
            this.onDragRelease( event );
            this.x = event.getSceneX();
            this.y = event.getSceneY();
        } );

        // intercept drag detected so props are not draggable
        this.mainPane.addEventFilter( MouseEvent.DRAG_DETECTED, ( MouseEvent event ) ->
        {
            event.consume();
        } );
        
        //
        //  MOUSE ENTERED EVENT
        //
        this.mainPane.addEventFilter( MouseEvent.MOUSE_ENTERED,( MouseEvent event ) -> 
        {
            this.connector = this.argNode.getParent();
            this.parentControl.setConclusionPaneController( this );       
        } );
        
        //
        //  MOUSE PRESSED EVEMT
        //
        this.mainPane.addEventFilter( MouseEvent.MOUSE_PRESSED, ( MouseEvent mouseEvent ) ->
        {
            this.x = mouseEvent.getSceneX();
            this.y = mouseEvent.getSceneY();            
        } );        

        //
        //  MOUSE CLICKED EVENT
        //
        this.mainPane.addEventFilter( MouseEvent.MOUSE_CLICKED, ( MouseEvent event ) -> 
        {
            if ( event.getButton() == MouseButton.PRIMARY )
            {
                try
                {
                    if ( ! hasProp )
                    {
                        this.deleteProp();
                        this.mainPane.getChildren().add( propositionRectangle );
                        this.prop = new PropositionModel();
                        this.addPropositionAsConclusion( prop );
                        this.propBox = loadNewPropPane( prop );
                        this.propBoxController.argCertaintyController = certaintyControl;
                        this.mainPane.getChildren().add( propBox );
                        this.hasProp = true;
                    }
                } 
                catch ( IOException ex )
                {
                    Logger.getLogger( ConclusionPaneController.class.getName() ).log( Level.SEVERE, null, ex );
                }
            }
            event.consume();
        });
        // create context menu for adding new premises
        this.contextMenu = new ContextMenu();
        this.setContextMenuItems();
        this.setContextMenuEventFilter();
    }

    public void setConnection( ArgumentNode connector )
    {
        this.connector = connector;
    }

    public ArgumentNode getConnection()
    {
        return this.connector;
    }
    
    public ArgumentNode getParent()
    {
        return this.parent;
    }
    
    public void setParent( ArgumentNode parent )
    {
        this.parent = parent;
    }

    public void setParentModelList( List<PremiseModel> modelList )
    {
        this.argument.setParentModelList( modelList );
    }

    public List<PremiseModel> getParentModelList()
    {
        return argument.getParentModelList();
    }
    
    @Override
    public void onDragRelease( MouseEvent event )
    {
        double a = event.getSceneX() - this.x;
        double b = event.getSceneY() - this.y;
        
        Iterator<ArgumentNode> children = this.argTree.getChildrenOfConclusion( this ).iterator();
        
        Line translateLine = ( Line ) children.next().getView();
        translateLine.setEndX( translateLine.getEndX() + a );
        translateLine.setEndY( translateLine.getEndY() + b );
        
        while( children.hasNext() )
        {
            ArgumentNode next = children.next();
            this.argTree.shiftNode( next, ( int ) a, ( int ) b );
        }
    }

    /**
     set menu items for context menu
     */
    private void setContextMenuItems()
    {
        MenuItem addCounterArg         = new MenuItem( "Add Counter Argument"    );
        MenuItem deleteCounterArgument = new MenuItem( "Delete Counter Argument" );
        MenuItem copy                  = new MenuItem( "Copy (CTRL+C)"          );
        MenuItem paste                 = new MenuItem( "Paste (CTRL+V)"         );        

        this.setHandlerForAddCounter( addCounterArg );
        this.setHandlerForDeleteCounterArgument( deleteCounterArgument );
        this.setHandlerForCopy       ( copy            );
        this.setHandlerForPaste      ( paste           );
                

        this.contextMenu.getItems().addAll( addCounterArg, deleteCounterArgument,
                                            copy, paste );
    }

    /*
     Adds a counter argument to the pane
     */
    private void setHandlerForAddCounter( MenuItem item )
    {
        item.setOnAction( action ->
        {
            try
            {
                this.argTree.addCounterArgument( argument.getConclusion(), argNode );
                this.argNode.caFlag = true;
                item.setDisable( true );
            } 
            catch ( IOException ex )
            {
                Logger.getLogger( ConclusionPaneController.class.getName() ).log( Level.SEVERE, null, ex );
            }
        } );
    }

    //Colors the pane based on the certainty
    @Override
    public void setViewColor( double d )
    {
        this.propositionRectangle.setFill( Color.rgb( FXUtils.ConvertToRed( d ), 0, FXUtils.ConvertToBlue( d ) ) );
    }

    private void extractProp()
    {
        parentControl.createProp( prop, contextCoords );
    }

    /*
     Removes proposition from view and model of this pane
     */
    private void removeProp()
    {
        this.contextMenu.getItems().clear();
        this.setContextMenuItems();
        if ( this.argument != null )
        {
            this.argument.removeConclusion();
        }
        this.prop = null;
        this.hasProp = false;
        this.propBoxController.deleteComment();
        this.mainPane.getChildren().remove( propBox );
    }

    /*
     Deletes the counter argument
     */
    private void setHandlerForDeleteCounterArgument( MenuItem item )
    {
        item.setOnAction( action ->
        {
            this.argTree.deleteCounterArgument( this, argument.getParentModelList(), argument );
            this.parent.caFlag = false;
        } );
    }

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

    public void setSchemeLabel( Label lbl )
    {
        this.schemeLabel = lbl;
        this.mainPane.getChildren().add( lbl );
    }

    public void setParentControl( ConstructionAreaController control )
    {
        this.parentControl = control;
    }

    public Pane getMainPane()
    {
        return this.mainPane;
    }

    public CounterArgumentModel getArgument()
    {
        return this.argument;
    }

    public void setArgumentModel( CounterArgumentModel arg )
    {
        this.argument = arg;
    }

    /*
     public PropositionModel getPropositionModel() { return propositionModel; }
     */
    public int getPosition()
    {
        return this.position;
    }

    public void setPosition( int position )
    {
        this.position = position;
    }

    public void setArgumentViewTree( ArgumentViewTree argTree )
    {
        this.argTree = argTree;
    }

    /**
     Listens for dragdropped events and handles them

     @param event
     */
    private void onDragDropped( DragEvent event )
    {
        Dragboard db = event.getDragboard();
        boolean dropped = false;
        if ( db.hasContent( propositionModelDataFormat ) )
        {
            try
            {
                this.dropProp( event );
                dropped = true;
            } 
            catch ( IOException ex )
            {
                Logger.getLogger( PremisePaneController.class.getName() ).log( Level.SEVERE, null, ex );
            }
        } 
        else if ( db.hasString() &&  ! db.getString().equals( argTree.getTreeID() ) )
        {
            try
            {
                this.dropTree( event );
                dropped = true;
            } 
            catch ( IOException ex )
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
    }

    private void addPropositionAsConclusion( PropositionModel prop )
    {
        if ( this.argument != null )
        {
            this.argument.setConclusion( prop );
        }
    }

    public PropositionModel getProposition()
    {
        return this.prop;
    }

    /**
     Creates a proposition within this pane

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
        this.propBox = tPropBox;
        this.prop = prop;
        propControl.setConstructionAreaControl( parentControl );
        propControl.setPropModel( prop );
        propControl.setParentContainer( mainPane );
        propControl.setContextMenu( contextMenu );
        this.propBoxController = propControl;
        this.hasProp = true;
        return tPropBox;
    }

    private void dropTree( DragEvent event ) throws IOException
    {
        Dragboard db = event.getDragboard();
        String treeID = db.getString();
        this.argTree.mergeTree( treeID, this );
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

    public void shiftToSub()
    {
        this.argTree.translateNode( argNode, 102, 0 );
        this.argTree.translateNode( argNode, position, position );
    }
}
