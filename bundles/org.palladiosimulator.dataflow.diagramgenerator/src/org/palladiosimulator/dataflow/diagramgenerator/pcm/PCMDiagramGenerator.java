package org.palladiosimulator.dataflow.diagramgenerator.pcm;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Plugin;
import org.palladiosimulator.dataflow.confidentiality.analysis.DataFlowConfidentialityAnalysis;
import org.palladiosimulator.dataflow.confidentiality.analysis.builder.DataFlowAnalysisBuilder;
import org.palladiosimulator.dataflow.confidentiality.analysis.builder.pcm.PCMDataFlowConfidentialityAnalysisBuilder;
import org.palladiosimulator.dataflow.confidentiality.analysis.entity.sequence.ActionSequence;
import org.palladiosimulator.dataflow.diagramgenerator.DiagramGenerator;
import org.palladiosimulator.dataflow.diagramgenerator.GeneratorOptions;
import org.palladiosimulator.dataflow.diagramgenerator.model.DataFlowNode;
import org.palladiosimulator.dataflow.diagramgenerator.model.DrawingStrategy;

import dev.abunai.impact.analysis.PCMUncertaintyImpactAnalysisBuilder;
import dev.abunai.impact.analysis.StandalonePCMUncertaintyImpactAnalysis;
import dev.abunai.impact.analysis.model.UncertaintyImpactCollection;
import dev.abunai.impact.analysis.model.impact.ConnectorUncertaintyImpact;
import dev.abunai.impact.analysis.model.impact.UncertaintyImpact;

/**
 * The StandaloneDiagramGenerator class is responsible for generating a data
 * flow diagram based on the provided GeneratorOptions.
 */
public class PCMDiagramGenerator implements DiagramGenerator<PCMGraphProcessor> {
	private GeneratorOptions options;
	private DataFlowConfidentialityAnalysis analysis;

	/**
	 * Constructs a StandaloneDiagramGenerator with the given GeneratorOptions.
	 * 
	 * @param options The GeneratorOptions for generating the data flow diagram.
	 * @throws IllegalArgumentException if the provided options are invalid.
	 */
	public PCMDiagramGenerator(GeneratorOptions options, DataFlowConfidentialityAnalysis analysis) {
		if (!options.isValid()) {
			throw new IllegalArgumentException("Invalid command line options. Aborting.");
		}
		this.options = options;
		this.analysis = analysis;
	}

	/**
	 * Generates the data flow diagram using the provided DrawingStrategy,
	 * DataFlowElementFactory, and DataFlowGraphProcessor.
	 * 
	 * @param drawer         The DrawingStrategy for generating the diagram.
	 * @param elementCreator The DataFlowElementFactory for creating data flow
	 *                       elements.
	 * @param graphProcessor The DataFlowGraphProcessor for processing action
	 *                       sequences.
	 */
	public void generateDataFlowDiagram(DrawingStrategy drawer, PCMGraphProcessor graphProcessor) {
		List<ActionSequence> actionSequences = getActionSequences();

		UncertaintyImpactCollection uncertaintyCollection = null;
		if (this.analysis instanceof StandalonePCMUncertaintyImpactAnalysis uncertaintyAnalysis) {
			uncertaintyCollection = uncertaintyAnalysis.propagate();
		}
		
		List<UncertaintyImpact<?>> uncertaintyImpacts = new ArrayList<>();
		if (uncertaintyCollection != null) {
			uncertaintyImpacts = uncertaintyCollection.getUncertaintyImpacts();
		}

		List<DataFlowNode> dataFlowNodes = graphProcessor.processActionSequences(actionSequences, analysis,
				uncertaintyImpacts);
		System.out.println("Model translation finished!");

		drawer.generate(dataFlowNodes);
		drawer.saveToDisk("output/data-flow.svg");
		System.out.println("Done!");
	}

	private List<ActionSequence> getActionSequences() {
		List<ActionSequence> actionSequences = analysis.findAllSequences();
		return analysis.evaluateDataFlows(actionSequences);
	}
}
