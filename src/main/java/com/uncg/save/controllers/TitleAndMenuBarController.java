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
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See thefo
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
import com.uncg.save.SchemeList;
import com.uncg.save.argumentviewtree.ArgumentNode;
import com.uncg.save.argumentviewtree.ArgumentViewTree;
import com.uncg.save.models.ArgumentModel;
import com.uncg.save.models.CQTuple;
import com.uncg.save.models.CaseModel;
import com.uncg.save.models.EthicsModel;
import com.uncg.save.models.SchemeModel;
import com.uncg.save.util.AlertStage;
import com.uncg.save.util.FXUtils;
import com.uncg.save.util.MouseUtils;
import java.awt.Desktop;
import java.awt.Toolkit;
import javafx.print.PrinterJob;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.ResourceBundle;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.PageLayout;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Pair;
import javax.swing.SwingUtilities;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.xml.sax.SAXException;

/*
===============================================================================
        FXML controller for the title bar and menu bar of the application.

    This class, as the title comment suggests, handles the events, buttons, etc.
    for the title and menu bar at the top of the screen (above the construction
    area). Most, if not all, of the menus are created via FXML files, but 
    the sub-options within each menu are created in this class because 
    they either have some behavior that requires more than what FXML allows,
    or they're needed as a reference in another arbitrary class.

    This class needs constant refactoring, but thus far, I've tried to keep
    everything well-documented and maintained. Of course, there are bugs
    and lingering issues which will be fixed at a later date.
===============================================================================
 */
public class TitleAndMenuBarController implements Initializable
{
    //////////////////////// INSTANCE VARIABLES /////////////////////////////
    
    //
    //  Different menus each representing a tab on the 
    //  menu bar.
    //
    @FXML private MenuBar      menuBar;
    @FXML private Menu         loadMenu;
    @FXML private Menu         casesMenu;
    @FXML private Menu         toolsMenu;
    @FXML private Menu         ethicsMenu;
    @FXML private Menu         argumentsMenu;      
    @FXML private Menu         helpMenu;
    
    //
    //  Title of the window
    //
    @FXML private Label        windowTitle;

    //
    //  RootParentControl reference
    //
    private RootPaneController parentControl              = null;
    
    //
    //  AnchorPane references for later resizing
    //
    private AnchorPane         ethicsAnchorPane           = null;
    private AnchorPane         casesAnchorPane            = null;
    private AnchorPane         tutorialAnchorPane         = null;
    
    //
    //  Tabpanes for later reference
    //
    private TabPane            ethicsTabPane              = null;
    private TabPane            casesTabPane               = null;
    private TabPane            tutorialTabPane            = null;
    private SchemeList         sl                         = null;

    //
    //  Search/query indexes
    //
    private List<Pair>         e_stringIndexes            = null;
    private List<Pair>         c_stringIndexes            = null;

    //
    //  Stores references to the objects that are removed when the 
    //  tabs are minimzed
    //
    //  Ethics buttons
    private Stack<Control>     ethicsButtons              = null;
    //  Cases buttons
    private Stack<Control>     casesButtons               = null;
    //  Tutorial buttons @TODO
    private Button             t_minimizeExitButtonRemove = null;

    //
    //  Indexes for referring to the objects in the tabpanes
    //  when minimizing/maximizing the tabs
    //
    //  NOTE: These are not necessary to have anymore due
    //  to the stack implementation, but it's nice to have.
    //
    private final int          START_STACK_INDEX          = 11; //Start index of stack
    private final int          END_STACK_INDEX            = 2;  //End index of stack
    private final int          SEARCH_PREV_INDEX          = 5;
    private final int          SEARCH_NEXT_INDEX          = 4;
    private final int          MINMAX_INDEX               = 1;
    
    //
    //  Positions in the text-areas of the selected 
    //  string.
    //
    private int                e_searchPosition           = 0;
    private int                c_searchPosition           = 0;
    
    //
    //  Indexes for referring to checkbox array indices
    //
    private final int          PREFIX_INDEX               = 0;
    private final int          SUFFIX_INDEX               = 1;
    private final int          CASE_SENSITIVE_INDEX       = 2;

    //
    //  Booleans for determining if the tabs are minimized or not
    //
    private boolean            ethicsMinimized            = false;
    private boolean            casesMinimized             = false;
    private boolean            tutorialMinimized          = false;
    private boolean            mouseOverText              = false; //If we are over the text boxes, dont drag
                                                                   //(enables copy and pasting)

    //
    //  Determines whether or not the tabs are currently open
    //
    private boolean            ethicsActive               = false;
    private boolean            casesActive                = false;
    private boolean            tutorialActive             = false;
    private boolean            schemesActive              = false;
    protected boolean          openArgWithKeyFlag         = false;
    private boolean            displayFullScreenWarning   = true;

    //
    //  If the user has saved, the save warning dialog box won't pop up.
    //
    private boolean            saved                      = false;    
    
    //
    //  Dimension information
    //  **Uses a pre-speciifed aspect ratio (16:9) to calculate height**
    private static final int   PANE_WIDTH                 = 600;
    private static final int   PANE_HEIGHT                = ( int ) ( ( PANE_WIDTH ) / 16 * 9 );
    private static final int   MAX_PANE_WIDTH             = ( int ) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    private static final int   MAX_PANE_HEIGHT            = ( int ) Toolkit.getDefaultToolkit().getScreenSize().getHeight();

    //
    //  Horizontal offset for opening and closing tabs
    //
    private int                e_horzSpacingOffset        = 0;   //Offset applied when stretching/shrinking the ethics tab horizontally
    private int                e_vertSpacingOffset        = 0;   //"    " vertically
    private int                c_horzSpacingOffset        = 0;   //Offset applied when stretching/shrinking the cases tab horizontally
    private int                c_vertSpacingOffset        = 0;   //"    " vertically
    
    //
    //  Button sizes
    //
    private final int          BUTTON_WIDTH               = 30;
    private final int          BUTTON_HEIGHT              = 15;

    //
    //  Predetermined Anchors for buttons
    //
    private final int          ANCHOR_OFFSET_FACTOR       = 36;
    private final double       RESIZE_BUTTON_ANCHOR       = 270.0;
    private final double       HORZ_STRETCH_ANCHOR        = RESIZE_BUTTON_ANCHOR + 25;
    private final double       HORZ_SHRINK_ANCHOR         = HORZ_STRETCH_ANCHOR  + ANCHOR_OFFSET_FACTOR;
    private final double       VERT_STRETCH_ANCHOR        = HORZ_SHRINK_ANCHOR   + ANCHOR_OFFSET_FACTOR;
    private final double       VERT_SHRINK_ANCHOR         = VERT_STRETCH_ANCHOR  + ANCHOR_OFFSET_FACTOR;
    private final double       FONT_SIZE_ANCHOR           = VERT_SHRINK_ANCHOR   + ANCHOR_OFFSET_FACTOR;

    //
    //  Offsets for stretching/shrinking tabs
    //
    private final int          DEFAULT_SPACE_OFFSET       = 50;              //Default factor of stretch/shrinking
    private final int          HORZ_TAB_OFFSET            = PANE_WIDTH + 12; //Space applied when only one tab is open
    private final int          DEFAULT_TAB_OFFSET         = 0;               //Default position of tab if no others are present 
 
    //
    //  Font sizes for TextAreas (measured in ems) (conversion factor is 16px to 1 em)
    //
    private int                e_fontSize                 = 1;
    private int                c_fontSize                 = 1;
    private final          ObservableList<String> options = FXCollections.observableArrayList(
                           "4", "6", "8", "10", "12", "13", "14", "15", "16", "17", "18", "20", 
                           "22", "24", "26", "28", "32", "36", "40", "44", "48", "54", "60",
                           "66", "72", "80" );
   
    //
    //  Background colors for search box options
    //
    private final Background   unselectedBackground       =  new Background( 
                                                             new BackgroundFill( 
                                                             new Color( 0.0, 0.0, 0.0, 0.0 ), 
                                                             CornerRadii.EMPTY, Insets.EMPTY ) );
    private final Background   hoverBackground            =  new Background(
                                                             new BackgroundFill (
                                                             new Color( 0.0, 0.0, 0.0, 0.15 ),
                                                             CornerRadii.EMPTY, Insets.EMPTY ) );
    private final Background   selectedBackground         =  new Background(
                                                             new BackgroundFill (
                                                             new Color( 0.0, 0.0, 0.0, 0.30 ),
                                                             CornerRadii.EMPTY, Insets.EMPTY ) );    
    //
    //  Search options
    //
    private final SelectableLabel[] searchParameters      = new SelectableLabel[ 3 ];
    private final Button[]          searchButtonSelectors = new Button[ searchParameters.length ];       

    //////////////////////// INSTANCE VARIABLES /////////////////////////////
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize( URL url, ResourceBundle rb )
    {
        if ( MainApp.DEBUG )
        {
            this.windowTitle.setText( "SWED - AI Ethic Debater DEBUG MODE" );
        }
        Logger.getLogger( TitleAndMenuBarController.class.getName() ).log( Level.OFF, null, "Starting title and menu bar..." );
        this.configureMenus();
    }

    public void setParentController( RootPaneController control )
    {
        this.parentControl = control;
    }
    
    @FXML
    private void loadHelpPDF()
    {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle( "Open Help PDF" );
        
        fileChooser.getExtensionFilters().add( new FileChooser.ExtensionFilter( "Portable Document Format"
                                                                                , "*.pdf" ) );
        fileChooser.setInitialDirectory( new File( System.getProperty( "user.dir" ) ) );
        
        final File filePath = fileChooser.showOpenDialog( menuBar.getScene().getWindow() );    
        
        if ( filePath != null )
        {
            SwingUtilities.invokeLater( () -> 
            {
                try 
                {
                    Desktop.getDesktop().open( filePath.getAbsoluteFile() );
                } 
                catch ( IOException e )
                {
                    e.printStackTrace();
                }
            });
        }
    }
    
    private void printChildren( ArgumentNode node )
    {
        ListIterator<ArgumentNode> children = node.getChildren().listIterator();
        
        while( children.hasNext() )
        {
            printChildren( children.next() );
        }
    }

    /**
     * Closes the program. If the user has a tree open, a dialog box 
     * prompting the user if they're sure they want to close without saving
     * appears.
     */
    @FXML
    protected void closeProgram()
    {
        ConstructionAreaController cac = this.parentControl.getConstructionAreaController();
        
        //
        // If they haven't saved or there's trees present then we need to 
        // display the dialog box.
        //
        if ( !( cac.getArgumentTrees().isEmpty() || cac.getArgTreeMap().isEmpty() ) && !saved )
        {
            //
            // Initialize stage 
            //
            final Stage dialog = new Stage();
            dialog.setTitle( "Exit Without Saving" );
            dialog.getIcons().add( MainApp.icon );
            dialog.initModality( Modality.NONE );
            dialog.setOnCloseRequest( evt -> evt.consume() );
            dialog.resizableProperty().setValue( false );
            dialog.initOwner( this.menuBar.getScene().getWindow() );

            VBox globalBox   = new VBox();      //Global vbox
            HBox instBox     = new HBox();      //Horizontal box with instruction buttons
            instBox.setAlignment( Pos.CENTER );
            
            // Display the msg
            Label label      = new Label( "Are you sure you want to exit without saving?" );
                label.setWrapText( true );
                label.setFont( Font.font( 18 ) );
                
            // Horizontal box of instruction buttons
            HBox yesNoHBox      = new HBox();
                yesNoHBox.setAlignment( Pos.BOTTOM_CENTER );
            Button noSaveButton = new Button( "DON'T SAVE" );
                noSaveButton.setPrefSize( 125, 25 );
            Button saveButton   = new Button( "SAVE" );
                saveButton.setPrefSize( 125, 25 );
            Button cancel       = new Button( "CANCEL" );
                cancel.setPrefSize( 125, 25 );
                
            // Adds everything to boxes and then creates the scene
            instBox.getChildren().add( label );    
            yesNoHBox.getChildren().addAll( saveButton, noSaveButton, cancel );
            globalBox.getChildren().addAll( instBox, yesNoHBox );
            
            dialog.setScene( new Scene( globalBox, 400, 50 ) );
                
            ///////////////////////////////////////
            //                                   //
            //   If the user presses DONT SAVE   //
            //                                   //
            ///////////////////////////////////////
            noSaveButton.setOnMouseClicked( ( MouseEvent _event ) ->
            {
                Stage primaryStage = ( Stage ) menuBar.getScene().getWindow();
                primaryStage.close();
                dialog.close();
            });
            
            ///////////////////////////////////////
            //                                   //
            //      If the user presses SAVE     //
            //                                   //
            ///////////////////////////////////////
            saveButton.setOnMouseClicked( ( MouseEvent _event ) ->
            {
                dialog.close();
                
                try 
                {
                    this.saveArgumentScheme( new ActionEvent() );
                } 
                catch ( FileNotFoundException | UnsupportedEncodingException ex ) 
                {
                    Logger.getLogger( TitleAndMenuBarController.class.getName() ).log( Level.SEVERE, null, ex );
                }
                
                Stage primaryStage = ( Stage ) menuBar.getScene().getWindow();
                primaryStage.close();
            });
            
            ///////////////////////////////////////
            //                                   //
            //    If the user presses CANCEL     //
            //                                   //
            ///////////////////////////////////////
            cancel.setOnMouseClicked( ( MouseEvent _event ) -> 
            {
                dialog.close();
            } );
            
            dialog.showAndWait();                
        } 
        else
        {
                        
            Stage primaryStage = ( Stage ) menuBar.getScene().getWindow();
            primaryStage.close();
        }
    }

    /**
     * Removes the last-added object from the pane.
     *
     * @TODO Allow for multi-model objects to be removed with one click
     *
     * @param event
     */
    @FXML
    protected void undoAction( ActionEvent event )
    {
        ConstructionAreaController cac = this.parentControl.getConstructionAreaController();
        Pane mainPane = cac.getMainPane();

        if ( !mainPane.getChildren().isEmpty() )
        {
            mainPane.getChildren().remove( mainPane.getChildren().size() - 1 );
        }
    }

    /**
     * Clears all currently-existing evidence pieces and arguments from the
     * construction area.
     */
    @FXML
    protected void clearDiagram()
    {
        ConstructionAreaController cac = this.parentControl.getConstructionAreaController();
        if ( cac.getMainPane().getChildren().isEmpty() )
        {
            return;
        }
        
        cac.getMainPane().getChildren().clear();
        cac.getArgTreeMap().clear();
        cac.getArgumentTrees().clear();
        cac.constructionAreaSizeCheck();
        
        this.tutorialActive = this.casesActive = this.ethicsActive = false;
    }

