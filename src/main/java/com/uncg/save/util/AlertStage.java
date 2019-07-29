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


package com.uncg.save.util;

import com.uncg.save.MainApp;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AlertStage {

    private Stage dialog;

    public AlertStage( String title, String alert, int width, 
                       int height  , int fontSize, Stage mainStage ) 
    {
        dialog = new Stage();
        dialog.setTitle( title );
        dialog.getIcons().add( MainApp.icon );
        dialog.initModality( Modality.NONE );
        dialog.resizableProperty().setValue( false );
        dialog.initOwner( mainStage );

        VBox globalBox = new VBox();      //Global vbox

        // Display the alert
        Label label = new Label( alert );
        label.setWrapText( true );
        label.setFont( Font.font( fontSize ) );
        
        globalBox.getChildren().addAll( label );
        dialog.setScene( new Scene( globalBox, width, height ) );
        dialog.showAndWait();
    }
    
    public AlertStage( AlertType alertType, String msg, Stage mainStage )
    {
        Alert alert = new Alert( alertType, msg, ButtonType.OK );
        dialog = ( Stage ) alert.getDialogPane().getScene().getWindow();
        dialog.setAlwaysOnTop( true );
        dialog.initOwner( mainStage );
        dialog.toFront();
        dialog.show();
    }
}
