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
package org.corpus_tools.peppermodules.infoModules;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import org.corpus_tools.pepper.impl.PepperExporterImpl;
import org.corpus_tools.pepper.modules.PepperExporter;
import org.corpus_tools.pepper.modules.PepperMapper;
import org.corpus_tools.pepper.modules.PepperModule;
import org.corpus_tools.pepper.modules.exceptions.PepperModuleException;
import org.corpus_tools.pepper.modules.exceptions.PepperModuleNotReadyException;
import org.corpus_tools.peppermodules.infoModules.ContainerInfo.STATUS;
import org.corpus_tools.salt.common.SCorpus;
import org.corpus_tools.salt.common.SCorpusDocumentRelation;
import org.corpus_tools.salt.common.SCorpusGraph;
import org.corpus_tools.salt.common.SCorpusRelation;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.common.SaltProject;
import org.corpus_tools.salt.core.SNode;
import org.corpus_tools.salt.core.SPathElement;
import org.corpus_tools.salt.graph.Identifier;
import org.corpus_tools.salt.graph.Relation;
import org.eclipse.emf.common.util.URI;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This module produces a corpus-site of a corpus. A corpus-site is a homepage
 * for the corpus containing all annotation names and their values and the
 * frequencies of annotations. The corpus site can be extended for further
 * description, to be used as a documentation.
 * 
 * @author Florian Zipser
 * @author Jakob Schmolling
 * @author Vivian Voigt
 * 
 */
