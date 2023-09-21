# Documentation

## Overview

![Class Diagram Overview](class-diagram_overview.svg)

Usage:
```java
GeneratorOptions options = GeneratorOptions.getInstance();

DataFlowConfidentialityAnalysis analysis;

PCMDiagramGenerator diagramGenerator = new PCMDiagramGenerator(options, analysis);

PlantUMLDrawingStrategy drawer = new PlantUMLDrawingStrategy();
PCMDataFlowElementFactory creator = PCMDataFlowElementFactory.getInstance();

Predicate condition;

PCMGraphProcessor processor = new PCMGraphProcessor(creator, condition);

diagramGenerator.generateDataFlowDiagram(drawer, processor);
```

## Model

![Class Diagram Model](class-diagram_model.svg)

## PCM

![Class Diagram PCM](class-diagram_pcm.svg)

## PlantUML

![Class Diagram PlantUML](class-diagram_plantuml.svg)