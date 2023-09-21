package org.palladiosimulator.dataflow.diagramgenerator.pcm;

import java.util.ArrayList;
import java.util.List;

import org.palladiosimulator.dataflow.confidentiality.analysis.characteristics.DataStore;
import org.palladiosimulator.dataflow.confidentiality.analysis.entity.pcm.seff.CallingSEFFActionSequenceElement;
import org.palladiosimulator.dataflow.confidentiality.analysis.entity.pcm.seff.DatabaseActionSequenceElement;
import org.palladiosimulator.dataflow.confidentiality.analysis.entity.pcm.seff.SEFFActionSequenceElement;
import org.palladiosimulator.dataflow.confidentiality.analysis.entity.pcm.user.CallingUserActionSequenceElement;
import org.palladiosimulator.dataflow.confidentiality.analysis.entity.sequence.AbstractActionSequenceElement;
import org.palladiosimulator.dataflow.confidentiality.pcm.model.confidentiality.repository.impl.OperationalDataStoreComponentImpl;
import org.palladiosimulator.pcm.parameter.VariableUsage;
import org.palladiosimulator.pcm.parameter.impl.VariableUsageImpl;
import org.palladiosimulator.pcm.repository.Parameter;
import org.palladiosimulator.pcm.repository.impl.OperationSignatureImpl;
import org.palladiosimulator.pcm.repository.impl.ParameterImpl;
import org.palladiosimulator.pcm.seff.impl.AbstractActionImpl;
import org.palladiosimulator.pcm.seff.impl.ExternalCallActionImpl;
import org.palladiosimulator.pcm.seff.impl.ProbabilisticBranchTransitionImpl;
import org.palladiosimulator.pcm.seff.impl.ResourceDemandingBehaviourImpl;
import org.palladiosimulator.pcm.seff.impl.ResourceDemandingSEFFImpl;
import org.palladiosimulator.pcm.seff.impl.SetVariableActionImpl;
import org.palladiosimulator.pcm.seff.impl.StartActionImpl;
import org.palladiosimulator.pcm.usagemodel.impl.EntryLevelSystemCallImpl;
import org.palladiosimulator.pcm.usagemodel.impl.ScenarioBehaviourImpl;
import org.palladiosimulator.pcm.usagemodel.impl.UsageScenarioImpl;

import de.uka.ipd.sdq.stoex.AbstractNamedReference;
import de.uka.ipd.sdq.stoex.VariableReference;
import de.uka.ipd.sdq.stoex.impl.VariableReferenceImpl;

public class PCMEntityUtility {
	private static final String SPECIAL_RETURN_VARIABLE = "RETURN";

	public static boolean isBranch(AbstractActionSequenceElement<?> element) {
		if (element instanceof SEFFActionSequenceElement<?> sase) {
			if (sase.getElement() instanceof StartActionImpl sai) {
				var seff = sai.getResourceDemandingBehaviour_AbstractAction();
				if (seff instanceof ResourceDemandingBehaviourImpl rdb) {
					ProbabilisticBranchTransitionImpl branch = (ProbabilisticBranchTransitionImpl) rdb
							.getAbstractBranchTransition_ResourceDemandingBehaviour();
					if (branch != null) {
						return true;
					}
				}
			}
		}

		return false;
	}

	public static String getEntityName(AbstractActionSequenceElement<?> element) {
		String name = null;
		if (element instanceof CallingUserActionSequenceElement cuase) {
			name = cuase.getElement().getEntityName();
		} else if (element instanceof SEFFActionSequenceElement<?> sase) {
			var innerElement = sase.getElement();
			// BasicComponentImpl bc = (BasicComponentImpl)
			// innerElement.eContainer().eContainer();
			// String containerName = bc.getEntityName();
			name = ((AbstractActionImpl) ((SEFFActionSequenceElement<?>) element).getElement()).getEntityName();
			if (innerElement instanceof ExternalCallActionImpl eca) {
			} else if (innerElement instanceof StartActionImpl sa) {
				var seff = sa.getResourceDemandingBehaviour_AbstractAction();
				if (seff instanceof ResourceDemandingSEFFImpl rds) {
					OperationSignatureImpl operation = (OperationSignatureImpl) rds.getDescribedService__SEFF();
					name = operation.getEntityName();
				} else if (seff instanceof ResourceDemandingBehaviourImpl rdb) {
					ProbabilisticBranchTransitionImpl branch = (ProbabilisticBranchTransitionImpl) rdb
							.getAbstractBranchTransition_ResourceDemandingBehaviour();
					name = branch.getEntityName();
				} else {
					throw new Error("Not implemented");
				}
			} else if (innerElement instanceof SetVariableActionImpl sva) {
			} else {
				throw new Error("Not implemented");
			}
		} else if (element instanceof DatabaseActionSequenceElement<?> dase) {
			DataStore dataStore = dase.getDataStore();
			return dataStore.getDatabaseComponentName();
		} else {
			throw new Error("Not implemented");
		}
		return name;
	}

