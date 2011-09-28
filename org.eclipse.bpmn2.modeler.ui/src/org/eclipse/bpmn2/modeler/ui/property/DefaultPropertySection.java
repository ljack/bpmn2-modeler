/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 *  All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 *
 * @author Innar Made
 ******************************************************************************/
package org.eclipse.bpmn2.modeler.ui.property;

import java.io.IOException;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.bpmn2.di.impl.BPMNDiagramImpl;
import org.eclipse.bpmn2.modeler.core.ModelHandlerLocator;
import org.eclipse.bpmn2.modeler.core.features.BusinessObjectUtil;
import org.eclipse.bpmn2.modeler.ui.Activator;
import org.eclipse.bpmn2.modeler.ui.editor.BPMN2Editor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.platform.GFPropertySection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

public class DefaultPropertySection extends AbstractBpmn2PropertySection {

	private DefaultPropertiesComposite composite;

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);
		composite = new DefaultPropertiesComposite(parent, SWT.None);
	}

	@Override
	protected AbstractBpmn2PropertiesComposite getComposite() {
		return composite;
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
	}

	@Override
	public void refresh() {
		PictogramElement pe = getSelectedPictogramElement();
		if (pe != null) {
			EObject be = BusinessObjectUtil.getFirstElementOfType(pe, BaseElement.class,true);
			if (be==null) {
				// maybe it's the Diagram (editor canvas)?
				be = BusinessObjectUtil.getFirstElementOfType(pe, BPMNDiagram.class);
			}
			final BPMNShape shape = BusinessObjectUtil.getFirstElementOfType(pe, BPMNShape.class);
			final BPMN2Editor diagramEditor = (BPMN2Editor) getDiagramEditor();
			updateComposite(be, diagramEditor, shape);
			
			super.refresh();
		}
	}

	private void updateComposite(EObject be, BPMN2Editor diagramEditor, BPMNShape shape) {
		if (be instanceof BPMNDiagramImpl) {
			try {
				Resource eResource = be.eResource();
				if (eResource != null) {
					Definitions definitions = ModelHandlerLocator.getModelHandler(eResource).getDefinitions();
					getComposite().setShape(shape);
					getComposite().setEObject(diagramEditor, definitions);
				} else {
					getComposite().setShape(shape);
					getComposite().setEObject(diagramEditor, null);
				}
			} catch (IOException e) {
				Activator.showErrorWithLogging(e);
			}
		}
		else 
		{
			getComposite().setShape(shape);
			getComposite().setEObject(diagramEditor, be);
		}
	}
}