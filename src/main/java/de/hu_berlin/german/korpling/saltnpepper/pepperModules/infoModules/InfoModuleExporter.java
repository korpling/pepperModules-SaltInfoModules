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
import java.util.Collections;
import java.util.Properties;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
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

	URI outputPath;

	private int documentCount = 0;
	
	private final InfoModule im = new InfoModule();

	private EList<Node> roots = ECollections.emptyEList();
	
	private static final String XSLT_INFO2HTML_XSL = "/xslt/info2html.xsl";
	private static final String XSLT_INFO2INDEX_XSL = "/xslt/info2index.xsl";
	static private Transformer info2html = null;
	
	public static Transformer getInfo2html() {
		return info2html;
	}

	public static Transformer getInfo2index() {
		return info2index;
	}

	static private Transformer info2index = null;
	

	
	public InfoModule getIm() {
		return im;
	}
	
//	Logger log = LogManager.getLogger(InfoModuleExporter.class);
	
	public InfoModuleExporter() {
			super();
		{
			// TODO /2/: change the name of the module, for example use the format
			// name and the ending Exporter (FORMATExporter)
			this.name = "InfoModuleExporter";
			this.addSupportedFormat("xml", "1.0",
					URI.createURI("https://korpling.german.hu-berlin.de/p/projects/peppermodules-statisticsmodules"));
//			this.setProperties(new InfoModuleProperties());
			System.out.println("Created InfoModuleExporter: ");
			
			//TODO: remove:
			im.setCaching(true);
			
			info2html = loadXSLTTransformer(XSLT_INFO2HTML_XSL);
//			info2index = loadXSLTTransformer(XSLT_INFO2INDEX_XSL);
		 }
	}
	
	/**
	 * 
	 * TODO: load Options
	 */
	private Properties loadOptions(final String path){
		Properties options = new Properties();
		if (this.getSpecialParams()!= null)
		{//init options
			File optionsFile= new File(path);
			if (!optionsFile.exists())
				this.getLogService().log(LogService.LOG_WARNING, "Cannot load special param file at location '"+optionsFile.getAbsolutePath()+"', because it does not exist.");
			else
			{
//				this.options= new Properties();
				try {
					options.load(new FileInputStream(optionsFile));
				} catch (FileNotFoundException e) {
					throw new PepperModuleException("File not found: " + path ,e);
				} catch (IOException e) {
					throw new PepperModuleException("Could not read file: " + path, e);
				}
			}
		}//init options
		return options;
	}
	
	
	
	@Override
	public void end() throws PepperModuleException {
		super.end();
//		URL saltinfocss = this.getClass().getResource(("/xslt/salt-info.xslt"));
		String[] resources = {"/css/saltinfo.css",
			  				  "/css/index.css",
							  "/js/saltinfo.js",
							  "/js/jquery.js",
							  "/img/information.png",
							  "/img/SaltNPepper_logo2010.svg"};
		for (String resName : resources) {
			URL res = this.getClass().getResource(resName);
			URI out = URI.createFileURI("."+resName).resolve(outputPath);
			System.out.println("Creating resource file: " + res + " saving to: " + out);
			File fout = new File(out.toFileString());
			//TODO: Switch overwriting
			if(true){
				fout.getParentFile().mkdirs();
				try {
					FileOutputStream fos = new FileOutputStream(fout);
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
		
		for (Node n : roots) {
			System.out.println("Roots");
			System.out.println(n);
			if(n instanceof SCorpus){
				SCorpus root = (SCorpus) n;
				URI rootxml = InfoModule.getDocumentLocationURI(root, outputPath);
				URI index = outputPath.trimSegments(1).appendSegment("index").appendFileExtension("html");
				info2index = loadXSLTTransformer(XSLT_INFO2INDEX_XSL);
				writeProduct(info2index, rootxml, index);
				
			}
		}
		
	}
	/**
	 * Creates a mapper of type {@link Salt2InfoMapper}.
	 */
	@Override
	public PepperMapper createPepperMapper(SElementId sElementId) {
		outputPath = getCorpusDefinition().getCorpusPath();
		System.out.println(String.format("Mapper for sElement %s output path: %s",sElementId,outputPath ) );
		++documentCount;
		if(!outputPath.hasTrailingPathSeparator()){
			outputPath = outputPath.appendSegment(URI.encodeSegment("", false));
		}
		PepperMapper mapper = new Salt2InfoMapper(this, Charset.forName("UTF-8"));
		((Salt2InfoMapper)mapper).setOutputPath(outputPath);
		mapper.setProperties(this.getProperties());
		
		
		// Location für Ressourcen Folder
		getResources();
		System.out.println("+Creating PepperMapper for "
				+ sElementId.getIdentifiableElement());
		IdentifiableElement elem = sElementId.getIdentifiableElement();
		if (elem instanceof SDocument) {
			final SDocument sdoc = (SDocument) elem;
			System.out.println("> Mapping SDocument" + sElementId);
			
			//Move to export Graph
//			String infoFileLocation = sdoc.getSElementPath().toString()
//					.substring("salt:/".length())
//					+ ".xml";
//			URI out = getCorpusDefinition().getCorpusPath().appendSegments(
//					infoFileLocation.split("/"));
//			log.debug(String.format("path: %s, root: %s", sdoc.getSElementPath().path(), outputPath));
			URI out = InfoModule.getDocumentLocationURI(sdoc, outputPath).appendFileExtension(XML_FILE_EXTENSION);
			getSElementId2ResourceTable().put(sElementId, out);
			System.out.println("ElementPath: " + sdoc.getSElementPath());
			System.out.println("RessourceTable: Entries= "
					+ getSElementId2ResourceTable().size());
//			for (URI resource : getSElementId2ResourceTable().values()) {
//				System.out.println("\tR= " + resource);
//			}
			mapper.setResourceURI(out);
		} else if (elem instanceof SCorpus) {
			// html export is 1 step
			++documentCount;
			final SCorpus scorpus = (SCorpus) elem;
			URI out = InfoModule.getDocumentLocationURI(scorpus, outputPath).appendFileExtension(XML_FILE_EXTENSION);
			getSElementId2ResourceTable().put(sElementId, out);
			mapper.setResourceURI(out);
			System.out.println("> Mapping SCorpus " + elem + " to " + out);
			
			roots  = scorpus.getSCorpusGraph().getRoots();
			
		}
		
		return (mapper);

	}
	
	public void writeProduct(Transformer transformer,URI xml,
			URI out) {

		System.out.println("Transform to : " + out);
		StreamSource source = new StreamSource(new File(xml.toFileString()));
		StreamResult result = new StreamResult(new File(
				out.toFileString()));
		try {
			transformer.transform(source, result);
		} catch (TransformerException e) {
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
			TransformerFactory transFac = TransformerFactory.newInstance();
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
