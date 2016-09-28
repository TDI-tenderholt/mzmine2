package net.sf.mzmine.modules.rawdatamethods.peakpicking.massdetection.PeakInvestigator;

import net.sf.mzmine.datamodel.Scan;
import net.sf.mzmine.datamodel.impl.SimpleMassList;

public class PeakInvestigatorMassList extends SimpleMassList {

	public PeakInvestigatorMassList(String name, Scan scan, PeakInvestigatorDataPoint[] mzPeaks) {
		super(name, scan, mzPeaks);
	}

	@Override
	public PeakInvestigatorDataPoint[] getDataPoints() {
		return (PeakInvestigatorDataPoint[]) getDataPoints();
	}
}