    /**
     * Switches between full-screen and windowed mode.
     */
    @FXML
    protected void toggleFullScreen()
    {
        Stage stage     = ( Stage ) this.menuBar.getScene().getWindow();

        boolean isFullB = ! stage.isFullScreen();

        String isFullS  = isFullB ? "Disable Full Screen" 
                                  : "Enable Full Screen";

        stage.setFullScreen( isFullB );
        if ( !stage.isFullScreen() )
        {
            stage.setMaximized( !isFullB );
        }

        //The fourth item is enabling and disabling full screen.
        this.loadMenu.getItems().get( 4 ).setText( isFullS );
    }

    /**
     * @TODO
     */
    @FXML
    private void feedback()
    {
        Logger.getLogger( TitleAndMenuBarController.class.getName() ).log( Level.SEVERE, null, "Cannot provide feedback at this time!" );
    }

    /**
     * @TODO
     */
    @FXML
    private void summary()
    {
        Logger.getLogger( TitleAndMenuBarController.class.getName() ).log( Level.SEVERE, null, "Cannot provide feedback at this time!" );
    }

    /**
     * Displays a tab providing buttons which lead to tutorials for the user to
     * follow in case they are confused on some functionality of SWED.
     *
     * @TODO Replace with actual tutorial as opposed to copy-and-paste from
     * documentation
     */
    @FXML
    protected void showTutorial()
    {
        if ( this.tutorialActive ) 
        {
            return;
        }
        
        //  Attempts to write the diagram as a .sch file
        FileChooser chooser = new FileChooser();
        chooser.setTitle( "Open Video Tutorial" );
        chooser.getExtensionFilters().add( new FileChooser.ExtensionFilter( "Video Formats"
                                                                          , "*.mp4", "*.avi"
                                                                          , "*.mov", "*.flv"
                                                                          , "*.wmv"       ) );
        chooser.setInitialDirectory( new File( System.getProperty( "user.dir" ) ) );

         // User-selected path
        final File filePath      = chooser.showOpenDialog( menuBar.getScene().getWindow() );    
        
            SwingUtilities.invokeLater( () -> 
            {
                try 
                {
                    Desktop.getDesktop().open( filePath.getAbsoluteFile() );
                } 
                catch ( IOException e )
                {
                    e.printStackTrace();
                }
            });
    }

