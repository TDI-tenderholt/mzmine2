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

package net.sf.mzmine.desktop.impl.projecttree;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import net.sf.mzmine.datamodel.MZmineProject;
import net.sf.mzmine.datamodel.MassList;
import net.sf.mzmine.datamodel.PeakList;
import net.sf.mzmine.datamodel.PeakListRow;
import net.sf.mzmine.datamodel.RawDataFile;
import net.sf.mzmine.datamodel.Scan;
import net.sf.mzmine.datamodel.impl.RemoteJob;
import net.sf.mzmine.main.MZmineCore;
import net.sf.mzmine.modules.peaklistmethods.orderpeaklists.OrderPeakListsModule;
import net.sf.mzmine.modules.peaklistmethods.orderpeaklists.OrderPeakListsParameters;
import net.sf.mzmine.modules.rawdatamethods.orderdatafiles.OrderDataFilesModule;
import net.sf.mzmine.modules.rawdatamethods.orderdatafiles.OrderDataFilesParameters;
import net.sf.mzmine.modules.rawdatamethods.peakpicking.massdetection.MassDetectionModule;
import net.sf.mzmine.modules.rawdatamethods.peakpicking.massdetection.MassDetectionParameters;
import net.sf.mzmine.modules.visualization.infovisualizer.InfoVisualizerModule;
import net.sf.mzmine.modules.visualization.msms.MsMsVisualizerModule;
import net.sf.mzmine.modules.visualization.peaklisttable.PeakListTableModule;
import net.sf.mzmine.modules.visualization.peaksummary.PeakSummaryVisualizerModule;
import net.sf.mzmine.modules.visualization.scatterplot.ScatterPlotVisualizerModule;
import net.sf.mzmine.modules.visualization.spectra.SpectraVisualizerModule;
import net.sf.mzmine.modules.visualization.spectra.SpectraVisualizerParameters;
import net.sf.mzmine.modules.visualization.spectra.SpectraVisualizerWindow;
import net.sf.mzmine.modules.visualization.spectra.datasets.MassListDataSet;
import net.sf.mzmine.modules.visualization.threed.ThreeDVisualizerModule;
import net.sf.mzmine.modules.visualization.tic.TICVisualizerModule;
import net.sf.mzmine.modules.visualization.twod.TwoDVisualizerModule;
import net.sf.mzmine.parameters.ParameterSet;
import net.sf.mzmine.parameters.parametertypes.selectors.PeakListsSelectionType;
import net.sf.mzmine.parameters.parametertypes.selectors.RawDataFilesSelectionType;
import net.sf.mzmine.taskcontrol.Task;
import net.sf.mzmine.util.ExitCode;
import net.sf.mzmine.util.GUIUtils;

import org.apache.commons.io.FilenameUtils;

/**
 * This class handles pop-up menus and double click events in the project tree
 */
