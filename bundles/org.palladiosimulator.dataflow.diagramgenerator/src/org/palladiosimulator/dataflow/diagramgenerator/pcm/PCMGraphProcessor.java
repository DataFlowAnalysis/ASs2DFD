package org.palladiosimulator.dataflow.diagramgenerator.pcm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.palladiosimulator.dataflow.confidentiality.analysis.DataFlowConfidentialityAnalysis;
import org.palladiosimulator.dataflow.confidentiality.analysis.characteristics.CharacteristicValue;
import org.palladiosimulator.dataflow.confidentiality.analysis.characteristics.DataFlowVariable;
import org.palladiosimulator.dataflow.confidentiality.analysis.entity.sequence.AbstractActionSequenceElement;
import org.palladiosimulator.dataflow.confidentiality.analysis.entity.sequence.ActionSequence;
import org.palladiosimulator.dataflow.diagramgenerator.GeneratorOptions;
import org.palladiosimulator.dataflow.diagramgenerator.GraphProcessor;
import org.palladiosimulator.dataflow.diagramgenerator.model.DataFlowElement;
import org.palladiosimulator.dataflow.diagramgenerator.model.DataFlowElementVariable;
import org.palladiosimulator.dataflow.diagramgenerator.model.DataFlowLiteral;
import org.palladiosimulator.dataflow.diagramgenerator.model.DataFlowNode;
import org.palladiosimulator.dataflow.diagramgenerator.model.DataFlowNodeManager;
import org.palladiosimulator.dataflow.dictionary.characterized.DataDictionaryCharacterized.impl.EnumCharacteristicTypeImpl;
import org.palladiosimulator.dataflow.dictionary.characterized.DataDictionaryCharacterized.impl.LiteralImpl;

import dev.abunai.impact.analysis.model.impact.UncertaintyImpact;

public class PCMGraphProcessor implements GraphProcessor {
	private final PCMDataFlowElementFactory elementCreator;
	private final DataFlowNodeManager nodeManager;
	private final Predicate<? super AbstractActionSequenceElement<?>> condition;

	public PCMGraphProcessor(PCMDataFlowElementFactory elementCreator,
			Predicate<? super AbstractActionSequenceElement<?>> condition) {
		this.elementCreator = elementCreator;
		this.nodeManager = new DataFlowNodeManager();
		this.condition = condition;
	}

	public List<DataFlowNode> processActionSequences(List<ActionSequence> actionSequences,
			DataFlowConfidentialityAnalysis analysis, List<UncertaintyImpact<?>> uncertaintyImpacts) {
		List<DataFlowNode> dataFlowNodes = new ArrayList<>();

		for (ActionSequence actionSequence : actionSequences) {
			processActionSequence(actionSequence, dataFlowNodes, analysis, uncertaintyImpacts);
		}

		return dataFlowNodes;
	}

	private void processActionSequence(ActionSequence actionSequence, List<DataFlowNode> dataFlowNodes,
			DataFlowConfidentialityAnalysis analysis, List<UncertaintyImpact<?>> uncertaintyImpacts) {
		DataFlowNode previousNode = null;
		GeneratorOptions options = GeneratorOptions.getInstance();

		List<AbstractActionSequenceElement<?>> violations = analysis.queryDataFlow(actionSequence, this.condition);

		for (AbstractActionSequenceElement<?> actionSequenceElement : actionSequence.getElements()) {
			boolean isViolation = violations.contains(actionSequenceElement);

			String name = PCMEntityUtility.getEntityName(actionSequenceElement);

			List<DataFlowElementVariable> variables = createDataFlowElementVariables(actionSequenceElement);
			List<DataFlowLiteral> literals = createDataFlowLiterals(actionSequenceElement);
			AbstractActionSequenceElement<?> prevElement = null;
			if (previousNode != null) {
				PCMOriginalSourceElement prevSource = (PCMOriginalSourceElement) previousNode.getOriginalSource();
				prevElement = prevSource.getOriginalElement();
			}

			List<String> parameters = PCMEntityUtility.getParameters(actionSequenceElement);
			String returnParameter = PCMEntityUtility.getRETURNParameter(prevElement);
			if (returnParameter != null)
				parameters.add(returnParameter);

			List<DataFlowElement> dataFlowElements = elementCreator.createDataFlowElementsForActionSequenceElement(
					actionSequenceElement, isViolation, returnParameter, uncertaintyImpacts);
			Map<DataFlowElement, DataFlowNode> existingMap = nodeManager.createDataFlowElementNodeMap(dataFlowElements,
					dataFlowNodes, uncertaintyImpacts);

			for (Entry<DataFlowElement, DataFlowNode> dataFlowEntry : existingMap.entrySet()) {
				DataFlowNode dataFlowNode = dataFlowEntry.getValue();

				PCMOriginalSourceElement originalSource = new PCMOriginalSourceElement(actionSequenceElement);
				dataFlowNode.setOriginalSource(originalSource);

				nodeManager.connectNodes(previousNode, dataFlowNode, parameters);
				if (!options.isDrawOnlyNumbers()) {
					if (options.isDrawVariables())
						nodeManager.addVariablesToNode(dataFlowNode, variables);
					if (options.isDrawNodeCharacteristics())
						nodeManager.addLiteralsToNode(dataFlowNode, literals);
				}
				previousNode = dataFlowNode;
			}
		}
	}

	private List<DataFlowElementVariable> createDataFlowElementVariables(
			AbstractActionSequenceElement<?> actionSequenceElement) {
		List<DataFlowElementVariable> variables = new ArrayList<>();

		for (DataFlowVariable variable : actionSequenceElement.getAllDataFlowVariables()) {
			DataFlowElementVariable elementVariable = findOrCreateVariable(variables, variable.variableName());

			for (CharacteristicValue val : variable.getAllCharacteristics()) {
				LiteralImpl elementLiteral = (LiteralImpl) val.characteristicLiteral();
				EnumCharacteristicTypeImpl type = (EnumCharacteristicTypeImpl) val.characteristicType();

				elementVariable.addLiteral(new DataFlowLiteral(type.getId(), type.getName(), elementLiteral.getId(),
						elementLiteral.getName()));
			}
		}

		return variables;
	}

	private DataFlowElementVariable findOrCreateVariable(List<DataFlowElementVariable> variables, String variableName) {
		return variables.stream().filter(v -> v.getName().equals(variableName)).findFirst().orElseGet(() -> {
			DataFlowElementVariable newVariable = new DataFlowElementVariable(variableName);
			variables.add(newVariable);
			return newVariable;
		});
	}

	private List<DataFlowLiteral> createDataFlowLiterals(AbstractActionSequenceElement<?> actionSequenceElement) {
		return actionSequenceElement.getAllNodeCharacteristics().stream().map(val -> {
			LiteralImpl elementLiteral = (LiteralImpl) val.characteristicLiteral();
			EnumCharacteristicTypeImpl type = (EnumCharacteristicTypeImpl) val.characteristicType();

			return new DataFlowLiteral(type.getId(), type.getName(), elementLiteral.getId(), elementLiteral.getName());
		}).collect(Collectors.toList());
	}
}
