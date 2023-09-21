package org.palladiosimulator.dataflow.diagramgenerator.model;

import java.util.ArrayList;
import java.util.List;

public abstract class Flow {
	private DataFlowNode parent;
	private DataFlowNode child;
	private List<String> parameters;

	public Flow(DataFlowNode parent, DataFlowNode child) {
		this.setParent(parent);
		this.setChild(child);
		this.parameters = new ArrayList<>();
	}

	public abstract Object accept(FlowVisitor<?> visitor);

	public List<String> getParameters() {
		return parameters;
	}

	public void setParameters(List<String> parameters) {
		this.parameters = parameters;
	}

	public boolean hasParameters() {
		return !this.parameters.isEmpty();
	}

	public DataFlowNode getParent() {
		return parent;
	}

	public void setParent(DataFlowNode parent) {
		this.parent = parent;
	}

	public DataFlowNode getChild() {
		return child;
	}

	public void setChild(DataFlowNode child) {
		this.child = child;
	}
}
