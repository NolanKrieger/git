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
    private JTextField projectPathField;
    private JButton browseButton;
    private JTextField repoNameField;
    private JTextField usernameField;
    private JTextArea descriptionArea;
    private JRadioButton publicButton, privateButton;
    private JButton initButton;
    private JButton pushButton;
    private JTextArea statusArea;
    private GitSubprocessClient gitClient;
    private GitHubApiClient githubClient;
    private String projectPath;
    private JPasswordField tokenField;

public GitUI() {
        super("GitHub Repo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(700, 800));

        // Load and store original logo image
        Image originalLogo = new ImageIcon("logo.png").getImage();
        JLabel logoLabel = new JLabel();
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.add(logoLabel);
        topPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel folderPanel = new JPanel(new BorderLayout(10, 10));
        folderPanel.setBorder(new TitledBorder("Project Folder"));
        projectPathField = new JTextField();
        browseButton = new JButton("Browse...");
        folderPanel.add(projectPathField, BorderLayout.CENTER);
        folderPanel.add(browseButton, BorderLayout.EAST);
        topPanel.add(folderPanel);

        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Form Fields
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        int row = 0;

        // Repo Name
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Repo Name:"), gbc);
        gbc.gridx = 1;
        repoNameField = new JTextField();
        formPanel.add(repoNameField, gbc);

        // Username Field
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        usernameField = new JTextField();
        formPanel.add(usernameField, gbc);

        // Add Token
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

        // Description
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

        // reset span
        gbc.gridwidth = 1;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        // Visibility
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

        // Bottom Panel: Action & Status
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        initButton = new JButton("Initialize");
        pushButton = new JButton("Push");
        actionPanel.add(initButton);
        actionPanel.add(pushButton);
        bottomPanel.add(actionPanel, BorderLayout.NORTH);

        // Status area
        statusArea = new JTextArea();
        statusArea.setEditable(false);
        statusArea.setRows(5);
        JScrollPane statusScroll = new JScrollPane(statusArea);
        statusScroll.setBorder(new TitledBorder("Status"));

        // Disclaimer label
        JLabel disclaimerLabel = new JLabel("Disclaimer: This application is a prototype and not for commercial use.");
        disclaimerLabel.setFont(disclaimerLabel.getFont().deriveFont(Font.ITALIC, 12f));
        disclaimerLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Combine bottom
        JPanel bottomWrapper = new JPanel(new BorderLayout(10, 10));
        bottomWrapper.add(actionPanel, BorderLayout.NORTH);
        bottomWrapper.add(statusScroll, BorderLayout.CENTER);
        bottomWrapper.add(disclaimerLabel, BorderLayout.SOUTH);

        mainPanel.add(bottomWrapper, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        pack();
        setLocationRelativeTo(null);

        // dynamic resizing of logo
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int width = getContentPane().getWidth();
                int height = getContentPane().getHeight() / 6; // reserve 1/6 for logo
                Image scaled = originalLogo.getScaledInstance(width / 3, height, Image.SCALE_SMOOTH);
                logoLabel.setIcon(new ImageIcon(scaled));
            }
        });

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
        String path = projectPathField.getText().trim();
        String name = repoNameField.getText().trim();
        String user = usernameField.getText().trim();
        String token = new String(tokenField.getPassword()).trim();

        if (path.isEmpty() || name.isEmpty() || token.isEmpty() || user.isEmpty()) {
            appendStatus("Project path, repo name and token  must be set.");
            return;
        }
        appendStatus("Starting initialization...");

        try {

            gitClient = new GitSubprocessClient(path);

            // creating the git init
            gitClient.gitInit();
            appendStatus("Initialized");

            // Write the .gitignore
            create(path);
            appendStatus(".gitignore Created");

            // Write README
            createReadMe(path, name);
            appendStatus("Created ReadMe");

            // Initial commit
            gitClient.gitAddAll();
            gitClient.gitCommit("Initial commit");
            appendStatus("Committed");

            // Setup Github client
            githubClient = new GitHubApiClient(user, token);
            
            // Create remote repo
            RequestParams params = new RequestParams();
            params.addParam("name ", name);
            params.addParam("description", descriptionArea.getText().trim());
            params.addParam("private", privateButton.isSelected());
            params.addParam("public", publicButton.isSelected());

            CreateRepoResponse response = githubClient.createRepo(params);
            String cloneUrl = response.getJson().get("clone_url").getAsString();
            appendStatus("GitHub repo created");

            // Add remote
            gitClient.gitRemoteAdd("origin", cloneUrl);
            appendStatus("Remote origin added");

        } catch (Exception e1) {
            appendStatus("Error : " + e1.getMessage());

        }

    }

    // Add push handler
    private void onPush(ActionEvent e) {
        if (gitClient == null) {
            appendStatus("Initialize");
            return;
        }
        appendStatus("Pushing");
        gitClient.gitPush("master");
        appendStatus("Push complete");

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

    // Creates the README
    public void createReadMe(String projectPath, String name) {
        this.projectPath = "./test";

        // Add README.md
        String readmePath = projectPath + "/README.md";
        try {
            FileWriter fw = new FileWriter(readmePath);
            fw.write("# Project\n");
            fw.write("Hello");
            fw.close();
        } catch (IOException e) {
            throw new RuntimeErrorException(null, "Cannot write file");
        }
    }

    private void appendStatus(String msg) {
        statusArea.append(msg + "\n");
        statusArea.setCaretPosition(statusArea.getDocument().getLength());
    }
}
