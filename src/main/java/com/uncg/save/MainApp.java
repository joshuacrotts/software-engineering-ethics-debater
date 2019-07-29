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

package com.uncg.save;

import com.uncg.save.controllers.RootPaneController;
import java.util.List;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;

public class MainApp extends Application
{
    //
    //  Debug variables
    //
    public static final ClassLoader LOADER          = MainApp.class.getClassLoader();
    public static final boolean     DEBUG           = false;
    public static Stage             MainStage       = null;
    
    //
    // Arguments for loading in an XML scheme
    // without having to explicitly do it
    //
    public static boolean           Jmp_Start       = false;
    public static String            Jmp_Start_Path  = "";
    
    
    public static DataFormat dataModelDataFormat        = new DataFormat( "dataModelFormat"        );
    public static DataFormat schemeModelDataFormat      = new DataFormat( "schemeModelFormat"      );
    public static DataFormat argumentModelDataFormat    = new DataFormat( "argumentModelFormat"    );
    public static DataFormat propositionModelDataFormat = new DataFormat( "propositionModelFormat" );
    public static DataFormat commentDataFormat          = new DataFormat( "commentFormat"          );
    public static Image icon;

    //Comment
    //Stuff added
    @Override
    public void start( Stage stage ) throws Exception
    {
        List<String> args = this.getParameters().getRaw();
        if ( !args.isEmpty() )
        {
            MainApp.Jmp_Start_Path = args.get( 0 );
            MainApp.Jmp_Start = true;
        }
        
        icon                            = new Image( this.getClass().getResourceAsStream( "/images/swed.png" ));
        
        
        FXMLLoader          loader      = new FXMLLoader( getClass().getResource( "/fxml/RootPane.fxml" ) );
        Parent              root        = loader.load();
        RootPaneController  rootControl = loader.<RootPaneController>getController();

        Scene scene = new Scene( root );
        scene.getStylesheets().add( "/styles/Styles.css" );
        stage.toBack();
        stage.setTitle( "SWED" );
        
        //Non-full screen breaks the comments...
        stage.setFullScreenExitKeyCombination( KeyCombination.NO_MATCH );
        stage.setFullScreen( true );
        stage.setScene( scene );
        stage.getIcons().add( icon );
        
        stage.show();
        
        MainStage = stage;
        
        rootControl.setScene( scene );
    }

    /**
     The main() method is ignored in correctly deployed JavaFX application.
     main() serves only as fallback in case the application can not be launched
     through deployment artifacts, e.g., in IDEs with limited FX support.
     NetBeans ignores main().

     @param args the command line arguments
     */
    public static void main( String[] args )
    {
        Application.launch( args );
    }
}