    /**
     * Opens the ethics pane with a textarea consisting of the text from the XML
     * file that the user selected.
     *
     * @param ethics
     * @throws java.io.IOException
     * @TODO: ???
     * 
     */
    protected void openEthics( EthicsModel ethics ) throws IOException
    {
        // AnchorPane will anchor the ethics tab
        // to the left, and the minimize
        // button to the right.
        this.ethicsAnchorPane   = new AnchorPane();
        this.ethicsTabPane      = new TabPane();
        this.ethicsButtons      = new Stack<>();
        this.ethicsTabPane.setPrefSize( PANE_WIDTH, PANE_HEIGHT );
        ContextMenu ethicsCM    = new ContextMenu();

        // Instantiates the ethics tab
        Tab ethicsTab           = new Tab( "Ethics" );
            ethicsTab.setClosable( false );
        
        boolean winOS                  = RootPaneController.OS.contains( "win" );
            
        // Menu Items
        final MenuItem colorPickerMenu;
        if ( MainApp.DEBUG )
        {
            colorPickerMenu            = new MenuItem( "Change Color"  );
        }
        final MenuItem copy            = new MenuItem( "Copy (CTRL+C)" );
        final MenuItem paste           = new MenuItem( "Paste (CTRL+V" );
        
        // Buttons                                                                                          //Index 0 is tabpane obj.
        final Button   exitButton      = new Button( new String( "\u274c".getBytes( "UTF-8" ), "UTF-8" ) ); //Index 1
        final Button   minimizeButton  = new Button( "-"                                                 ); //Index 2
        final Button   searchButton    = new Button( "Search"                                            ); //Index 3
        final Button   searchNext      = new Button( new String( "\u27fc".getBytes( "UTF-8" ), "UTF-8" ) ); //Index 4
        final Button   searchPrev      = new Button( new String( "\u27fb".getBytes( "UTF-8" ), "UTF-8" ) ); //Index 5
        final Button   resizeTab       = new Button( new String( "\u21ba".getBytes( "UTF-8" ), "UTF-8" ) ); //Index 6
        final Button   h_shrink        = new Button( new String( "\u2b9c".getBytes( "UTF-8" ), "UTF-8" ) ); //Index 7
        final Button   h_stretch       = new Button( new String( "\u2b9e".getBytes( "UTF-8" ), "UTF-8" ) ); //Index 8
        final Button   v_shrink        = new Button( new String( "\u2b9d".getBytes( "UTF-8" ), "UTF-8" ) ); //Index 9
        final Button   v_stretch       = new Button( new String( "\u2b9f".getBytes( "UTF-8" ), "UTF-8" ) ); //Index 10
        final ComboBox fontSizesBox    = new ComboBox ( options );                                          //Index 11
            fontSizesBox.setPromptText( "Font" );
            fontSizesBox.getStyleClass().add( ".combo-box" );        
        
        //
        // If we're not on Windows, the reset size button is disabled
        // temporary due to window pane conflictions.
        //
        resizeTab.setVisible( winOS );
        resizeTab.setDisable( !winOS );

        // Adds hover-over texts (tooltips)
        searchButton.setTooltip ( new Tooltip( "Click Here To Search For Words" ) );
        resizeTab.setTooltip    ( new Tooltip( "Reset Tab Size"                 ) );
        h_shrink.setTooltip     ( new Tooltip( "Shrink Window Horizontally"     ) );
        h_stretch.setTooltip    ( new Tooltip( "Stretch Window Horizontally"    ) );
        v_shrink.setTooltip     ( new Tooltip( "Shrink Window Vertically"       ) );
        v_stretch.setTooltip    ( new Tooltip( "Stretch Window Vertically"      ) );
        searchNext.setTooltip   ( new Tooltip( "View Next Word"                 ) );
        searchPrev.setTooltip   ( new Tooltip( "View Previous Word"             ) );

        // Defines the button sizes
        this.configureButtonSizes( exitButton, minimizeButton, h_shrink, h_stretch, v_shrink, 
                                   v_stretch, searchNext, searchPrev, searchButton );
        
        // Defines the placement of the buttons
        this.configureButtonAnchors( exitButton, minimizeButton, h_shrink, h_stretch, v_shrink, 
                                     v_stretch, searchNext, searchPrev, searchButton, fontSizesBox, 
                                     resizeTab );

        // If either the cases OR tutorial tab is already up, we need to horizontally
        // shift the ethics tab
        if ( this.casesActive ^ this.tutorialActive )
        {
            if ( this.casesActive )
            {
                this.ethicsAnchorPane.setLayoutX( HORZ_TAB_OFFSET + this.c_horzSpacingOffset );
            } 
            else
            {
                //Right now, the tutorial pane doesn't have a resizable tab pane
                this.ethicsAnchorPane.setLayoutX( HORZ_TAB_OFFSET );
            }
        } 
        
        // If they're both up, we need to shift it to the far right
        else if ( this.casesActive & this.tutorialActive )
        {
            this.ethicsAnchorPane.setLayoutX( HORZ_TAB_OFFSET << 1 );
        } 
        else
        {
            this.ethicsAnchorPane.setLayoutX( DEFAULT_TAB_OFFSET );
        }

        // Grabs the text from the XML file
        final TextArea ethicsTextArea = new TextArea( ethics.toString() );
        ethicsTextArea.setStyle( "-fx-font-size: 1.5em; -fx-control-inner-background: #ccffcc; -fx-text-fill: black; -fx-background-color: #ccffcc;" );
        ethicsTextArea.setEditable( false );
        ethicsTextArea.setWrapText( true );
        
        if ( MainApp.DEBUG )
        {
            ethicsCM.getItems().add( colorPickerMenu );
        }
        
        ethicsCM.getItems().addAll( copy, paste );
        ethicsTextArea.setContextMenu( ethicsCM );            


        ethicsTab.setContent( ethicsTextArea );
        
        this.configureDragListener( this.ethicsTabPane, this.ethicsAnchorPane, ethicsTextArea );   
        this.configureCopyEvent( ethicsTextArea );
        this.configurePasteEvent( ethicsTextArea );

        // Adds the tab to the pane
        this.ethicsTabPane.getTabs().add( ethicsTab );

        //Adds the tab pane and the minimize button 
        this.ethicsAnchorPane.getChildren().addAll( ethicsTabPane, minimizeButton, exitButton,
                                                    searchButton, searchNext,searchPrev, 
                                                    h_shrink, h_stretch, v_shrink,
                                                    v_stretch, fontSizesBox, resizeTab );
        
        // Grabs the top pane with the other tabs
        ConstructionAreaController cac  = this.parentControl.getConstructionAreaController();
        Pane mainPane                   = cac.getMainPane();

        // Attaches the ethics pane to the main pane
        mainPane.getChildren().add( this.ethicsAnchorPane );
        this.ethicsActive   = true;
        this.e_fontSize     = 1; //Reset the font size in case they close and reopen the tab

        /********************************************************/
        /**             ETHICS TAB EVENT HANDLERS              **/
        /********************************************************/
        
        ///////////////////////////////////////
        //      ON TAB CLOSE EVENT           //
        ///////////////////////////////////////
        exitButton.setOnAction( ( ActionEvent event ) ->
        {
            this.ethicsAnchorPane.getChildren().clear();
            this.ethicsActive = false;
            this.e_horzSpacingOffset = 0;
            this.e_vertSpacingOffset = 0;
            mainPane.getChildren().remove( this.ethicsAnchorPane );
        } );

        ///////////////////////////////////////
        //      ON MINIMIZE BOX              //
        ///////////////////////////////////////
        minimizeButton.setOnAction( ( ActionEvent event ) ->
        {
            ethicsMinimized =  ! ethicsMinimized;

            //If it's minimized, we need to shrink the box,
            //and remove all the active elements/controls
            if ( ethicsMinimized )
            {
                //Removes in decreasing order
                for( int i = START_STACK_INDEX; i >= END_STACK_INDEX; i-- )
                {
                    Node nextNode = this.ethicsAnchorPane.getChildren().remove( i );
                    if( nextNode instanceof Button )
                    {
                        this.ethicsButtons.push( ( Button ) nextNode );
                    } else if( nextNode instanceof ComboBox )
                    {
                        this.ethicsButtons.push( ( ComboBox ) nextNode );
                    }
                }
                ethicsTabPane.setPrefSize( 0, 0 );
                minimizeButton.setText( "+ " + ethics.getTitle() );
                minimizeButton.setPrefWidth( BUTTON_WIDTH << 4 );
            } 
            else
            {
                ethicsTabPane.setPrefSize( PANE_WIDTH + this.e_horzSpacingOffset, PANE_HEIGHT + this.e_vertSpacingOffset );
                minimizeButton.setText( "-" );
                
                //Adds in increasing order
                while( !this.ethicsButtons.isEmpty() )
                {
                    Node nextNode = this.ethicsButtons.pop();
                    
                    if( nextNode instanceof Button )
                    {
                        this.ethicsAnchorPane.getChildren().add( ( Button ) nextNode );
                    } 
                    else if( nextNode instanceof ComboBox )
                    {
                        this.ethicsAnchorPane.getChildren().add( ( ComboBox ) nextNode );
                    } 
                    minimizeButton.setPrefWidth( BUTTON_WIDTH + 5 );
                }
            }
        } );
        
        ///////////////////////////////////////
        //      ON CHANGE FONT SIZE          //
        ///////////////////////////////////////
        fontSizesBox.valueProperty().addListener( new ChangeListener<String>() 
        {
            @Override 
            public void changed( ObservableValue ov, String t, String fontChoice ) 
            {                
                e_fontSize = Integer.parseInt( fontChoice ); 
                ethicsTextArea.setStyle( "-fx-font-size: " + ( e_fontSize / 16.0 ) + "em; "
                                       + "-fx-control-inner-background: #ccffcc; fx-text-fill: black; "
                                       + "-fx-background-color: #ccffcc;" );
            }    
        });

        //////////////////////////////////////////////////
        //      ON HORIZONTAL DECREASE WINDOW SIZE      //
        //////////////////////////////////////////////////
        h_shrink.setOnAction( ( ActionEvent e ) ->
        {
            if ( this.ethicsTabPane.getPrefWidth() == PANE_WIDTH )
            {
                h_shrink.setDisable( true );
                return;
            }

            //We need to keep track of what the current offset is in case 
            //one of the other tabs are suddenly opened by the user.
            this.e_horzSpacingOffset -= DEFAULT_SPACE_OFFSET;
            
            h_stretch.setDisable( false );
            
            this.applyAnchorTransformation( h_shrink, h_stretch, v_shrink, 
                                            v_stretch, resizeTab, fontSizesBox, 0 );

            //Since we're applying a negative offset to the current spacing offset,
            //we need to ADD since it's technically smaller than before.
            this.ethicsTabPane.setPrefWidth( PANE_WIDTH + this.e_horzSpacingOffset );

            if ( this.tutorialActive && this.tutorialAnchorPane.getLayoutX() != DEFAULT_TAB_OFFSET 
                                     && this.ethicsAnchorPane.getLayoutX() < HORZ_TAB_OFFSET << 1 )
            {
                this.tutorialAnchorPane.setLayoutX( this.tutorialAnchorPane.getLayoutX() - DEFAULT_SPACE_OFFSET );
            }

            if ( this.casesActive && this.casesAnchorPane.getLayoutX() != DEFAULT_TAB_OFFSET 
                                  && this.ethicsAnchorPane.getLayoutX() < HORZ_TAB_OFFSET << 1 )
            {
                this.casesAnchorPane.setLayoutX( this.casesAnchorPane.getLayoutX() - DEFAULT_SPACE_OFFSET );
            }

        } );

        //////////////////////////////////////////////////
        //      ON HORIZONTAL INCREASE WINDOW SIZE      //
        //////////////////////////////////////////////////
        h_stretch.setOnAction( ( ActionEvent e ) ->
        {
            if( this.ethicsTabPane.getPrefWidth() >= MAX_PANE_WIDTH )
            {
                h_stretch.setDisable( true );
            }
            
            this.e_horzSpacingOffset += DEFAULT_SPACE_OFFSET;
            h_shrink.setDisable( false );

            //Anchors HORIZONTAL shrink/stretch buttons
            AnchorPane.setRightAnchor( h_shrink,  HORZ_SHRINK_ANCHOR   + this.e_horzSpacingOffset );
            AnchorPane.setRightAnchor( h_stretch, HORZ_STRETCH_ANCHOR  + this.e_horzSpacingOffset );

            //Anchors VERTICAL shrink/stretch buttons
            AnchorPane.setRightAnchor( v_shrink,  VERT_SHRINK_ANCHOR   + this.e_horzSpacingOffset);
            AnchorPane.setRightAnchor( v_stretch, VERT_STRETCH_ANCHOR  + this.e_horzSpacingOffset);

            //Anchors font change combo box
            AnchorPane.setRightAnchor( fontSizesBox, FONT_SIZE_ANCHOR  + this.e_horzSpacingOffset );
            
            //Anchors resize button
            AnchorPane.setRightAnchor( resizeTab, RESIZE_BUTTON_ANCHOR + this.e_horzSpacingOffset );
            
            //Remember, we're manipulating SIZE here
            this.ethicsTabPane.setPrefWidth( PANE_WIDTH + this.e_horzSpacingOffset );

            //Here, we manipulate POSITION due to the RESIZING of the ethics tab
            if ( this.tutorialActive && this.tutorialAnchorPane.getLayoutX() != DEFAULT_TAB_OFFSET 
                                     && this.ethicsAnchorPane.getLayoutX() < HORZ_TAB_OFFSET << 1 )
            {
                this.tutorialAnchorPane.setLayoutX( this.tutorialAnchorPane.getLayoutX() + DEFAULT_SPACE_OFFSET );
            }

            if ( this.casesActive && this.casesAnchorPane.getLayoutX() != DEFAULT_TAB_OFFSET 
                                  && this.ethicsAnchorPane.getLayoutX() < HORZ_TAB_OFFSET << 1 )
            {
                this.casesAnchorPane.setLayoutX( this.casesAnchorPane.getLayoutX() + DEFAULT_SPACE_OFFSET );
            }
        } );

        //////////////////////////////////////////////////
        //      ON VERTICAL DECREASE WINDOW SIZE        //
        //////////////////////////////////////////////////
        v_shrink.setOnAction( ( ActionEvent e ) ->
        {
            if ( this.ethicsTabPane.getPrefHeight() == PANE_HEIGHT )
            {
                v_shrink.setDisable( true );
            } 
            else
            {
                v_stretch.setDisable( false );
                this.e_vertSpacingOffset -= DEFAULT_SPACE_OFFSET;
                this.ethicsTabPane.setPrefHeight( PANE_HEIGHT + this.e_vertSpacingOffset );
            }
        } );

        //////////////////////////////////////////////////
        //      ON VERTICAL INCREASE WINDOW SIZE        //
        //////////////////////////////////////////////////
        v_stretch.setOnAction( ( ActionEvent event ) ->
        {
            if( this.ethicsTabPane.getPrefHeight() >= MAX_PANE_HEIGHT )
            {
                v_stretch.setDisable( true );
            } 
            else 
            {
                v_shrink.setDisable( false );
                this.e_vertSpacingOffset += DEFAULT_SPACE_OFFSET;
                this.ethicsTabPane.setPrefHeight( PANE_HEIGHT + this.e_vertSpacingOffset );
            }
        } );
        
        ///////////////////////////////////////
        //      ON RESET SIZE BUTTON         //
        ///////////////////////////////////////
        resizeTab.setOnAction( ( ActionEvent event ) -> 
        {
           this.ethicsTabPane.setPrefSize( PANE_WIDTH, PANE_HEIGHT );
           this.resetAnchors( h_shrink, h_stretch, v_shrink, v_stretch, resizeTab, fontSizesBox, 0 );
           
           // If one of the other two tabs are open when we reset, they need to shift
           // back to their original positions.
           if ( this.casesActive ^ this.tutorialActive )
            {
                AnchorPane smallerAP = null;
                AnchorPane largerAP  = null;
                
                if ( this.casesActive )
                {
                    smallerAP = this.ethicsAnchorPane.getLayoutX() < this.casesAnchorPane.getLayoutX()
                        ? this.ethicsAnchorPane : this.casesAnchorPane;
                    largerAP  = this.ethicsAnchorPane.getLayoutX() > this.casesAnchorPane.getLayoutX() 
                        ? this.ethicsAnchorPane : this.casesAnchorPane;
                } 
                else
                {
                    smallerAP = this.ethicsAnchorPane.getLayoutX() < this.tutorialAnchorPane.getLayoutX()
                        ? this.ethicsAnchorPane : this.tutorialAnchorPane;
                    largerAP  = this.ethicsAnchorPane.getLayoutX() > this.tutorialAnchorPane.getLayoutX() 
                        ? this.ethicsAnchorPane : this.tutorialAnchorPane;                    
                }
                if ( smallerAP == this.ethicsAnchorPane)
                {
                    largerAP.setLayoutX( HORZ_TAB_OFFSET );
                }
                else
                {
                    largerAP.setLayoutX( HORZ_TAB_OFFSET + this.c_horzSpacingOffset );
                }
            } 
           // If they're both open, then the smaller gets put at the default
           // horizontal offset, and the larger is placed at the horizontal offset * 2 
           // in addition to whatever the offset of the smaller tab is.
            else if ( this.casesActive & this.tutorialActive )
            {
                //Grabs a reference to the smaller of the two
                AnchorPane smallerAP = this.casesAnchorPane.getLayoutX() < this.tutorialAnchorPane.getLayoutX()
                        ? this.casesAnchorPane : this.tutorialAnchorPane;
                AnchorPane largerAP = this.casesAnchorPane.getLayoutX() > this.tutorialAnchorPane.getLayoutX()
                        ? this.casesAnchorPane : this.tutorialAnchorPane;

                int smallerOffset = ( int ) smallerAP.getLayoutX();
                int largerOffset  = ( int ) largerAP.getLayoutX();

                //First case (if ethics is in first pos)
                if ( smallerOffset != DEFAULT_TAB_OFFSET && largerOffset > HORZ_TAB_OFFSET )
                {
                    smallerAP.setLayoutX( HORZ_TAB_OFFSET );
                    largerAP.setLayoutX( ( HORZ_TAB_OFFSET << 1 ) + c_horzSpacingOffset );
                }
                //Second case (if ethics tab is in the middle)
                else if( smallerOffset == DEFAULT_TAB_OFFSET && largerOffset >= ( HORZ_TAB_OFFSET << 1 ) )
                {
                    largerAP.setLayoutX( ( HORZ_TAB_OFFSET << 1 ) + c_horzSpacingOffset );    
                }                
                //Third case (if ethics tab is last)
                else if ( smallerOffset == DEFAULT_TAB_OFFSET && largerOffset >= HORZ_TAB_OFFSET )
                {
                    largerAP.setLayoutX( ( HORZ_TAB_OFFSET ) + c_horzSpacingOffset );
                } 
                
            }
        });        
        
        //////////////////////////////////////////////////
        //      ON SEARCH BUTTON TRIGGER                //
        //////////////////////////////////////////////////
        
        //
        //  Configures the mouse-over events
        //
        searchButton.addEventFilter( MouseEvent.MOUSE_ENTERED, ( MouseEvent event ) ->
        {
            searchButton.setText( "Search" );
        });
        
        searchButton.addEventFilter( MouseEvent.MOUSE_EXITED, ( MouseEvent event ) ->
        {
            searchButton.setText( this.e_stringIndexes != null ? ( this.e_searchPosition + 1 ) + "/" + this.e_stringIndexes.size() 
                                                                       : "Search" );
        });
        
        //
        //  Configures the on-press (actual search) event
        //
        searchButton.setOnAction( ( ActionEvent event ) ->
        {
            TextField searchField = configureSearchBox( "Ethics Search", this );
            
            ////////////////////////////
            //                        //
            //                        //
            //  REGEX parsing begins  //
            //                        //
            //                        //
            ////////////////////////////
            String searchString;
            this.e_searchPosition = -1;

            if ( ! searchField.getText().isEmpty() )
            {
                searchString = searchField.getText();
                
                Pattern pattern      = this.getRegexPattern( searchString );
                Matcher matcher      = pattern.matcher( ethics.toString() ); //Where input is a TextInput class
                boolean searching    = matcher.find( 0 );

                this.e_stringIndexes = new ArrayList<>();
                searchNext.setDisable( true );
                searchPrev.setDisable( true );

                //Finds all starting and ending positions of each word in the text
                while ( searching )
                {
                    this.e_stringIndexes.add( new Pair( matcher.start(), matcher.end() ) );

                    searching = matcher.find( matcher.end() );
                }

                if ( this.e_stringIndexes.isEmpty() )
                {
                    this.configureNotFoundStage( ( Stage ) mainPane.getScene().getWindow(), searchString );
                } 
                else
                {
                    Pair nextPair = e_stringIndexes.get(  ++ e_searchPosition );
                    ethicsTextArea.selectRange( ( int ) nextPair.getKey(), ( int ) nextPair.getValue() ); // By default, selects the first pair
                    searchButton.setText( ( e_searchPosition + 1 ) + "/" + this.e_stringIndexes.size() );
                    if ( this.e_stringIndexes.size() != e_searchPosition + 1 )
                    {
                        this.ethicsAnchorPane.getChildren().get( SEARCH_NEXT_INDEX ).setDisable( false ); //Enables the 'next' button
                    }
                }
            } 
            else
            {
                searchButton.setText( "Search" );
            }
        } );

        ///////////////////////////////////////////////
        //           ON SELECT NEXT WORD EVENT       //
        ///////////////////////////////////////////////
        searchNext.setOnAction( ( ActionEvent event ) ->
        {
            if ( e_searchPosition >= this.e_stringIndexes.size() - 1)
            {
                searchNext.setDisable( true );
            } 
            else
            {
                Pair nextPair = this.e_stringIndexes.get(  ++ e_searchPosition  );

                ethicsTextArea.selectRange( ( int ) nextPair.getKey(), ( int ) nextPair.getValue() );

                searchButton.setText( ( e_searchPosition + 1 )+ "/" + this.e_stringIndexes.size() );
                
                //If we are not at either end of the linkedlist,
                //we can enable both buttons.
                if ( searchPrev.isDisabled() )
                {
                    searchPrev.setDisable( false );
                }
            }
        } );

        ///////////////////////////////////////////////
        //           ON SELECT PREV WORD EVENT       //
        ///////////////////////////////////////////////
        //
        //  Essentially the reverse of the above procedure
        searchPrev.setOnAction( ( ActionEvent event ) ->
        {
            if ( e_searchPosition <= 0 )
            {
                searchPrev.setDisable( true );
            } 
            else
            {
                Pair nextPair = this.e_stringIndexes.get(  -- e_searchPosition );

                ethicsTextArea.selectRange( ( int ) nextPair.getKey(), ( int ) nextPair.getValue() );

                searchButton.setText( ( e_searchPosition + 1 )+ "/" + this.e_stringIndexes.size() );
                
                //If we are not at either end of the linkedlist,
                //we can enable both buttons.
                if ( searchNext.isDisabled() )
                {
                    searchNext.setDisable( false );
                }
            }
        } );
        
        ///////////////////////////////////////////////
        //          COPY TEXT EVENT                  //
        ///////////////////////////////////////////////
        copy.setOnAction( ( ActionEvent event ) -> 
        {
            configureCopyEvent( ethicsTextArea );  
        } );
        
        ///////////////////////////////////////////////
        //          PASTE TEXT EVENT                 //
        ///////////////////////////////////////////////
        paste.setOnAction( ( ActionEvent event ) ->
        {
            configurePasteEvent( ethicsTextArea );
        } );
        
    }

    
    /**
     * Opens the case pane for the user after they have selected a valid XML
     * document.
     *
     * @param casesModel
     * @throws java.io.UnsupportedEncodingException
     */
    protected void openCaseTab( CaseModel casesModel ) throws UnsupportedEncodingException
    {
        //  AnchorPane will anchor the ethics tab
        //  to the left, and the minimize
        //  button to the right.
        this.casesAnchorPane = new AnchorPane();
        this.casesTabPane    = new TabPane();
        this.casesButtons    = new Stack<>();
        this.casesTabPane.setPrefSize( PANE_WIDTH, PANE_HEIGHT );
        ContextMenu casesCM  = new ContextMenu();
        
        Tab casesTab = new Tab( "Cases" );
            casesTab.setClosable( false );

        //
        //  If we are not on windows, we need to temporary disable the
        //  reset button, as it causes glitches.
        //
        boolean winOS               = RootPaneController.OS.contains( "win" );

        // MenuItems
        final MenuItem colorPickerMenu;
        if ( MainApp.DEBUG )
        {
            colorPickerMenu         = new MenuItem( "Change Color"   );
        }
        
        final MenuItem copy         = new MenuItem( "Copy (CTRL+C) " );
        final MenuItem paste        = new MenuItem( "Paste (CTRL+V)" );        
        
        // Buttons                                                                                     //Index 0 is tabpane obj.
        final Button exitButton     = new Button( new String( "\u274c".getBytes("UTF-8"), "UTF-8" ) ); //Index 1
        final Button minimizeButton = new Button( "-"                                               ); //Index 2
        final Button searchButton   = new Button( "Search"                                          ); //Index 3
        final Button searchNext     = new Button( new String( "\u27fc".getBytes("UTF-8"), "UTF-8" ) ); //Index 4
        final Button searchPrev     = new Button( new String( "\u27fb".getBytes("UTF-8"), "UTF-8" ) ); //Index 5
        final Button resizeTab      = new Button( new String( "\u21ba".getBytes("UTF-8"), "UTF-8" ) ); //Index 6
        final Button h_shrink       = new Button( new String( "\u2b9c".getBytes("UTF-8"), "UTF-8" ) ); //Index 7
        final Button h_stretch      = new Button( new String( "\u2b9e".getBytes("UTF-8"), "UTF-8" ) ); //Index 8
        final Button v_shrink       = new Button( new String( "\u2b9d".getBytes("UTF-8"), "UTF-8" ) ); //Index 9
        final Button v_stretch      = new Button( new String( "\u2b9f".getBytes("UTF-8"), "UTF-8" ) ); //Index 10
        final ComboBox fontSizesBox = new ComboBox ( options );                                        //Index 11
            fontSizesBox.setPromptText( "Font" );
            fontSizesBox.getStyleClass().add( ".combo-box" );        
        
        //
        //  If we're not on Windows, the reset size button is disabled
        //  temporary due to window pane conflictions.
        //
        resizeTab.setVisible( winOS );
        resizeTab.setDisable( !winOS );
        
        //  Adds hover-over texts (tooltips)
        searchButton.setTooltip ( new Tooltip( "Click Here To Search For Words" ) );
        resizeTab.setTooltip    ( new Tooltip( "Reset Tab Size"                 ) );
        h_shrink.setTooltip     ( new Tooltip( "Shrink Window Horizontally"     ) );
        h_stretch.setTooltip    ( new Tooltip( "Stretch Window Horizontally"    ) );
        v_shrink.setTooltip     ( new Tooltip( "Shrink Window Vertically"       ) );
        v_stretch.setTooltip    ( new Tooltip( "Stretch Window Vertically"      ) );

        this.configureButtonSizes( exitButton, minimizeButton, h_shrink, h_stretch, v_shrink, 
                                   v_stretch, searchNext, searchPrev, searchButton );
        this.configureButtonAnchors( exitButton, minimizeButton, h_shrink, h_stretch, v_shrink, 
                                     v_stretch, searchNext, searchPrev, searchButton, fontSizesBox, 
                                     resizeTab );
        //
        //  If either the ethics or tutorial panes are active, we need to shift
        //  the pane by the horizontal factors
        //
        if ( this.ethicsActive ^ this.tutorialActive )
        {
            if ( this.ethicsActive )
            {
                this.casesAnchorPane.setLayoutX( HORZ_TAB_OFFSET + e_horzSpacingOffset );
            } 
            else
            {
                this.casesAnchorPane.setLayoutX( HORZ_TAB_OFFSET );
            }
        } 
        else if ( this.ethicsActive & this.tutorialActive )
        {
            this.casesAnchorPane.setLayoutX( ( HORZ_TAB_OFFSET << 1 ) + e_horzSpacingOffset );
        } 
        else
        {
            this.casesAnchorPane.setLayoutX( DEFAULT_TAB_OFFSET );
        }

        // Grabs the string text data from the loaded xml file
        TextArea casesTextArea = new TextArea( casesModel.toString() );
        casesTextArea.setStyle( "-fx-font-size: 1.5em; -fx-control-inner-background: #fffccc; -fx-text-fill: black;" );
        casesTextArea.setEditable( false );
        casesTextArea.setWrapText( true );        
        
        if ( MainApp.DEBUG )
        {
            casesCM.getItems().add( colorPickerMenu );
        }
        
        casesCM.getItems().addAll( copy, paste );
        
        casesTextArea.setContextMenu( casesCM );
        casesTab.setContent( casesTextArea );
        
        this.configureDragListener( this.casesTabPane, this.casesAnchorPane, casesTextArea );   
        this.configureCopyEvent( casesTextArea );
        this.configurePasteEvent( casesTextArea );

        // Adds the tab to the pane
        this.casesTabPane.getTabs().add( casesTab );

        // Adds the tab pane and the minimize button 
        this.casesAnchorPane.getChildren().addAll( this.casesTabPane, minimizeButton, exitButton, 
                                                   searchButton,      searchNext,     searchPrev, 
                                                   h_shrink,          h_stretch,      v_shrink, 
                                                   v_stretch,         fontSizesBox,   resizeTab );
        
                                                   
        //Grabs the top pane with the other tabs
        ConstructionAreaController cac  = this.parentControl.getConstructionAreaController();
        Pane mainPane                   = cac.getMainPane();

        //Attaches the ethics pane to the main pane
        mainPane.getChildren().add( this.casesAnchorPane );
        
        this.casesActive = true;
        this.c_fontSize  = 1;   //  In case the user closes the tab, we want to reset font size

        /********************************************************/
        /**               CASES TAB EVENT HANDLERS             **/
        /********************************************************/

        ///////////////////////////////////////
        //      ON TAB CLOSE EVENT           //
        ///////////////////////////////////////
        exitButton.setOnAction( ( ActionEvent event ) ->
        {
            this.casesAnchorPane.getChildren().clear();
            this.casesActive         = false;
            this.c_horzSpacingOffset = 0;
            this.c_vertSpacingOffset = 0;
            mainPane.getChildren().remove( this.casesAnchorPane );
            
        } );
        
        ///////////////////////////////////////
        //      ON CHANGE FONT SIZE          //
        ///////////////////////////////////////
        fontSizesBox.valueProperty().addListener( new ChangeListener<String>() {
            @Override 
            public void changed( ObservableValue ov, String t, String fontChoice ) {                
                c_fontSize = Integer.parseInt( fontChoice ); 
                casesTextArea.setStyle( "-fx-font-size: " + ( c_fontSize / 16.0 ) + "em; "
                                      + "-fx-control-inner-background: #fffccc; "
                                      + "-fx-text-fill: black; "
                                      + "-fx-background-color: #fffccc;" );
            }    
        } );

        //////////////////////////////////////////////////
        //      ON HORIZONTAL DECREASE WINDOW SIZE      //
        //////////////////////////////////////////////////
        h_shrink.setOnAction( ( ActionEvent e ) ->
        {
            if ( this.casesTabPane.getPrefWidth() == PANE_WIDTH )
            {
                h_shrink.setDisable( true );
                return;
            }
            h_stretch.setDisable( false );
            
            //We need to keep track of what the current offset is in case 
            //one of the other tabs are suddenly opened by the user.
            this.c_horzSpacingOffset -= DEFAULT_SPACE_OFFSET;
            
            //Anchors HORIZONTAL shrink/stretch buttons
            this.applyAnchorTransformation( h_shrink, h_stretch, v_shrink, 
                                            v_stretch, resizeTab, fontSizesBox, 1 );

            //Since we're applying a negative offset to the current spacing offset,
            //we need to ADD since it's technically smaller than before.
            this.casesTabPane.setPrefWidth( PANE_WIDTH + this.c_horzSpacingOffset );

            if ( this.tutorialActive && this.tutorialAnchorPane.getLayoutX() != DEFAULT_TAB_OFFSET 
                                     && this.casesAnchorPane.getLayoutX() < ( HORZ_TAB_OFFSET << 1 ) )
            {
                this.tutorialAnchorPane.setLayoutX( this.tutorialAnchorPane.getLayoutX() - DEFAULT_SPACE_OFFSET );
            }

            if ( this.ethicsActive && this.ethicsAnchorPane.getLayoutX() != DEFAULT_TAB_OFFSET 
                                   && this.casesAnchorPane.getLayoutX() < ( HORZ_TAB_OFFSET << 1 ) )
            {
                this.ethicsAnchorPane.setLayoutX( this.ethicsAnchorPane.getLayoutX() - DEFAULT_SPACE_OFFSET );
            }

        } );

        //////////////////////////////////////////////////
        //      ON HORIZONTAL INCREASE WINDOW SIZE      //
        //////////////////////////////////////////////////
        h_stretch.setOnAction( ( ActionEvent e ) ->
        {
            if( this.casesTabPane.getPrefWidth() >= MAX_PANE_WIDTH )
            {
               h_stretch.setDisable( true );
               return;
            }
            
            h_shrink.setDisable( false );
            
            this.c_horzSpacingOffset += DEFAULT_SPACE_OFFSET;

            //Anchors HORIZONTAL shrink/stretch buttons
            AnchorPane.setRightAnchor( h_shrink,  HORZ_SHRINK_ANCHOR  + this.c_horzSpacingOffset );
            AnchorPane.setRightAnchor( h_stretch, HORZ_STRETCH_ANCHOR + this.c_horzSpacingOffset );

            //Anchors VERTICAL shrink/stretch buttons
            AnchorPane.setRightAnchor( v_shrink,  VERT_SHRINK_ANCHOR  + this.c_horzSpacingOffset);
            AnchorPane.setRightAnchor( v_stretch, VERT_STRETCH_ANCHOR + this.c_horzSpacingOffset);

            //Anchors font up and down buttons
            AnchorPane.setRightAnchor( fontSizesBox, FONT_SIZE_ANCHOR + this.c_horzSpacingOffset );
            
            //Anchors resize button
            AnchorPane.setRightAnchor( resizeTab, RESIZE_BUTTON_ANCHOR + this.c_horzSpacingOffset );
            
            //Remember, we're manipulating SIZE here
            this.casesTabPane.setPrefWidth( PANE_WIDTH + this.c_horzSpacingOffset );

            //Here, we manipulate POSITION due to the RESIZING of the ethics tab
            if ( this.tutorialActive && this.tutorialAnchorPane.getLayoutX() != DEFAULT_TAB_OFFSET
                    && this.casesAnchorPane.getLayoutX() < HORZ_TAB_OFFSET << 1 )
            {
                this.tutorialAnchorPane.setLayoutX( this.tutorialAnchorPane.getLayoutX() + DEFAULT_SPACE_OFFSET );
            }

            if ( this.ethicsActive && this.ethicsAnchorPane.getLayoutX() != DEFAULT_TAB_OFFSET
                    && this.casesAnchorPane.getLayoutX() < HORZ_TAB_OFFSET << 1 )
            {
                this.ethicsAnchorPane.setLayoutX( this.ethicsAnchorPane.getLayoutX() + DEFAULT_SPACE_OFFSET );
            }
        } );

        //////////////////////////////////////////////////
        //      ON VERTICAL DECREASE WINDOW SIZE        //
        //////////////////////////////////////////////////
        v_shrink.setOnAction( ( ActionEvent e ) ->
        {
            if ( this.casesTabPane.getPrefHeight() == PANE_HEIGHT )
            {
                v_shrink.setDisable( true );
            } else
            {
                v_stretch.setDisable( false );
                this.c_vertSpacingOffset -= DEFAULT_SPACE_OFFSET;
                this.casesTabPane.setPrefHeight( PANE_HEIGHT + this.c_vertSpacingOffset );
            }
        } );

        //////////////////////////////////////////////////
        //      ON VERTICAL INCREASE WINDOW SIZE        //
        //////////////////////////////////////////////////
        v_stretch.setOnAction( ( ActionEvent event ) ->
        {
            if( this.casesTabPane.getPrefHeight() >= MAX_PANE_HEIGHT )
            {
                v_stretch.setDisable( true );
            } 
            else 
            {
                v_shrink.setDisable( false );
                this.c_vertSpacingOffset += DEFAULT_SPACE_OFFSET;
                this.casesTabPane.setPrefHeight( PANE_HEIGHT + this.c_vertSpacingOffset );
            }
        } );
        
        ///////////////////////////////////////
        //      ON RESET SIZE BUTTON         //
        ///////////////////////////////////////
        resizeTab.setOnAction( ( ActionEvent event ) -> 
        {
           this.casesTabPane.setPrefSize( PANE_WIDTH, PANE_HEIGHT );
           this.resetAnchors( h_shrink, h_stretch, v_shrink, v_stretch, resizeTab, fontSizesBox, 1 );
           
           // If one of the other two tabs are open when we reset, they need to shift
           // back to their original positions.
           if ( this.ethicsActive ^ this.tutorialActive )
            {
                AnchorPane smallerAP = null;
                AnchorPane largerAP  = null;
                
                //
                //  This handles most if not all the cases present when any
                //  of the three tabs are open, at any time. We have to be 
                //  sure to reset their positions [appropriately] when resetting 
                //  the size of one of the tabs.
                //
                if ( this.ethicsActive )
                {
                    smallerAP = this.casesAnchorPane.getLayoutX() < this.ethicsAnchorPane.getLayoutX()
                        ? this.casesAnchorPane : this.ethicsAnchorPane;
                    largerAP = this.casesAnchorPane.getLayoutX() > this.ethicsAnchorPane.getLayoutX()
                        ? this.casesAnchorPane : this.ethicsAnchorPane;
                } 
                else
                {
                    smallerAP = this.casesAnchorPane.getLayoutX() < this.tutorialAnchorPane.getLayoutX()
                              ? this.casesAnchorPane : this.tutorialAnchorPane;
                    largerAP  = this.casesAnchorPane.getLayoutX() > this.tutorialAnchorPane.getLayoutX()
                              ? this.casesAnchorPane : this.tutorialAnchorPane;
                }
                
                smallerAP.setLayoutX( DEFAULT_TAB_OFFSET );
                
                if (smallerAP == this.casesAnchorPane)
                {
                    largerAP.setLayoutX( HORZ_TAB_OFFSET );
                }
                else
                {
                    largerAP.setLayoutX( HORZ_TAB_OFFSET + this.e_horzSpacingOffset );
                }
            } 
           // If they're both open, then the smaller gets put at the default
           // horizontal offset, and the larger is placed at the horizontal offset * 2 
           // in addition to whatever the offset of the smaller tab is.
            else if ( this.ethicsActive & this.tutorialActive )
            {
                //Grabs a reference to the smaller of the two
                AnchorPane smallerAP = this.ethicsAnchorPane.getLayoutX() < this.tutorialAnchorPane.getLayoutX()
                                     ? this.ethicsAnchorPane : this.tutorialAnchorPane;
                AnchorPane largerAP  = this.ethicsAnchorPane.getLayoutX() > this.tutorialAnchorPane.getLayoutX()
                                     ? this.ethicsAnchorPane : this.tutorialAnchorPane;

                int smallerOffset = ( int ) smallerAP.getLayoutX();
                int largerOffset  = ( int ) largerAP.getLayoutX();

                //First case ( if cases tab is first)
                if ( smallerOffset != DEFAULT_TAB_OFFSET && largerOffset > HORZ_TAB_OFFSET )
                {
                    smallerAP.setLayoutX( HORZ_TAB_OFFSET );
                    largerAP.setLayoutX( ( HORZ_TAB_OFFSET << 1 ) + e_horzSpacingOffset );
                }
                //Second case (if cases tab is in the middle
                else if( smallerOffset == DEFAULT_TAB_OFFSET && largerOffset >= ( HORZ_TAB_OFFSET << 1 ) ) // (if cases tab is middle)
                {
                    largerAP.setLayoutX( ( HORZ_TAB_OFFSET << 1 ) + e_horzSpacingOffset );    
                }                
                //Third case (if cases tab is last)
                else if ( smallerOffset == DEFAULT_TAB_OFFSET && largerOffset >= HORZ_TAB_OFFSET )
                {
                    largerAP.setLayoutX( ( HORZ_TAB_OFFSET ) + e_horzSpacingOffset );
                } 

                
            }           
        } );

        ///////////////////////////////////////
        //          ON MINIMIZE BOX          //
        ///////////////////////////////////////
        minimizeButton.setOnAction( ( ActionEvent event ) ->
        {
            casesMinimized =  ! casesMinimized;

            //If it's minimized, we need to shrink the box,
            //and remove all the active elements/controls
            //
            //@TODO: Refactor this into a data structure possibly?
            //       Using a Stack would be the best implementation
            //       but what superclass is above all of them?
            if ( casesMinimized )
            {
                //Removes in decreasing order
                for( int i = START_STACK_INDEX; i >= END_STACK_INDEX; i-- )
                {
                    Node nextNode = this.casesAnchorPane.getChildren().remove( i );
                    if( nextNode instanceof Button )
                    {
                        this.casesButtons.push( ( Button ) nextNode );
                    } else if( nextNode instanceof ComboBox )
                    {
                        this.casesButtons.push( ( ComboBox ) nextNode );
                    }
                }

                casesTabPane.setPrefSize( 0, 0 );
                minimizeButton.setText( "+ " + casesModel.getTitle() );
                minimizeButton.setPrefWidth( BUTTON_WIDTH << 4 );
            } else
            {
                casesTabPane.setPrefSize( PANE_WIDTH + this.c_horzSpacingOffset, PANE_HEIGHT + this.c_vertSpacingOffset );
                minimizeButton.setText( "-" );

                //Adds in increasing order
                while( !this.casesButtons.isEmpty() )
                {
                    Node nextNode = this.casesButtons.pop();
                    if( nextNode instanceof Button )
                    {
                        this.casesAnchorPane.getChildren().add( ( Button ) nextNode );
                    } else if( nextNode instanceof ComboBox )
                    {
                        this.casesAnchorPane.getChildren().add( ( ComboBox ) nextNode );
                    }
                    minimizeButton.setPrefWidth( BUTTON_WIDTH + 5 );
                }
                
            }
        } );

        //////////////////////////////////////////////////
        //           ON SEARCH BUTTON TRIGGER           //
        //////////////////////////////////////////////////
        
        //
        //  Configures the mouse-over events
        //
        searchButton.addEventFilter( MouseEvent.MOUSE_ENTERED, ( MouseEvent event ) ->
        {
            searchButton.setText( "Search" );
        });
        
        searchButton.addEventFilter( MouseEvent.MOUSE_EXITED, ( MouseEvent event ) ->
        {
            searchButton.setText( this.c_stringIndexes != null ? ( this.c_searchPosition + 1 ) + "/" + this.c_stringIndexes.size() 
                                                                       : "Search" );
        });
        
        //
        //  Configures the on-press (actual search) event
        //        
        searchButton.setOnAction( ( ActionEvent event ) ->
        {
            //
            //  In the future, perhaps refactor this into 
            //  an FXML file...
            //
            TextField searchField = configureSearchBox( "Cases Search", this );
            
            String searchString   = null;
            this.c_searchPosition      = -1;

            if (  ! searchField.getText().isEmpty() )
            {
                searchString = searchField.getText();

                //Thank you regex
                Pattern pattern      = this.getRegexPattern( searchString );
                Matcher matcher      = pattern.matcher( casesModel.toString() ); //Where input is a TextInput class
                boolean searching    = matcher.find( 0 );

                this.c_stringIndexes = new LinkedList<>();
                searchNext.setDisable( true );
                searchPrev.setDisable( true );

                while ( searching )
                {
                    this.c_stringIndexes.add( new Pair( matcher.start(), matcher.end() ) );

                    searching = matcher.find( matcher.end() );
                }

                if ( this.c_stringIndexes.isEmpty() )
                {
                    this.configureNotFoundStage( ( Stage ) mainPane.getScene().getWindow(), searchString );
                } 
                else
                {
                    Pair nextPair = this.c_stringIndexes.get(  ++ this.c_searchPosition );
                    casesTextArea.selectRange( ( int ) nextPair.getKey(), ( int ) nextPair.getValue() ); // By default, selects the first pair
                    searchButton.setText( ( this.c_searchPosition + 1 )+ "/" + this.c_stringIndexes.size() ); 
                    
                    if ( this.c_stringIndexes.size() != this.c_searchPosition + 1 )
                    {
                        this.casesAnchorPane.getChildren().get( SEARCH_NEXT_INDEX ).setDisable( false ); //Enables the 'next' button
                    }
                }
            }
            else
            {
                searchButton.setText( "Search" );
            }
        } );

        ///////////////////////////////////////////////
        //           ON SELECT NEXT WORD EVENT       //
        ///////////////////////////////////////////////
        searchNext.setOnAction( ( ActionEvent event ) ->
        {
            if ( c_searchPosition < c_stringIndexes.size() - 1)
            {
                Pair nextPair = c_stringIndexes.get(  ++ c_searchPosition );

                casesTextArea.selectRange( ( int ) nextPair.getKey(), ( int ) nextPair.getValue() );

                searchButton.setText( ( c_searchPosition + 1 )+ "/" + this.c_stringIndexes.size() );       

                //If we are not at either end of the linkedlist,
                //we can enable both buttons.
                if ( searchPrev.isDisabled() )
                {
                    searchPrev.setDisable( false );
                }
            } else
            {
                searchNext.setDisable( true );
            }
        } );

        ///////////////////////////////////////////////
        //           ON SELECT PREV WORD EVENT       //
        ///////////////////////////////////////////////
        //
        //  Essentially the reverse of the above procedure
        searchPrev.setOnAction( ( ActionEvent event ) ->
        {
            if ( c_searchPosition <= 0 )
            {
                searchPrev.setDisable( true );

            } else
            {
                Pair nextPair = c_stringIndexes.get(  -- c_searchPosition );

                casesTextArea.selectRange( ( int ) nextPair.getKey(), ( int ) nextPair.getValue() );

                searchButton.setText( ( c_searchPosition + 1 ) + "/" + this.c_stringIndexes.size() );       
                //If we are not at either end of the linkedlist,
                //we can enable both buttons.
                if ( searchNext.isDisabled() )
                {
                    searchNext.setDisable( false );
                }
            }
        } );

        ///////////////////////////////////////////////
        //          COPY TEXT EVENT                  //
        ///////////////////////////////////////////////
        copy.setOnAction( ( ActionEvent event ) -> 
        {
            configureCopyEvent( casesTextArea );  
        } );
        
        ///////////////////////////////////////////////
        //          PASTE TEXT EVENT                 //
        ///////////////////////////////////////////////
        paste.setOnAction( ( ActionEvent event ) ->
        {
            configurePasteEvent( casesTextArea );
        } );        
    }
    private boolean hasChains = false;
    /**
     * Saves the current argument scheme to a .sch file. Should use serialized
     * objects so if we load in a new scheme, we know where to place them upon
     * load-in. 
     *
     * @param event
     * @throws java.io.FileNotFoundException
     * @throws java.io.UnsupportedEncodingException
     * @TODO ???
     */
    @FXML
    protected void saveArgumentScheme( ActionEvent event ) throws FileNotFoundException, 
                                                                  UnsupportedEncodingException
    {
        HashMap<String, ArgumentViewTree> argTrees = this.parentControl
                                                     .getConstructionAreaController()
                                                     .getArgTreeMap();
        final File filePath;
        
        //Attempts to write the diagram as a .sch file
        FileChooser chooser = new FileChooser();
        chooser.setTitle( "Save Diagram" );
        chooser.getExtensionFilters().add( new FileChooser.ExtensionFilter( "SCH", "*.sch" ) );
        chooser.setInitialDirectory( new File( System.getProperty( "user.dir" ) ) );

         //User-selected path
        filePath = chooser.showSaveDialog( menuBar.getScene().getWindow() );
        if ( filePath != null )
        {
            try ( PrintWriter fileWriter = new PrintWriter( new FileOutputStream( filePath.getAbsolutePath(), false ) ) )
            {
                // Actually saves the trees if there are no chains
                if ( ! hasChains )
                {
                    this.windowTitle.setText("SWED - Software Engineering Ethics Debater -- " + filePath.getName() );
                    this.saved     = true;                    
                    for ( ArgumentViewTree tree : argTrees.values() )
                    {
                        tree.saveTree( fileWriter );
                    }
                }
                else
                {
                    AlertStage alert = new AlertStage( AlertType.WARNING, "To save the tree, disconnect all chained arguments. This is an experimental feature.",
                                                     ( Stage ) this.parentControl.constructionAreaController.mainPane.getScene().getWindow() );
                }
                this.hasChains = false;
                fileWriter.println( "EOF" );
                fileWriter.close();
            }
        }
    }
    
    

