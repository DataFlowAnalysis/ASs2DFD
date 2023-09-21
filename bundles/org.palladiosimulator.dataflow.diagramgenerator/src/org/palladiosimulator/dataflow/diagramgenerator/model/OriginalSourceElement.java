package org.palladiosimulator.dataflow.diagramgenerator.model;

public abstract class OriginalSourceElement<T> {
	private T originalElement;

	public OriginalSourceElement(T originalElement) {
		this.originalElement = originalElement;
	}

	public T getOriginalElement() {
		return originalElement;
	}

	public void setOriginalElement(T originalElement) {
		this.originalElement = originalElement;
	}
}
