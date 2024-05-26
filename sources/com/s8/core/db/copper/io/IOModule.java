package com.s8.core.db.copper.io;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import com.s8.core.arch.magnesium.db.MgIOException;
import com.s8.core.arch.magnesium.db.MgIOModule;
import com.s8.core.arch.magnesium.db.MgResourceStatus;
import com.s8.core.bohr.neodymium.repository.NdRepository;
import com.s8.core.bohr.neodymium.repository.NdRepositoryMetadata;
import com.s8.core.db.copper.RepoStoreMetadata;
import com.s8.core.io.json.JSON_Lexicon;
import com.s8.core.io.json.parsing.JSON_ParsingException;
import com.s8.core.io.json.types.JSON_CompilingException;
import com.s8.core.io.json.utilities.JOOS_BufferedFileReader;
import com.s8.core.io.json.utilities.JOOS_BufferedFileWriter;

public class IOModule implements MgIOModule<NdRepository> {
	

	public final static String METADATA_FILENAME = "repo-meta.js";

	private static JSON_Lexicon lexicon;


	public static JSON_Lexicon JOOS_getLexicon() throws JSON_CompilingException {

		return lexicon;
	}


	/**
	 * 
	 * @param handler
	 * @throws JSON_CompilingException
	 */
	public IOModule() throws JSON_CompilingException {
		super();

		if(lexicon == null) { 
			lexicon = JSON_Lexicon.from(RepoStoreMetadata.class); 
		}
	}


	@Override
	public boolean hasResource(Path path) {
		Path dataPath = path.resolve(METADATA_FILENAME);
		return dataPath.toFile().exists();
	}

	

	@Override
	public NdRepository readResource(Path path) throws MgIOException {
		try {

			Path dataPath = path.resolve(METADATA_FILENAME);
			FileChannel channel = FileChannel.open(dataPath, new OpenOption[]{ 
					StandardOpenOption.READ
			});

			/**
			 * lexicon
			 */

			JOOS_BufferedFileReader reader = new JOOS_BufferedFileReader(channel, StandardCharsets.UTF_8, 64);

			NdRepositoryMetadata metadata = (NdRepositoryMetadata) lexicon.parse(reader, true);

			reader.close();
			channel.close();

			NdRepository repository = new NdRepository(metadata);
			repository.nIO_path = dataPath;
			return repository;

		}
		catch (JSON_ParsingException e) {
			throw new MgIOException(new MgResourceStatus(0x0802, e.getMessage()));
		}
		catch (IOException e) {
			throw new MgIOException(new MgResourceStatus(0x0802, e.getMessage()));
		}	
	}



	@Override
	public void writeResource(Path path, NdRepository repository) throws IOException {

		FileChannel channel = FileChannel.open(path, new OpenOption[]{ 
				StandardOpenOption.WRITE
		});

		JOOS_BufferedFileWriter writer = new JOOS_BufferedFileWriter(channel, StandardCharsets.UTF_8, 256);

		lexicon.compose(writer, repository.metadata, "   ", false);

		writer.close();
		channel.close();
	}



	@Override
	public boolean deleteResource(Path path) {
		try {
			clearDirectory(path);
			return true;
		} 
		catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	public void clearDirectory(Path directory) throws IOException{
	    if (Files.exists(directory)) {
	            Files.list(directory)
	                 .forEach(
	                         path -> {
	                             try {
	                                 if (Files.isDirectory(path)) {
	                                     clearDirectory(path);
	                                 }
	                                 Files.delete(path);
	                             } catch (IOException e) {
	                                 // todo handle exception
	                             }
	                         }
	                 );
	        }
	}
}
