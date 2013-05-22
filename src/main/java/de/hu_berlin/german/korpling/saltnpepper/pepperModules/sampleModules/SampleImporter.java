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
package de.hu_berlin.german.korpling.saltnpepper.pepperModules.sampleModules;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.log.LogService;

import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperExceptions.PepperModuleException;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.CorpusDefinition;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.PepperImporter;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.PepperModule;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.impl.PepperImporterImpl;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.SaltProject;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SCorpus;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SCorpusGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SElementId;

/**
 * This is a sample {@link PepperImporter}, which can be used for creating individual Importers for the 
 * Pepper Framework. Therefore you have to take a look to todo's and adapt the code.
 * 
 * <ul>
 *  <li>the salt model to fill, manipulate or export can be accessed via {@link #getSaltProject()}</li>
 * 	<li>special parameters given by Pepper workflow can be accessed via {@link #getSpecialParams()}</li>
 *  <li>a place to store temprorary datas for processing can be accessed via {@link #getTemproraries()}</li>
 *  <li>a place where resources of this bundle are, can be accessed via {@link #getResources()}</li>
 *  <li>a logService can be accessed via {@link #getLogService()}</li>
 * </ul>
 * @author Florian Zipser
 * @version 1.0
 *
 */
//TODO /1/: change the name of the component, for example use the format name and the ending Importer (FORMATImporterComponent)
@Component(name="SampleImporterComponent", factory="PepperImporterComponentFactory")
public class SampleImporter extends PepperImporterImpl implements PepperImporter
{
	public SampleImporter()
	{
		super();
		//TODO /2/: change the name of the module, for example use the format name and the ending Exporter (FORMATExporter)
		this.name= "SampleImporter";
		//TODO /4/:change "sample" with format name and 1.0 with format version to support
		this.addSupportedFormat("sample", "1.0", null);
	}
	
	/**
	 * This method is called by Pepper at the start of conversion process. 
	 * It shall create the structure the corpus to import. That means creating all necessary {@link SCorpus}, 
	 * {@link SDocument} and all Relation-objects between them. The path to the corpus to import is given by
	 * {@link #getCorpusDefinition()} and {@link CorpusDefinition#getCorpusPath()}.
	 * 
	 * Often the corpus structure is not given by formats, a common use to deal with it is to map the corpus structure to the file 
	 * structure. Therefore we offer the method {@link #createCorpusStructure(org.eclipse.emf.common.util.URI, SElementId, org.eclipse.emf.common.util.EList)}.
	 * This method reads a file structure and maps each folder to a {@link SCorpus} object. Each file having an ending contained in the passed list
	 * will be added to the returned table and correspond to a {@link SDocument} object for later access.
	 * Here is a snippet to use the method.
	 * <code> 
	 * EList<String> endings= new BasicEList<String>();
	 * endings.add("FORMAT1");
	 * endings.add("FORMAT2");
	 * ...
	 * documentResourceTable= this.createCorpusStructure(this.getCorpusDefinition().getCorpusPath(), null, endings);
	 * </code>
	 * @param an empty graph given by Pepper, which shall contains the corpus structure
	 */
	@Override
	public void importCorpusStructure(SCorpusGraph sCorpusGraph)
			throws PepperModuleException
	{
		//TODO /6/: implement this method 	
	}
	
	/**
	 * If this method is not really implemented, it will call the Method {@link #start(SElementId)} for every document 
	 * and corpus, which shall be processed. If it is not really implemented, the method-call will be serial and
	 * and not parallel. To implement a parallelization override this method and take care, that your code is
	 * thread-safe. 
	 * For getting an impression how to implement this method, here is a snippet of super class 
	 * PepperImporter of this method:
	 * <br/>
	 * <code>
	 * boolean isStart= true;
	 * SElementId sElementId= null;
	 * while ((isStart) || (sElementId!= null))
	 * {	
	 *  isStart= false;
	 *		sElementId= this.getPepperModuleController().get();
	 *		if (sElementId== null)
	 *			break;
	 *		
	 *		//call for using push-method
	 *		this.start(sElementId);
	 *		
	 *		if (this.returningMode== RETURNING_MODE.PUT)
	 *		{	
	 *			this.getPepperModuleController().put(sElementId);
	 *		}
	 *		else if (this.returningMode== RETURNING_MODE.FINISH)
	 *		{	
	 *			this.getPepperModuleController().finish(sElementId);
	 *		}
	 *		else 
	 *			throw new PepperModuleException("An error occurs in this module (name: "+this.getName()+"). The returningMode isn't correctly set (its "+this.getReturningMode()+"). Please contact module supplier.");
	 *		this.end();
	 *	}
	 *</code>
	 * After all documents were processed this method of super class will call the method {@link #end()}.
	 */
	@Override
	public void start() throws PepperModuleException
	{
		//TODO /7/: delete this, if you want to parallelize processing 
		super.start();
	}
	
	/**
	 * This method is called by method {@link #start()} of superclass {@link PepperImporter}, if the method was not overwritten
	 * by the current class. If this is not the case, this method will be called for every document which has
	 * to be processed.
	 * @param sElementId the id value for the current document or corpus to process  
	 */
	@Override
	public void start(SElementId sElementId) throws PepperModuleException 
	{
		if (	(sElementId!= null) &&
				(sElementId.getSIdentifiableElement()!= null) &&
				((sElementId.getSIdentifiableElement() instanceof SDocument) ||
				((sElementId.getSIdentifiableElement() instanceof SCorpus))))
		{//only if given sElementId belongs to an object of type SDocument or SCorpus	
			//TODO /8/: create your own mapping
		}//only if given sElementId belongs to an object of type SDocument or SCorpus
	}
	
	/**
	 * This method is called by method {@link #start()} of super class {@link PepperModule}. If you do not implement
	 * this method, it will call {@link #start(SElementId)}, for all super corpora in current {@link SaltProject}. The
	 * {@link SElementId} refers to one of the super corpora. 
	 */
	@Override
	public void end() throws PepperModuleException
	{
		//TODO /9/: implement this method when necessary 
		super.end();
	}
	
//================================ start: methods used by OSGi
	/**
	 * This method is called by the OSGi framework, when a component with this class as class-entry
	 * gets activated.
	 * @param componentContext OSGi-context of the current component
	 */
	protected void activate(ComponentContext componentContext) 
	{
		this.setSymbolicName(componentContext.getBundleContext().getBundle().getSymbolicName());
		{//just for logging: to say, that the current module has been activated
			if (this.getLogService()!= null)
				this.getLogService().log(LogService.LOG_DEBUG,this.getName()+" is activated...");
		}//just for logging: to say, that the current module has been activated
	}

	/**
	 * This method is called by the OSGi framework, when a component with this class as class-entry
	 * gets deactivated.
	 * @param componentContext OSGi-context of the current component
	 */
	protected void deactivate(ComponentContext componentContext) 
	{
		{//just for logging: to say, that the current module has been deactivated
			if (this.getLogService()!= null)
				this.getLogService().log(LogService.LOG_DEBUG,this.getName()+" is deactivated...");
		}	
	}
//================================ start: methods used by OSGi
}
