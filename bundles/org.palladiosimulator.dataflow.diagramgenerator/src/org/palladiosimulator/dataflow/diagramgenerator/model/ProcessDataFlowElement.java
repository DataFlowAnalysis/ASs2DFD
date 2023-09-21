package org.palladiosimulator.dataflow.diagramgenerator.model;

public class ProcessDataFlowElement extends DataFlowElement {

	public ProcessDataFlowElement(String id, Boolean isCalling, boolean isViolation, String name) {
		super(id, isCalling, isViolation, name);
	}

	@Override
	public Object accept(DataFlowElementVisitor<?> visitor) {
		return visitor.visit(this);
	}
}
