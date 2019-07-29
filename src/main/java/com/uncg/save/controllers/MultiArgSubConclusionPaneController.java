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

import com.uncg.save.argumentviewtree.ArgumentNode;
import com.uncg.save.argumentviewtree.ArgumentViewTree;
import com.uncg.save.models.ArgumentModel;
import com.uncg.save.util.LayoutUtils;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

/**
 FXML controller for the individual conclusion components of
 multiple-arguments-sharing-the-same-conclusion structures. Appear as little
 black circles in the GUI. Allow arguments sharing the same conclusion to still
 act as independent arguments.

 */
public class MultiArgSubConclusionPaneController implements Initializable
{
    //////////////////////// INSTANCE VARIABLES /////////////////////////////     
    
    //
    //
    //
    @FXML protected Pane                    mainPane;
    @FXML private Circle                    anchorCircle;
    @FXML private Circle                    insideAnchorCircle;

    //
    //
    //
    private ArgumentViewTree                argTree;
    private ArgumentModel                   argument;
    private ArgumentNode                    argNode;

    //
    //
    //
    private ContextMenu                     contextMenu;
    private Point2D                         contextCoords;

    //
    //
    //
    private ArgumentCertaintyPaneController certaintyControl;
    private ConstructionAreaController      constructionControl;
    
    //
    //
    //
    protected boolean[]                     resizeWidth       = { false, false };
    protected boolean[]                     resizeHeight      = { false, false };    

    //////////////////////// INSTANCE VARIABLES /////////////////////////////      
    
    /**
     Initializes the controller class.
     */
    @Override
    public void initialize( URL url, ResourceBundle rb )
    {
        // create context menu for adding new premises
        this.contextMenu = new ContextMenu();
        this.setContextMenuItems();
        this.setContextMenuEventFilter();
        
        this.mainPane.addEventFilter( MouseEvent.MOUSE_ENTERED, ( MouseEvent event ) -> 
        {
            this.constructionControl.setMultiArgSubCPC( this );
        
            
        } );
        
        this.mainPane.addEventFilter( MouseEvent.MOUSE_PRESSED, ( MouseEvent event ) -> 
        {
            // Empty for now
        } );
        
        this.anchorCircle.setRadius( 10.0 );
        this.insideAnchorCircle.setRadius( this.anchorCircle.getRadius() - 3 );
        
    }

    public void setArgNode( ArgumentNode argNode )
    {
        this.argNode = argNode;
    }

    /**
     set menu items for context menu
     */
    private void setContextMenuItems()
    {
        MenuItem detachChain = new MenuItem( "Detach Argument" );
        this.setHandlerForDetachChain( detachChain );
        this.contextMenu.getItems().addAll( detachChain );
    }

    private void setHandlerForDetachChain( MenuItem item )
    { 
        item.setOnAction( action ->
        {
            Point2D localCoords = LayoutUtils.getLocalCoords( constructionControl.getMainPane(),
                                                              contextCoords.getX(),
                                                              contextCoords.getY() );
            
            this.detachMultiArgument( localCoords );
            action.consume();
        } );
    }
    
    public void detachMultiArgument( Point2D localCoords )
    {
        this.argTree.detachMultiArgument( this, localCoords );
    }

    /**
     add context menu event handler for mouse clicks
     */
    private void setContextMenuEventFilter()
    {
        this.anchorCircle.addEventFilter( ContextMenuEvent.CONTEXT_MENU_REQUESTED, ( ContextMenuEvent event ) ->
        {
            this.showContextMenu( event );
            event.consume();
        } );
        
        this.insideAnchorCircle.addEventFilter( ContextMenuEvent.CONTEXT_MENU_REQUESTED, ( ContextMenuEvent event ) ->
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
        ArgumentNode traverse = this.argNode.getParent().getParent().getParent().getParent();
        if ( traverse != this.argTree.getRoot() )
        {
            this.contextMenu.getItems().get( 0 ).setDisable( true );
        }        
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

    public void setArgument( ArgumentModel arg )
    {
        this.argument = arg;
    }

    public void setArgumentViewTree( ArgumentViewTree avt )
    {
        this.argTree = avt;
    }

    public void setParentControl( ConstructionAreaController control )
    {
        this.constructionControl = control;
    }

    public void setCertaintyControl( ArgumentCertaintyPaneController control )
    {
        this.certaintyControl = control;
    }

    public ArgumentCertaintyPaneController getCertaintyControl()
    {
        return this.certaintyControl;
    }

    public Pane getMainPane()
    {
        return this.mainPane;
    }

    public ArgumentModel getArgument()
    {
        return this.argument;
    }
    
    public ArgumentViewTree getArgTree()
    {
        return this.argTree;
    }
}
