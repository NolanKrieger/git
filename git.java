import github.tools.client.GitHubApiClient;
import github.tools.responseObjects.*;
import git.tools.client.GitSubprocessClient;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;

import javax.management.RuntimeErrorException;
import javax.swing.*;

public class git{
    private GitSubprocessClient git;
    private GitHubApiClient github;


    // public git(String projectPath, String githubToken){
    //     this.git = new GitSubprocessClient(projectPath);
        
    // }
   
    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> {
            new GitUI().setVisible(true);
        });
    }
}