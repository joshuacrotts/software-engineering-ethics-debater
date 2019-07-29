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
import com.uncg.save.argumentviewtree.PremiseNexusNode;
import com.uncg.save.models.ArgumentModel;
import com.uncg.save.models.PremiseModel;
import com.uncg.save.models.PropositionModel;
import com.uncg.save.util.FXUtils;
import com.uncg.save.util.MouseUtils;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;

/**
 FXML controller for conclusion of
 multiple-arguments-sharing-the-same-conclusion structure when it is the root of
 an argument

 */
public class MultiArgConclusionPaneController extends ConclusionPaneController implements Initializable
{
    private PremiseNexusNode nexus;
    
    protected double x;
    protected double y;
    
    protected boolean[]     resizeNexus = { false, false };

    @Override
    public void initialize( URL url, ResourceBundle rb )
    {
        this.conclusionArgList = new ArrayList<>();
        this.mainPane.getStyleClass().add( "conclusion-pane" );
        this.propositionRectangle.getStyleClass().add( "conclusion-rectangle" );
        this.mainPane.addEventFilter( DragEvent.DRAG_DROPPED, ( DragEvent event ) ->
        {
            Dragboard db = event.getDragboard();
            if ( db.hasContent( propositionModelDataFormat ) || db.hasString() )
            {
                try
                {
                    this.onDragDropped( event );
                    event.consume();
                } catch ( IOException ex )
                {
                    Logger.getLogger( PremisePaneController.class.getName() ).log( Level.SEVERE, null, ex );
                }
            }
        } );    

        //
        //  DRAG DETECTED EVENT
        //
        this.mainPane.addEventFilter( MouseEvent.DRAG_DETECTED, ( MouseEvent event ) -> {
            
            if ( this.propBoxController != null && !this.propBoxController.text.getSelectedText().isEmpty() )
            {
                this.dragging = false;
                event.consume();
            }
            else
            {
                event.setDragDetect( true );
                this.parentControl.setConclusionPaneController( this );
                this.dragDetected();
                this.dragging = true;

                if ( this.propBoxController != null && this.propBoxController.text != null)
                {
                    this.propBoxController.text.setTooltip( null );
                }   

                event.consume();
            }
        } );

        //
        //  CONTEXT MENU REQUESTED EVENT
        //
        this.mainPane.addEventFilter( ContextMenuEvent.CONTEXT_MENU_REQUESTED,( ContextMenuEvent event ) ->
        {
            this.contextMenu.getItems().get( 0 ).setDisable( this.argNode.caFlag );
            this.showContextMenu( event );
            event.consume();
        } );

        //
        //  MOUSE ENTERED EVENT
        //
        this.mainPane.addEventFilter( MouseEvent.MOUSE_ENTERED, ( MouseEvent mouseEvent ) ->
        {
            this.parentControl.setConclusionPaneController( this );
            
            if ( this.dragging || this.propBoxController == null || 
                 this.propBoxController.text == null || this.prop == null )
            {
                mouseEvent.consume();
            }
            else
            {
                this.propBoxController.text.setTooltip( new Tooltip( this.prop.getDefinition() ) );
            }
            
            this.parentControl.setConclusionPaneController( this );
        });

        // Create context menu for adding new premises
        this.contextMenu = new ContextMenu();
        this.setContextMenuItems();
    }

    @Override
    public void setArgNode( ArgumentNode argNode )
    {
        this.argNode = argNode;
    }

