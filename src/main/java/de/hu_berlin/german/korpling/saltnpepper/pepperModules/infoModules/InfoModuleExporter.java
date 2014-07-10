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
import org.apache.log4j.Logger;

import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.PepperExporter;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.PepperMapper;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.exceptions.PepperModuleException;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.impl.PepperExporterImpl;
import de.hu_berlin.german.korpling.saltnpepper.salt.graph.Edge;
import de.hu_berlin.german.korpling.saltnpepper.salt.graph.GRAPH_TRAVERSE_TYPE;
import de.hu_berlin.german.korpling.saltnpepper.salt.graph.IdentifiableElement;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.SaltProject;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.info.InfoModule;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SCorpus;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SCorpusGraph;
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
 * <li>a place to store temprorary datas for processing can be accessed via
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
@Component(name = "InfoModuleExporterComponent", factory = "PepperExporterComponentFactory")
public class InfoModuleExporter extends PepperExporterImpl implements
		PepperExporter {
	
	private static final String XML_FILE_EXTENSION = "xml";
	final List<String> resources = new ArrayList<String>();
	public final String[] defaultResources = {"/css/saltinfo.css",
		  "/css/index.css",
		  "/js/saltinfo.js",
		  "/js/jquery.js",
		  "/img/information.png",
		  "/img/SaltNPepper_logo2010.svg"};

	URI outputPath;

	private int documentCount = 0;
	
	private final InfoModule im = new InfoModule();

	private EList<SNode> roots = new BasicEList<SNode>();

	// default charset
	private final Charset charset;
	
	private final String XSLT_INFO2HTML_XSL = "/xslt/info2html.xsl";
	private final String XSLT_INFO2INDEX_XSL = "/xslt/info2index.xsl";
	private final TransformerFactory transFac = TransformerFactory.newInstance();
	
	public Transformer getInfo2html() {
		return loadXSLTTransformer(XSLT_INFO2HTML_XSL);
	}

	public Transformer getInfo2index() {
		return loadXSLTTransformer(XSLT_INFO2INDEX_XSL);
	}
	
	public InfoModule getIm() {
		return im;
	}
	
	private final Map<String, Semaphore> syncMap = new HashMap<String, Semaphore>();
	
	private Logger logger = Logger.getLogger(InfoModuleExporter.class);
	
	public InfoModuleExporter() {
			super();
		{
			this.charset = Charset.forName("UTF-8");
			this.resources.addAll(Arrays.asList(defaultResources));
			this.setName("InfoModuleExporter");
			this.addSupportedFormat("xml", "1.0",
					URI.createURI("https://korpling.german.hu-berlin.de/p/projects/peppermodules-statisticsmodules"));
			this.setProperties(new InfoModuleProperties());
			logger.debug("Created InfoModuleExporter: ");
						
			//TODO: remove:
			im.setCaching(true);
		 }
	}
	

	class SCorpusTraverser implements SGraphTraverseHandler {

		private PepperExporter exporter;
		private final Set<SCorpus> sCorpusSet;

		public SCorpusTraverser(PepperExporter infoModuleExporter) {
			this.setExporter(infoModuleExporter);
			sCorpusSet = new HashSet<SCorpus>();
		}

		@Override
		public void nodeReached(GRAPH_TRAVERSE_TYPE traversalType,
				String traversalId, SNode currNode, SRelation sRelation,
				SNode fromNode, long order) {
			// TODO Auto-generated method stub
			if (currNode instanceof SCorpus) {
				SCorpus sCorpus = (SCorpus) currNode;
				logger.debug("Started " + sCorpus.getSElementId());
				if (!sCorpusSet.contains(sCorpus)) {
					sCorpusSet.add(sCorpus);
					exporter.start(sCorpus.getSElementId());
				}
			}
		}

		@Override
		public void nodeLeft(GRAPH_TRAVERSE_TYPE traversalType,
				String traversalId, SNode currNode, SRelation edge,
				SNode fromNode, long order) {
			// TODO Auto-generated method stub
		}

		@Override
		public boolean checkConstraint(GRAPH_TRAVERSE_TYPE traversalType,
				String traversalId, SRelation edge, SNode currNode, long order) {
			// TODO Auto-generated method stub
			return currNode instanceof SCorpus;
		}

		public void setExporter(PepperExporter exporter) {
			this.exporter = exporter;
		}

	}
	@Override
	public void end() throws PepperModuleException {
		startCorpusExport();
		SaltProject saltProject = this.getSaltProject();
		String sname = saltProject.getSName();
		if(sname == null){
			sname = "salt-project";
		}
		URI projectXML = URI.createURI(sname)
				.appendFileExtension(XML_FILE_EXTENSION).resolve(outputPath);
		im.writeProjectInfoXML(saltProject, projectXML);
		
//		URL saltinfocss = this.getClass().getResource(("/xslt/salt-info.xslt"));
		logger.debug("End infomodule export " + this.getName());
		copyRessourcesTo(outputPath);
		for (SCorpusGraph corpGraph : this.getSaltProject().getSCorpusGraphs()) {
			roots = corpGraph.getSRoots();
		}
		
		// create index.html
//		for (Node n : roots) {
//			logger.debug("Roots:");
//			logger.debug("\t" + n);
//			if(n instanceof SCorpus){
//				SCorpus root = (SCorpus) n;
//				try {
//					waitForSubDocuments(root);
//					URI rootxml = getInfoXMLPath(root);
//					URI index = URI.createURI("index.html").resolve(outputPath);
//					writeProduct(loadXSLTTransformer(XSLT_INFO2INDEX_XSL), rootxml, index);
//				}
//				catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					throw new PepperModuleException("Root is not ready",e);
//				}
//				
//				
//			}
//		}
//		waitForSubDocuments(root);
//		URI rootxml = getInfoXMLPath(root);
		URI index = URI.createURI("index.html").resolve(outputPath);
		applyXSLT(loadXSLTTransformer(XSLT_INFO2INDEX_XSL), projectXML, index);
		
	}

	public void copyRessourcesTo(URI outputPath) {
		for (String resName : resources) {
			URL res = this.getClass().getResource(resName);
			URI out = URI.createFileURI("."+resName).resolve(outputPath);
			File fout = new File(out.toFileString());
			//TODO: Switch overwriting
			if(true){
				fout.getParentFile().mkdirs();
				try {
					FileOutputStream fos = new FileOutputStream(fout );
					InputStream is = res.openStream();
					byte[] buffer = new byte[1024];
					int len = 0;
					while((len = is.read(buffer)) != -1){
						fos.write(buffer, 0, len);
					}
					is.close();
					fos.close();
					logger.debug("\tcreated resource file: " + res + " saving to: " + out);
				} catch (IOException e) {
					throw new PepperModuleException("Error while copying resource: " + outputPath);
				}
			}
		}
	}



	/**
	 * Start the corpora traversal
	 */
	private void startCorpusExport() throws PepperModuleException{
		SCorpusTraverser t = new SCorpusTraverser(this);
//		logger.debug("CorpusGraphs " + this.getSaltProject().getSCorpusGraphs().size());
		for (SCorpusGraph corpGraph : this.getSaltProject().getSCorpusGraphs()) {
			EList<SNode> startNodes = new BasicEList<SNode>();
//			logger.debug("Corpora " + corpGraph.getSCorpora().size());
			for (SCorpus corpus : corpGraph.getSCorpora()) {
				if (isCorpusEndNode(corpGraph,corpus)) {
					startNodes.add(corpus);
				}
			}
			if (startNodes.isEmpty()){
				throw new PepperModuleException("No corpora to start export in " + corpGraph);
			}else{
				corpGraph.traverse(startNodes, GRAPH_TRAVERSE_TYPE.BOTTOM_UP_BREADTH_FIRST, "start_corpus_export", t,false);				
			}
		}
	}
	
	/**
	 * returns false if the given corpus has another corpus as a child
	 * @param corpGraph
	 * @param corpus
	 * @return
	 */
	private boolean isCorpusEndNode(SCorpusGraph corpGraph, SCorpus corpus) {
		for (Edge e : corpGraph.getOutEdges(corpus.getId())) {
			if (e.getTarget() instanceof SCorpus){
				return false;
			}
		}
		return true;
	}

	private URI getInfoXMLPath(final SIdentifiableElement elem) {
		return InfoModule.getDocumentLocationURI(elem, outputPath).appendFileExtension(XML_FILE_EXTENSION);
	}

	/**
	 * Creates a mapper of type {@link Salt2InfoMapper}.
	 */
	@Override
	public PepperMapper createPepperMapper(SElementId sElementId) {
		PepperMapper mapper = new Salt2InfoMapper(this, Charset.forName("UTF-8"));
		IdentifiableElement elem = sElementId.getIdentifiableElement();
		
		outputPath = getCorpusDesc().getCorpusPath();
		if(!outputPath.hasTrailingPathSeparator()){
			outputPath = outputPath.appendSegment(URI.encodeSegment("", false));
		}
		logger.debug(String.format("creating mapper for sElement:\n \t%s\n \toutput path: %s",sElementId,outputPath ) );
		++documentCount;
		((Salt2InfoMapper)mapper).setOutputPath(outputPath);
		mapper.setProperties(this.getProperties());

		if (elem instanceof SDocument) {
			final SDocument sdoc = (SDocument) elem;
			URI out = getInfoXMLPath(sdoc);
			
			getSElementId2ResourceTable().put(sElementId, out);
			mapper.setResourceURI(out);

			logger.debug("\t\tMapping SDocument:\n\t\t" + sElementId);
			logger.debug("\t\tElementPath: \t" + sdoc.getSElementPath());
			logger.debug("\t\tRessourceTable entries= "
					+ getSElementId2ResourceTable().size());
		} else if (elem instanceof SCorpus) {
			final SCorpus scorpus = (SCorpus) elem;
			URI out = getInfoXMLPath(scorpus);
			Semaphore s = new Semaphore(1 - scorpus.getSCorpusGraph().getOutEdges(sElementId.getId()).size());
			
			getSElementId2ResourceTable().put(sElementId, out);
			mapper.setResourceURI(out);
			// html export is 1 step
			++documentCount;
			
			logger.debug("> Mapping SCorpus " + elem + " to " + out);
			logger.debug(String.format("Syncing document %s / %s", scorpus.getSId(),s.availablePermits()));
		}
		return (mapper);

	}
	
	@Override
	public void start() throws PepperModuleException {
		logger.debug("Starting InfoModuleExport:");
		logger.debug(syncMap.toString());
		
		super.start();
	}
	
	public void release(SCorpus scorpus){
		for (Map.Entry<String, Semaphore> permit : syncMap.entrySet()) {
			logger.debug(String.format("%s: %s", permit.getKey(), permit.getValue().availablePermits()));
		}
		Semaphore s;
		synchronized (syncMap) {
			s = syncMap.get(scorpus.getSId());
			if (s == null){
				s = createSyncEntry(scorpus);
				s.release();
			}else{
				s.release();
				logger.debug(String.format("release %s  / %s permits", scorpus, s.availablePermits()));
			}
		}
	}

	private Semaphore createSyncEntry(SCorpus scorpus) {
		Semaphore s;
		s = new Semaphore(1 - scorpus.getSCorpusGraph().getOutEdges(scorpus.getSId()).size());
		logger.debug(String.format("New semaphore for document %s / %s", scorpus.getSId(),s.availablePermits()));
		synchronized (syncMap) {
			syncMap.put(scorpus.getSId(), s);	
		}
		return s;
	}
	
	/**
	 * This method blocks until all children have released the semaphore for the given scorpus.
	 * @param scorpus
	 * @throws InterruptedException
	 * @throws PepperModuleException
	 */
	public void acquire(SCorpus scorpus) throws InterruptedException, PepperModuleException {
		Semaphore s;
		synchronized(syncMap){
			 s = syncMap.get(scorpus.getSId());
		}
		
		if(s != null){
			s.acquire();
		}else{
			s = createSyncEntry(scorpus);
			s.acquire();
		}
	}
	
	 public void applyXSLT(Transformer transformer, URI xml, URI out) {
		StreamSource source = new StreamSource(new File(xml.toFileString()));
		StreamResult result = new StreamResult(new File(
				out.toFileString()));
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
			Source xsltSource = new StreamSource(res.openStream(),
					res.toString());
			t = transFac.newTransformer(xsltSource);
		} catch (Exception e) {
			throw new PepperModuleException("Can't create xslt cache for " + path, e);
		}
		return t;
	}

	public double getDocumentCount() {
		return documentCount;
	}

	public URI getOutputPath() {
		return outputPath;
	}



}
