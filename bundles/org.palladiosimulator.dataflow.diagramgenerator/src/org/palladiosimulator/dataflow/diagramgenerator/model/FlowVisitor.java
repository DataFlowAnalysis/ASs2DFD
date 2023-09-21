package org.palladiosimulator.dataflow.diagramgenerator.model;

public interface FlowVisitor<T> {
	T visit(ControlFlow flow);

	T visit(DataFlow flow);
}