public class ProjectTreeMouseHandler extends MouseAdapter implements
        ActionListener {

    private ProjectTree tree;
    private JPopupMenu dataFilePopupMenu, peakListPopupMenu, scanPopupMenu,
            massListPopupMenu, peakListRowPopupMenu, jobPopupMenu;
    private Object rightClickObj;

    /**
     * Constructor
     */
    public ProjectTreeMouseHandler(ProjectTree tree) {

        this.tree = tree;

        dataFilePopupMenu = new JPopupMenu();

        GUIUtils.addMenuItem(dataFilePopupMenu, "Show TIC", this, "SHOW_TIC");
        GUIUtils.addMenuItem(dataFilePopupMenu, "Show mass spectrum", this,
                "SHOW_SPECTRUM");
        GUIUtils.addMenuItem(dataFilePopupMenu, "Show 2D visualizer", this,
                "SHOW_2D");
        GUIUtils.addMenuItem(dataFilePopupMenu, "Show 3D visualizer", this,
                "SHOW_3D");
        GUIUtils.addMenuItem(dataFilePopupMenu, "Peak/mass detection", this,
                "MASS_DETECTION");
        GUIUtils.addMenuItem(dataFilePopupMenu, "Show MS/MS visualizer", this,
                "SHOW_IDA");
        GUIUtils.addMenuItem(dataFilePopupMenu, "Sort alphabetically", this,
                "SORT_FILES");
        GUIUtils.addMenuItem(dataFilePopupMenu, "Remove file extension", this,
                "REMOVE_EXTENSION");
        GUIUtils.addMenuItem(dataFilePopupMenu, "Remove", this, "REMOVE_FILE");

        jobPopupMenu = new JPopupMenu();
        GUIUtils.addMenuItem(jobPopupMenu, "Retrieve job results", this,
                "RETRIEVE_JOB");

        scanPopupMenu = new JPopupMenu();

        GUIUtils.addMenuItem(scanPopupMenu, "Show scan", this, "SHOW_SCAN");

        massListPopupMenu = new JPopupMenu();

        GUIUtils.addMenuItem(massListPopupMenu, "Show mass list", this,
                "SHOW_MASSLIST");

        GUIUtils.addMenuItem(massListPopupMenu, "Remove mass list", this,
                "REMOVE_MASSLIST");
        GUIUtils.addMenuItem(massListPopupMenu,
                "Remove all mass lists with this name", this,
                "REMOVE_ALL_MASSLISTS");

        peakListPopupMenu = new JPopupMenu();

        GUIUtils.addMenuItem(peakListPopupMenu, "Show peak list table", this,
                "SHOW_PEAKLIST_TABLES");
        GUIUtils.addMenuItem(peakListPopupMenu, "Show peak list info", this,
                "SHOW_PEAKLIST_INFO");
        GUIUtils.addMenuItem(peakListPopupMenu, "Show scatter plot", this,
                "SHOW_SCATTER_PLOT");
        GUIUtils.addMenuItem(peakListPopupMenu, "Sort alphabetically", this,
                "SORT_PEAKLISTS");
        GUIUtils.addMenuItem(peakListPopupMenu, "Remove", this,
                "REMOVE_PEAKLIST");

        peakListRowPopupMenu = new JPopupMenu();

        GUIUtils.addMenuItem(peakListRowPopupMenu, "Show peak summary", this,
                "SHOW_PEAK_SUMMARY");

    }

    public void actionPerformed(ActionEvent e) {

        String command = e.getActionCommand();

        // Actions for raw data files

        if (command.equals("SHOW_TIC")) {
            RawDataFile[] selectedFiles = getObjList(RawDataFile.class);
            TICVisualizerModule.setupNewTICVisualizer(selectedFiles);
        }

        if (command.equals("SHOW_SPECTRUM")) {
            RawDataFile[] selectedFiles = getObjList(RawDataFile.class);
            SpectraVisualizerModule module = MZmineCore
                    .getModuleInstance(SpectraVisualizerModule.class);
            ParameterSet parameters = MZmineCore.getConfiguration()
                    .getModuleParameters(SpectraVisualizerModule.class);
            parameters.getParameter(SpectraVisualizerParameters.dataFiles)
                    .setValue(RawDataFilesSelectionType.SPECIFIC_FILES,
                            selectedFiles);
            ExitCode exitCode = parameters.showSetupDialog(MZmineCore
                    .getDesktop().getMainWindow(), true);
            MZmineProject project = MZmineCore.getProjectManager()
                    .getCurrentProject();
            if (exitCode == ExitCode.OK)
                module.runModule(project, parameters, new ArrayList<Task>());
        }

        if (command.equals("SHOW_IDA")) {
            RawDataFile[] selectedFiles = getObjList(RawDataFile.class);
            if (selectedFiles.length == 0)
                return;
            MsMsVisualizerModule.showIDAVisualizerSetupDialog(selectedFiles[0]);

        }

        if (command.equals("SHOW_2D")) {
            RawDataFile[] selectedFiles = getObjList(RawDataFile.class);
            if (selectedFiles.length == 0)
                return;
            TwoDVisualizerModule.show2DVisualizerSetupDialog(selectedFiles[0]);
        }

        if (command.equals("SHOW_3D")) {
            RawDataFile[] selectedFiles = getObjList(RawDataFile.class);
            if (selectedFiles.length == 0)
                return;
            ThreeDVisualizerModule.setupNew3DVisualizer(selectedFiles[0]);
        }

        if (command.equals("MASS_DETECTION")) {
            RawDataFile file = (RawDataFile) rightClickObj;
            startJob(file, null);
        }

        if (command.equals("SORT_FILES")) {
            // save current selection
            TreePath savedSelection[] = tree.getSelectionPaths();
            RawDataFile selectedFiles[] = getObjList(RawDataFile.class);
            OrderDataFilesModule module = MZmineCore
                    .getModuleInstance(OrderDataFilesModule.class);
            ParameterSet params = MZmineCore.getConfiguration()
                    .getModuleParameters(OrderDataFilesModule.class);
            params.getParameter(OrderDataFilesParameters.dataFiles).setValue(
                    RawDataFilesSelectionType.SPECIFIC_FILES, selectedFiles);
            module.runModule(MZmineCore.getProjectManager().getCurrentProject(), params, new ArrayList<Task>());
            // restore selection
            tree.setSelectionPaths(savedSelection);
        }

        if (command.equals("REMOVE_EXTENSION")) {
            RawDataFile[] selectedFiles = getObjList(RawDataFile.class);
            for (RawDataFile file : selectedFiles) {
                file.setName(FilenameUtils.removeExtension(file.toString()));
            }
            tree.updateUI();
        }

        if (command.equals("REMOVE_FILE")) {
            RawDataFile[] selectedFiles = getObjList(RawDataFile.class);
            PeakList allPeakLists[] = MZmineCore.getProjectManager()
                    .getCurrentProject().getPeakLists();
            for (RawDataFile file : selectedFiles) {
                for (PeakList peakList : allPeakLists) {
                    if (peakList.hasRawDataFile(file)) {
                        String msg = "Cannot remove file " + file.getName()
                                + ", because it is present in the peak list "
                                + peakList.getName();
                        MZmineCore.getDesktop().displayErrorMessage(
                                MZmineCore.getDesktop().getMainWindow(), msg);
                        return;
                    }
                }
                MZmineCore.getProjectManager().getCurrentProject()
                        .removeFile(file);
            }
        }

        if (command.equals("RETRIEVE_JOB")) {
            for (RemoteJob job : getObjList(RemoteJob.class))
                startJob(job.getRawDataFile(), job);
        }

        // Actions for scans

        if (command.equals("SHOW_SCAN")) {
            Scan selectedScans[] = getObjList(Scan.class);
            for (Scan scan : selectedScans) {
                SpectraVisualizerModule.showNewSpectrumWindow(
                        scan.getDataFile(), scan.getScanNumber());
            }
        }

        if (command.equals("SHOW_MASSLIST")) {
            MassList selectedMassLists[] = getObjList(MassList.class);
            for (MassList massList : selectedMassLists) {
                Scan scan = massList.getScan();
                SpectraVisualizerWindow window = SpectraVisualizerModule
                        .showNewSpectrumWindow(scan.getDataFile(),
                                scan.getScanNumber());
                MassListDataSet dataset = new MassListDataSet(massList);
                window.addDataSet(dataset, Color.green);
            }
        }

        if (command.equals("REMOVE_MASSLIST")) {
            MassList selectedMassLists[] = tree
                    .getSelectedObjects(MassList.class);
            for (MassList massList : selectedMassLists) {
                Scan scan = massList.getScan();
                scan.removeMassList(massList);
            }
        }

        if (command.equals("REMOVE_ALL_MASSLISTS")) {
            MassList selectedMassLists[] = getObjList(MassList.class);
            for (MassList massList : selectedMassLists) {
                String massListName = massList.getName();
                RawDataFile dataFiles[] = MZmineCore.getProjectManager()
                        .getCurrentProject().getDataFiles();
                for (RawDataFile dataFile : dataFiles) {
                    int scanNumbers[] = dataFile.getScanNumbers();
                    for (int scanNum : scanNumbers) {
                        Scan scan = dataFile.getScan(scanNum);
                        MassList ml = scan.getMassList(massListName);
                        if (ml != null)
                            scan.removeMassList(ml);
                    }
                }
            }
        }

        // Actions for peak lists

        if (command.equals("SHOW_PEAKLIST_TABLES")) {
            PeakList[] selectedPeakLists = getObjList(PeakList.class);
            for (PeakList peakList : selectedPeakLists) {
                PeakListTableModule.showNewPeakListVisualizerWindow(peakList);
            }
        }

        if (command.equals("SHOW_PEAKLIST_INFO")) {
            PeakList[] selectedPeakLists = getObjList(PeakList.class);
            for (PeakList peakList : selectedPeakLists) {
                InfoVisualizerModule.showNewPeakListInfo(peakList);
            }
        }

        if (command.equals("SHOW_SCATTER_PLOT")) {
            PeakList[] selectedPeakLists = getObjList(PeakList.class);
            for (PeakList peakList : selectedPeakLists) {
                ScatterPlotVisualizerModule.showNewScatterPlotWindow(peakList);
            }
        }

        if (command.equals("SORT_PEAKLISTS")) {
            // save current selection
            TreePath savedSelection[] = tree.getSelectionPaths();
            PeakList selectedPeakLists[] = getObjList(PeakList.class);
            OrderPeakListsModule module = MZmineCore
                    .getModuleInstance(OrderPeakListsModule.class);
            ParameterSet params = MZmineCore.getConfiguration()
                    .getModuleParameters(OrderPeakListsModule.class);
            params.getParameter(OrderPeakListsParameters.peakLists)
                    .setValue(PeakListsSelectionType.SPECIFIC_PEAKLISTS,
                            selectedPeakLists);
            module.runModule(MZmineCore.getProjectManager().getCurrentProject(), params, new ArrayList<Task>());
            // restore selection
            tree.setSelectionPaths(savedSelection);
        }

        if (command.equals("REMOVE_PEAKLIST")) {
            PeakList[] selectedPeakLists = getObjList(PeakList.class);
            for (PeakList peakList : selectedPeakLists)
                MZmineCore.getProjectManager().getCurrentProject()
                        .removePeakList(peakList);
        }

        // Actions for peak list rows

        if (command.equals("SHOW_PEAK_SUMMARY")) {
            PeakListRow[] selectedRows = getObjList(PeakListRow.class);
            for (PeakListRow row : selectedRows) {
                PeakSummaryVisualizerModule.showNewPeakSummaryWindow(row);
            }
        }

    }

    public void mousePressed(MouseEvent e) {
        Object clickedObject = getClickedObject(e.getX(), e.getY());

        if (e.isPopupTrigger() && SwingUtilities.isRightMouseButton(e)) {
            rightClickObj = clickedObject; // save object in case nothing else
                                           // is selected
            handlePopupTriggerEvent(e, clickedObject);
        } else if (e.isPopupTrigger())
            handlePopupTriggerEvent(e, clickedObject);

        else if ((e.getClickCount() == 2)
                && (e.getButton() == MouseEvent.BUTTON1)) // left click
            handleDoubleClickEvent(clickedObject);

    }

    /**
     * Get a list of all selected elements of a given class. If none are
     * selected, assume the element that was last right clicked is selected.
     * 
     * @param objectClass
     * @return
     */
    @SuppressWarnings("unchecked")
    private <T> T[] getObjList(Class<T> objectClass) {
        T[] list = tree.getSelectedObjects(objectClass);
        if (list.length == 0) {
            list = Arrays.copyOf(list, 1);
            list[0] = (T) rightClickObj;
        }
        return list;
    }

    /**
     * Get the tree node user object that was clicked on.
     * 
     * @param e
     * @return
     */
    private Object getClickedObject(int x, int y) {
        TreePath clickedPath = tree.getPathForLocation(x, y);
        if (clickedPath == null)
            return null;
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) clickedPath
                .getLastPathComponent();
        return node.getUserObject();
    }

    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger())
            handlePopupTriggerEvent(e, getClickedObject(e.getX(), e.getY()));
    }

    private void handlePopupTriggerEvent(MouseEvent e, Object clickedObject) {
        if (clickedObject instanceof RawDataFile)
            dataFilePopupMenu.show(e.getComponent(), e.getX(), e.getY());
        if (clickedObject instanceof Scan)
            scanPopupMenu.show(e.getComponent(), e.getX(), e.getY());
        if (clickedObject instanceof MassList)
            massListPopupMenu.show(e.getComponent(), e.getX(), e.getY());
        if (clickedObject instanceof PeakList)
            peakListPopupMenu.show(e.getComponent(), e.getX(), e.getY());
        if (clickedObject instanceof PeakListRow)
            peakListRowPopupMenu.show(e.getComponent(), e.getX(), e.getY());
        if (clickedObject instanceof RemoteJob)
            jobPopupMenu.show(e.getComponent(), e.getX(), e.getY());
    }

    private void handleDoubleClickEvent(Object clickedObject) {

        if (clickedObject instanceof RawDataFile) {
            RawDataFile clickedFile = (RawDataFile) clickedObject;
            TICVisualizerModule.setupNewTICVisualizer(clickedFile);
        }

        if (clickedObject instanceof PeakList) {
            PeakList clickedPeakList = (PeakList) clickedObject;
            PeakListTableModule
                    .showNewPeakListVisualizerWindow(clickedPeakList);
        }

        if (clickedObject instanceof Scan) {
            Scan clickedScan = (Scan) clickedObject;
            SpectraVisualizerModule.showNewSpectrumWindow(
                    clickedScan.getDataFile(), clickedScan.getScanNumber());
        }

        if (clickedObject instanceof MassList) {
            MassList clickedMassList = (MassList) clickedObject;
            Scan clickedScan = clickedMassList.getScan();
            SpectraVisualizerWindow window = SpectraVisualizerModule
                    .showNewSpectrumWindow(clickedScan.getDataFile(),
                            clickedScan.getScanNumber());
            MassListDataSet dataset = new MassListDataSet(clickedMassList);
            window.addDataSet(dataset, Color.green);
        }

        if (clickedObject instanceof PeakListRow) {
            PeakListRow clickedPeak = (PeakListRow) clickedObject;
            PeakSummaryVisualizerModule.showNewPeakSummaryWindow(clickedPeak);
        }

        if (clickedObject instanceof RemoteJob) {
            RemoteJob job = (RemoteJob) clickedObject;
            startJob(job.getRawDataFile(), job);
        }

    }

    /**
     * Retrieve the given job from remote server
     * 
     * @param raw
     * @param job
     */
    private void startJob(RawDataFile raw, RemoteJob job) {
        MassDetectionModule module = MZmineCore
                .getModuleInstance(MassDetectionModule.class);
        MassDetectionParameters parameters = (MassDetectionParameters) MZmineCore
                .getConfiguration().getModuleParameters(
                        MassDetectionModule.class);
        ExitCode exitCode = parameters.setJobParams(raw, job); // set params for
                                                               // this job
        if (exitCode == ExitCode.OK) {
            ParameterSet parametersCopy = parameters.cloneParameterSet();
            ArrayList<Task> tasks = new ArrayList<Task>();
            module.runModule(
                    MZmineCore.getProjectManager().getCurrentProject(),
                    parametersCopy, tasks);
            MZmineCore.getTaskController().addTasks(tasks.toArray(new Task[0]));
            parameters.setName(""); // clear name field
        }
    }

}