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

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import org.xml.sax.SAXException;

public final class ShortcutKeyController 
{
    //////////////////////// INSTANCE VARIABLES /////////////////////////////        
    
    //
    // Controller
    //
    private final TitleAndMenuBarController tambc;
    
    //
    //  Key combinations for shortcut keys
    //
    protected static final KeyCombination   SAVE_KEY;
    protected static final KeyCombination   FULLSCREEN_KEY;
    protected static final KeyCombination   UNDO_KEY;
    protected static final KeyCombination   CLEAR_KEY;
    protected static final KeyCombination   LOAD_ARG_SCHEME;
    protected static final KeyCombination   OPEN_DIAGRAM_KEY;
    protected static final KeyCombination   OPEN_ETHICS_KEY;
    protected static final KeyCombination   OPEN_CASES_KEY;
    protected static final KeyCombination   OPEN_TUTORIAL_KEY;
    protected static final KeyCombination   PRINT_KEY;
    
    //////////////////////// INSTANCE VARIABLES /////////////////////////////        
    
    static 
    {
        SAVE_KEY            = new KeyCodeCombination( KeyCode.S,          KeyCombination.CONTROL_DOWN );
        UNDO_KEY            = new KeyCodeCombination( KeyCode.Z,          KeyCombination.CONTROL_DOWN );
        LOAD_ARG_SCHEME     = new KeyCodeCombination( KeyCode.D,          KeyCombination.CONTROL_DOWN );
        OPEN_DIAGRAM_KEY    = new KeyCodeCombination( KeyCode.O,          KeyCombination.CONTROL_DOWN );
        OPEN_ETHICS_KEY     = new KeyCodeCombination( KeyCode.E,          KeyCombination.CONTROL_DOWN );
        OPEN_CASES_KEY      = new KeyCodeCombination( KeyCode.R,          KeyCombination.CONTROL_DOWN );
        OPEN_TUTORIAL_KEY   = new KeyCodeCombination( KeyCode.T,          KeyCombination.CONTROL_DOWN );      
        PRINT_KEY           = new KeyCodeCombination( KeyCode.P,          KeyCombination.CONTROL_DOWN );
        CLEAR_KEY           = new KeyCodeCombination( KeyCode.BACK_SPACE, KeyCombination.CONTROL_DOWN );
        FULLSCREEN_KEY      = new KeyCodeCombination( KeyCode.F11                                     );
    }
    
    public ShortcutKeyController( Scene scene, TitleAndMenuBarController tambc )
    {
        this.tambc = tambc;
        this.init( scene );
    }
    
    private void init( Scene scene )
    {
        scene.addEventHandler( KeyEvent.KEY_RELEASED, ( KeyEvent event ) -> {
            try{
                if( SAVE_KEY.match( event ) )
                {
                    this.tambc.saveArgumentScheme( new ActionEvent() );
                }
                else if( FULLSCREEN_KEY.match( event ) )
                {
                    this.tambc.toggleFullScreen();
                }
                else if( UNDO_KEY.match( event ) )
                {
                    this.tambc.undoAction( new ActionEvent() );
                }
                else if( CLEAR_KEY.match( event ) ) 
                {
                    this.tambc.clearDiagram();
                }
                else if( LOAD_ARG_SCHEME.match( event ) ) 
                {
                    this.tambc.openArgWithKeyFlag = true;
                    this.tambc.loadArgumentSchemeData( new ActionEvent() );
                }
                else if( OPEN_DIAGRAM_KEY.match( event ) )
                {
                    this.tambc.openArgumentScheme( new ActionEvent() );
                }
                else if( OPEN_ETHICS_KEY.match( event ) )
                {
                    this.tambc.loadEthics( new ActionEvent() );
                }
                else if( OPEN_CASES_KEY.match( event ) ) 
                {
                    this.tambc.loadCaseStudy( new ActionEvent() );
                }
                else if( OPEN_TUTORIAL_KEY.match( event ) ) 
                {
                    this.tambc.showTutorial();
                }
                else if( PRINT_KEY.match( event ) )
                {
                    this.tambc.printArgumentScheme( new ActionEvent() );
                }
            }
            catch( IOException | SAXException ex )
            {
                Logger.getLogger(ShortcutKeyController.class.getName()).log(Level.SEVERE, null, ex);
            } 

        });
    }
}
