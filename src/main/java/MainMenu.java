import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.github.rjeschke.txtmark.Processor;
import com.google.gson.Gson;
import com.jthemedetecor.OsThemeDetector;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.FileChooser;
import org.apache.commons.io.FilenameUtils;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainMenu {
    static String futureRestoreGUIVersion = "";

    private JButton selectBlobFileButton;
    private JButton selectTargetIPSWFileButton;
    private JCheckBox updateUCheckBox;
    private JCheckBox waitWCheckBox;
    private JCheckBox debugDCheckBox;
    private JPanel mainMenuView;
    private JButton startFutureRestoreButton;
    private JButton selectFutureRestoreBinaryExecutableButton;
    private JButton selectBuildManifestButton;
    private JButton exitRecoveryButton;
    private JTextArea logTextArea;
    private JComboBox basebandComboBox;
    private JTextField basebandTextField;
    private JComboBox sepComboBox;
    private JTextField sepTextField;
    private JScrollPane logScrollPane;
    private JProgressBar logProgressBar;
    private JTextField currentTaskTextField;
    private JButton stopFutureRestoreUnsafeButton;
    private JButton downloadFutureRestoreButton;
    private JButton settingsButton;
    private JLabel authorAndVersionLabel;
    private JCheckBox pwnedRestoreCheckBox;

    private String futureRestoreFilePath;
    private String blobName;
    private String blobFilePath;
    private String targetIpswName;
    private String targetIpswPath;
    private String sepFilePath;
    private String basebandFilePath;
    private String buildManifestPath;
    private String sepState = "latest";
    private String bbState = "latest";

    private boolean optionDebugState = true;
    private boolean optionUpdateState = false;
    private boolean optionPwndfuState = false;
    private boolean optionWaitState = false;

    public MainMenu() {
        $$$setupUI$$$();
        selectFutureRestoreBinaryExecutableButton.addActionListener(e -> {
            Platform.runLater(() -> {
                FRUtils.setMainMenuEnabled(mainMenuView, false);
                //Create a file chooser
                FileChooser futureRestoreFileChooser = new FileChooser();
                //Open dialogue and set the return file
                File file = futureRestoreFileChooser.showOpenDialog(null);

                if (file != null) {
                    messageToLog("Set " + file.getAbsolutePath() + " to FutureRestore executable.");
                    futureRestoreFilePath = file.getAbsolutePath();
                    //Set name of button to blob file name
                    selectFutureRestoreBinaryExecutableButton.setText("✓ " + file.getName());
                } else
                    System.out.println("Cancelled");
                FRUtils.setMainMenuEnabled(mainMenuView, true);
                mainMenuFrame.requestFocus();
            });
        });
        selectBlobFileButton.addActionListener(e -> {
            Platform.runLater(() -> {
                FRUtils.setMainMenuEnabled(mainMenuView, false);
                //Create a file chooser
                FileChooser blobFileChooser = new FileChooser();
                //Set filter
                FileChooser.ExtensionFilter fileFilter =
                        new FileChooser.ExtensionFilter(
                                "Blob File (SHSH2, SHSH)", "*.shsh2", "*.shsh");
                blobFileChooser.getExtensionFilters().add(fileFilter);
                //Open dialogue and set the return file
                File file = blobFileChooser.showOpenDialog(null);

                if (file != null) {
                    messageToLog("Set " + file.getAbsolutePath() + " to SHSH blob.");
                    blobFilePath = file.getAbsolutePath();
                    blobName = file.getName();
                    selectBlobFileButton.setText("✓ " + file.getName());
                } else
                    System.out.println("Cancelled");
                FRUtils.setMainMenuEnabled(mainMenuView, true);
                mainMenuFrame.requestFocus();
            });
        });
        selectTargetIPSWFileButton.addActionListener(e -> {
            Platform.runLater(() -> {
                FRUtils.setMainMenuEnabled(mainMenuView, false);
                //Create a file chooser
                FileChooser targetIpswFileChooser = new FileChooser();
                //Set filter
                FileChooser.ExtensionFilter fileFilter =
                        new FileChooser.ExtensionFilter(
                                "iOS Firmware (IPSW)", "*.ipsw");
                targetIpswFileChooser.getExtensionFilters().add(fileFilter);
                //Open dialogue and set the return file
                File file = targetIpswFileChooser.showOpenDialog(null);

                if (file != null) {
                    messageToLog("Set " + file.getAbsolutePath() + " to target IPSW.");
                    targetIpswPath = file.getAbsolutePath();
                    targetIpswName = file.getName();
                    //Set name of button to ipsw file name
                    selectTargetIPSWFileButton.setText("✓ " + file.getName());
                } else
                    System.out.println("Cancelled");
                FRUtils.setMainMenuEnabled(mainMenuView, true);
                mainMenuFrame.requestFocus();
            });
        });

        selectBuildManifestButton.addActionListener(e -> {
            Platform.runLater(() -> {
                FRUtils.setMainMenuEnabled(mainMenuView, false);
                //Create a file chooser
                FileChooser targetIpswFileChooser = new FileChooser();
                //Set filter
                FileChooser.ExtensionFilter fileFilter =
                        new FileChooser.ExtensionFilter(
                                "BuildManifest (PList)", "*.plist");
                targetIpswFileChooser.getExtensionFilters().add(fileFilter);
                //Open dialogue and set the return file
                File file = targetIpswFileChooser.showOpenDialog(null);

                if (file != null) {
                    messageToLog("Set " + file.getAbsolutePath() + " to BuildManifest.");
                    buildManifestPath = file.getAbsolutePath();
                    //Set name of button to ipsw file name
                    selectBuildManifestButton.setText("✓ " + file.getName());
                } else
                    System.out.println("Cancelled");
                FRUtils.setMainMenuEnabled(mainMenuView, true);
                mainMenuFrame.requestFocus();
            });
        });

        ActionListener optionsListener = e -> {
            optionDebugState = debugDCheckBox.isSelected();
            optionUpdateState = updateUCheckBox.isSelected();
            optionPwndfuState = pwnedRestoreCheckBox.isSelected();
            optionWaitState = waitWCheckBox.isSelected();
        };
        debugDCheckBox.addActionListener(optionsListener);
        updateUCheckBox.addActionListener(optionsListener);
        pwnedRestoreCheckBox.addActionListener(optionsListener);
        waitWCheckBox.addActionListener(optionsListener);

        startFutureRestoreButton.addActionListener(e -> {
            //Ensure they have FutureRestore selected
            if (futureRestoreFilePath == null) {
                JOptionPane.showMessageDialog(mainMenuView, "Please select a FutureRestore executable.", "No FutureRestore Selected", JOptionPane.ERROR_MESSAGE);
                return;
            }

            //Ensure they actually selected a blob, IPSW, and buildmanifest if needed
            if (blobFilePath == null) {
                JOptionPane.showMessageDialog(mainMenuView, "Select a blob file.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (targetIpswPath == null) {
                JOptionPane.showMessageDialog(mainMenuView, "Select an IPSW file.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (bbState.equals("manual") || sepState.equals("manual")) {
                if (buildManifestPath == null) {
                    JOptionPane.showMessageDialog(mainMenuView, "Select a BuildManifest file.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            //If blob name has a build number in it
            Pattern blobPattern = Pattern.compile("(?<=[_-])[A-Z0-9]{5,10}[a-z]?(?=[_-])");
            Matcher blobMatcher = blobPattern.matcher(blobName);
            String blobBuild = null;
            if (blobMatcher.find()) {
//                System.out.println("Blob build is " + blobMatcher.group(0));
                blobBuild = blobMatcher.group(0);
            }

            //If IPSW has a build name in it
            Pattern ipswPattern = Pattern.compile("(?<=[_-])[A-Z0-9]{5,10}[a-z]?(?=[_-])");
            Matcher ipswMatcher = ipswPattern.matcher(targetIpswName);
            String targetIpswBuild = null;
            if (ipswMatcher.find()) {
//                System.out.println("IPSW build is " + ipswMatcher.group(0));
                targetIpswBuild = ipswMatcher.group(0);
            }

            //If they're different
            if (blobBuild != null && targetIpswBuild != null)
                if (!blobBuild.equals(targetIpswBuild)) {
                    JOptionPane.showMessageDialog(mainMenuView, "The build in your blob name, " + blobBuild + ", does not match the one in the IPSW name, " + targetIpswBuild + ". Ensure you have the right blob and IPSW before continuing.", "Warning", JOptionPane.WARNING_MESSAGE);
                }

            //Build their final command
            ArrayList<String> allArgs = new ArrayList<>();

            allArgs.add("--apticket");
            allArgs.add(blobFilePath);

            if (optionDebugState)
                allArgs.add("--debug");
            if (optionUpdateState)
                allArgs.add("--update");
            if (optionPwndfuState)
                allArgs.add("--use-pwndfu");
            if (optionWaitState)
                allArgs.add("--wait");

            switch (sepState) {
                case "latest":
                    allArgs.add("--latest-sep");
                    break;
                case "manual":
                    allArgs.add("--sep");
                    allArgs.add(sepFilePath);
                    allArgs.add("--sep-manifest");
                    allArgs.add(buildManifestPath);
                    break;
            }

            switch (bbState) {
                case "latest":
                    allArgs.add("--latest-baseband");
                    break;
                case "manual":
                    allArgs.add("--baseband");
                    allArgs.add(basebandFilePath);
                    allArgs.add("--baseband-manifest");
                    allArgs.add(buildManifestPath);
                    break;
                case "none":
                    allArgs.add("--no-baseband");
                    break;
            }

            allArgs.add(targetIpswPath);

            //Run command
            runCommand(allArgs, true);
        });
        exitRecoveryButton.addActionListener(e -> {
            //If they haven't selected a futurerestore yet
            if (futureRestoreFilePath == null) {
                JOptionPane.showMessageDialog(mainMenuView, "Please select a FutureRestore executable in order to exit recovery.", "No FutureRestore Selected", JOptionPane.ERROR_MESSAGE);
                return;
            }

            runCommand(new ArrayList<>(Arrays.asList("--exit-recovery")), false);
        });

        basebandComboBox.addActionListener(e -> {
            switch (basebandComboBox.getSelectedItem().toString()) {
                case "Latest Baseband":
                    bbState = "latest";
                    basebandTextField.setText("✓ (No file)");
                    if (sepState.equals("latest"))
                        selectBuildManifestButton.setEnabled(false);
                    break;
                case "Manual Baseband":
                    Platform.runLater(() -> {
                        FRUtils.setMainMenuEnabled(mainMenuView, false);
                        if (chooseBbfw()) {
                            bbState = "manual";
                            selectBuildManifestButton.setEnabled(true);
                        } else {
                            bbState = "latest";
                            basebandComboBox.setSelectedItem("Latest Baseband");
                            if (sepState.equals("latest"))
                                selectBuildManifestButton.setEnabled(false);
                        }
                        FRUtils.setMainMenuEnabled(mainMenuView, true);
                    });
                    break;
                case "No Baseband":
                    bbState = "none";
                    basebandTextField.setText("✓ (No file)");
                    if (sepState.equals("latest"))
                        selectBuildManifestButton.setEnabled(false);
                    break;
            }
        });
        sepComboBox.addActionListener(e -> {
            switch (sepComboBox.getSelectedItem().toString()) {
                case "Latest SEP":
                    sepState = "latest";
                    sepTextField.setText("✓ (No file)");
                    if (bbState.equals("latest") || bbState.equals("none"))
                        selectBuildManifestButton.setEnabled(false);
                    break;
                case "Manual SEP":
                    Platform.runLater(() -> {
                        FRUtils.setMainMenuEnabled(mainMenuView, false);
                        if (chooseSep()) {
                            sepState = "manual";
                            selectBuildManifestButton.setEnabled(true);
                        } else {
                            sepState = "latest";
                            sepComboBox.setSelectedItem("Latest SEP");
                            if (bbState.equals("latest") || bbState.equals("none"))
                                selectBuildManifestButton.setEnabled(false);
                        }
                        FRUtils.setMainMenuEnabled(mainMenuView, true);
                    });
                    break;
            }
        });
        stopFutureRestoreUnsafeButton.addActionListener(e -> {
            Process futureRestoreProcess = FutureRestoreWorker.futureRestoreProcess;

            if (futureRestoreProcess != null) {
                if (futureRestoreProcess.isAlive()) {
                    int response = JOptionPane.showConfirmDialog(mainMenuView, "Are you sure you want to stop FutureRestore? This is considered unsafe if the device is currently restoring.", "Stop FutureRestore?", JOptionPane.YES_NO_OPTION);
                    if (response == JOptionPane.YES_OPTION) {
                        futureRestoreProcess.destroy();
                        messageToLog("FutureRestore process killed.");
                    }
                }
            }

            startFutureRestoreButton.setEnabled(true);
            stopFutureRestoreUnsafeButton.setText("Stop FutureRestore");
            currentTaskTextField.setText("");
        });

        downloadFutureRestoreButton.addActionListener(event -> {
            String osName = System.getProperty("os.name").toLowerCase();
            String urlString = null;
            String downloadName = null;

            if (osName.contains("mac")) {
                try {
                    Map<String, String> result;
                    if (properties.getProperty("futurerestore_beta").equals("false")) {
                        result = getLatestFrDownload("mac");
                    } else {
                        result = getLatestFrBetaDownload("mac");
                    }
                    urlString = result.get("link");
                    downloadName = result.get("name");
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(mainMenuView, "Unable to download FutureRestore.", "Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                    return;
                }
            } else if (osName.contains("win")) {
                try {
                    Map<String, String> result;
                    if (properties.getProperty("futurerestore_beta").equals("false")) {
                        result = getLatestFrDownload("win");
                    } else {
                        result = getLatestFrBetaDownload("win");
                    }
                    urlString = result.get("link");
                    downloadName = result.get("name");
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(mainMenuView, "Unable to download FutureRestore.", "Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                    return;
                }
            } else if (osName.contains("linux")) {
                try {
                    JOptionPane.showMessageDialog(mainMenuView, "Linux OS detected. Ubuntu is the only OS with a working compiled FutureRestore build. Ensure you are running Ubuntu.", "Ubuntu Only", JOptionPane.INFORMATION_MESSAGE);
                    Map<String, String> result;
                    if (properties.getProperty("futurerestore_beta").equals("false")) {
                        result = getLatestFrDownload("ubuntu");
                    } else {
                        result = getLatestFrBetaDownload("ubuntu");
                    }
                    urlString = result.get("link");
                    downloadName = result.get("name");
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(mainMenuView, "Unable to download FutureRestore.", "Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                    return;
                }
            } else {
                Object[] choices = {"Open link", "Ok"};
                Object defaultChoice = choices[0];

                int response = JOptionPane.showOptionDialog(mainMenuView, "Unknown operating system detected. Please download FutureRestore manually for your operating system.\n" +
                        "https://github.com/m1stadev/futurerestore/releases/latest/", "Download FutureRestore", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, choices, defaultChoice);
                if (response == JOptionPane.YES_OPTION) {
                    FRUtils.openWebpage("https://github.com/m1stadev/futurerestore/releases/latest/", this);
                }
            }

            // Pop-up for this error already shown in getLatestFrDownload()
            if (urlString == null)
                return;

            SwingUtilities.invokeLater(() -> {
                currentTaskTextField.setText("Downloading FutureRestore...");
                messageToLog("Downloading FutureRestore...");
            });

            // Actually download the file
            downloadFutureRestore(urlString, downloadName, osName);
        });

        settingsButton.addActionListener(e -> {
            settingsMenuFrame.setVisible(true);
        });
    }

    public static Properties properties = new Properties();

    static JFrame mainMenuFrame;
    static JFrame settingsMenuFrame;

    public static void main() {
        //load and init prefs
        initializePreferences();

        boolean isDarkThemeUsed = false;
        switch (properties.getProperty("theme_preference")) {
            case "auto": {
                final OsThemeDetector detector = OsThemeDetector.getDetector();
                isDarkThemeUsed = detector.isDark();
                //Must set L&F before we create instance of MainMenu
                if (isDarkThemeUsed) {
                    FlatDarculaLaf.install();
                } else {
//                    //Only set if not Mac
//                    if (!System.getProperty("os.name").toLowerCase().contains("mac"))
                    FlatIntelliJLaf.install();
                }
                break;
            }
            case "light": {
                //Good practice
                isDarkThemeUsed = false;
//                //Only set if not Mac
//                if (!System.getProperty("os.name").toLowerCase().contains("mac"))
                FlatIntelliJLaf.install();
                break;
            }
            case "dark": {
                isDarkThemeUsed = true;
                FlatDarculaLaf.install();
                break;
            }

        }

        final boolean finalIsDarkThemeUsed = isDarkThemeUsed;
        SwingUtilities.invokeLater(() -> {
            mainMenuFrame = new JFrame("FutureRestore GUI");
            settingsMenuFrame = new JFrame("Settings");

            MainMenu mainMenuInstance = new MainMenu();
            SettingsMenu settingsMenuInstance = new SettingsMenu();

            //Auto scroll log
            new SmartScroller(mainMenuInstance.logScrollPane, SmartScroller.VERTICAL, SmartScroller.END);

            //For JavaFX
            new JFXPanel();

            // Main Menu
            mainMenuFrame.setContentPane(mainMenuInstance.mainMenuView);
            //End program on close
            mainMenuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainMenuFrame.pack();
            //Centers it on screen
            mainMenuFrame.setLocationRelativeTo(null);

            // init SettingsMenu
            SettingsMenu.initializeSettingsMenu(settingsMenuInstance);

            // Settings Menu
            settingsMenuFrame.setContentPane(settingsMenuInstance.settingsMenuView);
            //Set settings frame to invisible on close
            settingsMenuFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            settingsMenuFrame.pack();
            //Centers it on screen
            settingsMenuFrame.setLocationRelativeTo(null);

            //Prepare for dark mode
            if (finalIsDarkThemeUsed)
                turnDark(mainMenuInstance);
            else {
                //Custom light UI setup
                mainMenuInstance.startFutureRestoreButton.setBackground(new Color(135, 180, 255));
            }


            //Tell them if they are or are not sharing logs
            if (properties.getProperty("upload_logs").equals("true")) {
                mainMenuInstance.messageToLog("Help improve FutureRestore by sharing logs: Enabled");
            } else {
                mainMenuInstance.messageToLog("Help improve FutureRestore by sharing logs: Disabled");
            }

            //Set text for version
            mainMenuInstance.authorAndVersionLabel.setText("by CoocooFroggy — v" + futureRestoreGUIVersion);

            //Shows the view
            mainMenuFrame.setVisible(true);

            //Only if they have the setting enabled, check for updates
            if (properties.getProperty("check_updates").equals("true")) {
                System.out.println("Checking for FutureRestore GUI updates in the background...");
                mainMenuInstance.messageToLog("Checking for FutureRestore GUI updates in the background...");
                alertIfNewerFRGUIAvailable(mainMenuInstance, futureRestoreGUIVersion);
            }

            //If they previously downloaded FR, set it
            String homeDirectory = System.getProperty("user.home");
            String frPath = homeDirectory + "/FutureRestoreGUI/";
            File extracted = new File(frPath + "extracted/");
            //If ~/FRGUI/extracted/ exists
            if (extracted.exists()) {
                //If a file exists in there, set it
                File[] filesInExtracted = extracted.listFiles();
                if (filesInExtracted != null && filesInExtracted.length > 0) {
                    File frExecutable = filesInExtracted[0];
                    if (frExecutable.exists()) {
                        mainMenuInstance.futureRestoreFilePath = frExecutable.getAbsolutePath();
                        mainMenuInstance.messageToLog("Set previous FutureRestore download, " + frExecutable.getAbsolutePath() + ", to FutureRestore executable.");
                        //Set name of button to blob file name
                        mainMenuInstance.selectFutureRestoreBinaryExecutableButton.setText("✓ " + frExecutable.getName());
                    }
                }
            }

        });
    }

    /*UTILITIES*/

    static void turnDark(MainMenu mainMenu) {
        JPanel mainMenuView = mainMenu.mainMenuView;
        JTextArea logTextArea = mainMenu.logTextArea;
        JScrollPane logScrollPane = mainMenu.logScrollPane;
        JButton startFutureRestoreButton = mainMenu.startFutureRestoreButton;


        mainMenuView.setBackground(new Color(40, 40, 40));
        logTextArea.setBackground(new Color(20, 20, 20));
        logTextArea.setForeground(new Color(200, 200, 200));
        logScrollPane.setBorder(null);

        //Loop through all components to make this faster
        for (Component c : mainMenuView.getComponents()) {
            if (c instanceof JLabel) {
                c.setForeground(new Color(200, 200, 200));
                continue;
            }

            if (c instanceof JButton) {
                c.setBackground(new Color(60, 60, 60));
                c.setForeground(new Color(200, 200, 200));
                if (c == startFutureRestoreButton)
                    c.setBackground(new Color(38, 85, 163));
                continue;
            }

            if (c instanceof JTextField) {
                c.setBackground(new Color(60, 60, 60));
                c.setForeground(new Color(200, 200, 200));
            }

            if (c instanceof JCheckBox) {
                c.setBackground(new Color(40, 40, 40));
                c.setForeground(new Color(200, 200, 200));
            }

            if (c instanceof JComboBox) {
                c.setBackground(new Color(60, 60, 60));
                c.setForeground(new Color(200, 200, 200));
            }
        }
    }

    boolean chooseBbfw() {
        //Create a file chooser
        FileChooser targetIpswFileChooser = new FileChooser();
        //Set filter
        FileChooser.ExtensionFilter fileFilter =
                new FileChooser.ExtensionFilter(
                        "Baseband Firmware (BBFW)", "*.bbfw");
        targetIpswFileChooser.getExtensionFilters().add(fileFilter);
        //Open dialogue and set the return file
        File file = targetIpswFileChooser.showOpenDialog(null);
        mainMenuFrame.requestFocus();

        if (file != null) {
            messageToLog("Set " + file.getAbsolutePath() + " to baseband firmware.");
            basebandTextField.setText("✓ " + file.getName());
            basebandFilePath = file.getAbsolutePath();
            return true;
        } else {
            System.out.println("Cancelled");
            return false;
        }
    }

    boolean chooseSep() {
        //Create a file chooser
        FileChooser targetIpswFileChooser = new FileChooser();
        //Set filter
        FileChooser.ExtensionFilter fileFilter =
                new FileChooser.ExtensionFilter(
                        "SEP (IM4P)", "*.im4p");
        targetIpswFileChooser.getExtensionFilters().add(fileFilter);
        //Open dialogue and set the return file
        File file = targetIpswFileChooser.showOpenDialog(null);
        mainMenuFrame.requestFocus();

        if (file != null) {
            messageToLog("Set " + file.getAbsolutePath() + " to SEP IM4P.");
            sepTextField.setText("✓ " + file.getName());
            sepFilePath = file.getAbsolutePath();
            return true;
        } else {
            System.out.println("Cancelled");
            return false;
        }
    }

    void runCommand(ArrayList<String> allArgs, boolean fullFR) {

        //Preview command if necessary. If returned false, then they clicked copy only, so don't run command.
        if (!previewCommand(allArgs)) return;

        //If they're running an actual restore
        if (fullFR) {
            //Set current task to starting...
            currentTaskTextField.setText("Starting FutureRestore...");

            //Disable interaction
            startFutureRestoreButton.setEnabled(false);
            stopFutureRestoreUnsafeButton.setText("Stop FutureRestore (Unsafe)");

            // Not necessary if we don't check GitHub for version as well
            // Check FutureRestore version
            Runtime runtime = Runtime.getRuntime();
            String version = null;
            try {
                Process process = runtime.exec(futureRestoreFilePath);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                Pattern pattern = Pattern.compile("Version: (.*)");
                String s;
                //Only check the first 5 lines
                for (int i = 0; i < 5; i++) {
                    s = bufferedReader.readLine();
                    Matcher matcher = pattern.matcher(s);
                    if (matcher.find())
                        version = matcher.group(1);
                }
            } catch (IOException ioException) {
                System.out.println("Unable to check FutureRestore version.");
                JOptionPane.showMessageDialog(mainMenuView, "Unable to run FutureRestore. Ensure you selected the correct FutureRestore executable.", "Error", JOptionPane.ERROR_MESSAGE);
                ioException.printStackTrace();
                startFutureRestoreButton.setEnabled(true);
                currentTaskTextField.setText("");
                stopFutureRestoreUnsafeButton.setText("Stop FutureRestore");
                return;
            }


            /*
            // Good idea at first but got kinda annoying every time

            // Ask if they want to check for latest
            if (version == null) {
                JOptionPane.showMessageDialog(mainMenuView, "Unable to check FutureRestore version from selected executable. Manually ensure you have the latest version.", "Warning", JOptionPane.ERROR_MESSAGE);
            } else {
                int response = JOptionPane.showConfirmDialog(mainMenuView, "Your FutureRestore's version: v" + version + ". Would you like to ensure this is the latest version on m1sta's fork?", "FutureRestore Version", JOptionPane.YES_NO_OPTION);
                if (response == JOptionPane.YES_OPTION) {
                    try {
                        String latestVersion = getLatestFutureRestore();
                        if (version.equals(latestVersion)) {
                            JOptionPane.showMessageDialog(mainMenuView, "You're up to date! The latest version is v" + latestVersion + ".");
                        } else {
                            JOptionPane.showMessageDialog(mainMenuView, "You're not on the latest version. The latest version is " + latestVersion + ", and you're on " + version + ".", "Version Mismatch", JOptionPane.WARNING_MESSAGE);
                        }
                    } catch (IOException ioException) {
                        System.out.println("Unable to check for latest FutureRestore");
                        JOptionPane.showMessageDialog(mainMenuView, "Unable to fetch FutureRestore's latest version. Manually check that " + version + " is the latest.", "Unable to Fetch Version", JOptionPane.WARNING_MESSAGE);
                        ioException.printStackTrace();
                    }
                }
            }
            */
        }

        System.out.println("Starting FutureRestore...");
        messageToLog("Make sure to hit \"trust\" on your device if prompted!");

        new Thread(() -> {
            try {
                FutureRestoreWorker.runFutureRestore(futureRestoreFilePath, allArgs, mainMenuView, logTextArea, logProgressBar, currentTaskTextField, startFutureRestoreButton, stopFutureRestoreUnsafeButton);
            } catch (IOException | InterruptedException | TimeoutException e) {
                System.out.println("Unable to run FutureRestore.");
                startFutureRestoreButton.setEnabled(true);
                stopFutureRestoreUnsafeButton.setText("Stop FutureRestore");
                e.printStackTrace();
            }
        }).start();

    }

    int lineNumber = 1;

    void messageToLog(String string) {
        SwingUtilities.invokeLater(() -> {
            logTextArea.append("[" + lineNumber + "] " + string + "\n");
            lineNumber++;
        });
    }

    String getLatestFrTag() throws IOException {
        //Vars
        Map<String, Object> newestRelease = getLatestFrGithub();
        String newestTag = (String) newestRelease.get("tag_name");
        System.out.println("Newest version: " + newestTag);

        return newestTag;
    }

    Map<String, String> getLatestFrDownload(String operatingSystem) throws IOException {
        // operatingSystem = "mac", "windows", "ubuntu"

        Map<String, String> linkNameMap = new HashMap<>();

        Map<String, Object> newestRelease = getLatestFrGithub();
        ArrayList<Map<String, Object>> assets = (ArrayList<Map<String, Object>>) newestRelease.get("assets");
        //Get asset for our operating system
        for (Map<String, Object> asset : assets) {
            String assetName = ((String) asset.get("name"));
            if (assetName.toLowerCase().contains(operatingSystem)) {
                linkNameMap.put("link", (String) asset.get("browser_download_url"));
                linkNameMap.put("name", assetName);
                return linkNameMap;
            }
        }
        //Pop-up saying "no binaries for your OS available"
        noFrForOSPopup();
        return linkNameMap;
    }

    Map<String, String> getLatestFrBetaDownload(String operatingSystem) throws IOException {
        // operatingSystem = "mac", "windows", "ubuntu"

        Map<String, String> linkNameMap = new HashMap<>();

        String content = getRequestUrl("https://api.github.com/repos/m1stadev/futurerestore/actions/artifacts");

        Gson gson = new Gson();
        Map<String, Object> result = gson.fromJson(content, Map.class);
        ArrayList<Map<String, Object>> artifacts = (ArrayList<Map<String, Object>>) result.get("artifacts");

        //Get asset for our operating system
        for (Map<String, Object> artifact : artifacts) {
            String assetName = ((String) artifact.get("name"));
            if (assetName.toLowerCase().contains(operatingSystem)) {
                linkNameMap.put("link", (String) artifact.get("archive_download_url"));
                linkNameMap.put("name", assetName);
                return linkNameMap;
            }
        }

        //Pop-up saying "no binaries for your OS available"
        noFrForOSPopup();
        return linkNameMap;
    }

    private void noFrForOSPopup() {
        Object[] choices = {"Open link", "Ok"};
        Object defaultChoice = choices[0];

        int response = JOptionPane.showOptionDialog(mainMenuView, "No FutureRestore asset found for your operating system. Check releases to see if there's one available.\n" +
                "https://github.com/m1stadev/futurerestore/releases/latest/", "Download FutureRestore", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, choices, defaultChoice);
        if (response == JOptionPane.YES_OPTION) {
            FRUtils.openWebpage("https://github.com/m1stadev/futurerestore/releases/latest/", this);
        }
    }

    public static String getRequestUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        con.disconnect();

        return content.toString();
    }

    private Map<String, Object> getLatestFrGithub() throws IOException {
        String content = getRequestUrl("https://api.github.com/repos/m1stadev/futurerestore/releases");

        Gson gson = new Gson();
        ArrayList<Map<String, Object>> result = gson.fromJson(content, ArrayList.class);
        Map<String, Object> newestRelease = result.get(0);
        return newestRelease;
    }

    void downloadFutureRestore(String urlString, String downloadName, String operatingSystem) {
        //Download asynchronously
        new Thread(() -> {
            String homeDirectory = System.getProperty("user.home");
            File frGuiDir = new File(homeDirectory + "/FutureRestoreGUI/");

            /*
            //Wipe the directory
            try {
                Process process = Runtime.getRuntime().exec("rm -r " + frGuiDir);
                process.waitFor();
            } catch (IOException | InterruptedException e) {
                System.out.println("Unable to wipe FutureRestoreGUI directory.");
                e.printStackTrace();
            }
            */

            //Make directory to store files
            if (!frGuiDir.exists()) {
                frGuiDir.mkdir();
            }

            String finalFrPath = homeDirectory + "/FutureRestoreGUI/";

            // Clean the area first
            File destinationDir = new File(finalFrPath + "extracted/");
            if (destinationDir.exists()) {
                if (destinationDir.listFiles().length > 0) {
                    System.out.println("More than 0 files in dir. Cleaning");
                    try {
                        FileUtils.cleanDirectory(destinationDir);
                    } catch (IOException e) {
                        System.out.println("Unable to delete all existing files in extracted directory. Aborting.");
                        messageToLog("Unable to delete all existing files in extracted directory. Aborting.");
                        e.printStackTrace();
                        currentTaskTextField.setText("");
                        return;
                    }
                }
            }

            File downloadedFr;
            try {
                downloadedFr = FRUtils.downloadFile(urlString, finalFrPath, this);
                if (downloadedFr == null) {
                    System.err.println("Unable to download FutureRestore. Aborting.");
                    messageToLog("Unable to download FutureRestore. Aborting.");
                    SwingUtilities.invokeLater(() -> {
                        currentTaskTextField.setText("");
                        logProgressBar.setValue(0);
                    });
                    return;
                }

                SwingUtilities.invokeLater(() -> {
                    currentTaskTextField.setText("");
                    logProgressBar.setValue(0);
                    messageToLog("FutureRestore finished downloading.");
                });
            } catch (IOException e) {
                System.err.println("Unable to download FutureRestore.");
                messageToLog("Unable to download FutureRestore.");
                e.printStackTrace();
                return;
            }
            //Now unzip the file
            boolean result = false;
            try {
                result = extractFutureRestore(downloadedFr, finalFrPath, operatingSystem);
            } catch (IOException exception) {
                System.out.println("Unable to decompress " + downloadedFr);
                messageToLog("Unable to decompress " + downloadedFr);
                exception.printStackTrace();
            }
            // If fail, set the current task to nothing
            if (!result) {
                SwingUtilities.invokeLater(() -> {
                    currentTaskTextField.setText("");
                });
            }
        }).start();
    }

    boolean extractFutureRestore(File fileToExtract, String finalFrPath, String operatingSystem) throws IOException {
        SwingUtilities.invokeLater(() -> {
            currentTaskTextField.setText("Decompressing FutureRestore...");
            messageToLog("Decompressing FutureRestore...");
        });

        File destinationDir = new File(finalFrPath + "extracted/");

        String downloadedFileExtension = FilenameUtils.getExtension(fileToExtract.getName());
        switch (downloadedFileExtension) {
            case "zip": {
                Archiver archiver = ArchiverFactory.createArchiver("zip");
                archiver.extract(fileToExtract, destinationDir);
                break;
            }
            case "xz": {
                Archiver archiver = ArchiverFactory.createArchiver("tar", "xz");
                archiver.extract(fileToExtract, destinationDir);
                break;
            }
            case "exe":
            case "": {
                FileUtils.copyFileToDirectory(fileToExtract, destinationDir);
                break;
            }
            default: {
                System.out.println("Cannot decompress, unknown file format :(");
                messageToLog("Cannot decompress, unknown file format :(");
                return false;
            }
        }

        deleteFile(fileToExtract);

        // Actions artifacts are in a .zip then in a .tar.xz. Extract again if we need to
        File unzippedFile = destinationDir.listFiles()[0];
        String unzippedExtension = FilenameUtils.getExtension(unzippedFile.getName());
        if (unzippedExtension.equals("xz") || unzippedExtension.equals("zip")) {
            // Move the archive from /FRGUI/extracted to /FRGUI
            FileUtils.moveFileToDirectory(unzippedFile, new File(finalFrPath), false);
            // Declare this file
            File xzFile = new File(finalFrPath + unzippedFile.getName());
            // Extract it and select it (run this method with it)
            boolean result = extractFutureRestore(xzFile, finalFrPath, operatingSystem);
            // Delete the archive at /FRGUI
            deleteFile(xzFile);
            // Return
            return result;
        }

        File futureRestoreExecutable = destinationDir.listFiles()[0];

        if (futureRestoreExecutable == null) {
            System.out.println("Unable to decompress " + fileToExtract);
            messageToLog("Unable to decompress " + fileToExtract);
            return false;
        }

        //Only run on MacOS and Linux
        if (operatingSystem.contains("mac") || operatingSystem.contains("linux")) {
            //Make FutureRestore executable
            Process process;
            try {
                process = Runtime.getRuntime().exec("chmod +x " + futureRestoreExecutable);
                process.waitFor();
            } catch (IOException | InterruptedException e) {
                System.out.println("Unable to make FutureRestore executable.");
                messageToLog("Unable to make FutureRestore executable.");
                e.printStackTrace();
            }
        }

        SwingUtilities.invokeLater(() -> {
            currentTaskTextField.setText("");
            messageToLog("Decompressed FutureRestore");
            futureRestoreFilePath = futureRestoreExecutable.getAbsolutePath();
            messageToLog("Set " + futureRestoreExecutable.getAbsolutePath() + " to FutureRestore executable.");
            //Set name of button to blob file name
            selectFutureRestoreBinaryExecutableButton.setText("✓ " + futureRestoreExecutable.getName());
        });
        return true;

    }

    static void initializePreferences() {
        //Read preferences
        String homeDirectory = System.getProperty("user.home");
        File frGuiDirectory = new File(homeDirectory + "/FutureRestoreGUI/");
        if (!frGuiDirectory.exists())
            frGuiDirectory.mkdir();
        File prefsFile = new File(homeDirectory + "/FutureRestoreGUI/preferences.properties");

        //Init
        if (!prefsFile.exists()) {
            try {
                prefsFile.createNewFile();
            } catch (IOException e) {
                System.out.println("Unable to initialize preferences.");
                e.printStackTrace();
                return;
            }
        }

        try {
            properties.load(new FileReader(prefsFile));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        if (properties.getProperty("upload_logs") == null)
            properties.setProperty("upload_logs", "true");
        if (properties.getProperty("discord_name") == null)
            properties.setProperty("discord_name", "None");
        if (properties.getProperty("preview_command") == null)
            properties.setProperty("preview_command", "false");
        if (properties.getProperty("check_updates") == null)
            properties.setProperty("check_updates", "true");
        if (properties.getProperty("futurerestore_beta") == null)
            properties.setProperty("futurerestore_beta", "false");
        if (properties.getProperty("theme_preference") == null)
            properties.setProperty("theme_preference", "auto");

        savePreferences();
    }

    static void savePreferences() {
        String homeDirectory = System.getProperty("user.home");
        File prefsFile = new File(homeDirectory + "/FutureRestoreGUI/preferences.properties");

        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(prefsFile);
        } catch (FileNotFoundException e) {
            System.out.println("Unable to create output stream for preferences.");
            e.printStackTrace();
            return;
        }

        try {
            properties.store(outputStream, "Preferences for FutureRestore GUI");
        } catch (IOException e) {
            System.out.println("Unable to save preferences.");
            e.printStackTrace();
            return;
        }
    }

    boolean previewCommand(ArrayList<String> allArgs) {
        //If they want to preview command
        if (properties.getProperty("preview_command").equals("true")) {
            StringBuilder commandStringBuilder = new StringBuilder();
            // Surround FutureRestore's path in quotes
            commandStringBuilder.append("\"" + futureRestoreFilePath + "\" ");
            for (String arg : allArgs) {
                if (!arg.startsWith("-")) {
                    // If it's an argument that doesn't start with -, (so a file), surround it in quotes.
                    commandStringBuilder.append("\"" + arg + "\" ");
                    continue;
                }
                commandStringBuilder.append(arg + " ");
            }

            //Build the preview area
            JTextArea commandPreviewTextArea = new JTextArea();
            commandPreviewTextArea.setEditable(false);
            commandPreviewTextArea.setLineWrap(true);

            String finalCommand = commandStringBuilder.toString();
            commandPreviewTextArea.setText(finalCommand);
            JScrollPane scrollPane = new JScrollPane(commandPreviewTextArea);
            scrollPane.setPreferredSize(new Dimension(300, 125));

            Object[] choices = {"Copy command only", "Copy command and run", "Only run"};
            int response = JOptionPane.showOptionDialog(mainMenuView, scrollPane, "Command Preview", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, choices, choices[1]);

            StringSelection stringSelection = new StringSelection(finalCommand);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            switch (response) {
                case 0: {
                    //Copy command only
                    clipboard.setContents(stringSelection, null);
                    messageToLog("Copied \"" + finalCommand + "\" to clipboard.");
                    //Return false, don't continue running
                    return false;
                }
                case 1: {
                    //Copy command and run
                    clipboard.setContents(stringSelection, null);
                    messageToLog("Copied \"" + finalCommand + "\" to clipboard.");
                    //Return true, continue running
                    return true;
                }
                case 2: {
                    //Run only
                    //Return true, continue running
                    return true;
                }
                case JOptionPane.CLOSED_OPTION: {
                    //If they close the popup just don't do anything
                    return false;
                }
            }
        }
        //Return true, continue running since preview command is disabled
        return true;
    }

    static void alertIfNewerFRGUIAvailable(MainMenu mainMenuInstance, String currentFRGUIVersion) {
        new Thread(() -> {
            try {
                String content = getRequestUrl("https://api.github.com/repos/CoocooFroggy/FutureRestore-GUI/releases");

                Gson gson = new Gson();
                ArrayList<Map<String, Object>> result = gson.fromJson(content, ArrayList.class);
                Map<String, Object> newestRelease = result.get(0);
                String newestTag = (String) newestRelease.get("tag_name");
                System.out.println("Newest FRGUI version: " + newestTag);

                //If user is not on latest version
                if (!newestTag.equals(currentFRGUIVersion)) {
                    System.out.println("A newer version of FutureRestore GUI is available.");
                    mainMenuInstance.messageToLog("A newer version of FutureRestore GUI is available.");

                    // Label on top of release notes
                    JLabel label = new JLabel("A newer version of FutureRestore GUI is available.\n" +
                            "You're on version " + currentFRGUIVersion + " and the latest version is " + newestTag + ".");
                    Border padding = BorderFactory.createEmptyBorder(0, 0, 10, 10);
                    label.setBorder(padding);

                    // Fetch release notes
                    String mdReleaseBody = getLatestFrguiReleaseBody();
                    String htmlReleaseBody = "<html>" +
                            "<head>" +
                            "<style type=\"text/css\">" +
                            HTMLPresets.css +
                            "</style>" +
                            "</head>" +
                            "<class=\"markdown-body\">"
                            + Processor.process(mdReleaseBody).replaceAll("\\n", "") +
                            "</class>" +
                            "</html>";
                    System.out.println(htmlReleaseBody);

                    // Build the text area
                    JTextPane whatsNewTextPane = new JTextPane();
                    whatsNewTextPane.setEditable(false);
                    whatsNewTextPane.setContentType("text/html");
                    whatsNewTextPane.setText(htmlReleaseBody);
                    JScrollPane scrollPane = new JScrollPane(whatsNewTextPane);
                    scrollPane.setBorder(null);
                    scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
                    scrollPane.setPreferredSize(new Dimension(150, 300));

                    JPanel panel = new JPanel();
                    BoxLayout boxlayout = new BoxLayout(panel, BoxLayout.Y_AXIS); // Top to bottom
                    panel.setBorder(null);
                    panel.setLayout(boxlayout);
                    panel.add(label);
                    panel.add(scrollPane);

                    Object[] choices = {"Update now", "Remind me later"};
                    Object defaultChoice = choices[0];
                    int response = JOptionPane.showOptionDialog(mainMenuFrame, panel, "Update FutureRestore GUI", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, choices, defaultChoice);

                    if (response == JOptionPane.YES_OPTION) {
//                        FRUtils.openWebpage("https://github.com/CoocooFroggy/FutureRestore-GUI/releases");
                        boolean didSucceedUpdate = FRUtils.updateFRGUI(mainMenuInstance);
                        // If update failed fatally, enable everything again
                        if (!didSucceedUpdate) {
                            FRUtils.setMainMenuEnabled(mainMenuInstance.mainMenuView, true);
                        }
                    }

                } else {
                    System.out.println("You're on the latest version of FutureRestore GUI.");
                    mainMenuInstance.messageToLog("You're on the latest version of FutureRestore GUI.");
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static String getLatestFrguiReleaseBody() throws IOException {
        String content = getRequestUrl("https://api.github.com/repos/CoocooFroggy/FutureRestore-GUI/releases");

        Gson gson = new Gson();
        ArrayList<Map<String, Object>> result = gson.fromJson(content, ArrayList.class);
        Map<String, Object> newestRelease = result.get(0);
        return (String) newestRelease.get("body");
    }

    public static void deleteFile(File fileToDelete) {
        if (!fileToDelete.delete()) {
            try {
                FileUtils.forceDelete(fileToDelete);
            } catch (IOException exception) {
                System.err.println("Unable to delete " + fileToDelete.getAbsolutePath() + ".");
                exception.printStackTrace();
            }
        }
    }

    /* Private vars */
    public JFrame getMainMenuFrame() {
        return mainMenuFrame;
    }

    public JPanel getMainMenuView() {
        return mainMenuView;
    }

    public JProgressBar getLogProgressBar() {
        return logProgressBar;
    }

    public JTextField getCurrentTaskTextField() {
        return currentTaskTextField;
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainMenuView = new JPanel();
        mainMenuView.setLayout(new GridBagLayout());
        final JLabel label1 = new JLabel();
        Font label1Font = this.$$$getFont$$$(null, Font.BOLD, -1, label1.getFont());
        if (label1Font != null) label1.setFont(label1Font);
        label1.setText("Blob");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 0, 10);
        mainMenuView.add(label1, gbc);
        selectTargetIPSWFileButton = new JButton();
        selectTargetIPSWFileButton.setText("Select Target iPSW File...");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.gridwidth = 6;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 10);
        mainMenuView.add(selectTargetIPSWFileButton, gbc);
        final JLabel label2 = new JLabel();
        Font label2Font = this.$$$getFont$$$(null, Font.BOLD, -1, label2.getFont());
        if (label2Font != null) label2.setFont(label2Font);
        label2.setText("Target IPSW");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 0, 10);
        mainMenuView.add(label2, gbc);
        final JLabel label3 = new JLabel();
        Font label3Font = this.$$$getFont$$$(null, Font.BOLD, -1, label3.getFont());
        if (label3Font != null) label3.setFont(label3Font);
        label3.setText("Options");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 0, 10);
        mainMenuView.add(label3, gbc);
        final JLabel label4 = new JLabel();
        Font label4Font = this.$$$getFont$$$(null, Font.BOLD, -1, label4.getFont());
        if (label4Font != null) label4.setFont(label4Font);
        label4.setText("FutureRestore");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 0, 10);
        mainMenuView.add(label4, gbc);
        selectFutureRestoreBinaryExecutableButton = new JButton();
        selectFutureRestoreBinaryExecutableButton.setText("Select FutureRestore Binary/Executable...");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainMenuView.add(selectFutureRestoreBinaryExecutableButton, gbc);
        final JLabel label5 = new JLabel();
        Font label5Font = this.$$$getFont$$$(null, Font.BOLD, 28, label5.getFont());
        if (label5Font != null) label5.setFont(label5Font);
        label5.setText("FutureRestore GUI");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 0, 0);
        mainMenuView.add(label5, gbc);
        authorAndVersionLabel = new JLabel();
        authorAndVersionLabel.setText("by CoocooFroggy");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 10, 0);
        mainMenuView.add(authorAndVersionLabel, gbc);
        final JSeparator separator1 = new JSeparator();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 7;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipady = 1;
        mainMenuView.add(separator1, gbc);
        logScrollPane = new JScrollPane();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 16;
        gbc.gridwidth = 7;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mainMenuView.add(logScrollPane, gbc);
        logTextArea = new JTextArea();
        logTextArea.setColumns(0);
        logTextArea.setEditable(false);
        Font logTextAreaFont = this.$$$getFont$$$("Andale Mono", -1, -1, logTextArea.getFont());
        if (logTextAreaFont != null) logTextArea.setFont(logTextAreaFont);
        logTextArea.setLineWrap(true);
        logTextArea.setRows(20);
        logTextArea.setText("");
        logTextArea.setWrapStyleWord(true);
        logScrollPane.setViewportView(logTextArea);
        selectBlobFileButton = new JButton();
        selectBlobFileButton.setHideActionText(false);
        selectBlobFileButton.setHorizontalAlignment(0);
        selectBlobFileButton.setText("Select Blob File...");
        selectBlobFileButton.setVerticalAlignment(0);
        selectBlobFileButton.setVerticalTextPosition(0);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 6;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 10);
        mainMenuView.add(selectBlobFileButton, gbc);
        basebandComboBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("Latest Baseband");
        defaultComboBoxModel1.addElement("Manual Baseband");
        defaultComboBoxModel1.addElement("No Baseband");
        basebandComboBox.setModel(defaultComboBoxModel1);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 10;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainMenuView.add(basebandComboBox, gbc);
        basebandTextField = new JTextField();
        basebandTextField.setEditable(false);
        basebandTextField.setText("✓ (No file)");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 10;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainMenuView.add(basebandTextField, gbc);
        sepComboBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel2 = new DefaultComboBoxModel();
        defaultComboBoxModel2.addElement("Latest SEP");
        defaultComboBoxModel2.addElement("Manual SEP");
        sepComboBox.setModel(defaultComboBoxModel2);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 11;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainMenuView.add(sepComboBox, gbc);
        sepTextField = new JTextField();
        sepTextField.setEditable(false);
        sepTextField.setText("✓ (No file)");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 11;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainMenuView.add(sepTextField, gbc);
        final JSeparator separator2 = new JSeparator();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 12;
        gbc.gridwidth = 7;
        gbc.weighty = 0.01;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipady = 1;
        mainMenuView.add(separator2, gbc);
        final JSeparator separator3 = new JSeparator();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 7;
        gbc.weighty = 0.01;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipady = 1;
        mainMenuView.add(separator3, gbc);
        final JLabel label6 = new JLabel();
        label6.setText("Arguments");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.WEST;
        mainMenuView.add(label6, gbc);
        startFutureRestoreButton = new JButton();
        Font startFutureRestoreButtonFont = this.$$$getFont$$$(null, Font.BOLD, 16, startFutureRestoreButton.getFont());
        if (startFutureRestoreButtonFont != null) startFutureRestoreButton.setFont(startFutureRestoreButtonFont);
        startFutureRestoreButton.setText("Start FutureRestore");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 13;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.BOTH;
        mainMenuView.add(startFutureRestoreButton, gbc);
        exitRecoveryButton = new JButton();
        exitRecoveryButton.setText("Exit Recovery");
        exitRecoveryButton.setVerticalAlignment(0);
        exitRecoveryButton.setVerticalTextPosition(0);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 13;
        gbc.fill = GridBagConstraints.BOTH;
        mainMenuView.add(exitRecoveryButton, gbc);
        final JLabel label7 = new JLabel();
        Font label7Font = this.$$$getFont$$$(null, Font.BOLD, -1, label7.getFont());
        if (label7Font != null) label7.setFont(label7Font);
        label7.setText("Current Task");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 14;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(15, 10, 15, 10);
        mainMenuView.add(label7, gbc);
        logProgressBar = new JProgressBar();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 15;
        gbc.gridwidth = 7;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainMenuView.add(logProgressBar, gbc);
        settingsButton = new JButton();
        settingsButton.setText("Settings");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0, 0, 0, 10);
        mainMenuView.add(settingsButton, gbc);
        downloadFutureRestoreButton = new JButton();
        downloadFutureRestoreButton.setText("Download FutureRestore");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 10);
        mainMenuView.add(downloadFutureRestoreButton, gbc);
        updateUCheckBox = new JCheckBox();
        updateUCheckBox.setText("Preserve Data");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 7;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        mainMenuView.add(updateUCheckBox, gbc);
        final JLabel label8 = new JLabel();
        Font label8Font = this.$$$getFont$$$("Menlo", -1, 10, label8.getFont());
        if (label8Font != null) label8.setFont(label8Font);
        label8.setText("(--update)");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 8;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(0, 25, 0, 0);
        mainMenuView.add(label8, gbc);
        waitWCheckBox = new JCheckBox();
        waitWCheckBox.setText("AP Nonce Collision");
        gbc = new GridBagConstraints();
        gbc.gridx = 6;
        gbc.gridy = 7;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 0, 10);
        mainMenuView.add(waitWCheckBox, gbc);
        final JLabel label9 = new JLabel();
        Font label9Font = this.$$$getFont$$$("Menlo", -1, 10, label9.getFont());
        if (label9Font != null) label9.setFont(label9Font);
        label9.setText("(--wait)");
        gbc = new GridBagConstraints();
        gbc.gridx = 6;
        gbc.gridy = 8;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(0, 25, 0, 0);
        mainMenuView.add(label9, gbc);
        final JSeparator separator4 = new JSeparator();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 9;
        gbc.gridwidth = 6;
        gbc.weighty = 0.01;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipady = 1;
        gbc.insets = new Insets(7, 0, 0, 0);
        mainMenuView.add(separator4, gbc);
        final JLabel label10 = new JLabel();
        Font label10Font = this.$$$getFont$$$("Menlo", -1, 10, label10.getFont());
        if (label10Font != null) label10.setFont(label10Font);
        label10.setText("(--debug)");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 8;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(0, 25, 0, 0);
        mainMenuView.add(label10, gbc);
        debugDCheckBox = new JCheckBox();
        debugDCheckBox.setSelected(true);
        debugDCheckBox.setText("Extra Logs");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 7;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        mainMenuView.add(debugDCheckBox, gbc);
        pwnedRestoreCheckBox = new JCheckBox();
        pwnedRestoreCheckBox.setText("Pwned Restore");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        mainMenuView.add(pwnedRestoreCheckBox, gbc);
        final JLabel label11 = new JLabel();
        Font label11Font = this.$$$getFont$$$("Menlo", -1, 10, label11.getFont());
        if (label11Font != null) label11.setFont(label11Font);
        label11.setText("(--use-pwndfu)");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(0, 25, 0, 0);
        mainMenuView.add(label11, gbc);
        currentTaskTextField = new JTextField();
        currentTaskTextField.setEditable(false);
        Font currentTaskTextFieldFont = this.$$$getFont$$$(null, -1, 18, currentTaskTextField.getFont());
        if (currentTaskTextFieldFont != null) currentTaskTextField.setFont(currentTaskTextFieldFont);
        currentTaskTextField.setHorizontalAlignment(0);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 14;
        gbc.gridwidth = 6;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 10);
        mainMenuView.add(currentTaskTextField, gbc);
        final JLabel label12 = new JLabel();
        label12.setText("OR");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 5, 0, 5);
        mainMenuView.add(label12, gbc);
        selectBuildManifestButton = new JButton();
        selectBuildManifestButton.setEnabled(false);
        selectBuildManifestButton.setText("Select BuildManifest...");
        gbc = new GridBagConstraints();
        gbc.gridx = 6;
        gbc.gridy = 10;
        gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 10);
        mainMenuView.add(selectBuildManifestButton, gbc);
        stopFutureRestoreUnsafeButton = new JButton();
        stopFutureRestoreUnsafeButton.setText("Stop FutureRestore");
        gbc = new GridBagConstraints();
        gbc.gridx = 6;
        gbc.gridy = 13;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 10);
        mainMenuView.add(stopFutureRestoreUnsafeButton, gbc);
        final JLabel label13 = new JLabel();
        Font label13Font = this.$$$getFont$$$(null, Font.BOLD, -1, label13.getFont());
        if (label13Font != null) label13.setFont(label13Font);
        label13.setText("Controls");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 13;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 0, 10);
        mainMenuView.add(label13, gbc);
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainMenuView;
    }

}
