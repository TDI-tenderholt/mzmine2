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

package net.sf.mzmine.modules.visualization.tic;

import net.sf.mzmine.datamodel.DataPoint;
import net.sf.mzmine.datamodel.Feature;
import net.sf.mzmine.datamodel.RawDataFile;
import net.sf.mzmine.desktop.preferences.MZminePreferences;
import net.sf.mzmine.modules.rawdatamethods.peakpicking.massdetection.PeakInvestigator.PeakInvestigatorDataPoint;

import org.jfree.data.xy.AbstractXYDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYDataset;

/**
 * Integrated peak area data set. Separate data set is created for every peak
 * shown in this visualizer window.
 */
public class PeakDataSet extends AbstractXYDataset {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final Feature peak;
    private final double[] retentionTimes;
    private final double[] intensities;
    private double[] intensityErrors = null;
    private final double[] mzValues;
    private final String name;
    private final int peakItem;

    /**
     * Create the data set.
     *
     * @param p
     *            the peak.
     * @param id
     *            peak identity to use as a label.
     */
    public PeakDataSet(final Feature p, final String id) {

	peak = p;
	name = id;

	final int[] scanNumbers = peak.getScanNumbers();
	final RawDataFile dataFile = peak.getDataFile();
	final int peakScanNumber = peak.getRepresentativeScanNumber();

	// Copy peak data.
	final int scanCount = scanNumbers.length;
	retentionTimes = new double[scanCount];
	intensities = new double[scanCount];
	mzValues = new double[scanCount];
	int peakIndex = -1;
	for (int i = 0; i < scanCount; i++) {

	    // Representative scan number?
	    final int scanNumber = scanNumbers[i];
	    if (peakIndex < 0 && scanNumber == peakScanNumber) {

		peakIndex = i;
	    }

	    // Copy RT and m/z.
	    retentionTimes[i] = dataFile.getScan(scanNumber).getRetentionTime();
	    final DataPoint dataPoint = peak.getDataPoint(scanNumber);
			if (dataPoint == null) {

				mzValues[i] = 0.0;
				intensities[i] = 0.0;

			} else {

				mzValues[i] = dataPoint.getMZ();
				intensities[i] = dataPoint.getIntensity();
				if (dataPoint instanceof PeakInvestigatorDataPoint) {
					if (intensityErrors == null) {
						intensityErrors = new double[scanCount];
					}
					intensityErrors[i] = ((PeakInvestigatorDataPoint) dataPoint)
							.getIntensityError();
				}
			}
	}

	peakItem = peakIndex;
    }

    /**
     * Create the data set - no label.
     *
     * @param p
     *            the peak.
     */
    public PeakDataSet(final Feature p) {
	this(p, null);
    }

    @Override
    public int getSeriesCount() {
	return 1;
    }

    @Override
    public Comparable<?> getSeriesKey(final int series) {
	return peak.toString();
    }

    @Override
    public int getItemCount(final int series) {
	return retentionTimes.length;
    }

    @Override
    public Number getX(final int series, final int item) {
	return retentionTimes[item];
    }

    @Override
    public Number getY(final int series, final int item) {
	return intensities[item];
    }

    public double getMZ(final int item) {
	return mzValues[item];
    }

    public String getName() {
	return name;
    }

    public boolean isPeak(final int item) {
	return item == peakItem;
    }

    public XYDataset getErrorBarDataSet() {
		if (intensityErrors != null) {
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
			return retentionTimes.length;
		}

		@Override
		public Number getX(int series, int item) {
			return retentionTimes[item];
		}

		@Override
		public Number getY(int series, int item) {
			return intensities[item];
		}

		@Override
		public Number getStartX(int series, int item) {
			return getX(series, item);
		}

		@Override
		public double getStartXValue(int series, int item) {
			return (double) getStartX(series, item);
		}

		@Override
		public Number getEndX(int series, int item) {
			return getX(series, item);
		}

		@Override
		public double getEndXValue(int series, int item) {
			return (double) getEndX(series, item);
		}

		@Override
		public Number getStartY(int series, int item) {
			return intensities[item] - MULTIPLIER * intensityErrors[item];
		}

		@Override
		public double getStartYValue(int series, int item) {
			return (double) getStartY(series, item);
		}

		@Override
		public Number getEndY(int series, int item) {
			return intensities[item] + MULTIPLIER * intensityErrors[item];
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
			return peak.toString();
		}
    	
    }
}
