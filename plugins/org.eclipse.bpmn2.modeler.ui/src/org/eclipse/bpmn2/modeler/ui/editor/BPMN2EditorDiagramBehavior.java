/*******************************************************************************
 * Copyright (c) 2011, 2012, 2013 Red Hat, Inc.
 * All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.bpmn2.modeler.ui.editor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.editor.DefaultMarkerBehavior;
import org.eclipse.graphiti.ui.editor.DefaultPersistencyBehavior;
import org.eclipse.graphiti.ui.editor.DefaultUpdateBehavior;
import org.eclipse.graphiti.ui.editor.DiagramBehavior;
import org.eclipse.graphiti.ui.editor.DiagramEditorContextMenuProvider;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPart;

public class BPMN2EditorDiagramBehavior extends DiagramBehavior {

	BPMN2Editor bpmn2Editor;
	
	public BPMN2EditorDiagramBehavior(BPMN2Editor bpmn2Editor) {
		super(bpmn2Editor);
		this.bpmn2Editor = bpmn2Editor;
		setParentPart((IWorkbenchPart) bpmn2Editor);
		initDefaultBehaviors();
	}
	
	@Override
	protected DefaultUpdateBehavior createUpdateBehavior() {
		return new BPMN2EditorUpdateBehavior(this);
	}
	
    @Override
    protected DefaultPersistencyBehavior createPersistencyBehavior() {
    	return new BPMN2PersistencyBehavior(this);
    }
    
	protected DefaultMarkerBehavior createMarkerBehavior() {
		return new BPMN2EditorMarkerBehavior(this);
	}
	
	@Override
	protected PictogramElement[] getPictogramElementsForSelection() {
		// filter out invisible elements when setting selection
		PictogramElement[] pictogramElements = super.getPictogramElementsForSelection();
		if (pictogramElements==null)
			return null;
		ArrayList<PictogramElement> visibleList = new ArrayList<PictogramElement>();
		for (PictogramElement pe : pictogramElements) {
			if (pe.isVisible())
				visibleList.add(pe);
		}
		return visibleList.toArray(new PictogramElement[visibleList.size()]);
	}

	@Override
	protected void selectPictogramElements(PictogramElement[] pictogramElements) {
		// Avoid NPE when a final selection comes in from the Outline Viewer AFTER
		// the editor is closed and the workbench is shutting down.
		Control control = getDiagramContainer().getGraphicalViewer().getControl();
		if (control==null || control.isDisposed())
			return;
		super.selectPictogramElements(pictogramElements);
	}

	@Override
	protected ContextMenuProvider createContextMenuProvider() {
		return new DiagramEditorContextMenuProvider(getDiagramContainer().getGraphicalViewer(),
				getDiagramContainer().getActionRegistry(),
				getConfigurationProvider()) {
			@Override
			public void buildContextMenu(IMenuManager manager) {
				super.buildContextMenu(manager);
				IAction action = getDiagramContainer().getActionRegistry().getAction("show.or.hide.source.view"); //$NON-NLS-1$
				action.setText(action.getText());
				manager.add(action);

				int pageIndex = bpmn2Editor.getMultipageEditor().getActivePage();
				int lastPage = bpmn2Editor.getMultipageEditor().getDesignPageCount();
				if (pageIndex > 0 && pageIndex < lastPage) {
					action = getDiagramContainer().getActionRegistry().getAction("delete.page"); //$NON-NLS-1$
					action.setText(action.getText());
					action.setEnabled(action.isEnabled());
					manager.add(action);
				}

				action = getDiagramContainer().getActionRegistry().getAction("show.property.view"); //$NON-NLS-1$
				action.setText(action.getText());
				manager.add(action);

//				action = getDiagramContainer().getActionRegistry().getAction("show.hide.elements"); //$NON-NLS-1$
//				action.setText(action.getText());
//				manager.add(action);
			}
		};
	}
}
