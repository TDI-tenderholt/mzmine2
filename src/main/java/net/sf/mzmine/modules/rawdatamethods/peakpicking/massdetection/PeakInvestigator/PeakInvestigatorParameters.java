/*
 * Copyright 2013-2016 Veritomyx, Inc.
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

package net.sf.mzmine.modules.rawdatamethods.peakpicking.massdetection.PeakInvestigator;

import java.awt.Window;
import java.io.IOException;
import java.lang.Math;
import java.util.Arrays;
import java.util.List;

import com.jcraft.jsch.JSchException;
import com.veritomyx.PeakInvestigatorSaaS;
import com.veritomyx.VeritomyxSettings;
import com.veritomyx.actions.Action.ResponseFormatException;
import com.veritomyx.actions.PiVersionsAction;

import net.sf.mzmine.modules.rawdatamethods.peakpicking.massdetection.MassDetectorSetupDialog;
import net.sf.mzmine.parameters.Parameter;
import net.sf.mzmine.parameters.impl.SimpleParameterSet;
import net.sf.mzmine.parameters.parametertypes.ComboParameter;
import net.sf.mzmine.parameters.parametertypes.IntegerParameter;
import net.sf.mzmine.parameters.parametertypes.selectors.RawDataFilesSelection;
import net.sf.mzmine.parameters.parametertypes.selectors.RawDataFilesSelectionType;
import net.sf.mzmine.util.ExitCode;
import net.sf.mzmine.util.dialogs.DefaultDialogFactory;
import net.sf.mzmine.util.dialogs.interfaces.DialogFactory;
import net.sf.mzmine.util.dialogs.interfaces.BasicDialog;
import net.sf.mzmine.main.MZmineCore;
import net.sf.mzmine.datamodel.RawDataFile;
import net.sf.mzmine.datamodel.Scan;
import net.sf.mzmine.desktop.preferences.MZminePreferences;

public class PeakInvestigatorParameters extends SimpleParameterSet
{
	private final static int BAD_CREDENTIALS_ERROR_CODE = 3;
	private static DialogFactory dialogFactory = new DefaultDialogFactory();

	public final static String LAST_USED_STRING = "lastUsed";

	public static final ComboParameter<String> versions = new ComboParameter<String>(
			"PeakInvestigator™ version",
			"The PeakInvestigator version to use for the analysis.",
			new String[] { LAST_USED_STRING });
	public static final IntegerParameter startMass = new IntegerParameter(
		    "Start m/z",
		    "The nominal starting m/z in the data to be used for analysis.",
		    0);
	public static final IntegerParameter endMass = new IntegerParameter(
		    "End m/z",
		    "The nominal ending m/z in the data to be used for analysis.",
		    Integer.MAX_VALUE);

	public static final CalibrationFilesParameter calibrationScans = new CalibrationFilesParameter(
			new RawDataFilesSelection(RawDataFilesSelectionType.SPECIFIC_FILES));

	public PeakInvestigatorParameters() {
		super(new Parameter[] { versions, calibrationScans, startMass, endMass });

		versions.setValue("lastUsed");
	}

	public static void setDialogFactory(DialogFactory headlessDialogFactory) {
		PeakInvestigatorParameters.dialogFactory = headlessDialogFactory;
	}

	public ExitCode showSetupDialog(Window parent, boolean valueCheckRequired) {
		PiVersionsAction action = null;
		try {
			if (System.getProperty("PeakInvestigatorParameters.noWeb") != null) {
				action = simulatePiVersionsCall(null);
			} else {
				action = performPiVersionsCall(MZmineCore.getConfiguration()
						.getPreferences());
			}
		} catch (ResponseFormatException | JSchException | IOException e) {
			e.printStackTrace();
			return ExitCode.ERROR;
		}

		if (action == null) {
			return ExitCode.ERROR;
		}

		versions.setChoices(formatPiVersions(action));
		setupMassParamters();

		MassDetectorSetupDialog dialog = new MassDetectorSetupDialog(parent,
				valueCheckRequired, PeakInvestigatorDetector.class, this);
		dialog.setVisible(true);

		return dialog.getExitCode();
	}

	private void setupMassParamters() {
		RawDataFile[] files = MZmineCore.getProjectManager()
				.getCurrentProject().getDataFiles();
		int[] massRange = determineMassRangeFromData(files);

		startMass.setValue(massRange[0]);
		startMass.setMinMax(massRange[0], massRange[1] - 1);

		endMass.setValue(massRange[1]);
		endMass.setMinMax(massRange[0] + 1, massRange[1]);
	}

	/**
	 * Convenience function to determine the encompassing mass range in scans
	 * across files.
	 * 
	 * @param files
	 *            Array of RawDataFiles.
	 * @return int[2] massRange: lower and upper m/z
	 */
	protected static int[] determineMassRangeFromData(RawDataFile[] files) {
		int[] massRange = new int[] { Integer.MAX_VALUE, 0 };

		if (files.length == 0) {
			return massRange;
		}

		for (RawDataFile file : files) {
			int[] scanNumbers = file.getScanNumbers();
			for (int scanNum : scanNumbers) {
				Scan scan = file.getScan(scanNum);

				// determine minimum value
				int value = (int) Math.floor(scan.getDataPointMZRange()
						.lowerEndpoint());
				if (value < massRange[0]) {
					massRange[0] = value;
				}

				value = (int) Math.ceil(scan.getDataPointMZRange()
						.upperEndpoint());
				if (value > massRange[1]) {
					massRange[1] = value;
				}

			}
		}

		return massRange;
	}

	/**
	 * Convenience function to create a list of versions that identifies which,
	 * if any, are the current versions and previously used versions.
	 * 
	 * @param action
	 *            Valid PiVersionsAction that contains list of versions.
	 * @return List of versions with identifications appended
	 */
	protected static String[] formatPiVersions(PiVersionsAction action) {
		List<String> choices = Arrays.asList(action.getVersions());
		int currentIndex = choices.indexOf((String) action.getCurrentVersion());
		int lastIndex = choices.indexOf((String) action.getLastUsedVersion());
		if (currentIndex >= 0 && lastIndex == currentIndex) {
			String newString = choices.get(currentIndex)
					+ " (current and last used)";
			choices.set(currentIndex, newString);
		} else {
			if (currentIndex >= 0) {
				String newString = choices.get(currentIndex) + " (current)";
				choices.set(currentIndex, newString);
			}
			if (lastIndex >= 0) {
				String newString = choices.get(lastIndex) + " (last used)";
				choices.set(lastIndex, newString);
			}
		}

		return choices.toArray(new String[choices.size()]);
	}

	/**
	 * Make a call into PI_VERSIONS API to have a list of PeakInvestigator
	 * versions available. This also checks that credentials are correct. This
	 * uses the version of the method that takes more parameters, using sensible
	 * defaults.
	 * 
	 * @param preferences
	 *            A MZminePreferences object to get Veritomyx credentials.
	 * 
	 * @return An object containing response from server, or null if credentials
	 *         are wrong and not corrected by the user.
	 * @throws ResponseFormatException
	 * @throws JSchException 
	 * @throws IOException 
	 */
	public static PiVersionsAction performPiVersionsCall(
			MZminePreferences preferences) throws ResponseFormatException,
			JSchException, IOException {

		VeritomyxSettings settings = preferences.getVeritomyxSettings();
		PeakInvestigatorSaaS webService = new PeakInvestigatorSaaS(
				settings.server);
		return performPiVersionsCall(preferences, webService, MZmineCore
				.getDesktop().getMainWindow());
	}

	/**
	 * Make a call into PI_VERSIONS API to have a list of PeakInvestigator
	 * versions available. This also checks that credentials are correct. This
	 * function takes more arguments than necessary to make it testable.
	 * 
	 * @param preferences
	 *            A MZminePreferences instance to get Veritomyx credentials
	 *            from, and display dialog if credentials are incorrect.
	 * @param webService
	 *            A PeakInvestigatorSaaS instance for making calls to the public
	 *            API
	 * @param widnow
	 *            A generic window object (hack to make testable)
	 * 
	 * @return An object containing response from server, or null if credentials
	 *         are wrong and not corrected by the user.
	 * @throws ResponseFormatException
	 * @throws IOException 
	 */
	protected static PiVersionsAction performPiVersionsCall(
			MZminePreferences preferences, PeakInvestigatorSaaS webService,
			Window window) throws ResponseFormatException, IOException {

		VeritomyxSettings settings = preferences.getVeritomyxSettings();
		PiVersionsAction action = new PiVersionsAction(settings.username,
				settings.password);

		String response = webService.executeAction(action);
		action.processResponse(response);
		if (!action.isReady("PI_VERSIONS")) {
			return null;
		}

		while (action.hasError()) {
			BasicDialog dialog = dialogFactory.createDialog();
			dialog.displayErrorMessage(action.getErrorMessage(), null);

			long code = action.getErrorCode();
			// Check if error is credentials problem
			if (code != BAD_CREDENTIALS_ERROR_CODE) {
				return null;
			}

			if (preferences.showSetupDialog(window, false) != ExitCode.OK)
				return null;

			settings = preferences.getVeritomyxSettings();

			action = new PiVersionsAction(settings.username, settings.password);
			response = webService.executeAction(action);
			action.processResponse(response);
		}

		return action;
	}

	public static PiVersionsAction simulatePiVersionsCall(
			MZminePreferences preferences) throws ResponseFormatException {

		PiVersionsAction action = new PiVersionsAction("username", "password");
		action.processResponse("{\"Action\":\"PI_VERSIONS\", \"Current\":\"simulated\", \"LastUsed\":\"\", \"Count\":1, \"Versions\":[\"simulated\"]}");
		return action;
	}

	public String getPiVersion() {
		String version = getParameter(versions).getValue();
		if (version.contains("(")) {
			int index = version.indexOf("(");
			return version.substring(0, index - 1);
		}

		return version;
	}

	public int[] getMassRange() {
		int startMassValue = getParameter(startMass).getValue();
		int endMassValue = getParameter(endMass).getValue();

		RawDataFile[] files = MZmineCore.getProjectManager()
				.getCurrentProject().getDataFiles();
		int[] dataMassRange = determineMassRangeFromData(files);

		// if we still have default values, then set to data ranges
		if (startMassValue == 0) {
			startMassValue = dataMassRange[0];
		}

		if (endMassValue == Integer.MAX_VALUE) {
			endMassValue = dataMassRange[1];
		}

		return new int[] { dataMassRange[0], dataMassRange[1],
				startMass.getValue(), endMass.getValue() };
	}

}