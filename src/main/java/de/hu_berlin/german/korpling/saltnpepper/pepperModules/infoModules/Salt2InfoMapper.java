/**
 * Copyright 2009 Humboldt-Universität zu Berlin, INRIA.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 */
package de.hu_berlin.german.korpling.saltnpepper.pepperModules.infoModules;

import javax.xml.transform.Transformer;

import org.eclipse.emf.common.util.URI;

import de.hu_berlin.german.korpling.saltnpepper.pepper.common.DOCUMENT_STATUS;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.PepperModule;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.exceptions.PepperModuleException;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.impl.PepperMapperImpl;
import de.hu_berlin.german.korpling.saltnpepper.pepperModules.infoModules.ContainerInfo.STATUS;
/**
 * 
 * @author Florian Zipser
 *
 */
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
	/** {@link Transformer} to create an html output if {@link SaltInfoProperties#HTML_OUTPUT} is set to true**/
	private Transformer xsltTransformer= null;
	/** @return {@link Transformer} to create an html output if {@link SaltInfoProperties#HTML_OUTPUT} is set to true **/
	public Transformer getXsltTransformer() {
		return xsltTransformer;
	}
	/** @param xsltTransformer {@link Transformer} to create an html output if {@link SaltInfoProperties#HTML_OUTPUT} is set to true**/
	public void setXsltTransformer(Transformer xsltTransformer) {
		this.xsltTransformer = xsltTransformer;
	}
	@Override
	public DOCUMENT_STATUS mapSCorpus() {
		if (getSCorpus()!= null){
			for (ContainerInfo cont: ((CorpusInfo)getContainerInfo()).getContainerInfos()){
				while	(	(!STATUS.FINISHED.equals(cont.getStatus()))&&
							(!STATUS.ERROR.equals(cont.getStatus()))){
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						throw new PepperModuleException(this, "Cannot send thread to sleep. ", e);
					}
				}
			}
			getContainerInfo().write(getSCorpus());
			if (((SaltInfoProperties) getProperties()).isHtmlOutput()) {
				URI htmlOutput= URI.createFileURI(getResourceURI().toFileString().replace("."+PepperModule.ENDING_XML, ".html"));
				SaltInfoExporter.applyXSLT(getXsltTransformer(), getResourceURI(), htmlOutput);
			}
		}
		return(DOCUMENT_STATUS.COMPLETED);
	}
	
	@Override
	public DOCUMENT_STATUS mapSDocument() {
		if (	(getSDocument()!= null)&&
				(getSDocument().getSDocumentGraph()!= null)){
			((DocumentInfo)getContainerInfo()).retrieveData(getSDocument());
			getContainerInfo().write(getSDocument());
			if (((SaltInfoProperties) getProperties()).isHtmlOutput()) {
				URI htmlOutput= URI.createFileURI(getResourceURI().toFileString().replace("."+PepperModule.ENDING_XML, ".html"));
				SaltInfoExporter.applyXSLT(getXsltTransformer(), getResourceURI(), htmlOutput);
			}
		}
		return(DOCUMENT_STATUS.COMPLETED);
	}
}
