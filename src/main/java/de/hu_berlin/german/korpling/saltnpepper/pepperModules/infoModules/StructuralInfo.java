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

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.exceptions.PepperModuleException;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDominanceRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SOrderRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SPointingRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpan;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpanningRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SStructure;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STextualDS;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STextualRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STimeline;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STimelineRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SRelation;
/**
 * Contains all occurances of elements to be stored in {@link SaltInfoDictionary#TAG_STRUCTURAL_INFO}.
 * @author Florian Zipser
 *
 */
public class StructuralInfo {
	public static final String KEY_SNODE="SNode";
	public static final String KEY_STOKEN="SToken";
	public static final String KEY_SSPAN="SSpan";
	public static final String KEY_SSTRUCTURE="SStructure";
	public static final String KEY_STIMELINE="STimeline";
    public static final String KEY_STEXTUAL_DS="STextualDS";
    
    public static final String KEY_SRELATION="SRelation";
	public static final String KEY_STEXTUAL_RELATION="STextualRelation";
    public static final String KEY_SSPANNING_RELATION="SSpanningRelation";
    public static final String KEY_SPOINTING_RELATION="SPointingRelation";
    public static final String KEY_SDOMINANCE_RELATION="SDominanceRelation";
    public static final String KEY_SORDER_RELATION="SOrderRelation";
    public static final String KEY_STIMELINE_RELATION="STimelineRelation";
    
	/** Stores the number of occurances of all {@link SNode} objects.**/
	public Integer occurance_SNode=0;
	/** Stores the number of occurances of all {@link STimeline} objects.**/
	public Integer occurance_STimeline=0;
	/** Stores the number of occurances of all {@link STextualDS} objects.**/
	public Integer occurance_STextualDS=0;
	/** Stores the number of occurances of all {@link SToken} objects.**/
	public Integer occurance_SToken=0;
	/** Stores the number of occurances of all {@link SSpan} objects.**/
	public Integer occurance_SSpan=0;
	/** Stores the number of occurances of all {@link SStructure} objects.**/
	public Integer occurance_SStructure=0;
	
	/** Stores the number of occurances of all {@link SRelation} objects.**/
	public Integer occurance_SRelation=0;
	/** Stores the number of occurances of all {@link SSpanningRelation} objects.**/
	public Integer occurance_SSpanningRelation=0;
	/** Stores the number of occurances of all {@link SDominanceRelation} objects.**/
	public Integer occurance_SDominanceRelation=0;
	/** Stores the number of occurances of all {@link SPointingRelation} objects.**/
	public Integer occurance_SPointingRelation=0;
	/** Stores the number of occurances of all {@link STextualRelation} objects.**/
	public Integer occurance_STextualRelation=0;
	/** Stores the number of occurances of all {@link STimelineRelation} objects.**/
	public Integer occurance_STimelineRelation=0;
	/** Stores the number of occurances of all {@link SOrderRelation} objects.**/
	public Integer occurance_SOrderRelation=0;

