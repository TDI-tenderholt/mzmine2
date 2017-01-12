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

import java.util.Collection;

import net.sf.mzmine.parameters.ParameterSet;
import net.sf.mzmine.parameters.UserParameter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class MZToleranceParameter implements
		UserParameter<MZTolerance, MZToleranceComponent> {

	private final MZTolerance[] mzTolerances;

	private String name, description;
	private MZTolerance value;

	public MZToleranceParameter() {
		this(new MZTolerance[] { new MaximumMZTolerance() });
	}

	public MZToleranceParameter(String name, String description) {
		this(name, description, new MZTolerance[] { new MaximumMZTolerance() });
	}

	public MZToleranceParameter(MZTolerance[] mzTolerances) {
		this(
				"m/z tolerance",
				"The allowed difference between two m/z values to be considered same.",
				mzTolerances);
	}

	public MZToleranceParameter(String name, String description, MZTolerance[] mzTolerances) {
		this.name = name;
		this.description = description;
		this.mzTolerances = mzTolerances;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public MZToleranceComponent createEditingComponent() {
		MZToleranceComponent component = new MZToleranceComponent(mzTolerances);
		component.setValue(value);
		return component;
	}

	@Override
	public MZToleranceParameter cloneParameter() {
		MZToleranceParameter copy = new MZToleranceParameter(name, description, mzTolerances);
		copy.setValue(this.getValue());
		return copy;
	}

	@Override
	public void setValueFromComponent(MZToleranceComponent component) {
		value = component.getValue();
	}

	@Override
	public void setValueToComponent(MZToleranceComponent component,
			MZTolerance newValue) {
		component.setValue(newValue);
	}

	@Override
	public MZTolerance getValue() {
		return value;
	}

	@Override
	public void setValue(MZTolerance newValue) {
		this.value = newValue;
	}

	@Override
	public void loadValueFromXML(Element xmlElement) {
		// Set some default values
		NodeList items = xmlElement.getElementsByTagName("type");
		if (items.getLength() != 1) {
			return;
		}

		String type = items.item(0).getTextContent();
		Class<?> clazz;
		try {
			clazz = Class.forName(type);
			value = (MZTolerance) clazz.newInstance();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			return;
		}

		ParameterSet parameters = value.getParameterSet();
		parameters.loadValuesFromXML(xmlElement);
		value.updateFromParameterSet(parameters);
	}

	@Override
	public void saveValueToXML(Element xmlElement) {
		if (value == null)
			return;

		Document parentDocument = xmlElement.getOwnerDocument();
		Element newElement = parentDocument.createElement("type");
		newElement.setTextContent(value.getClass().getName());
		xmlElement.appendChild(newElement);
		value.getParameterSet().saveValuesToXML(xmlElement);
	}

	@Override
	public boolean checkValue(Collection<String> errorMessages) {
		if (value == null) {
			errorMessages.add(name + " is not set properly");
			return false;
		}

		ParameterSet parameters = value.getParameterSet();
		return parameters.checkParameterValues(errorMessages);
	}

}
