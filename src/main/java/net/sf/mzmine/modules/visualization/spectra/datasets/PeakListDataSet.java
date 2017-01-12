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

import java.util.Vector;

import net.sf.mzmine.datamodel.DataPoint;
import net.sf.mzmine.datamodel.Feature;
import net.sf.mzmine.datamodel.PeakList;
import net.sf.mzmine.datamodel.RawDataFile;
import net.sf.mzmine.desktop.preferences.MZminePreferences;
import net.sf.mzmine.modules.rawdatamethods.peakpicking.massdetection.PeakInvestigator.PeakInvestigatorDataPoint;

import org.jfree.data.xy.AbstractXYDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYDataset;

/**
 * Picked peaks data set;
 */
public class PeakListDataSet extends AbstractXYDataset implements
	IntervalXYDataset {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private PeakList peakList;

    private Feature displayedPeaks[];
    private double mzValues[], intensityValues[];
    private double[] mzErrors = null, intensityErrors = null, minErrors = null;
    private String label;

    public PeakListDataSet(RawDataFile dataFile, int scanNumber,
	    PeakList peakList) {

	this.peakList = peakList;

	Feature peaks[] = peakList.getPeaks(dataFile);

	Vector<Feature> candidates = new Vector<Feature>();
	for (Feature peak : peaks) {
	    DataPoint peakDataPoint = peak.getDataPoint(scanNumber);
	    if (peakDataPoint != null)
		candidates.add(peak);
	}
	displayedPeaks = candidates.toArray(new Feature[0]);

	mzValues = new double[displayedPeaks.length];
	intensityValues = new double[displayedPeaks.length];

	for (int i = 0; i < displayedPeaks.length; i++) {
	    DataPoint dp = displayedPeaks[i].getDataPoint(scanNumber);
	    if (dp == null)
		continue;
	    mzValues[i] = dp.getMZ();
	    intensityValues[i] = dp.getIntensity();
	    if (dp instanceof PeakInvestigatorDataPoint) {
	    	if (mzErrors == null) {
	    		mzErrors = new double[displayedPeaks.length];
	    		intensityErrors = new double[displayedPeaks.length];
	    		minErrors = new double[displayedPeaks.length];
	    	}

	    	PeakInvestigatorDataPoint dpPI = (PeakInvestigatorDataPoint) dp;
	    	mzErrors[i] = dpPI.getMzError();
	    	intensityErrors[i] = dpPI.getIntensityError();
	    	minErrors[i] = dpPI.getMzMinimumError();
	    }
	}

	label = "Peaks in " + peakList.getName();

    }

    @Override
    public int getSeriesCount() {
	return 1;
    }

    @Override
    public Comparable<?> getSeriesKey(int series) {
	return label;
    }

    public PeakList getPeakList() {
	return peakList;
    }

    public Feature getPeak(int series, int item) {
	return displayedPeaks[item];
    }

    public int getItemCount(int series) {
	return mzValues.length;
    }

    public Number getX(int series, int item) {
	return mzValues[item];
    }

    public Number getY(int series, int item) {
	return intensityValues[item];
    }

    public Number getEndX(int series, int item) {
	return getX(series, item).doubleValue();
    }

    public double getEndXValue(int series, int item) {
	return getX(series, item).doubleValue();
    }

    public Number getEndY(int series, int item) {
	return getY(series, item);
    }

    public double getEndYValue(int series, int item) {
	return getYValue(series, item);
    }

    public Number getStartX(int series, int item) {
	return getX(series, item).doubleValue();
    }

    public double getStartXValue(int series, int item) {
	return getX(series, item).doubleValue();
    }

    public Number getStartY(int series, int item) {
	return getY(series, item);
    }

    public double getStartYValue(int series, int item) {
	return getYValue(series, item);
    }

	public XYDataset getErrorBarDataSet() {
		if (mzErrors != null && mzErrors.length > 0) {
			return new ErrorBarDataSet();
		}

		return null;
	}

	public class ErrorBarDataSet extends AbstractXYDataset implements
			IntervalXYDataset {

		private static final long serialVersionUID = 1L;
		private final double MULTIPLIER = MZminePreferences.numOfStdDevs.getValue();

		@Override
		public int getItemCount(int series) {
			return mzValues.length;
		}

		@Override
		public Number getX(int series, int item) {
			return mzValues[item];
		}

		@Override
		public Number getY(int series, int item) {
			return intensityValues[item];
		}

		@Override
		public Number getStartX(int series, int item) {
			return mzValues[item] - MULTIPLIER * mzErrors[item];
		}

		@Override
		public double getStartXValue(int series, int item) {
			return (double) getStartX(series, item);
		}

		@Override
		public Number getEndX(int series, int item) {
			return mzValues[item] + MULTIPLIER * mzErrors[item];
		}

		@Override
		public double getEndXValue(int series, int item) {
			return (double) getEndX(series, item);
		}

		@Override
		public Number getStartY(int series, int item) {
			return intensityValues[item] - MULTIPLIER * intensityErrors[item];
		}

		@Override
		public double getStartYValue(int series, int item) {
			return (double) getStartY(series, item);
		}

		@Override
		public Number getEndY(int series, int item) {
			return intensityValues[item] + MULTIPLIER * intensityErrors[item];
		}

		@Override
		public double getEndYValue(int series, int item) {
			return (double) getEndY(series, item);
		}

		@Override
		public int getSeriesCount() {
			return 1;
		}

		@Override
		public Comparable<String> getSeriesKey(int series) {
			return label;
		}
    }
}
