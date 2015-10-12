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

import java.util.Hashtable;
import java.util.Map;

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
public class DocumentInfo extends ContainerInfo implements SaltInfoDictionary{
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
			if (	(sNode.getSLayers()!= null)&&
					(sNode.getSLayers().size()>0)){
				for (SLayer sLayer: sNode.getSLayers()){
					Map<String, AnnotationInfo> annos= getAnnotations().get(sLayer.getSName());
					
					if (annos== null){
						annos= new Hashtable<>();
						getAnnotations().put(sLayer.getSName(), annos);
					}
					retrieveAnnotations(sNode, annos);
				}
			}else{
				Map<String, AnnotationInfo> annos= getAnnotations().get(NO_LAYER);
				if (annos== null){
					annos= new Hashtable<>();
					getAnnotations().put(NO_LAYER, annos);
				}
				retrieveAnnotations(sNode, annos);
			}
		}
		
		//retrieve annotations for relations
		for (SRelation sRel: sDocument.getSDocumentGraph().getSRelations()){
			if (	(sRel.getSLayers()== null)&&
					(sRel.getSLayers().size()>0)){
				for (SLayer sLayer: sRel.getSLayers()){
					Map<String, AnnotationInfo> annos= getAnnotations().get(sLayer.getSName());
					if (annos== null){
						annos= new Hashtable<>();
						getAnnotations().put(sLayer.getSName(), annos);
					}
					retrieveAnnotations(sRel, annos);
				}
			}else{
				Map<String, AnnotationInfo> annos= getAnnotations().get(NO_LAYER);
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
	private void retrieveAnnotations(SAnnotatableElement sElem, Map<String, AnnotationInfo> annotations){
		if (	(sElem!= null)&&
				(annotations!= null)){
			for (SAnnotation sAnno: sElem.getSAnnotations()){
				AnnotationInfo annoInfo= annotations.get(sAnno.getSName());
				if (annoInfo== null){
					annoInfo= new AnnotationInfo();
					annotations.put(sAnno.getSName(), annoInfo);
				}
				annoInfo.add(sAnno.getSValueSTEXT());
			}
		}
	}
	
	/**
	 * Exports the passed document structure to the given file as saltinfo.
	 * @param sDocGraph
	 * @param xml
	 */
	public void write(SDocument sDocument){
		retrieveData(sDocument);
		super.write(sDocument);
	}
}