    /**
     *
     * After the user has saved a .sch file (which should encapsulate the data
     * from the Models), they can open a .sch to load in their saved data at the
     * respective positions they were before.
     *
     * @param event
     * @throws java.io.IOException
     * @TODO  Currently works on 1-layer deep trees, but need to add > 1-layer deep 
     *        tree functionality. This can POSSIBLY be implemented via the 
     *        SchemeModel array... but... how...
     */
    @FXML
    protected void openArgumentScheme( ActionEvent event ) throws IOException
    {
        File filePath       = null;
        
        //  Attempts to write the diagram as a .sch file
        FileChooser chooser = new FileChooser();
        chooser.setTitle( "Open Diagram" );
        chooser.getExtensionFilters().add( new FileChooser.ExtensionFilter( "SCH", "*.sch" ) );
        chooser.setInitialDirectory( new File( System.getProperty( "user.dir" ) ) );

         // User-selected path
        filePath = chooser.showOpenDialog( menuBar.getScene().getWindow() );
        if ( filePath != null )
        {
            BufferedReader fileReader = null;
            
            try
            {
                fileReader = new BufferedReader( new FileReader( filePath.getAbsolutePath() ) );
            }
            catch ( FileNotFoundException ex )
            {
                ex.printStackTrace();
            }
            ConstructionAreaController cac  =  this.parentControl.getConstructionAreaController();

            /////////////////////////////////////////
            //                                     //
            //                                     //
            //            LOAD IN SCHEME           //
            //                                     //
            //                                     //
            /////////////////////////////////////////
            
            String      line         = "";   //  Current line
            int         treeDepth    = 0;    //  Depth of tree tracker
            final int   CA_FLAG      = 1;    //  Flags for loading in data (bitmasks)
            final int   CQ_FLAG      = 3;    //  If the flag is set to 3, it's a CQ
            final int   TYPE_FLAG      = 0;  //  Position 0 (x) in the flags
            final int   PRO_CON_FLAG   = 1;  //  Position 1 (y) in the flags
            final int   CQ_CHOICE_FLAG = 2;  //  Position 2 (z) in the flags)
            
            //  HashMap to contain all pre-created trees with their
            //  respective depths so we can add premises to them later
            HashMap<Integer, ArgumentViewTree> trees =  new HashMap<>();
            LinkedList<CQTuple> cqTuples             = new LinkedList<>();
            
            //  SchemeModel array houses all schemes created which are 
            //  eventually added to the AVTs. Necessary to add premises
            //  down the road (in case the file is out of order which
            //  95% of the time, it will be.
            SchemeModel[] schemes                    = new SchemeModel [ 200 ];
            int currentProp = 1;
            
            while( !( line = fileReader.readLine() ).equals( "EOF" ) )
            {
                if( line.equals( "ENDTREE" ) ){
                    //REGISTER ALL TREES
                    this.registerTrees( schemes, trees, cqTuples );
                            
                    //Null out the array for secondary trees
                    schemes   = new SchemeModel[ 200 ];
                    trees.clear();
                    continue;
                }
                //////// Process the next lines /////////
                // Read in current depth
                treeDepth = Character.getNumericValue( line.charAt( line.length() - 1 ) );
                
                // Read in type of proposition
                line = fileReader.readLine();
                String type = line.substring( treeDepth + 5 );

                String title      = null;
                String definition = null;
                String[] CQs      = null;
                
                // Read in title for non cq elements
                if ( ! type.equalsIgnoreCase( "cq" ) )
                {
                    // Read in title
                    line  = fileReader.readLine();
                    title = line.substring( treeDepth + 6 );
                }
                
                // Read in CQs for conclusion ONLY
                if ( type.equalsIgnoreCase( "conclusion" ) )
                {
                    //CQs
                    line  = fileReader.readLine();
                    CQs   = ( line.substring( treeDepth + 4 ) ).split( ";" );
                }
                
                //  Read in proposition value (actual text from box when saved)
                line = fileReader.readLine();
                String proposition  = line.substring( treeDepth + 12 );            
                
                //  Read in definition for non-cq elements
                if ( ! type.equalsIgnoreCase( "cq" ) )
                {
                    line = fileReader.readLine();
                    definition = line.substring( treeDepth + 11 );
                }
                
                //  Read in x position    
                line = fileReader.readLine();
                int xPos = Integer.parseInt( line.substring( treeDepth + 2 ) );
                
                //  Read in y position
                line = fileReader.readLine();
                int yPos = Integer.parseInt( line.substring( treeDepth + 2 ) );
                
                //
                //  Reads in the counter argument and pro/con flag.
                //
                //  Format: flags=x y z 
                //
                //  x is the CA flag, or CQ flag
                //  y is the border color (0 = green, 1 = red, 2 = black) 
                //  z is the CQ choice flag (which question from the scheme they chose)
                //
                line = fileReader.readLine();
                int[] flags = { -1, -1, -1 };
                flags[ TYPE_FLAG ]    = Integer.parseInt( line.substring( treeDepth + 6, treeDepth + 7 ) );
                
                if ( flags[ TYPE_FLAG ] == CQ_FLAG )
                {
                    flags[ PRO_CON_FLAG ] = Integer.parseInt( line.substring( line.length() - 3, line.length() - 2 ) );
                    flags[ CQ_CHOICE_FLAG ] = Integer.parseInt( line.substring( line.length() - 1 ) );
                }
                else
                {
                    
                    flags[ PRO_CON_FLAG ] = Integer.parseInt( line.substring( line.length() - 1 ) );
                }
                
                System.out.println( "Line: " + line + " \nflags: " + flags[ PRO_CON_FLAG ] );
                
                ArgumentViewTree argViewTree = new ArgumentViewTree( cac.getMainPane(), cac );
                
                //  Insert the conclusion into the array holding logical positions 
                //  of conclusions
                if( type.equalsIgnoreCase( "conclusion" ) || type.equalsIgnoreCase( "counter" ) )
                {
                    SchemeModel scheme      = new SchemeModel( title, definition, proposition, new Pair( xPos, yPos ), treeDepth, flags );
                    scheme.setCriticalQs( Arrays.asList( CQs ) );
                    
                    SchemeModel[] schemeArray = type.equalsIgnoreCase( "conclusion" ) ? schemes : null;
                    HashMap<Integer, ArgumentViewTree> treeSelection = type.equalsIgnoreCase( "conclusion" ) ? trees : null;
                    
                    schemeArray[ treeDepth ]  = scheme;
                    treeSelection.put( treeDepth, argViewTree );
                    
                    //  If we have a conclusion that has other premises, but is 
                    //  also the premise of ANOTHER [higher-up] conclusion,
                    //  we need to designate it as a premise for that aforesaid
                    //  [higher-up] conclusion.
                    if ( schemeArray[ treeDepth - 1 ] != null )
                    {
                        schemeArray[ treeDepth - 1 ].addPremise( new Premise( title, definition, proposition ) );
                    }                     
                }
                
                // Loads in the CQ flag if it's an actual CQ (we check to see if 
                // the pro/con flag is 3 in case the pro/con is incorrect)
                if ( type.equalsIgnoreCase( "CQ" ) && flags[ PRO_CON_FLAG ] != 3 )
                {
                    cqTuples.add( new CQTuple( flags[ CQ_CHOICE_FLAG ], flags[ PRO_CON_FLAG], proposition ) );
                }
                
                //
                //  If we're NOT on a counter (premise or CQ)
                //
                else if ( ! type.equalsIgnoreCase( "counter" ) )
                {
                    //
                    //  If we have counter argument trees present, we need to
                    //  add the next schemes to it instead
                    //
                    SchemeModel[] schemeArray = schemes; /*FXUtils.isEmpty( caSchemes ) ? schemes : caSchemes;*/
                    
                    //  We need to traverse backwards from our current position up to
                    //  the top of the tree to see if there's a conclusion above 
                    //  the premise/conclusion.
                    for( int p = treeDepth - 1; p > 0; p-- )
                    {
                        //
                        //  A conclusion is valid if and only if it is not null in
                        //  the array, AND its depth is not the same as the current
                        //  proposition's depth (that would imply that it isn't
                        //  a conclusion of the prfoposition).
                        //
                        if ( schemeArray[ p ] != null && schemeArray[ p ].getTreeDepth() != treeDepth )
                        {
                            int colonIndex = proposition.indexOf( ":" );

                            if ( colonIndex > 0 )
                            {
                                schemeArray[ p ].addPremise( new Premise( title, definition, proposition ) );
                            } 
                            else
                            {
                                AlertStage alert = new AlertStage( AlertType.ERROR, "Could not open diagram; all premises should have a title and definition separated by a colon ( : ).",
                                                                   ( Stage ) this.parentControl.constructionAreaController.mainPane.getScene().getWindow() );
                                throw new IllegalArgumentException( "Premises should have a name and a title separated by a colon ( : )" );
                            }

                            break;
                        }
                    }
                }
                
                // Grab the last "END" line
                line = fileReader.readLine();
                // A simple validation checker
                if ( line.substring( treeDepth ).contains( "END" ) )
                {
                    currentProp++;
                } 
                else
                {
                   throw new IllegalStateException( "The last line should contain \"END\"!" );
                }
            }
            // REGISTER ALL TREES
            this.registerTrees( schemes, trees, cqTuples );
            
            this.windowTitle.setText("SWED - Software Engineering Ethics Debater -- " + filePath.getName() );
        }
    }

