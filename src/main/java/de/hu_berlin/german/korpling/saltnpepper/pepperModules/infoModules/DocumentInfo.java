package de.hu_berlin.german.korpling.saltnpepper.pepperModules.infoModules;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.exceptions.PepperModuleException;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SAnnotatableElement;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SAnnotation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SLayer;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SMetaAnnotation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SNode;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SRelation;

/**
 * Contains all information concerning a {@link SDocumentGraph} to be exported as salt info.
 * @author Florian Zipser
 *
 */
public class DocumentInfo implements SaltInfoDictionary{
	
	public static final String NO_LAYER="_NO_LAYER_";
	private StructuralInfo structuralInfo= new StructuralInfo();
	/**
	 * Returns the {@link StructuralInfo} object containing all {@link SNode}s, {@link SRelation}s etc. contained
	 * in this document structure. 
	 * @return
	 */
	public StructuralInfo getStructuralInfo() {
		return structuralInfo;
	}
	/** path to file to export salt info**/
	private File exportPath= null; 
	/** @return path to file to export salt info **/
	public File getExportPath() {
		return exportPath;
	}
	/** @param exportPath path to file to export salt info. **/
	public void setExportPath(File exportPath) {
		this.exportPath = exportPath;
	}
	/** A map containing all meta data, SName as key and SValue as value **/
	private Map<String, String> metaDataInfo= null;
	
	public Map<String, String> getMetaDataInfo() {
		if (metaDataInfo== null){
			metaDataInfo= new Hashtable<>();
		}
		return metaDataInfo;
	}
	/** A map mapping all {@link SLayer}, {@link SAnnotation}s and their occurances key= slayerName, value= annotations(key= sName, value=(key= SValue, value= occurences))**/
	private Map<String, Map<String, Map<String, Integer>>> annotations= null;
	/** @return a map mapping all {@link SLayer}, {@link SAnnotation}s and their occurances key= slayerName, value= annotations(key= sName, value=(key= SValue, value= occurences))**/
	public Map<String, Map<String, Map<String, Integer>>> getAnnotations() {
		if (annotations== null){
			annotations= new Hashtable<>();
		}
		return annotations;
	}
	/** 
	 * Retrieves all necessary data contained in the passes {@link SDocument} object and filles the
	 * agregator objects of this object, like {@link #getStructuralInfo()}, {@link #getMetaDataInfo()}
	 * and {@link #getAnnotations()}.  
	 */
	public void retrieveData(SDocument sDocument){
		//retrieve meta data
		for (SMetaAnnotation sMeta: sDocument.getSMetaAnnotations()){
			getMetaDataInfo().put(sMeta.getQName(), sMeta.getSValueSTEXT());
		}
		
		//retrieve structural data
		if (sDocument.getSDocumentGraph().getSNodes().size()!= 0){
			getStructuralInfo().occurance_SNode= sDocument.getSDocumentGraph().getSNodes().size();
		}
		if (sDocument.getSDocumentGraph().getSTimeline()!= null){
			getStructuralInfo().occurance_STimeline= 1;;
		}
		if (sDocument.getSDocumentGraph().getSTextualDSs().size()!= 0){
			getStructuralInfo().occurance_STextualDS= sDocument.getSDocumentGraph().getSTextualDSs().size();
		}
		if (sDocument.getSDocumentGraph().getSTokens().size()!= 0){
			getStructuralInfo().occurance_SToken= sDocument.getSDocumentGraph().getSTokens().size();
		}
		if (sDocument.getSDocumentGraph().getSSpans().size()!= 0){
			getStructuralInfo().occurance_SSpan= sDocument.getSDocumentGraph().getSSpans().size();
		}
		if (sDocument.getSDocumentGraph().getSStructures().size()!= 0){
			getStructuralInfo().occurance_SStructure= sDocument.getSDocumentGraph().getSStructures().size();
		}
		if (sDocument.getSDocumentGraph().getSRelations().size()!= 0){
			getStructuralInfo().occurance_SRelation= sDocument.getSDocumentGraph().getSRelations().size();
		}
		if (sDocument.getSDocumentGraph().getSSpanningRelations().size()!= 0){
			getStructuralInfo().occurance_SSpanningRelation= sDocument.getSDocumentGraph().getSSpanningRelations().size();
		}
		if (sDocument.getSDocumentGraph().getSDominanceRelations().size()!= 0){
			getStructuralInfo().occurance_SDominanceRelation= sDocument.getSDocumentGraph().getSDominanceRelations().size();
		}
		if (sDocument.getSDocumentGraph().getSOrderRelations().size()!= 0){
			getStructuralInfo().occurance_SOrderRelation= sDocument.getSDocumentGraph().getSOrderRelations().size();
		}
		if (sDocument.getSDocumentGraph().getSPointingRelations().size()!= 0){
			getStructuralInfo().occurance_SPointingRelation= sDocument.getSDocumentGraph().getSPointingRelations().size();
		}
		//retrieve annotations for nodes
		for (SNode sNode: sDocument.getSDocumentGraph().getSNodes()){
			if (sNode.getSLayers()== null){
				for (SLayer sLayer: sNode.getSLayers()){
					Map<String, Map<String, Integer>> annos= annotations.get(sLayer.getSName());
					if (annos== null){
						annos= new Hashtable<>();
						getAnnotations().put(sLayer.getSName(), annos);
					}
					retrieveAnnotations(sNode, annos);
				}
			}else{
				Map<String, Map<String, Integer>> annos= annotations.get(NO_LAYER);
				if (annos== null){
					annos= new Hashtable<>();
					getAnnotations().put(NO_LAYER, annos);
				}
				retrieveAnnotations(sNode, annos);
			}
		}
		
		//retrieve annotations for relations
		for (SRelation sRel: sDocument.getSDocumentGraph().getSRelations()){
			if (sRel.getSLayers()== null){
				for (SLayer sLayer: sRel.getSLayers()){
					Map<String, Map<String, Integer>> annos= annotations.get(sLayer.getSName());
					if (annos== null){
						annos= new Hashtable<>();
						getAnnotations().put(sLayer.getSName(), annos);
					}
					retrieveAnnotations(sRel, annos);
				}
			}else{
				Map<String, Map<String, Integer>> annos= annotations.get(NO_LAYER);
				if (annos== null){
					annos= new Hashtable<>();
					getAnnotations().put(NO_LAYER, annos);
				}
				retrieveAnnotations(sRel, annos);
			}
		}
	}
	/**
	 * Iterates through all annotations of the passed {@link SAnnotatableElement} and adds them to the 
	 * passed annotations map.
	 * @param sElem
	 * @param annotations
	 */
	private void retrieveAnnotations(SAnnotatableElement sElem, Map<String, Map<String, Integer>> annotations){
		if (	(sElem!= null)&&
				(annotations!= null)){
			for (SAnnotation sAnno: sElem.getSAnnotations()){
				Map<String, Integer>anno= annotations.get(sAnno.getSName());
				if (anno== null){
					anno= new Hashtable<>();
					annotations.put(sAnno.getSName(), anno);
				}
				Integer occurences= anno.get(sAnno.getSValue());
				if (occurences== null){
					anno.put(sAnno.getSValueSTEXT(), 1);
				}else{
					anno.put(sAnno.getSValueSTEXT(), occurences++);
				}
			}
		}
	}
	
