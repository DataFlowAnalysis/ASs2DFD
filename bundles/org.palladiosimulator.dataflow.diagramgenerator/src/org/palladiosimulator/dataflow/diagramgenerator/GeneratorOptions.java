package org.palladiosimulator.dataflow.diagramgenerator;

public class GeneratorOptions {
	private static GeneratorOptions instance;
	private String projectName;
	private String usageModelPath;
	private String allocationPath;
	private String characteristicsPath;
	private boolean drawNodeCharacteristics;
	private boolean drawVariables;
	private boolean drawControlFlow;
	private boolean drawOnlyNumbers;
	private boolean drawParameters;
	private boolean drawViolations;
	private boolean drawUncertainty;
	
	private GeneratorOptions() {
		drawNodeCharacteristics = true;
		drawVariables = true;
		drawControlFlow = false;
		drawOnlyNumbers = false;
		drawParameters = true;
		drawViolations = false;
		drawUncertainty = false;
	}
	
	public boolean isDrawUncertainty() {
		return drawUncertainty;
	}

	public boolean isDrawViolations() {
		return drawViolations;
	}

	public void setDrawViolations(boolean drawViolations) {
		this.drawViolations = drawViolations;
	}

	public void setDrawUncertainty(boolean drawUncertainty) {
		this.drawUncertainty = drawUncertainty;
	}



	public static synchronized GeneratorOptions getInstance() {
		if (instance == null) {
			instance = new GeneratorOptions();
		}
		return instance;
	}

	public boolean isDrawParameters() {
		return drawParameters;
	}

	public void setDrawParameters(boolean drawParameters) {
		this.drawParameters = drawParameters;
	}

	public boolean isDrawOnlyNumbers() {
		return drawOnlyNumbers;
	}

	public void setDrawOnlyNumbers(boolean drawOnlyNumbers) {
		this.drawOnlyNumbers = drawOnlyNumbers;
	}

	public String getCharacteristicsPath() {
		return characteristicsPath;
	}

	public void setCharacteristicsPath(String characteristicsPath) {
		this.characteristicsPath = characteristicsPath;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getUsageModelPath() {
		return usageModelPath;
	}

	public void setUsageModelPath(String usageModelPath) {
		this.usageModelPath = usageModelPath;
	}

	public String getAllocationPath() {
		return allocationPath;
	}

	public void setAllocationPath(String allocationPath) {
		this.allocationPath = allocationPath;
	}

	public boolean isValid() {
		return projectName != null && usageModelPath != null && allocationPath != null;
	}

	public boolean isDrawNodeCharacteristics() {
		return drawNodeCharacteristics;
	}

	public void setDrawNodeCharacteristics(boolean drawNodeCharacteristics) {
		this.drawNodeCharacteristics = drawNodeCharacteristics;
	}

	public boolean isDrawVariables() {
		return drawVariables;
	}

	public void setDrawVariables(boolean drawVariables) {
		this.drawVariables = drawVariables;
	}

	public boolean isDrawControlFlow() {
		return drawControlFlow;
	}

	public void setDrawControlFlow(boolean drawControlFlow) {
		this.drawControlFlow = drawControlFlow;
	}
}
