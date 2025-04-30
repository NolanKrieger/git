import javax.management.RuntimeErrorException;
import javax.swing.*;
import javax.swing.border.TitledBorder;

import git.tools.client.GitSubprocessClient;
import github.tools.client.GitHubApiClient;
import github.tools.client.RequestParams;
import github.tools.responseObjects.CreateRepoResponse;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class GitUI extends JFrame {
    // UI components
    private JTextField projectPathField; // Field to input the project path
    private JButton browseButton; // Button to browse for a project folder
    private JTextField repoNameField; // Field to input the repository name
    private JTextField usernameField; // Field to input the GitHub username
    private JTextArea descriptionArea; // Text area for the repository description
    private JRadioButton publicButton, privateButton; // Radio buttons for visibility options
    private JButton initButton; // Button to initialize the repository
    private JButton pushButton; // Button to push the repository to GitHub
    private JTextArea statusArea; // Text area to display status messages
    private GitSubprocessClient gitClient; // Client for Git subprocess commands
    private GitHubApiClient githubClient; // Client for GitHub API interactions
    private String projectPath; // Path to the project folder
    private JPasswordField tokenField; // Field to input the GitHub personal access token

    public GitUI() {
        super("GitHub Repo"); // Set the title of the JFrame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Close the application on exit
        setMinimumSize(new Dimension(700, 800)); //Set the minimum size of the window

        //Load and store the original logo image
        Image originalLogo = new ImageIcon("logo.png").getImage();
        JLabel logoLabel = new JLabel();
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        //Top panel for the logo and project folder selection
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.add(logoLabel);
        topPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        //Panel for selecting the project folder
        JPanel folderPanel = new JPanel(new BorderLayout(10, 10));
        folderPanel.setBorder(new TitledBorder("Project Folder"));
        projectPathField = new JTextField();
        browseButton = new JButton("Browse...");
        folderPanel.add(projectPathField, BorderLayout.CENTER);
        folderPanel.add(browseButton, BorderLayout.EAST);
        topPanel.add(folderPanel);

        //Main panel to hold all components
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.add(topPanel, BorderLayout.NORTH);

        //Form panel for repository details
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        int row = 0;

        //Add repository name field
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Repo Name:"), gbc);
        gbc.gridx = 1;
        repoNameField = new JTextField();
        formPanel.add(repoNameField, gbc);

        //Add username field
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        usernameField = new JTextField();
        formPanel.add(usernameField, gbc);

        //Add token field
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("GitHub Token: "), gbc);
        gbc.gridx = 1;
        tokenField = new JPasswordField();
        formPanel.add(tokenField, gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 2;
        formPanel.add(new JLabel("(generate at github.com/setting/tokens)"), gbc);

        //Add description field
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.NORTH;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 0.5;
        descriptionArea = new JTextArea(5, 30);
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        formPanel.add(descScroll, gbc);
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        // Add visibility options
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Visibility:"), gbc);
        gbc.gridx = 1;
        JPanel visibilityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        publicButton = new JRadioButton("Public", true);
        privateButton = new JRadioButton("Private");
        ButtonGroup visibilityGroup = new ButtonGroup();
        visibilityGroup.add(publicButton);
        visibilityGroup.add(privateButton);
        visibilityPanel.add(publicButton);
        visibilityPanel.add(privateButton);
        formPanel.add(visibilityPanel, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        //Bottom panel for action buttons and status area
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        initButton = new JButton("Initialize");
        pushButton = new JButton("Push");
        actionPanel.add(initButton);
        actionPanel.add(pushButton);
        bottomPanel.add(actionPanel, BorderLayout.NORTH);

        // Status area to display messages
        statusArea = new JTextArea();
        statusArea.setEditable(false);
        statusArea.setRows(5);
        JScrollPane statusScroll = new JScrollPane(statusArea);
        statusScroll.setBorder(new TitledBorder("Status"));

        // Disclaimer label
        JLabel disclaimerLabel = new JLabel("Disclaimer: This application is a prototype and not for commercial use.");
        disclaimerLabel.setFont(disclaimerLabel.getFont().deriveFont(Font.ITALIC, 12f));
        disclaimerLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Combine bottom components
        JPanel bottomWrapper = new JPanel(new BorderLayout(10, 10));
        bottomWrapper.add(actionPanel, BorderLayout.NORTH);
        bottomWrapper.add(statusScroll, BorderLayout.CENTER);
        bottomWrapper.add(disclaimerLabel, BorderLayout.SOUTH);

        mainPanel.add(bottomWrapper, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        pack();
        setLocationRelativeTo(null);

        //Resize logo dynamically when the window is resized
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int width = getContentPane().getWidth();
                int height = getContentPane().getHeight() / 6; // Reserve 1/6 for logo
                Image scaled = originalLogo.getScaledInstance(width / 3, height, Image.SCALE_SMOOTH);
                logoLabel.setIcon(new ImageIcon(scaled));
            }
        });

        //Add action listeners for buttons
        browseButton.addActionListener(this::onBrowse);
        initButton.addActionListener(this::onInitialize);
        pushButton.addActionListener(this::onPush);
    }

    //Browse for a project folder
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

    //Initialize the repository
    private void onInitialize(ActionEvent e) {
        String path = projectPathField.getText().trim();
        String name = repoNameField.getText().trim();
        String user = usernameField.getText().trim();
        String token = new String(tokenField.getPassword()).trim();

        if (path.isEmpty() || name.isEmpty() || token.isEmpty() || user.isEmpty()) {
            appendStatus("Project path, repo name, and token must be set.");
            return;
        }
        appendStatus("Starting initialization...");

        try {
            //Initialize Git repository
            gitClient = new GitSubprocessClient(path);
            gitClient.gitInit();
            appendStatus("Initialized");

            //Create .gitignore file
            create(path);
            appendStatus(".gitignore Created");

            //Create README file
            createReadMe(path, name);
            appendStatus("Created README");

            //Commit changes
            gitClient.gitAddAll();
            gitClient.gitCommit("Initial commit");
            appendStatus("Committed");

            //Create GitHub repository
            githubClient = new GitHubApiClient(user, token);
            RequestParams params = new RequestParams();
            params.addParam("name", name);
            params.addParam("description", descriptionArea.getText().trim());
            params.addParam("private", privateButton.isSelected());
            CreateRepoResponse response = githubClient.createRepo(params);
            String cloneUrl = response.getJson().get("clone_url").getAsString();
            appendStatus("GitHub repo created");

            //Add remote origin
            gitClient.gitRemoteAdd("origin", cloneUrl);
            appendStatus("Remote origin added");

        } catch (Exception e1) {
            appendStatus("Error: " + e1.getMessage());
        }
    }

    //Push the repository to GitHub
    private void onPush(ActionEvent e) {
        if (gitClient == null) {
            appendStatus("Initialize the repository first.");
            return;
        }
        appendStatus("Pushing...");
        gitClient.gitPush("master");
        appendStatus("Push complete.");
    }

    //Create a .gitignore file
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

    //Create a README file
    public void createReadMe(String projectPath, String name) {
        String readmePath = projectPath + "/README.md";
        try {
            FileWriter fw = new FileWriter(readmePath);
            fw.write("# " + name + "\n");
            fw.write("This is the README for the project.\n");
            fw.close();
        } catch (IOException e) {
            throw new RuntimeErrorException(null, "Cannot write file");
        }
    }

    //Append a message to the status area
    private void appendStatus(String msg) {
        statusArea.append(msg + "\n");
        statusArea.setCaretPosition(statusArea.getDocument().getLength());
    }
}