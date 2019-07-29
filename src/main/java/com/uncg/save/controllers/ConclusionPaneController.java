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

import com.uncg.save.MainApp;
import static com.uncg.save.MainApp.propositionModelDataFormat;
import com.uncg.save.NodeType;
import com.uncg.save.Resizable;
import com.uncg.save.argumentviewtree.ArgumentNode;
import com.uncg.save.argumentviewtree.ArgumentSchemeLabel;
import com.uncg.save.argumentviewtree.ArgumentViewTree;
import com.uncg.save.argumentviewtree.PremiseNexusNode;
import com.uncg.save.models.ArgumentModel;
import com.uncg.save.models.PropositionModel;
import com.uncg.save.util.AlertStage;
import com.uncg.save.util.FXUtils;
import com.uncg.save.util.MouseUtils;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ConclusionPaneController implements Initializable, Resizable
{
    //////////////////////// INSTANCE VARIABLES /////////////////////////////
    
    //
    //  Pane references
    //
    @FXML protected Pane mainPane;
    protected Pane       propBox;

    //
    //
    //
    @FXML protected Rectangle     propositionRectangle;
    protected Label               schemeLabel;
    protected ArgumentSchemeLabel argumentSchemeLabel;

    //
    //  Controllers
    //
    protected PropositionBoxController        propBoxController = null;
    protected ArgumentCertaintyPaneController certaintyControl  = null;
    protected ConstructionAreaController      parentControl     = null;

    //
    //
    //
    protected PropositionModel      prop              = null;
    protected List<ArgumentModel>   conclusionArgList = null;
    protected ArgumentViewTree      argTree           = null;
    protected ArgumentNode          argNode           = null;

    //
    //
    //
    protected ContextMenu           contextMenu       = null;
    protected Point2D               contextCoords     = null;

    //
    //
    //
    protected boolean               hasProp           = false;
    protected boolean               hasCounter        = false;
    protected boolean               dragging          = false; 
    protected boolean[]             resizeWidth       = { false, false };
    protected boolean[]             resizeHeight      = { false, false };
    protected int                   proConStatus      = 2; // 0 is pro, 1 is con 2 is indeterminate
    
    //////////////////////// INSTANCE VARIABLES /////////////////////////////
    
    /**
     * Controls the ultimate conclusion of an argument structure
     *
     * Associated view node: ConclusionNode
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(  URL url, ResourceBundle rb )
    {
        this.conclusionArgList = new ArrayList<>();
        this.mainPane.getStyleClass().add( "conclusion-pane" );

        // Sets the style (using the Styles.css file) of the border for the rectangle
        this.propositionRectangle.getStyleClass().add( "conclusion-rectangle" );
        
        //
        //  DRAG DROPPED EVENT
        //
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
        //  DRAG DETECTED EVENT
        //
        this.mainPane.addEventFilter( MouseEvent.DRAG_DETECTED, ( MouseEvent event ) ->
        {
            if ( this.propBoxController != null && !this.propBoxController.text.getSelectedText().isEmpty() )
            {
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
        //  Index 8 in the CM item list is the add counter argument button
        //
        this.mainPane.addEventFilter( ContextMenuEvent.CONTEXT_MENU_REQUESTED, ( ContextMenuEvent event ) ->
        {
            if ( this.contextMenu != null )
            {
                if ( MainApp.DEBUG )
                {
                    //this.contextMenu.getItems().get( 1 ).setDisable( this.argNode.caFlag );
                }
                else
                {
                    //this.contextMenu.getItems().get( 0 ).setDisable( this.argNode.caFlag );
                }
                this.showContextMenu( event );
            }
            
            event.consume();
                
        } );

        //
        //  MOUSE ENTERED EVENT
        //
        //  If the user enters the pane and they aren't dragging the mouse, then
        //  we can display the tooltip containing the definition.
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
        } );   
    }
    
    public void initializePane()
    {
        try
        {
            if ( ! hasProp )
            {
                this.deleteProp();
                // Create context menu for adding new premises
                this.contextMenu = new ContextMenu();                        
                this.mainPane.getChildren().add( propositionRectangle );
                this.prop = new PropositionModel();
                this.addPropositionAsConclusion( prop );
                this.propBox = loadNewPropPane( prop, true );
                this.mainPane.getChildren().add( propBox );
                this.propBoxController.nodeType = NodeType.CONCLUSION;
                this.propBoxController.argCertaintyController = getCertaintyController();
                this.parentControl.setConclusionPaneController( this );
                
                this.prop.setTitle( this.argumentSchemeLabel.getSchemeLabelText() );
                
                //
                //  We want the definition to always remain the same. Therefore,
                //  we have to separate it from the actual conclusion; the definition
                //  is immutable while the conclusion is NOT. The definition is what
                //  appears when hovering over the conclusion.
                //
                String concDefinition = this.getConclusionArgumentModel().getSchemeDefinition();
                this.prop.setDefinition( concDefinition != null ? concDefinition : this.getConclusionArgumentModel().getSchemeConclusion() );

                //  Sets the proposition to either the definition (if it's a brand new scheme,
                //  or the saved text from the user.
                this.prop.setProposition( this.getConclusionArgumentModel().getSchemeConclusion() );
                
                this.propBoxController.text.setText( this.prop.getProposition() );
                
                this.hasProp = true;
            }
        }
        catch( IOException ex )
        {
            System.err.println( "Error" );
        }
    }
    
    @Override
    public void setWidth( int width, int oldWidth )
    {
        this.propBoxController.text.setPrefWidth( width );
        this.propositionRectangle.setWidth( width );

        if ( width > oldWidth )
        {
            this.propositionRectangle.setTranslateX( ( int ) ( this.propositionRectangle.getTranslateX() - ( Math.abs( oldWidth - width ) >> 1 ) ) );
        }
        else
        {
            this.propositionRectangle.setTranslateX( ( int ) ( this.propositionRectangle.getTranslateX() + ( Math.abs( oldWidth - width ) >> 1 ) ) );
        }
    }
    
    @Override
    public void setHeight( int height, int oldHeight )
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

    /**
     * Set menu items for context menu
     */
    private void setContextMenuItems() throws IOException
    {
        MenuItem counterArg      = new MenuItem( "Add a Counter Argument" );
        MenuItem toggleProCon    = new MenuItem( "Toggle Pro/Con"         );
        MenuItem deleteArg       = new MenuItem( "Delete Argument"        );
        MenuItem clearText       = new MenuItem( "Clear Text"             );
        MenuItem copy            = new MenuItem( "Copy (CTRL+C)"          );
        MenuItem paste           = new MenuItem( "Paste (CTRL+V)"         );
            counterArg.setDisable( true );
            
        if ( MainApp.DEBUG )
        {
            MenuItem colorPickerMenu = new MenuItem( "Change Color" );
            this.setHandlerForColorPicker( colorPickerMenu );
            this.contextMenu.getItems().add(  colorPickerMenu );
        }
        
        this.setHandlerForCounterArg ( counterArg      );
        this.setHandlerForToggle     ( toggleProCon    );
        this.setHandlerForDeleteArg  ( deleteArg       );
        this.setHandlerForClearText  ( clearText       );
        this.setHandlerForCopy       ( copy            );
        this.setHandlerForPaste      ( paste           );

        this.contextMenu.getItems().addAll( counterArg, toggleProCon, deleteArg, clearText, copy, paste );
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

    private void setHandlerForCounterArg( MenuItem item )
    {
        item.setOnAction( action ->
        {
            try
            {
                if ( !this.argNode.caFlag )
                {
                    this.argTree.addCounterArgument( conclusionArgList.get( 0 ).getConclusion(), argNode );
                    this.argNode.caFlag = true;
                    item.setDisable( true );
                }
            }
            catch ( IOException ex )
            {
                Logger.getLogger( ConclusionPaneController.class.getName() ).log( Level.SEVERE, null, ex );
            }
        } );
    }

    private void setHandlerForClearText(MenuItem item)
    {
        item.setOnAction( action ->
        {
            if ( propBoxController == null || propBoxController.text == null )
            {
                return;
            }
            this.propBoxController.text.clear();
            this.propBoxController.text.setText( "" );
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
     * Sets the handler for deleting an argument tree
     *
     * @param item MenuItem
     */
    private void setHandlerForDeleteArg(MenuItem item)
    {
        item.setOnAction( action ->
        {
            this.argTree.deleteArgument();
            action.consume();
        } );
    }

    private void setHandlerForColorPicker(MenuItem item)
    {
        item.setOnAction( action ->
        {
            this.configureColorPicker();
        } );
    }

    /**
     * Sets the associated argument label
     *
     * @param lbl
     */
    public void setSchemeLabel( Label lbl )
    {
        this.schemeLabel = lbl;
        this.schemeLabel.setStyle( "-fx-text-fill: black;" );
        this.mainPane.getChildren().add( schemeLabel );
    }
    
    public void setSchemeTitleLabel( ArgumentSchemeLabel asl )
    {
        this.argumentSchemeLabel = asl;
    }
    
    public String getSchemeLabel()
    {
        return this.schemeLabel.getText();
    }

    public ArgumentModel getConclusionArgumentModel()
    {
        if ( ! conclusionArgList.isEmpty() )
        {
            return conclusionArgList.get( 0 );
        }
        
        return new ArgumentModel();
    }

    public void addConclusionArgumentModel( ArgumentModel arg )
    {
        this.conclusionArgList.clear();
        this.conclusionArgList.add( 0, arg );
    }

    /**
     * Handles a dragdropped event according to the contents of event
     *
     * @param event
     *
     * @throws IOException
     */
    private void onDragDropped( DragEvent event ) throws IOException
    {
        Dragboard db = event.getDragboard();
        boolean dropped = false;
        if ( db.hasContent( propositionModelDataFormat ) )
        {
            System.out.println( "Dropping 1 prop" );
            PropositionModel tProp = ( PropositionModel ) db.getContent( propositionModelDataFormat );
            addProposition( tProp );
            dropped = true;
        }
        else if ( db.hasString() )
        {
            String treeID = db.getString();
            if ( !treeID.equals( argTree.getTreeID() ) )
            {
                if ( !argTree.getRoot().caFlag )
                {
                    //argTree.createMultiArgBranch( treeID, this );
                }   
                else
                {

                    AlertStage alert = new AlertStage( AlertType.WARNING, "Please "
                            + "delete the counter argument in your main tree to "
                            + "merge the two. After merging, you can re-add the "
                            + "counter argument. This is an experimental feature.",
                            ( Stage ) this.parentControl.mainPane.getScene().getWindow() );
                }
                dropped = true;
            }
        }
        
        event.setDropCompleted( dropped );
        event.consume();
    }

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
        this.conclusionArgList.forEach( ( arg ) ->
        {
            arg.setConclusion( prop );
        } );
    }

    /**
     * Loads a new proposition into the model and view based on prop
     *
     * @param prop
     *
     * @return
     *
     * @throws IOException
     */
    private Pane loadNewPropPane( PropositionModel prop ) throws IOException
    {
        try
        {
            this.contextMenu.getItems().clear();
            this.setContextMenuItems();
        }
        catch ( IOException ex )
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

    private Pane loadNewPropPane(PropositionModel prop, boolean click) throws IOException
    {//OVERLOADED SO TEXT CAN BE PUT IN ON CLICK SPAWN
        try
        {
            this.contextMenu.getItems().clear();
            this.setContextMenuItems();
        }
        catch ( IOException ex )
        {
            Logger.getLogger( ConclusionPaneController.class.getName() ).log( Level.SEVERE, null, ex );
        }
        
        if ( this.propBoxController != null )
        {
            this.propBoxController.deleteComment();
        }
        
        FXMLLoader loader   = new FXMLLoader( getClass().getResource( "/fxml/PropositionBox.fxml" ) );
        Pane       tPropBox = loader.load();
        
        this.propBox        = tPropBox;
        
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

    @FXML
    private void dragOver(DragEvent event)
    {
        Dragboard db = event.getDragboard();
        if ( db.hasContent( propositionModelDataFormat ) )
        {
            event.acceptTransferModes( TransferMode.MOVE );
        }
        
        if ( db.hasString() )
        {
            event.acceptTransferModes( TransferMode.MOVE );
        }
        event.consume();
    }

    private void dragDetected()
    {
        Dragboard db = mainPane.startDragAndDrop( TransferMode.MOVE );
        ClipboardContent content = new ClipboardContent();
        content.putString( argTree.getTreeID() );
        db.setContent( content );
    }

    /**
     * shows the context menu using the X and Y of the event to determine
     * location
     *
     * @param event MouseEvent
     */
    private void showContextMenu(ContextMenuEvent event)
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
     * Functioned in place of .clear so certain children aren't always deleted
     */
    protected void deleteProp()
    {
        if ( mainPane.getChildren().contains( propositionRectangle ) )
        {
            mainPane.getChildren().remove( propositionRectangle );
        }
        if ( mainPane.getChildren().contains( propBox ) )
        {
            mainPane.getChildren().remove( propBox );
        }
        if ( mainPane.getChildren().contains( schemeLabel ) )
        {
            mainPane.getChildren().remove( schemeLabel );
        }
    }
    
    /**
     * Removes a proposition from the view and model of this pane
     */
    private void removeProp()
    {
        try
        {
            this.contextMenu.getItems().clear();
            this.setContextMenuItems();
        }
        catch ( IOException ex )
        {
            Logger.getLogger( ConclusionPaneController.class.getName() ).log( Level.SEVERE, null, ex );
        }
        this.conclusionArgList.forEach( ArgumentModel::removeConclusion );
        this.prop = null;
        this.mainPane.getChildren().remove( propBox );
        this.mainPane.getChildren().add( schemeLabel );
        this.propBoxController.deleteComment();
        this.hasProp = false;
    }

    public void moveComment( double x, double y )
    {
        this.propBoxController.moveComment( x, y );
    }

    public void deleteCommentPane()
    {
        this.propBoxController.deleteComment();
    }

    public void checkHasProp()
    {
        if ( this.propBox != null )
        {
            this.hasProp = true;
        }
    }

    /**
     * This is a modified version of the colorPicker method in
     * TitleAndMenuBarController.java; because we have to have access to certain
     * things within CAC, there needs to be a separate method.
     *
     * In this method, we don't need to pass in a textarea because we're
     * changing the proposition rectangle's color; NOT the text-area.
     *
     */
    private void configureColorPicker()
    {
        final Stage colorPickerStage = new Stage();
        final Scene colorPickerScene = new Scene( new HBox( 20 ), 400, 30 );
        final HBox box = ( HBox ) colorPickerScene.getRoot();
        final ColorPicker colorPicker = new ColorPicker();
        final Label text = new Label( "Try the color picker!" );

        colorPickerStage.setTitle( "Change Background Color" );
        colorPickerStage.getIcons().add( MainApp.icon );
        colorPickerStage.initModality( Modality.NONE );
        colorPickerStage.resizableProperty().setValue( false );
        colorPickerStage.initOwner( ( Stage ) this.parentControl.mainPane.getScene().getWindow() );

        text.setFont( Font.font( "System Regular", 20 ) );
        text.setAlignment( Pos.TOP_RIGHT );
        box.setPadding( new Insets( 5, 5, 5, 5 ) );

        colorPicker.setValue( Color.BLACK );

        colorPicker.setOnAction( ( ActionEvent event ) ->
        {
            Color c = colorPicker.getValue();
            this.propositionRectangle.setFill( Color.rgb( ( int ) ( c.getRed() * 255 ),
                    ( int ) ( c.getGreen() * 255 ),
                    ( int ) ( c.getBlue() * 255 ) ) );
        } );

        box.getChildren().addAll( colorPicker, text );
        colorPickerStage.setScene( colorPickerScene );
        colorPickerStage.show();
    }
        
    public String getTextInTextArea()
    {
        return this.propBoxController.getTextAreaText();
    }

    public int getWidth()
    {
        return ( int ) this.propositionRectangle.getWidth();
    }

    public int getHeight()
    {
        return ( int ) this.propositionRectangle.getHeight();
    }
    
    public void resetSize()
    {
        this.setWidth( 325, this.getWidth() );
        this.setHeight( 145, this.getHeight() );
    }

    public void setArgNode(ArgumentNode node)
    {
        this.argNode = node;
    }

    /**
     * Sets the background color corresponding to certainty
     * 
     * @param d
     */
    public void setViewColor( double d )
    {
        String color = FXUtils.toRGBCode( Color.rgb( FXUtils.ConvertToRed( d ), 0, FXUtils.ConvertToBlue( d ) ) );
        
        //this.propBoxController.text.setStyle( "-fx-background-color: " + color + "; -fx-text-fill: white;" );
    }
    
    private void extractProp()
    {
        this.parentControl.createProp( prop, contextCoords );
    }

    public void setParentControl(ConstructionAreaController control)
    {
        this.parentControl = control;
    }
    
    /**
     * hides the context menu
     */
    private void closeContextMenu()
    {
        this.contextMenu.hide();
    }

    public Pane getMainPane()
    {
        return this.mainPane;
    }

    public List<ArgumentModel> getConclusionArgumentModelList()
    {
        return this.conclusionArgList;
    }

    public void removeArgument(ArgumentModel argument)
    {
        this.conclusionArgList.remove( argument );
    }

    public void setArgumentViewTree( ArgumentViewTree argTree )
    {
        this.argTree = argTree;
    }

    public ArgumentViewTree getArgumentViewTree()
    {
        return this.argTree;
    }    

    public void setCertaintyController(ArgumentCertaintyPaneController control)
    {
        this.certaintyControl = control;
        this.certaintyControl.setConcControl( this );
    }
    
    public PropositionBoxController getPropositionBoxController()
    {
        return this.propBoxController;
    }

    public ArgumentCertaintyPaneController getCertaintyController()
    {
        return certaintyControl;
    }

    public PropositionModel getProposition()
    {
        return prop;
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
        
        int leftEdge   = ( int ) rectBounds.getMaxX() - 25;
        int rightEdge  = ( int ) rectBounds.getMaxX() + 25;
        int topEdge    = ( int ) rectBounds.getMinY() - 5;
        int bottomEdge = ( int ) rectBounds.getMaxY() + 5;
        
        return    mx >= leftEdge && mx <= rightEdge  
               && my >= topEdge && my <= bottomEdge;
    }
    
    public ArgumentNode getArgNode()
    {
        return this.argNode;
    }
    
    public int getProCon()
    {
        return this.proConStatus;
    }
    
    /**
     * Subclasses need to override this method
     * @param pnn 
     */
    public void setPremiseNexus( PremiseNexusNode pnn ){}
}
