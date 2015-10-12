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
import java.util.Iterator;
import java.util.Map;

import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.core.SAnnotation;
import org.corpus_tools.salt.core.SAnnotationContainer;
import org.corpus_tools.salt.core.SLayer;
import org.corpus_tools.salt.core.SMetaAnnotation;
import org.corpus_tools.salt.core.SNode;
import org.corpus_tools.salt.core.SRelation;

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
	public void retrieveData(SDocument document){
		//retrieve meta data
		for (SMetaAnnotation sMeta: document.getMetaAnnotations()){
			getMetaDataInfo().put(sMeta.getQName(), sMeta.getValue_STEXT());
		}
		
		//retrieve structural data
		if (document.getDocumentGraph().getNodes().size()!= 0){
			getStructuralInfo().occurance_SNode= document.getDocumentGraph().getNodes().size();
		}
		if (document.getDocumentGraph().getTimeline()!= null){
			getStructuralInfo().occurance_STimeline= 1;;
		}
		if (document.getDocumentGraph().getTextualDSs().size()!= 0){
			getStructuralInfo().occurance_STextualDS= document.getDocumentGraph().getTextualDSs().size();
		}
		if (document.getDocumentGraph().getTokens().size()!= 0){
			getStructuralInfo().occurance_SToken= document.getDocumentGraph().getTokens().size();
		}
		if (document.getDocumentGraph().getSpans().size()!= 0){
			getStructuralInfo().occurance_SSpan= document.getDocumentGraph().getSpans().size();
		}
		if (document.getDocumentGraph().getStructures().size()!= 0){
			getStructuralInfo().occurance_SStructure= document.getDocumentGraph().getStructures().size();
		}
		if (document.getDocumentGraph().getRelations().size()!= 0){
			getStructuralInfo().occurance_SRelation= document.getDocumentGraph().getRelations().size();
		}
		if (document.getDocumentGraph().getSpanningRelations().size()!= 0){
			getStructuralInfo().occurance_SSpanningRelation= document.getDocumentGraph().getSpanningRelations().size();
		}
		if (document.getDocumentGraph().getDominanceRelations().size()!= 0){
			getStructuralInfo().occurance_SDominanceRelation= document.getDocumentGraph().getDominanceRelations().size();
		}
		if (document.getDocumentGraph().getOrderRelations().size()!= 0){
			getStructuralInfo().occurance_SOrderRelation= document.getDocumentGraph().getOrderRelations().size();
		}
		if (document.getDocumentGraph().getPointingRelations().size()!= 0){
			getStructuralInfo().occurance_SPointingRelation= document.getDocumentGraph().getPointingRelations().size();
		}
		//retrieve annotations for nodes
		for (SNode node: document.getDocumentGraph().getNodes()){
			if (	(node.getLayers()!= null)&&
					(node.getLayers().size()>0)){
				for (SLayer sLayer: node.getLayers()){
					Map<String, AnnotationInfo> annos= getAnnotations().get(sLayer.getName());
					
					if (annos== null){
						annos= new Hashtable<>();
						getAnnotations().put(sLayer.getName(), annos);
					}
					retrieveAnnotations(node, annos);
				}
			}else{
				Map<String, AnnotationInfo> annos= getAnnotations().get(NO_LAYER);
				if (annos== null){
					annos= new Hashtable<>();
					getAnnotations().put(NO_LAYER, annos);
				}
				retrieveAnnotations(node, annos);
			}
		}
		
		//retrieve annotations for relations
		for (SRelation rel: document.getDocumentGraph().getRelations()){
			if (	(rel.getLayers()== null)&&
					(rel.getLayers().size()>0)){
				Iterator<SLayer> it= rel.getLayers().iterator();
				while(it.hasNext()){
					SLayer layer= it.next();
					Map<String, AnnotationInfo> annos= getAnnotations().get(layer.getName());
					if (annos== null){
						annos= new Hashtable<>();
						getAnnotations().put(layer.getName(), annos);
					}
					retrieveAnnotations(rel, annos);
				}
			}else{
				Map<String, AnnotationInfo> annos= getAnnotations().get(NO_LAYER);
				if (annos== null){
					annos= new Hashtable<>();
					getAnnotations().put(NO_LAYER, annos);
				}
				retrieveAnnotations(rel, annos);
			}
		}
	}
	/**
	 * Iterates through all annotations of the passed {@link SAnnotatableElement} and adds them to the 
	 * passed annotations map.
	 * @param sElem
	 * @param annotations
	 */
	private void retrieveAnnotations(SAnnotationContainer sElem, Map<String, AnnotationInfo> annotations){
		if (	(sElem!= null)&&
				(annotations!= null)){
			for (SAnnotation sAnno: sElem.getAnnotations()){
				AnnotationInfo annoInfo= annotations.get(sAnno.getName());
				if (annoInfo== null){
					annoInfo= new AnnotationInfo();
					annotations.put(sAnno.getName(), annoInfo);
				}
				annoInfo.add(sAnno.getValue_STEXT());
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
