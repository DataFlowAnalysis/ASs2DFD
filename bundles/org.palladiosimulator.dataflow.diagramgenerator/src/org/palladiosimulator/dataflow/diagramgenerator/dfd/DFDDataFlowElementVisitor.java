package org.palladiosimulator.dataflow.diagramgenerator.dfd;

import org.palladiosimulator.dataflow.diagramgenerator.model.DataFlowElementVisitor;
import org.palladiosimulator.dataflow.diagramgenerator.model.DataStoreDataFlowElement;
import org.palladiosimulator.dataflow.diagramgenerator.model.ExternalEntityDataFlowElement;
import org.palladiosimulator.dataflow.diagramgenerator.model.ProcessDataFlowElement;

import mdpa.dfd.dataflowdiagram.impl.ExternalImpl;
import mdpa.dfd.dataflowdiagram.impl.NodeImpl;
import mdpa.dfd.dataflowdiagram.impl.ProcessImpl;
import mdpa.dfd.dataflowdiagram.impl.StoreImpl;
import mdpa.dfd.dataflowdiagram.impl.dataflowdiagramFactoryImpl;

public class DFDDataFlowElementVisitor implements DataFlowElementVisitor<NodeImpl> {
	private dataflowdiagramFactoryImpl dfdFactory;

	public DFDDataFlowElementVisitor() {
		this.dfdFactory = new dataflowdiagramFactoryImpl();
	}

	@Override
	public NodeImpl visit(ProcessDataFlowElement element) {
		ProcessImpl dfdProcess = (ProcessImpl) this.dfdFactory.createProcess();
		dfdProcess.setEntityName(element.getName());
		dfdProcess.setId(element.getId());

		return dfdProcess;
	}

	@Override
	public NodeImpl visit(ExternalEntityDataFlowElement element) {
		ExternalImpl dfdExternal = (ExternalImpl) this.dfdFactory.createExternal();
		dfdExternal.setEntityName(element.getName());
		dfdExternal.setId(element.getId());
		
		return dfdExternal;
	}

	@Override
	public NodeImpl visit(DataStoreDataFlowElement element) {
		StoreImpl dfdStore = (StoreImpl) this.dfdFactory.createStore();
		dfdStore.setEntityName(element.getName());
		dfdStore.setId(element.getId());
		
		return dfdStore;
	}

}
