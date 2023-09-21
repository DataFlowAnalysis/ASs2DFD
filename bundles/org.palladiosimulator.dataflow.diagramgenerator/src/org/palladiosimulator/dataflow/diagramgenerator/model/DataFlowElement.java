package org.palladiosimulator.dataflow.diagramgenerator.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class DataFlowElement {
	private String id;
	private Boolean isCalling;
	private boolean isViolation;
	private String name;
	private boolean hasRETURN;
	private boolean hasUncertainty;

	protected DataFlowElement(String id, Boolean isCalling, boolean isViolation, String name) {
		this.id = id;
		this.isCalling = isCalling;
		this.isViolation = isViolation;
		this.name = name;
		this.hasRETURN = false;
		this.hasUncertainty = false;
	}

	public abstract Object accept(DataFlowElementVisitor<?> visitor);
	
	public boolean isHasUncertainty() {
		return hasUncertainty;
	}

	public void setHasUncertainty(boolean hasUncertainty) {
		this.hasUncertainty = hasUncertainty;
	}

	public boolean isHasRETURN() {
		return hasRETURN;
	}

	public void setHasRETURN(boolean hasRETURN) {
		this.hasRETURN = hasRETURN;
	}
	
	public boolean isViolation() {
		return this.isViolation;
	}

	public String getId() {
		return id;
	}

	public Boolean getIsCalling() {
		return isCalling;
	}

	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, isCalling);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DataFlowElement other = (DataFlowElement) obj;
		return Objects.equals(id, other.id) && Objects.equals(isCalling, other.isCalling);
	}
}
