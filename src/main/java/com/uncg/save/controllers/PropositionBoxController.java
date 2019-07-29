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
import com.uncg.save.NodeType;
import com.uncg.save.models.PropositionModel;
import com.uncg.save.util.FXUtils;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

/**
 *  FXML controller class for the view element that represents a proposition. Meant
 *  to either float in the construction area or can be placed in a premise or
 *  conclusion
 *
 */
public class PropositionBoxController implements Initializable
{
    //
    //  Pane references
    //
    @FXML protected GridPane              mainGridPane;
    protected Pane                        parentContainer;
    protected Pane                        commentPane;

    //
    //  Miscellaneous references
    //
    protected ArgumentCertaintyPaneController     argCertaintyController = null;
    protected ConstructionAreaController          constControl;
    private PropositionModel                      prop;

    //
    //
    //
    @FXML protected TextArea            text;               //TextBox for typing
    @FXML protected Button              minimizeButton;     //Self-explanatory
    
    //
    //  Enum for determining what type of proposition
    //  this node is
    //
    protected NodeType                  nodeType;

    //
    //  Booleans for determining specific states
    //
    protected boolean                   hasComment     = false;
    protected boolean                   commentVisible = false;
    protected boolean                   minimized      = false;
    private   boolean                   hasDefaultText = true;
    
    //
    //  This will control which propositions have the box to enable
    //  a red/green outline.
    //
    private boolean                     isProConProp   = true;  //@TODO

    //
    //  Dimensions
    //
    private final int                      PANE_HEIGHT = 145;
    private final int                 TEXT_AREA_HEIGHT = 100;

    /**
      * Initializes the controller class.
      */
    @Override
    public void initialize( URL url, ResourceBundle rb )
    {
        this.text.getStyleClass().add( "text-area1" );
        this.mainGridPane.getStyleClass().add( "grid-pane-prop" );
        this.mainGridPane.setPrefHeight( 125 );
        this.mainGridPane.setCache( false );

        ContextMenu contextMenu = new ContextMenu();
        
        this.minimizeButton.getStyleClass().add( "minimize-button" );
        this.minimizeButton.setPrefSize( 20.0, 10.0 );
        this.text.setCache( false );        
        this.text.setContextMenu( contextMenu );
        this.text.setEditable( true );
        this.text.setWrapText( true );
        
        try
        {
            this.setContextMenuItems( contextMenu );
        } 
        catch ( IOException ex )
        {
            Logger.getLogger( PropositionBoxController.class.getName() ).log( Level.SEVERE, null, ex );
        }

        this.mainGridPane.addEventFilter( MouseEvent.DRAG_DETECTED, ( MouseEvent event ) ->
        {
            this.dragDetected( event );
            event.consume();
        } );

        this.mainGridPane.addEventFilter( DragEvent.DRAG_DROPPED, ( DragEvent event ) ->
        {
            this.dragDrop( event );
        } );
    }

    /**
      * Set menu items for context menu
      */
    private void setContextMenuItems( ContextMenu contextMenu ) throws IOException
    {
        MenuItem deleteProp    = new MenuItem( "Delete Proposition" );
        //MenuItem toggleComment = new MenuItem( "Show/Hide Comment"  );
        MenuItem clearText     = new MenuItem( "Clear Text"         );

        setHandlerForDeleteProp( deleteProp    );
        //setHandlerForComment   ( toggleComment );
        setHandlerForClearText ( clearText     );

        contextMenu.getItems().addAll( deleteProp, clearText );
    }

    /**
     * Clears all text except for the proposition's title.
     * 
     * @param item 
     */
    private void setHandlerForClearText( MenuItem item )
    {
        item.setOnAction( action ->
        {
            this.text.clear();
            this.text.setText( this.prop.getTitle() );
        } );
    }

   /**
    * 
    * @param item
    * @throws IOException 
    */
   private void setHandlerForComment( MenuItem item ) throws IOException
    {
        item.setOnAction( action ->
        {
            if (  ! hasComment )
            {
                try
                {
                    double x = 0;
                    double y = 0;
                    this.commentPane = loadComment();
                    
                    if ( constControl.getMainPane().getChildren().contains( this.mainGridPane ) )
                    {
                        x = mainGridPane.getLayoutX();
                        y = mainGridPane.getLayoutY();
                    } 
                    else
                    {
                        for ( int i = 0; i < this.constControl.getMainPane().getChildren().size(); i ++ )
                        {
                            if ( this.constControl.getMainPane().getChildren().get( i ) instanceof Pane )
                            {
                                Pane testPane = ( Pane ) constControl.getMainPane().getChildren().get( i );
                                if ( testPane.getChildren().contains( this.mainGridPane ) )
                                {
                                    Bounds paneBounds = FXUtils.nodePosition( testPane );
                                    x = paneBounds.getMaxX();
                                    y = paneBounds.getMaxY();
                                }
                            }
                        }
                    }
                    this.commentPane.setTranslateX( ( int ) ( x - 2 ) );
                    this.commentPane.setTranslateY( ( int ) ( y - 37 ) );
                } 
                catch ( IOException ex )
                {
                    Logger.getLogger( PremisePaneController.class.getName() ).log( Level.SEVERE, null, ex );
                }
                
                this.hasComment = true;
                this.constControl.getMainPane().getChildren().add( commentPane );
            } 
            this.commentVisible = ! commentVisible;
            this.setCommentVisibility( commentVisible );
        } );
    }
   
