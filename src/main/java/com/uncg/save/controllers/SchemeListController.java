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
import com.uncg.save.Premise;
import com.uncg.save.models.SchemeModel;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;

/**
 FXML controller for the view element that contains the individual argument
 scheme panes

 */
public class SchemeListController implements Initializable
{
    //////////////////////// INSTANCE VARIABLES /////////////////////////////      
    
    //
    //
    //
    @FXML private VBox                  schemeVBox;
    @FXML private ScrollPane            mainScrollPane;

    //
    //  Controllers
    //
    private RootPaneController          rootControl                = null;
    private List<SchemePaneController>  schemePaneControllers      = null;

    //
    //
    //
    private List<SchemeModel>           schemeModelElements        = null;
    private List<AnchorPane>            schemeViewElements         = null;

    //
    //  Lists that organize the search pairs (start, end) with their corresponding
    //  controllers. The controllers denote what controller the pairs are located
    //  in.
    //
    private List<Pair>                  schemeSearchPairs          = null;    
    private List<SchemePaneController>  schemeSearchControllers    = null; 
    
    //
    //  Indexes for referring to checkbox array indices
    //
    protected final int                 PREFIX_INDEX               = 0;
    protected final int                 SUFFIX_INDEX               = 1;
    protected final int                 CASE_SENSITIVE_INDEX       = 2;

    //
    //  Search options
    //
    protected final SelectableLabel[]   searchParameters           = new SelectableLabel[ 3 ];
    protected final Button[]            searchButtonSelectors      = new Button[ searchParameters.length ];
    
    //
    //  Background colors for search box options
    //
    private final Background            unselectedBackground       =  new Background( 
                                                                      new BackgroundFill( 
                                                                      new Color( 0.0, 0.0, 0.0, 0.0 ), 
                                                                      CornerRadii.EMPTY, Insets.EMPTY ) );
    private final Background            hoverBackground            =  new Background(
                                                                      new BackgroundFill (
                                                                      new Color( 0.0, 0.0, 0.0, 0.15 ),
                                                                      CornerRadii.EMPTY, Insets.EMPTY ) );
    private final Background            selectedBackground         =  new Background(
                                                                      new BackgroundFill (
                                                                      new Color( 0.0, 0.0, 0.0, 0.30 ),
                                                                      CornerRadii.EMPTY, Insets.EMPTY ) );    
    
    //
    //  Miscellaneous size variables
    //
    protected Dimension                 screenSize                 = Toolkit.getDefaultToolkit().getScreenSize();
    protected double                    height                     = screenSize.getHeight();
    private   int                       searchPos                  = -1;
   
    //////////////////////// INSTANCE VARIABLES /////////////////////////////      
    
