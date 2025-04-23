import github.tools.client.GitHubApiClient;
import github.tools.responseObjects.*;
import git.tools.client.GitSubprocessClient;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;

import javax.management.RuntimeErrorException;
import javax.swing.*;

public class git{
	
    public static void main(String[] args){
       
        String projectPath = "./test"; // THIS COMES FROM UI TEXT FIELD
        //method to create an initial commit
        GitSubprocessClient gitSubprocessClient = new GitSubprocessClient(projectPath); 
         String gitInit = gitSubprocessClient.gitInit(); 

        //Add README.md

        String readmePath = projectPath + "/README.md";
        try {
            FileWriter fw = new FileWriter(readmePath);
            fw.write("# Project\n");
            fw.write("Hello");
            fw.close();
        }
        catch (IOException e) {
            throw new RuntimeErrorException(null, "Cannot write file");
        }

    }
}