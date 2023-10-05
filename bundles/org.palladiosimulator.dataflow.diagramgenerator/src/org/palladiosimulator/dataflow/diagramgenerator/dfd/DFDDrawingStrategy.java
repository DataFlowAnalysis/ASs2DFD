package org.palladiosimulator.dataflow.diagramgenerator.dfd;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.eclipse.xtext.EcoreUtil2;
import org.palladiosimulator.dataflow.confidentiality.analysis.characteristics.CharacteristicValue;
import org.palladiosimulator.dataflow.confidentiality.analysis.entity.pcm.seff.SEFFActionSequenceElement;
import org.palladiosimulator.dataflow.confidentiality.analysis.entity.sequence.AbstractActionSequenceElement;
import org.palladiosimulator.dataflow.confidentiality.pcm.model.confidentiality.ConfidentialityVariableCharacterisation;
import org.palladiosimulator.dataflow.confidentiality.pcm.model.confidentiality.dictionary.PCMDataDictionary;
import org.palladiosimulator.dataflow.confidentiality.pcm.model.confidentiality.expression.LhsEnumCharacteristicReference;
import org.palladiosimulator.dataflow.confidentiality.pcm.model.confidentiality.expression.NamedEnumCharacteristicReference;
import org.palladiosimulator.dataflow.diagramgenerator.model.DataFlowElement;
import org.palladiosimulator.dataflow.diagramgenerator.model.DataFlowNode;
import org.palladiosimulator.dataflow.diagramgenerator.model.DrawingStrategy;
import org.palladiosimulator.dataflow.diagramgenerator.model.Flow;
import org.palladiosimulator.dataflow.diagramgenerator.pcm.PCMOriginalSourceElement;
import org.palladiosimulator.dataflow.dictionary.characterized.DataDictionaryCharacterized.CharacteristicType;
import org.palladiosimulator.dataflow.dictionary.characterized.DataDictionaryCharacterized.EnumCharacteristicType;
import org.palladiosimulator.dataflow.dictionary.characterized.DataDictionaryCharacterized.Enumeration;
import org.palladiosimulator.dataflow.dictionary.characterized.DataDictionaryCharacterized.Literal;
import org.palladiosimulator.dataflow.dictionary.characterized.DataDictionaryCharacterized.expressions.And;
import org.palladiosimulator.dataflow.dictionary.characterized.DataDictionaryCharacterized.expressions.False;
import org.palladiosimulator.dataflow.dictionary.characterized.DataDictionaryCharacterized.expressions.Or;
import org.palladiosimulator.dataflow.dictionary.characterized.DataDictionaryCharacterized.expressions.Term;
import org.palladiosimulator.dataflow.dictionary.characterized.DataDictionaryCharacterized.expressions.True;
import org.palladiosimulator.pcm.parameter.VariableCharacterisation;
import org.palladiosimulator.pcm.parameter.VariableUsage;
import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.seff.ExternalCallAction;
import org.palladiosimulator.pcm.seff.SetVariableAction;

import de.uka.ipd.sdq.stoex.AbstractNamedReference;
import mdpa.dfd.datadictionary.AND;
import mdpa.dfd.datadictionary.Assignment;
import mdpa.dfd.datadictionary.Behaviour;
import mdpa.dfd.datadictionary.Label;
import mdpa.dfd.datadictionary.LabelReference;
import mdpa.dfd.datadictionary.LabelType;
import mdpa.dfd.datadictionary.OR;
import mdpa.dfd.datadictionary.Pin;
import mdpa.dfd.datadictionary.datadictionaryFactory;
import mdpa.dfd.datadictionary.impl.datadictionaryFactoryImpl;
import mdpa.dfd.dataflowdiagram.DataFlowDiagram;
import mdpa.dfd.dataflowdiagram.Node;
import mdpa.dfd.dataflowdiagram.dataflowdiagramFactory;
import mdpa.dfd.dataflowdiagram.impl.dataflowdiagramFactoryImpl;

