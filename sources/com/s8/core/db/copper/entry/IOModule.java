package com.s8.core.db.copper.entry;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import com.s8.core.arch.magnesium.handlers.h3.H3MgIOModule;
import com.s8.core.arch.silicon.SiliconEngine;
import com.s8.core.bohr.neodymium.repository.NdRepository;
import com.s8.core.bohr.neodymium.repository.NdRepositoryMetadata;
import com.s8.core.db.copper.branch.MgBranchHandler;
import com.s8.core.db.copper.io.RepoStore;
import com.s8.core.io.json.JSON_Lexicon;
import com.s8.core.io.json.composing.JSON_ComposingException;
import com.s8.core.io.json.parsing.JSON_ParsingException;
import com.s8.core.io.json.types.JSON_CompilingException;
import com.s8.core.io.json.utilities.JOOS_BufferedFileReader;
import com.s8.core.io.json.utilities.JOOS_BufferedFileWriter;

public class IOModule implements H3MgIOModule<NdRepository> {

	private static JSON_Lexicon lexicon;



	public final MgRepositoryHandler handler;


	public IOModule(MgRepositoryHandler handler) throws JSON_CompilingException {
		super();
		this.handler = handler;

		if(lexicon == null) { 
			lexicon = JSON_Lexicon.from(NdRepositoryMetadata.class); 
		}
	}


	@Override
	public NdRepository load() throws IOException, JSON_ParsingException {

		FileChannel channel = FileChannel.open(handler.getMetadataFilePath(), new OpenOption[]{ 
				StandardOpenOption.READ
		});

		/**
		 * lexicon
		 */

		JOOS_BufferedFileReader reader = new JOOS_BufferedFileReader(channel, StandardCharsets.UTF_8, 64);

		NdRepositoryMetadata repoMetadata = (NdRepositoryMetadata) lexicon.parse(reader, true);
		reader.close();

		/**
		 * 
		 * @param ng
		 * @param store
		 * @return
		 */
		RepoStore store = handler.store;
		SiliconEngine ng = handler.ng;

		Path path = store.composeRepositoryPath(repoMetadata.address);

		NdRepository repository = new NdRepository(repoMetadata, path);
		repoMetadata.branches.forEach((name, branchMetadata) -> {
			repository.branchHandlers.put(name, 
					new MgBranchHandler(ng, 
							store, 
							repository, 
							branchMetadata,
							true)); /* already existing */
		});

		return repository;
	}



	@Override
	public void save(NdRepository repo) throws IOException, JSON_ComposingException {

		
		Files.createDirectories(handler.getFolderPath());
		FileChannel channel = FileChannel.open(handler.getMetadataFilePath(), new OpenOption[]{ 
				StandardOpenOption.WRITE, StandardOpenOption.CREATE });


		JOOS_BufferedFileWriter writer = new JOOS_BufferedFileWriter(channel, StandardCharsets.UTF_8, 256);

		lexicon.compose(writer, repo.metadata, "   ", false);

		writer.close();
	}
}
