package org.palladiosimulator.dataflow.diagramgenerator.dfd;

import org.palladiosimulator.dataflow.diagramgenerator.model.DataFlowElementVisitor;
import org.palladiosimulator.dataflow.diagramgenerator.model.DataStoreDataFlowElement;
import org.palladiosimulator.dataflow.diagramgenerator.model.ExternalEntityDataFlowElement;
import org.palladiosimulator.dataflow.diagramgenerator.model.ProcessDataFlowElement;

import mdpa.dfd.dataflowdiagram.External;
import mdpa.dfd.dataflowdiagram.Node;
import mdpa.dfd.dataflowdiagram.Process;
import mdpa.dfd.dataflowdiagram.Store;
import mdpa.dfd.dataflowdiagram.impl.dataflowdiagramFactoryImpl;

public class DFDDataFlowElementVisitor implements DataFlowElementVisitor<Node> {
	private dataflowdiagramFactoryImpl dfdFactory;

	public DFDDataFlowElementVisitor() {
		this.dfdFactory = new dataflowdiagramFactoryImpl();
	}

	@Override
	public Node visit(ProcessDataFlowElement element) {
		Process dfdProcess = (Process) this.dfdFactory.createProcess();
		dfdProcess.setEntityName(element.getName());
		dfdProcess.setId(element.getId());

		return dfdProcess;
	}

	@Override
	public Node visit(ExternalEntityDataFlowElement element) {
		External dfdExternal = (External) this.dfdFactory.createExternal();
		dfdExternal.setEntityName(element.getName());
		dfdExternal.setId(element.getId());

		return dfdExternal;
	}

	@Override
	public Node visit(DataStoreDataFlowElement element) {
		Store dfdStore = (Store) this.dfdFactory.createStore();
		dfdStore.setEntityName(element.getName());
		dfdStore.setId(element.getId());

		return dfdStore;
	}

}