    /**
     * Very primitive way of printing the active diagram, but it will take an
     * screen-shot of the current construction area, while minimizing the side
     * bars if they are present, then prints it out 
     *
     * @param event
     */
    @FXML
    protected void printArgumentScheme( ActionEvent event )
    {
        //If the scheme pane is currently active, we need to 
        //temporary disable it so we can get a clean screenshot
        boolean schemes = false;

        if ( parentControl.schemesShowing() )
        {
            parentControl.toggleSchemes();
            schemes = true;
        }
        
        //  We need to grab the parent pane from the CAC 
        //  so we can screenshot the ENTIRE workspace 
        //  and not only the visible portion.
        ConstructionAreaController cac = this.parentControl.getConstructionAreaController();
        WritableImage snapshot         = cac.mainPane.snapshot( new SnapshotParameters(), null );

        //Flips it back to their original state
        if ( schemes )
        {
            parentControl.toggleSchemes();
        }
        
        ImageView image = new ImageView( snapshot );
        
        PrinterJob printJob    = PrinterJob.createPrinterJob();
        PageLayout printLayout = printJob.getJobSettings().getPageLayout();
        printJob.getJobSettings().setPageLayout( printLayout );
        
        image.setFitWidth( printLayout.getPrintableWidth() );
        image.setFitHeight( printLayout.getPrintableHeight() );
        image.setPreserveRatio( true );
        
        if ( printJob != null )
        {
            Window window = this.parentControl.constructionAreaController.mainPane
                                                            .getScene().getWindow();
            
            boolean pageSetupDialog = printJob.showPageSetupDialog( window );
            
            if ( !pageSetupDialog )
            {
                printJob.cancelJob();
                return;
            }
            
            boolean printDialog     = printJob.showPrintDialog( window );
            
            if ( printDialog )
            {
                boolean printed     = printJob.printPage( image );
                     
                if ( printed )
                {
                    printJob.endJob();
                }
            } 
            else
            {
                printJob.cancelJob();
            }
        }
    }