public class DFDDrawingStrategy implements DrawingStrategy {
	private dataflowdiagramFactory dfdFactory;
	private datadictionaryFactory ddFactory;
	private List<LabelType> labelTypes;
	private List<Label> labels;

	public DFDDrawingStrategy() {
		this.dfdFactory = new dataflowdiagramFactoryImpl();
		this.ddFactory = new datadictionaryFactoryImpl();

		this.labelTypes = new ArrayList<>();
		this.labels = new ArrayList<>();
	}

	@Override
	public void generate(List<DataFlowNode> dataFlowNodes) {
		/*
		 * Wir extrahieren den ersten Knoten, um daraus das PCMDataDataDictionary zu
		 * extrahieren. Das benötigen wir, um erstmal alle LabelTypes und Labels
		 * erzeugen zu können.
		 */
		DataFlowNode firstNode = dataFlowNodes.stream().filter(node -> !node.getLiterals().isEmpty()).findFirst()
				.orElse(null);

		if (firstNode == null) {
			return;
		}

		/*
		 * Jetzt holen wir uns aus dem Knoten das OriginalElement (in diesem Fall ein
		 * PCM Element).
		 */
		AbstractActionSequenceElement<?> ogElement = (AbstractActionSequenceElement<?>) firstNode.getOriginalSource()
				.getOriginalElement();

		CharacteristicValue charac = ogElement.getAllNodeCharacteristics().get(0);
		PCMDataDictionary dict = (PCMDataDictionary) charac.characteristicLiteral().getEnum().eContainer();

		this.createLabelsandLabelTypes(dict, labelTypes);

		/*
		 * Jetzt können wir loslegen. Alle Inhalte des DFDs werden in ein
		 * DataFlowDiagram reingeschmissen.
		 */
		DataFlowDiagram dfd = this.dfdFactory.createDataFlowDiagram();

		DFDDataFlowElementVisitor visitor = new DFDDataFlowElementVisitor();

		for (DataFlowNode node : dataFlowNodes) {
			/*
			 * Wir behandeln die Node nur, wenn sie eine Datenflussnode ist (Die Regeln sind
			 * aus dem DiagramGenerator)
			 */
			if (node.hasChildrenParameters() || node.hasParentParameters()) {

				DataFlowElement element = node.getElement();

				Node dfdNode = (Node) element.accept(visitor);
				dfd.getNodes().add(dfdNode);

				/*
				 * NODE BEHAVIOUR
				 */
				Behaviour nodeBehaviour = this.createNodeBehaviour();
				dfdNode.setBehaviour(nodeBehaviour);

				/*
				 * BEHAVIOUR ASSIGNMENT
				 */
				this.createAssignments(node, nodeBehaviour, charac);

				/*
				 * FLOWS
				 */
				DFDFlowVisitor flowVisitor = new DFDFlowVisitor();
				this.createFlows(node, dfd, flowVisitor, dfdNode);
			}
		}
	}

