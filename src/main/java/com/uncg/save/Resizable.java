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
  
========*/
package com.uncg.save;

public interface Resizable
{
    /**
     * Configures the width for the proposition rectangle and the
     * PropositionBoxController's text area. The proposition rectangle should
     * be located within the respective class, as should the PropBoxController
     * reference (propBoxController.text).
     * 
     * Before setting the width, be sure to check against null pointers to
     * the PropositionBoxController, and the PBC text area reference.
     * 
     * Set the text-area's width as follows:
     *  this.propBoxController.text.setPrefWidth( width );
     * 
     * Set the proposition rectangle's width as follows: 
     *  this.propositionRectangle.setWidth( width );
     * 
     * To ensure the proper dimension is set, check to see if the new
     * width is greater than the oldWidth. If so, apply this NEGATIVE offset to 
     * the proposition rectangle's translateX. Otherwise, apply the offset
     * POSITIVELY.
     *  +/- ( Math.abs( oldWidth - width ) >> 1 )
     * 
     * @param width
     * @param oldWidth 
     */
    public void setWidth( int width, int oldWidth );
    
    /**
     * Configures the height for the proposition rectangle and the
     * PropositionBoxController's text area. The proposition rectangle should
     * be located within the respective class, as should the PropBoxController
     * reference (propBoxController.text).
     * 
     * Before setting the height, be sure to check against null pointers to
     * the PropositionBoxController, and the PBC text area reference.
     * 
     * Set the text-area's height as follows:
     *  this.propBoxController.text.setPrefHeight( height );
     * 
     * Set the proposition rectangle's height as follows: 
     *  this.propositionRectangle.setHeight( height );
     * 
     * To ensure the proper height is set, check to see if the new
     * height is greater than the oldHeight. If so, apply this NEGATIVE offset to 
     * the proposition rectangle's translateX. Otherwise, apply the offset
     * POSITIVELY.
     *  +/- ( Math.abs( height - oldHeight ) >> 1 )
     * 
     * @param height
     * @param oldHeight 
     */
    public void setHeight( int height, int oldHeight );
}
