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

import org.eclipse.emf.common.util.URI;
import org.osgi.service.component.annotations.Component;

import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.MAPPING_RESULT;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.PepperExporter;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.PepperMapper;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.impl.PepperExporterImpl;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.impl.PepperMapperImpl;
import de.hu_berlin.german.korpling.saltnpepper.salt.SaltFactory;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SElementId;

/**
 * This is a sample {@link PepperExporter}, which can be used for creating individual Exporters for the 
 * Pepper Framework. Therefore you have to take a look to todo's and adapt the code.
 * 
 * <ul>
 *  <li>the salt model to fill, manipulate or export can be accessed via {@link #getSaltProject()}</li>
 * 	<li>special parameters given by Pepper workflow can be accessed via {@link #getSpecialParams()}</li>
 *  <li>a place to store temprorary datas for processing can be accessed via {@link #getTemproraries()}</li>
 *  <li>a place where resources of this bundle are, can be accessed via {@link #getResources()}</li>
 *  <li>a logService can be accessed via {@link #getLogService()}</li>
 * </ul>
 * @author Jakob Schmolling
 * @version 1.0
 *
 */
//TODO /1/: change the name of the component, for example use the format name and the ending Exporter (FORMATExporterComponent)
@Component(name="InfoModuleExporterComponent", factory="PepperExporterComponentFactory")
public class InfoModuleExporter extends PepperExporterImpl implements PepperExporter
{
	public InfoModuleExporter()
	{
		super();
		//TODO /2/: change the name of the module, for example use the format name and the ending Exporter (FORMATExporter)
		this.name= "InfoModuleExporter";
		//TODO /4/:change "sample" with format name and 1.0 with format version to support
		this.addSupportedFormat("info", "1.0", null);
	}
	
	/**
	 * Creates a mapper of type {@link Salt2InfoMapper}.
	 * {@inheritDoc PepperModule#createPepperMapper(SElementId)}
	 */
	@Override
	public PepperMapper createPepperMapper(SElementId sElementId)
	{
		PepperMapper mapper= new PepperMapperImpl()
		{
			@Override
			public MAPPING_RESULT mapSDocument() {
				return(MAPPING_RESULT.FINISHED);
			}
			@Override
			public MAPPING_RESULT mapSCorpus() {
				System.out.println("Printing SCorpus" + getSCorpus());
				getSCorpus().printInfo(URI.createFileURI("/Developer/saltnpepper/speedy/ridges/ridgesInfo.xml"));
				return(MAPPING_RESULT.FINISHED);
			}
		};
		
		String segments= "";
		URI outputURI= null;
		
		for (String segment: sElementId.getSElementPath().segmentsList())
			segments= segments+ "/"+segment;
		outputURI= URI.createFileURI(this.getCorpusDefinition().getCorpusPath().toFileString() + segments+"."+SaltFactory.FILE_ENDING_DOT);
		
		mapper.setResourceURI(outputURI);
		return(mapper);
	}
}
