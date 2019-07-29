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

import static com.uncg.save.MainApp.argumentModelDataFormat;
import static com.uncg.save.MainApp.commentDataFormat;
import static com.uncg.save.MainApp.propositionModelDataFormat;
import static com.uncg.save.MainApp.schemeModelDataFormat;
import com.uncg.save.Premise;
import com.uncg.save.ResizeEnum;
import com.uncg.save.argumentviewtree.ArgumentViewTree;
import com.uncg.save.models.ArgumentModel;
import com.uncg.save.models.PropositionModel;
import com.uncg.save.models.SchemeModel;
import com.uncg.save.util.LayoutUtils;
import com.uncg.save.util.MouseUtils;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ConstructionAreaController implements Initializable
{
    //////////////////////// INSTANCE VARIABLES /////////////////////////////
    
    //
    //
    //
    @FXML protected Pane                        mainPane;

    //
    //
    //
    private HashMap<String, ArgumentViewTree>   argumentTrees;

    //
    //
    //
    private ContextMenu                         contextMenu;

    //
    //
    //
    private Point2D                             targetpropBoxControlleroords;

    //
    // References for the selected pane. When the user clicks on a pane,
    // whichever one is selected is set, with the others being nulled.
    //
    private RootPaneController                  rpc;
    private PremisePaneController               ppc;
    private ConclusionPaneController            cpc;
    private CQPaneController                    cqpc;
    private MultiArgSubConclusionPaneController multiArgSubCPC;
    
    //////////////////////// INSTANCE VARIABLES /////////////////////////////    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize( URL url, ResourceBundle rb )
    {
        // initialize lists
        this.setArgumentTrees( new HashMap<>() );

        this.getMainPane().getStyleClass().add( "pane" );
        

        //
        //  MOUSE MOVED EVENT
        //
        this.mainPane.addEventFilter( MouseEvent.MOUSE_MOVED, ( MouseEvent mouseEvent ) -> 
        {
            int mx = ( int ) mouseEvent.getSceneX();
            int my = ( int ) mouseEvent.getSceneY(); 
            
            //
            // If we're resizing the conclusion pane
            //
            if ( this.cpc != null )
            {
                if ( this.cpc.mouseInsideLeftRect( mx, my ) || this.cpc.mouseInsideRightRect( mx, my ) )
                {
                    this.cpc.mainPane.getScene().setCursor( Cursor.W_RESIZE );
                }
                
                // We also need to check if the user wants to resize the premise nexus
                else if ( this.cpc instanceof MultiArgConclusionPaneController )
                {
                    MultiArgConclusionPaneController maCPC = ( MultiArgConclusionPaneController ) this.cpc;
                    
                    if ( maCPC.mouseInsideLeftRectNexus( mx, my ) || maCPC.mouseInsideRightRectNexus( mx, my ) )
                    {
                        maCPC.mainPane.getScene().setCursor( Cursor.W_RESIZE );
                    }
                    else
                    {
                        if ( this.cpc != null && this.cpc.mainPane != null && this.cpc.mainPane.getScene() != null )
                        {
                            this.cpc.mainPane.getScene().setCursor( Cursor.DEFAULT );   
                        }
                    }
                }
                else
                {
                    if ( this.cpc != null && this.cpc.mainPane != null && this.cpc.mainPane.getScene() != null )
                    {
                        this.cpc.mainPane.getScene().setCursor( Cursor.DEFAULT );   
                    }
                }
            }
            
            //
            // If we're resizing the premise pane
            //
            else if ( this.ppc != null && this.ppc.mainPane != null && this.ppc.mainPane.getScene() != null )
            {
                if ( this.ppc.mouseInsideLeftRect( mx, my ) || this.ppc.mouseInsideRightRect( mx, my ) )
                {
                    this.ppc.mainPane.getScene().setCursor( Cursor.W_RESIZE );
                } 
                else
                {
                     this.ppc.mainPane.getScene().setCursor( Cursor.DEFAULT );   
                }
            }       
        } );        
        
        //
        //  MOUSE PRESSED EVENT
        //        
        this.mainPane.addEventFilter( MouseEvent.MOUSE_PRESSED, ( MouseEvent mouseEvent ) ->
        {
            int mx = ( int ) mouseEvent.getSceneX();
            int my = ( int ) mouseEvent.getSceneY(); 
            
            // If we're resizing the conclusion pane
            if ( this.cpc != null )
            {
                this.cpc.resizeWidth[ ResizeEnum.LEFT.ID ]   = this.cpc.mouseInsideLeftRect( mx, my );
                this.cpc.resizeWidth[ ResizeEnum.RIGHT.ID ]  = this.cpc.mouseInsideRightRect( mx, my );
                this.cpc.dragging =  this.cpc.resizeWidth[ ResizeEnum.LEFT.ID ] | this.cpc.resizeWidth[ ResizeEnum.RIGHT.ID ];
                // We also need to check if the user wants to resize the premise nexus
                if ( this.cpc instanceof MultiArgConclusionPaneController )
                {
                    MultiArgConclusionPaneController maCPC   = ( MultiArgConclusionPaneController ) this.cpc;
                    
                    maCPC.resizeNexus[ ResizeEnum.LEFT.ID ]  = maCPC.mouseInsideLeftRectNexus( mx, my );
                    maCPC.resizeNexus[ ResizeEnum.RIGHT.ID ] = maCPC.mouseInsideRightRectNexus( mx, my );
                    maCPC.dragging = maCPC.resizeNexus[ ResizeEnum.LEFT.ID ] | maCPC.resizeNexus[ ResizeEnum.RIGHT.ID ];
                }
            }
            // If we're resizing the premise pane
            else if ( this.ppc != null )
            {
                this.ppc.resizeWidth[ ResizeEnum.LEFT.ID ] = this.ppc.mouseInsideLeftRect( mx, my );
                this.ppc.resizeWidth[ ResizeEnum.RIGHT.ID ] = this.ppc.mouseInsideRightRect( mx, my );
            }
            
            MouseUtils.Mouse_X   = mx;

        } );            
        
        //
        //  MOUSE RELEASED EVENT ( NOT FULLY WORKING )
        //
        this.mainPane.addEventFilter( MouseEvent.MOUSE_RELEASED, ( MouseEvent mouseEvent ) -> 
        {
            // If we're resizing the conclusion pane
            if ( this.cpc != null ) 
            {
                
                if ( this.cpc.resizeWidth[ ResizeEnum.LEFT.ID ] || this.cpc.resizeWidth[ ResizeEnum.RIGHT.ID ] )
                {
                    // If we are currently stretching the pane horizontally, we need
                    // to grab the offset difference between where we started and 
                    // where we are now. We can use this to calculate the new dimensions.
                    
                    // Determine which side we're trying to resize from
                    if ( this.cpc.resizeWidth[ ResizeEnum.LEFT.ID ] )
                    {
                        MouseUtils.Delta_X = MouseUtils.Mouse_X - mouseEvent.getSceneX() ;
                    } 
                    else 
                    {
                        MouseUtils.Delta_X = mouseEvent.getSceneX() - MouseUtils.Mouse_X ;
                    }

                    // If we're stretching to the left or right
                    if ( Math.signum( MouseUtils.Delta_X ) > 0 )
                    {
                        this.cpc.setWidth( ( int ) MouseUtils.Delta_X + this.cpc.getWidth(), this.cpc.getWidth() );
                    }
                    else if ( this.cpc.getWidth() - MouseUtils.Delta_X  >= 325 )
                    {
                        this.cpc.setWidth( this.cpc.getWidth() - ( int ) Math.abs( MouseUtils.Delta_X ) , this.cpc.getWidth() );
                    }
                    else
                    {
                        this.cpc.resetSize();
                    }
                    this.cpc.resizeWidth[ ResizeEnum.LEFT.ID ] = this.cpc.resizeWidth[ ResizeEnum.RIGHT.ID ] = false;
                    this.mainPane.getScene().setCursor( Cursor.DEFAULT );
                }   
                
                //
                // If our controller is a MultiArgCPC, then we need to have a check
                // to determine if the user is hovering over the premise nexus 
                // or if they just wanted to resize the pane itself (above code)
                //
                else if ( this.cpc instanceof MultiArgConclusionPaneController && this.cpc.dragging )
                {
                    MultiArgConclusionPaneController maCPC = ( MultiArgConclusionPaneController ) this.cpc;

                    if ( maCPC.resizeNexus[ ResizeEnum.LEFT.ID ] )
                    {
                        MouseUtils.Delta_X = MouseUtils.Mouse_X - mouseEvent.getSceneX() ;
                    } 
                    else 
                    {
                        MouseUtils.Delta_X = mouseEvent.getSceneX() - MouseUtils.Mouse_X ;
                    }                    
                    
                     
                     maCPC.argTree.nexusMatrixAdjustment( MouseUtils.Delta_X );
                }
            }
            
            //
            // If we're resizing the premise pane
            //
            else if ( this.ppc != null ) 
            {
                if ( this.ppc.resizeWidth[ ResizeEnum.LEFT.ID ] || this.ppc.resizeWidth[ ResizeEnum.RIGHT.ID ] )
                {
                    // If we are currently stretching the pane horizontally, we need
                    // to grab the offset difference between where we started and 
                    // where we are now. We can use this to calculate the new dimensions.
                    
                    // DeltaX is dependent on which anchor the user is holding
                    if ( this.ppc.resizeWidth[ ResizeEnum.LEFT.ID ])
                    {
                        MouseUtils.Delta_X = MouseUtils.Mouse_X - mouseEvent.getSceneX() ;
                    } 
                    else 
                    {
                        MouseUtils.Delta_X = mouseEvent.getSceneX() - MouseUtils.Mouse_X ;
                    }

                    // If we have a positive delta X, that means we are growing
                    // in size and therefore need to append the new width to our 
                    // current width
                    if ( Math.signum( MouseUtils.Delta_X ) > 0 )
                    {
                        this.ppc.setWidth( ( int ) MouseUtils.Delta_X + this.ppc.getWidth(), this.ppc.getWidth() );
                    }
                    
                    //
                    //  Otherwise, we can just subtract the width 
                    //  @TODO: make it so 325 is the limit
                    //
                    else if ( this.ppc.getWidth() - MouseUtils.Delta_X  >= 325  )
                    {
                        this.ppc.setWidth( this.ppc.getWidth() - 
                        ( int ) Math.abs( MouseUtils.Delta_X ), this.ppc.getWidth() );
                    }
                    else
                    {
                        this.ppc.resetSize();
                    }
                    this.ppc.resizeWidth[ ResizeEnum.LEFT.ID ] = this.ppc.resizeWidth[ ResizeEnum.RIGHT.ID ] = false;
                    this.mainPane.getScene().setCursor( Cursor.DEFAULT );
                }                
            }        
        } );
    }

    /**
     * event handler for drag over events
     *
     * @param event
     */
    @FXML
    private void dragOver( DragEvent event )
    {
        event.acceptTransferModes( TransferMode.ANY );
    }

    /**
     * event handler for drop events from draggables
     *
     * @param event
     */
    @FXML
    private void dragDropped( DragEvent event )
    {
        boolean   success = false;
        Dragboard db      = event.getDragboard();
        if ( db.hasContent( schemeModelDataFormat ) )
        {
            /*
             * Use new scheme to generate Argument
             */
            try
            {
                this.dropScheme( db, event );
                success = true;
            } 
            catch ( IOException ex )
            {
                Logger.getLogger( ConstructionAreaController.class.getName() ).log( Level.SEVERE, null, ex );
            }
        } 
        else if ( db.hasContent( argumentModelDataFormat ) )
        {
            try
            {
                this.dropArgument( db, event );
                success = true;
            } 
            catch ( IOException ex )
            {
                Logger.getLogger( ConstructionAreaController.class.getName() ).log( Level.SEVERE, null, ex );
            }
        } 
        else if ( db.hasContent( propositionModelDataFormat ) )
        {
            try
            {
                /*
                 * Move proposition in construction area
                 */
                this.dropProp( ( PropositionModel ) db.getContent( propositionModelDataFormat ),
                                new Point2D( event.getSceneX(), event.getSceneY() ) );
                
                success = true;
            } 
            catch ( IOException ex )
            {
                Logger.getLogger( ConstructionAreaController.class.getName() ).log( Level.SEVERE, null, ex );
            }
        } 
        else if ( db.hasContent( commentDataFormat ) )
        {
            //?????????
        } 
        else if ( db.hasString() )
        {
            try
            {
                /*
                 * Drag argument tree
                 */
                this.dropArgument( db, event );
                success = true;
                this.cpc.dragging = false;
            } catch ( IOException ex )
            {
                Logger.getLogger( ConstructionAreaController.class.getName() ).log( Level.SEVERE, null, ex );
            }
        }
        this.constructionAreaSizeCheck();
        event.setDropCompleted( success );
        event.consume();
    }

    /**
     * Right-side collision doesn't FULLY work, and is temporarily disabled when
     * the EvidencePane is loaded in for some reason...
     *
     * @Update 05/30/2019: Do we even really need this anymore?
     */
    public void constructionAreaSizeCheck()
    {
        int leftEdgeBuffer   = 75;
        int rightEdgeBuffer  = 325;
        int bottomEdgeBuffer = 400;

        // Check if nodes need to be shifted because they're falling off the 
        // left side
        double moveBuffer    = this.furthestNodeMinX();

        if ( moveBuffer < 0 ) 
        {
            this.shiftAllChildren( moveBuffer );
        }
        
        // Check if nodes need to be shifted because they're falling off the 
        // right side
        moveBuffer = this.furthestNodeMaxX();

        if ( moveBuffer > this.getMainPane().getPrefWidth() )
        {
            //shiftAllChildren( rightEdgeBuffer );
            this.mainPane.layout();
            this.mainPane.setPrefSize( getMainPane().getPrefWidth() + 50, getMainPane().getPrefHeight() );
        }
        else
        {
            this.mainPane.layout();
            // Fix dimensions          
        }
        
        moveBuffer = this.furthestNodeMaxY();
        
        if( moveBuffer > this.getMainPane().getPrefHeight() )
        {
            this.mainPane.layout();
            this.mainPane.setPrefSize( getMainPane().getPrefWidth(),
                                       furthestNodeMaxY() + bottomEdgeBuffer );
        }
    }
    
    /**
     * 
     * @param move 
     */
    private void shiftAllChildren( double move )
    {
        move *= -1;
        for ( Node node : getMainPane().getChildren() )
        {
            node.setLayoutX( node.getLayoutX() + move );
        }
        getMainPane().setPrefWidth( getMainPane().getWidth() + move );
    }

    /**
     * Finds the leftmost node on the area
     * 
     * @return 
     */
    private double furthestNodeMinX()
    {
        double minX = Double.MAX_VALUE;
        for ( Node node : getMainPane().getChildren() )
        {
            if ( node.getBoundsInParent().getMinX() < minX )
            {
                minX = node.getBoundsInParent().getMinX();
            }
        }
        return minX;
    }

    /**
     * Finds the bottom most node on the area
     * 
     * @return 
     */
    private double furthestNodeMinY()
    {
        double minY = Double.MAX_VALUE;
        for ( Node node : getMainPane().getChildren() )
        {
            if ( node.getBoundsInParent().getMinY() > minY )
            {
                minY = node.getBoundsInParent().getMinY();
            }
        }
        return minY;
    }

    /**
     * Finds the right most node on the area
     * 
     * @return 
     */
    private double furthestNodeMaxX()
    {
        double maxX = Double.MIN_VALUE;
        for ( Node node : getMainPane().getChildren() )
        {
            if ( node.getBoundsInParent().getMaxX() > maxX )
            {
                maxX = node.getBoundsInParent().getMaxX();
            }
        }
        return maxX;
    }

    /**
     * Finds the top most node on the area
     * 
     * @return 
     */
    private double furthestNodeMaxY()
    {
        double maxY = Double.MIN_VALUE;
        for ( Node node : getMainPane().getChildren() )
        {
            if ( node.getBoundsInParent().getMaxY() > maxY )
            {
                maxY = node.getBoundsInParent().getMaxY();
            }
        }
        return maxY;
    }

    /**
     * 
     * @param db
     * @param event
     * @throws IOException 
     */
    private void dropScheme( Dragboard db, DragEvent event ) throws IOException
    {
        SchemeModel scheme = ( SchemeModel ) db.getContent( schemeModelDataFormat );
        if ( scheme.getTitle().equals( "Generic" ) )
        {
            //ASK FOR NUMBER OF PREMISES FROM USER
            scheme.clearPremise();
            getGeneric( scheme );
        }
        ArgumentModel argument   = new ArgumentModel( scheme );
        ArgumentViewTree argTree = new ArgumentViewTree( getMainPane(), this );
        String treeID            = Integer.toString( argTree.hashCode() );
        argTree.setTreeID( treeID );
        getArgumentTrees().put( treeID, argTree );
        argTree.addRootArgument( argument, ( int ) event.getSceneX(), ( int ) event.getSceneY() );
    }

    /**
     * Loads the generic scheme maker and writes the specified scheme to the area
     * 
     * @param scheme
     * @throws IOException 
     */
    private void getGeneric( SchemeModel scheme ) throws IOException
    {
        FXMLLoader loader                 = new FXMLLoader( getClass().getResource( "/fxml/GenericPremisePane.fxml" ) );
        Parent genericPremSetterBox       = loader.load();
        GenericPremisePaneController gppC = loader.<GenericPremisePaneController>getController();
        gppC.setInitialScheme( scheme );
        Scene scene = new Scene( genericPremSetterBox );
        Stage stage = new Stage();
        stage.initOwner( getMainPane().getScene().getWindow() );
        stage.setTitle( "Set Number Premises" );
        stage.setScene( scene );
        stage.initModality( Modality.APPLICATION_MODAL );
        gppC.setStage( stage );
        stage.showAndWait();

        int i = gppC.getPremiseNumber();
        for ( int j = i; j != 0; j-- )
        {
            scheme.addPremise( new Premise("Premise:", "Definition:") );
        }
        scheme.setCriticalQs( gppC.getCQs() );
    }

    /**
     * Creates a new argument from an existing structure of argument trees to
     * simulate movement
     *
     * @param db
     * @param event
     *
     * @throws IOException
     */
    private void dropArgument( Dragboard db, DragEvent event ) throws IOException
    {
        String draggedTreeID = db.getString();
        ArgumentViewTree targetTree = null;
        for ( String treeID : getArgumentTrees().keySet() )
        {
            if ( treeID.equals( draggedTreeID ) )
            {
                targetTree = getArgumentTrees().get( draggedTreeID );
            }
        }
        
        // CHANGED THIS 06/25/19
        Point2D localCoords = LayoutUtils.getLocalCoords( getMainPane(), event.getSceneX(), event.getSceneY() );

        targetTree.translateTree( localCoords.getX(), localCoords.getY() );
    }

    /**
     * Creates a new proposition box from an existing model from dragboard to
     * simulate movement
     *
     * @param db Dragboard
     * @param event DragEvent
     *
     * @throws IOException
     */
    private void dropProp( PropositionModel prop, Point2D coords ) throws IOException
    {
        Pane propBox = loadNewPropPane( prop );
        Point2D localCoords = LayoutUtils.getLocalCoords( getMainPane(), coords.getX(), coords.getY() );
        LayoutUtils.setChildLayout( propBox, localCoords );

        getMainPane().getChildren().add( propBox );
    }

    /**
     * Loads a new proposition box and sets fields according to proposition
     * provided
     *
     * @param prop Proposition Model
     *
     * @return Pane
     *
     * @throws IOException
     */
    private Pane loadNewPropPane( PropositionModel prop ) throws IOException
    {
        FXMLLoader loader = new FXMLLoader( getClass().getResource( "/fxml/PropositionBox.fxml" ) );
        Pane propBox = loader.load();
        PropositionBoxController propControl = loader.<PropositionBoxController>getController();
        propControl.setConstructionAreaControl( this );
        propControl.setPropModel( prop );
        propControl.setParentContainer( getMainPane() );
        return propBox;
    }

    public void removePane( Pane pane )
    {
        getMainPane().getChildren().remove( pane );
        constructionAreaSizeCheck();
    }

    public ArgumentViewTree getArgTree( String key )
    {
        return getArgumentTrees().get( key );
    }
    
    public HashMap<String, ArgumentViewTree> getArgTreeMap()
    {
        return this.argumentTrees;
    }

    public void removeArgumentTree( String key )
    {
        getArgumentTrees().remove( key );
        constructionAreaSizeCheck();
    }

    public void registerNewArgTree( ArgumentViewTree tree )
    {
        String treeID = Integer.toString( tree.hashCode() );
        tree.setTreeID( treeID );
        getArgumentTrees().put( treeID, tree );
    }

    public void createProp( PropositionModel prop, Point2D coords )
    {
        try
        {
            dropProp( prop, coords );
        } 
        catch ( IOException ex )
        {
            Logger.getLogger( ConstructionAreaController.class.getName() ).log( Level.SEVERE, null, ex );
        }
    }

    public ArgumentViewTree getTargetTree( String s )
    {
        ArgumentViewTree targetTree = null;
        for ( String treeID : getArgumentTrees().keySet() )
        {
            if ( treeID.equals( s ) )
            {
                targetTree = getArgumentTrees().get( s );
            }
        }
        return targetTree;
    }

    /**
     * @param mainPane the mainPane to set
     */
    public void setMainPane( Pane mainPane )
    {
        this.mainPane = mainPane;
    }

    /**
     * @return the argumentTrees
     */
    public HashMap<String, ArgumentViewTree> getArgumentTrees()
    {
        return argumentTrees;
    }

    /**
     * @param argumentTrees the argumentTrees to set
     */
    public void setArgumentTrees( HashMap<String, ArgumentViewTree> argumentTrees )
    {
        this.argumentTrees = argumentTrees;
    }

    /**
     * @return the contextMenu
     */
    public ContextMenu getContextMenu()
    {
        return contextMenu;
    }

    /**
     * @param contextMenu the contextMenu to set
     */
    public void setContextMenu( ContextMenu contextMenu )
    {
        this.contextMenu = contextMenu;
    }

    /**
     * @return the targetpropBoxControlleroords
     */
    public Point2D getTargetpropBoxControlleroords()
    {
        return targetpropBoxControlleroords;
    }

    /**
     * @param targetpropBoxControlleroords the targetpropBoxControlleroords to set
     */
    public void setTargetpropBoxControlleroords( Point2D targetpropBoxControlleroords )
    {
        this.targetpropBoxControlleroords = targetpropBoxControlleroords;
    }

    /**
     * @return the rpc
     */
    public RootPaneController getRpc()
    {
        return rpc;
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
    public void setRootPaneController( RootPaneController rpc )
    {
        this.setRpc( rpc );
    }
    
    public void setConclusionPaneController( ConclusionPaneController cpc )
    {
        this.cpc            = cpc;
        this.ppc            = null;
        this.cqpc           = null;
        this.multiArgSubCPC = null;
    }
    
    public void setPremisePaneController( PremisePaneController ppc )
    {
        this.ppc            = ppc;
        this.cpc            = null;
        this.cqpc           = null;
        this.multiArgSubCPC = null;

    }
    
    public void setCQPaneController( CQPaneController cqpc )
    {
        this.cqpc           = cqpc;
        this.ppc            = null;
        this.cpc            = null;
        this.multiArgSubCPC = null;
    }
    
    public void setMultiArgSubCPC( MultiArgSubConclusionPaneController mascpc )
    {
        this.multiArgSubCPC = mascpc;
    }
    
    public CQPaneController getCQPaneController()
    {
        return this.cqpc;
    }
    
    public PremisePaneController getPremisePaneController()
    {
        return this.ppc;
    }
    
    public ConclusionPaneController getConclusionPaneController()
    {
        return this.cpc;
    }
    
        
    /**
     * @param rpc the rpc to set
     */
    public void setRpc( RootPaneController rpc )
    {
        this.rpc            = rpc;
    }
}