    /**
     Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize( URL url, ResourceBundle rb )
    {
        //Initialize main pane to house the side pane
        this.mainScrollPane.getStyleClass().add( "side-pane" );
        this.mainScrollPane.setPrefHeight( height - 33 );
        ContextMenu menu         = new ContextMenu();
        MenuItem colorPickerMenu = null;
        if ( MainApp.DEBUG )
        {
            colorPickerMenu = new MenuItem( "Change Color" );
        }
        
        //Initialize the arraylist that holds the scheme data
        this.schemeViewElements = new ArrayList<>();
        
        Button search      = null; 
        Button searchNext  = null; 
        Button searchPrev  = null;
        
        try 
        {
            this.initializeSearchFunctions( search, searchNext, searchPrev );
        } 
        catch ( UnsupportedEncodingException ex ) 
        {
            Logger.getLogger( SchemeListController.class.getName() ).log( Level.SEVERE, null, ex );
        }
        
        // init context menu for the color picker
        if ( MainApp.DEBUG )
        {
            menu.getItems().add( colorPickerMenu );
        }
        this.mainScrollPane.setContextMenu( menu );

    }
    
    /**
     * Initializes and instantiates the search button, search next and search previous 
     * buttons.
     * 
     * @param searchButton
     * @param searchNext
     * @param searchPrev
     * @throws UnsupportedEncodingException 
     */
    private void initializeSearchFunctions( Button searchButton, Button searchNext, Button searchPrev ) 
                                            throws UnsupportedEncodingException
    {
        HBox hbox    = new HBox();

        //Search button
        searchButton = new Button( "SEARCH" );
            searchButton.setPrefSize( 250.0, 20.0 );
            searchButton.setAlignment( Pos.CENTER );
            searchButton.getStyleClass().add( "search-text-button" );
        
        //Search next button
        searchNext   = new Button( new String( "\u27fc".getBytes("UTF-8"), "UTF-8" ) );
            searchNext.setPrefWidth( 40.0 );
            searchNext.setAlignment( Pos.CENTER );
            searchNext.getStyleClass().add( "search-text-button" );
            searchNext.setDisable( true );
            
        //Search previous button
        searchPrev   = new Button( new String( "\u27fb".getBytes("UTF-8"), "UTF-8" ) );
            searchPrev.setPrefWidth( 40.0 );    
            searchPrev.setAlignment( Pos.CENTER );
            searchPrev.getStyleClass().add( "search-text-button" );
            searchPrev.setDisable( true );
            
        //Add to the horizontal box, then add to global vbox
        hbox.getChildren().addAll( searchButton, searchPrev, searchNext  );
        this.schemeVBox.getChildren().add( hbox );
        
        //Configure handlers
        this.setHandlerForSearch( searchButton, searchNext, searchPrev );
        this.setHandlerForSearchNext( searchButton, searchNext, searchPrev );
        this.setHandlerForSearchPrev( searchButton, searchPrev, searchNext );


    }
    
    /**
     * Event handler for when the user presses the search next button. 
     * 
     * References for both buttons are included to toggle the enable states.
     * 
     * For searchNext, if we hit the end of the list, we disable it,
     * 
     * @param searchNext 
     * @param searchPrev
     */
    private void setHandlerForSearchNext( Button searchButton, Button searchNext, Button searchPrev )
    {
        
        
        searchNext.setOnAction( action -> 
        {
            Pair nextPair = this.schemeSearchPairs.get(  ++ searchPos );

            searchButton.setText( ( searchPos + 1 ) + "/" + this.schemeSearchControllers.size() );

            //  If we go to the next scheme, we need to close the previous
            //  one and then open the next (if they are the same, they
            //  close & reopen).
            if( this.schemeSearchControllers.get( searchPos ).schemeCollapsed )
            {
                this.schemeSearchControllers.get( searchPos ).expandScheme();
                this.schemeSearchControllers.get( searchPos - 1 ).expandScheme();
            }
            
            //  Select the next pair
            searchPrev.setDisable( false );
            searchNext.setDisable( searchPos == this.schemeSearchPairs.size() - 1 );

            int nextPairKey = ( int ) nextPair.getKey();
            int nextPairVal = ( int ) nextPair.getValue();
            
            int premiseTextAreaSize = this.schemeSearchControllers.get( searchPos ).premiseLabel.getText().length();
            int cqTextAreaSize      = this.schemeSearchControllers.get( searchPos ).criticalQuestionLabel.getText().length();
            
            TextArea searchArea     = null;
            System.out.println( nextPairKey + " size of prem: " + premiseTextAreaSize );
            if ( nextPairKey >= premiseTextAreaSize )
            {
                searchArea = this.schemeSearchControllers.get( searchPos ).criticalQuestionLabel;
                
                // If they're expanded, close them
                if ( this.schemeSearchControllers.get( searchPos ).cqCollapsed )
                {
                    // Opens CQs
                    this.schemeSearchControllers.get( searchPos ).expandCQs();
                }
                nextPairKey -= premiseTextAreaSize;
                nextPairVal -= premiseTextAreaSize;
            }
            else
            {
                searchArea = this.schemeSearchControllers.get( searchPos ).premiseLabel;          
            }
            
            searchArea.selectRange( nextPairKey, nextPairVal );  
        } );
    }
    
