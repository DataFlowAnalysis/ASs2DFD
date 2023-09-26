package org.palladiosimulator.dataflow.diagramgenerator.dfd;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.palladiosimulator.dataflow.confidentiality.analysis.entity.pcm.seff.SEFFActionSequenceElement;
import org.palladiosimulator.dataflow.confidentiality.analysis.entity.sequence.AbstractActionSequenceElement;
import org.palladiosimulator.dataflow.confidentiality.pcm.model.confidentiality.ConfidentialityVariableCharacterisation;
import org.palladiosimulator.dataflow.confidentiality.pcm.model.confidentiality.expression.LhsEnumCharacteristicReference;
import org.palladiosimulator.dataflow.confidentiality.pcm.model.confidentiality.expression.NamedEnumCharacteristicReference;
import org.palladiosimulator.dataflow.confidentiality.pcm.model.confidentiality.expression.VariableCharacterizationLhs;
import org.palladiosimulator.dataflow.diagramgenerator.model.DataFlowElement;
import org.palladiosimulator.dataflow.diagramgenerator.model.DataFlowLiteral;
import org.palladiosimulator.dataflow.diagramgenerator.model.DataFlowNode;
import org.palladiosimulator.dataflow.diagramgenerator.model.DrawingStrategy;
import org.palladiosimulator.dataflow.diagramgenerator.model.Flow;
import org.palladiosimulator.dataflow.diagramgenerator.pcm.PCMOriginalSourceElement;
import org.palladiosimulator.dataflow.dictionary.characterized.DataDictionaryCharacterized.expressions.And;
import org.palladiosimulator.dataflow.dictionary.characterized.DataDictionaryCharacterized.expressions.Term;
import org.palladiosimulator.dataflow.dictionary.characterized.DataDictionaryCharacterized.expressions.True;
import org.palladiosimulator.pcm.core.PCMRandomVariable;
import org.palladiosimulator.pcm.parameter.VariableCharacterisation;
import org.palladiosimulator.pcm.parameter.VariableCharacterisationType;
import org.palladiosimulator.pcm.parameter.VariableUsage;
import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.seff.ExternalCallAction;
import org.palladiosimulator.pcm.seff.SetVariableAction;
import org.palladiosimulator.pcm.seff.StartAction;

import mdpa.dfd.datadictionary.Behaviour;
import mdpa.dfd.datadictionary.Label;
import mdpa.dfd.datadictionary.LabelType;
import mdpa.dfd.datadictionary.datadictionaryFactory;
import mdpa.dfd.datadictionary.impl.datadictionaryFactoryImpl;
import mdpa.dfd.dataflowdiagram.DataFlowDiagram;
import mdpa.dfd.dataflowdiagram.Node;
import mdpa.dfd.dataflowdiagram.dataflowdiagramFactory;
import mdpa.dfd.dataflowdiagram.impl.dataflowdiagramFactoryImpl;

