/**
 * Copyright 2009 Humboldt University of Berlin, INRIA.
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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FileUtils;
import org.eclipse.emf.common.util.URI;
import org.osgi.service.component.annotations.Component;

import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.PepperExporter;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.PepperMapper;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.PepperModule;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.exceptions.PepperModuleException;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.impl.PepperExporterImpl;
import de.hu_berlin.german.korpling.saltnpepper.salt.graph.Edge;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.SaltProject;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SCorpus;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SCorpusDocumentRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SCorpusGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SCorpusRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SElementId;

/**
 * This is a sample {@link PepperExporter}, which can be used for creating
 * individual Exporters for the Pepper Framework. Therefore you have to take a
 * look to todo's and adapt the code.
 * 
 * <ul>
 * <li>the salt model to fill, manipulate or export can be accessed via
 * {@link #getSaltProject()}</li>
 * <li>special parameters given by Pepper workflow can be accessed via
 * {@link #getSpecialParams()}</li>
 * <li>a place to store temporary datas for processing can be accessed via
 * {@link #getTemproraries()}</li>
 * <li>a place where resources of this bundle are, can be accessed via
 * {@link #getResources()}</li>
 * <li>a logService can be accessed via {@link #getLogService()}</li>
 * </ul>
 * 
 * @author Florian Zipser
 * @author Jakob Schmolling
 * @version 0.1
 * 
 */
@Component(name = "SaltInfoExporterComponent", factory = "PepperExporterComponentFactory")
public class SaltInfoExporter extends PepperExporterImpl implements PepperExporter, SaltInfoDictionary{

	final List<String> resources = new ArrayList<String>();
	public static final String[] defaultResources = { "/css/saltinfo.css", "/css/index.css", "/js/saltinfo.js", "/js/jquery.js", "/img/information.png", "/img/SaltNPepper_logo2010.svg" };
	/** name of the file containing the corpus-structure for SaltInfo**/
	public static final String PROJECT_INFO_FILE="salt-project";
	public static final String XSLT_INFO2HTML_XSL = "/xslt/info2html.xsl";
	public static final String XSLT_INFO2INDEX_XSL = "/xslt/info2index.xsl";
	private static final TransformerFactory transFac = TransformerFactory.newInstance();

	public SaltInfoExporter() {
		super();
		this.resources.addAll(Arrays.asList(defaultResources));
		this.setName("SaltInfoExporter");
		this.addSupportedFormat(PepperModule.ENDING_XML, "1.0", URI.createURI("https://korpling.german.hu-berlin.de/p/projects/peppermodules-statisticsmodules"));
		this.setProperties(new SaltInfoProperties());
	}
	/** Stores {@link SElementId}s to all {@link SDocument} and {@link SCorpus} objects and the corresponding {@link ContainerInfo} objects.**/
	private Map<SElementId, ContainerInfo> sElementId2Container= null; 
	/**
	 * Fill table {@link #sElementId2Container} and creates a {@link DocumentInfo} for each {@link SDocument}
	 * object and a {@link CorpusInfo} for each {@link SCorpus} object.
	 */
	@Override
	public void start() throws PepperModuleException {
		sElementId2Container= new Hashtable<>();
		ContainerInfo cont= null;
		for (SCorpusGraph sCorpusGraph: getSaltProject().getSCorpusGraphs()){
			for (SCorpus sCorpus: sCorpusGraph.getSCorpora()){
				cont= new CorpusInfo();
				cont.setsName(sCorpus.getSName());
				cont.setSIdf(sCorpus.getSId());
				sElementId2Container.put(sCorpus.getSElementId(), cont);
			}
			for (SDocument sDocument: sCorpusGraph.getSDocuments()){
				cont= new DocumentInfo();
				cont.setsName(sDocument.getSName());
				cont.setSIdf(sDocument.getSId());
				sElementId2Container.put(sDocument.getSElementId(), cont);
			}
		}
		super.start();
	}
	
