package mychessmate;

import javax.swing.*;
import java.awt.*;


public class    AboutPane extends JPanel{
    
    //Create AboutPane Init
    public AboutPane(){
        
        //declare variable
        setLayout(new BorderLayout());
        JPanel northPane = new NorthPane();       
        JPanel centerPane = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints(); 
        
        //Set background for JPanel and the space, Center mod
        northPane.setBackground(Color.MAGENTA);
        centerPane.setBackground(Color.decode("#efd39c"));
        c.insets = new Insets(4,4,4,4);
        c.fill = GridBagConstraints.HORIZONTAL;        

        //Varible some text for about, get from Properties file
        String[][] values = new String[][]{
            {"Project","MyChessmate "+GameData.VERSION},
            {"Category","Game"},
            {"Author",GameData.AUTHOR},
            {"Date created",GameData.DATECREATE}
        };
        
        //Enable text above to JPanel
        for(int i=0; i<values.length; i++){
            JLabel header = new JLabel(values[i][0]+": ");
            header.setFont(new Font(header.getFont().getName(),Font.BOLD,13));
            JLabel data = new JLabel(values[i][1]);
            c.gridx = 0;
            c.gridy = i;
            centerPane.add(header,c);
            c.gridx = 1;
            centerPane.add(data,c);
        }
        centerPane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        //The end, add this to JPanel parent
        add(northPane,BorderLayout.NORTH);
        add(centerPane,BorderLayout.CENTER);
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    }    
    
    //Create JFrame and add info and Enable to UI
    public static void createAndShowUI(){                   
        JFrame f = new JFrame("AboutBox");
        AboutPane ap = new AboutPane();
        f.getContentPane().add(ap);
        f.setResizable(false);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
    
    //Create Jpanel for title Aboutbox, 
    class NorthPane extends JPanel{
        NorthPane(){          
            JLabel label = new JLabel("About MyChessmate",JLabel.LEFT);
            label.setFont(new Font(label.getFont().getName(),Font.BOLD,15));
            label.setForeground(Color.decode("#9900AF"));
            add(label);
        }
        //Draw the Line to space title JPanel and information Game
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            int width = this.getWidth()- 5;
            int height = this.getHeight() - 1;
            g.setColor(Color.decode("#9900FF"));
            g.drawLine(0, height, width, height);
        }
    }
}
