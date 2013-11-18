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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.print.attribute.standard.MediaSize.Other;

import org.eclipse.emf.common.util.URI;
import org.osgi.service.component.annotations.Component;

import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperExceptions.PepperModuleException;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.PepperExporter;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.PepperMapper;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.impl.PepperExporterImpl;
import de.hu_berlin.german.korpling.saltnpepper.salt.graph.IdentifiableElement;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SCorpus;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SElementId;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SIdentifiableElement;

/**
 * This is a sample {@link PepperExporter}, which can be used for creating
 * individual Exporters for the Pepper Framework. Therefore you have to take a
 * look to todo's and adapt the code.
 * 
 * <ul>
 * <li>the salt model to fill, manipulate or export can be accessed via
 * {@link #getSaltProject()}</li>
 * <li>special parameters given by Pepper workflow can be accessed via
 * {@link #getSpecialParams()}</li>
 * <li>a place to store temprorary datas for processing can be accessed via
 * {@link #getTemproraries()}</li>
 * <li>a place where resources of this bundle are, can be accessed via
 * {@link #getResources()}</li>
 * <li>a logService can be accessed via {@link #getLogService()}</li>
 * </ul>
 * 
 * @author Jakob Schmolling
 * @version 0.1
 * 
 */
@Component(name = "InfoModuleExporterComponent", factory = "PepperExporterComponentFactory")
public class InfoModuleExporter extends PepperExporterImpl implements
		PepperExporter {
	
	URI outputPath;

	private int documentCount = 0;
	
	public InfoModuleExporter() {
			super();
		{
			// TODO /2/: change the name of the module, for example use the format
			// name and the ending Exporter (FORMATExporter)
			this.name = "InfoModuleExporter";
			// TODO /4/:change "sample" with format name and 1.0 with format version
			// to support
			this.addSupportedFormat("xml", "1.0",
					URI.createURI("https://korpling.german.hu-berlin.de/p/projects/peppermodules-statisticsmodules"));
			this.setProperties(new InfoModuleProperties());

		 }
	}
	
	@Override
	public void end() throws PepperModuleException {
		super.end();
//		URL saltinfocss = this.getClass().getResource(("/xslt/salt-info.xslt"));
		String[] resources = {"/css/saltinfo.css",
							  "/js/saltinfo.js",
							  "/js/jquery.js",
							  "/img/information.png",
							  "/img/SaltNPepper_logo2010.svg"};
		for (String resName : resources) {
			URL res = this.getClass().getResource(resName);
			URI out = URI.createFileURI("."+resName).resolve(outputPath);
			System.out.println("Creating resource file: " + res + " saving to: " + out);
			File fout = new File(out.toFileString());
			//TODO: Switch overwriting
			if(true){
				fout.getParentFile().mkdirs();
				try {
					FileOutputStream fos = new FileOutputStream(fout);
					InputStream is = res.openStream();
					byte[] buffer = new byte[1024];
					int len = 0;
					while((len = is.read(buffer)) != -1){
						fos.write(buffer, 0, len);
					}
					is.close();
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	/**
	 * Creates a mapper of type {@link Salt2InfoMapper}.
	 */
	@Override
	public PepperMapper createPepperMapper(SElementId sElementId) {
		outputPath = getCorpusDefinition().getCorpusPath();
		++documentCount;
		if(!outputPath.hasTrailingPathSeparator()){
			outputPath = outputPath.appendSegment(URI.encodeSegment("", false));
		}
		PepperMapper mapper = new Salt2InfoMapper(this);
		((Salt2InfoMapper)mapper).setOutputPath(outputPath);
		mapper.setProperties(this.getProperties());
		
		// Location für Ressourcen Folder
		getResources();
		System.out.println("+Creating PepperMapper for "
				+ sElementId.getIdentifiableElement());
		IdentifiableElement elem = sElementId.getIdentifiableElement();
		if (elem instanceof SDocument) {
			final SDocument sdoc = (SDocument) elem;
			System.out.println("> Mapping SDocument" + sElementId);
			
			//Move to export Graph
//			String infoFileLocation = sdoc.getSElementPath().toString()
//					.substring("salt:/".length())
//					+ ".xml";
//			URI out = getCorpusDefinition().getCorpusPath().appendSegments(
//					infoFileLocation.split("/"));
			URI out = getDocumentLocationURI(sdoc, outputPath);
			getSElementId2ResourceTable().put(sElementId, out);
			System.out.println("ElementPath: " + sdoc.getSElementPath());
			System.out.println("RessourceTable: Entries= "
					+ getSElementId2ResourceTable().size());
			for (URI resource : getSElementId2ResourceTable().values()) {
				System.out.println("\tR= " + resource);
			}
			mapper.setResourceURI(out);
		} else if (elem instanceof SCorpus) {
			// html export is 1 step
			++documentCount;
			final SCorpus scorpus = (SCorpus) elem;
			URI out = getDocumentLocationURI(scorpus, outputPath);
			getSElementId2ResourceTable().put(sElementId, out);
			mapper.setResourceURI(out);
			System.out.println("> Mapping SCorpus " + elem + " to " + out);
		}
		return (mapper);

	}
	
	/**
	 * FIXME: This function appears in 
	 * de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.helper.modules.InfoModule also
	 * @param sdoc
	 * @param root
	 * @return
	 */
	public static URI getDocumentLocationURI(final SIdentifiableElement sdoc,
			final URI root) {
		URI infoFileLocation =  URI.createFileURI("." + sdoc.getSElementPath().path()).appendFileExtension("xml");
		URI partial = infoFileLocation.resolve(root);
		return partial;
	}

	public double getDocumentCount() {
		// TODO Auto-generated method stub
		return documentCount;
	}
	
	/*
	 * TODO: Change path for temporaries
	 * (non-Javadoc)
	 * @see de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.impl.PepperModuleImpl#setTemproraries(org.eclipse.emf.common.util.URI)
	 */
	@Override
	public void setTemproraries(URI newTemproraries) {
		// TODO Auto-generated method stub
		super.setTemproraries(newTemproraries);
	}

}
