// import github.tools.client.GitHubApiClient;
// import github.tools.responseObjects.*;
// import git.tools.client.GitSubprocessClient;
// import java.awt.*;
// import java.io.FileWriter;
// import java.io.IOException;

// import javax.management.RuntimeErrorException;
import javax.swing.SwingUtilities;

public class git{
 
    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> {
            new GitUI().setVisible(true);
        });
    }
}