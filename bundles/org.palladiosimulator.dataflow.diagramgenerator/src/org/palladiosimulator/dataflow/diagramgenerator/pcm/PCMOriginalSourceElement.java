package org.palladiosimulator.dataflow.diagramgenerator.pcm;

import org.palladiosimulator.dataflow.confidentiality.analysis.entity.sequence.AbstractActionSequenceElement;
import org.palladiosimulator.dataflow.diagramgenerator.model.OriginalSourceElement;

public class PCMOriginalSourceElement extends OriginalSourceElement<AbstractActionSequenceElement<?>> {
	public PCMOriginalSourceElement(AbstractActionSequenceElement<?> originalElement) {
		super(originalElement);
	}
}
