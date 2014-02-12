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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Semaphore;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.osgi.service.blueprint.reflect.MapEntry;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.log.LogService;

import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperExceptions.PepperModuleException;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.PepperExporter;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.PepperMapper;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.impl.PepperExporterImpl;
import de.hu_berlin.german.korpling.saltnpepper.salt.graph.IdentifiableElement;
import de.hu_berlin.german.korpling.saltnpepper.salt.graph.Node;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.info.InfoModule;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SCorpus;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SElementId;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SIdentifiableElement;

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
	final String[] defaultResources = {"/css/saltinfo.css",
		  "/css/index.css",
		  "/js/saltinfo.js",
		  "/js/jquery.js",
		  "/img/information.png",
		  "/img/SaltNPepper_logo2010.svg"};

	URI outputPath;

	private int documentCount = 0;
	
	private final InfoModule im = new InfoModule();

	private EList<Node> roots = ECollections.emptyEList();

	// default charset
	private final Charset charset ;
	
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
	
//	Logger log = LogManager.getLogger(InfoModuleExporter.class);
	
	public InfoModuleExporter() {
			super();
		{
			this.charset = Charset.forName("UTF-8");
			this.resources.addAll(Arrays.asList(defaultResources));
			this.name = "InfoModuleExporter";
			this.addSupportedFormat("xml", "1.0",
					URI.createURI("https://korpling.german.hu-berlin.de/p/projects/peppermodules-statisticsmodules"));
//			this.setProperties(new InfoModuleProperties());
			System.out.println("Created InfoModuleExporter: ");
			
			URI specialParams = getSpecialParams();
			if(specialParams != null){
				loadOptions(specialParams);
			}
			
			//TODO: remove:
			im.setCaching(true);
			// for safety reasons
			this.setIsMultithreaded(false);
		 }
	}
	
	/**
	 * 
	 * TODO: load Options
	 */
	private Properties loadOptions(final URI specialParams){
		Properties options = new Properties();
		if (this.getSpecialParams()!= null)
		{//init options
			File optionsFile= new File(specialParams.toFileString());
			if (!optionsFile.exists())
				this.getLogService().log(LogService.LOG_WARNING, "Cannot load special param file at location '"+optionsFile.getAbsolutePath()+"', because it does not exist.");
			else
			{
//				this.options= new Properties();
				try {
					options.load(new FileInputStream(optionsFile));
				} catch (FileNotFoundException e) {
					throw new PepperModuleException("File not found: " + specialParams ,e);
				} catch (IOException e) {
					throw new PepperModuleException("Could not read file: " + specialParams, e);
				}
			}
		}//init options
		return options;
	}
	
	
