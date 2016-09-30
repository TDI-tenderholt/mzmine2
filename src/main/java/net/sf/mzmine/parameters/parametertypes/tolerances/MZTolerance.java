package net.sf.mzmine.parameters.parametertypes.tolerances;

import net.sf.mzmine.datamodel.DataPoint;
import net.sf.mzmine.parameters.ParameterSet;

import com.google.common.collect.Range;

public interface MZTolerance {
	public Range<Double> getToleranceRange(final double mzValue);
	public Range<Double> getToleranceRange(final DataPoint dataPoint);
	public Range<Double> getToleranceRange(final Range<Double> mzRange);
	public boolean checkWithinTolerance(final double mz1, final double mz2);
	public ParameterSet getParameterSet();
	public void updateFromParameterSet(ParameterSet parameterSet);
}
