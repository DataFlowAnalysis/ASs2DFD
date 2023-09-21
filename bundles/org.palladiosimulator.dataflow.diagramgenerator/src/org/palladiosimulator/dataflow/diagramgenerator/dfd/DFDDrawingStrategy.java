package org.palladiosimulator.dataflow.diagramgenerator.dfd;

import java.util.ArrayList;
import java.util.List;

import org.palladiosimulator.dataflow.diagramgenerator.model.DataFlowElement;
import org.palladiosimulator.dataflow.diagramgenerator.model.DataFlowNode;
import org.palladiosimulator.dataflow.diagramgenerator.model.DrawingStrategy;
import org.palladiosimulator.dataflow.diagramgenerator.model.Flow;

import mdpa.dfd.dataflowdiagram.Node;
import mdpa.dfd.dataflowdiagram.impl.DataFlowDiagramImpl;
import mdpa.dfd.dataflowdiagram.impl.NodeImpl;
import mdpa.dfd.dataflowdiagram.impl.dataflowdiagramFactoryImpl;

public class DFDDrawingStrategy implements DrawingStrategy {
	private mdpa.dfd.dataflowdiagram.Flow DFDFlow;

	@Override
	public void generate(List<DataFlowNode> dataFlowNodes) {
		dataflowdiagramFactoryImpl dfdFactory = new dataflowdiagramFactoryImpl();

		DataFlowDiagramImpl dfd = (DataFlowDiagramImpl) dfdFactory.createDataFlowDiagram();

		DFDDataFlowElementVisitor visitor = new DFDDataFlowElementVisitor();

		for (DataFlowNode node : dataFlowNodes) {
			DataFlowElement element = node.getElement();

			Node dfdNode = (Node) element.accept(visitor);

			dfd.getNodes().add(dfdNode);

			DFDFlowVisitor flowVisitor = new DFDFlowVisitor();
			
			// TODO: Properly connect Nodes with flows

			for (Flow parentFlow : node.getParentFlows()) {
				mdpa.dfd.dataflowdiagram.Flow newFlow = (mdpa.dfd.dataflowdiagram.Flow) parentFlow.accept(flowVisitor);
				if (newFlow != null) {
					newFlow.setDestinationNode(dfdNode);
					
					dfd.getFlows().add(newFlow);
				}
			}

			for (Flow childFlow : node.getChildrenFlows()) {
				mdpa.dfd.dataflowdiagram.Flow newFlow = (mdpa.dfd.dataflowdiagram.Flow) childFlow.accept(flowVisitor);
				if (newFlow != null) {
					newFlow.setSourceNode(dfdNode);
					
					dfd.getFlows().add(newFlow);
				}
			}
			
			// TODO: How to create pins?
		}
		
		var test = dfd.getFlows();

		var i = 1;
	}

	@Override
	public boolean saveToDisk(String path) {
//		ResourceSet resourceSet = new ResourceSetImpl();
//		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
//		.put(Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());
//		resourceSet.getPackageRegistry().put(dataflowdiagramPackage.eNS_URI,
//				dataflowdiagramPackage.eINSTANCE);
//
//		Resource resource = resourceSet.createResource(URI.createFileURI("output/changedModel.dataflowdiagrammodel"));
//
//		resource.getContents().add(dfd);
		return false;
	}

}