@Component(name = "SaltInfoExporterComponent", factory = "PepperExporterComponentFactory")
public class SaltInfoExporter extends PepperExporterImpl implements
		PepperExporter, SaltInfoDictionary {
	static final Logger logger = LoggerFactory.getLogger("SaltInfoExporter");

	public static final String SITE_RESOURCES = "site/";
	public static final String CSS_RESOURCES = "css/";
	public static final String XSLT_INFO_TO_HTML = "xslt/saltInfo2html.xsl";
	public static final String XSLT_INDEX_TO_HTML = "xslt/saltInfo2index.xsl";
	/** name of the file containing the corpus-structure for SaltInfo **/
	public static final String PROJECT_INFO_FILE = "index";

	private static TransformerFactory transFac = null;

	public SaltInfoExporter() {
		super();
		setName("SaltInfoExporter");
		setSupplierContact(URI.createURI("saltnpepper@lists.hu-berlin.de"));
		setSupplierHomepage(URI
				.createURI("https://github.com/korpling/pepperModules-SaltInfoModules"));
		setDesc("This module produces a corpus-site of a corpus. A corpus-site is a homepage for the corpus containing all annotation names and their values and the frequencies of annotations. The corpus site can be extended for further description, to be used as a documentation. ");
		addSupportedFormat(PepperModule.ENDING_XML, "1.0", null);
		addSupportedFormat("html", "5.0", null);
		setProperties(new SaltInfoProperties());
		setProperties(new SaltInfoProperties());
	}

	private URI siteResources = null;
	private URI cssResources = null;

	@Override
	public boolean isReadyToStart() throws PepperModuleNotReadyException {
		boolean isReady = super.isReadyToStart();
		siteResources = URI.createFileURI(getResources().toFileString()
				+ SITE_RESOURCES);
		cssResources = URI.createFileURI(getResources().toFileString()
				+ SITE_RESOURCES + CSS_RESOURCES);

		File file = new File(siteResources.toFileString());
		if (!file.exists()) {
			logger.warn(
					"Pepper module '{}' is not startable, because the folder '{}' does not exist in resource folder: {}.",
					getName(), SITE_RESOURCES, file.getAbsolutePath());
			isReady = false;
		}
		file = new File(cssResources.toFileString());
		if (!file.exists()) {
			logger.warn(
					"Pepper module '{}' is not startable, because the folder '{}' does not exist in resource folder: {}.",
					getName(), CSS_RESOURCES, file.getAbsolutePath());
			isReady = false;
		}

		System.setProperty("javax.xml.transform.TransformerFactory",
				"net.sf.saxon.TransformerFactoryImpl");
		transFac = TransformerFactory.newInstance();
		return (isReady);
	}

	/**
	 * Stores {@link Identifier}s to all {@link SDocument} and {@link SCorpus}
	 * objects and the corresponding {@link ContainerInfo} objects.
	 **/
	private Map<Identifier, ContainerInfo> sElementId2Container = null;

	/**
	 * Fill table {@link #sElementId2Container} and creates a
	 * {@link DocumentInfo} for each {@link SDocument} object and a
	 * {@link CorpusInfo} for each {@link SCorpus} object.
	 */
	@Override
	public void start() throws PepperModuleException {
		sElementId2Container = new Hashtable<>();
		ContainerInfo cont = null;
		for (SCorpusGraph sCorpusGraph : getSaltProject().getCorpusGraphs()) {
			for (SCorpus sCorpus : sCorpusGraph.getCorpora()) {
				cont = new CorpusInfo();
				cont.setsName(sCorpus.getName());
				cont.setIdf(sCorpus.getId());
				sElementId2Container.put(sCorpus.getIdentifier(), cont);
			}
			for (SDocument sDocument : sCorpusGraph.getDocuments()) {
				cont = new DocumentInfo();
				cont.setsName(sDocument.getName());
				cont.setIdf(sDocument.getId());
				sElementId2Container.put(sDocument.getIdentifier(), cont);
			}
		}
		super.start();
	}

	@Override
	public PepperMapper createPepperMapper(Identifier id) {
		Salt2InfoMapper mapper = new Salt2InfoMapper();
		mapper.setContainerInfo(sElementId2Container.get(id));
		if ((id != null) && (id.getIdentifiableElement() != null)) {
			if (id.getIdentifiableElement() instanceof SDocument) {

			} else if (id.getIdentifiableElement() instanceof SCorpus) {
				SCorpus sCorpus = (SCorpus) id.getIdentifiableElement();
				CorpusInfo corpInfo = (CorpusInfo) mapper.getContainerInfo();
				for (Relation edge : sCorpus.getGraph().getOutRelations(
						sCorpus.getId())) {
					if ((edge instanceof SCorpusRelation)
							|| (edge instanceof SCorpusDocumentRelation)) {
						ContainerInfo cont = sElementId2Container
								.get((Identifier) edge.getTarget()
										.getIdentifier());
						corpInfo.getContainerInfos().add(cont);
					}
				}
			}
			URI resource = URI.createFileURI(getCorpusDesc().getCorpusPath()
					.toFileString());
			for (String segment : ((SPathElement) id.getIdentifiableElement())
					.getPath().segments()) {
				resource = resource.appendSegment(segment);
			}
			resource = resource.appendFileExtension(PepperModule.ENDING_XML);
			mapper.setResourceURI(resource);
			mapper.getContainerInfo().setExportFile(
					new File(resource.toFileString()));

			URI xslt = URI.createFileURI(getResources().toFileString()
					+ XSLT_INFO_TO_HTML);

			Transformer transformer = loadXSLTTransformer(xslt.toFileString());
			mapper.setXsltTransformer(transformer);
		}
		return (mapper);
	}

	/**
	 * Writes the info xml file for salt-project containing the corpus-structure
	 * of the SaltInfo
	 */
	@Override
	public void end() throws PepperModuleException {
		super.end();

		XMLOutputFactory xof = XMLOutputFactory.newInstance();
		XMLStreamWriter xml;
		File projectInfoFile = new File(getCorpusDesc().getCorpusPath()
				.appendSegment(PROJECT_INFO_FILE)
				.appendFileExtension(PepperModule.ENDING_XML).toFileString());
		try {
			xml = xof.createXMLStreamWriter(new FileWriter(projectInfoFile));
		} catch (XMLStreamException | IOException e) {
			throw new PepperModuleException("Cannot write salt info to file '"
					+ projectInfoFile + "'. ", e);
		}
		try {
			writeProjectInfo(getSaltProject(), xml);
		} catch (XMLStreamException e) {
			throw new PepperModuleException(this,
					"Cannot write salt info project file '" + projectInfoFile
							+ "'. ", e);
		}
		if (((SaltInfoProperties) getProperties()).isHtmlOutput()) {
			URI htmlOutput = getCorpusDesc().getCorpusPath()
					.appendSegment(PROJECT_INFO_FILE)
					.appendFileExtension("html");
			URI xmlInput = URI.createFileURI(projectInfoFile.getAbsolutePath());
			URI xslt = URI.createFileURI(getResources().toFileString()
					+ XSLT_INDEX_TO_HTML);
			Transformer transformer = loadXSLTTransformer(xslt.toFileString());
			applyXSLT(transformer, xmlInput, htmlOutput);
		}
		// copy resources: css, js, and images
		File resourceFolder = new File(siteResources.toFileString());
		File cssFolder = new File(cssResources.toFileString());
		// first copy the whole site-folder except for the theme-*-folder under
		// the css-directory
		if ((resourceFolder != null) && (!resourceFolder.exists())) {
			logger.warn("Cannot export the resources for project site, since the resource folder is null or does not exist: "
					+ resourceFolder);
		} else {
			try {
				if (((SaltInfoProperties) getProperties())
						.getHtmlInterpretation()) {
					try {
			            File jsFile = new File(getResources().toFileString()+"site/js/saltinfo.js");
			            String fileContext = FileUtils.readFileToString(jsFile);
			            fileContext = fileContext.replaceAll("var INTERPRET_AS_HTML = false;", "var INTERPRET_AS_HTML = true;");
			            FileUtils.write(jsFile, fileContext);
			        } catch (IOException e) {
			        	SaltInfoExporter.logger.warn("No java script file found. Html elements in the short corpus description will be escaped.");
			        }					
				}
				
				FileUtils.copyDirectory(resourceFolder, new File(
						getCorpusDesc().getCorpusPath().toFileString()),
						new FileFilter() {
							public boolean accept(File pathname) {
								String name = pathname.getName();

								return !(name.contains("theme_") && pathname
										.isDirectory());
							}
						}, true);
			} catch (IOException e) {
				logger.warn("Cannot export the resources for project site, because of a nested exception: "
						+ e.getMessage());
			}
		}
		String theme_value = null;
		if (SaltInfoProperties.THEME_DEFAULT
				.equals(((SaltInfoProperties) getProperties()).getTheme())) {
			theme_value = SaltInfoProperties.THEME_DEFAULT;
		} else if (SaltInfoProperties.THEME_HISTORIC
				.equals(((SaltInfoProperties) getProperties()).getTheme())) {
			theme_value = SaltInfoProperties.THEME_HISTORIC;
		}
		File theme = new File(getCorpusDesc().getCorpusPath().toFileString()
				+ "/css/theme/");
		File[] cssFiles = cssFolder.listFiles();
		for (File css : cssFiles) {
			if (css.isDirectory()) {
				try {
					FileUtils.copyDirectory(
							new File(cssResources.toFileString() + "theme_"
									+ theme_value), theme);
				} catch (IOException e) {
					logger.warn("Cannot export the css_theme-resources for project site, because of a nested exception: "
							+ e.getMessage());
				}
			}
		}
	}

	/**
	 * Writes the project info file retrieved out of the {@link SaltProject}
	 * into the passed xml stream.
	 * 
	 * @param saltProject
	 * @param xml
	 * @throws XMLStreamException
	 */
	private void writeProjectInfo(SaltProject saltProject, XMLStreamWriter xml)
			throws XMLStreamException {
		xml.writeStartDocument();
		xml.writeStartElement(TAG_SPROJECT);
		String name = PROJECT_INFO_FILE;
		if (saltProject.getCorpusGraphs().size() == 1) {
			List<SNode> roots = saltProject.getCorpusGraphs().get(0).getRoots();
			if (roots.size() == 1) {
				name = roots.get(0).getName();
			}
		}
		xml.writeAttribute(ATT_SNAME, name);

		Date date = new Date();
		DateFormat dformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		xml.writeAttribute(ATT_GENERATED_ON, dformat.format(date));
		for (SCorpusGraph sCorpusGraph : saltProject.getCorpusGraphs()) {
			List<SNode> roots = sCorpusGraph.getRoots();
			if ((roots != null) && (!roots.isEmpty())) {
				for (SNode sRoot : roots) {
					ContainerInfo cont = sElementId2Container.get(sRoot
							.getIdentifier());
					writeContainerInfoRec(cont, xml);
				}
			}
		}
		xml.writeEndElement();
		xml.writeEndDocument();
		xml.flush();
	}

	/**
	 * Writes single entries for corpora and documents into passed xml stream.
	 * This method is recursive to write the entire corpus structure.
	 **/
	private void writeContainerInfoRec(ContainerInfo cont, XMLStreamWriter xml)
			throws XMLStreamException {
		if (cont != null) {
			if (cont.getExportFile() == null) {
				logger.warn("Cannot store project info file, because no file is given for ContainerInfo '"
						+ cont.getId() + "'. ");
				cont.setStatus(STATUS.ERROR);
				// throw new
				// PepperModuleException("Cannot store project info file, because no file is given for ContainerInfo '"
				// + cont.getId() + "'. ");
			} else {
				String containerTag = null;
				if (cont instanceof CorpusInfo) {
					containerTag = TAG_SCORPUS_INFO;
				} else if (cont instanceof DocumentInfo) {
					containerTag = TAG_SDOCUMENT_INFO;
				}
				xml.writeStartElement(containerTag);
				xml.writeAttribute(ATT_SNAME, cont.getsName());
				xml.writeAttribute(ATT_SID, cont.getId());
				String location = "";

				try {
					location = cont
							.getExportFile()
							.getCanonicalPath()
							.replace(
									getCorpusDesc().getCorpusPath()
											.toFileString(), "");
				} catch (IOException e) {
					location = cont
							.getExportFile()
							.getAbsolutePath()
							.replace(
									getCorpusDesc().getCorpusPath()
											.toFileString(), "");
				}
				// remove prefixing /
				if (location.startsWith("/")) {
					location.replaceFirst("/", "");
				}
				xml.writeAttribute(ATT_LOCATION, location);
				if (cont instanceof CorpusInfo) {
					for (ContainerInfo sub : ((CorpusInfo) cont)
							.getContainerInfos()) {
						writeContainerInfoRec(sub, xml);
					}
				}
				xml.writeEndElement();
			}
		}
	}

	public static void applyXSLT(Transformer transformer, URI xml, URI out) {
		StreamSource source = new StreamSource(new File(xml.toFileString()));
		StreamResult result = new StreamResult(new File(out.toFileString()));
		try {
			transformer.transform(source, result);
			logger.debug(String.format("XSL-Transformation completed %s",
					out.path()));
		} catch (TransformerException e) {
			logger.debug("Failed to transform to :\t\t" + out.toFileString());
			logger.debug("from:\t\t" + xml.toFileString());
			logger.debug("with:\t\t" + transformer.toString());
			throw new PepperModuleException(String.format(
					"Can't generate HTML output %s", xml), e);
		}
	}

	/**
	 * Returns a Transformer defined by the salt-info.xslt
	 * 
	 * @return XML Transformer that transform SaltInfo XML to HTML
	 */
	private static Transformer loadXSLTTransformer(String path) {
		File xslt = new File(path);
		if (!xslt.exists()) {
			throw new PepperModuleException(
					"Cannot find xslt transformation to create html output at location "
							+ xslt.getAbsolutePath());
		}
		Transformer t = null;
		try {
			Source xsltSource = new StreamSource(xslt);
			t = transFac.newTransformer(xsltSource);
		} catch (Exception e) {
			throw new PepperModuleException(
					"Can't create xslt transformer for " + path, e);
		}
		return t;
	}
}
