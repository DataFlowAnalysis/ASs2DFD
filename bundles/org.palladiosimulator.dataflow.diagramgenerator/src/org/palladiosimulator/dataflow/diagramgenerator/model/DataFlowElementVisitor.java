package org.palladiosimulator.dataflow.diagramgenerator.model;

public interface DataFlowElementVisitor<T> {
	T visit(ProcessDataFlowElement element);

	T visit(ExternalEntityDataFlowElement element);

	T visit(DataStoreDataFlowElement element);
}
