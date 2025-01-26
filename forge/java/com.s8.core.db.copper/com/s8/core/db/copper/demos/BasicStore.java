package com.s8.core.db.copper.demos;

import com.s8.api.flow.S8User;
import com.s8.api.flow.repository.objects.RepoS8Object;
import com.s8.api.flow.repository.requests.CreateRepositoryS8Request;
import com.s8.core.arch.silicon.SiliconConfiguration;
import com.s8.core.arch.silicon.SiliconEngine;
import com.s8.core.bohr.neodymium.exceptions.NdBuildException;
import com.s8.core.db.copper.CuRepoDB;
import com.s8.core.db.copper.CuRepoDBConfiguration;
import com.s8.core.io.json.types.JSON_CompilingException;

public class BasicStore {

	public static void main(String[] args) throws JSON_CompilingException, NdBuildException {
		
		SiliconConfiguration ngConfig = SiliconConfiguration.createDefault4Cores();
		SiliconEngine ng = new SiliconEngine(ngConfig);
		ng.start();
		
		CuRepoDBConfiguration config = new CuRepoDBConfiguration();
		config.rootFolderPathname = "/Users/pc/qx/db/alphaventor.s8app/repo-db";
		
		
		CuRepoDB db = config.create(ng, new Class<?>[] { StubProject.class });
		
		S8User user = new S8User() {
			
			@Override
			public String getUsername() {
				return "toto";
			}
			
			@Override
			public String getPersonalSpaceId() {
				return "toto-workspace";
			}
			
			@Override
			public String getPassword() {
				return "1234";
			}
		};
		
		StubProject project = new StubProject();
		project.name = "PC's project";
		project.a = 23.5;
		project.index = 89797;
		
		db.createRepository(0, user, () -> System.out.println("<chain>"), new CreateRepositoryS8Request(
				"project stub of March the 14th", 
				"project-08gfea", 
				"test project", 
				"main", 
				new RepoS8Object[] { project }, 
				"initial commit") {
			
			@Override
			public void onResponse(Status status, long version) {
				System.out.println("\nstored: " + status);
			}
			
			@Override
			public void onFailed(Exception exception) {
				System.out.println("\nfailed");
				exception.printStackTrace();
			}
		});
		
		db.save();
		
	}
	
}
