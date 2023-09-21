package org.palladiosimulator.dataflow.diagramgenerator.model;

public class DataFlowLiteral {
	private String typeID;
	private String typeName;
	private String literalID;
	private String literalName;

	public DataFlowLiteral(String enumID, String enumerationName, String literalID, String literalName) {
		super();
		this.typeID = enumID;
		this.typeName = enumerationName;
		this.literalID = literalID;
		this.literalName = literalName;
	}
	
	public void accept(DataFlowLiteralVisitor visitor) {
		visitor.visit(this);
	}

	public String getTypeID() {
		return typeID;
	}

	public String getTypeName() {
		return typeName;
	}

	public String getLiteralID() {
		return literalID;
	}

	public String getLiteralName() {
		return literalName;
	}

}
