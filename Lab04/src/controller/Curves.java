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

import java.awt.event.MouseEvent;
import java.util.List;

import model.*;
import view.Application;
import view.CurvesPanel;

/**
 * <p>Title: Curves</p>
 * <p>Description: (AbstractTransformer)</p>
 * <p>Copyright: Copyright (c) 2004 S�bastien Bois, Eric Paquette</p>
 * <p>Company: (�TS) - �cole de Technologie Sup�rieure</p>
 * @author unascribed
 * @version $Revision: 1.10 $
 */
public class Curves extends AbstractTransformer implements DocObserver {
		
	/**
	 * Default constructor
	 */
	public Curves() {
		Application.getInstance().getActiveDocument().addObserver(this);
	}	

	/* (non-Javadoc)
	 * @see controller.AbstractTransformer#getID()
	 */
	public int getID() { return ID_CURVES; }
	
	public void activate() {
		firstPoint = true;
		Document doc = Application.getInstance().getActiveDocument();
		List selectedObjects = doc.getSelectedObjects();
		if (selectedObjects.size() > 0){
			Shape s = (Shape)selectedObjects.get(0);
			if (s instanceof Curve){
				curve = (Curve)s;
				firstPoint = false;
				cp.setCurveType(curve.getCurveType());
				cp.setNumberOfSections(curve.getNumberOfSections());
			}
			else if (s instanceof ControlPoint){
				curve = (Curve)s.getContainer();
				firstPoint = false;
			}
		}
		
		if (firstPoint) {
			// First point means that we will have the first point of a new curve.
			// That new curve has to be constructed.
			curve = new Curve(100,100);
			setCurveType(cp.getCurveType());
			setNumberOfSections(cp.getNumberOfSections());
		}
	}
    
	/**
	 * 
	 */
	protected boolean mouseReleased(MouseEvent e){
		int mouseX = e.getX();
		int mouseY = e.getY();

		if (firstPoint) {
			firstPoint = false;
			Document doc = Application.getInstance().getActiveDocument();
			doc.addObject(curve);
		}
		ControlPoint cp = new ControlPoint(mouseX, mouseY);
		curve.addPoint(cp);
				
		return true;
	}

	/**
	 * @param string
	 */
	public void setCurveType(String string) {
		if (string == CurvesModel.BEZIER) {
			curve.setCurveType(new BezierCurveType(CurvesModel.BEZIER));
		} else if (string == CurvesModel.LINEAR) {
			curve.setCurveType(new PolylineCurveType(CurvesModel.LINEAR));
		} else if (string == CurvesModel.HERMITE) {
			curve.setCurveType(new HermiteCurveType(CurvesModel.HERMITE));
		} else if (string == CurvesModel.BSPLINE) {
			curve.setCurveType(new BSplineCurveType(CurvesModel.BSPLINE));
		}else {
			System.out.println("Curve type [" + string + "] is unknown.");
		}
	}

	public Curve checkG1Continuity(int controlPointIndex){
		/*
			p4 = p5
			𝑃4 − 𝑃3 = 𝑘(𝑃6 − 𝑃5)
			𝑘 > 0
			𝑃3 , 𝑃4 = 𝑃5 et 𝑃6 colinéaires
		*/
		// on extrait les points a utiliser
		// dans la liste des points, P4 et P5 sont deux points différents; dans les notes du prof c'est le même point (il utilise P6)
		// -> pt ctrl choisi = P4; P5 prof = P4 Prof = P4 code; P6 Prof = P5 Code
		int nbCtrlPts = curve.getShapes().size();

		// on veut empecher de faire es modification si le point choisi est le premier ou le dernier, et si il n'y a pas suffisament de pts de controles
		if(controlPointIndex < nbCtrlPts-1 && nbCtrlPts > 2 && controlPointIndex >= 1){

			ControlPoint p3 = (ControlPoint)curve.getShapes().get(controlPointIndex-1);
			ControlPoint p4 = (ControlPoint)curve.getShapes().get(controlPointIndex);
			ControlPoint p5 = (ControlPoint)curve.getShapes().get(controlPointIndex+1);

			// calcul des deltas X et Y
			int v0y = (p4.getCenter().y - p3.getCenter().y);
			int v0x = (p4.getCenter().x - p3.getCenter().x);

			int v1y = (p5.getCenter().y - p4.getCenter().y);
			int v1x = (p5.getCenter().x - p4.getCenter().x);

			// calculs des normes des vecteurs V0 (avant pt selectionne) et v1 (apres pt selectionne)
			double v0 = Math.sqrt(Math.pow(v0y,2) + Math.pow(v0x,2));
			double v1 = Math.sqrt(Math.pow(v1y,2) + Math.pow(v1x,2));

			double k = v1/v0;

			int newX = p4.getCenter().x + (int) k*v0x;
			int newY = p4.getCenter().y + (int) k*v0y;

			((ControlPoint)curve.getShapes().get(controlPointIndex+1)).setCenter(newX, newY);
		}
		else{
			System.out.println("no modifications !");
		}
		return curve;
	}


