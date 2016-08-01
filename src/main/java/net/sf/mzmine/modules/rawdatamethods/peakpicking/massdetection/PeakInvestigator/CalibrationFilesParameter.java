package net.sf.mzmine.modules.rawdatamethods.peakpicking.massdetection.PeakInvestigator;

import java.util.Collection;

import net.sf.mzmine.parameters.parametertypes.selectors.RawDataFilesParameter;
import net.sf.mzmine.parameters.parametertypes.selectors.RawDataFilesSelection;

public class CalibrationFilesParameter extends RawDataFilesParameter {

	public CalibrationFilesParameter(RawDataFilesSelection rawDataFilesSelection) {
		super(rawDataFilesSelection);
	}

	@Override
	public String getName() {
		return "Calibration files";
	}

    @Override
    public String getDescription() {
        return "Raw data files that this module will take as its input.";
    }

    @Override
    public RawDataFilesParameter cloneParameter() {
        RawDataFilesParameter copy = new CalibrationFilesParameter(getValue());
        return copy;
    }

    @Override
    public boolean checkValue(Collection<String> errorMessages) {
    	return true;
    }

}
