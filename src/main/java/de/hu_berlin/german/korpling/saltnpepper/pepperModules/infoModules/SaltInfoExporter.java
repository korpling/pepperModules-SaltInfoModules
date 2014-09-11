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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Semaphore;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.PepperExporter;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.PepperMapper;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.PepperModule;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.exceptions.PepperModuleException;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.impl.PepperExporterImpl;
import de.hu_berlin.german.korpling.saltnpepper.salt.graph.Edge;
import de.hu_berlin.german.korpling.saltnpepper.salt.graph.GRAPH_TRAVERSE_TYPE;
import de.hu_berlin.german.korpling.saltnpepper.salt.graph.IdentifiableElement;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.SaltProject;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.info.InfoModule;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SCorpus;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SCorpusDocumentRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SCorpusGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SCorpusRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SElementId;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SGraphTraverseHandler;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SIdentifiableElement;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SNode;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SRelation;

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
 * @author Jakob Schmolling
 * @version 0.1
 * 
 */
@Component(name = "SaltInfoExporterComponent", factory = "PepperExporterComponentFactory")
public class SaltInfoExporter extends PepperExporterImpl implements PepperExporter {

	final List<String> resources = new ArrayList<String>();
	public static final String[] defaultResources = { "/css/saltinfo.css", "/css/index.css", "/js/saltinfo.js", "/js/jquery.js", "/img/information.png", "/img/SaltNPepper_logo2010.svg" };

	private static final String XSLT_INFO2HTML_XSL = "/xslt/info2html.xsl";
	private static final String XSLT_INFO2INDEX_XSL = "/xslt/info2index.xsl";
	private static final TransformerFactory transFac = TransformerFactory.newInstance();

	public SaltInfoExporter() {
		super();
		this.resources.addAll(Arrays.asList(defaultResources));
		this.setName("SaltInfoExporter");
		this.addSupportedFormat(PepperModule.ENDING_XML, "1.0", URI.createURI("https://korpling.german.hu-berlin.de/p/projects/peppermodules-statisticsmodules"));
		this.setProperties(new InfoModuleProperties());
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
		for (SCorpusGraph sCorpusGraph: getSaltProject().getSCorpusGraphs()){
			for (SCorpus sCorpus: sCorpusGraph.getSCorpora()){
				sElementId2Container.put(sCorpus.getSElementId(), new CorpusInfo());
			}
			for (SDocument sDocument: sCorpusGraph.getSDocuments()){
				sElementId2Container.put(sDocument.getSElementId(), new DocumentInfo());
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
			System.out.println("RESOURCE: "+ resource);
		}
		return(mapper);
	}
	
	public Transformer getInfo2html() {
		return loadXSLTTransformer(XSLT_INFO2HTML_XSL);
	}

	public Transformer getInfo2index() {
		return loadXSLTTransformer(XSLT_INFO2INDEX_XSL);
	}


	public void applyXSLT(Transformer transformer, URI xml, URI out) {
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
	private Transformer loadXSLTTransformer(String path) {
		Transformer t = null;
		try {
			URL res = this.getClass().getResource(path);
			Source xsltSource = new StreamSource(res.openStream(), res.toString());
			t = transFac.newTransformer(xsltSource);
		} catch (Exception e) {
			throw new PepperModuleException("Can't create xslt cache for " + path, e);
		}
		return t;
	}
}
