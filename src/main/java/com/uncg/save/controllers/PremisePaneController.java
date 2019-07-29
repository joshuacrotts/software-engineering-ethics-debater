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
import com.uncg.save.MainApp;
import static com.uncg.save.MainApp.propositionModelDataFormat;
import static com.uncg.save.argumentviewtree.ArgumentNode.PREMISE_WIDTH;
import com.uncg.save.NodeType;
import com.uncg.save.argumentviewtree.ArgumentNode;
import com.uncg.save.argumentviewtree.ArgumentViewTree;
import com.uncg.save.argumentviewtree.PremiseConnectionNode;
import com.uncg.save.models.ArgumentModel;
import com.uncg.save.models.PropositionModel;
import com.uncg.save.util.FXUtils;
import com.uncg.save.util.MouseUtils;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML controller for premise view components of an argument structure.
 */
public class PremisePaneController implements Initializable, Draggable
{
    //
    // Pane objects
    //
    @FXML protected Pane                      mainPane;
    private Pane                              propBox;

    //
    // Arg Tree this PPC is a part of
    //
    private ArgumentViewTree                  argTree;
    
    //
    // Nodes and model objects
    //
    private PropositionModel                  prop;
    private ArgumentModel                     argument;
    private ArgumentNode                      argNode;
    private PremiseConnectionNode             connectionNode;
    
    //
    // Miscellaneous objects
    //
    private      String                       text;
    private      Point2D                      contextCoords;
    private      Label                        schemeLabel;
    @FXML public Rectangle                    propositionRectangle;

    //
    // Controller references
    //
    protected PropositionBoxController        propBoxController  = null;
    protected ConstructionAreaController      parentControl      = null;
    protected ArgumentCertaintyPaneController certaintyControl   = null;
    
    //
    // Booleans primarily for determining the 
    // state of this premise 
    //
    private int                               position;
    private boolean                           initialized        = false;
    private boolean                           hasProp            = false;
    private boolean                           dragging           = false;

    //
    // Left, right, top, and bottom points
    // on the rectangle; set to true when the
    // mouse is over any of these, false otherwise
    //  
    protected boolean[]                       resizeWidth        = { false, false }; //L, R
    protected boolean[]                       resizeHeight       = { false, false }; //T, B    
    
    //
    // Right-clickable menu showing different 
    // options for the premise pane
    //
    private ContextMenu                       contextMenu;
    
    //
    // Coordinates for mouse position 
    // when dragging around the premise
    // pane object
    //
    private double                          x;
    private double                          y;
    private int                             proConStatus = 0;

