package org.palladiosimulator.dataflow.diagramgenerator.pcm;

import java.util.ArrayList;
import java.util.List;

import org.palladiosimulator.dataflow.confidentiality.analysis.entity.pcm.AbstractPCMActionSequenceElement;
import org.palladiosimulator.dataflow.confidentiality.analysis.entity.pcm.seff.DatabaseActionSequenceElement;
import org.palladiosimulator.dataflow.confidentiality.analysis.entity.pcm.seff.SEFFActionSequenceElement;
import org.palladiosimulator.dataflow.confidentiality.analysis.entity.pcm.user.CallingUserActionSequenceElement;
import org.palladiosimulator.dataflow.confidentiality.analysis.entity.sequence.AbstractActionSequenceElement;
import org.palladiosimulator.dataflow.diagramgenerator.model.DataFlowElement;
import org.palladiosimulator.dataflow.diagramgenerator.model.DataFlowElementFactory;
import org.palladiosimulator.dataflow.diagramgenerator.model.DataStoreDataFlowElement;
import org.palladiosimulator.dataflow.diagramgenerator.model.ExternalEntityDataFlowElement;
import org.palladiosimulator.dataflow.diagramgenerator.model.ProcessDataFlowElement;

import dev.abunai.impact.analysis.model.impact.UncertaintyImpact;

/**
 * The DataFlowElementFactory class is responsible for creating data flow
 * elements based on the given action sequence elements.
 */
public class PCMDataFlowElementFactory extends DataFlowElementFactory {
	private static PCMDataFlowElementFactory instance;

	private PCMDataFlowElementFactory() {
	}

	/**
	 * Returns the singleton instance of the DataFlowElementFactory.
	 * 
	 * @return The singleton instance of the factory.
	 */
	public static synchronized PCMDataFlowElementFactory getInstance() {
		if (instance == null) {
			instance = new PCMDataFlowElementFactory();
		}
		return instance;
	}

	/**
	 * Creates data flow elements for the given action sequence element.
	 * 
	 * @param element The action sequence element to create data flow elements for.
	 * @return The list of created data flow elements.
	 * @throws UnsupportedOperationException if the element type is not supported.
	 */
	public List<DataFlowElement> createDataFlowElementsForActionSequenceElement(
			AbstractActionSequenceElement<?> element, boolean isViolation, String returnParameter,
			List<UncertaintyImpact<?>> uncertaintyImpacts) {
		List<DataFlowElement> dataFlowElements = new ArrayList<>();

		String id = PCMEntityUtility.getEntityId(element);
		String name = PCMEntityUtility.getEntityName(element);
		Boolean isCalling = PCMEntityUtility.getIsCalling(element);
		List<String> parameters = PCMEntityUtility.getParameters(element);
		if (returnParameter != null) {
			parameters.add(returnParameter);
		}
		String actorName = PCMEntityUtility.getActorName(element);
		boolean isBranch = PCMEntityUtility.isBranch(element);
		boolean isUncertainty = false;

		for (UncertaintyImpact<?> uncertaintyImpact : uncertaintyImpacts) {
			AbstractPCMActionSequenceElement<?> ase2 = (AbstractPCMActionSequenceElement<?>) uncertaintyImpact
					.getAffectedElement();
			if (((AbstractPCMActionSequenceElement<?>) element).getElement().getId()
					.equals(ase2.getElement().getId())) {
				isUncertainty = true;
				break;
			}
		}

		if (!isBranch) {
			if (element instanceof CallingUserActionSequenceElement) {
				createExternalEntityDataFlowElement(dataFlowElements, actorName, isViolation, isUncertainty);
			} else if (element instanceof SEFFActionSequenceElement) {
				createProcessDataFlowElement(dataFlowElements, id, isCalling, name, isViolation, isUncertainty);
			} else if (element instanceof DatabaseActionSequenceElement<?>) {
				createDataStoreDataFlowElement(dataFlowElements, id, isCalling, name, isViolation, isUncertainty);
			} else {
				throw new UnsupportedOperationException("Element type not supported");
			}
		}

		return dataFlowElements;
	}

	private void createExternalEntityDataFlowElement(List<DataFlowElement> dataFlowElements, String actorName,
			boolean isViolation, boolean isUncertainty) {
		if (actorName != null) {
			ExternalEntityDataFlowElement externalEntity = new ExternalEntityDataFlowElement(actorName, true,
					isViolation, actorName);
			externalEntity.setHasUncertainty(isUncertainty);
			dataFlowElements.add(externalEntity);
		}
	}

	private void createProcessDataFlowElement(List<DataFlowElement> dataFlowElements, String id, Boolean isCalling,
			String name, boolean isViolation, boolean isUncertainty) {
		ProcessDataFlowElement process = new ProcessDataFlowElement(id, isCalling, isViolation, name);
		process.setHasUncertainty(isUncertainty);
		dataFlowElements.add(process);
	}

	private void createDataStoreDataFlowElement(List<DataFlowElement> dataStoreElements, String id, Boolean isCalling,
			String name, boolean isViolation, boolean isUncertainty) {
		DataStoreDataFlowElement dataStore = new DataStoreDataFlowElement(id, isCalling, isViolation, name);
		dataStore.setHasUncertainty(isUncertainty);
		dataStoreElements.add(dataStore);
	}
}
