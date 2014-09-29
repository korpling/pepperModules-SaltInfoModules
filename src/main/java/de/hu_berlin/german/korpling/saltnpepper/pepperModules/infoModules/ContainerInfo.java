package de.hu_berlin.german.korpling.saltnpepper.pepperModules.infoModules;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.exceptions.PepperModuleException;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SCorpus;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SAnnotation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SLayer;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SNode;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SRelation;

/**
 * Contains all information concerning a {@link SDocumentGraph} to be exported as salt info.
 * @author Florian Zipser
 *
 */
public abstract class ContainerInfo implements SaltInfoDictionary{
	enum STATUS{NOT_STARTED, STARTED, FINISHED, ERROR}
	/** status of this {@link ContainerInfo}**/
	private volatile STATUS status= STATUS.NOT_STARTED;
	/** return status of this {@link ContainerInfo}**/
	public STATUS getStatus() {
		return status;
	}
	/**@param status status of this {@link ContainerInfo} **/
	public void setStatus(STATUS status) {
		this.status = status;
	}
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
	/** The name of the document or corpus **/
	private String sName="null";
	/**@return The name of the document or corpus **/
	public String getsName() {
		return sName;
	}
	/** @param sName The name of the document or corpus**/
	public void setsName(String sName) {
		this.sName = sName;
	}
	/** The id of the document or corpus **/
	private String sId="null";
	/**@return The id of the document or corpus **/
	public String getSId() {
		return sId;
	}
	/** @param sId The id of the document or corpus**/
	public void setSIdf(String sId) {
		this.sId = sId;
	}
	/** file to export salt info**/
	private File exportFile= null; 
	/** @return file to export salt info **/
	public File getExportFile() {
		return exportFile;
	}
	/** @param exportFile file to export salt info. **/
	public void setExportFile(File exportFile) {
		this.exportFile = exportFile;
	}
	/** A map containing all meta data, SName as key and SValue as value **/
	private Multimap<String, String> metaDataInfo= null;
	
	public Multimap<String, String> getMetaDataInfo() {
		if (metaDataInfo== null){
			metaDataInfo= HashMultimap.create();
		}
		return metaDataInfo;
	}
	/** A map mapping all {@link SLayer}, {@link SAnnotation}s and their occurances key= slayerName, value= annotations(key= sName, value=(key= SValue, value= occurences))**/
	private Map<String, Map<String, AnnotationInfo>> annotations= null;
	/** @return a map mapping all {@link SLayer}, {@link SAnnotation}s and their occurances key= slayerName, value= annotations(key= sName, value=(key= SValue, value= occurences))**/
	public Map<String, Map<String, AnnotationInfo>> getAnnotations() {
		if (annotations== null){
			annotations= new Hashtable<>();
		}
		return annotations;
	}
	
	public class AnnotationInfo{
		public Map<String, Integer> annotations= new Hashtable<>();
		public Integer occurances= 0;
		
		public void add(String sAnnoValue){
			if (	(sAnnoValue!= null)&&
					(!sAnnoValue.isEmpty())){
				occurances++;
				Integer occurences= annotations.get(sAnnoValue);
				if (occurences== null){
					annotations.put(sAnnoValue, 1);
				}else{
					annotations.put(sAnnoValue, ++occurences);
				}
			}
		}
		/** {@inheritDoc Map#keySet()}**/
		public Set<String> keySet(){
			return(annotations.keySet());
		}
		/** {@inheritDoc Map#get(Object)}**/
		public Object get(String key){
			return(annotations.get(key));
		}
	}
	
	
	/**
	 * Exports the passed document structure or corpus structure to the given file as saltinfo.
	 * @param sNode
	 * @param xml
	 */
	public void write(SNode sNode){
		if (getExportFile()== null){
			throw new PepperModuleException("Cannot create SaltInfo for '"+sNode.getSId()+"', because no file to export is given. ");
		}
		if (!getExportFile().getParentFile().exists()){
			getExportFile().getParentFile().mkdirs();
		}
		XMLOutputFactory xof = XMLOutputFactory.newInstance();
        XMLStreamWriter xml;
		try {
			xml = xof.createXMLStreamWriter(new FileWriter(getExportFile()));
		} catch (XMLStreamException | IOException e) {
			throw new PepperModuleException("Cannot write salt info to file '"+getExportFile()+"'. ", e);
		}
		
        write(sNode, xml);
	}
	
	/**
	 * Exports the passed document structure to the passed xml stream as saltinfo.
	 * @param sNode
	 * @param xml
	 */
	public void write(SNode sNode, XMLStreamWriter xml){
		setStatus(STATUS.STARTED);
		try {
			xml.writeStartDocument();
				if (sNode instanceof SDocument){
					xml.writeStartElement(TAG_SDOCUMENT_INFO);
				}else if (sNode instanceof SCorpus){
					xml.writeStartElement(TAG_SCORPUS_INFO);
				}
					Date date = new Date();
					DateFormat dformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					xml.writeAttribute(ATT_GENERATED_ON, dformat.format(date));
					xml.writeAttribute(ATT_SNAME, (sNode.getSName()!= null?sNode.getSName():""));
					xml.writeAttribute(ATT_SID, (sNode.getSId()!= null?sNode.getSId():""));
					//write meta data
					writeMetaDataInfo(xml);
					//write structural info
					if (getStructuralInfo()!= null){
						getStructuralInfo().write(xml);
					}
					Map<String, AnnotationInfo> annotations= getAnnotations().get(NO_LAYER);
					writeAnnotations(annotations, xml);
					for (String sLayerName: getAnnotations().keySet()){
						if (!NO_LAYER.equals(sLayerName)){
							xml.writeStartElement(TAG_SLAYERINFO);
								xml.writeAttribute(ATT_SNAME, sLayerName);
								writeAnnotations(getAnnotations().get(sLayerName), xml);
							xml.writeEndElement();
						}
					}
				xml.writeEndElement();
			xml.writeEndDocument();
			xml.flush();
		} catch (XMLStreamException e) {
			setStatus(STATUS.ERROR);
			throw new PepperModuleException("Cannot write salt info of sDocument or SCorpus '"+sNode.getSId()+"' to stream. ", e);
		}	
		setStatus(STATUS.FINISHED);
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
					String sMetaValue= null;
					int i= 0;
					for (String sMetaDate: getMetaDataInfo().get(key)){
						if (i== 0){
							sMetaValue= sMetaDate;
						}else{
							sMetaValue= sMetaValue+", "+ sMetaDate;
						}
						i++;
					}
					xml.writeCharacters(sMetaValue);
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
	private void writeAnnotations(Map<String, AnnotationInfo> annotations, XMLStreamWriter xml) throws XMLStreamException{
		if (annotations!= null){
			for (String annoName: annotations.keySet()){
				xml.writeStartElement(TAG_SANNOTATION_INFO);
					xml.writeAttribute(ATT_SNAME, annoName);
					xml.writeAttribute(ATT_OCCURANCES, annotations.get(annoName).occurances.toString());
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
}