//	@Override
//	public void start() throws PepperModuleException {
//		// TODO Auto-generated method stub
//		super.start();
//	}
	@Override
	public void end() throws PepperModuleException {
		super.end();
//		URL saltinfocss = this.getClass().getResource(("/xslt/salt-info.xslt"));
		System.out.println("End infomodule export " + this.getName());
		for (String resName : resources) {
			URL res = this.getClass().getResource(resName);
			URI out = URI.createFileURI("."+resName).resolve(outputPath);
			System.out.println("\tCreating resource file: " + res + " saving to: " + out);
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
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		// create index.html
		for (Node n : roots) {
			System.out.println("Roots:");
			System.out.println("\t" + n);
			if(n instanceof SCorpus){
				SCorpus root = (SCorpus) n;
				try {
					waitForSubDocuments(root);
					URI rootxml = getInfoXMLPath(root);
					URI index = outputPath.trimSegments(1).appendSegment("index").appendFileExtension("html");
					writeProduct(loadXSLTTransformer(XSLT_INFO2INDEX_XSL), rootxml, index);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					throw new PepperModuleException("Root is not ready",e);
				}
				
				
			}
		}
		
	}
	private URI getInfoXMLPath(final SIdentifiableElement elem) {
		// TODO Auto-generated method stub
		return InfoModule.getDocumentLocationURI(elem, outputPath).appendFileExtension(XML_FILE_EXTENSION);
	}

	/**
	 * Creates a mapper of type {@link Salt2InfoMapper}.
	 */
	@Override
	public PepperMapper createPepperMapper(SElementId sElementId) {
		outputPath = getCorpusDefinition().getCorpusPath();
		System.out.println(String.format("Mapper for sElement:\n \t%s\n \toutput path: %s",sElementId,outputPath ) );
		++documentCount;
		if(!outputPath.hasTrailingPathSeparator()){
			outputPath = outputPath.appendSegment(URI.encodeSegment("", false));
		}
		PepperMapper mapper = new Salt2InfoMapper(this, Charset.forName("UTF-8"));
		((Salt2InfoMapper)mapper).setOutputPath(outputPath);
		mapper.setProperties(this.getProperties());
		
		
		// Location für Ressourcen Folder
		getResources();
		System.out.println("\tCreating PepperMapper for:\n\t\t"
				+ sElementId.getIdentifiableElement());
		IdentifiableElement elem = sElementId.getIdentifiableElement();
		if (elem instanceof SDocument) {
			final SDocument sdoc = (SDocument) elem;
			System.out.println("\t\tMapping SDocument:\n\t\t" + sElementId);
			
			//Move to export Graph
			URI out = getInfoXMLPath(sdoc);
			getSElementId2ResourceTable().put(sElementId, out);
			System.out.println("\t\tElementPath: \t" + sdoc.getSElementPath());
			System.out.println("\t\tRessourceTable entries= "
					+ getSElementId2ResourceTable().size());

			mapper.setResourceURI(out);
//			Semaphore s = new Semaphore(0);
//			System.out.println(String.format("Syncing document %s / %s", sdoc.getSId(),s.availablePermits()));
//			syncMap.put(sdoc.getSId(), s);
		} else if (elem instanceof SCorpus) {
			// html export is 1 step
			++documentCount;
			final SCorpus scorpus = (SCorpus) elem;
			URI out = getInfoXMLPath(scorpus);
			getSElementId2ResourceTable().put(sElementId, out);
			mapper.setResourceURI(out);
			System.out.println("> Mapping SCorpus " + elem + " to " + out);
			
			roots  = scorpus.getSCorpusGraph().getRoots();
			
			Semaphore s = new Semaphore(1 - scorpus.getSCorpusGraph().getOutEdges(sElementId.getId()).size());
//			syncMap.put(scorpus.getSId(), s);
			System.out.println(String.format("Syncing document %s / %s", scorpus.getSId(),s.availablePermits()));
		}
		
		return (mapper);

	}
	
	@Override
	public void start() throws PepperModuleException {
		System.out.println(syncMap.toString());
		super.start();
		
	}
	
	public void releaseSubDocuments(SCorpus scorpus){
		Semaphore s;
		synchronized (syncMap) {
			s = syncMap.get(scorpus.getSId());
			if (s == null){
				s = createSyncEntry(scorpus);
				s.release();
			}else{
				s.release();
				System.out.println(String.format("release %s  / %s permits", scorpus, s.availablePermits()));
			}
			
		}
		

	}

	private Semaphore createSyncEntry(SCorpus scorpus) {
		Semaphore s;
		s = new Semaphore(1 - scorpus.getSCorpusGraph().getOutEdges(scorpus.getSId()).size());
		System.out.println(String.format("New semaphore for document %s / %s", scorpus.getSId(),s.availablePermits()));
		synchronized (syncMap) {
			syncMap.put(scorpus.getSId(), s);	
		}
		return s;
	}
	
	public void waitForSubDocuments(SCorpus scorpus) throws InterruptedException, PepperModuleException {
		Semaphore s;
		System.out.println(syncMap);
		synchronized(syncMap){
			 s = syncMap.get(scorpus.getSId());
		}
		
		if(s != null){
			int r = (int) (Math.random()*10000);
			System.out.println(String.format("Acquiring %s / %s -- %s" ,scorpus.getSId(), s.availablePermits() , r ));
			System.out.println(r);
			s.acquire();
			System.out.println("---" + r);
		}else{
			createSyncEntry(scorpus);
		}
	}
	
	synchronized public void writeProduct(Transformer transformer, URI xml, URI out) {
	
		StreamSource source = new StreamSource(new File(xml.toFileString()));
		StreamResult result = new StreamResult(new File(
				out.toFileString()));
		try {
			System.out.println(String.format("XSL-Transformation to %s", out.path()));
			transformer.transform(source, result);
		} catch (TransformerException e) {
			System.out.println("Failed to transform to :\t\t" + out.toFileString());
			System.out.println("from:\t\t" + xml.toFileString());
			System.out.println("with:\t\t" + transformer.toString());
			throw new PepperModuleException("Can't generate HTML output", e);
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
			// if(cachedXSLT == null){
			URL res = this.getClass().getResource(path);
			Source xsltSource = new StreamSource(res.openStream(),
					res.toString());
			t = transFac.newTransformer(xsltSource);
			// }
		} catch (Exception e) {
			throw new PepperModuleException("Can't create xslt cache", e);
		}
		return t;
	}

	public double getDocumentCount() {
		// TODO Auto-generated method stub
		return documentCount;
	}
	
	/*
	 * TODO: Change path for temporaries
	 * (non-Javadoc)
	 * @see de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.impl.PepperModuleImpl#setTemproraries(org.eclipse.emf.common.util.URI)
	 */
	@Override
	public void setTemproraries(URI newTemproraries) {
		// TODO Auto-generated method stub
		super.setTemproraries(newTemproraries);
	}

	public URI getOutputPath() {
		// TODO Auto-generated method stub
		return outputPath;
	}



}
