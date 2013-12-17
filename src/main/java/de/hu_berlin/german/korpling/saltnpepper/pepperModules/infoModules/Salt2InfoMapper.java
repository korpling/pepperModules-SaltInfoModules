package de.hu_berlin.german.korpling.saltnpepper.pepperModules.infoModules;

import java.io.File;
import java.net.URL;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.emf.common.util.URI;

import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperExceptions.PepperModuleException;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.MAPPING_RESULT;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.PepperMapper;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.impl.PepperMapperImpl;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SCorpus;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;

/**
 * A PepperMapper that generates SaltInfo-XML, as defined in saltProjectInfo.xsd,
 * for every SDocument and SCorpus.
 * 
 * The XML files for every corpus can be transformed to HTML.
 * 
 * @author jschmolling
 *
 */
public class Salt2InfoMapper extends PepperMapperImpl implements PepperMapper {

	private URI outputPath;
	final private InfoModuleExporter exporter;
	static private Transformer cachedXSLT = null;
	
	public Salt2InfoMapper(InfoModuleExporter infoModuleExporter) {
		this.exporter = infoModuleExporter;
	}
	/**
	 * Returns a Transformer defined by the salt-info.xslt
	 * 
	 * @return XML Transformer that transform SaltInfo XML to HTML
	 */
	private Transformer getCachedTemplate() {
		Transformer t = null;
		try{
			if(cachedXSLT == null){
				URL res = this.getClass().getResource(("/xslt/salt-info.xslt"));
				Source xsltSource = new StreamSource(res.openStream(), res.toString());
				TransformerFactory transFac = TransformerFactory.newInstance();
				cachedXSLT = transFac.newTransformer(xsltSource);
			}
		}catch (Exception e){
			throw new PepperModuleException("Can't create xslt cache", e);
		}
		return cachedXSLT;
	}
	public void setOutputPath(final URI outputPath) {
		this.outputPath = outputPath;
	}

	@Override
	protected void initialize() {
		super.initialize();
	}

	protected static final Object EVIRTUAL_NO_VALUE = new Object();
//	/**
//	 * The resource set for all resources.
//	 */
//	private ResourceSet resourceSet = null;


	/**
	 * Creates the SaltInfo-XML for the mapped SDocument
	 */
	@Override
	public MAPPING_RESULT mapSDocument() {
		SDocument sdoc = getSDocument();
		try {
			sdoc.printInfo(getResourceURI());
		} catch (Exception e) {
			throw new PepperModuleException("Cannot export document '"
					+ getSDocument().getSId() + "', nested exception is: ", e);
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
			InfoModule im;
//			scorpus.printInfo(getResourceURI(),outputPath);
		} catch (Exception e) {
			throw new PepperModuleException("Cannot export document '"
					+ getSDocument().getSId() + "', nested exception is: ", e);
		}
		addProgress(1.0 / exporter.getDocumentCount());
		
//		if ((Boolean) this.getProperties()
//				.getProperty(InfoModuleProperties.HTML_OUTPUT).getValue()) {
		System.out.println("Checking for  HTML output "
				+ getResourceURI().trimFileExtension().appendFileExtension(
						"html"));
		System.out.println(getResourceURI().deresolve(outputPath).segmentCount());
		if(getResourceURI().deresolve(outputPath).segmentCount() == 1){
			URI htmlOutput = getResourceURI().trimFileExtension()
					.appendFileExtension("html");
			Transformer htmlTransform = getCachedTemplate();
			
			StreamSource source = new StreamSource(new File(getResourceURI().toFileString()));
			StreamResult result = new StreamResult(new File(htmlOutput.toFileString())); 
			try {
				htmlTransform.transform(source, result);
			} catch (TransformerException e) {
				throw new PepperModuleException("Can't generate HTML output", e);
			}
		}
		addProgress(1.0 / exporter.getDocumentCount());
		return MAPPING_RESULT.FINISHED;
	}
}
