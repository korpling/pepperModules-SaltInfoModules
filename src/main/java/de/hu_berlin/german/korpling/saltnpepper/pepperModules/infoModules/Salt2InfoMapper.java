package de.hu_berlin.german.korpling.saltnpepper.pepperModules.infoModules;

import java.io.File;

import de.hu_berlin.german.korpling.saltnpepper.pepper.common.DOCUMENT_STATUS;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.impl.PepperMapperImpl;

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
			getContainerInfo().setExportFile(new File(getResourceURI().toFileString()));
//			((CorpusInfo)getContainerInfo()).retrieveData(getSCorpus());
			getContainerInfo().write(getSCorpus());
		}
		return(DOCUMENT_STATUS.COMPLETED);
	}
	
	@Override
	public DOCUMENT_STATUS mapSDocument() {
		if (	(getSDocument()!= null)&&
				(getSDocument().getSDocumentGraph()!= null)){
			getContainerInfo().setExportFile(new File(getResourceURI().toFileString()));
			((DocumentInfo)getContainerInfo()).retrieveData(getSDocument());
			getContainerInfo().write(getSDocument());
		}
		return(DOCUMENT_STATUS.COMPLETED);
	}
}