    /**
     * Configures the argument scheme xml file loader.
     * 
     * @param event 
     */
    @FXML
    protected void loadArgumentSchemeData( ActionEvent event )
    {
        File loadedXML                = null;
        List<SchemeModel> schemesList = null;    
        boolean replacing             = false;
        
        if ( !MainApp.Jmp_Start )
        {
            FileChooser chooser = new FileChooser();
            this.configureFileChooser( chooser );
            
            chooser.setTitle( "Load Argument Scheme XML" );
            loadedXML = chooser.showOpenDialog( menuBar.getScene().getWindow() );
            replacing = this.openArgWithKeyFlag ? false : Boolean.parseBoolean
                                                        ( ( String ) ( ( MenuItem ) event.getSource() ).getUserData() );   
        } 
        else 
        {
            loadedXML = new File( MainApp.Jmp_Start_Path );
        }
        
        if ( loadedXML != null )
        {
            try
            {
                schemesList = this.generateSchemeList( loadedXML );
                this.parentControl.setSchemeModelList( schemesList, replacing );
                this.parentControl.toggleSchemes();
            } 
            catch ( JAXBException ex )
            {
                Logger.getLogger( TitleAndMenuBarController.class.getName() ).log( Level.SEVERE, null, ex );
                this.schemesActive = false;
                return;
            }
            
            this.parentControl.toggleSchemes();
        }
        
        this.schemesActive = true;
        
        // Once we officially load in a scheme XML, we can swap the accessibility
        // of the "load" and "replace" scheme XML buttons
        this.argumentsMenu.getItems().get( 0 ).setDisable( true );
        this.argumentsMenu.getItems().get( 1 ).setDisable( false );
        
        event.consume();
    }
    
    /**
     * Instead of copy and pasting the method information from above, why not
     * just call the loadArgumentSchemeData method with a different userData
     * parameter from the event >:D
     * @param event 
     */
    @FXML
    protected void replaceArgumentSchemeData( ActionEvent event )
    { 
        this.loadArgumentSchemeData( event );
    }
    
    /**
     * Configures the case study XML file loader.
     * @param event
     * @throws SAXException 
     */
    @FXML
    protected void loadCaseStudy( ActionEvent event ) throws SAXException
    {
        if ( this.casesActive )
        {
            AlertStage alert = new AlertStage( AlertType.ERROR, "Cannot open cases pane; one is already present. "
                                                              + "Please close that one before opening another.",
                                                                ( Stage ) this.parentControl.constructionAreaController.mainPane.getScene().getWindow() );
            return;
        }        
        FileChooser chooser = new FileChooser();
        configureFileChooser( chooser );
        chooser.setTitle( "Load Case Study XML" );

        File loadedXML = chooser.showOpenDialog( menuBar.getScene().getWindow() );
        CaseModel caseStudy = null;
        
        if ( loadedXML != null )
        {
            try
            {
                caseStudy = generateCaseStudy( loadedXML );
            } 
            catch ( JAXBException ex )
            {
                Logger.getLogger( TitleAndMenuBarController.class.getName() ).log( Level.SEVERE, null, ex );
                this.casesActive = false;
                return;
            }
        }
        
        event.consume();
        
        try 
        {
            this.openCaseTab( caseStudy );
        } 
        catch ( UnsupportedEncodingException ex ) 
        {
            Logger.getLogger( TitleAndMenuBarController.class.getName() ).log( Level.SEVERE, null, ex );
            this.casesActive = false;
        }
    }

    /**
     * Configures the ethics XML file loader.
     * @param event
     * @throws SAXException
     * @throws IOException 
     */
    @FXML
    protected void loadEthics( ActionEvent event ) throws SAXException, IOException
    {
        if ( this.ethicsActive )
        {
            AlertStage alert = new AlertStage( AlertType.ERROR, "Cannot open ethics pane; one is already present. "
                                                              + "Please close that one before opening another.",
                                                             ( Stage ) this.parentControl.constructionAreaController.mainPane.getScene().getWindow() );
            return;
        }
        
        
        FileChooser chooser = new FileChooser();
        configureFileChooser( chooser );
        chooser.setTitle( "Load Ethics XML" );
        
        File loadedXML = chooser.showOpenDialog( menuBar.getScene().getWindow() );
        
        EthicsModel ethics = null;
        if ( loadedXML != null )
        {
            try
            {
                ethics = generateEthics( loadedXML );
            } 
            catch ( JAXBException ex )
            {
                Logger.getLogger( TitleAndMenuBarController.class.getName() ).log( Level.SEVERE, null, ex );
                this.ethicsActive = false;
                return;
            }
        }
        
        event.consume();

        try
        {
            this.openEthics( ethics );
        }
        catch ( IOException ex )
        {
            Logger.getLogger( TitleAndMenuBarController.class.getName() ).log( Level.SEVERE, null, ex );
            this.ethicsActive = false;
        }
    }

    /**
     * Generates the ethics model for the XML parser
     * 
     * @param fileName
     * @return
     * @throws JAXBException
     * @throws SAXException 
     */
    private EthicsModel generateEthics( File fileName ) throws JAXBException, SAXException
    {
        EthicsModel ethics = null;
        try
        {
            SchemaFactory sf               = SchemaFactory.newInstance( "http://www.w3.org/2001/XMLSchema" );
            InputStream   xmlStream        = new FileInputStream( fileName.getAbsoluteFile() );
            JAXBContext   jaxbContext      = JAXBContext.newInstance( EthicsModel.class );
            Unmarshaller  jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            ethics = ( EthicsModel ) jaxbUnmarshaller.unmarshal( xmlStream );
        } 
        catch ( JAXBException | FileNotFoundException e )
        {
            AlertStage alert = new AlertStage( AlertType.ERROR, "Error -- Could not load Ethics XML file. Did you load the wrong file?",
                                             ( Stage ) this.parentControl.constructionAreaController.mainPane.getScene().getWindow() );
            Logger.getLogger( TitleAndMenuBarController.class.getName() ).log( Level.SEVERE, null, e );
            this.ethicsActive = false;
            return null;
        }

        return ethics;
    }

    /**
     * Loads in the necessary case study xml file
     * 
     * @param fileName
     * @return
     * @throws JAXBException
     * @throws SAXException 
     */
    private CaseModel generateCaseStudy( File fileName ) throws JAXBException, SAXException
    {
        CaseModel caseStudy = null;
        try
        {
            SchemaFactory sf               = SchemaFactory.newInstance( "http://www.w3.org/2001/XMLSchema" );
            InputStream   xmlStream        = new FileInputStream( fileName.getAbsoluteFile() );
            JAXBContext   jaxbContext      = JAXBContext.newInstance( CaseModel.class );
            Unmarshaller  jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            caseStudy                      = ( CaseModel ) jaxbUnmarshaller.unmarshal( xmlStream );
        } 
        catch ( JAXBException | FileNotFoundException e )
        {
            AlertStage alert = new AlertStage( AlertType.ERROR, "Error -- Could not load Cases XML file. Did you load the wrong file?",
                                             ( Stage ) this.parentControl.constructionAreaController.mainPane.getScene().getWindow() );
            
            Logger.getLogger( TitleAndMenuBarController.class.getName() ).log( Level.SEVERE, null, e );
            this.casesActive = false;
            return null;
        }

        return caseStudy;
    }

    /**
     * Loads in the scheme list xml file
     * 
     * @param fileName
     * @return
     * @throws JAXBException 
     */
    private List<SchemeModel> generateSchemeList( File fileName ) throws JAXBException
    {
        List<SchemeModel> schemeList = new ArrayList<>();
        try
        {
            SchemaFactory sf               = SchemaFactory.newInstance( "http://www.w3.org/2001/XMLSchema" );
            Schema        schema           = sf.newSchema( getClass().getResource( "/xml/scheme.xsd" ) );
            InputStream   xmlStream        = new FileInputStream( fileName.getAbsoluteFile()  );
            JAXBContext   jaxbContext      = JAXBContext.newInstance( SchemeList.class );
            Unmarshaller  jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            sl                             = ( SchemeList ) jaxbUnmarshaller.unmarshal( xmlStream );
            
            jaxbUnmarshaller.setSchema( schema );
            schemeList = sl.getModels();
        } 
        catch ( SAXException | FileNotFoundException ex )
        {
            AlertStage alert = new AlertStage( AlertType.ERROR, "Error -- Could not load Scheme XML file. Did you load the wrong file?",
                                             ( Stage ) this.parentControl.constructionAreaController.mainPane.getScene().getWindow() ); 
            
            Logger.getLogger( MainApp.class.getName() ).log( Level.SEVERE, null, ex );
            this.schemesActive = false;
            return null;
        }
       
        return schemeList;

    }

    /**
     * Prematurely configures the file chooser parameter to select XML-extension
     * files.
     * @param chooser 
     */
    private void configureFileChooser( FileChooser chooser )
    {
        chooser.getExtensionFilters().add( new FileChooser.ExtensionFilter( "XML", "*.xml" ) );
        chooser.setInitialDirectory( new File( System.getProperty( "user.dir" ) ) );
    }
    
