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

package com.uncg.save.argumentviewtree;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.Node;

/**
 ArgumentNode class specifies a common base class for use in argument view
 trees. Each ArgumentNode contains a javafx Node (Rectangle, FXML defined views,
 etc) as data, as well as a list of children to represent the tree structure of
 an argument view.

 */
public abstract class ArgumentNode
{

    public static final int      PREMISE_WIDTH      = 325;
    public static final int      PREMISE_HEIGHT     = 145;
    protected static final int   PADDING            = 30; 
    protected static final int   MULTI_ARG_Y_OFFSET = 62;

    private ArgumentNode         parent             = null;
    public  List<ArgumentNode>   children           = null;

    public boolean               connectFlag        = false;
    public boolean               caFlag             = false;
    public boolean               cqFlag             = false;
    private int                  width              = -1;

    public ArgumentNode()
    {
        this.children = new ArrayList<>();
    }

    public List<ArgumentNode> getChildren()
    {
        return children;
    }

    public ArgumentNode getParent()
    {
        return parent;
    }

    public void setParent( ArgumentNode parent )
    {
        this.parent = parent;
    }

    public void addAsChild( ArgumentNode node )
    {
        children.add( node );
    }

    public void removeChild( ArgumentNode node )
    {
        children.remove( node );
    }

    public void shrinkOnDetatch()
    {
    }

    public void setWidth( int i )
    {
        width = i;
    }

    public int getWidth()
    {
        return this.width;
    }

    public abstract Node getView();

    public abstract void setArgTree( ArgumentViewTree argTree );

    public abstract void moveComment( double x, double y );

    public abstract void deleteCommentPane();
}
