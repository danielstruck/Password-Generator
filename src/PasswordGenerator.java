import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.PrintStream;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

class PasswordGenerator {
	private static final String saveFileDelimeter = " = ";
	private static final String saveDir = System.getProperty("user.dir");
	private static final File saveFile = createNewWhitelistFile(saveDir, "Password Whitelist.txt");
	private static final IndexedStringArray fileData = new IndexedStringArray(saveFile);
    private static String masterPass = fileData.getNext();
    private static int passLength = Integer.parseInt(fileData.getNext());

    PasswordGenerator() {
    	try {
	    	runWithGUI();
    	} catch (Exception e) {
    		e.printStackTrace();
    		JOptionPane.showMessageDialog(null, e.getMessage(), "An error has occured", JOptionPane.ERROR_MESSAGE);
    	}
    }
    private void runWithGUI() throws Exception {
    	JLabel prompt = new JLabel("Site: ");
    	JRadioButton updateWhitelist = new JRadioButton("Update whitelist");
    	JPanel panel = new JPanel();
    	panel.add(prompt);
    	panel.add(updateWhitelist);
        String site = JOptionPane.showInputDialog(panel);
        if (site == null)
        	System.exit(0);
        site = trimSiteName(site);
        
        String password = hash(site, masterPass);
        
        StringSelection passSelection = new StringSelection(password);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(passSelection, null);
        
        if (updateWhitelist.isSelected())
        	updateWhitelistFor(site);
        
        JTextField box = new JTextField(password);
        JOptionPane.showMessageDialog(null, box, site, JOptionPane.INFORMATION_MESSAGE);
    }
    public static void main(String[] args) throws Exception {
    	new PasswordGenerator();
    	System.exit(0);
    }

    private String hash(String site, String key) throws Exception {
        String password = "1Da";
        
        String line;
        while ((line = fileData.getNext()) != null) {
    		String[] split = line.split(saveFileDelimeter);
        	String lineName = split[0];
        	if (lineName.equals(site)) {
        		char newPassSymbol = split[1].charAt(0);
        		
        		String siteAdjusted = "";
        		for (char c: site.toCharArray())
        			siteAdjusted += c + newPassSymbol;
        		site = siteAdjusted;
        		break;
        	}
        }

        for (int i = 0; i < passLength; ++i) {
            char nextChar = (char) ((key.charAt(i%key.length()) << 8 ^ site.charAt(i%site.length()) + i) % 93 + 32);

            if (nextChar == '\\')
                nextChar = '\\';
            else if (nextChar == '\n')
                nextChar = ' ';

            password += nextChar;
        }

        return password;
    }

    private String trimSiteName(String site) {
        String tor = "";

        for (char c: site.toCharArray())
            if (c != ' ')
            	tor += c;
        
        return tor.toLowerCase();
    }
    
    private static File createNewWhitelistFile(String path, String name) {
    	File file = new File(path + "\\" + name);
    	
    	try {
	    	if (!file.exists()) {
	    		file.createNewFile();
	    		
	    		PrintStream printer = new PrintStream(file);
	    		printer.println("*d3f@u1t p@55w()rd*");
	    		printer.println("16");
	    		printer.close();
	    	}
    	} catch (Exception e) {
    		JOptionPane.showMessageDialog(null, e.getMessage(), "An error occured when creating the whitelist file", JOptionPane.ERROR_MESSAGE);
    	}
    	
    	return file;
    }
    
    private static void updateWhitelistFor(String siteName) throws Exception {
    	IndexedStringArray toWrite = new IndexedStringArray(fileData);
    	
    	boolean siteIsWhitelisted = false;
    	String line;
    	PrintStream printer = new PrintStream(saveFile);

    	printer.println(toWrite.getNext()); // skip over the master pass
    	printer.println(toWrite.getNext()); // skip over the pass length
    	while ((line = toWrite.getNext()) != null) {
    		String[] split = line.split(saveFileDelimeter);
    		if (split[0].equals(siteName)) {
    			siteIsWhitelisted = true;
    			line = saveFormat(split[0], (char) split[1].charAt(0) + 1);
    		}
    		
    		printer.println(line);
    	}
    	
    	if (!siteIsWhitelisted) {
    		printer.println(saveFormat(siteName));
    	}
    	
    	printer.close();
    }
    
    private static String saveFormat(String siteName) {
    	return saveFormat(siteName, 32);
    }
    private static String saveFormat(String siteName, int passwordValue) {
    	if (passwordValue < 32 || passwordValue > 126)
    		passwordValue = 32;
    	
    	return siteName + saveFileDelimeter + ((char) passwordValue);
    }
}