    /**
     * Event handler for when the user presses the search previous button. 
     * 
     * References for both buttons are included to toggle the enable states.
     * 
     * For searchPrev, if our searchPos goes below 0, we need to stop 
     * going backwards.
     * 
     * @param searchPrev
     * @param searchNext
     */    
    private void setHandlerForSearchPrev( Button searchButton, Button searchPrev, Button searchNext )
    {
        searchPrev.setOnAction( action -> 
        {
            //  If we go to the previous scheme, we need to close the current
            //  one and then open the prev (if they are the same, they
            //  close & reopen).
            Pair nextPair = this.schemeSearchPairs.get(  -- searchPos );
            
            searchButton.setText( ( searchPos + 1 ) + "/" + this.schemeSearchControllers.size() );
            
            if( this.schemeSearchControllers.get( searchPos ).schemeCollapsed )
            {
                this.schemeSearchControllers.get( searchPos ).expandScheme();
                this.schemeSearchControllers.get( searchPos + 1 ).expandScheme();
            }                        
            //  Select the previous pair
            
            
            if ( searchPos == 0 )
            {
                searchPrev.setDisable( true );
            }
            
            searchNext.setDisable( false );
            
            int nextPairKey = ( int ) nextPair.getKey();
            int nextPairVal = ( int ) nextPair.getValue();
            
            int premiseTextAreaSize = this.schemeSearchControllers.get( searchPos ).premiseLabel.getText().length();
            int cqTextAreaSize      = this.schemeSearchControllers.get( searchPos ).criticalQuestionLabel.getText().length();
            
            TextArea searchArea     = null;
            
            if ( nextPairKey >= premiseTextAreaSize )
            {
                searchArea = this.schemeSearchControllers.get( searchPos ).criticalQuestionLabel;
                
                // If they're expanded, close them
                if ( this.schemeSearchControllers.get( searchPos ).cqCollapsed )
                {
                    // Opens CQs
                    this.schemeSearchControllers.get( searchPos ).expandCQs();
                }
                nextPairKey -= premiseTextAreaSize;
                nextPairVal -= premiseTextAreaSize;
            }
            else
            {
                searchArea = this.schemeSearchControllers.get( searchPos ).premiseLabel;          
            }         
            
            searchArea.selectRange( nextPairKey, nextPairVal );        
        } );
    }

  
    /**
     * Event handler for when the user presses the search button. We need 
     * access to all three buttons to toggle the searchNext/Prev buttons 
     * whenever they hit either end of the pair arraylist.
     * 
     */
    private void setHandlerForSearch( Button searchButton, Button searchNext, Button searchPrev )
    {
        searchButton.addEventFilter( MouseEvent.MOUSE_ENTERED, ( MouseEvent event ) ->
        {
            searchButton.setText( "SEARCH" );
        });
        
        searchButton.addEventFilter( MouseEvent.MOUSE_EXITED, ( MouseEvent event ) ->
        {
            searchButton.setText( this.schemeSearchControllers != null ? ( this.searchPos + 1 ) + "/" + this.schemeSearchControllers.size() 
                                                                       : "SEARCH" );
        });
        
        searchButton.setOnAction( action -> {
            
            // Resets the text
            searchButton.setText( "SEARCH" );
            
            TextField searchField = this.rootControl.getTitleAndMenuBarController()
                                                    .configureSearchBox( "Scheme Search", this );
            
            this.searchPos        = -1;
            
            // If we have any open schemes, we need to close them so nothing looks
            // out of place/weird.
            this.closeSchemes();
            
            ////////////////////////////
            //                        //
            //                        //
            //  REGEX parsing begins  //
            //                        //
            //                        //
            ////////////////////////////
            String searchString = "";
            this.searchPos           = -1;
            
            if ( ! searchField.getText().isEmpty() )
            {
                this.schemeSearchPairs       = new ArrayList<>();
                this.schemeSearchControllers = new ArrayList<>();
                
                for( int i = 0; i < this.schemePaneControllers.size(); i++ )
                {
                    SchemePaneController currentController = this.schemePaneControllers.get( i );
                    
                    
                    searchString        = searchField.getText();
                
                    //  Create the pattern for the regular expression
                    Pattern pattern     = this.getRegexPattern( searchString );
                    Matcher matcher     = pattern.matcher( currentController.premiseLabel.getText() + currentController.getCriticalQuestionsTextArea().getText() ); //Where input is a TextInput class
                    boolean searching   = matcher.find( 0 );

                    //Finds all starting and ending positions of each word in the
                    //text. 
                    while ( searching )
                    {
                        this.schemeSearchPairs.add( new Pair( matcher.start(), matcher.end() ) );
                        this.schemeSearchControllers.add( currentController );
                        searching = matcher.find( matcher.end() );
                    }
                }
                
                //If we didn't find any pairs, we let the user know
                if ( this.schemeSearchPairs.isEmpty() )
                {
                    this.configureNotFoundStage( ( Stage ) mainScrollPane.getScene().getWindow(), searchString );
                } 
                
                //Otherwise, we go through the schemes and select the texts
                else
                {
                    Pair nextPair = this.schemeSearchPairs.get(  ++ searchPos );
                    this.schemeSearchControllers.get( searchPos ).premiseLabel.selectRange( 
                                    ( int ) nextPair.getKey(), ( int ) nextPair.getValue() ); // By default, selects the first pair
                    
                    //  If there's only one word in the arraylist, we can't
                    //  go forward or backwards.
                    if( this.schemeSearchControllers.size() == 1 )
                    {
                        searchNext.setDisable( true );
                        searchPrev.setDisable( true );
                    }
                    else
                    {
                        searchNext.setDisable( false );
                    }
                    
                    //  If we find a word that's in a collapsed scheme,
                    //  we need to expand it so the user can see.
                    if( this.schemeSearchControllers.get( searchPos ).schemeCollapsed )
                    {
                        this.schemeSearchControllers.get( searchPos ).expandScheme();
                    }
                    
                    searchButton.setText( ( searchPos + 1 ) + "/" + this.schemeSearchControllers.size() );
                }
            } 
        } );
    }