	private void createLabelsandLabelTypes(PCMDataDictionary dict, List<LabelType> labelTypes) {
		/*
		 * Jetzt iterieren wir über alle Enum Einträge, die wir haben. Die Besonderheit
		 * ist, dass es in den .pccd Dateien enumCharacteristicTypes aus enums bilden.
		 * Für das DFD-Modell bedeutet das, wir brauchen ein LabelType für jeden
		 * EnumCharacteristicType, denn es gibt die allgemeinen Enums nicht.
		 */
		for (Enumeration e : dict.getCharacteristicEnumerations()) {
			List<EnumCharacteristicType> types = new ArrayList<>();
			for (CharacteristicType type : dict.getCharacteristicTypes()) {
				if (type instanceof EnumCharacteristicType ect) {
					if (ect.getType().equals(e)) {
						types.add(ect);
					}
				}
			}

			List<Literal> literals = e.getLiterals();
			List<Label> tempLabels = new ArrayList<>();

			/*
			 * Für die korrekte Zuordnung brauchen wir erstmal alle Labels.
			 */
			for (Literal literal : literals) {
				Label label = this.ddFactory.createLabel();
				label.setEntityName(literal.getName());
				label.setId(literal.getId());
				tempLabels.add(label);
				this.labels.add(label);
			}

			/*
			 * Jetzt die LabelTypes, eines für jeden EnumCharacteristicType
			 */
			for (EnumCharacteristicType ect : types) {
				LabelType labelType = this.ddFactory.createLabelType();
				labelType.setEntityName(ect.getName());
				labelType.setId(ect.getId());

				this.labelTypes.add(labelType);

				for (Label tempLabel : tempLabels) {
					/*
					 * Dieses copy hier ist notwendig, da EMF sonst die Labels aus den anderen
					 * LabelTypes wieder rausklaut.
					 */
					labelType.getLabel().add(EcoreUtil2.copy(tempLabel));
				}
			}
		}
	}

	private Behaviour createNodeBehaviour() {
		Behaviour nodeBehaviour = this.ddFactory.createBehaviour();
		nodeBehaviour.setEntityName("aName");

		return nodeBehaviour;
	}

	private void createAssignments(DataFlowNode node, Behaviour nodeBehaviour, CharacteristicValue charac) {
		PCMOriginalSourceElement originalWrapper = (PCMOriginalSourceElement) node.getOriginalSource();
		AbstractActionSequenceElement<?> originalElement = originalWrapper.getOriginalElement();

		if (originalElement instanceof SEFFActionSequenceElement<?> sase) {
			AbstractAction action = sase.getElement();

			List<VariableUsage> variableUsages = new ArrayList<>();

			if (action instanceof ExternalCallAction eca) {
				variableUsages.addAll(eca.getInputVariableUsages__CallAction());

				/*
				 * TODO: eca.getReturnVariableUsage__CallReturnAction() sind die Zuweisungen,
				 * die beim Return passieren.
				 */
			} else if (action instanceof SetVariableAction sva) {
				variableUsages.addAll(sva.getLocalVariableUsages_SetVariableAction());
			}

			for (VariableUsage usage : variableUsages) {
				List<VariableCharacterisation> characterisations = usage.getVariableCharacterisation_VariableUsage();

				for (VariableCharacterisation variableCharacterisation : characterisations) {
					/*
					 * Jede Variable ist ein Assignment
					 */
					if (variableCharacterisation instanceof ConfidentialityVariableCharacterisation cvc) {
						Assignment assignment = this.ddFactory.createAssignment();
						assignment.setEntityName("aName");

						Term rightHandSide = cvc.getRhs();
						AbstractNamedReference reference = variableCharacterisation
								.getVariableUsage_VariableCharacterisation().getNamedReference__VariableUsage();

						Pin outputPin = this.ddFactory.createPin();
						outputPin.setEntityName(reference.getReferenceName());
						assignment.setOutputPin(outputPin);
						nodeBehaviour.getOut().add(outputPin);

						assignment.setTerm(this.evaluateTerm(rightHandSide, charac));
					}
				}
			}
		}
	}

	private void createFlows(DataFlowNode node, DataFlowDiagram dfd, DFDFlowVisitor flowVisitor, Node dfdNode) {
		/*
		 * TODO: Flows müssen extra behandelt werden, weil die Pins erst alle existieren
		 * müssen. Wie genau müssen die Pins verbunden werden?
		 */
		for (Flow parentFlow : node.getParentFlows()) {

			mdpa.dfd.dataflowdiagram.Flow newFlow = (mdpa.dfd.dataflowdiagram.Flow) parentFlow.accept(flowVisitor);

			Node parentDFDNode = dfd.getNodes().stream()
					.filter(n -> n.getId().equals(parentFlow.getParent().getElement().getId())).findFirst()
					.orElse(null);

			if (parentDFDNode != null && newFlow != null) {
				newFlow.setSourceNode(parentDFDNode);
				newFlow.setDestinationNode(dfdNode);

				dfd.getFlows().add(newFlow);
			}
		}
	}

