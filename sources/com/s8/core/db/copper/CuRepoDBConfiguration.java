package com.s8.core.db.copper;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.s8.core.arch.silicon.SiliconEngine;
import com.s8.core.bohr.neodymium.codebase.NdCodebase;
import com.s8.core.bohr.neodymium.exceptions.NdBuildException;
import com.s8.core.db.copper.store.CuRepoDB;
import com.s8.core.io.json.types.JSON_CompilingException;
import com.s8.core.io.xml.annotations.XML_SetElement;
import com.s8.core.io.xml.annotations.XML_Type;


@XML_Type(root=true, name = "Copper-config")
public class CuRepoDBConfiguration {


	public String rootFolderPathname;

	
	@XML_SetElement(tag = "path")
	public void setRootFolderPathname(String pathname) {
		this.rootFolderPathname = pathname;
	}


	
	/**
	 * 
	 * @param path
	 * @return
	 * @throws JSON_CompilingException 
	 * @throws NdBuildException 
	 * @throws BeBuildException 
	 */
	public CuRepoDB create(SiliconEngine ng, Class<?>[] classes) 
			throws JSON_CompilingException, NdBuildException {
		
		NdCodebase codebase = NdCodebase.from(classes); 
		
		Path rootFolderPath = Paths.get(rootFolderPathname);
		
		Path metadataFilePath = CuRepoDB.getMetadataPath(rootFolderPath);
		
		boolean isSaved = metadataFilePath.toFile().exists();
		
		return new CuRepoDB(ng, codebase, rootFolderPath, isSaved);	
	}
}
