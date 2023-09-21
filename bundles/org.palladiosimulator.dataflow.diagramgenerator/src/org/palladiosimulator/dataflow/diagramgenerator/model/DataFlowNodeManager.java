package org.palladiosimulator.dataflow.diagramgenerator.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.abunai.impact.analysis.model.impact.UncertaintyImpact;

public class DataFlowNodeManager {
	private int idCounter;

	public DataFlowNodeManager() {
		this.idCounter = 0;
	}

	public DataFlowNode createNewDataFlowNode(OriginalSourceElement<?> originalSource,
			DataFlowElement dataFlowElement) {
		return new DataFlowNode(originalSource, dataFlowElement, idCounter++);
	}

	public void addLiteralsToNode(DataFlowNode dataFlowNode, List<DataFlowLiteral> literals) {
		literals.forEach(dataFlowNode::addLiteral);
	}

	public void addVariablesToNode(DataFlowNode dataFlowNode, List<DataFlowElementVariable> variables) {
		variables.forEach(dataFlowNode::addVariable);
	}

	public void connectNodes(DataFlowNode previousNode, DataFlowNode dataFlowNode, List<String> parameters) {
		if (previousNode != null) {
			Flow flow = null;
			if (parameters.size() > 0) {
				flow = new DataFlow(previousNode, dataFlowNode);
			} else {
				flow = new ControlFlow(previousNode, dataFlowNode);
			}

			flow.setParameters(parameters);

			previousNode.addChildFlow(flow);
			dataFlowNode.addParentFlow(flow);
		}
	}

	public void addNodeToListIfNotExists(DataFlowNode dataFlowNode, List<DataFlowNode> dataFlowNodes) {
		if (!dataFlowNodes.contains(dataFlowNode)) {
			dataFlowNodes.add(dataFlowNode);
		}
	}

	public Map<DataFlowElement, DataFlowNode> createDataFlowElementNodeMap(List<DataFlowElement> dataFlowElements,
			List<DataFlowNode> dataFlowNodes, List<UncertaintyImpact<?>> uncertaintyImpacts) {
		Map<DataFlowElement, DataFlowNode> existingMap = new HashMap<>();

		for (DataFlowElement dfe : dataFlowElements) {
			DataFlowNode existingNode = findExistingNode(dfe, dataFlowNodes);
			if (existingNode == null) {
				existingNode = createNewDataFlowNode(null, dfe); // Pass null or appropriate argument for
																	// actionSequenceElement
				addNodeToListIfNotExists(existingNode, dataFlowNodes);
			}
			existingMap.put(dfe, existingNode);
		}

		return existingMap;
	}

	private DataFlowNode findExistingNode(DataFlowElement dataFlowElement, List<DataFlowNode> dataFlowNodes) {
		return dataFlowNodes.stream().filter(node -> node.getElement().equals(dataFlowElement)).findFirst()
				.orElse(null);
	}
}
