package org.palladiosimulator.dataflow.diagramgenerator.model;

public class ControlFlow extends Flow {

	public ControlFlow(DataFlowNode parent, DataFlowNode child) {
		super(parent, child);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object accept(FlowVisitor<?> visitor) {
		return visitor.visit(this);
	}

}