    public void setSchemeModelElements( List<SchemeModel> schemeElements, boolean replacing )
    {
        this.schemePaneControllers = new ArrayList<>();
        this.schemeModelElements   = schemeElements;
        try
        {
            if ( replacing )
            {
                this.clearSchemes();
            }
            
            this.populateSchemeView();
        } catch ( IOException ex )
        {
            Logger.getLogger( SchemeListController.class.getName() ).log( Level.SEVERE, null, ex );
        }

    }

    /**
     * Loads in the scheme panel layout and puts the scheme model object inside
     * the pane.
     * 
     * @throws IOException 
     */
    public void populateSchemeView() throws IOException
    {
        int     examplesIndex = -1;
        boolean examplesFound = true;
        
        // If the user tries to override the previous scheme, we need to clear
        // out the previous ones
        this.clearSchemes();
        
        for ( SchemeModel scheme : schemeModelElements )
        {
            FXMLLoader loader = new FXMLLoader( getClass().getResource( "/fxml/SchemePane.fxml" ) );
            AnchorPane schemePane = loader.load();
            SchemePaneController schemePaneControl = loader.<SchemePaneController>getController();
            schemePaneControl.setScheme( scheme );
            setSchemeModelViewElements( schemePaneControl, scheme );
            schemePaneControl.setParentController( this );
            this.schemeViewElements.add( schemePane );
            this.schemeVBox.getChildren().add( schemePane );
            this.schemePaneControllers.add( schemePaneControl );
        }
    }
    
    /**
     * Removes any schemes that are currently loaded in. Only used for 
     * replacing the existing schemes.
     */
    public void clearSchemes()
    {
        this.schemeViewElements.clear();
        this.schemeVBox.getChildren().clear();
        this.schemePaneControllers.clear();
        
        Button search      = null; 
        Button searchNext  = null; 
        Button searchPrev  = null;
        
        // Because we clear the SchemeVBox, we need to reinstantiate the search
        // elements in the SLC
        try 
        {
            this.initializeSearchFunctions( search, searchNext, searchPrev );
        } 
        catch ( UnsupportedEncodingException ex ) 
        {
            Logger.getLogger( SchemeListController.class.getName() ).log( Level.SEVERE, null, ex );
        }        
    }
    
