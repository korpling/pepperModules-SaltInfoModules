package de.hu_berlin.german.korpling.saltnpepper.pepperModules.sampleModules;

import java.io.File;
import java.net.URL;

import javax.xml.transform.Source;
import javax.xml.transform.Templates;
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

public class Salt2InfoMapper extends PepperMapperImpl implements PepperMapper {

	private URI outputPath;
	
	static private Templates cachedXSLT = null;
	
	synchronized private Transformer getCachedTemplate() {
		Transformer t = null;
		try{
			if(cachedXSLT == null){
				URL res = this.getClass().getResource(("/xslt/salt-info.xslt"));
				Source xsltSource = new StreamSource(res.openStream(), res.toString());
				TransformerFactory transFac = TransformerFactory.newInstance();
				cachedXSLT = transFac.newTemplates(xsltSource);
			}
			t = cachedXSLT.newTransformer();
		}catch (Exception e){
			throw new PepperModuleException("Can't create xslt cache", e);
		}
		return t;
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



	@Override
	public MAPPING_RESULT mapSDocument() {
		System.out.println("Salt2InfoMapper SDocument " + getSDocument());
		SDocument sdoc = getSDocument();
		System.out.println(":: Location: " + getResourceURI());
		System.out.println(":: SDocGraph: Annotations"
				+ sdoc.getSDocumentGraph().getSAnnotations().size());
		System.out.println("Paths:");
		System.out.println(getResourceURI());
		System.out.println(getSDocument().getSElementId().getSElementPath());

		try {
			sdoc.printInfo(getResourceURI());
		} catch (Exception e) {
			throw new PepperModuleException("Cannot export document '"
					+ getSDocument().getSId() + "', nested exception is: ", e);
		}

		addProgress(1.0);
		return MAPPING_RESULT.FINISHED;
	}

	@Override
	public MAPPING_RESULT mapSCorpus() {
		SCorpus scorpus = getSCorpus();
		System.out.println("Map SCorpus at " + scorpus);
		try {
			scorpus.printInfo(getResourceURI(),outputPath);
		} catch (Exception e) {
			throw new PepperModuleException("Cannot export document '"
					+ getSDocument().getSId() + "', nested exception is: ", e);
		}
		addProgress(1.0);
		
//		if ((Boolean) this.getProperties()
//				.getProperty(InfoModuleProperties.HTML_OUTPUT).getValue()) {
		if(true){
			System.out.println("Produce HTML to "
					+ getResourceURI().trimFileExtension().appendFileExtension(
							"html"));
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
		return MAPPING_RESULT.FINISHED;
	}
}
