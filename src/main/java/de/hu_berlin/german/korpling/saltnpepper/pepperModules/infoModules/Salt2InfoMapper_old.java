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
import java.nio.charset.Charset;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hu_berlin.german.korpling.saltnpepper.pepper.common.DOCUMENT_STATUS;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.PepperMapper;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.exceptions.PepperModuleException;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.impl.PepperMapperImpl;
import de.hu_berlin.german.korpling.saltnpepper.salt.graph.Edge;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SCorpus;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;

/**
 * A PepperMapper that generates SaltInfo-XML, as defined in
 * saltProjectInfo.xsd, for every SDocument and SCorpus.
 * 
 * The XML files for every corpus can be transformed to HTML.
 * 
 * @author jschmolling
 * 
 */
public class Salt2InfoMapper_old extends PepperMapperImpl implements PepperMapper {
	static private Logger logger = LoggerFactory.getLogger(Salt2InfoMapper_old.class);

	private final Charset charset;
	private URI outputPath;
	final private SaltInfoExporter exporter;

	public Salt2InfoMapper_old(SaltInfoExporter infoModuleExporter, Charset c) {
		this.exporter = infoModuleExporter;
		this.charset = c;
	}

	public void setOutputPath(final URI outputPath) {
		this.outputPath = outputPath;
	}

	@Override
	protected void initialize() {
		super.initialize();
	}

	protected static final Object EVIRTUAL_NO_VALUE = new Object();

	/**
	 * Creates the SaltInfo-XML for the mapped SDocument
	 */
	@Override
	public DOCUMENT_STATUS mapSDocument() {
		SDocument sdoc = getSDocument();
		logger.debug("==SDoc::Start: " + sdoc.getSId());
		try {
			// sdoc.printInfo(getResourceURI());
			File out = new File(getResourceURI().toFileString());
			logger.debug(String.format("write to %s", out));
			exporter.getInfoModule().writeInfoFile(sdoc, out, null);
			if (((InfoModuleProperties) getProperties()).isHtmlOutput()) {
				exporter.applyXSLT(exporter.getInfo2html(), getResourceURI(), getResourceURI().trimFileExtension().appendFileExtension("html"));
			}
		} catch (Exception e) {
			throw new PepperModuleException("Cannot export document '" + sdoc.getSId() + "', nested exception is: ", e);
		}

		addProgress(1.0 / exporter.getDocumentCount());
		EList<Edge> in = sdoc.getSCorpusGraph().getInEdges(sdoc.getSId());
		for (Edge e : in) {
			SCorpus parent = (SCorpus) e.getSource();
			exporter.release(parent);
		}
		logger.debug("==SDoc::end: " + sdoc.getSId());
		return DOCUMENT_STATUS.COMPLETED;
	}

	/**
	 * Creates the SaltInfo-XML for the mapped corpus
	 */
	@Override
	public DOCUMENT_STATUS mapSCorpus() {
		SCorpus scorpus = getSCorpus();
		logger.debug("==scorp::Start: " + scorpus.getSId());
		logger.debug("Map SCorpus at " + scorpus);
		try {
			exporter.acquire(scorpus);

			File out = new File(getResourceURI().toFileString());
			URI htmlOutput = getResourceURI().trimFileExtension().appendFileExtension("html");
			EList<Edge> in = scorpus.getSCorpusGraph().getInEdges(scorpus.getSId());

			exporter.getInfoModule().writeInfoFile(getSCorpus(), out, outputPath);
			addProgress(1.0 / exporter.getDocumentCount());
			if (((InfoModuleProperties) getProperties()).isHtmlOutput()) {
				exporter.applyXSLT(exporter.getInfo2html(), getResourceURI(), htmlOutput);
			}
			addProgress(1.0 / exporter.getDocumentCount());

			for (Edge e : in) {
				SCorpus parent = (SCorpus) e.getSource();
				exporter.release(parent);
			}
			// if (in.size() == 0 ){
			// // must be a root node
			// exporter.release(scorpus);
			// }
		} catch (Exception e) {
			throw new PepperModuleException("Cannot export document '" + getSCorpus().getSId() + "', nested exception is: ", e);
		}
		logger.debug("==scorp::end:  " + scorpus.getSId());
		return DOCUMENT_STATUS.COMPLETED;
	}
}
