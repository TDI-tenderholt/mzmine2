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
 * MZmine 2; if not, write to the Free Software Foundation, Inc., 51 Franklin
 * St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package net.sf.mzmine.parameters.parametertypes.tolerances;

import java.awt.Window;
import java.text.DecimalFormat;

import net.sf.mzmine.datamodel.DataPoint;
import net.sf.mzmine.parameters.Parameter;
import net.sf.mzmine.parameters.ParameterSet;
import net.sf.mzmine.parameters.impl.SimpleParameterSet;
import net.sf.mzmine.parameters.parametertypes.DoubleParameter;
import net.sf.mzmine.util.ExitCode;

import com.google.common.collect.Range;

/**
 * This class represents m/z tolerance. Tolerance is set using absolute (m/z)
 * and relative (ppm) values. The tolerance range is calculated as the maximum
 * of the absolute and relative values.
 */
public class MaximumMZTolerance implements MZTolerance {

    // PPM conversion factor.
    private static final double MILLION = 1000000.0;

    // Tolerance has absolute (in m/z) and relative (in ppm) values
    private Double mzTolerance;
    private Double ppmTolerance;

    public MaximumMZTolerance() {
		mzTolerance = MaximumMzToleranceParameters.mzTolerance.getValue();
		ppmTolerance = MaximumMzToleranceParameters.ppmTolerance.getValue();
    }

    public MaximumMZTolerance(double mzTolerance, double ppmTolerance) {
    	this.mzTolerance = mzTolerance;
    	this.ppmTolerance = ppmTolerance;
    	MaximumMzToleranceParameters.mzTolerance.setValue(mzTolerance);
    	MaximumMzToleranceParameters.ppmTolerance.setValue(ppmTolerance);
    }

    public double getMzTolerance() {
	return mzTolerance;
    }

    public double getPpmTolerance() {
	return ppmTolerance;
    }

    private double getMzToleranceForMass(final double mzValue) {
	return Math.max(mzTolerance, mzValue / MILLION * ppmTolerance);
    }

    public double getPpmToleranceForMass(final double mzValue) {
	return Math.max(ppmTolerance, mzTolerance / (mzValue / MILLION));
    }

    @Override
    public Range<Double> getToleranceRange(final double mzValue) {
	final double absoluteTolerance = getMzToleranceForMass(mzValue);
	return Range.closed(mzValue - absoluteTolerance, mzValue
		+ absoluteTolerance);
    }

	@Override
	public Range<Double> getToleranceRange(final DataPoint dataPoint) {
		return getToleranceRange(dataPoint.getMZ());
	}

    @Override
    public Range<Double> getToleranceRange(final Range<Double> mzRange) {
	return Range.closed(
		mzRange.lowerEndpoint()
			- getMzToleranceForMass(mzRange.lowerEndpoint()),
		mzRange.upperEndpoint()
			+ getMzToleranceForMass(mzRange.upperEndpoint()));
    }

    @Override
    public boolean checkWithinTolerance(final double mz1, final double mz2) {
	return getToleranceRange(mz1).contains(mz2);
    }

	@Override
	public ParameterSet getParameterSet() {
		return new MaximumMzToleranceParameters();
	}

	@Override
	public void updateFromParameterSet(ParameterSet parameterSet) {
		mzTolerance = parameterSet.getParameter(
				MaximumMzToleranceParameters.mzTolerance).getValue();
		ppmTolerance = parameterSet.getParameter(
				MaximumMzToleranceParameters.ppmTolerance).getValue();
	}

	@Override
	public String toString() {
		return String.format("Maximum of %s Da or %s ppm",
				mzTolerance, ppmTolerance);
	}

    public static class MaximumMzToleranceParameters extends SimpleParameterSet {
    	private static final DoubleParameter mzTolerance = new DoubleParameter(
				"mzTolerance", "The absolute (in m/z) tolerance",
				new DecimalFormat("0.0000"), 0.015, 0.0, Double.MAX_VALUE);
    	private static final DoubleParameter ppmTolerance = new DoubleParameter(
				"ppmTolerance", "The relative (in ppm) tolerance",
				new DecimalFormat("0.00"), 10.0, 0.0, Double.MAX_VALUE);

    	public MaximumMzToleranceParameters() {
    		super(new Parameter<?>[] { mzTolerance, ppmTolerance});
    	}

    	@Override
    	public ExitCode showSetupDialog(Window parent, boolean valueCheckRequired) {
    		return super.showSetupDialog(parent, valueCheckRequired);
    	}

		public MZTolerance getMzTolerance() {
			return new MaximumMZTolerance(mzTolerance.getValue(),
					ppmTolerance.getValue());
		}
    }

}
