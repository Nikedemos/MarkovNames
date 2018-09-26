package nikedemos.markovnames;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class Form extends JFrame {

  /**
	 * 
	 */
  private static final long serialVersionUID = 4828844368057679043L;
  
  private JButton btnTutup  = new JButton("Tutup");

  private JTextField txtA = new JTextField();

  private JLabel selectGenerator = new JLabel("Choose generator: ");

  public Form(){
    setTitle("MarkovNames by Nikedemos 2018");
    setSize(640,480);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(null);    
    setResizable(false);

    initComponent();    
    initEvent();    
  }

  private void initComponent(){
    btnTutup.setBounds(300,130, 80,25);
    
    selectGenerator.setBounds(8,8,120,16);
    
    txtA.setBounds(128,8,256,18);
    add(btnTutup);
    add(txtA);
    add(selectGenerator);
  }

  private void initEvent(){
	  /*
    btnTutup.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        btnTutupClick(e);
      }
    });
  }

  private void btnTutupClick(ActionEvent evt){
    try{
      txtA.getText();
      
    }catch(Exception e){
      System.out.println(e);
      JOptionPane.showMessageDialog(null, 
          e.toString(),
          "Error", 
          JOptionPane.ERROR_MESSAGE);
    }*/
  } 
}