	public Curve checkC1Continuity(int controlPointIndex){
			/*
			// comme G1 mais on enleve le K
				• 𝑃4 − 𝑃3 = 𝑃6 − 𝑃5
				• 𝑃3 , 𝑃4 = 𝑃5 et 𝑃6 colinéaires et à même distance
			*/
		int nbCtrlPts = curve.getShapes().size();

		// on veut empecher de faire es modification si le point choisi est le premier ou le dernier, et si il n'y a pas suffisament de pts de controles
		if(controlPointIndex < nbCtrlPts-1 && nbCtrlPts > 2 && controlPointIndex >= 1){

			ControlPoint p3 = (ControlPoint)curve.getShapes().get(controlPointIndex-1);
			ControlPoint p4 = (ControlPoint)curve.getShapes().get(controlPointIndex);
			ControlPoint p5 = (ControlPoint)curve.getShapes().get(controlPointIndex+1);

			// calcul des deltas X et Y
			int v0y = (p4.getCenter().y - p3.getCenter().y);
			int v0x = (p4.getCenter().x - p3.getCenter().x);

			int v1y = (p5.getCenter().y - p4.getCenter().y);
			int v1x = (p5.getCenter().x - p4.getCenter().x);

			// calculs des normes des vecteurs V0 (avant pt selectionne) et v1 (apres pt selectionne)
			double v0 = Math.sqrt(Math.pow(v0y,2) + Math.pow(v0x,2));
			double v1 = Math.sqrt(Math.pow(v1y,2) + Math.pow(v1x,2));

			int newX = p4.getCenter().x+v0x;
			int newY = p4.getCenter().y+v0y;

			((ControlPoint)curve.getShapes().get(controlPointIndex+1)).setCenter(newX, newY);
			System.out.println("C1 modified");
		}
		else{
			System.out.println("no modifications !");
		}
		return curve;
	}


	public void alignControlPoint() {
		if (curve != null) {
			Document doc = Application.getInstance().getActiveDocument();
			List selectedObjects = doc.getSelectedObjects();
			if (selectedObjects.size() > 0){
				Shape s = (Shape)selectedObjects.get(0);
				if (curve.getShapes().contains(s)){
					int controlPointIndex = curve.getShapes().indexOf(s);
					curve = checkG1Continuity(controlPointIndex);
					curve.update();
//					curve = checkG1Continuity(curve, controlPointIndex);
				}
			}
		}
	}
	
	public void symetricControlPoint() {
		if (curve != null) {
			Document doc = Application.getInstance().getActiveDocument();
			List selectedObjects = doc.getSelectedObjects(); 
			if (selectedObjects.size() > 0){
				Shape s = (Shape)selectedObjects.get(0);
				if (curve.getShapes().contains(s)){
					int controlPointIndex = curve.getShapes().indexOf(s);
					curve = checkC1Continuity(controlPointIndex);
					curve.update();
				}
			}
		}
	}

	public void setNumberOfSections(int n) {
		curve.setNumberOfSections(n);
	}
	
	public int getNumberOfSections() {
		if (curve != null)
			return curve.getNumberOfSections();
		else
			return Curve.DEFAULT_NUMBER_OF_SECTIONS;
	}
	
	public void setCurvesPanel(CurvesPanel cp) {
		this.cp = cp;
	}
	
	/* (non-Javadoc)
	 * @see model.DocObserver#docChanged()
	 */
	public void docChanged() {
	}

	/* (non-Javadoc)
	 * @see model.DocObserver#docSelectionChanged()
	 */
	public void docSelectionChanged() {
		activate();
	}

	private boolean firstPoint = false;
	private Curve curve;
	private CurvesPanel cp;
}
