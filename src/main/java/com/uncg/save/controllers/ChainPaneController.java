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
import com.uncg.save.argumentviewtree.PremiseConnectionNode;
import com.uncg.save.models.ArgumentModel;
import com.uncg.save.models.PropositionModel;
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
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;

public class ChainPaneController extends ConclusionPaneController implements Initializable, Draggable
{

    //
    //  Argument data
    //
    protected ArgumentNode          connector;
    protected ArgumentNode          cqLabel;
    protected ArgumentSchemeLabel   argSchemeLabel;
    protected PremiseConnectionNode connectionNode;

    //
    //
    //
    protected List<ArgumentModel> premiseArgList;

    //
    //
    //
    protected int       position;
    protected boolean   cqFlag        = false;
    protected boolean   counterFlag   = false;
    protected boolean   subFlag       = false;
    protected boolean   hasComment    = false;
    protected boolean   visible       = true;
    
    protected   double    x;
    protected   double    y;

    /*
     Functions as an interem conclusion for chained arguments

     Associated view node: ChainNode
     */
    @Override
    public void initialize( URL url, ResourceBundle rb )
    {
        this.premiseArgList    = new ArrayList<>();
        this.conclusionArgList = new ArrayList<>();
        this.mainPane.getStyleClass().add( "chain-pane" );
        this.propositionRectangle.getStyleClass().add( "chain-rectangle" );

        this.mainPane.addEventFilter( DragEvent.DRAG_DROPPED, ( DragEvent event ) ->
        {
            Dragboard db = event.getDragboard();
            if ( db.hasContent( propositionModelDataFormat ) || db.hasString() )
            {
                try
                {
                    this.onDragDropped( event );
                    event.consume();
                } 
                catch ( IOException ex )
                {
                    Logger.getLogger( PremisePaneController.class.getName() ).log( Level.SEVERE, null, ex );
                }
            }
        } );
        
        //
        //  CONTEXT MENU REQUESTED EVENT
        //
        //  Index 4 in the CM item list is the add counter argument button
        //
        this.mainPane.addEventFilter( ContextMenuEvent.CONTEXT_MENU_REQUESTED, ( ContextMenuEvent event ) ->
        {
            //this.contextMenu.getItems().get( 0 ).setDisable( this.argNode.caFlag );
            this.showContextMenu( event );
            event.consume();
        } );        

        //
        //  DRAG DETECTED EVENT
        //
        // Intercept drag detected events so merged trees are not draggable
        this.mainPane.addEventFilter( MouseEvent.DRAG_DETECTED, ( MouseEvent event ) ->
        {
            event.consume();
        } );
        
        //
        //  MOUSE PRESSED EVENT
        //
        this.mainPane.addEventFilter( MouseEvent.MOUSE_PRESSED, ( MouseEvent event ) ->
        {
            this.x = event.getSceneX();
            this.y = event.getSceneY();
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
            
            this.onDragRelease( event );
            this.x = event.getSceneX();
            this.y = event.getSceneY();
        });
        
        //
        //  MOUSE ENTERED EVENT
        //
        // By setting the conclusion pane controller as soon as the mouse
        // is inside the pane, we create a workaround for minimizing subtrees!
        this.mainPane.addEventFilter( MouseEvent.MOUSE_ENTERED, ( MouseEvent event ) ->
        {
            this.parentControl.setConclusionPaneController( this );
        });

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
                        this.createEmptyProp();
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
        
        // Create context menu for adding new premises
        this.contextMenu = new ContextMenu();
        
        try
        {
            this.setContextMenuItems();
        } 
        catch ( IOException ex )
        {
            Logger.getLogger( ChainPaneController.class.getName() ).log( Level.SEVERE, null, ex );
        }
        
        this.setContextMenuEventFilter();
    }

    public void setConnector( ArgumentNode connection )
    {
        this.connector = connection;
    }

    public ArgumentNode getConnector()
    {
        return this.connector;
    }

    public void setCQLabel( ArgumentNode cqLabel )
    {
        this.cqLabel = cqLabel;
    }

    public ArgumentNode getCQLabel()
    {
        return this.cqLabel;
    }

    public void setArgSchemeLabel( ArgumentNode argLabel )
    {
        this.argSchemeLabel = ( ArgumentSchemeLabel ) argLabel;
    }

    public ArgumentNode getArgSchemeLabel()
    {
        return this.argSchemeLabel;
    }

    private void createEmptyProp() throws IOException
    {
        deleteProp();
        mainPane.getChildren().add( propositionRectangle );
        prop = new PropositionModel();
        addPropositionAsPremise( prop );
        Pane tPropBox = loadNewPropPane( prop );
        mainPane.getChildren().add( tPropBox );
    }

    /**
     set menu items for context menu
     * 7/11/19 - REMOVED DETACH ARG
     */
    private void setContextMenuItems() throws IOException
    {
        MenuItem addCounterArg = new MenuItem( "Add Counter Argument"  );
        MenuItem copy          = new MenuItem( "Copy (CTRL+C)"          );
        MenuItem paste         = new MenuItem( "Paste (CTRL+V)"         );
        
        this.setHandlerForCounterArg( addCounterArg     );
        this.setHandlerForCopy      ( copy            );
        this.setHandlerForPaste     ( paste           );        
            addCounterArg.setDisable( true );
        this.contextMenu.getItems().addAll( addCounterArg, copy, paste );
    }