	private mdpa.dfd.datadictionary.Term evaluateTerm(Term term, CharacteristicValue characteristicValue) {
		if (term instanceof True) {
			LabelReference labelRef = this.ddFactory.createLabelReference();
			for (LabelType lt : this.labelTypes) {
				if (lt.getEntityName().equals(characteristicValue.characteristicType().getName())) {
					for (Label l : lt.getLabel()) {
						if (l.getEntityName().equals(characteristicValue.characteristicLiteral().getName())) {
							labelRef.setLabel(l);
							break;
						}
					}
					break;
				}
			}
			return labelRef;
		} else if (term instanceof False) {
			// False wird im DFD-Modell nicht dargestellt
			return null;
		} else if (term instanceof NamedEnumCharacteristicReference necr) {
			/*
			 * TODO: necr.getNamedReference().getReferenceName() ist der Variablenname.
			 * dieser Variablenname ist im DFD-Modell eine Referenz auf einen Input-Pin.
			 * Wenn also z. B. die Variable query reinkommt, muss der Pin gesucht werden,
			 * der query heißt. Dann müssen irgendwie Terme geholt werden und geprüft
			 * werden, ob die Belegung passt. Wir müssen also das Literal und den EnumType
			 * holen und gegen die DFD Labels und LabelTypes prüfen (ähnlich zum True Term).
			 * Wie das geht, keine Ahnung.
			 */
			var t1 = necr.getLiteral();
			var t2 = necr.getNamedReference().getReferenceName();
			var t3 = necr.getCharacteristicType();
			return this.ddFactory.createTRUE();
		} else if (term instanceof And andTerm) {
			AND newAnd = this.ddFactory.createAND();

			mdpa.dfd.datadictionary.Term leftTerm = evaluateTerm(andTerm.getLeft(), characteristicValue);
			mdpa.dfd.datadictionary.Term rightTerm = evaluateTerm(andTerm.getRight(), characteristicValue);

			if (leftTerm == null) {
				return rightTerm;
			}
			if (rightTerm == null) {
				return leftTerm;
			}

			newAnd.getTerms().add(leftTerm);
			newAnd.getTerms().add(rightTerm);
			return newAnd;
		} else if (term instanceof Or orTerm) {
			OR newOr = this.ddFactory.createOR();

			mdpa.dfd.datadictionary.Term leftTerm = evaluateTerm(orTerm.getLeft(), characteristicValue);
			mdpa.dfd.datadictionary.Term rightTerm = evaluateTerm(orTerm.getRight(), characteristicValue);

			if (leftTerm == null) {
				return rightTerm;
			}
			if (rightTerm == null) {
				return leftTerm;
			}

			newOr.getTerms().add(rightTerm);
			newOr.getTerms().add(leftTerm);
			return newOr;
		} else {
			throw new IllegalArgumentException("Unknown type: " + term.getClass().getName());
		}
	}

	@Override
	public boolean saveToDisk(String path) {
		/*
		 * TODO: Das erzeugte DFD-Modell muss gespeichert werden. Anscheinend soll es
		 * ungefähr mit dem folgenden Code funktionieren.
		 */
		// ResourceSet resourceSet = new ResourceSetImpl();
		// resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
		// .put(Resource.Factory.Registry.DEFAULT_EXTENSION, new
		// XMIResourceFactoryImpl());
		// resourceSet.getPackageRegistry().put(dataflowdiagramPackage.eNS_URI,
		// dataflowdiagramPackage.eINSTANCE);
		//
		// Resource resource =
		// resourceSet.createResource(URI.createFileURI("output/changedModel.dataflowdiagrammodel"));
		//
		// resource.getContents().add(dfd);
		return false;
	}

}