    @Override
    public void initialize( URL url, ResourceBundle rb )
    {
        this.mainPane.getStyleClass().add( "premise-pane" );
        this.mainPane.setCache( false );
        this.propositionRectangle.getStyleClass().add( "premise-rectangle" );
        
        //
        //  DRAG DROPPED EVENT
        //
        //  Once the user drops the premise pane, we update the final coordinates
        //  of where it is.
        //
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
        
        //
        //  CONTEXT MENU REQUESTED EVENT
        //
        //  Index 5 in the CM item list is the add counter argument button
        //
        this.mainPane.addEventFilter( ContextMenuEvent.CONTEXT_MENU_REQUESTED, ( ContextMenuEvent event ) ->
        {
            if ( this.contextMenu != null )
            {
                this.showContextMenu( event );
            }
            
            event.consume();
        } );        

        //
        //  DRAG DETECTED EVENT
        //
        //  We need to detect drags, but prevent them so the user doesn't drag 
        //  the actual proposition pane away from the rectangle.
        //
        this.mainPane.addEventFilter( MouseEvent.DRAG_DETECTED, ( MouseEvent mouseEvent ) ->
        {
            mouseEvent.consume();
        } );                
        
        //
        //  MOUSE RELEASED EVENT
        //
        //  If we are done dragging, we can re-instantiate the tool-tip if it's 
        //  currently not there.
        //
        this.mainPane.addEventFilter( MouseEvent.MOUSE_RELEASED, ( MouseEvent mouseEvent ) ->
        {
            if ( this.dragging )
            {
                this.dragging = false;
                
                if ( this.propBoxController != null && this.propBoxController.text != null && 
                     this.propBoxController.text.getTooltip() == null &&
                    !this.propBoxController.text.getText().startsWith( "CQ" ) )
                {
                    this.propBoxController.text.setTooltip( new Tooltip( this.prop.getDefinition() ) );
                }
            }
        });
        
        //
        //  MOUSE DRAGGED EVENT
        //
        //  We can simulate movement by dropping and picking up the 
        //  pane over and over as the user moves the mouse while 
        //  clicking & dragging
        //
        this.mainPane.addEventFilter( MouseEvent.MOUSE_DRAGGED, ( MouseEvent mouseEvent ) ->
        {
            
            //
            // If the user is trying to select text, we need to prevent the pane from
            // dragging
            //
            if ( this.propBoxController != null && !this.propBoxController.text.getSelectedText().isEmpty() )
            {
                return;
            }
            // If the pane has a counter argument present, we need to translate
            // all its children in the appropriate direction. If not, we only need
            // to translate the parent.
            if ( ! this.argNode.caFlag )
            {
                this.onDragRelease( mouseEvent );
            } 
            else
            {
                this.onDragRelease( mouseEvent );
                Iterator<ArgumentNode> children = argTree.getChildrenOfPremise( this ).iterator();
                        
                // The first child in the iterator is the node itself so we need
                // to pool it before we start 
                children.next();
                        
                double deltaX = mouseEvent.getSceneX() - this.x;
                double deltaY = mouseEvent.getSceneY() - this.y;

                while ( children.hasNext() )
                {
                    this.argTree.shiftNode( children.next(), ( int ) deltaX, ( int ) deltaY );
                }

            }
            
            //
            // If we're currently dragging the pane, we don't want the tooltip
            // to arbitrarily appear because it's not clean.
            //
            this.dragging = true;
            this.x = mouseEvent.getSceneX();
            this.y = mouseEvent.getSceneY();  
            
            if ( this.propBoxController != null && this.propBoxController.text != null )
            {
                this.propBoxController.text.setTooltip( null );
            }
        } ); 
        
        //
        //  MOUSE PRESSED EVENT 
        //
        //  We need a way of distinguishing between mouse CLICKS and mouse PRESSES
        //  for when the user drags the premises around. This remediates that problem.
        //
        this.mainPane.addEventFilter( MouseEvent.MOUSE_PRESSED, ( MouseEvent mouseEvent ) ->
        {            
            this.parentControl.setPremisePaneController( this );
            this.x = mouseEvent.getSceneX();
            this.y = mouseEvent.getSceneY();          
        } );
        
        //
        //  MOUSE ENTERED EVENT
        //
        //  If the user enters the pane and they aren't dragging the mouse, then
        //  we can display the tooltip containing the definition.
        //
        this.mainPane.addEventFilter( MouseEvent.MOUSE_ENTERED, ( MouseEvent event ) -> 
        {
            if ( this.dragging || this.propBoxController == null || this.propBoxController.text == null 
                               || this.prop == null )
            {
                event.consume();
            }
            else if ( !this.propBoxController.text.getText().startsWith( "CQ" ) )
            {
                this.propBoxController.text.setTooltip( new Tooltip( this.prop.getDefinition() ) );
            }
        } );
        
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
     * Actual translation method for the pane and the line housing the pane.
     * 
     * @param _x
     * @param _y 
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
        MenuItem copy            = new MenuItem( "Copy (CTRL+C)"        );
        MenuItem paste           = new MenuItem( "Paste (CTRL+V)"       );
        MenuItem clearText       = new MenuItem( "Clear Text"           );
        MenuItem counterArg      = new MenuItem( "Add Counter Argument" );
            counterArg.setDisable( true );
        
        if ( MainApp.DEBUG )
        {
            MenuItem colorPickerMenu = new MenuItem( "Change Color" );
            this.setHandlerForColorPicker( colorPickerMenu );
            this.contextMenu.getItems().add( colorPickerMenu );
        }
        
        //this.setHandlerForCounterArg ( counterArg      );
        this.setHandlerForClearText  ( clearText       );
        this.setHandlerForCopy       ( copy            );
        this.setHandlerForPaste      ( paste           );

        this.contextMenu.getItems().addAll( copy,      paste, 
                                            clearText, counterArg   );
    }
    
