package de.hu_berlin.german.korpling.saltnpepper.pepperModules.infoModules;

import java.io.File;

import de.hu_berlin.german.korpling.saltnpepper.pepper.common.DOCUMENT_STATUS;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.exceptions.PepperModuleException;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.impl.PepperMapperImpl;
import de.hu_berlin.german.korpling.saltnpepper.pepperModules.infoModules.ContainerInfo.STATUS;

public class Salt2InfoMapper extends PepperMapperImpl{
	/** The containerInfo to be filled in this mapping. **/
	private ContainerInfo containerInfo= null;
	/** @return containerInfo to be filled in this mapping. **/
	public ContainerInfo getContainerInfo() {
		return containerInfo;
	}
	/** @param containerInfo containerInfo to be filled in this mapping. **/
	public void setContainerInfo(ContainerInfo containerInfo) {
		this.containerInfo = containerInfo;
	}

	@Override
	public DOCUMENT_STATUS mapSCorpus() {
		if (getSCorpus()!= null){
			System.out.println("Set file for '"+getSCorpus().getSId()+"' to "+ getResourceURI().toFileString());
//			getContainerInfo().setExportFile(new File(getResourceURI().toFileString()));
			for (ContainerInfo cont: ((CorpusInfo)getContainerInfo()).getContainerInfos()){
				while	(	(!STATUS.FINISHED.equals(cont.getStatus()))&&
							(!STATUS.ERROR.equals(cont.getStatus()))){
					System.out.println(getContainerInfo().getSId()+": still waiting for "+ cont.getSId()+ "status is "+ cont.getStatus());
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						throw new PepperModuleException(this, "Cannot send thread to sleep. ", e);
					}
				}
			}
			getContainerInfo().write(getSCorpus());
		}
		return(DOCUMENT_STATUS.COMPLETED);
	}
	
	@Override
	public DOCUMENT_STATUS mapSDocument() {
		if (	(getSDocument()!= null)&&
				(getSDocument().getSDocumentGraph()!= null)){
//			getContainerInfo().setExportFile(new File(getResourceURI().toFileString()));
			((DocumentInfo)getContainerInfo()).retrieveData(getSDocument());
			getContainerInfo().write(getSDocument());
		}
		return(DOCUMENT_STATUS.COMPLETED);
	}
}