	public static String getActorName(AbstractActionSequenceElement<?> element) {
		String actorName = null;
		if (element instanceof CallingUserActionSequenceElement cuase) {
			EntryLevelSystemCallImpl elsc = (EntryLevelSystemCallImpl) cuase.getElement();
			ScenarioBehaviourImpl sbi = (ScenarioBehaviourImpl) elsc.getScenarioBehaviour_AbstractUserAction();
			if (sbi != null) {
				UsageScenarioImpl usi = (UsageScenarioImpl) sbi.getUsageScenario_SenarioBehaviour();
				if (usi != null) {
					actorName = usi.getEntityName();
				}
			}
		}
		return actorName;
	}

	public static Boolean getIsCalling(AbstractActionSequenceElement<?> element) {
		if (element instanceof CallingUserActionSequenceElement cuase) {
			return cuase.isCalling();
		} else if (element instanceof CallingSEFFActionSequenceElement csase) {
			return csase.isCalling();
		} else if (element instanceof DatabaseActionSequenceElement<?> dase) {
			return null;
		} else {
			return null;
		}
	}

	public static String getEntityId(AbstractActionSequenceElement<?> element) {
		if (element instanceof CallingUserActionSequenceElement) {
			var innerElement = ((CallingUserActionSequenceElement) element).getElement();
			if (innerElement instanceof EntryLevelSystemCallImpl) {
			} else {
				throw new Error("Not implemented!");
			}
			return ((CallingUserActionSequenceElement) element).getElement().getId();
		} else if (element instanceof SEFFActionSequenceElement) {
			var innerElement = ((SEFFActionSequenceElement<?>) element).getElement();
			if (innerElement instanceof StartActionImpl) {
			} else if (innerElement instanceof ExternalCallActionImpl) {
			} else if (innerElement instanceof SetVariableActionImpl) {
			} else {
				throw new Error("Not implemented!");
			}
			return ((AbstractActionImpl) ((SEFFActionSequenceElement<?>) element).getElement()).getId();
		} else if (element instanceof DatabaseActionSequenceElement<?> dase) {
			OperationalDataStoreComponentImpl innerElement = (OperationalDataStoreComponentImpl) dase.getElement();
			return innerElement.getId();
		} else {
			throw new Error("Not implemented!");
		}
	}

	public static String getParameterString(AbstractActionSequenceElement<?> element) {
		String result = "";
		if (element instanceof SEFFActionSequenceElement<?> sase) {
			List<Parameter> parameters = sase.getParameter();
			for (var entry : parameters) {
				ParameterImpl parameter = (ParameterImpl) entry;
				if (result.length() == 0) {
					result = parameter.getParameterName();
				} else {
					result += ", " + parameter.getParameterName();
				}
			}
		}

		return result;
	}

	public static String getRETURNParameter(AbstractActionSequenceElement<?> element) {

		if (element instanceof SEFFActionSequenceElement<?> sase) {
			if (sase.getElement() instanceof SetVariableActionImpl sva) {
				List<VariableUsage> variableUsages = sva.getLocalVariableUsages_SetVariableAction();

				for (VariableUsage vu : variableUsages) {
					VariableReferenceImpl vr = (VariableReferenceImpl) vu.getNamedReference__VariableUsage();

					String name = vr.getReferenceName();

					if (name.equals(SPECIAL_RETURN_VARIABLE)) {
						return name;
					}
				}

			}
		}
		return null;
	}

	public static List<String> getParameters(AbstractActionSequenceElement<?> element) {
		List<String> paras = new ArrayList<String>();
		if (element instanceof SEFFActionSequenceElement<?> sase) {
			if (sase.getElement() instanceof SetVariableActionImpl sva) {
				List<VariableUsage> variableUsages = sva.getLocalVariableUsages_SetVariableAction();
			}

			List<Parameter> parameters = sase.getParameter();
			for (var entry : parameters) {
				ParameterImpl parameter = (ParameterImpl) entry;
				paras.add(parameter.getParameterName());
			}
		}

		return paras;
	}
}
