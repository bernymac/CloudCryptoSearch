package pt.unlfctdi.cryptosearch.core.client;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import pt.unlfctdi.cryptosearch.cloud.data.document.PDocument;
import pt.unlfctdi.cryptosearch.cloud.data.posting.Posting;

public class PrototypeClientApp {
	
	private static JFrame frame;
	private static JFileChooser fileChooser;
	private static JButton uploadButton;
	private static JTextField searchField;
	private static JTextField downloadField;
	
	private static PrototypeClientConnector connector; 
	
	public static void main (String[] args) throws Exception {
		connector = new PrototypeClientConnector();
		frame = new JFrame("Cloud Crypto Search");
		fileChooser = new JFileChooser("/Users/bernardo/Desktop");
		fileChooser.setMultiSelectionEnabled(true);
		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
		
		frame.getContentPane().add(Box.createRigidArea(new Dimension(0, 20)));
		uploadButton = new JButton("Store New Files");
		uploadButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		frame.getContentPane().add(uploadButton);
		uploadButton.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				if (fileChooser.showOpenDialog(frame.getContentPane()) == JFileChooser.APPROVE_OPTION) {
					connector.addFirstDocuments(fileChooser.getSelectedFiles());
					connector.rebuildIndex();
					JOptionPane.showMessageDialog(frame.getContentPane(), "Files Stored!");
				}
			}
		});
		
		frame.getContentPane().add(Box.createRigidArea(new Dimension(0, 20)));
		searchField = new JTextField("Make a new search and press enter!",30);
		frame.getContentPane().add(searchField);
		searchField.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				List<Posting> postings = connector.query(searchField.getText());
				if (postings == null || postings.size() == 0)
					JOptionPane.showMessageDialog(frame.getContentPane(), "No match found for the query!");
				else {
					String message = "File Name: \t File ID:\n";
					for (int i = 0; i < postings.size(); i++) 
						message += connector.getDocumentName(postings.get(i).getDocId())+" \t "+postings.get(i).getDocId()+"\n";
					JOptionPane.showMessageDialog(frame.getContentPane(),message);
				}
			}
		});
		searchField.addFocusListener(new FocusListener() {
			@Override public void focusLost(FocusEvent e) {
				searchField.setText("Make a new search and press enter!");
			}
			@Override public void focusGained(FocusEvent arg0) {
				searchField.setText("");
			}
		});
		
		frame.getContentPane().add(Box.createRigidArea(new Dimension(0, 20)));
		downloadField = new JTextField("Retrieve a file by ID!");
		frame.getContentPane().add(downloadField);
		downloadField.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				PDocument doc = connector.getDocumentById(downloadField.getText());
				JFrame newFrame = new JFrame(doc.getTitle());
				newFrame.setContentPane(new JScrollPane(new JTextArea(doc.getContent())));
				newFrame.pack();
				newFrame.setVisible(true);
//				JOptionPane.showMessageDialog(frame.getContentPane(),doc.getTitle() +"\n" + doc.getContent() );
			}
		});
		downloadField.addFocusListener(new FocusListener() {
			@Override public void focusLost(FocusEvent e) {
				downloadField.setText("Retrieve a file by ID!");
			}
			@Override public void focusGained(FocusEvent arg0) {
				downloadField.setText("");
			}
		});
		
		frame.getContentPane().add(Box.createRigidArea(new Dimension(0, 20)));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

}
