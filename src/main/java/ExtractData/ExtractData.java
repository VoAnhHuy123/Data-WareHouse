package ExtractData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.sl.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

public class ExtractData {
	BufferedReader read;
	String server = "jdbc:mysql:";
	String serverConfig = "jdbc:mysql:";
	String addressControlDB = "localhost:3306/mydb";
	String addressDestDatabase;
	String user = "root";
	String pass = "0126";
	Connection con;
	Connection conn;
	String tableName;
	String fileType;
	int numOfcol;
	int numOfdata;
	ArrayList<String> columnsList = new ArrayList<String>();
//	int numberColumns;
	String dataPath;
	ArrayList<Cell> valuesList = new ArrayList<Cell>();
// connect destination database
	public void createConnection() throws SQLException {
		System.out.println("Connecting database....");
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(server + "//" + addressDestDatabase, user, pass);

			System.out.println("Complete!!!!!");
			System.out.println("----------------------------------------------------------------");

		} catch (ClassNotFoundException e) {
			System.out.println("Can't connect!!!!!!!!!!");
			System.out.println("----------------------------------------------------------------");
		}

	}
// get config
	public void getConfig(int id, String delimiter) throws SQLException {
		System.out.println("Getting config....");
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(serverConfig + "//" + addressControlDB, user, pass);
			System.out.println("Connected ControlDB");
			

		} catch (ClassNotFoundException e) {
			System.out.println("Can't connect!!!!!!!!!!");
			System.out.println("----------------------------------------------------------------");
		}
		PreparedStatement pre = (PreparedStatement) con
				.prepareStatement("SELECT * FROM mydb.configuration where id=?;");
		pre.setInt(1, id);
		ResultSet tmp = pre.executeQuery();
		tmp.next();
		tableName = tmp.getString("filename");
		fileType = tmp.getString("filetype");
		numOfcol = Integer.parseInt(tmp.getString("numofcol"));
		String listofcol = tmp.getString("listofcol");
		numOfdata = Integer.parseInt(tmp.getString("numofdata"));
		dataPath = tmp.getString("datapath");
		server = tmp.getString("server");
		user = tmp.getString("username");
		pass = tmp.getString("pass");
		addressDestDatabase = tmp.getString("address_des_database");
		StringTokenizer tokens = new StringTokenizer(listofcol, delimiter);
		while (tokens.hasMoreTokens()) {
			columnsList.add(tokens.nextToken());
		}
		System.out.println("Get config: complete!!!!");
		System.out.println("----------------------------------------------------------------");

	}
// convert xlsx to database
	public void convertData() throws IOException, EncryptedDocumentException, InvalidFormatException, SQLException {
		File file = new File(dataPath);
//		URL url = new URL("http://drive.ecepvn.org:5000/index.cgi?launchApp=SYNO.SDS.App.FileStation3.Instance&launchParam=openfile%3D%252FECEP%252Fsong.nguyen%252FDW_2020%252F&fbclid=IwAR1uHCCSN5m35GwuGLdNM0z41d6kn8IeXXaUBsv006I6EwbyZ8diTKLablg");
		FileInputStream fis = new FileInputStream(file);
//		InputStreamReader fis = new InputStreamReader(url.openStream());
		// creating workbook instance that refers to .xls file
		XSSFWorkbook wb = new XSSFWorkbook(fis);
		// creating a Sheet object to retrieve the object
		XSSFSheet sheet = wb.getSheetAt(0);
		// evaluating cell type
		FormulaEvaluator formulaEvaluator = wb.getCreationHelper().createFormulaEvaluator();
		boolean isFirstRow= true;
		for (Row row : sheet) // iteration over row using for each loop
		{
			
			ArrayList<Cell> ab = new ArrayList<Cell>();
			for (Cell cell : row) // iteration over cell using for each loop
			{
				ab.add(cell);

			}
			if (!isFirstRow) {
				insertData(ab);
			}
			isFirstRow=false;
			
		}
	}
// create table in destination database
	public String sqlCreatTable() {
		String preSql = "CREATE TABLE " + tableName + " (";
		preSql += columnsList.get(0) + " INT PRIMARY KEY NOT NULL,";
		for (int i = 1; i < numOfcol; i++) {
			preSql += columnsList.get(i) + " VARCHAR(100),";

		}
		preSql = preSql.substring(0, preSql.length() - 1) + ");";

		return preSql;

	}

	public void insertData(ArrayList<Cell>g) throws SQLException {

		String preSql = "INSERT INTO " + tableName + "(";
		for (int i = 0; i < numOfcol; i++) {
			preSql += columnsList.get(i) + ",";

		}
		preSql = preSql.substring(0, preSql.length() - 1) + ") VALUES ( ";
		PreparedStatement state;
			String a = preSql;
			a += g.get(0) + ",";
			for (int j = 1; j < g.size(); j++) {
				a += "\"" + g.get(j) + "\"" + ",";

			}
			a = a.substring(0, a.length() - 1) + ");";
			state = conn.prepareStatement(a);
			state.execute();
		
	}

	public void extractData() throws SQLException {
		String sqlCreateTb = sqlCreatTable();
		// create table
		boolean tableStatus = false;
				try {
			System.out.println("Creating table " + tableName + ".......");
			PreparedStatement state = conn.prepareStatement(sqlCreateTb);
			state.execute();
			tableStatus = true;
			System.out.println("Create Table: Complete!!!");
			System.out.println("----------------------------------------------------------------");
		} catch (Exception e) {
			System.out.println("Can't create table " + tableName);
			System.out.println("----------------------------------------------------------------");
		}
		if (tableStatus) {
			try {
				System.out.println("Convert data!!!!!");
				convertData();
				System.out.println("Done!!!!!!");
			} catch (Exception e) {
				System.out.println("Can't convert data from " + dataPath);
			}
		}
		

	}

	public static void main(String[] args)
			throws SQLException, IOException, EncryptedDocumentException, InvalidFormatException {
		ExtractData ex = new ExtractData();
		//get config
		ex.getConfig(2,"|");
		//create connection
		ex.createConnection();
		//extract data
		ex.extractData();

//		ex.readData();
//		ex.insertData();
//		ex.convertDataToSql();
//		ex.sqlCreatTable();
//		ex.extractData();

//		ex.execute();
//		ex.ChangeWordToTxt();

//		ex.getTableInfor();
//		System.out.println(ex.numberColumns);
//		for (int i = 0; i < ex.ColumnList.size(); i++) {
//			System.out.print(ex.ColumnList.get(i)+ " ");
//		}
	}
}