    /**
     * Closes any schemes that are open.
     */
    private void closeSchemes()
    {
        if( this.schemeSearchControllers == null)
        {
             return;
        }
        
        for( int i = 0; i < this.schemeSearchControllers.size(); i++ )
        {
            if( ! this.schemeSearchControllers.get( i ).schemeCollapsed )
            {
                this.schemeSearchControllers.get( i ).expandScheme();
            }
        }
    }

    public void setSchemeModelViewElements( SchemePaneController control, SchemeModel model )
    {
        control.setPremisesTextArea( premiseTextDisplayBuilder( model.getPremises() ) );
        control.setCriticalQuestionsTextArea( cqTextDisplayBuilder( model.getCriticalQs() ) );
        control.setTitle();
    }

    /**
     * Constructs the scheme models inside of the scheme pane on the right side of
     * the screen with the included premises and examples.
     * 
     * @param s - list of premise objects that contain names and definitions;
     *            the toString() representation of the Premise objects is what
     *            is displayed in the pane.
     * 
     * @param examples
     * 
     * @return StringBuilder representation of text on right pane
     */
    private String premiseTextDisplayBuilder( List<Premise> s )
    {
        StringBuilder sb = new StringBuilder();
        int i            = 0;
        while ( i < s.size() )
        {
            int j = i + 1;
            sb.append( "Premise :" );
            sb.append( "\n" );
            sb.append( s.get( i ) );
            sb.append( "\n\u2015\u2015\u2015\u2015\u2015"
                     + "\u2015\u2015\u2015\u2015\u2015"
                     + "\u2015\u2015\u2015\u2015\n" );            
            i ++;
        }
        return sb.toString();
    }


    private String cqTextDisplayBuilder( List<String> s )
    {
        StringBuilder sb = new StringBuilder();
        int i            = 0;
        while ( i < s.size() )
        {
            int j = i + 1;
            sb.append( s.get( i ) );
            sb.append( "\n" );
            sb.append( "\n" );
            i ++;
        }
        return sb.toString();
    }
    
    /**
     * Creates a Pattern object suitable for searching through 
     * a string of text to find prefixes, suffixes, or whole words,
     * including case-sensitivity. All of these are options selected
     * by the user.
     * 
     * @param searchString
     * @return Pattern object
     */
    private Pattern getRegexPattern( String searchString )
    {
        StringBuilder regex   = new StringBuilder();
        Pattern       pattern = null;
        
        
        //Prefix
        if ( searchParameters[ PREFIX_INDEX ].isSelected )
        {
           regex.append("\\b").append( searchString ).append("[^\\s]+");
        } 
        //Suffix
        else if ( searchParameters[ SUFFIX_INDEX ].isSelected )
        {
           regex.append("[^\\s]+").append( searchString ).append("\\b");
        }
        //Default search
        else 
        {
           if( searchString.contains( "*" ) )
           {
               searchString = searchString.replace("*", ".");
           }
           
           regex.append( "\\b" ).append( searchString ).append("\\b"); 
        }
                
         //If case sensitive
        if ( searchParameters[ CASE_SENSITIVE_INDEX ].isSelected )
        {
           pattern = Pattern.compile( regex.toString() );
        }
        else
        {
           pattern = Pattern.compile( regex.toString(), Pattern.CASE_INSENSITIVE );
        }
        return pattern;
    }    