	/**
	 * Writes the part of {@link SaltInfoDictionary#TAG_STRUCTURAL_INFO} to passed stream.
	 * @param xml
	 * @throws XMLStreamException 
	 */
	public void write(XMLStreamWriter xml) throws XMLStreamException{
		if (xml== null){
			throw new PepperModuleException("Cannot write structural info, because no output stream was passed. ");
		}
		xml.writeStartElement(SaltInfoDictionary.TAG_STRUCTURAL_INFO);
			if (occurance_SNode!= 0){
				xml.writeStartElement(SaltInfoDictionary.TAG_ENTRY);
					xml.writeAttribute(SaltInfoDictionary.ATT_KEY, KEY_SNODE);
					xml.writeCharacters(occurance_SNode.toString());
				xml.writeEndElement();
			}
			if (occurance_STimeline!= 0){
				xml.writeStartElement(SaltInfoDictionary.TAG_ENTRY);
					xml.writeAttribute(SaltInfoDictionary.ATT_KEY, KEY_STIMELINE);
					xml.writeCharacters(occurance_STimeline.toString());
				xml.writeEndElement();
			}
			if (occurance_STextualDS!= 0){
				xml.writeStartElement(SaltInfoDictionary.TAG_ENTRY);
					xml.writeAttribute(SaltInfoDictionary.ATT_KEY, KEY_STEXTUAL_DS);
					xml.writeCharacters(occurance_STextualDS.toString());
				xml.writeEndElement();
			}
			if (occurance_SToken!= 0){
				xml.writeStartElement(SaltInfoDictionary.TAG_ENTRY);
					xml.writeAttribute(SaltInfoDictionary.ATT_KEY, KEY_STOKEN);
					xml.writeCharacters(occurance_SToken.toString());
				xml.writeEndElement();
			}
			if (occurance_SSpan!= 0){
				xml.writeStartElement(SaltInfoDictionary.TAG_ENTRY);
					xml.writeAttribute(SaltInfoDictionary.ATT_KEY, KEY_SSPAN);
					xml.writeCharacters(occurance_SSpan.toString());
				xml.writeEndElement();
			}
			if (occurance_SStructure!= 0){
				xml.writeStartElement(SaltInfoDictionary.TAG_ENTRY);
					xml.writeAttribute(SaltInfoDictionary.ATT_KEY, KEY_SSTRUCTURE);
					xml.writeCharacters(occurance_SStructure.toString());
				xml.writeEndElement();
			}
			
			if (occurance_SRelation!= 0){
				xml.writeStartElement(SaltInfoDictionary.TAG_ENTRY);
					xml.writeAttribute(SaltInfoDictionary.ATT_KEY, KEY_SRELATION);
					xml.writeCharacters(occurance_SRelation.toString());
				xml.writeEndElement();
			}
			if (occurance_STextualRelation!= 0){
				xml.writeStartElement(SaltInfoDictionary.TAG_ENTRY);
					xml.writeAttribute(SaltInfoDictionary.ATT_KEY, KEY_STEXTUAL_RELATION);
					xml.writeCharacters(occurance_STextualRelation.toString());
				xml.writeEndElement();
			}
			if (occurance_STimelineRelation!= 0){
				xml.writeStartElement(SaltInfoDictionary.TAG_ENTRY);
					xml.writeAttribute(SaltInfoDictionary.ATT_KEY, KEY_STIMELINE_RELATION);
					xml.writeCharacters(occurance_STimelineRelation.toString());
				xml.writeEndElement();
			}
			if (occurance_SSpanningRelation!= 0){
				xml.writeStartElement(SaltInfoDictionary.TAG_ENTRY);
					xml.writeAttribute(SaltInfoDictionary.ATT_KEY, KEY_SSPANNING_RELATION);
					xml.writeCharacters(occurance_SSpanningRelation.toString());
				xml.writeEndElement();
			}
			if (occurance_SDominanceRelation!= 0){
				xml.writeStartElement(SaltInfoDictionary.TAG_ENTRY);
					xml.writeAttribute(SaltInfoDictionary.ATT_KEY, KEY_SDOMINANCE_RELATION);
					xml.writeCharacters(occurance_SDominanceRelation.toString());
				xml.writeEndElement();
			}
			if (occurance_SOrderRelation!= 0){
				xml.writeStartElement(SaltInfoDictionary.TAG_ENTRY);
					xml.writeAttribute(SaltInfoDictionary.ATT_KEY, KEY_SORDER_RELATION);
					xml.writeCharacters(occurance_SOrderRelation.toString());
				xml.writeEndElement();
			}
			if (occurance_SPointingRelation!= 0){
				xml.writeStartElement(SaltInfoDictionary.TAG_ENTRY);
					xml.writeAttribute(SaltInfoDictionary.ATT_KEY, KEY_SPOINTING_RELATION);
					xml.writeCharacters(occurance_SPointingRelation.toString());
				xml.writeEndElement();
			}
		xml.writeEndElement();
	}
}
