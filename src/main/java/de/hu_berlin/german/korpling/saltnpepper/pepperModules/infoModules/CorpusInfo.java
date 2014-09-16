package de.hu_berlin.german.korpling.saltnpepper.pepperModules.infoModules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SCorpus;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SMetaAnnotation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SNode;

/**
 * Contains all information concerning a {@link SDocumentGraph} to be exported as salt info.
 * @author Florian Zipser
 *
 */
public class CorpusInfo extends ContainerInfo implements SaltInfoDictionary{
	
	/** A list of all sub containers, to be merged into this object. **/
	private List<ContainerInfo> containerInfos= null;
	/**
	 * @return a list of all sub containers, to be merged into this object. 
	 */
	public List<ContainerInfo> getContainerInfos() {
		if (containerInfos== null){
			containerInfos= new ArrayList<>();
		}
		return containerInfos;
	}
	
	/** 
	 * Retrieves all necessary data contained in the passes {@link SCorpus} object and filles the
	 * agregator object for meta data.
	 */
	public void retrieveData(SCorpus sCorpus){
		if (sCorpus!= null){
			// integrate meta data
			if (sCorpus.getSMetaAnnotations()!= null){
				for (SMetaAnnotation sMeta: sCorpus.getSMetaAnnotations()){
					getMetaDataInfo().put(sMeta.getSName(), sMeta.getSValueSTEXT());
				}
			}
		}
	}
	
	/** 
	 * Retrieves all necessary data contained in the passes {@link SDocument} object and filles the
	 * agregator objects of this object, like {@link #getStructuralInfo()}, {@link #getMetaDataInfo()}
	 * and {@link #getAnnotations()}.  
	 */
	public void retrieveData(ContainerInfo cont){
		if (cont!= null){
			// integrate meta data
			if (cont.getMetaDataInfo()!= null){
				for (String sMetaName: cont.getMetaDataInfo().keySet()){
					Collection<String> sMetaValues= cont.getMetaDataInfo().get(sMetaName);
					if (sMetaValues!= null){
						getMetaDataInfo().putAll(sMetaName, sMetaValues);
					}
				}
			}
		
			//retrieve structural data
			if (cont.getStructuralInfo()!= null){
				if (cont.getStructuralInfo().occurance_SNode!= 0){
					getStructuralInfo().occurance_SNode+= cont.getStructuralInfo().occurance_SNode;
				}
				if (cont.getStructuralInfo().occurance_STimeline!= null){
					getStructuralInfo().occurance_STimeline+= cont.getStructuralInfo().occurance_STimeline;
				}
				if (cont.getStructuralInfo().occurance_STextualDS!= 0){
					getStructuralInfo().occurance_STextualDS+= cont.getStructuralInfo().occurance_STextualDS;
				}
				if (cont.getStructuralInfo().occurance_SToken!= 0){
					getStructuralInfo().occurance_SToken+= cont.getStructuralInfo().occurance_SToken;
				}
				if (cont.getStructuralInfo().occurance_SSpan!= 0){
					getStructuralInfo().occurance_SSpan+= cont.getStructuralInfo().occurance_SSpan;
				}
				if (cont.getStructuralInfo().occurance_SStructure!= 0){
					getStructuralInfo().occurance_SStructure+= cont.getStructuralInfo().occurance_SStructure;
				}
				if (cont.getStructuralInfo().occurance_SRelation!= 0){
					getStructuralInfo().occurance_SRelation+= cont.getStructuralInfo().occurance_SRelation;
				}
				if (cont.getStructuralInfo().occurance_SSpanningRelation!= 0){
					getStructuralInfo().occurance_SSpanningRelation+= cont.getStructuralInfo().occurance_SSpanningRelation;
				}
				if (cont.getStructuralInfo().occurance_SDominanceRelation!= 0){
					getStructuralInfo().occurance_SDominanceRelation+= cont.getStructuralInfo().occurance_SDominanceRelation;
				}
				if (cont.getStructuralInfo().occurance_SOrderRelation!= 0){
					getStructuralInfo().occurance_SOrderRelation+= cont.getStructuralInfo().occurance_SOrderRelation;
				}
				if (cont.getStructuralInfo().occurance_SPointingRelation!= 0){
					getStructuralInfo().occurance_SPointingRelation+= cont.getStructuralInfo().occurance_SPointingRelation;
				}
			}
			//retrieve annotations
			if (cont.getAnnotations()!= null){
				for (String sLayerName: cont.getAnnotations().keySet()){
					Map<String, AnnotationInfo> anno= cont.getAnnotations().get(sLayerName);
					if (!getAnnotations().containsKey(sLayerName)){
						//when this annotations does not contain such a layer, copy
						getAnnotations().put(sLayerName, anno);
					}else{
						//merge data
						Map<String, AnnotationInfo> anno_self= getAnnotations().get(sLayerName);
						for (String sAnnoName: anno.keySet()){
							if (!anno_self.containsKey(sAnnoName)){
								anno_self.put(sAnnoName, anno.get(sAnnoName));
							}else{
								for (String SAnnoValue: anno.get(sAnnoName).keySet()){
									//add as much values have been in given anno table
									for (int i=0; i< (Integer)anno.get(sAnnoName).get(SAnnoValue); i++){
										anno_self.get(sAnnoName).add(SAnnoValue);
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * Exports the passed document structure to the given file as saltinfo.
	 * @param sDocGraph
	 * @param xml
	 */
	public void write(SNode sCorpus){
		if (sCorpus instanceof SCorpus){
			retrieveData((SCorpus)sCorpus);
			for (ContainerInfo contInfo: getContainerInfos()){
				retrieveData(contInfo);
			}
			super.write(sCorpus);
		}
	}

	
}
