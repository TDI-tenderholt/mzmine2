/*
 * Copyright 2006-2015 The MZmine 2 Development Team
 * 
 * This file is part of MZmine 2.
 * 
 * MZmine 2 is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * MZmine 2 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * MZmine 2; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

package net.sf.mzmine.modules.visualization.spectra.datasets;

import org.jfree.data.xy.AbstractXYDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYDataset;

import net.sf.mzmine.datamodel.MassList;
import net.sf.mzmine.modules.rawdatamethods.peakpicking.massdetection.PeakInvestigator.PeakInvestigatorDataPoint;

/**
 * Data set for MassList
 */
public class MassListDataSet extends DataPointsDataSet {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

	public MassListDataSet(MassList massList) {
		super("Mass list " + massList.getName(), massList.getDataPoints());
	}

	public XYDataset getErrorBarDataSet() {
		if (mzPeaks[0] instanceof PeakInvestigatorDataPoint) {
			return new ErrorBarDataSet();
		}
		return null;
	}

	public class ErrorBarDataSet extends AbstractXYDataset implements
			IntervalXYDataset {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public int getItemCount(int series) {
			return mzPeaks.length;
		}

		@Override
		public Number getX(int series, int item) {
			return mzPeaks[item].getMZ();
		}

		@Override
		public Number getY(int series, int item) {
			return mzPeaks[item].getIntensity();
		}

		@Override
		public int getSeriesCount() {
			return 1;
		}

		@Override
		public Comparable<String> getSeriesKey(int series) {
			return label;
		}

		@Override
		public Number getStartX(int series, int item) {
			if (mzPeaks[item] instanceof PeakInvestigatorDataPoint) {
				PeakInvestigatorDataPoint dataPoint = (PeakInvestigatorDataPoint) mzPeaks[item];
				return dataPoint.getMZ() - 1.96 * dataPoint.getMzError();
			}

			return getX(series, item);
		}

		@Override
		public double getStartXValue(int series, int item) {
			return (double) getStartX(series, item);
		}

		@Override
		public Number getEndX(int series, int item) {
			if (mzPeaks[item] instanceof PeakInvestigatorDataPoint) {
				PeakInvestigatorDataPoint dataPoint = (PeakInvestigatorDataPoint) mzPeaks[item];
				return dataPoint.getMZ() + 1.96 * dataPoint.getMzError();
			}

			return getX(series, item);
		}

		@Override
		public double getEndXValue(int series, int item) {
			return (double) getEndX(series, item);
		}

		@Override
		public Number getStartY(int series, int item) {
			if (mzPeaks[item] instanceof PeakInvestigatorDataPoint) {
				PeakInvestigatorDataPoint dataPoint = (PeakInvestigatorDataPoint) mzPeaks[item];
				return dataPoint.getIntensity() - 1.96 * dataPoint.getIntensityError();
			}

			return getY(series, item);
		}

		@Override
		public double getStartYValue(int series, int item) {
			return (double) getStartY(series, item);
		}

		@Override
		public Number getEndY(int series, int item) {
			if (mzPeaks[item] instanceof PeakInvestigatorDataPoint) {
				PeakInvestigatorDataPoint dataPoint = (PeakInvestigatorDataPoint) mzPeaks[item];
				return dataPoint.getIntensity() + 1.96 * dataPoint.getIntensityError();
			}

			return getY(series, item);
		}

		@Override
		public double getEndYValue(int series, int item) {
			return (double) getEndY(series, item);
		}

	}
}
