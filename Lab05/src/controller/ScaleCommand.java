/*
   This file is part of j2dcg.
   j2dcg is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 2 of the License, or
   (at your option) any later version.
   j2dcg is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.
   You should have received a copy of the GNU General Public License
   along with j2dcg; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
package controller;

import model.Disc;
import model.Rectangle;
import model.Shape;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.Iterator;
import java.util.List;

/**
 * <p>Title: ScaleCommand</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004 Jean-Fran�ois Barras, �ric Paquette</p>
 * <p>Company: (�TS) - �cole de Technologie Sup�rieure</p>
 * <p>Created on: 2004-03-19</p>
 * @version $Revision: 1.2 $
 */
public class ScaleCommand extends AnchoredTransformationCommand {

	/**
	 * @param sx the multiplier to the horizontal size
	 * @param sy the multiplier to the vertical size
	 * @param anchor one of the predefined positions for the anchor point
	 */
	public ScaleCommand(double sx, double sy, int anchor, List aObjects) {
		super(anchor);
		this.sx = sx;
		this.sy = sy;
		objects = aObjects;
	}


	/* (non-Javadoc)
	 * @see controller.Command#execute()
	 */
	public void execute() {
		System.out.println("command: scaling x by " + sx +
				" and y by " + sy + " ; anchored on " + getAnchor());

		Iterator iter = objects.iterator();
		Shape shape;

		while (iter.hasNext()) {
			// SCALING
			shape = (Shape) iter.next();
			mt.addMememto(shape);
			AffineTransform sc = shape.getAffineTransform();
			AffineTransform sc2 = new AffineTransform();

			// on va chercher la position initiale de l'ancre
			Point initAnchor = getAnchorPosition(shape);
			System.out.println("init anchor position:" + "("+initAnchor.x+","+initAnchor.y+")");

			// on fais le changement de scale
			sc2.scale(this.sx, this.sy);

			sc.preConcatenate(sc2);
			shape.setAffineTransform(sc);

			// TRANSLATION
			mt.addMememto(shape);
			AffineTransform tt = shape.getAffineTransform();
			AffineTransform tt2 = new AffineTransform();

			Point newAnchor = getAnchorPosition(shape);

			// on fais la difference entre les positions de la nouvele et de l'ancienne ancre pour trouver la variation de position
			int diffX = newAnchor.x - initAnchor.x;
			int diffY = newAnchor.y - initAnchor.y;

			System.out.println("New anchor:" + "("+newAnchor.x+","+newAnchor.y+")");
			System.out.println("diff:" + "("+diffX+","+diffY+")");

			// on déplace la figure pour quelle retourne a sa place originale
			tt2.translate(-diffX, -diffY);
//			tt2.translate(-diffX, -diffY);

			Point lastAnchor = getAnchorPosition(shape);
			System.out.println("last anchor:" + "("+lastAnchor.x+","+lastAnchor.y+")");

			tt.preConcatenate(tt2);
			shape.setAffineTransform(tt);



/*
			shape = (Shape) iter.next();
			mt.addMememto(shape);
			AffineTransform sc = shape.getAffineTransform();
			AffineTransform sc2 = new AffineTransform();

			sc2.scale(this.sx, this.sy);
			sc2.translate(-getAnchorPoint(shape).x, -getAnchorPoint(shape).y);

			sc.preConcatenate(sc2);
			shape.setAffineTransform(sc);
*/
		}
	}
	/* (non-Javadoc)
	 * @see controller.Command#undo()
	 */
	public void undo() {
		mt.setBackMementos();
	}

	private MementoTracker mt = new MementoTracker();
	private List objects;
	private double sx;
	private double sy;
}