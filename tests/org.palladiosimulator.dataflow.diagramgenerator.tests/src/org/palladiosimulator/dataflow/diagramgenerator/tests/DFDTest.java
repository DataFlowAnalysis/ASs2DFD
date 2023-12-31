package org.palladiosimulator.dataflow.diagramgenerator.tests;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;
import org.palladiosimulator.dataflow.confidentiality.analysis.DataFlowConfidentialityAnalysis;
import org.palladiosimulator.dataflow.confidentiality.analysis.builder.DataFlowAnalysisBuilder;
import org.palladiosimulator.dataflow.confidentiality.analysis.builder.pcm.PCMDataFlowConfidentialityAnalysisBuilder;
import org.palladiosimulator.dataflow.confidentiality.analysis.entity.sequence.AbstractActionSequenceElement;
import org.palladiosimulator.dataflow.diagramgenerator.GeneratorOptions;
import org.palladiosimulator.dataflow.diagramgenerator.dfd.DFDDrawingStrategy;
import org.palladiosimulator.dataflow.diagramgenerator.pcm.PCMDataFlowElementFactory;
import org.palladiosimulator.dataflow.diagramgenerator.pcm.PCMDiagramGenerator;
import org.palladiosimulator.dataflow.diagramgenerator.pcm.PCMGraphProcessor;
import org.palladiosimulator.dataflow.diagramgenerator.testmodels.Activator;

import dev.abunai.impact.analysis.PCMUncertaintyImpactAnalysisBuilder;
import dev.abunai.impact.analysis.StandalonePCMUncertaintyImpactAnalysis;

public class DFDTest {
	public static String PROJECT_NAME = "org.palladiosimulator.dataflow.diagramgenerator.testmodels";
	public static String USAGE_MODEL_PATH = "models/TravelPlannerNew/travelPlanner.usagemodel";
	public static String ALLOCATION_PATH = "models/TravelPlannerNew/travelPlanner.allocation";
	public static String CHARACTERISTICS_PATH = "models/TravelPlannerNew/travelPlanner.nodecharacteristics";

	@Test
	void runGeneratorWithModel() {
		GeneratorOptions options = GeneratorOptions.getInstance();
		options.setProjectName(PROJECT_NAME);
		options.setUsageModelPath(USAGE_MODEL_PATH);
		options.setAllocationPath(ALLOCATION_PATH);
		options.setCharacteristicsPath(CHARACTERISTICS_PATH);
		options.setDrawControlFlow(false); // IMPORTANT!!!
		options.setDrawNodeCharacteristics(true);
		options.setDrawVariables(true);
		options.setDrawParameters(true);
		options.setDrawViolations(true);
		options.setDrawUncertainty(true);

		String projectName = options.getProjectName();
		String usageModelPath = options.getUsageModelPath();
		String allocationPath = options.getAllocationPath();
		String characteristicsPath = options.getCharacteristicsPath();

		DataFlowConfidentialityAnalysis analysis = new DataFlowAnalysisBuilder().standalone()
				.modelProjectName(PROJECT_NAME).useBuilder(new PCMDataFlowConfidentialityAnalysisBuilder())
				.usePluginActivator(Activator.class).useUsageModel(USAGE_MODEL_PATH).useAllocationModel(ALLOCATION_PATH)
				.useNodeCharacteristicsModel(CHARACTERISTICS_PATH).build();

		try {
			analysis.initializeAnalysis();
		} catch (Exception e) {
			analysis = new DataFlowAnalysisBuilder().standalone().modelProjectName(PROJECT_NAME)
					.useBuilder(new PCMDataFlowConfidentialityAnalysisBuilder()).legacy() // DARUM HIER MIT LEGACY
					.usePluginActivator(Activator.class).useUsageModel(USAGE_MODEL_PATH)
					.useAllocationModel(ALLOCATION_PATH).useNodeCharacteristicsModel(CHARACTERISTICS_PATH).build();
			analysis.initializeAnalysis();
		}

		// analysis.getUncertaintySources().addConnectorUncertaintyInConnector("_w-qoYLNzEe2o46d27a6tVQ");
		// // S1_1
		// analysis.getUncertaintySources().addActorUncertaintyInResourceContainer("_E9SLkLN3Ee2o46d27a6tVQ");
		// // S1_2

		PCMDiagramGenerator diagramGenerator = new PCMDiagramGenerator(options, analysis);

		DFDDrawingStrategy drawer = new DFDDrawingStrategy();
		PCMDataFlowElementFactory creator = PCMDataFlowElementFactory.getInstance();

		Predicate<? super AbstractActionSequenceElement<?>> condition = it -> {
			return false;
		};

		PCMGraphProcessor processor = new PCMGraphProcessor(creator, condition);

		diagramGenerator.generateDataFlowDiagram(drawer, processor);
	}

	BiPredicate<List<String>, List<String>> getConstraint() {
		return (List<String> dataLiterals, List<String> nodeLiterals) -> {
			// S1_1
			if (dataLiterals.contains("ConnectionIntercepted")) {
				return true;
			}

			// S1_2
			if (nodeLiterals.contains("IllegalDeploymentLocation")) {
				return true;
			}

			return false;
		};
	}
}
