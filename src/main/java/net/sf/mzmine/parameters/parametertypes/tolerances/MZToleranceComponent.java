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

package net.sf.mzmine.parameters.parametertypes.tolerances;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.sf.mzmine.parameters.ParameterSet;
import net.sf.mzmine.parameters.dialogs.ParameterSetupDialog;
import net.sf.mzmine.util.ExitCode;

public class MZToleranceComponent extends JPanel implements ActionListener {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;

	private JComboBox<MZTolerance> comboBox;
	private JButton setButton;

	public MZToleranceComponent(MZTolerance[] mzTolerances) {

		super(new BorderLayout());

		setBorder(BorderFactory.createEmptyBorder(0, 9, 0, 0));

		if (mzTolerances == null || mzTolerances.length == 0) {
			throw new IllegalArgumentException(
					"Invalid number of MZTolerance's passed to MZToleranceComponent()");
		}

		comboBox = new JComboBox<MZTolerance>(mzTolerances);
		comboBox.addActionListener(this);
		add(comboBox, BorderLayout.CENTER);

		setButton = new JButton("...");
		setButton.addActionListener(this);
		setButton.setEnabled(true);
		add(setButton, BorderLayout.EAST);

	}

	public void setValue(MZTolerance value) {
		comboBox.setSelectedItem(value);
	}

	public MZTolerance getValue() {
		return (MZTolerance) comboBox.getSelectedItem();
	}

	@Override
	public void setToolTipText(String toolTip) {
		super.setToolTipText(toolTip);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		Object src = event.getSource();

		MZTolerance selected = (MZTolerance) comboBox.getSelectedItem();
		if (selected == null) {
			setButton.setEnabled(false);
			return;
		}

		ParameterSet parameterSet = selected.getParameterSet();

		if (src == comboBox) {
			setButton.setEnabled(parameterSet.getParameters().length > 0);
		}

		if (src == setButton) {
			ParameterSetupDialog dialog = (ParameterSetupDialog) SwingUtilities
					.getAncestorOfClass(ParameterSetupDialog.class, this);
			if (dialog == null)
				return;
			ExitCode code = parameterSet.showSetupDialog(dialog, dialog.isValueCheckRequired());
			if (code == ExitCode.OK) {
				selected.updateFromParameterSet(parameterSet);
			}
		}

	}

}