	@Override
	public PepperMapper createPepperMapper(SElementId sElementId) {
		Salt2InfoMapper mapper= new Salt2InfoMapper();
		mapper.setContainerInfo(sElementId2Container.get(sElementId));
		if (	(sElementId!= null)&&
				(sElementId.getSIdentifiableElement()!= null)){
			if (sElementId.getSIdentifiableElement() instanceof SDocument){
				
			}else if(sElementId.getSIdentifiableElement() instanceof SCorpus){
				SCorpus sCorpus= (SCorpus) sElementId.getSIdentifiableElement();
				CorpusInfo corpInfo= (CorpusInfo) mapper.getContainerInfo();
				for (Edge edge: sCorpus.getSCorpusGraph().getOutEdges(sCorpus.getSId())){
					if (	(edge instanceof SCorpusRelation) ||
							(edge instanceof SCorpusDocumentRelation)){
						ContainerInfo cont= sElementId2Container.get((SElementId)edge.getTarget().getIdentifier());
						corpInfo.getContainerInfos().add(cont);
					}
				}
			}
			URI resource= URI.createFileURI(getCorpusDesc().getCorpusPath().toFileString());
			for (String segment: sElementId.getSElementPath().segments()){
				resource= resource.appendSegment(segment);
			}
			resource= resource.appendFileExtension(PepperModule.ENDING_XML);
			mapper.setResourceURI(resource);
			mapper.getContainerInfo().setExportFile(new File(resource.toFileString()));
			Transformer transformer= loadXSLTTransformer(getResources().appendSegment("xslt").appendSegment("info2html").appendFileExtension("xsl").toFileString());
			mapper.setXsltTransformer(transformer);
		}
		return(mapper);
	}
	/**
	 * Writes the info xml file for salt-project containing the corpus-structure of the SaltInfo
	 */
	@Override
	public void end() throws PepperModuleException {
		super.end();
		
		XMLOutputFactory xof = XMLOutputFactory.newInstance();
        XMLStreamWriter xml;
        File projectInfoFile= new File(getCorpusDesc().getCorpusPath().appendSegment("salt-project").appendFileExtension(PepperModule.ENDING_XML).toFileString());
		try {
			xml = xof.createXMLStreamWriter(new FileWriter(projectInfoFile));
		} catch (XMLStreamException | IOException e) {
			throw new PepperModuleException("Cannot write salt info to file '"+projectInfoFile+"'. ", e);
		}
		try {
			writeProjectInfo(getSaltProject(), xml);
		} catch (XMLStreamException e) {
			throw new PepperModuleException(this, "Cannot write salt info project file '"+projectInfoFile+"'. ", e);
		}
		if (((SaltInfoProperties) getProperties()).isHtmlOutput()) {
			URI htmlOutput= getCorpusDesc().getCorpusPath().appendSegment(PROJECT_INFO_FILE).appendFileExtension("html");
			URI xmlInput= URI.createFileURI(projectInfoFile.getAbsolutePath());
			Transformer transformer= loadXSLTTransformer(getResources().appendSegment("xslt").appendSegment("info2index").appendFileExtension("xsl").toFileString());
			applyXSLT(transformer, xmlInput, htmlOutput);
		}
		//copy resources: css, js, and images
		copyResources();
	}
	/**
	 * Writes the project info file retrieved out of the {@link SaltProject} into the passed xml stream.
	 * @param saltProject
	 * @param xml
	 * @throws XMLStreamException
	 */
	private void writeProjectInfo(SaltProject saltProject, XMLStreamWriter xml) throws XMLStreamException{
		xml.writeStartDocument();
		xml.writeStartElement(TAG_SPROJECT);
			xml.writeAttribute(ATT_SNAME, PROJECT_INFO_FILE);
			for (SCorpusGraph sCorpusGraph: saltProject.getSCorpusGraphs()){
				List<SCorpus> roots= sCorpusGraph.getSRootCorpus();
				if (	(roots!= null)&&
						(!roots.isEmpty())){
					for (SCorpus sRoot: roots){
						ContainerInfo cont= sElementId2Container.get(sRoot.getSElementId());
						writeContainerInfoRec(cont, xml);
					}
				}
			}
		xml.writeEndElement();
		xml.writeEndDocument();
		xml.flush();
	}
	/** 
	 * Writes single entries for corpora and documents into passed xml stream. This method is recursive
	 * to write the entire corpus structure.
	**/
	private void writeContainerInfoRec(ContainerInfo cont, XMLStreamWriter xml) throws XMLStreamException{
		if (cont != null){
			String containerTag= null;
			if (cont instanceof CorpusInfo){
				containerTag= TAG_SCORPUS_INFO;
			}else if (cont instanceof DocumentInfo){
				containerTag= TAG_SDOCUMENT_INFO;
			}
			xml.writeStartElement(containerTag);
				xml.writeAttribute(ATT_SNAME, cont.getsName());
				xml.writeAttribute(ATT_SID, cont.getSId());
				String location= "";
				if (cont.getExportFile()== null){
					throw new PepperModuleException("Cannot store project info file, because no file is given for ContainerInfo '"+cont.getSId()+"'. ");
				}
				try {
					location = cont.getExportFile().getCanonicalPath().replace(getCorpusDesc().getCorpusPath().toFileString(), "");
				} catch (IOException e) {
					location = cont.getExportFile().getAbsolutePath().replace(getCorpusDesc().getCorpusPath().toFileString(), "");
				}
				xml.writeAttribute(ATT_LOCATION, location);
				if (cont instanceof CorpusInfo){
					for (ContainerInfo sub: ((CorpusInfo) cont).getContainerInfos()){
						writeContainerInfoRec(sub, xml);
					}
				}
			xml.writeEndElement();
		}
	}

