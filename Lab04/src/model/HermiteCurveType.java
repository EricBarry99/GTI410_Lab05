package model;

import java.awt.Point;
import java.util.List;


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

        /**
         * <p>Title: BezierCurveType</p>
         * <p>Description: ... (CurveType)</p>
         * <p>Copyright: Copyright (c) 2004 Eric Paquette</p>
         * <p>Company: (�TS) - �cole de Technologie Sup�rieure</p>
         * @author Eric Paquette
         * @version $Revision: 1.3 $
         */

public class HermiteCurveType extends CurveType{

        public HermiteCurveType(String name) {
            super(name);
        }

            /* (non-Javadoc)
          * @see model.CurveType#getNumberOfSegments(int)
          */
            public int getNumberOfSegments(int numberOfControlPoints) {
                return numberOfControlPoints - 1;
            }

        /* (non-Javadoc)
         * @see model.CurveType#getNumberOfControlPointsPerSegment()
         */
        public int getNumberOfControlPointsPerSegment() {
            return 2;
        }

            /* (non-Javadoc)
         * @see model.CurveType#getControlPoint(int, int)
         */
            public ControlPoint getControlPoint(List controlPoints,
                                                int segmentNumber, int controlPointNumber) {
                int controlPointIndex = segmentNumber + controlPointNumber;
                return (ControlPoint)controlPoints.get(controlPointIndex);
            }

        /* (non-Javadoc)
         * @see model.CurveType#evalCurveAt(java.util.List, double)
         */
        public Point evalCurveAt(List controlPoints, double t) {

            List tVector = Matrix.buildRowVector4(t*t*t, t*t, t, 1);

            List gVector = Matrix.buildColumnVector4(((ControlPoint)controlPoints.get(0)).getCenter(),
                    ((ControlPoint)controlPoints.get(1)).getCenter(),
                    ((ControlPoint)controlPoints.get(0)).getCenter(),
                    ((ControlPoint)controlPoints.get(1)).getCenter());


            Point p = Matrix.eval(tVector, matrix, gVector);

            // derivee de P1 donne R1, derivee de p4 donne R4
            return p;
        }

        private List hermiteMatrix =
                Matrix.buildMatrix4(2,  -2, 1, 1,
                        -3, 3,  -2, -1,
                        0,  0,  1, 0,
                        1,  0,  0, 0);

            private List matrix = hermiteMatrix;
    }
