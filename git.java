import javax.swing.SwingUtilities;
public class git{
    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> {
            new GitUI().setVisible(true);
        });
    }
}