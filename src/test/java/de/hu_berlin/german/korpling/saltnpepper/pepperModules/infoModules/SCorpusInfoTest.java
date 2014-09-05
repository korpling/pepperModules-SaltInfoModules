package de.hu_berlin.german.korpling.saltnpepper.pepperModules.infoModules;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import de.hu_berlin.german.korpling.saltnpepper.salt.SaltFactory;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SCorpus;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;
import de.hu_berlin.german.korpling.saltnpepper.salt.samples.SampleGenerator;

public class SCorpusInfoTest {

	private CorpusInfo fixture= null;
	
	
	public CorpusInfo getFixture() {
		return fixture;
	}

	public void setFixture(CorpusInfo fixture) {
		this.fixture = fixture;
	}

	@Before
	public void setUp() throws Exception {
		setFixture(new CorpusInfo());
	}

	/**
	 * Merges two documents and some own meta data.
	 */
	@Test
	public void testWriteData() {
		SDocument sDocument= SaltFactory.eINSTANCE.createSDocument();
		SampleGenerator.createMorphologyAnnotations(sDocument);
		SampleGenerator.createSyntaxAnnotations(sDocument);
		SampleGenerator.createAnaphoricAnnotations(sDocument);
		sDocument.createSMetaAnnotation(null, "att1", "value1");
		sDocument.createSMetaAnnotation(null, "att2", "value2");
		DocumentInfo docInfo1= new DocumentInfo();
		docInfo1.retrieveData(sDocument);
		
		SDocument sDocument2= SaltFactory.eINSTANCE.createSDocument();
		SampleGenerator.createMorphologyAnnotations(sDocument2);
		SampleGenerator.createSyntaxAnnotations(sDocument2);
		SampleGenerator.createAnaphoricAnnotations(sDocument2);
		sDocument2.createSMetaAnnotation(null, "att1", "value1");
		sDocument2.createSMetaAnnotation(null, "att2", "value2");
		DocumentInfo docInfo2= new DocumentInfo();
		docInfo2.retrieveData(sDocument2);
		
		SCorpus sCorpus= SaltFactory.eINSTANCE.createSCorpus();
		sCorpus.createSMetaAnnotation(null, "newMeta1", "metaVale");
		sCorpus.createSMetaAnnotation(null, "newMeta2", "metaVale2");
		
		getFixture().getContainerInfos().add(docInfo1);
		getFixture().getContainerInfos().add(docInfo2);
		
		getFixture().write(sCorpus);
		
		fail("tests needed");
	}

}
