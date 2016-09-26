package net.sf.mzmine.parameters.parametertypes.tolerances;

import com.google.common.collect.Range;

public interface MZTolerance {
	Range<Double> getToleranceRange(final double mzValue);
	Range<Double> getToleranceRange(final Range<Double> mzRange);
	boolean checkWithinTolerance(final double mz1, final double mz2);
}
