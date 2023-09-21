package org.palladiosimulator.dataflow.diagramgenerator.model;

public class DataFlow extends Flow {

	public DataFlow(DataFlowNode parent, DataFlowNode child) {
		super(parent, child);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object accept(FlowVisitor<?> visitor) {
		return visitor.visit(this);
	}

}
