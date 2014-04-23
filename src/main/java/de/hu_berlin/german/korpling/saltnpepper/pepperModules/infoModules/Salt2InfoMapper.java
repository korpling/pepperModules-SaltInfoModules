package de.hu_berlin.german.korpling.saltnpepper.pepperModules.infoModules;

import java.io.File;
import java.nio.charset.Charset;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;

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
public class Salt2InfoMapper extends PepperMapperImpl implements PepperMapper {


	private final Charset charset;
	private URI outputPath;
	final private InfoModuleExporter exporter;
	private boolean htmlOutput = true;
	
	public Salt2InfoMapper(InfoModuleExporter infoModuleExporter, Charset c) {
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

	// /**
	// * The resource set for all resources.
	// */
	// private ResourceSet resourceSet = null;

	/**
	 * Creates the SaltInfo-XML for the mapped SDocument
	 */
	@Override
	public DOCUMENT_STATUS mapSDocument() {
		SDocument sdoc = getSDocument();
		System.out.println("==SDoc::Start: " + sdoc.getSId());
		try {
			// sdoc.printInfo(getResourceURI());
			File out = new File(getResourceURI().toFileString());
			System.out.println(String.format("write to %s", out));
			exporter.getIm().writeInfoFile(sdoc, out, null);
			if (htmlOutput) {
				exporter.writeProduct(exporter.getInfo2html(),getResourceURI(),
						getResourceURI().trimFileExtension()
								.appendFileExtension("html"));
			}
		} catch (Exception e) {
			throw new PepperModuleException("Cannot export document '"
					+ sdoc.getSId() + "', nested exception is: ", e);
		}

		addProgress(1.0 / exporter.getDocumentCount());
		EList<Edge> in = sdoc.getSCorpusGraph().getInEdges(sdoc.getSId());
		for( Edge e : in){
			SCorpus parent = (SCorpus) e.getSource();
			exporter.releaseSubDocuments(parent);
		}
		System.out.println("==SDoc::end: " + sdoc.getSId());
		return DOCUMENT_STATUS.COMPLETED;
	}



	/**
	 * Creates the SaltInfo-XML for the mapped corpus
	 */
	@Override
	public DOCUMENT_STATUS mapSCorpus() {
		SCorpus scorpus = getSCorpus();
		System.out.println("==scorp::Start: " + scorpus.getSId());
		System.out.println("Map SCorpus at " + scorpus);
		try {
			exporter.waitForSubDocuments(scorpus);
			File out = new File(getResourceURI().toFileString());
			exporter.getIm().writeInfoFile(getSCorpus(), out, outputPath);
		} catch (Exception e) {
			throw new PepperModuleException("Cannot export document '"
					+ getSCorpus().getSId() + "', nested exception is: ", e);
		}
		addProgress(1.0 / exporter.getDocumentCount());
		
		URI htmlOutput = getResourceURI().trimFileExtension()
				.appendFileExtension("html");
		exporter.writeProduct(exporter.getInfo2html(), getResourceURI(), htmlOutput);
		
		addProgress(1.0 / exporter.getDocumentCount());
		EList<Edge> in = scorpus.getSCorpusGraph().getInEdges(scorpus.getSId());
		for( Edge e : in){
			SCorpus parent = (SCorpus) e.getSource();
			exporter.releaseSubDocuments(parent);
		}
		if (in.size() == 0 ){
			// must be a root node
			exporter.releaseSubDocuments(scorpus);
		}
		System.out.println("==scorp::end:  " + scorpus.getSId());
		return DOCUMENT_STATUS.COMPLETED;
	}
}
