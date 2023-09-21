package org.palladiosimulator.dataflow.diagramgenerator.model;

import java.util.ArrayList;
import java.util.List;

public class DataFlowElementVariable {
	private String name;
	private List<DataFlowLiteral> literals;

	public DataFlowElementVariable(String name) {
		super();
		this.name = name;
		this.literals = new ArrayList<DataFlowLiteral>();
	}
	
	public void accept(DataFlowElementVariableVisitor visitor) {
		visitor.visit(this);
	}

	public String getName() {
		return name;
	}

	public List<DataFlowLiteral> getLiterals() {
		return literals;
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