    /**
     * This method creates a work-around for CQs that are added 
     * as premises rather than CQs themselves when loading a
     * tree back into SWED.
     */
    protected void addProConToggle()
    {
        MenuItem toggleProCon = new MenuItem( "Toggle Pro/Con" );
        
        this.setHandlerForToggle( toggleProCon );
        
        this.contextMenu.getItems().add( toggleProCon );
    }
    
    private void showContextMenu( ContextMenuEvent event )
    {
        /*
         call to hide() ensures that bugs arent encountered if multiple context
         menus are opened back to back
         */
        this.contextMenu.hide();
        this.contextMenu.show( mainPane, event.getScreenX(), event.getScreenY()  );
        this.contextCoords = new Point2D( event.getScreenX(), event.getScreenY() );
        event.consume();
    }    
    
    /**
     * Initializes the interactibility of the pane without the user having 
     * to click on the actual pane; allows the user to save/drag immediately
     */
    public void initializePane()
    {
        try
        {
            if (  ! this.hasProp )
            {
                this.deleteProp();
                this.contextMenu = new ContextMenu();
                this.mainPane.getChildren().add( this.propositionRectangle );
                this.prop = new PropositionModel();
                this.addPropositionAsPremise( this.prop );
                this.propBox = loadNewPropPane( this.prop, true );
                this.mainPane.getChildren().add( this.propBox );
                this.propBoxController.nodeType = NodeType.PREMISE;
                this.propBoxController.argCertaintyController = this.certaintyControl;
                this.parentControl.setPremisePaneController( this );
                this.hasProp = true;

                // Defines the title and definitino of a premise
                if ( !this.initialized )
                {
                    // We want this text to be immutable
                    String premiseTitle = this.getArgument().getSchemePremiseName( position );

                    // Definition
                    String premiseDef   = this.getArgument().getSchemePremiseDefinition( position );
                    
                    // If the user has saved text, it'll be loaded in instead of just the title
                    String savedText    = this.getArgument().getSchemePremiseText( position );
                    
                    this.propBoxController.text.setText( savedText != null ? savedText : premiseTitle + ": " );

                    this.prop.setTitle( premiseTitle );
                    this.prop.setDefinition( premiseDef );
                    this.prop.setProposition( this.getTextInTextArea() );
                    this.propBoxController.text.setTooltip( new Tooltip( this.prop.getDefinition() ) );
                    // Create context menu for adding new premises
                    this.setContextMenuItems();
                    
                    this.initialized = true;
                }
            }
        } 
        catch ( IOException ex )
        {
            Logger.getLogger( ConclusionPaneController.class.getName() ).log( Level.SEVERE, null, ex );
        }
    }
    
    /**
     * 
     * @param item 
     */
    private void setHandlerForClearText( MenuItem item )
    {
        item.setOnAction( action ->
        {
            this.propBoxController.text.clear();    
            this.propBoxController.text.setText( this.prop.getTitle() + ": ");
            this.propBoxController.text.positionCaret( this.propBoxController.text.getLength() );
        } );
    }

    /**
     * 
     * @param item 
     */
    private void setHandlerForToggle( MenuItem item )
    {
        item.setOnAction( action ->
        {
            switch( this.proConStatus )
            {
                case 0: this.getPropositionBoxController().text.setStyle( "-fx-border-color: #0f0; -fx-border-width: 4;" ); break; //Green
                case 1: this.getPropositionBoxController().text.setStyle( "-fx-border-color: #f00; -fx-border-width: 4;" ); break; //Red 
                case 2: this.getPropositionBoxController().text.setStyle( "-fx-border-color: #000; -fx-border-width: 0;" ); break; //Black
            }
            
            this.proConStatus++;
            
            if ( this.proConStatus > 2 )
            {
                this.proConStatus = 0;
            }
            
            action.consume();
        } );
    }
    
    /**
     * 
     * @param item 
     */
    private void setHandlerForColorPicker( MenuItem item )
    {
        item.setOnAction( action ->
        {
            this.configureColorPicker();
        } );        
    }    

