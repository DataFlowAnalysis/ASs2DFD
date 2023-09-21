package org.palladiosimulator.dataflow.diagramgenerator.model;

import java.util.List;

public interface DrawingStrategy {
	void generate(List<DataFlowNode> dataFlowElements);
	boolean saveToDisk(String path);
}
