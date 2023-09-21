package org.palladiosimulator.dataflow.diagramgenerator;

import org.palladiosimulator.dataflow.diagramgenerator.model.DrawingStrategy;

public interface DiagramGenerator<GP> {
	public void generateDataFlowDiagram(DrawingStrategy drawer, GP graphProcessor);
}
