import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class game{
    public static void main(String args[]){
        JFrame myFrame = new JFrame("Sample Frame");
        myFrame.setSize(300,400);
        myFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        myFrame.setVisible(true);
    }
}