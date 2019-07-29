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
import com.uncg.save.models.SchemeModel;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;

/**
 FXML controller for the main underlying pane of the application.

 */
public class RootPaneController implements Initializable
{
    //////////////////////// INSTANCE VARIABLES /////////////////////////////          
    
    //  Operating System Information
    //
    //  This will be for replacing unicode characters on non-Windows
    //  systems
    //
    public static final String  OS                   = System.getProperty("os.name").toLowerCase();
    
    //
    //
    //
    private Scene               scene;
    
    //
    // Panes
    //
    @FXML protected ScrollPane  mainScrollPane       = null;
    @FXML private AnchorPane    caseStudyAnchorPane  = null;
    @FXML private AnchorPane    schemesAnchorPane    = null;

    //
    // Controllers
    //
    
    @FXML private   TitleAndMenuBarController  titleAndMenuBarController  = null;
    @FXML protected SchemeListController       schemeListController       = null;
    @FXML protected ConstructionAreaController constructionAreaController = null;
    private         ShortcutKeyController      shortcutKeyController      = null;

    //
    // Buttons
    //
    @FXML private Button      hideSchemesButton;

    //
    // Model Lists
    //
    private List<SchemeModel> schemeModelList        = null;

    //
    // Booleans for the scheme buttons
    //
    private boolean           schemesButtonHidden    = true;

    //
    // Booleans determining the visibility of the panes
    //
    private boolean           schemesUp              = false;

    //
    // Dimensions of the current screen 
    //
    private final Dimension   screenSize   = Toolkit.getDefaultToolkit().getScreenSize();
    private double            screenWidth  = 0.0;

    //
    // Pane dimensions 
    //
    private final int         INIT_SCHEME_PANE_WIDTH = 0;
    private final int         BUTTON_DIMENSION       = 25;
    private int               prevSchemeWidth        = INIT_SCHEME_PANE_WIDTH;
    
    //
    //  
    //
    protected HostServices    hostServices;
    
    
    //////////////////////// INSTANCE VARIABLES /////////////////////////////  

    @Override
    public void initialize( URL url, ResourceBundle rb )
    {
        this.hideSchemesButton.setDisable( true );
        this.hideSchemesButton.setVisible( false );

        javafx.stage.Screen screen = Screen.getPrimary();
        javafx.geometry.Rectangle2D rectangle2D = screen.getVisualBounds();
        this.screenWidth = rectangle2D.getWidth();

        this.schemesAnchorPane.setPrefWidth( INIT_SCHEME_PANE_WIDTH );
        this.hideSchemesButton.setMaxWidth( BUTTON_DIMENSION );
        this.hideSchemesButton.setMinWidth( BUTTON_DIMENSION );
        this.mainScrollPane.setMaxWidth( screenWidth - schemesAnchorPane.getPrefWidth() );
        this.mainScrollPane.setMinWidth( screenWidth - schemesAnchorPane.getPrefWidth() );
        this.constructionAreaController.getMainPane().setMinWidth( mainScrollPane.getMaxWidth() );
        this.titleAndMenuBarController.setParentController( this );
        this.schemeListController.setParentController( this );
    }
    
    public void setScene( Scene scene )
    {
        this.scene = scene;
        this.shortcutKeyController = new ShortcutKeyController( scene, this.titleAndMenuBarController );
        
        if ( MainApp.Jmp_Start )
        {
            this.titleAndMenuBarController.loadArgumentSchemeData( new ActionEvent() );
        }
        
        this.scene.setOnKeyPressed( _key ->
        {
            if( _key.getCode() == KeyCode.ESCAPE )
            {
                this.titleAndMenuBarController.closeProgram();
            }
        } );
    }

    public void setSchemeModelList( List<SchemeModel> modelList, boolean replacing )
    {
        this.schemeModelList = modelList;
        this.schemeListController.setSchemeModelElements( schemeModelList, replacing );
    }

    /**
     Enables the schemes button once it has been loaded in from the user's
     scheme file.
     */
    private void enableSchemesButton()
    {
        this.hideSchemesButton.setMaxWidth( BUTTON_DIMENSION );
        this.hideSchemesButton.setMinWidth( BUTTON_DIMENSION );
        this.mainScrollPane.setMaxWidth( mainScrollPane.getMaxWidth() - BUTTON_DIMENSION );
        this.mainScrollPane.setMinWidth( mainScrollPane.getMinWidth() - BUTTON_DIMENSION );
        this.hideSchemesButton.setDisable( false );

        this.schemesButtonHidden = false;
        this.hideSchemesButton.setVisible( true );
        this.prevSchemeWidth = 350;
    }