   /**
    * Toggles the visibility of the comment pane.
    * 
    * @param v 
    */
    public void setCommentVisibility( boolean v )
    {
        if( this.commentPane == null )
        {
            return;
        }
        this.commentPane.setVisible( v );
        this.commentVisible = v;
    }

    /**
     * 
     */
    @FXML
    private void setArgumentState()
    {
        throw new UnsupportedOperationException( "NO longer a feature; need to remove from XML!" );
    }
    
    /**
     * Toggles the visibility/minimize state of the proposition pane.
     */
    @FXML
    public void toggleMinimize()
    {
        this.minimized = ! minimized;
        
        this.onMinimizeEvent();
        this.setCommentVisibility( ! this.minimized );
        
        // Minimizes the currently visible elements except the minimize button
        this.parentContainer.getChildren().get( 0 ).setVisible( ! minimized );
        this.mainGridPane.getChildren().get( 0 ).setVisible( ! minimized );     //Index 0: Pane(?)
        this.text.setVisible( ! minimized ); 
       
        if( this.argCertaintyController != null && this.argCertaintyController.visible )
        {
            this.argCertaintyController.toggleVisible();
        }
        
        String        textS    = this.text.getText();
       //String[]      strArray = textS.split(  "\\s+" );
        StringBuilder minText  = new StringBuilder();
            
        if ( this.minimized )
        {
            if ( this.prop != null && this.prop.getTitle() != null && !this.prop.getTitle().equals( "" ) )
            {
                minText.append( this.prop.getTitle() ).append( " + " );
            }
            else
            {
                int colonIndex = textS.indexOf( ":" );
//                
//                for( int i = 0; i < strArray.length && i < 3; i++ )
//                {
//                    minText.append( strArray[ i ] ).append( " " );
//                }

                minText.append( textS.substring( 0, colonIndex ) + "+" );
            }
        }
        else
        {
            minText.append( "-" );
        }

        Button minimize = ( Button ) this.mainGridPane.getChildren().get( 1 );  //Index 2: Minimize button
        minimize.setText( minText.toString() );
        
        if( this.minimized )
        {
            minimize.setPrefWidth( 200.0 );            
            minimize.setAlignment( Pos.CENTER );
            GridPane.setHalignment( minimize, HPos.CENTER );
            
            if ( this.nodeType == NodeType.CONCLUSION )
            {
                GridPane.setValignment( minimize, VPos.BOTTOM );
            }
        } 
        else 
        {
            minimize.setPrefWidth( 20.0 );            
            GridPane.setHalignment( minimize, HPos.RIGHT );
            GridPane.setValignment( minimize, VPos.TOP );
        }
    }

    /**
     * 
     * @return
     * @throws IOException 
     */
    private Pane loadComment() throws IOException
    {
        FXMLLoader loader = new FXMLLoader( getClass().getResource( "/fxml/CommentPane.fxml" ) );
        this.commentPane = loader.load();
        CommentPaneController commentControl = loader.<CommentPaneController>getController();
        commentControl.setParent( this );
        commentControl.setComment( this.prop.getComment() );
        this.commentPane.toFront();
        return this.commentPane;
    }

    /**
     * 
     * @param x
     * @param y 
     */
    public void moveComment( double x, double y )
    {
        if ( this.commentPane != null )
        {
            this.commentPane.setTranslateX( ( int ) ( this.commentPane.getTranslateX() + x ) );
            this.commentPane.setTranslateY( ( int ) ( this.commentPane.getTranslateY() + y ) );
        }
    }

    /**
     * 
     */
    @FXML
    private void updateText()
    {
        // We only want to update the text if it's a PREMISE being updated, not a CPC or CQPC
        if ( !this.text.getText().contains( this.prop.getTitle() ) && this.constControl.getConclusionPaneController() == null )
        {
            this.text.setText( this.prop.getTitle() + ": " );
            this.text.positionCaret( this.text.getLength() );
        }
        this.prop.setProposition( text.getText() );
    }

    /**
     * 
     */
    @FXML
    private void clearTextOnDown()
    {

    }

    /**
     * 
     * @param delete 
     */
    private void setHandlerForDeleteProp( MenuItem delete )
    {
        delete.setOnAction( action ->
        {
            this.parentContainer.getChildren().remove( this.mainGridPane );
            this.deleteComment();
            action.consume();
        } );
    }