    /**
     * Registers the trees in the hashmap and schememodel to the current construction
     * area.
     * @param schemes contains the conclusion propositions from the file, with 
     *                 each index containing a separate proposition with premise
     *                 objects as children (not in the array; separate instances
     *                 in the SchemeModel class)
     * 
     * @param trees hashmap contains an integer denoting the appropriate index
     *              that the corresponding schememodel is within the schemes array.
     */
    private void registerTrees( SchemeModel[] schemes, HashMap<Integer, ArgumentViewTree> trees, 
                                LinkedList<CQTuple> cqTuples )
    {
        ConstructionAreaController cac = this.parentControl.getConstructionAreaController();
        for( int i = 0; i < schemes.length; i++ )
        {
            SchemeModel scheme      = schemes[ i ];
            if ( scheme == null )
            {
                continue;
            }
            
            ArgumentModel argModel  = new ArgumentModel( scheme );
            ArgumentViewTree tree   = trees.get( i );
            tree.addRootArgument( argModel, ( int ) scheme.getX(), ( int ) scheme.getY() );
            cac.registerNewArgTree( tree );
            ConclusionPaneController cpcRoot = tree.getRoot().getControl();     
            
            // Adds any CQs that exist in the tree
            while ( ! cqTuples.isEmpty() )
            {
                try
                {
                    tree.loadCriticalQuestion( argModel, cpcRoot.argumentSchemeLabel, cqTuples.pollFirst() );
                }
                catch ( IOException ex )
                {
                    Logger.getLogger( TitleAndMenuBarController.class.getName() ).log( Level.SEVERE, null, ex );
                }
            }
            
            // Adds the pro/con flags to the conclusion
            int proConFlag = schemes[ i ].getProConFlag();

            if ( proConFlag >= 0 && proConFlag <= 2 )
            {
                cpcRoot.proConStatus = schemes[ i ].getProConFlag();
                
                System.out.println( cpcRoot.proConStatus );
                
                switch ( cpcRoot.proConStatus )
                {
                    case 0: cpcRoot.getPropositionBoxController().text.setStyle( "-fx-border-color: #0f0; -fx-border-width: 4;" ); break; //Green
                    case 1: cpcRoot.getPropositionBoxController().text.setStyle( "-fx-border-color: #f00; -fx-border-width: 4;" ); break; //Red 
                    case 2: cpcRoot.getPropositionBoxController().text.setStyle( "-fx-border-color: #000; -fx-border-width: 0;" ); break; //Black
                }
            }
        }    
    }
    
    /**
     * Applies the necessary offsets to the objects whenever the user is 
     * using the resize buttons. This is to prevent the objects from scrolling
     * with the resizing pane.
     * 
     * @param h_shrink
     * @param h_stretch
     * @param v_shrink
     * @param v_stretch
     * @param fontSizesBox
     * @param choice 
     */
    private void applyAnchorTransformation( Button h_shrink,  Button h_stretch, 
                                            Button v_shrink,  Button v_stretch,
                                            Button resizeTab, ComboBox fontSizesBox, 
                                            int choice )
    {
        if( choice != 1 && choice != 0 )
        {
            throw new IllegalArgumentException( "Must pass in 0 for ethics offset or 1 for cases offset!" );
        }
            
        int offset = choice == 0 ? this.e_horzSpacingOffset : this.c_horzSpacingOffset;
            
        //Anchors HORIZONTAL shrink/stretch buttons
        AnchorPane.setRightAnchor( h_shrink, HORZ_SHRINK_ANCHOR + offset );
        AnchorPane.setRightAnchor( h_stretch, HORZ_STRETCH_ANCHOR + offset );

        //Anchors VERTICAL shrink/stretch buttons
        AnchorPane.setRightAnchor( v_shrink, VERT_SHRINK_ANCHOR + offset);
        AnchorPane.setRightAnchor( v_stretch, VERT_STRETCH_ANCHOR + offset);
            
        //Anchors font up and down buttons
        AnchorPane.setRightAnchor( fontSizesBox, FONT_SIZE_ANCHOR + offset );
           
        //Anchors resize button
        AnchorPane.setRightAnchor( resizeTab, RESIZE_BUTTON_ANCHOR + offset );
    }

    /**
     * Configures the necessary anchors for the top pane of the tabs. These
     * anchors dictate the spacing between one node and another.
     * 
     * @param minimizeButton
     * @param h_shrink
     * @param h_stretch
     * @param v_shrink
     * @param v_stretch
     * @param searchNext
     * @param searchPrev
     * @param searchButton
     * @param sizes 
     */
    private void configureButtonAnchors( Button exitButton,   Button minimizeButton,
                                         Button h_shrink,     Button h_stretch,  Button v_shrink, 
                                         Button v_stretch,    Button searchNext, Button searchPrev,
                                         Button searchButton, ComboBox sizes,    Button resizeTab )
    {
        //Forgive me, for I hate raw immediate values,
        //but testing this was hell...
        
        //Anchors exit button
        AnchorPane.setTopAnchor( exitButton, 3.0 );
        AnchorPane.setRightAnchor( exitButton, 2.0 );
        
        //Anchors minimize button
        AnchorPane.setTopAnchor( minimizeButton, 3.0 );
        AnchorPane.setRightAnchor( minimizeButton, 38.0);
        
        //Anchors search button
        AnchorPane.setTopAnchor( searchButton, 3.0 );
        AnchorPane.setRightAnchor( searchButton, 176.0 ); // 75

        //Anchors search next button
        AnchorPane.setTopAnchor( searchNext, 3.0 );
        AnchorPane.setRightAnchor( searchNext, 95.0 ); // 167

        //Anchors search previous button
        AnchorPane.setTopAnchor( searchPrev, 3.0 );
        AnchorPane.setRightAnchor( searchPrev, 135.0 ); //207
        
        //Anchors resize tab pane button
        AnchorPane.setTopAnchor( resizeTab, 3.0 );
        AnchorPane.setRightAnchor( resizeTab, RESIZE_BUTTON_ANCHOR );
        
        //Anchors HORIZONTAL shrink/stretch buttons
        AnchorPane.setTopAnchor( h_shrink, 3.0 );
        AnchorPane.setRightAnchor( h_shrink, HORZ_SHRINK_ANCHOR );
        AnchorPane.setTopAnchor( h_stretch, 3.0 );
        AnchorPane.setRightAnchor( h_stretch, HORZ_STRETCH_ANCHOR );

        //Anchors VERTICAL shrink/stretch buttons
        AnchorPane.setTopAnchor( v_shrink, 3.0 );
        AnchorPane.setRightAnchor( v_shrink, VERT_SHRINK_ANCHOR );
        AnchorPane.setTopAnchor( v_stretch, 3.0 );
        AnchorPane.setRightAnchor( v_stretch, VERT_STRETCH_ANCHOR );
        
        //Anchors font size combobox
        AnchorPane.setTopAnchor( sizes, 3.0 );
        AnchorPane.setRightAnchor( sizes, FONT_SIZE_ANCHOR );
    }
    
    /**
     * Upon resetting the size of the tab, this supplementary method is called
     * to put the buttons that are shifted back to their original places.
     * @param h_shrink
     * @param h_stretch
     * @param v_shrink
     * @param v_stretch
     * @param resizeTab
     * @param fontSizesBox
     * @param choice 
     */
    private void resetAnchors( Button h_shrink, Button h_stretch, 
                               Button v_shrink, Button v_stretch,
                               Button resizeTab, ComboBox fontSizesBox, 
                               int choice )
    {
        if( choice != 1 && choice != 0)
        {
            throw new IllegalArgumentException( "Must pass in 0 for ethics offset or 1 for cases offset!" );
        }
            
        switch( choice )
        {
            case 0: this.e_horzSpacingOffset = this.e_vertSpacingOffset = 0;
                    break;
            case 1: this.c_horzSpacingOffset = this.c_vertSpacingOffset = 0;
                    break;
        } 
            
        //Anchors HORIZONTAL shrink/stretch buttons
        AnchorPane.setRightAnchor( h_shrink,     HORZ_SHRINK_ANCHOR );
        AnchorPane.setRightAnchor( h_stretch,    HORZ_STRETCH_ANCHOR );

        //Anchors VERTICAL shrink/stretch buttons
        AnchorPane.setRightAnchor( v_shrink,     VERT_SHRINK_ANCHOR );
        AnchorPane.setRightAnchor( v_stretch,    VERT_STRETCH_ANCHOR );
        //Anchors font up and down buttons
        AnchorPane.setRightAnchor( fontSizesBox, FONT_SIZE_ANCHOR );
            
        //Anchors resize button
        AnchorPane.setRightAnchor( resizeTab,    RESIZE_BUTTON_ANCHOR );

    }
    
    /**
     * Initializes all button sizes according to their value/purpose.
     * 
     * @param minimizeButton
     * @param h_shrink
     * @param h_stretch
     * @param v_shrink
     * @param v_stretch
     * @param searchNext
     * @param searchPrev
     * @param searchButton 
     */
    private void configureButtonSizes( Button exitButton, Button minimizeButton,
                                       Button h_shrink,   Button h_stretch, 
                                       Button v_shrink,   Button v_stretch,  
                                       Button searchNext, Button searchPrev, 
                                       Button searchButton )
    {
        final int H_OFFSET = 5;
        
        //Sets sizes for exit button
        exitButton.setPrefSize    ( BUTTON_WIDTH + H_OFFSET, BUTTON_HEIGHT );
        
        //Sets sizes for minimize button
        minimizeButton.setPrefSize( BUTTON_WIDTH + H_OFFSET, BUTTON_HEIGHT );
        
        //Sets size for search button
        searchButton.setPrefSize  ( BUTTON_WIDTH + 41, BUTTON_HEIGHT );
        
        //Sets size for shrink/stretch buttons
        h_shrink.setPrefSize ( BUTTON_WIDTH + H_OFFSET, BUTTON_HEIGHT );
        h_stretch.setPrefSize( BUTTON_WIDTH + H_OFFSET, BUTTON_HEIGHT );
        v_shrink.setPrefSize ( BUTTON_WIDTH + H_OFFSET, BUTTON_HEIGHT );
        v_stretch.setPrefSize( BUTTON_WIDTH + H_OFFSET, BUTTON_HEIGHT );

        //We don't need to enable the search-next until 
        //the user has searched something
        searchNext.setDisable( true );
        searchNext.setStyle( "-fx-padding: 0; -fx-font-size: 16;" );

        searchNext.setPrefWidth ( BUTTON_WIDTH + ( H_OFFSET << 1 ) );
        searchNext.setPrefHeight( BUTTON_HEIGHT );

        //Same goes for search-previous button
        searchPrev.setDisable( true );
        searchPrev.setStyle( "-fx-padding: 0; -fx-font-size: 16;" );
        searchPrev.setPrefWidth ( BUTTON_WIDTH + ( H_OFFSET << 1 ) );
        searchPrev.setPrefHeight( BUTTON_HEIGHT );
    }
    
//    /**
//     * Configures the JavaFX ColorPicker object stage to 
//     * set the background color of a TextArea object. 
//     * @param node
//     * @param textArea 
//     */
//    protected void configureColorPicker( Node node )
//    {
//        final Stage colorPickerStage  = new Stage();
//        final Scene colorPickerScene  = new Scene( new HBox( 20 ), 400, 30 );
//        final HBox box                = ( HBox ) colorPickerScene.getRoot();
//        final ColorPicker colorPicker = new ColorPicker();
//        final Label text              = new Label( "Try the color picker!" );
//        
//        colorPickerStage.setTitle( "Change Background Color" );
//        colorPickerStage.getIcons().add( MainApp.icon );
//        colorPickerStage.initModality( Modality.NONE );
//        colorPickerStage.resizableProperty().setValue( false );
//        colorPickerStage.initOwner( ( Stage ) this.parentControl.getConstructionAreaController()
//                                              .mainPane.getScene().getWindow() );
//        
//        text.setFont(Font.font ( "System Regular", 20 ) );
//        text.setAlignment( Pos.BASELINE_RIGHT );
//        box.setPadding( new Insets( 5, 5, 5, 5 ) );  
//
//        colorPicker.setValue( Color.BLACK );
//             
//        colorPicker.setOnAction( new EventHandler() 
//        {
//            @Override
//            public void handle( Event t ) 
//            {
//                String RGBCode = FXUtils.toRGBCode( colorPicker.getValue() );
//                if( node instanceof TextArea )
//                {
//                    ( ( TextArea ) node ).setStyle( "-fx-control-inner-background: " + RGBCode + ";"
//                                                  + "-fx-background-color: "         + RGBCode + ";"
//                                                  + "-fx-font-size: 1.5em;" );
//                }
//            }
//        } );
//                
//        box.getChildren().addAll( colorPicker, text );
//        colorPickerStage.setScene( colorPickerScene );
//        colorPickerStage.show();
//    }        
    
    /**
     * Grabs the text from whatever text area is selected and stores it 
     * in the "clipboard".
     * 
     * @param textArea 
     */
    protected void configureCopyEvent( TextArea textArea )
    {
        MouseUtils.ClipBoard = textArea.getSelectedText();
    }
    
    protected void configurePasteEvent( TextArea textArea )
    {
        if ( MouseUtils.hasContent() )
        {
            textArea.insertText( textArea.getCaretPosition(), MouseUtils.ClipBoard );
        }
    }
    
