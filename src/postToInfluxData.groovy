/**
 * UrbanCode Deploy Plug-in for InfluxDB
 * 
 * This plugin posts deployment status to InfluxDB
 * @version 1.0
 * @author nikh@ch.ibm.com
 */
import com.urbancode.air.AirPluginTool;
import com.urbancode.air.CommandHelper;

import groovy.json.JsonBuilder

import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.methods.PostMethod
import org.apache.commons.httpclient.methods.StringRequestEntity

final def workDir = new File('.').canonicalFile;
final def props = new Properties();
final def inputPropsFile = new File(args[0]);
try {
    inputPropsStream = new FileInputStream(inputPropsFile);
    props.load(inputPropsStream);
}
catch (IOException e) {
    throw new RuntimeException(e);
}

// properties
final def myURL = props['url'];
final def myDATA = props['data'];
final def myDB = props['db'];    
  

System.out.println("URL:" + myURL + "\n");
System.out.println("DATA:" + myDATA + "\n");
System.out.println("myDB:" + myDB + "\n");

def commandHelper = new CommandHelper(workDir);

// Setup path
try {
	def curPath = System.getenv("PATH");
	def pluginHome = new File(System.getenv("PLUGIN_HOME"))
	println "Setup of path using plugin home: " + pluginHome;
	def binDir = new File(pluginHome, "bin")
	def newPath = curPath+":"+binDir.absolutePath;
	commandHelper.addEnvironmentVariable("PATH", newPath);
} catch(Exception e){
	println "ERROR setting path: ${e.message}";
	System.exit(1);
}


// HTTP POST to InfluxDB
try{

    //def completeRequest = "curl -i -XPOST 'http://ucd:8086/write?db=" + myDB + "' --data-binary 'deployment_status,host=" + myHOST + ",system=" + mySYSTEM + ",component=" + myCOMPONENT + " value=" + myVALUE + "'"
    
    def myRequestRL = myURL + "/write?db=" + myDB
    
	def requestEntity = new StringRequestEntity(
			myDATA,
			"text/plain",
			"UTF-8"
	);
	def http = new HttpClient();
	def post = new PostMethod(myRequestRL);
	post.setRequestEntity(requestEntity);
	
	def status = http.executeMethod(post);
	
	if (status == 200){
		println "Success: ${status}";
		System.exit(0);;
	} else if (status == 204){
		println "Success: ${status}";
		System.exit(0);;
	} else{
		println "Failure: ${status}"
		System.exit(3);
	}	
} catch (Exception exception) {
	println "ERROR setting path: ${exception.message}"
	System.exit(2)
}