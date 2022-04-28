import java.io.File 
import groovy.swing.SwingBuilder 
import javax.swing.* 
import java.awt.* 
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.text.SimpleDateFormat;
import javax.swing.RowSorter;
// @Grab(group='org.apache.poi', module='poi', version='4.0.0')
// @Grab(group='org.apache.poi', module='poi-ooxml', version='4.0.0')
import org.apache.poi.poi.*;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;


def myapp = new SwingBuilder()

// get date for report:
def date = new Date()
sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")


def readSats = { f -> 
    def map = [:]
    def list = []
    f.readLines() 
    f.each { it -> 
        def( LID, analysis ) = [it.split("\\|")[0].replaceFirst ("^0*", "").replaceAll("\\s",""), it.split("\\|")[1]]
        if (!LID.toLowerCase().contains("lid") && ( !LID.matches("11071|11072|11097|178") )) {
            map[ LID ] = analysis
            list.add( LID )
        }
    }
    
    // Tell antall med både hcy + mma, :
    list.countBy {it}.findAll { it.value > 1 }.keySet().each{
        map[it] = "MMAHCY"
    }
    // Gå gjennom map, og fjern kolonne 2. Der det kun skal gjøres 1 analyse, legg til analysenavn etter underscore på sample name:
    list = []
    
    map.each {if (it.value == "MMAHCY" ) {
        list.add(it.key)
    } else {
        list.add("${it.key}_${it.value}")
    }
}
    
    return list
}


def OpenSats = { text ->
    // Sets initial path to project dir
    def initialPath = System.getProperty("user.dir");
    JFileChooser fc = new JFileChooser(initialPath);
    fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
    fc.setDialogTitle(text);
    int result = fc.showOpenDialog( null );
    switch ( result ) {
        case JFileChooser.APPROVE_OPTION:
            File file = fc.getSelectedFile();
            def path =  fc.getCurrentDirectory().getAbsolutePath();
            // Lagt til midlertidig
            return file
        break;
        case JFileChooser.CANCEL_OPTION:
        case JFileChooser.ERROR_OPTION:
            break;
    }
}

def SaveReport = { data ->
    def initialPath = System.getProperty("user.dir");
    JFileChooser fc = new JFileChooser(initialPath);
    fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
    fc.setDialogTitle("Lagre liste");
    int result = fc.showOpenDialog( null );
    switch ( result ) { 
        case JFileChooser.APPROVE_OPTION:
            FileWriter writer = new FileWriter(fc.getSelectedFile());
            data.each {
                writer.write( it + "\r\n");
                writer.flush();
            }
            writer.close();
            break;
        case JFileChooser.CANCEL_OPTION:
        case JFileChooser.ERROR_OPTION:
            break;
    }
}

def SaveExcel = { data -> 
    def initialPath = System.getProperty("user.dir");
    JFileChooser fc = new JFileChooser(initialPath);
    fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
    fc.setDialogTitle("Lagre liste");
    int result = fc.showOpenDialog( null );
    switch ( result ) { 
        case JFileChooser.APPROVE_OPTION:
            // 
            FileInputStream fsIP = new FileInputStream(new File("testfiles/190924_A.xls")); //Read the spreadsheet that needs to be updated
            Workbook wb = WorkbookFactory.create( fsIP ); 
            Sheet worksheet = wb.getSheetAt(0); 
            Cell cell = null; // declare a Cell object
            // Add a blank at position 23 hvis det finnes så mange prøver
            // if (data.size() > 24 ) {
            //     data.addAll(24, '178')
            // } else {
            //     data.addAll(data.size(), '178')
            // }
            data.eachWithIndex { d, c ->
                println("---")
                println(d)
                println(c)
                println("---")
                cell = worksheet.getRow(c + 1).getCell(1);
                cell.setCellValue("${d}");

            }

            fsIP.close(); //Close the InputStream
            
            FileOutputStream output_file =new FileOutputStream(fc.getSelectedFile());  //Open FileOutputStream to write updates
            //FileOutputStream output_file =new FileOutputStream(new File("TechartifactExcel.xls"));  //Open FileOutputStream to write updates
            wb.write(output_file); //write changes
            wb.close()
            //      
            break;
        case JFileChooser.CANCEL_OPTION:
        case JFileChooser.ERROR_OPTION:
            break;
    }
}


def process = {    
     
    // Les inn sats
    def satsFile = OpenSats.call("Velg SATS")
    // Gjør om sats til en liste med de prøvene som skal kjøres
    def satsmap = readSats.call(satsFile)    
    //SaveReport.call( satsmap )   
    SaveExcel.call( satsmap )   
        
}

def buttonPanel = {
    myapp.panel(constraints : BorderLayout.SOUTH) {
        button(text : 'Åpne fil', actionPerformed : process ) // Endret fra OpenReportFromLC midlertidig
   } 
}  

def mainPanel = {
   myapp.panel(layout : new BorderLayout()) {
      label(text : 'Åpne en Sats fra flexlab med MMY & HCY', horizontalAlignment : JLabel.CENTER, constraints : BorderLayout.CENTER)
      buttonPanel()   
   }
}  

def myframe = myapp.frame(title : 'MMAHCY v0.1', location : [100, 100],
   size : [400, 300], defaultCloseOperation : WindowConstants.EXIT_ON_CLOSE) {
      mainPanel()
     
   } 
	
myframe.setVisible(true)