    /**
     * 
     * @param item 
     */
    @Deprecated
    private void setHandlerForDetach( MenuItem item )
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
    
    /**
     * 
     * @param delete 
     */
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
     * 
     */
    public void certaintyVisibilityOff()
    {
        this.certaintyControl.killVisibility();
    }

    /**
     * 
     * @param d 
     */
    public void setViewColor( double d )
    {
        String color = FXUtils.toRGBCode( Color.rgb( FXUtils.ConvertToRed( d ), 0, FXUtils.ConvertToBlue( d ) ) );
        this.propBoxController.text.setStyle( "-fx-background-color: " + color + "; -fx-text-fill: white;" );
    }

    /**
     * 
     */
    private void extractProp()
    {
        Bounds localBounds   = mainPane.getBoundsInLocal();
        Bounds screenBounds  = mainPane.localToScreen( localBounds );
        Point2D targetCoords = new Point2D( screenBounds.getMinX() + 50, screenBounds.getMinY() + 50 );
        parentControl.createProp( prop, targetCoords );
    }

    /**
     * 
     */
    private void removeProp()
    {
        this.contextMenu.getItems().clear();
        
        if ( this.argument != null )
        {
            this.argument.removePremise( this.position );
        }
        this.prop    = null;
        this.hasProp = false;
        
        this.mainPane.getChildren().remove( this.propBox );
        this.mainPane.getChildren().add( this.schemeLabel );        
        this.propBoxController.deleteComment();
    }

    /**
     * 
     * @param item 
     */
    private void setHandlerForCounterArg( MenuItem item )
    {        
        throw new UnsupportedOperationException( "Disabled as of 07/24/19 - counter arg panes disabled" );
//        item.setOnAction( action ->
//        {
//            try
//            {
//                this.argTree.addCounterArgument( this.argument.getPremise( this.position ), this.argNode );
//                this.argNode.caFlag = true;
//                item.setDisable( true );
//            } 
//            catch ( IOException ex )
//            {
//                Logger.getLogger( ConclusionPaneController.class.getName() ).log( Level.SEVERE, null, ex );
//            }
//        } );
    }

    /**
     * 
     * @param lbl 
     */
    public void setSchemeLabel( Label lbl )
    {
        this.schemeLabel = lbl;
        this.schemeLabel.setStyle( "-fx-text-fill: black;" );
        this.mainPane.getChildren().add( lbl );
    }

    /**
     * 
     * @param pm 
     */
    public void setPropositionModel( PropositionModel pm )
    {
        Pane tPropBox;
        try
        {
            this.propBox = this.loadNewPropPane( pm );
            this.mainPane.getChildren().add( this.propBox );
        } catch ( IOException ex )
        {
            Logger.getLogger( PremisePaneController.class.getName() ).log( Level.SEVERE, null, ex );
        }
    }

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
        else if ( db.hasString() && ! db.getString().equals( argTree.getTreeID() ) )
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

    /**
     * Change 07/08/19: Renamed this propmodel variable from "prop" to "propModel"
     *                  to avoid conflictions with the instance variable prop.
     * 
     * @param event
     * @throws IOException 
     */
    private void dropProp( DragEvent event ) throws IOException
    {
        Dragboard db               = event.getDragboard();
        PropositionModel propModel = ( PropositionModel ) db.getContent( propositionModelDataFormat );
        this.deleteProp();
        this.mainPane.getChildren().add( propositionRectangle );
        this.hasProp = true;
        this.addProposition( propModel );
    }

    private void dropTree( DragEvent event ) throws IOException
    {
        Dragboard db = event.getDragboard();
        String treeID = db.getString();
        this.argTree.mergeTree( treeID, this );
    }

    private void addProposition( PropositionModel prop ) throws IOException
    {
        this.prop = prop;

        this.addPropositionAsPremise( prop );
        this.propBox = loadNewPropPane( prop );
        this.mainPane.getChildren().add( propBox );
    }

    private void addPropositionAsPremise( PropositionModel prop )
    {
        if ( this.argument != null )
        {
            this.argument.addPremise( prop, position );
        }
    }