    /**
     * 
     * @param event 
     */
    private void dragDrop( DragEvent event )
    {
        boolean success = false;
        Dragboard db    = event.getDragboard();
        event.setDropCompleted( success );
        event.consume();
    }

    /**
      * Event handler method for drag detection on prop elements passes a reference
      *the the PropositionModel object represented by the dragged view
      */
    @FXML
    private void dragDetected( Event event )
    {
        Dragboard db = this.mainGridPane.startDragAndDrop( TransferMode.MOVE );
        ClipboardContent content = new ClipboardContent();
        content.put( propositionModelDataFormat, this.prop );
        db.setContent( content );
        event.consume();
    }

    /**
      * Event handler for completion of drag event. Deletes the view associated
      * with this controller to simulate movement
      * 
      * @param event
      */
    @FXML
    private void dragDone( DragEvent event )
    {
        if ( event.isAccepted() )
        {
            if ( this.constControl.getMainPane().getChildren().contains( this.commentPane ) )
            {
                this.constControl.getMainPane().getChildren().remove( this.commentPane );
            }
            this.constControl.removePane( this.mainGridPane );
        }
        event.consume();
    }

    /**
     * 
     */
    public void deleteComment()
    {
        if ( this.constControl.getMainPane().getChildren().contains( this.commentPane ) )
        {
            this.constControl.getMainPane().getChildren().remove( this.commentPane );
        }
        this.hasComment     = false;
        this.commentVisible = false;
    }

    /**
     * 
     * @param menu
     * @throws IOException 
     */
    public void setContextMenu( ContextMenu menu ) throws IOException
    {
        this.text.setContextMenu( menu );
        //MenuItem toggleComment = new MenuItem( "Show/Hide Comment" );
        //this.setHandlerForComment( toggleComment );
        //this.text.getContextMenu().getItems().add( toggleComment );
    }

    /**
     * Controls how the tree is minimized.
     * 
     * If there is not a ppc, cqpc, or cpc, then the entire tree is minimized. The 
     * big problem with THAT is if the user minimizes ANY counter argument or 
     * critical question, it then minimizes the entire tree.
     */
    private void onMinimizeEvent()
    {
        this.constControl.getArgTreeMap().values().forEach( ( tree ) -> 
        {
            if ( this.constControl.getPremisePaneController() != null )
            {
                tree.minimizeTree( this.constControl.getPremisePaneController(), !this.minimized );
            }
            else if ( this.constControl.getConclusionPaneController() != null )
            {
                tree.minimizeTree( this.constControl.getConclusionPaneController(), !this.minimized );
            } 
            else if ( this.constControl.getCQPaneController() != null )
            {
                tree.minimizeTree( this.constControl.getCQPaneController(), !this.minimized );                
            }
            else
            {
                tree.minimizeTreeFromRoot();
//                throw new IllegalStateException( "PPC and CPC are both null, did you"
//                                               + " try to minimize a merged subtree,"
//                                               + " a critical question, or counter argument?" );
            }
        });
    }

    public void translateComment( double x, double y )
    {
        this.commentPane.setTranslateX( commentPane.getTranslateX() + x );
        this.commentPane.setTranslateY( commentPane.getTranslateY() + y );
    }
   
    public void setViewText( String s )
    {
        this.text.setStyle( "-fx-text-fill: black;" );
        this.text.setText( s );
    }

    public void setInitialText( String s )
    {
        this.text.setStyle( "-fx-text-fill: black;" );
        this.text.setText( s );
    }
    
    public void setComment( String comment )
    {
        this.prop.setComment( comment );
    }

    public Pane getComment()
    {
        return this.commentPane;
    }

    public boolean getHasComment()
    {
        return this.hasComment;
    }
    
    public void setText( String s ) 
    {
        if ( this.text != null )
        {
            this.text.setText( s );
            this.prop.setProposition( s );
        }
    }    
    
    public String getTextAreaText()
    {
        return this.text.getText();
    }

    public String getPropText()
    {
        return prop.getProposition();
    }

    public String getCommentText()
    {
        return prop.getComment();
    }    
    
    /**
     * Sets the proposition model that the view associated with this controller is
     * based on
     * 
     * @param prop PropositionModel
     * 
     * @throws java.io.IOException
     */
    public void setPropModel( PropositionModel prop ) throws IOException
    {
        this.prop = prop;
        this.text.setText( prop.getProposition() );
    }

    /**
     Sets the controller for the parent of the view this controller is
     associated with

     @param control ConstructionAreaController
     */
    public void setConstructionAreaControl( ConstructionAreaController control )
    {
        this.constControl = control;
    }

    public void setColor( double r, double b )
    {

    }

    public void setParentContainer( Pane container )
    {
        this.parentContainer = container;
    }
    
}