	public static void applyXSLT(Transformer transformer, URI xml, URI out) {
		StreamSource source = new StreamSource(new File(xml.toFileString()));
		StreamResult result = new StreamResult(new File(out.toFileString()));
		try {
			transformer.transform(source, result);
			logger.debug(String.format("XSL-Transformation completed %s", out.path()));
		} catch (TransformerException e) {
			logger.debug("Failed to transform to :\t\t" + out.toFileString());
			logger.debug("from:\t\t" + xml.toFileString());
			logger.debug("with:\t\t" + transformer.toString());
			throw new PepperModuleException(String.format("Can't generate HTML output %s", xml), e);
		}
	}

	/**
	 * Returns a Transformer defined by the salt-info.xslt
	 * 
	 * @return XML Transformer that transform SaltInfo XML to HTML
	 */
	private static Transformer loadXSLTTransformer(String path) {
		File xslt= new File(path);
		if (!xslt.exists()){
			throw new PepperModuleException("Cannot find xslt transformation to create html output at location "+ xslt.getAbsolutePath());
		}
		Transformer t = null;
		try {
			Source xsltSource = new StreamSource(xslt);
			t = transFac.newTransformer(xsltSource);
		} catch (Exception e) {
			throw new PepperModuleException("Can't create xslt transformer for " + path, e);
		}
		return t;
	}
	
	/**
	 * Copies all javascript, css and image resources to the target folder for displaying html output in case of it is set.
	 */
	private void copyResources(){
		if (((SaltInfoProperties)getProperties()).isHtmlOutput()){
			URI jsResource= getResources().appendSegment("js");
			URI cssResource= getResources().appendSegment("css");
			URI imgResource= getResources().appendSegment("img");
			
			File output= null;
			File input= null;
			try {
				input= new File(jsResource.toFileString());
				output= new File(getCorpusDesc().getCorpusPath().appendSegment("js").toFileString());
				FileUtils.copyDirectory(input, output);
				input= new File(cssResource.toFileString());
				output= new File(getCorpusDesc().getCorpusPath().appendSegment("css").toFileString());
				FileUtils.copyDirectory(input, output);
				input= new File(imgResource.toFileString());
				output= new File(getCorpusDesc().getCorpusPath().appendSegment("img").toFileString());
				FileUtils.copyDirectory(input, output);
			} catch (IOException e) {
				throw new PepperModuleException(this, "Cannot copy resource '"+input.getAbsolutePath()+"' to target path '"+output.getAbsolutePath()+"'.", e);
			}
		}
	}
}
