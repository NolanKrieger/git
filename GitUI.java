import javax.management.RuntimeErrorException;
import javax.swing.*;
import javax.swing.border.TitledBorder;

import git.tools.client.GitSubprocessClient;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class GitUI extends JFrame {
    private JTextField projectPathField;
    private JButton browseButton;
    private JTextField repoNameField;
    private JTextArea descriptionArea;
    private JRadioButton publicButton, privateButton;
    private JButton initButton;
    private JButton pushButton;
    private JTextArea statusArea;

    private GitSubprocessClient git;
    //testing
    //testing2
    
    public GitUI() {
        super("GitHub Repo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Project Folder Selection
        JPanel folderPanel = new JPanel(new BorderLayout(5, 5));
        folderPanel.setBorder(new TitledBorder("Project Folder"));
        projectPathField = new JTextField();
        browseButton = new JButton("Browse...");
        folderPanel.add(projectPathField, BorderLayout.CENTER);
        folderPanel.add(browseButton, BorderLayout.EAST);
        mainPanel.add(folderPanel, BorderLayout.NORTH);

        // Form Fields
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Repo Name
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Repo Name:"), gbc);
        gbc.gridx = 1;
        repoNameField = new JTextField();
        formPanel.add(repoNameField, gbc);

        // Description
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.NORTH;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.weighty = 1.0;
        descriptionArea = new JTextArea(4, 20);
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        formPanel.add(descScroll, gbc);
        gbc.weighty = 0; gbc.anchor = GridBagConstraints.CENTER;

        // Visibility
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Visibility:"), gbc);
        gbc.gridx = 1;
        JPanel visibilityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        publicButton = new JRadioButton("Public", true);
        privateButton = new JRadioButton("Private");
        ButtonGroup visibilityGroup = new ButtonGroup();
        visibilityGroup.add(publicButton);
        visibilityGroup.add(privateButton);
        visibilityPanel.add(publicButton);
        visibilityPanel.add(privateButton);
        formPanel.add(visibilityPanel, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Bottom Panel: Action & Status
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        initButton = new JButton("Initialize");
        pushButton = new JButton("Push");
        actionPanel.add(initButton);
        actionPanel.add(pushButton);
        bottomPanel.add(actionPanel, BorderLayout.NORTH);

        statusArea = new JTextArea(6, 50);
        statusArea.setEditable(false);
        JScrollPane statusScroll = new JScrollPane(statusArea);
        statusScroll.setBorder(new TitledBorder("Status"));
        bottomPanel.add(statusScroll, BorderLayout.CENTER);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Listeners
        browseButton.addActionListener(this::onBrowse);
        initButton.addActionListener(this::onInitialize);
        pushButton.addActionListener(this::onPush);
    }

    private void onBrowse(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selected = chooser.getSelectedFile();
            projectPathField.setText(selected.getAbsolutePath());
            repoNameField.setText(selected.getName());
        }
    }

    private void onInitialize(ActionEvent e) {
        appendStatus("Starting initialization...");
        
    }

    public static void create(String projectPath) throws IOException {
        File gitignoreFile = new File(projectPath + "/.gitignore");

        try (PrintWriter writer = new PrintWriter(gitignoreFile)) {
            writer.println("*.class");
            writer.println("*.log");
            writer.println(".DS_Store");
            writer.println("target/");
            writer.println(".idea/");
            writer.println("node_modules/");
        }

        System.out.println(".gitignore created at: " + gitignoreFile.getAbsolutePath());
    }

    public void createRepository(String name) {
        // Simulate request parameters
        class RequestParams {
            private final java.util.Map<String, Object> params = new java.util.HashMap<>();
            public void addParam(String key, Object value) { params.put(key, value); }
            public Object getParam(String key) { return params.get(key); }
        }

        // Simulate GitHub API client
        class GitHubApiClient {
            public void createRepo(RequestParams params) {
                String repoName = (String) params.getParam("name");
                System.out.println("Repository created: https://github.com/user/" + repoName);
            }
        }

        RequestParams requestParams = new RequestParams();
        requestParams.addParam("name", name);

        GitHubApiClient gitHubApiClient = new GitHubApiClient();
        gitHubApiClient.createRepo(requestParams);
    }

     //Creates the initial commit and Adds the README
    public static void gitInit() {
        String projectPath = "./test"; 
        // method to create an initial commit
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

    private void onPush(ActionEvent e) {
        appendStatus("Starting push...");
        // TODO: Hook up Git push
    }

    private void appendStatus(String msg) {
        statusArea.append(msg + "\n");
        statusArea.setCaretPosition(statusArea.getDocument().getLength());
    }
}