	/**
	 * Exports the passed document structure to the given file as saltinfo.
	 * @param sDocGraph
	 * @param xml
	 */
	public void write(SDocument sDocument){
		XMLOutputFactory xof = XMLOutputFactory.newInstance();
//		fixtureFile= new File(PepperModuleTest.getTempPath_static("saltInfoExporter").getAbsolutePath()+"/structuralInfo.xml");
        XMLStreamWriter xml;
			try {
				xml = xof.createXMLStreamWriter(new FileWriter(getExportPath()));
			} catch (XMLStreamException | IOException e) {
				throw new PepperModuleException("Cannot write salt info to file '"+getExportPath()+"'. ", e);
			}
		
        write(sDocument, xml);
	}
	
	/**
	 * Exports the passed document structure to the passed xml stream as saltinfo.
	 * @param sDocument
	 * @param xml
	 */
	public void write(SDocument sDocument, XMLStreamWriter xml){
		//retrieve all data
		retrieveData(sDocument);
		
		try {
			xml.writeStartDocument();
				xml.writeStartElement(TAG_SDOCUMENT_INFO);
					Date date = new Date();
					DateFormat dformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					xml.writeAttribute(ATT_GENERATED_ON, dformat.format(date));
					xml.writeAttribute(ATT_SNAME, (sDocument.getSName()!= null?sDocument.getSName():""));
					xml.writeAttribute(ATT_SNAME, (sDocument.getSId()!= null?sDocument.getSId():""));
					//write meta data
					writeMetaDataInfo(xml);
					//write structural info
					if (getStructuralInfo()!= null){
						getStructuralInfo().write(xml);
					}
					Map<String, Map<String, Integer>> annotations= getAnnotations().get(NO_LAYER);
				xml.writeEndElement();
			xml.writeEndDocument();
		} catch (XMLStreamException e) {
			throw new PepperModuleException("Cannot write salt info of sDocument '"+sDocument.getSId()+"' to stream. ", e);
		}
		
		
	}
	/**
	 * Writes the meta data contained in this object to passed stream.
	 * @throws XMLStreamException 
	 */
	public void writeMetaDataInfo(XMLStreamWriter xml) throws XMLStreamException{
		xml.writeStartElement(TAG_META_DATA_INFO);
			for (String key: getMetaDataInfo().keySet()){
				xml.writeStartElement(TAG_ENTRY);
					xml.writeAttribute(ATT_KEY, key);
					xml.writeCharacters(getMetaDataInfo().get(key));
				xml.writeEndElement();
			}
		xml.writeEndElement();
	}
	/**
	 * Writes annotations to passed xml stream
	 * @param annotations
	 * @param xml
	 * @throws XMLStreamException 
	 */
	public void writeAnnotations(Map<String, Map<String, Integer>> annotations, XMLStreamWriter xml) throws XMLStreamException{
		for (String annoName: annotations.keySet()){
			xml.writeStartElement(TAG_SANNOTATION_INFO);
				xml.writeAttribute(ATT_SNAME, annoName);
				for (String annoValue: annotations.get(annoName).keySet()){
					xml.writeStartElement(TAG_SVALUE);
						xml.writeAttribute(ATT_OCCURANCES, annotations.get(annoName).get(annoValue).toString());
						xml.writeCharacters(annoValue);
					xml.writeEndElement();
				}
			xml.writeEndElement();
		}
	}
}
