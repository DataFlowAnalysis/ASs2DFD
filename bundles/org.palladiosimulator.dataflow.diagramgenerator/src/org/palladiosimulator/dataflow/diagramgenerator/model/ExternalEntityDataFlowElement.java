package org.palladiosimulator.dataflow.diagramgenerator.model;

public class ExternalEntityDataFlowElement extends DataFlowElement {

	public ExternalEntityDataFlowElement(String id, Boolean isCalling, boolean isViolation, String name) {
		super(id, isCalling, isViolation, name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object accept(DataFlowElementVisitor<?> visitor) {
		return visitor.visit(this);
	}

}
