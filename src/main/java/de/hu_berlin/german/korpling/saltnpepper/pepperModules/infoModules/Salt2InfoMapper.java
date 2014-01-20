package de.hu_berlin.german.korpling.saltnpepper.pepperModules.infoModules;

import java.io.File;
import java.nio.charset.Charset;

import org.eclipse.emf.common.util.URI;

import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperExceptions.PepperModuleException;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.MAPPING_RESULT;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.PepperMapper;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.impl.PepperMapperImpl;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.info.InfoModule;
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
	public MAPPING_RESULT mapSDocument() {
		SDocument sdoc = getSDocument();
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
		return MAPPING_RESULT.FINISHED;
	}



	/**
	 * Creates the SaltInfo-XML for the mapped corpus
	 */
	@Override
	public MAPPING_RESULT mapSCorpus() {
		SCorpus scorpus = getSCorpus();
		System.out.println("Map SCorpus at " + scorpus);
		try {
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
		return MAPPING_RESULT.FINISHED;
	}
}