    /**
     set menu items for context menu
     */
    private void setContextMenuItems()
    {
        MenuItem counterArg  = new MenuItem( "Add a Counter Argument" );
        MenuItem deleteArg   = new MenuItem( "Delete Argument"        );
        MenuItem copy        = new MenuItem( "Copy (CTRL+C)"          );
        MenuItem paste       = new MenuItem( "Paste (CTRL+V)"         );
            
        this.setHandlerForCounterArg( counterArg      );
        this.setHandlerForDeleteArg ( deleteArg       );
        this.setHandlerForCopy      ( copy            );
        this.setHandlerForPaste     ( paste           );
        
        this.contextMenu.getItems().addAll( counterArg, deleteArg, copy, paste );
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
                item.setDisable(  true );
            } 
            catch ( IOException ex )
            {
                Logger.getLogger( ConclusionPaneController.class.getName() ).log( Level.SEVERE, null, ex );
            }
        } );
    }

    /**
     sets the handler for deleting an argument tree

     @param item MenuItem
     */
    private void setHandlerForDeleteArg( MenuItem item )
    {
        item.setOnAction( action ->
        {
            this.argTree.deleteArgument();
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

    private void onDragDropped( DragEvent event ) throws IOException
    {
        Dragboard db = event.getDragboard();
        boolean dropped = false;
        if ( db.hasContent( propositionModelDataFormat ) )
        {
            PropositionModel tProp = ( PropositionModel ) db.getContent( propositionModelDataFormat );
            this.addProposition( tProp );
            dropped = true;
        } else if ( db.hasString() )
        {
            String treeID = db.getString();
            if ( ! treeID.equals( this.argTree.getTreeID() ) )
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
        this.mainPane.getChildren().add( this.propositionRectangle );
        this.addPropositionAsConclusion( prop );
        Pane tPropBox = this.loadNewPropPane( prop );
        this.mainPane.getChildren().add( tPropBox );
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
        this.propBox  = tPropBox;
        this.prop     = prop;
        propControl.setConstructionAreaControl( parentControl );
        propControl.setPropModel( prop );
        propControl.setParentContainer( mainPane );
        this.propBoxController      = propControl;
        return tPropBox;
    }

    private void dragDetected()
    {
        Dragboard db = mainPane.startDragAndDrop( TransferMode.MOVE );
        ClipboardContent content = new ClipboardContent();
        content.putString( argTree.getTreeID() );
        db.setContent( content );
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
    private void closeContextMenu()
    {
        this.contextMenu.hide();
    }

    @Override
    public void addConclusionArgumentModel( ArgumentModel arg )
    {
        this.conclusionArgList.add( arg );
    }

    public void setPropositionModel( PropositionModel pm )
    {
        try
        {
            Pane tPropBox = loadNewPropPane(pm);
            this.mainPane.getChildren().add(tPropBox);
            this.addProposition( pm );
        } catch ( IOException ex )
        {
            Logger.getLogger( PremisePaneController.class.getName() ).log( Level.SEVERE, null, ex );
        }
    }
    
    @Override
    public void setPremiseNexus( PremiseNexusNode nexus )
    {
        this.nexus = nexus;
        
        this.nexus.getView().addEventFilter( MouseEvent.MOUSE_PRESSED, ( MouseEvent event ) -> 
        {
            // Empty event for now
        } );        
    }
    
    public PremiseNexusNode getPremiseNexus()
    {
        return this.nexus;
    } 
    
    protected boolean mouseInsideLeftRectNexus( int mx, int my )
    {
        if ( this.nexus.getView() == null )
        {
            return false;
        }
        
        Bounds rectBounds = FXUtils.nodePosition( this.nexus.getView() );
        
        if ( rectBounds == null )
        {
            return false;
        }
        
        int width  = ( int ) rectBounds.getMinX() + 25;
        int height = ( int ) rectBounds.getMaxY() + 5;
        
        return   ( mx >=  ( int ) rectBounds.getMinX() - 5 ) && ( mx <= width ) 
              && ( my >=  ( int ) rectBounds.getMinY() - 5 ) && ( my <= height );
    }
    
    protected boolean mouseInsideRightRectNexus( int mx, int my )
    {
        if ( this.nexus.getView() == null )
        {
            return false;
        }
        
        Bounds rectBounds = FXUtils.nodePosition( this.nexus.getView() );
        
        if ( rectBounds == null )
        {
            return false;
        }
        
        int leftEdge  = ( int ) rectBounds.getMaxX();
        int rightEdge = ( int ) rectBounds.getMaxX() + 25;
        int topEdge   = ( int ) rectBounds.getMinY() - 5;
        int bottomEdge = ( int ) rectBounds.getMaxY() + 5;
        
        return    mx >= leftEdge && mx <= rightEdge  
               && my >= topEdge && my <= bottomEdge;
    }       
}
