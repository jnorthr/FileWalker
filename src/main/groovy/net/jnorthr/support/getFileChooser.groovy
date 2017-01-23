// groovy sample to choose one file using java's  JFileChooser
// would only allow choice of a single directory by setting another JFileChooser feature
// http://docs.oracle.com/javase/tutorial/uiswing/components/filechooser.html

// see more examples in above link to include a file filter
import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;

// start to choose files from pwd
def initialPath = System.getProperty("user.dir");

JFileChooser fc = new JFileChooser(initialPath);

// fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

int result = fc.showOpenDialog( null );
switch ( result )
{
   case JFileChooser.APPROVE_OPTION:
      File file = fc.getSelectedFile();
      
      def path =  fc.getCurrentDirectory().getAbsolutePath();
      println "path="+path+"\nfile name="+file.toString();
      break;
   case JFileChooser.CANCEL_OPTION:
   case JFileChooser.ERROR_OPTION:
      break;
}