package net.jnorthr.support;
import java.io.*
import javax.swing.JOptionPane;

// groovy code to choose one folder using our Chooser feature
// **************************************************************
import java.io.File;
import java.io.IOException;
import org.apache.log4j.*
import groovy.util.logging.*  

/**
* The Walker program implements a support application that allows user to pick a single file or folder directory and then
* step thru that folder. Options allow drill-down into sub-folders or not; can provide a RegEx expression to choose target files;
* can provide optional Closure to use against each chosen file.
*
* Initially starts to choose artifacts from program working directory and saves user
* choice of path in a local text file 
*
* Use annotation to inject log field into the class.
*
* @author  jnorthr
* @version 1.0
* @since   2017-01-22
*/
@Log4j
public class Walker 
{       
    /**
     * List to hold user selected files from the chooser dialog.
     */
	def files = []
	
    /**
     * Keeps name of folder being traversed.
     */
	def fn = '~/Dropbox/Projects/Website';

    /**
     * Count of selected target files in folder being traversed.
     */
	def count = 0;

    /**
     * List of selected target file names in folder being traversed.
     */
	def result

    /**
     * Map of selected target file suffixes in folder being traversed.
     */
	def map=[:]

    /**
     * Temporary boolean used in provided Closure
     */
	boolean yn;

    /**
     * Temporary string variable.
     */
	def ss;

    /**
     * Handle to Chooser utility.
     */
	Chooser ch;
	
	Object[] options = ["Reuse Path", "Choose Path"];	
   
   // =========================================================================
   /** 
    * Class constructor.
    *
    * defaults to let user pick either a file or a folder
    */
	public Walker()
	{
		int n = JOptionPane.showOptionDialog(null,
    		"Would you like to re-use the same path as before ?",
	    	"Use Prior Path", // frame title
		    JOptionPane.YES_NO_OPTION,
    		JOptionPane.QUESTION_MESSAGE,
    		null,     //do not use a custom Icon
    		options,  //the titles of buttons
    		options[0]); //default button title
    	if (n==JOptionPane.NO_OPTION) {say "NO ="+n;}
    	
        ch = new Chooser();
        ch.selectFolderOnly();
        Response re = ch.getChoice();
        if (re.chosen)
        {
            say "path="+re.path;
            say "file name="+re.artifact;    
            say "Ready to parse input folder "+re.fullname;    
            fn = re.path;
        }
        else
        {
        	say "User made no choice, so output fullame is "+re.fullname+" and path="+re.path;
            say "artifact name="+re.artifact;    
            fn = re.path;
            System.exit(0);
        } // end of else
	} // end of constructor

        
   /** 
    * Closure.
    *
    * defaults to walking the folder looking for files that match a RegEx value
    */
	Closure findTxtFileClos = {
        it.eachDir(findTxtFileClos);
        it.eachFile() {file ->            //Match(~/.*.adoc/) {file ->
                yn = (file.name.startsWith('.')) ? true :  false;
                if (!yn && !file.absolutePath.contains('/.git'))
                {
                    count++;
                    say file.absolutePath;
                    int i = file.name.lastIndexOf('.');                                                                
                    if (i>-1) 
                    {
                        ss = file.name.substring(i+1).toLowerCase()
                        println "i=$i "+ss+" ="+file.name;
                        if(map.containsKey(ss)) {
                          map[ss]+=1;
                        }
                        else
                        {
                            map[ss]=1;
                        }
                    }
                    result += "${file.absolutePath}\n"
                }
        }
    } // end of closure


   /**
    * Method to examine the chosen folder.
    */
    public void parse()
    {
		say "\n------------------------\nparse() ----->"
		def pattern = ~/^.*\.groovy$/
		//new File('/Users/jimnorthrop/Dropbox/Projects/FileWalker/src/main/groovy/net/jnorthr/support').eachFileMatch(~/^.*\.groovy$/) { files << it.name };
/*
		def transform = { str, transformation ->
  			transformation(str)
		}
*/
		//def dirname="/Users/jimnorthrop/Dropbox/Projects/FileWalker"
		new File("$fn").eachDirRecurse { dir ->
		        dir.eachFileMatch(pattern) {    myfile ->
                say  "$myfile matched pattern "+pattern;
                files << myfile;
        	} // eachFileMatch
		} // eachDir

		count = 0
		files.each
		{
    		count++;
    		println it;
		} // end of each

		println "\n===================================\nFound $count files\n"
    } // end of run


    
   /**
    * Method to examine the chosen folder.
    */
    public void run()
    {
		findTxtFileClos(new File(fn))
		say "\n===================================\nFound $count files\nMap contains:"
		map.each{ k, v -> say "${k}:${v}" }
		say "---  end of map ---"
    } // end of run


   /** 
    * Produce log messages using .info method
    */
    public void say(String msg)
    {
    	log.info msg;
    } // end of say
    
    
	// =============================================================================    
    /**
     * The primary method to execute this class. Can be used to test and examine logic and performance issues. 
     * 
     * argument is a list of strings provided as command-line parameters. 
     * 
     * @param  args a list of possibly zero entries from the command line
     */
    public static void main(String[] args)
    {
		/*
		 * need to test get image only files like .jpg using Filter class
		 */
        def ch = new Walker();
        println "\n---------------\n"
		ch.run();
        println "\n---------------\n"
		ch.parse();
        System.exit(0);
    } // end of main    
    
} // end of class