    /**
     * Initializes all labels into the respective array, and also establishes
     * all mouse listeners.
     */
    protected void initSelectableLabels()
    {
        ///////Initialize all labels/////////

        //  Load prefix configuration
        searchParameters[ PREFIX_INDEX ] = new SelectableLabel( "Match Prefix" );
            searchParameters[ PREFIX_INDEX ].setStyle( "-fx-border-color: #D3D3D3; -fx-border-width : 1 0 1 0; " );
            searchParameters[ PREFIX_INDEX ].setFont( Font.font( "Calibri", 20 ) );
            searchParameters[ PREFIX_INDEX ].setPrefWidth( 250 );
            searchParameters[ PREFIX_INDEX ].setBackground( this.unselectedBackground );
            
            //  If the user hovers over the box, it is slightly greyed
            searchParameters[ PREFIX_INDEX ].setOnMouseEntered( ( MouseEvent ev ) ->       
            {
                if ( ! searchParameters[ PREFIX_INDEX ].isSelected )
                {
                    searchParameters[ PREFIX_INDEX ].setBackground( this.hoverBackground );
                }
            } );
            
            //  If the user exits the box, it is returned to the default unless
            //  they have selected it.
            searchParameters[ PREFIX_INDEX ].setOnMouseExited( ( MouseEvent ev ) -> 
            {
                if ( ! searchParameters[ PREFIX_INDEX ].isSelected )
                {
                    searchParameters[ PREFIX_INDEX ].setBackground( this.unselectedBackground );
                }
            } );
            
            //  If the user clicks on the label, it is deemed "selected" 
            //  and takes the grey background
            searchParameters[ PREFIX_INDEX ].setOnMousePressed ( ( MouseEvent ev ) -> 
            {
                
                searchParameters[ PREFIX_INDEX ].isSelected = 
                        ! searchParameters[ PREFIX_INDEX ].isSelected;
                
                if( searchParameters[ PREFIX_INDEX ].isSelected )
                {
                    searchButtonSelectors[ PREFIX_INDEX ].setText( "\u2714" );

                }
                else
                {
                    searchParameters[ PREFIX_INDEX ].setBackground( this.unselectedBackground );
                    searchButtonSelectors[ PREFIX_INDEX ].setText( "" );
                }
            } );
            
        //  Load case sensitive config
        searchParameters[ SUFFIX_INDEX ] = new SelectableLabel( "Match Suffix" );
            searchParameters[ SUFFIX_INDEX ].setStyle( "-fx-border-color: #D3D3D3; -fx-border-width : 1 0 1 0;" );
            searchParameters[ SUFFIX_INDEX ].setFont( Font.font( "Calibri", 20 ) );
            searchParameters[ SUFFIX_INDEX ].setPrefWidth( 250 );
            searchParameters[ SUFFIX_INDEX ].setBackground( this.unselectedBackground );
            
            //  If the user hovers over the box, it is slightly greyed
            searchParameters[ SUFFIX_INDEX ].setOnMouseEntered( ( MouseEvent ev ) ->       
            {
                if ( ! searchParameters[ SUFFIX_INDEX ].isSelected )
                {
                    searchParameters[ SUFFIX_INDEX ].setBackground( this.hoverBackground );
                }
            } );
            
            //  If the user exits the box, it is returned to the default unless
            //  they have selected it.
            searchParameters[ SUFFIX_INDEX ].setOnMouseExited( ( MouseEvent ev ) -> 
            {
                if ( ! searchParameters[ SUFFIX_INDEX ].isSelected )
                {
                    searchParameters[ SUFFIX_INDEX ].setBackground( this.unselectedBackground );
                }
            } );
            
            //  If the user clicks on the label, it is deemed "selected" 
            //  and takes the grey background
            searchParameters[ SUFFIX_INDEX ].setOnMousePressed ( ( MouseEvent ev ) -> 
            {
                searchParameters[ SUFFIX_INDEX ].isSelected = 
                        ! searchParameters[ SUFFIX_INDEX ].isSelected;
                
                if( searchParameters[ SUFFIX_INDEX ].isSelected )
                {
                    searchButtonSelectors[ SUFFIX_INDEX ].setText( "\u2714" );
                }
                else
                {
                    searchParameters[ SUFFIX_INDEX ].setBackground( this.unselectedBackground );
                    searchButtonSelectors[ SUFFIX_INDEX ].setText( "" );
                }
            } );
        
        //  Load case sensitive config
        searchParameters[ CASE_SENSITIVE_INDEX ] = new SelectableLabel( "Match Case" );
            searchParameters[ CASE_SENSITIVE_INDEX ].setStyle( "-fx-border-color: #D3D3D3; -fx-border-width : 1 0 1 0;" );
            searchParameters[ CASE_SENSITIVE_INDEX ].setFont( Font.font( "Calibri", 20 ) );
            searchParameters[ CASE_SENSITIVE_INDEX ].setPrefWidth( 250 );
            searchParameters[ CASE_SENSITIVE_INDEX ].setBackground( this.unselectedBackground );
            
            //  If the user hovers over the box, it is slightly greyed
            searchParameters[ CASE_SENSITIVE_INDEX ].setOnMouseEntered( ( MouseEvent ev ) ->       
            {
                if ( ! searchParameters[ CASE_SENSITIVE_INDEX ].isSelected )
                {
                    searchParameters[ CASE_SENSITIVE_INDEX ].setBackground( this.hoverBackground );
                }
            } );
            
            //  If the user exits the box, it is returned to the default unless
            //  they have selected it.
            searchParameters[ CASE_SENSITIVE_INDEX ].setOnMouseExited( ( MouseEvent ev ) -> 
            {
                if ( ! searchParameters[ CASE_SENSITIVE_INDEX ].isSelected )
                {
                    searchParameters[ CASE_SENSITIVE_INDEX ].setBackground( this.unselectedBackground );
                }
            } );
            
            //  If the user clicks on the label, it is deemed "selected" 
            //  and takes the grey background
            searchParameters[ CASE_SENSITIVE_INDEX ].setOnMousePressed ( ( MouseEvent ev ) -> 
            {
                searchParameters[ CASE_SENSITIVE_INDEX ].isSelected = 
                        ! searchParameters[ CASE_SENSITIVE_INDEX ].isSelected;
                
                if( searchParameters[ CASE_SENSITIVE_INDEX ].isSelected )
                {
                    searchButtonSelectors[ CASE_SENSITIVE_INDEX ].setText( "\u2714" );
                }
                else
                {
                    searchParameters[ CASE_SENSITIVE_INDEX ].setBackground( this.unselectedBackground );
                    searchButtonSelectors[ CASE_SENSITIVE_INDEX ].setText( "" );
                }
            } );
    }
    