    /**
     Attempting to experiment with dynamic window resizing. According to various
     online sources, AnchorPanes are NOT the way to go; SplitPanes may be
     necessary.
     */
    @FXML
    public void toggleSchemes()
    {
        if ( this.schemesButtonHidden )
        {
            this.enableSchemesButton();
            this.schemesUp = false;
        }

        if (  ! this.schemesUp )
        {
            //int width = this.getSchemeSize();
            //this.schemeListController.getMainScrollPane().setPrefWidth( width );
            //this.prevSchemeWidth = width;
            //showSchemes( width );
            this.showSchemes( 350 );
        } 
        else
        {
            this.hideSchemes( 350 );
        }
        this.schemesUp =  ! this.schemesUp;
    }

    private void showSchemes( int width )
    {
        this.schemesAnchorPane.setMinWidth( width );
        this.schemesAnchorPane.setMaxWidth( width );
        this.mainScrollPane.setMaxWidth( mainScrollPane.getMaxWidth() - width );
        this.mainScrollPane.setMinWidth( mainScrollPane.getMinWidth() - width );
        this.constructionAreaController.constructionAreaSizeCheck();
        try 
        {
            //Just trying to make it more obvious to the user ;)
            if ( OS.contains("win") )
            {
                hideSchemesButton.setText( new String( ( "\u2b9e\u2b9e\u2b9e\u2b9e\u2b9e\u2b9e\u2b9e\u2b9e\u2b9e\u2b9e"
                                                       + "\u2b9e\u2b9e\u2b9e\u2b9e\u2b9e\u2b9e\u2b9e\u2b9e\u2b9e\u2b9e" ).
                                                    getBytes( "UTF-8" ), "UTF-8" ) );
            } 
            else 
            {
                hideSchemesButton.setText("→→→→→→→→→→→→→→→→→→→→");
            }
        } 
        catch ( UnsupportedEncodingException ex ) 
        {
            Logger.getLogger( RootPaneController.class.getName() ).log( Level.SEVERE, null, ex );
        }
        this.constructionAreaController.getMainPane().setMinWidth( mainScrollPane.getMinWidth() );
        this.constructionAreaController.getMainPane().setPrefWidth( constructionAreaController.getMainPane().getWidth() + width );

    }

    private void hideSchemes( int width )
    {
        this.schemesAnchorPane.setMinWidth( 0 );
        this.schemesAnchorPane.setMaxWidth( 0 );
        this.mainScrollPane.setMaxWidth( mainScrollPane.getMaxWidth() + width );
        this.mainScrollPane.setMinWidth( mainScrollPane.getMinWidth() + width );
        this.constructionAreaController.constructionAreaSizeCheck();
        try 
        {
            if ( OS.contains( "win" ) )
            {
                this.hideSchemesButton.setText( new String( ("\u2b9c\u2b9c\u2b9c\u2b9c\u2b9c\u2b9c\u2b9c\u2b9c\u2b9c\u2b9c"
                                                      + "\u2b9c\u2b9c\u2b9c\u2b9c\u2b9c\u2b9c\u2b9c\u2b9c\u2b9c\u2b9c" )
                                                    .getBytes( "UTF-8" ), "UTF-8" ) );
            }
            else
            {
                this.hideSchemesButton.setText( "←←←←←←←←←←←←←←←←←←←←" );
            }
        } 
        catch ( UnsupportedEncodingException ex ) {
            Logger.getLogger( RootPaneController.class.getName() ).log( Level.SEVERE, null, ex );
        }
        this.constructionAreaController.getMainPane().setMinWidth( mainScrollPane.getMinWidth() );
        this.constructionAreaController.getMainPane().setPrefWidth( constructionAreaController.getMainPane().getWidth() - width );

    }

    private int getSchemeSize()
    {
        TextInputDialog dialog = new TextInputDialog( "Scheme Window Size" );
        dialog.setTitle( "Scheme Width" );
        dialog.setHeaderText( "Input Scheme Width, or Blank For Default (350):" );

        Optional<String> pixelSize = dialog.showAndWait();

        int width = -1;

        if ( pixelSize.isPresent() )
        {
            try
            {
                width = Integer.parseInt( pixelSize.get() );

            } 
            catch ( ClassCastException ex )
            {
                throw new ClassCastException( "Cannot cast " + pixelSize + "to Integer." );
            }
        } else
        {
            return 350;
        }

        return width;
    }
    
    public void setHostServices( HostServices hs )
    {
        this.hostServices = hs;
    }
    
    public TitleAndMenuBarController getTitleAndMenuBarController()
    {
        return this.titleAndMenuBarController;
    }

    public ConstructionAreaController getConstructionAreaController()
    {
        return this.constructionAreaController;
    }

    public boolean schemesShowing()
    {
        return this.schemesUp;
    }

    public List<SchemeModel> getSchemeModels()
    {
        return this.schemeModelList;
    }
}