    /*
     Adds a counter argument attached to this pane
     */
    private void setHandlerForCounterArg( MenuItem item )
    {
        item.setOnAction( action ->
        {
            try
            {
                this.argTree.addCounterArgument( conclusionArgList.get( 0 ).getConclusion(), argNode );
                this.argNode.caFlag = true;
                item.setDisable( true );
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

    /**
     add context menu event handler for mouse clicks
     */
    private void setContextMenuEventFilter()
    {
        this.mainPane.addEventFilter( ContextMenuEvent.CONTEXT_MENU_REQUESTED, ( ContextMenuEvent event ) ->
        {
            this.showContextMenu( event );
            event.consume();
        });
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
        this.contextMenu.show( this.mainPane, event.getScreenX(), event.getScreenY() );
        this.contextCoords = new Point2D( event.getScreenX(), event.getScreenY() );
        event.consume();
    }

    /**
     hides the context menu
     */
    private void closeContextMenu( Event event )
    {
        contextMenu.hide();
        event.consume();
    }

    public void addPremiseArgument( ArgumentModel arg )
    {
        premiseArgList.clear();
        premiseArgList.add( 0, arg );
    }

    public ArgumentModel getPremiseArgument()
    {
        return premiseArgList.get( 0 );
    }

    public void setPropositionModel( PropositionModel pm )
    {
        try
        {
            this.addProposition( pm );
        } catch ( IOException ex )
        {
            Logger.getLogger( PremisePaneController.class.getName() ).log(
                              Level.SEVERE, null, ex );
        }
    }

    public int getPosition()
    {
        return position;
    }

    public void setPosition( int position )
    {
        this.position = position;
    }
    
    public void onDragRelease( MouseEvent event )
    {
        double a = event.getSceneX() - this.x;
        double b = event.getSceneY() - this.y;
        
        Iterator<ArgumentNode> children = this.argTree.getChildrenOfConclusion( this ).iterator();
        children.next();
        
        while ( children.hasNext() )
        {
            this.argTree.shiftNode( children.next(), ( int ) a, ( int ) b);
        }
        
        Line translateLine = null;
        
        if ( this.connectionNode != null )
        {
            translateLine  =  ( Line ) this.connectionNode.getView();
        }
        
        if ( translateLine != null )
        {
            translateLine.setEndX( ( int ) ( translateLine.getEndX() + a ) );
            translateLine.setEndY( ( int ) ( translateLine.getEndY() + b ) );
        }
    }

    /*
     Listens for dragdropped events on this pane and determines how to handle
     */
    private void onDragDropped( DragEvent event ) throws IOException
    {
        Dragboard db      = event.getDragboard();
        boolean   dropped = false;
        
        if ( db.hasContent( propositionModelDataFormat ) )
        {
            PropositionModel tProp = ( PropositionModel ) db.getContent( propositionModelDataFormat );
            addProposition( tProp );
            hasProp = true;
            dropped = true;
        } 
        else if ( db.hasString() )
        {
            String treeID = db.getString();
            if (  ! treeID.equals( argTree.getTreeID() ) )
            {
                this.argTree.createMultiArgChain( treeID, this );
                dropped = true;
            }
        }
        
        event.setDropCompleted( dropped );
        event.consume();
    }

    @Override
    public void addProposition( PropositionModel prop ) throws IOException
    {
        this.deleteProp();
        this.mainPane.getChildren().add( propositionRectangle );
        this.addPropositionAsPremise( prop );
        this.addPropositionAsConclusion( prop );
        this.propBox = this.loadNewPropPane( prop );
        this.mainPane.getChildren().add( propBox );
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
        } );
    }

    /*
     Loads new proposition within pane
     */
    private Pane loadNewPropPane( PropositionModel prop ) throws IOException
    {
        try
        {
            this.contextMenu.getItems().clear();
            this.setContextMenuItems();
        } catch ( IOException ex )
        {
            Logger.getLogger( ConclusionPaneController.class.getName() ).log( Level.SEVERE, null, ex );
        }
        if ( this.propBoxController != null )
        {
            this.propBoxController.deleteComment();
        }
        FXMLLoader loader = new FXMLLoader( getClass().getResource( "/fxml/PropositionBox.fxml" ) );
        Pane tPropBox = loader.load();
        PropositionBoxController propControl = loader.<PropositionBoxController>getController();
        this.prop = prop;
        this.propBox = tPropBox;
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

    public boolean getCQFlag()
    {
        return this.cqFlag;
    }

    public void setCQFlag()
    {
        this.cqFlag =  ! cqFlag;
    }

    public boolean getCounterFlag()
    {
        return this.counterFlag;
    }

    public void setCounterFlag()
    {
        this.counterFlag =  ! counterFlag;
    }
    
    public void setConnectionNode( PremiseConnectionNode pcn )
    {
        this.connectionNode = pcn;
    }
    
    public PremiseConnectionNode getConnectionNode()
    {
        return this.connectionNode;
    }
}
