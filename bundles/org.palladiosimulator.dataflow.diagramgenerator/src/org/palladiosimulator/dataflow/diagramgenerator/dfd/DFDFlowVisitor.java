package org.palladiosimulator.dataflow.diagramgenerator.dfd;

import org.palladiosimulator.dataflow.diagramgenerator.model.ControlFlow;
import org.palladiosimulator.dataflow.diagramgenerator.model.DataFlow;
import org.palladiosimulator.dataflow.diagramgenerator.model.FlowVisitor;

import mdpa.dfd.dataflowdiagram.Flow;
import mdpa.dfd.dataflowdiagram.impl.dataflowdiagramFactoryImpl;

public class DFDFlowVisitor implements FlowVisitor<Flow> {
	private dataflowdiagramFactoryImpl dfdFactory;

	public DFDFlowVisitor() {
		this.dfdFactory = new dataflowdiagramFactoryImpl();
	}

	@Override
	public Flow visit(ControlFlow flow) {
		// Control Flow is not supported by the DFD-Metamodel
		return null;
	}

	@Override
	public Flow visit(DataFlow flow) {
		Flow newFlow = this.dfdFactory.createFlow();

		return newFlow;
	}

}