public class DFDDrawingStrategy implements DrawingStrategy {
	@Override
	public void generate(List<DataFlowNode> dataFlowNodes) {
		dataflowdiagramFactory dfdFactory = new dataflowdiagramFactoryImpl();
		datadictionaryFactory ddFactory = new datadictionaryFactoryImpl();

		List<LabelType> labelTypes = new ArrayList<>();
		List<Label> labels = new ArrayList<>();

		DataFlowDiagram dfd = dfdFactory.createDataFlowDiagram();

		DFDDataFlowElementVisitor visitor = new DFDDataFlowElementVisitor();

		for (DataFlowNode node : dataFlowNodes) {
			// ONLY USE NODE IF IT IS A DATA FLOW NODE
			if (node.hasChildrenParameters() || node.hasParentParameters()) {

				DataFlowElement element = node.getElement();

				Node dfdNode = (Node) element.accept(visitor);

				// NODE PROPERTIES

				// Create the LiteralTypes and Literals if they do not exist already
				for (DataFlowLiteral literal : node.getLiterals()) {
					LabelType nodeLabelType = null;
					for (LabelType labelType : labelTypes) {
						if (labelType.getId().equals(literal.getTypeID())) {
							nodeLabelType = labelType;
						}
					}

					if (nodeLabelType == null) {
						nodeLabelType = ddFactory.createLabelType();

						nodeLabelType.setEntityName(literal.getTypeName());
						nodeLabelType.setId(literal.getTypeID());

						labelTypes.add(nodeLabelType);
					}

					Label nodeLabel = null;
					for (Label label : labels) {
						if (label.getId().equals(literal.getLiteralID())) {
							nodeLabel = label;
						}
					}

					if (nodeLabel == null) {
						nodeLabel = ddFactory.createLabel();

						nodeLabel.setId(literal.getLiteralID());
						nodeLabel.setEntityName(literal.getLiteralName());

						labels.add(nodeLabel);
					}

					if (!nodeLabelType.getLabel().contains(nodeLabel)) {
						nodeLabelType.getLabel().add(nodeLabel);
					}

					dfdNode.getProperties().add(nodeLabel);
				}

				dfd.getNodes().add(dfdNode);

				// NODE BEHAVIOUR

				Behaviour nodeBehaviour = ddFactory.createBehaviour();

				// BEHAVIOUR ASSIGNMENT
				PCMOriginalSourceElement originalWrapper = (PCMOriginalSourceElement) node.getOriginalSource();
				AbstractActionSequenceElement<?> originalElement = originalWrapper.getOriginalElement();

				if (originalElement instanceof SEFFActionSequenceElement<?> sase) {
					AbstractAction action = sase.getElement();

					List<VariableUsage> variableUsages = new ArrayList<>();

					if (action instanceof ExternalCallAction eca) {
						variableUsages.addAll(eca.getInputVariableUsages__CallAction());
						variableUsages.addAll(eca.getReturnVariableUsage__CallReturnAction());
					} else if (action instanceof SetVariableAction sva) {
						variableUsages.addAll(sva.getLocalVariableUsages_SetVariableAction());
					}

					for (VariableUsage usage : variableUsages) {
						List<VariableCharacterisation> characterisations = usage
								.getVariableCharacterisation_VariableUsage();

						for (VariableCharacterisation c : characterisations) {
							if (c instanceof ConfidentialityVariableCharacterisation cvc) {
								VariableCharacterizationLhs a = cvc.getLhs();
								Term b = cvc.getRhs();

								if (a instanceof LhsEnumCharacteristicReference l) {
									var test = l.getCharacteristicType();
									var i = 1;
								} else {
									var i = 1;
								}

								if (b instanceof NamedEnumCharacteristicReference necr) {
									var x = necr.getCharacteristicType();
									var y = necr.getLiteral();
									var z = necr.getNamedReference();
									var i = 1;
								} else if (b instanceof And and) {
									
									var i = 1;
								} else if (b instanceof True t) {
									
								} else {
									var i = 1;
								}

								var i = 1;
							}
							PCMRandomVariable s = c.getSpecification_VariableCharacterisation();
							VariableCharacterisationType type = c.getType();

							var a7 = s.getExpression();
							var a17 = s.getSpecification();

							var i = 1;
						}

						var i = 1;
					}
				}

				// TODO: What is the behaviour name?
				nodeBehaviour.setEntityName("?");

				// FLOWS

				DFDFlowVisitor flowVisitor = new DFDFlowVisitor();

				for (Flow parentFlow : node.getParentFlows()) {

					mdpa.dfd.dataflowdiagram.Flow newFlow = (mdpa.dfd.dataflowdiagram.Flow) parentFlow
							.accept(flowVisitor);

					Node parentDFDNode = null;

					for (Node n : dfd.getNodes()) {
						if (n.getId().equals(parentFlow.getParent().getElement().getId())) {
							parentDFDNode = n;
						}
					}

					if (parentDFDNode != null) {
						if (newFlow != null) {
							newFlow.setSourceNode(parentDFDNode);
							newFlow.setDestinationNode(dfdNode);

							dfd.getFlows().add(newFlow);
						}
					}

				}
			}

			// TODO: How to create pins?
		}

		for (mdpa.dfd.dataflowdiagram.Flow flow : dfd.getFlows()) {
			String sourceName = flow.getSourceNode() != null ? flow.getSourceNode().getEntityName() : "null";
			String destName = flow.getDestinationNode() != null ? flow.getDestinationNode().getEntityName() : "null";
			System.out.println(sourceName + " -> " + destName);
		}
	}

	@Override
	public boolean saveToDisk(String path) {
//		ResourceSet resourceSet = new ResourceSetImpl();
//		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
//		.put(Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());
//		resourceSet.getPackageRegistry().put(dataflowdiagramPackage.eNS_URI,
//				dataflowdiagramPackage.eINSTANCE);
//
//		Resource resource = resourceSet.createResource(URI.createFileURI("output/changedModel.dataflowdiagrammodel"));
//
//		resource.getContents().add(dfd);
		return false;
	}

}
