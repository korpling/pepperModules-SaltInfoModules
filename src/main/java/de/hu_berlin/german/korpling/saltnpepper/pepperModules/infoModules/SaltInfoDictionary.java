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
package de.hu_berlin.german.korpling.saltnpepper.pepperModules.infoModules;

/**
 * This class contains the dictionary of the SaltInfo xml output.
 * @author Florian Zipser
 * @author Vivian Voigt
 *
 */
public interface SaltInfoDictionary {
	public static final String SCHEMA_LOCATION = "noNamespaceSchemaLocation";
	public static final String SCHEMA_LOCATION_XSI = "xsi:" + SCHEMA_LOCATION;
	public static final String SCHEMA_LOCATION_SALT_INFO = "https://korpling.german.hu-berlin.de/saltnpepper/salt/info/info-10";
	public static final String SCHEMA_LOCATION_SPROJECT_INFO = SCHEMA_LOCATION_SALT_INFO + "/" + "saltProjectInfo.xsd";
	public static final String SCHEMA_LOCATION_SCORPUS_INFO = SCHEMA_LOCATION_SALT_INFO + "/" + "sCorpusInfo.xsd";
	public static final String SCHEMA_LOCATION_SDOCUMENT_INFO = SCHEMA_LOCATION_SALT_INFO + "/" + "sDocumentInfo.xsd";
	public static final String SCHEMA_LOCATION_XLINK = "http://www.w3.org/TR/xlink/";
	public static final String SCHEMA_LOCATION_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema-instance";
	
	public static final String XMLNS_XSI = "xmlns:xsi";
	public static final String XMLNS_XLINK = "xmlns:xlink";
	
	public static final String XML_VERSION = "1.0";
	
	public static final String TAG_META_DATA_INFO = "metaDataInfo";
	public static final String TAG_ENTRY = "entry";
	public static final String TAG_SCORPUS_INFO = "sCorpusInfo";
	public static final String TAG_SDOCUMENT_INFO = "sDocumentInfo";
	public static final String TAG_STRUCTURAL_INFO = "structuralInfo";
	public static final String TAG_SANNOTATION_INFO = "sAnnotationInfo";
	public static final String TAG_SVALUE = "sValue";
	public static final String TAG_SLAYERINFO = "sLayerInfo";
	public static final String TAG_SPROJECT = "saltProjectInfo";
	
	public static final String ATT_KEY = "key";
	public static final String ATT_TYPE = "type";
	public static final String ATT_OCCURRENCE = "occurrence";
	public static final String ATT_XHREF = "rel-location";
	public static final String ATT_SNAME = "sName";
	public static final String ATT_SID = "id";
	public static final String ATT_GENERATED_ON = "generatedOn";
	/** relative location for document info and corpus info files, used in project info file**/
	public static final String ATT_LOCATION = "rel-location";
}