    private Pane loadNewPropPane( PropositionModel prop ) throws IOException
    {
        this.contextMenu.getItems().clear();
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
        this.hasProp = true;
        return tPropBox;
    }

    private Pane loadNewPropPane( PropositionModel prop, boolean click ) throws IOException
    {//OVERLOADED SO TEXT CAN BE PUT IN ON CLICK SPAWN
        this.contextMenu.getItems().clear();
        
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
        
        if ( click )
        {
            propControl.setInitialText( schemeLabel.getText() );
        }
        
        this.propBoxController = propControl;
        this.hasProp = true;
        return tPropBox;
    }
    
    /**
     * This is a modified version of the colorPicker method in
     * TitleAndMenuBarController.java; because we have to have access
     * to certain things within CAC, there needs to be a separate method.
     * 
     * In this method, we don't need to pass in a textarea because 
     * we're changing the proposition rectangle's color; NOT the text-area.
     * 
     */
    private void configureColorPicker()
    {
        final Stage colorPickerStage  = new Stage();
        final Scene colorPickerScene  = new Scene( new HBox( 20 ), 400, 30 );
        final HBox box                = ( HBox ) colorPickerScene.getRoot();
        final ColorPicker colorPicker = new ColorPicker();
        final Label text              = new Label( "Try the color picker!" );
        
        colorPickerStage.setTitle( "Change Background Color" );
        colorPickerStage.getIcons().add( MainApp.icon );
        colorPickerStage.initModality( Modality.NONE );
        colorPickerStage.resizableProperty().setValue( false );
        colorPickerStage.initOwner( ( Stage ) this.parentControl.
                                          mainPane.getScene().getWindow() );
        
        text.setFont( Font.font ( "System Regular", 20 ) );
        box.setPadding( new Insets( 5, 5, 5, 5 ) );  

        colorPicker.setValue( Color.BLACK );
             
        colorPicker.setOnAction( ( ActionEvent event ) -> 
        {
            Color c = colorPicker.getValue();
            propositionRectangle.setFill( Color.rgb( ( int ) ( c.getRed()   * 255 ), 
                                                     ( int ) ( c.getGreen() * 255 ),
                                                     ( int ) ( c.getBlue()  * 255 ) ) );
        } );
                
        box.getChildren().addAll( colorPicker, text );
        colorPickerStage.setScene( colorPickerScene );
        colorPickerStage.show();
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

    private void deleteProp()
    {
        if ( this.mainPane.getChildren().contains( propositionRectangle ) )
        {
            this.mainPane.getChildren().remove( propositionRectangle );
        }
        if ( this.mainPane.getChildren().contains( propBox ) )
        {
            this.mainPane.getChildren().remove( propBox );
        }
        if ( this.mainPane.getChildren().contains( schemeLabel ) )
        {
            this.mainPane.getChildren().remove( schemeLabel );
        }
    }
    
    public void setWidth( int width, int oldWidth )
    {
        this.propBoxController.text.setPrefWidth( width );
        this.propositionRectangle.setWidth( width );
        
        // If we go below the minimium size, we need to fix the pane
        // so it's the default size instead
        if ( this.propBoxController.text.getWidth() < PREMISE_WIDTH  )
        {
            this.propBoxController.text.setPrefWidth( PREMISE_WIDTH );
            this.propositionRectangle.setWidth( PREMISE_WIDTH  );
            this.propositionRectangle.setTranslateX( ( int ) this.propBoxController.text.getTranslateX() );
            return;
        }
        
        if ( width > oldWidth )
        {
            this.propositionRectangle.setTranslateX( ( int ) ( this.propositionRectangle.getTranslateX() 
                                                           - ( Math.abs( oldWidth - width ) >> 1 ) ) );
        }
        else
        {
            this.propositionRectangle.setTranslateX( ( int ) ( this.propositionRectangle.getTranslateX() 
                                                           + ( Math.abs( oldWidth - width ) >> 1 ) ) );
        }
    }
    
    public void setHeight(int height, int oldHeight )
    {
        this.propBoxController.text.setPrefHeight( height );
        this.propositionRectangle.setHeight( height );     
        int yOffset = height - oldHeight;

        if( height > oldHeight )
        {
            this.mainPane.setTranslateY( ( int ) ( this.mainPane.getTranslateY() - yOffset ) );
        } 
        else 
        {
            this.mainPane.setTranslateY( ( int ) ( this.mainPane.getTranslateY() - yOffset ) );            
        }
    }    
    
    public void resetSize()
    {
        this.setWidth( 325, this.getWidth() );
        this.setHeight( 145, this.getHeight() );
    }    
    
    protected boolean mouseInsideLeftRect( int mx, int my )
    {
        if ( this.propBoxController == null )
        {
            return false;
        }
        Bounds rectBounds = FXUtils.nodePosition( this.propBoxController.text );
        
        if ( rectBounds == null )
        {
            return false;
        }
        
        int width  = ( int ) rectBounds.getMinX() + 25;
        int height = ( int ) rectBounds.getMaxY() + 5;
        
        return   ( mx >=  ( int ) rectBounds.getMinX() - 5 ) && ( mx <= width ) 
              && ( my >=  ( int ) rectBounds.getMinY() - 5 ) && ( my <= height );
    }
    
    protected boolean mouseInsideRightRect( int mx, int my )
    {
        if ( this.propBoxController == null )
        {
            return false;
        }
        
        Bounds rectBounds = FXUtils.nodePosition( this.propBoxController.text );
        
        if ( rectBounds == null )
        {
            return false;
        }
        
        int leftEdge   = ( int ) rectBounds.getMaxX();
        int rightEdge  = ( int ) rectBounds.getMaxX() + 25;
        int topEdge    = ( int ) rectBounds.getMinY() - 5;
        int bottomEdge = ( int ) rectBounds.getMaxY() + 5;
        
        return    mx >= leftEdge && mx <= rightEdge  
               && my >= topEdge && my <= bottomEdge;
    }        

    public void moveComment( double x, double y )
    {
        if ( propBoxController != null )
        {
            propBoxController.moveComment( x, y );
        }
    }
    
    public void deleteCommentPane()
    {
        propBoxController.deleteComment();
    }
    
    /**
     * 
     * @param control 
     */
    public void setParentControl( ConstructionAreaController control )
    {
        this.parentControl = control;
    }

    /**
     * 
     * @param control 
     */
    public void setCertaintyController( ArgumentCertaintyPaneController control )
    {
        this.certaintyControl = control;
        this.certaintyControl.setPremControl( this );
    }

    /**
     * 
     * @return 
     */
    public ArgumentCertaintyPaneController getCertaintyController()
    {
        return this.certaintyControl;
    }

    /**
     * 
     * @return 
     */
    public Pane getMainPane()
    {
        return this.mainPane;
    }

    /**
     * 
     * @return 
     */
    public ArgumentModel getArgument()
    {
        return this.argument;
    }

    /**
     * 
     * @param arg 
     */
    public void setArgumentModel( ArgumentModel arg )
    {
        this.argument = arg;
    }
    
    public String getText()
    {
        return this.text;
    }

    public void setText( String s )
    {
        this.text = s;
    }
    
    public void setConnectionNode( PremiseConnectionNode connectionNode ) 
    {
        this.connectionNode = connectionNode;
    }
    
    public PremiseConnectionNode getConnectionNode()
    {
        return this.connectionNode;
    }
        
    /**
     * 
     * @return 
     */
    public int getWidth()
    {
        return ( int ) this.propositionRectangle.getWidth();
    }

    /**
     * 
     * @return 
     */
    public int getHeight()
    {
        return ( int ) this.propositionRectangle.getHeight();
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

    /**
     * 
     * @return 
     */
    public PropositionModel getProposition()
    {
        return prop;
    }
    
    public PropositionBoxController getPropositionBoxController()
    {
        return this.propBoxController;
    }    
    
    /**
     * 
     * @param argNode 
     */
    public void setArgNode( ArgumentNode argNode )
    {
        this.argNode = argNode;
    }
    
    public String getTextInTextArea()
    {
        if ( this.propBoxController == null )
        {
            return null;
        }
        return this.propBoxController.text.getText();
    }    
    
    public ArgumentNode getArgNode()
    {
        return this.argNode;
    }
}
    