     /**
     * Configures the stage for when the user searches a word that isn't found
     * by the search algorithm. 
     * 
     * This was originally an Alert dialog box, but I reconfigured it into
     * a stage so the construction area wouldn't minimize.
     * 
     * @param mainPane
     * @param notFound 
     */
    private void configureNotFoundStage( Stage mainPane, String notFound )
    {
        // Initialize stage for word not found
        //
        final Stage dialog = new Stage();
        dialog.setTitle( "Word Not Found" );
        dialog.getIcons().add( MainApp.icon );
        dialog.initModality( Modality.NONE );
        dialog.resizableProperty().setValue( false );
        dialog.initOwner( mainPane );
        
        // Display the string that wasnt found
        Label label = new Label( notFound + " was not found." );
            label.setAlignment( Pos.CENTER );
            label.setFont( Font.font( 14 ) );
            
        dialog.setScene( new Scene( label, 250, 25 ) );
        // If the user presses the enter key, close the window.
        dialog.getScene().setOnKeyPressed( _key -> 
        { 
            if (_key.getCode() == KeyCode.ENTER ) 
            {
                dialog.close(); 
            }
        } );
        
        dialog.showAndWait();
    }
    
    public void setRootControl( RootPaneController control )
    {
        rootControl = control;
    }
    
    public void setParentController( RootPaneController parent )
    {
        this.rootControl = parent;
    }    

    public ScrollPane getMainScrollPane()
    {
        return this.mainScrollPane;
    }

    public List<AnchorPane> getSchemeElements()
    {
        return schemeViewElements;
    }
    
    public VBox getVBox()
    {
        return this.schemeVBox;
    }

}