package org.palladiosimulator.dataflow.diagramgenerator.model;

import java.util.ArrayList;
import java.util.List;

public class DataFlowNode {
	private DataFlowElement element;
	private OriginalSourceElement<?> originalSource;
	private List<Flow> parentFlows;
	private List<Flow> childrenFlows;
	private List<DataFlowLiteral> literals;
	private List<DataFlowElementVariable> variables;
	private int id;

	public DataFlowNode(OriginalSourceElement<?> originalSource, DataFlowElement element, int id) {
		this.element = element;
		this.originalSource = originalSource;
		this.parentFlows = new ArrayList<>();
		this.childrenFlows = new ArrayList<>();
		this.literals = new ArrayList<>();
		this.variables = new ArrayList<>();
		this.id = id;
	}

	public boolean hasParentParameters() {
		return this.parentFlows.stream().anyMatch(Flow::hasParameters);
	}

	public boolean hasChildrenParameters() {
		return this.childrenFlows.stream().anyMatch(Flow::hasParameters);
	}

	public void setOriginalSource(OriginalSourceElement<?> originalSource) {
		this.originalSource = originalSource;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public DataFlowElement getElement() {
		return element;
	}

	public OriginalSourceElement<?> getOriginalSource() {
		return this.originalSource;
	}

	public List<Flow> getParentFlows() {
		return this.parentFlows;
	}

	public void addParentFlow(Flow parentFlow) {
		// only add if there is no parent flow that has the same parent and child and
		// the
		// same parameters. the parameters need to contain the same string values
		boolean exists = false;
		for (Flow comp : this.parentFlows) {
			if (comp.getParent().equals(parentFlow.getParent()) && comp.getChild().equals(parentFlow.getChild())) {
				if (comp.getParameters().size() == parentFlow.getParameters().size()) {
					boolean same = true;
					for (String param : comp.getParameters()) {
						if (!parentFlow.getParameters().contains(param)) {
							same = false;
						}
					}
					if (same) {
						exists = true;
					}
				}
			}
		}
		if (!exists) {
			this.parentFlows.add(parentFlow);
		}
	}

	public void removeParentFlow(Flow parentFlow) {
		this.parentFlows.remove(parentFlow);
	}

	public List<Flow> getChildrenFlows() {
		return this.childrenFlows;
	}

	public void addChildFlow(Flow childFlow) {
		// only add if there is no child flow that has the same parent and child and the
		// same parameters. the parameters need to contain the same string values
		boolean exists = false;
		for (Flow comp : this.childrenFlows) {
			if (comp.getParent().equals(childFlow.getParent()) && comp.getChild().equals(childFlow.getChild())) {
				if (comp.getParameters().size() == childFlow.getParameters().size()) {
					boolean same = true;
					for (String param : comp.getParameters()) {
						if (!childFlow.getParameters().contains(param)) {
							same = false;
						}
					}
					if (same) {
						exists = true;
					}
				}
			}
		}
		if (!exists) {
			this.childrenFlows.add(childFlow);
		}
	}

	public void removeChildFlow(Flow childFlow) {
		this.childrenFlows.remove(childFlow);
	}

	public void accept(DataFlowNodeVisitor visitor) {
		visitor.visit(this);
	}

	public List<DataFlowElementVariable> getVariables() {
		return this.variables;
	}

	public void setVariables(List<DataFlowElementVariable> variables) {
		this.variables = variables;
	}

	public void addVariable(DataFlowElementVariable variable) {
		boolean exists = false;
		for (DataFlowElementVariable comp : this.variables) {
			if (comp.getName().equals(variable.getName()))
				exists = true;
		}
		if (!exists) {
			this.variables.add(variable);
		}
	}

	public void removeVariable(DataFlowElementVariable variable) {
		this.variables.remove(variable);
	}

	public boolean hasVariable(DataFlowElementVariable variable) {
		boolean exists = false;
		for (DataFlowLiteral comp : this.literals) {
			if (comp.getLiteralID().equals(variable.getName()))
				exists = true;
		}
		return exists;
	}

	public List<DataFlowLiteral> getLiterals() {
		return this.literals;
	}

	public void setLiterals(List<DataFlowLiteral> literals) {
		this.literals = literals;
	}

	public void addLiteral(DataFlowLiteral literal) {
		boolean exists = false;
		for (DataFlowLiteral comp : this.literals) {
			if (comp.getLiteralID().equals(literal.getLiteralID()) && comp.getTypeID().equals(literal.getTypeID()))
				exists = true;
		}
		if (!exists) {
			this.literals.add(literal);
		}
	}

	public void removeLiteral(DataFlowLiteral literal) {
		this.literals.remove(literal);
	}

	public boolean hasLiteral(DataFlowLiteral literal) {
		boolean exists = false;
		for (DataFlowLiteral comp : this.literals) {
			if (comp.getLiteralID().equals(literal.getLiteralID()) && comp.getTypeID().equals(literal.getTypeID()))
				exists = true;
		}
		return exists;
	}
}
