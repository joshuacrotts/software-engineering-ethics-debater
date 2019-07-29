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
import com.uncg.save.argumentviewtree.ArgumentSchemeLabel;
import com.uncg.save.argumentviewtree.ArgumentViewTree;
import com.uncg.save.argumentviewtree.PremiseConnectionNode;
import com.uncg.save.models.ArgumentModel;
import com.uncg.save.models.PropositionModel;
import com.uncg.save.util.MouseUtils;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;

public class CQPaneController extends ConclusionPaneController implements Initializable, Draggable
{
    //////////////////////// INSTANCE VARIABLES /////////////////////////////    
    
    //
    //
    //
    private ArgumentModel           argument;
    private ArgumentModel           parentArgument;
    private ArgumentNode            connector;
    private ArgumentNode            label;
    private ArgumentSchemeLabel     parentSchemeLabel;
    private PremiseConnectionNode   connectionNode;

    //
    //
    //
    private String                  text;

    //
    //
    //
    private int                     position;
    private double                  x;
    private double                  y;
    private int                     proConStatus = -1;
    private int                     cqNum;

    //////////////////////// INSTANCE VARIABLES /////////////////////////////
    
    /**
      * Controls single pane answers to critical questions
      * 
      * Only exists as single panes, arguments in support of a CQ lead to a
      * CQArgumentPane
      * 
      * Associated view node: CQNode
      */
    @Override
    public void initialize( URL url, ResourceBundle rb )
    {
        this.mainPane.getStyleClass().add( "cq-pane" );

        this.mainPane.addEventFilter( DragEvent.DRAG_DROPPED, ( DragEvent event ) ->
        {
            Dragboard db = event.getDragboard();
            if ( db.hasContent( propositionModelDataFormat ) || db.hasString() )
            {
                this.onDragDropped( event );
                this.x = event.getX();
                this.y = event.getY();                
            }
        } );

        // intercept drag detected so props are not draggable
        this.mainPane.addEventFilter( MouseEvent.DRAG_DETECTED, ( event ) -> event.consume() );
        
        //
        //  MOUSE ENTERED EVENT
        //
        this.mainPane.addEventFilter( MouseEvent.MOUSE_ENTERED, ( MouseEvent mouseEvent ) -> 
        {
            this.parentControl.setConclusionPaneController( this );
        } ); 
        
        //
        //  We need a way of distinguishing between mouse CLICKS and mouse PRESSES
        //  for when the user drags the premises around. This remediates that problem.
        //
        this.mainPane.addEventFilter( MouseEvent.MOUSE_PRESSED, ( MouseEvent mouseEvent ) ->
        {      
            this.x = mouseEvent.getSceneX();
            this.y = mouseEvent.getSceneY();          
        } );        

        //
        //  We can simulate movement by dropping and picking up the 
        //  pane over and over as the user moves the mouse while 
        //  clicking & dragging
        //
        this.mainPane.addEventFilter( MouseEvent.MOUSE_DRAGGED, ( MouseEvent mouseEvent ) ->
        {
            // If the pane has a counter argument present, we need to translate
            // all its children in the appropriate direction. If not, we only need
            // to translate the parent.
            if ( ! this.hasCounter )
            {
                this.onDragRelease( mouseEvent );
            } 
            else
            {
                this.onDragRelease( mouseEvent );
                Iterator<ArgumentNode> children = argTree.getChildrenOfConclusion( this ).iterator();
                        
                // The first child in the iterator is the node itself so we need
                // to pool it before we start 
                children.next();
                        
                double deltaX = mouseEvent.getSceneX() - this.x;
                double deltaY = mouseEvent.getSceneY() - this.y;                
                
                while ( children.hasNext() )
                {
                    argTree.shiftNode( children.next(), ( int ) deltaX, ( int ) deltaY );
                }
            }

            this.dragging = true;
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
                    if (  ! hasProp )
                    {
                        this.deleteProp();
                        this.mainPane.getChildren().add( propositionRectangle );
                        this.prop = new PropositionModel();
                        this.addPropositionAsPremise( prop );
                        this.propBox = loadNewPropPane( prop );
                        this.mainPane.getChildren().add( propBox );
                        this.propBoxController.text.setText( this.getText() );
                        this.parentControl.setCQPaneController( this );
                        this.hasProp = true;
                    }
                } 
                catch ( IOException ex )
                {
                    Logger.getLogger( ConclusionPaneController.class.getName() ).log( Level.SEVERE, null, ex );
                }
            }
            event.consume();
        } );
        // Create context menu for adding new premises
        this.contextMenu = new ContextMenu();
        this.setContextMenuItems();
        this.setContextMenuEventFilter();
    }
    
    public void initializePane( String proposition, int proConFlag )
    {
        try
        {
            if (  ! hasProp )
            {
                this.deleteProp();
                this.mainPane.getChildren().add( propositionRectangle );
                this.prop = new PropositionModel();
                this.addPropositionAsPremise( prop );
                this.propBox = loadNewPropPane( prop );
                this.mainPane.getChildren().add( propBox );
                this.setText( proposition );
                this.propBoxController.text.setText( this.getText() );
                this.parentControl.setCQPaneController( this );
                this.hasProp = true;
                this.proConStatus = proConFlag;
                switch ( this.proConStatus )
                {
                    case 0: this.propBoxController.text.setStyle( "-fx-border-color: #0f0; -fx-border-width: 4;" ); break; //Green
                    case 1: this.propBoxController.text.setStyle( "-fx-border-color: #f00; -fx-border-width: 4;" ); break; //Red 
                    case 2: this.propBoxController.text.setStyle( "-fx-border-color: #000; -fx-border-width: 0;" ); break; //Black
                }                
            }
        } 
        catch ( IOException ex )
        {
            Logger.getLogger( ConclusionPaneController.class.getName() ).log( Level.SEVERE, null, ex );
        }        
    }

    /**
     * We can continuously call this method to simulate movement
     * by dragging and dropping the pane very quickly. 
     * 
     * @param event 
     */
    @Override
    public void onDragRelease( MouseEvent event )
    {
        double a = event.getSceneX() - this.x;
        double b = event.getSceneY() - this.y;
        
        this.translatePremiseNode( a, b );
        this.parentControl.constructionAreaSizeCheck();
    }
    
    /**
     * Translates the pane itself as well as the premise connection node
     * 
     * @param _x offset x
     * @param _y offset y
     */
    private void translatePremiseNode( double _x, double _y )
    {
        this.mainPane.setTranslateX( ( int ) ( this.mainPane.getTranslateX() + _x ) );
        this.mainPane.setTranslateY( ( int ) ( this.mainPane.getTranslateY() + _y ) );
        
        this.certaintyControl.mainContainer.setTranslateX( ( int ) this.mainPane.getTranslateX() );
        this.certaintyControl.mainContainer.setTranslateY( ( int ) this.mainPane.getTranslateY() );
        
        this.moveComment( _x, _y );

        Line translateLine = ( Line ) this.getConnectionNode().getView();
        translateLine.setEndX( translateLine.getEndX() + _x );
        translateLine.setEndY( translateLine.getEndY() + _y );
        
    }    

    /**
     * Set menu items for context menu
     */
    private void setContextMenuItems()
    {
        MenuItem copy          = new MenuItem( "Copy (CTRL+C)"        );
        MenuItem paste         = new MenuItem( "Paste (CTRL+V)"       );
        MenuItem toggleProCon  = new MenuItem( "Toggle Pro/Con" );
        MenuItem addCounterArg = new MenuItem( "Add Counter Argument" );
        MenuItem deleteCQ      = new MenuItem( "Delete Critical Question" );
        
        this.setHandlerForToggle     ( toggleProCon    );
        this.setHandlerForAddCounter ( addCounterArg   );
        this.setHandlerForDeleteCQ   ( deleteCQ        );
        this.setHandlerForCopy       ( copy            );
        this.setHandlerForPaste      ( paste           );
        
        this.contextMenu.getItems().addAll( copy, paste, toggleProCon, addCounterArg, deleteCQ );
        
        addCounterArg.setDisable( true );
    }

    private void setHandlerForToggle( MenuItem item )
    {
        item.setOnAction( action ->
        {
            this.proConStatus++;
            
            if ( this.proConStatus > 2 )
            {
                this.proConStatus = 0;
            }
            
            switch( this.proConStatus )
            {
                case 0: this.getPropositionBoxController().text.setStyle( "-fx-border-color: #0f0; -fx-border-width: 4;" ); break; //Green
                case 1: this.getPropositionBoxController().text.setStyle( "-fx-border-color: #f00; -fx-border-width: 4;" ); break; //Red 
                case 2: this.getPropositionBoxController().text.setStyle( "-fx-border-color: #000; -fx-border-width: 0;" ); break; //Black
            }

            action.consume();
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
    

    /*
     Adds a counter argument to the critical question
     */
    private void setHandlerForAddCounter( MenuItem item )
    {
        item.setOnAction( action ->
        {
            try
            {
                this.argTree.addCounterArgument( argument.getConclusion(), argNode);
                this.hasCounter = true;
            } 
            catch ( IOException ex )
            {
                Logger.getLogger( ConclusionPaneController.class.getName() ).log( Level.SEVERE, null, ex );
            }
        } );
    }

    private void setHandlerForDeleteCQ( MenuItem delete )
    {
        delete.setOnAction( action ->
        {
            this.argTree.deleteCriticalQuestion( this );
        } );
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
        this.contextMenu.hide();
        this.contextMenu.show( mainPane, event.getScreenX(), event.getScreenY() );
        this.contextCoords = new Point2D( event.getScreenX(), event.getScreenY() );
        event.consume();
    }

    public void setPropositionModel( PropositionModel pm )
    {
        Pane tPropBox;
        try
        {
            tPropBox = loadNewPropPane( pm );
            this.mainPane.getChildren().add( tPropBox );
        } catch ( IOException ex )
        {
            Logger.getLogger( PremisePaneController.class.getName() ).log( Level.SEVERE, null, ex );
        }
    }

    /*
     Listens for dragdropped events on this pane and determines how to handle
     */
    private void onDragDropped( DragEvent event )
    {
        Dragboard db    = event.getDragboard();
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

    private void dropTree( DragEvent event ) throws IOException
    {
        Dragboard db = event.getDragboard();
        String treeID = db.getString();
        argTree.mergeTree( treeID, this );
    }

    @Override
    public void addProposition( PropositionModel prop ) throws IOException
    {
        this.prop = prop;

        this.addPropositionAsPremise( prop );
        Pane tPropBox = loadNewPropPane( prop );
        
        this.mainPane.getChildren().add( tPropBox );
        this.hasProp = true;
    }

    /**
     * Adds the prop as a premise for the argument model
     */
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
        contextMenu.getItems().clear();
        setContextMenuItems();
        if ( propBoxController != null )
        {
            propBoxController.deleteComment();
        }
        FXMLLoader loader = new FXMLLoader( getClass().getResource( "/fxml/PropositionBox.fxml" ) );
        Pane tPropBox = loader.load();
        this.propBox = tPropBox;
        PropositionBoxController propControl = loader.<PropositionBoxController>getController();
        propControl.setConstructionAreaControl( parentControl );
        propControl.setPropModel( prop );
        propControl.setParentContainer( mainPane );
        propControl.setContextMenu( contextMenu );
        propBoxController = propControl;
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

    @Override
    public void checkHasProp()
    {
        if ( this.propBox != null )
        {
            this.hasProp = true;
        }
    }

    @Override
    public void moveComment( double x, double y )
    {
        if ( this.propBoxController != null )
        {
            this.propBoxController.moveComment( x, y );
        }
    }

    @Override
    public void deleteCommentPane()
    {
        this.propBoxController.deleteComment();
    }
    
    
    public void setConnectionNode( PremiseConnectionNode pcn )
    {
        this.connectionNode = pcn;
    }
    
    public PremiseConnectionNode getConnectionNode()
    {
        return this.connectionNode;
    }

    /*
     Adds the proposition in this pane to the conclusion of its associated
     ArgumentModel
     */
    private void addPropositionAsConclusion( PropositionModel prop )
    {
        if ( this.argument != null )
        {
            this.argument.setConclusion( prop );
        }
    }

    public void setParentArgument( ArgumentModel arg )
    {
        this.parentArgument = arg;
    }

    public ArgumentModel getParentArgument()
    {
        return this.parentArgument;
    }

    public void setConnector( ArgumentNode connection )
    {
        this.connector = connection;
    }

    public ArgumentNode getConnector()
    {
        return this.connector;
    }

    public void setLabel( ArgumentNode label )
    {
        this.label = label;
    }

    public ArgumentNode getLabel()
    {
        return this.label;
    }

    public void setArgumentLabel( ArgumentNode label )
    {
        this.parentSchemeLabel = ( ArgumentSchemeLabel ) label;
    }

    public ArgumentNode getArgSchemeLabel()
    {
        return this.parentSchemeLabel;
    }    

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
    
    @Override
    public int getProCon()
    {
        return this.proConStatus;
    }
    
    public void setCQNum( int num )
    {
        this.cqNum = num;
    }
    
    public int getCQNum()
    {
        return this.cqNum;
    }
}