    /**
     * Configures the color picker stage, allowing the user to change the color
     * of the scheme pane nodes and background.
     * 
     * @param node
     * @param spcList 
     */
//    protected void configureColorSchemes( Node node, List<SchemePaneController> spcList )
//    {
//        final Stage colorPickerStage  = new Stage();
//        final Scene colorPickerScene  = new Scene( new HBox( 20 ), 400, 30 );
//        final HBox box                = ( HBox ) colorPickerScene.getRoot();
//        final ColorPicker colorPicker = new ColorPicker();
//        final Label text              = new Label( "Try the color picker!" );
//        
//        colorPickerStage.setTitle( "Change Background Color" );
//        colorPickerStage.getIcons().add( MainApp.icon );
//        colorPickerStage.initModality( Modality.NONE );
//        colorPickerStage.resizableProperty().setValue( false );
//        colorPickerStage.initOwner( ( Stage ) this.parentControl.getConstructionAreaController()
//                                              .mainPane.getScene().getWindow() );
//        
//        text.setFont(Font.font ( "System Regular", 20 ) );
//        text.setAlignment( Pos.BASELINE_RIGHT );
//        box.setPadding( new Insets( 5, 5, 5, 5 ) );  
//
//        colorPicker.setValue( Color.BLACK );
//        
//        colorPicker.setOnAction( new EventHandler() 
//        {
//            @Override
//            public void handle( Event event )
//            {
//                String RGBCode = FXUtils.toRGBCode( colorPicker.getValue() );
//                
//                // Recolor the entire pane's background 
//                ( ( ScrollPane ) node ).setStyle( "    -fx-background-color: " + RGBCode + ";" 
//                                                + "    -fx-border-insets: 0;" 
//                                                + "    -fx-border-width: 2px;" 
//                                                + "    -fx-border-color: black lightgray lightgray black;" );
//                
//                // Iterate through the SPCs and change their individual component color
//                spcList.forEach( ( spc ) ->
//                {   
//                    spc.superAnchorPane.setStyle( "-fx-background-color: " + RGBCode + ";" );
//                    spc.childAnchorPane.setStyle( "-fx-background-color: " + RGBCode + ";" );
//                } );                        
//            }
//        } );
//        
//        box.getChildren().addAll( colorPicker, text );
//        colorPickerStage.setScene( colorPickerScene );
//        colorPickerStage.show();
//    }
    
    /**
     * Configures the search box used for searching through either of the tabs,
     * or in the schemes pane. 
     * 
     * @param title of window
     * @param initializableController - because there's no parent class of 
     *                                   controller, this has to be checked
     *                                   at runtime if it's a specific
     *                                   controller that has shared
     *                                   instance variables such as 
     *                                   searchButtonSelectors[].
     * @return TextField reference that has the string from the user's search
     */
    protected TextField configureSearchBox( String title, Object initializableController ) 
    {
        SchemeListController scl = null;
        
        if ( initializableController instanceof SchemeListController)
        {
            scl = ( SchemeListController ) initializableController;
        }
        
        final Stage searchStage = new Stage();
        searchStage.setTitle( title );
        searchStage.getIcons().add( MainApp.icon );
        searchStage.initModality( Modality.NONE );
        searchStage.resizableProperty().setValue( false );
        searchStage.initOwner( ( Stage ) this.parentControl.getConstructionAreaController()
                                        .mainPane.getScene().getWindow() );
                    
        //VBox is the root which houses all hboxes
        VBox vbox = new VBox();
        HBox instructionBox = new HBox();
            instructionBox.setPadding( new Insets( 0, 0, 5, 0) );
            instructionBox.setAlignment( Pos.CENTER );
        HBox prefixBox = new HBox();                                //Prefix 
            prefixBox.setPadding( new Insets( 0, 0, 5, 0 ) );
        HBox suffixBox = new HBox();                                //Suffix
            suffixBox.setPadding( new Insets( 0, 0, 5, 0 ) );
        HBox caseSensitiveBox = new HBox();                         //Case sensitive
            caseSensitiveBox.setPadding( new Insets( 0, 0, 5, 0 ) );

            
        //  Add all check-boxes (literally lol) to their hboxes
        
        //
        // Kind of a crummy way of doing this but...
        //
        if( scl != null )
        {
            scl.initSelectableLabels();

            for( int i = 0; i < this.searchButtonSelectors.length; i++ )
            {
            
                scl.searchButtonSelectors[ i ] = new Button( "" );
                scl.searchButtonSelectors[ i ].setPrefSize( 60, 50 );
                scl.searchButtonSelectors[ i ].setStyle( "-fx-font-size: 12; -fx-background-color: transparent;" );
                scl.searchButtonSelectors[ i ].setAlignment( Pos.CENTER );
            }
            
            prefixBox.getChildren().addAll( scl.searchButtonSelectors[ scl.PREFIX_INDEX ], scl.searchParameters[ scl.PREFIX_INDEX ] );
            suffixBox.getChildren().addAll( scl.searchButtonSelectors[ scl.SUFFIX_INDEX ], scl.searchParameters[ scl.SUFFIX_INDEX ] );
            caseSensitiveBox.getChildren().addAll( scl.searchButtonSelectors[ scl.CASE_SENSITIVE_INDEX ], scl.searchParameters[ scl.CASE_SENSITIVE_INDEX ] );
        }
        else
        {
            this.initSelectableLabels();            
            
            for( int i = 0; i < this.searchButtonSelectors.length; i++ )
            {
            
                this.searchButtonSelectors[ i ] = new Button( "" );
                this.searchButtonSelectors[ i ].setPrefSize( 60, 50 );
                this.searchButtonSelectors[ i ].setStyle( "-fx-font-size: 12; -fx-background-color: transparent;" );
                this.searchButtonSelectors[ i ].setAlignment( Pos.CENTER );
            }            
            
            prefixBox.getChildren().addAll( this.searchButtonSelectors[ this.PREFIX_INDEX ], this.searchParameters[ this.PREFIX_INDEX ] );
            suffixBox.getChildren().addAll( this.searchButtonSelectors[ this.SUFFIX_INDEX ], this.searchParameters[ this.SUFFIX_INDEX ] );
            caseSensitiveBox.getChildren().addAll( this.searchButtonSelectors[ this.CASE_SENSITIVE_INDEX ], this.searchParameters[ this.CASE_SENSITIVE_INDEX ] );
        }
            
        Label instructionLabel = new Label( "Enter Search Below. Wildcards are * or ." );
            instructionLabel.setFont( Font.font( "System Regular", 12 ) );
                
        //Add all cbs to their hboxes
        instructionBox.getChildren().add( instructionLabel );

        TextField searchField = new TextField();
            searchField.setFont( Font.font( "System Regular", 12 ) );
                
        //Add okay button
        Button ok = new Button( "Search" );
            ok.setPrefSize( 260, 5 );
            ok.setFont( Font.font( "System Regular", 16 ) );
            ok.setTooltip( new Tooltip( "Press Enter to Search" ) );

        VBox.setMargin( ok, new Insets( 5, 5, 5, 5 ) );
            
        //add them to global vbox
        vbox.getChildren().addAll( instructionBox, searchField, prefixBox, suffixBox, caseSensitiveBox, ok );
        searchStage.setScene( new Scene( vbox, 250, 200 ) );
            
        //If the user clicks the button or presses enter
        ok.setOnMouseClicked( ( MouseEvent _event ) -> { searchStage.close(); } );
        searchStage.getScene().setOnKeyPressed( _key -> 
        { 
            if (_key.getCode() == KeyCode.ENTER) 
            {
                searchStage.close(); 
            }
        } );
            
        searchStage.showAndWait();
        
        return searchField;
    }
    
    /**
     * Initializes all labels into the respective array, and also establishes
     * all mouse listeners.
     */
    private void initSelectableLabels()
    {
        ///////Initialize all labels/////////

        //  Load prefix configuration
        searchParameters[ PREFIX_INDEX ] = new SelectableLabel( "Match Prefix" );
            searchParameters[ PREFIX_INDEX ].setStyle( "-fx-border-color: #D3D3D3; -fx-border-width : 1 0 1 0; " );
            searchParameters[ PREFIX_INDEX ].setFont( Font.font( "System Regular", 16 ) );
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
            searchParameters[ SUFFIX_INDEX ].setFont( Font.font( "System Regular", 16 ) );
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
            searchParameters[ CASE_SENSITIVE_INDEX ].setFont( Font.font( "System Regular", 16 ) );
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
        
        // Prefix
        if ( searchParameters[ PREFIX_INDEX ].isSelected )
        {
           regex.append("\\b").append(searchString).append("[^\\s]+");
        } 
        // Suffix
        else if ( searchParameters[ SUFFIX_INDEX ].isSelected )
        {
           regex.append("[^\\s]+").append(searchString).append("\\b");
        }
        // Default search
        else 
        {
           if( searchString.contains( "*" ) )
           {
               searchString = searchString.replace("*", ".");
           }
           
           regex.append( "\\b" ).append( searchString ).append("\\b"); 
        }
                
         // If case sensitive
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
     * Configures text in sub-menu items to have shortcut keys right-justified.
     */
    private void configureMenus()
    {
        /** File menu **/
        loadMenu.getItems().get( 0 ).setText( String.format( "%-4s%25s", "Open"   , "(Ctrl+O)" ) ); //Open
        loadMenu.getItems().get( 2 ).setText( String.format( "%-7s%22s", "Save-As", "(Ctrl+S)" ) ); //Save-as
        loadMenu.getItems().get( 3 ).setText( String.format( "%-5s%27s", "Print"  , "(Ctrl+P)" ) ); //Print
        loadMenu.getItems().get( 5 ).setText( String.format( "%-5s%29s", "Exit"   , "(ESC)"    ) ); //Exit
        
        /** Tools menu **/
        toolsMenu.getItems().get( 0 ).setText( String.format( "%-8s%25s", "Undo"  , "(Ctrl+Z)"         ) ); //Undo
        toolsMenu.getItems().get( 1 ).setText( String.format( "%-5s%23s", "Clear" , "(Ctrl+Backspace)" ) ); //Clear

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
            if (_key.getCode() == KeyCode.ENTER) 
            {
                dialog.close(); 
            }
        } );
        
        dialog.showAndWait();
    }
    
    /**
     * Configures the mouse dragged and mouse pressed events for the 
     * tab pane that is passed in. This should be a TabPane object.
     * @param tabPane
     * @param children - buttons for tabpane
     */
    private void configureDragListener( TabPane tabPane, AnchorPane children, TextArea... setOfTextAreas )
    {
        for ( TextArea text : setOfTextAreas )
        {
            text.addEventFilter( MouseEvent.MOUSE_ENTERED, ( MouseEvent mouseEvent ) -> 
            {
                mouseOverText = true;
                mouseEvent.consume();
            });
        }
        
        for( TextArea text : setOfTextAreas )
        {
            text.addEventFilter( MouseEvent.MOUSE_EXITED, ( MouseEvent mouseEvent ) ->
            {
                mouseOverText = false;   
                mouseEvent.consume();
            });        
        }
        
        tabPane.addEventFilter( MouseEvent.MOUSE_ENTERED, ( MouseEvent mouseEvent ) -> 
        {
            tabPane.requestFocus();
            mouseEvent.consume();
        } );
        
        tabPane.addEventFilter( MouseEvent.MOUSE_DRAGGED, ( MouseEvent mouseEvent ) ->
        {
            if ( ! mouseOverText )
            {
                this.onDragRelease( mouseEvent, tabPane, children );
                tabPane.requestFocus();
                MouseUtils.Mouse_X = mouseEvent.getSceneX();
                MouseUtils.Mouse_Y = mouseEvent.getSceneY();                    
            }
        } );
        
        tabPane.addEventFilter( MouseEvent.MOUSE_PRESSED, ( MouseEvent mouseEvent ) ->
        {
            tabPane.requestFocus();
            MouseUtils.Mouse_X = mouseEvent.getSceneX();
            MouseUtils.Mouse_Y = mouseEvent.getSceneY();            
        } );        
    }

    /**
     * Method that calculates the offset for the current X and Y against the 
     * previous X and Y to simulate dragging/movement.
     * @param event
     * @param pane 
     */
    private void onDragRelease( MouseEvent event, TabPane pane, AnchorPane children )
    {
        double a = event.getSceneX() - MouseUtils.Mouse_X;
        double b = event.getSceneY() - MouseUtils.Mouse_Y;
        
        // Collision detection to prevent the user from dragging the pane off
        // the screen
        if ( this.checkPaneCollision( pane, children ) )
        {
            return;
        }
        
        this.translatePane( a, b, pane, children );
    }    
    
    /**
     * We need a way to prevent the user from dragging the pane [too-far] off the bounds
     * of the screen (left and top), so we can constrict it to a boundary 
     * 
     * (X_BOUND, Y_BOUND) 
     * 
     * @param pane
     * @param children 
     * @return true if there was a collision so we don't translate anything
     *         in the caller, false otherwise
     */
    private boolean checkPaneCollision( TabPane pane, AnchorPane children )
    {
        Bounds  boundsInScreen = FXUtils.nodePosition( pane );
        boolean hasCollision   = false;
        
        // Hardcoded values for edges of screen with some breathing room.
        final int X_BOUND      = -100;
        final int Y_BOUND      = 35;
        
        // Check x coordinate
        if ( boundsInScreen.getMinX() < X_BOUND )
        {
            // Since we're setting their TRANSLATE position, we need to
            // set it in accordance to its starting point, which is SLIGHTLY 
            // offset from the top, but according to the translate coordinate
            // system, that point (0, 35ish) IS the pane's starting point.
            //
            // PROBLEM: This DOES NOT WORK CORRECTLY when the user is trying to
            // re-position a pane that was spawned in second/third and did not 
            // originally start in the top-left.
            pane.setTranslateX( 0 );
            Iterator<Node> it = children.getChildren().iterator();
            
            while( it.hasNext() )
            {
                Node next = it.next();
                next.setTranslateX( ( int ) pane.getTranslateX() );
            }            
            hasCollision = true;
        }        
         
        // Check y coordinate
        if ( boundsInScreen.getMinY() < Y_BOUND )
        {
            pane.setTranslateY( 0 );   
            Iterator<Node> it = children.getChildren().iterator();
            
            while( it.hasNext() )
            {
                Node next = it.next();
                next.setTranslateY( pane.getTranslateY() );
            }     
            hasCollision = true;
        }
        
        return hasCollision;
    }
    
    /**
     * Helper method to the onDragRelease event; actually applies the 
     * offsets.
     * 
     * @param _x
     * @param _y
     * @param pane 
     */
    private void translatePane( double _x, double _y, TabPane pane, AnchorPane children )
    {
        pane.setTranslateX( ( int ) ( pane.getTranslateX() + _x ) );
        pane.setTranslateY( ( int ) ( pane.getTranslateY() + _y ) );      
        Iterator<Node> it = children.getChildren().iterator();
        
        while( it.hasNext() )
        {
            Node next = it.next();
            next.setTranslateX( ( int ) pane.getTranslateX() );
            next.setTranslateY( ( int ) pane.getTranslateY());
        }
    }        
}

/**
 * Simple subclass of Label so we can store a boolean (selected) in the label.
 * The purpose was to avoid using a button (because of the hideous detailing)
 */
class SelectableLabel extends Label 
{
    protected boolean isSelected;
    
    public SelectableLabel( String s ) 
    {
        super( s );
    }
}