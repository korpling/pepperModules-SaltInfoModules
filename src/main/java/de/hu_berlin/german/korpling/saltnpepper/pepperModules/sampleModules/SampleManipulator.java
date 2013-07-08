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

import org.osgi.service.component.annotations.Component;

import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.PepperManipulator;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.PepperMapper;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.impl.PepperManipulatorImpl;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SElementId;

/**
 * This is a sample {@link PepperManipulator}, which can be used for creating individual manipulators for the 
 * Pepper Framework. Therefore you have to take a look to todo's and adapt the code.
 * 
 * <ul>
 *  <li>the salt model to fill, manipulate or export can be accessed via {@link #getSaltProject()}</li>
 * 	<li>special parameters given by Pepper workflow can be accessed via {@link #getSpecialParams()}s()</li>
 *  <li>a place to store temprorary datas for processing can be accessed via {@link #getTemproraries()}</li>
 *  <li>a place where resources of this bundle are, can be accessed via {@link #getResources()}</li>
 *  <li>a logService can be accessed via {@link #getLogService()}</li>
 * </ul>
 * @author Florian Zipser
 * @version 1.0
 *
 */
//TODO /1/: change the name of the component, for example use the format name and the ending manipulator (FORMATManipulatorComponent)
@Component(name="SampleManipulatorComponent", factory="PepperManipulatorComponentFactory")
public class SampleManipulator extends PepperManipulatorImpl 
{
	public SampleManipulator()
	{
		super();
		//TODO /2/: change the name of the module, for example use the format name and the ending Exporter (FORMATExporter)
		this.name= "SampleManipulator";
	}
	
	/**
	 * Creates a mapper of type {@link Salt2InfoMapper}.
	 * {@inheritDoc PepperModule#createPepperMapper(SElementId)}
	 */
	@Override
	public PepperMapper createPepperMapper(SElementId sElementId)
	{
		Salt2InfoMapper mapper= new Salt2InfoMapper();
		return(mapper);
	}
}
