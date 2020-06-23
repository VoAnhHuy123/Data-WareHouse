package ExtractData;
//	import com.chilkatsoft.*;

import com.chilkatsoft.CkGlobal;
import com.chilkatsoft.CkScp;
import com.chilkatsoft.CkSsh;

public class ChilkatExample {

	  static {
	    try {
	        System.loadLibrary("chilkat");
	    } catch (UnsatisfiedLinkError e) {
	      System.err.println("Native code library failed to load.\n" + e);
	      System.exit(1);
	    }
	  }
	  public void getTrial() {
		// This example requires the Chilkat API to have been previously unlocked.
		    // See Global Unlock Sample for sample code.
			  CkGlobal glob = new CkGlobal();
			    boolean success = glob.UnlockBundle("Anything for 30-day trial");
			    if (success != true) {
			        System.out.println(glob.lastErrorText());
			        return;
			        }

			    int status = glob.get_UnlockStatus();
			    if (status == 2) {
			        System.out.println("Unlocked using purchased unlock code.");
			        }
			    else {
			        System.out.println("Unlocked in trial mode.");
			        }

			    // The LastErrorText can be examined in the success case to see if it was unlocked in
			    // trial more, or with a purchased unlock code.
			    System.out.println(glob.lastErrorText());
	  }

	  public static void main(String argv[])
	  {
	    ChilkatExample gg = new ChilkatExample();
	    		gg.getTrial();
		  
	    		CkSsh ssh = new CkSsh();

	    	    // Hostname may be an IP address or hostname:
	    	    String hostname = "http://drive.ecepvn.org:5000/index.cgi?launchApp=SYNO.SDS.App.FileStation3.Instance&launchParam=openfile%3D%252FECEP%252Fsong.nguyen%252FDW_2020%252F&fbclid=IwAR2G9GjUJe9ihM8okxvl9UpoNpTFHZEqEye-Sn0yQTG_cnXn2i3uv2ncuBY";
	    	    int port = 2227;

	    	    // Connect to an SSH server:
	    	    boolean success = ssh.Connect(hostname,port);
	    	    if (success != true) {
	    	        System.out.println(ssh.lastErrorText());
	    	        return;
	    	        }

	    	    // Wait a max of 5 seconds when reading responses..
	    	    ssh.put_IdleTimeoutMs(5000);

	    	    // Authenticate using login/password:
	    	    success = ssh.AuthenticatePw("guest_access","123456");
	    	    if (success != true) {
	    	        System.out.println(ssh.lastErrorText());
	    	        return;
	    	        }

	    	    // Once the SSH object is connected and authenticated, we use it
	    	    // in our SCP object.
	    	    CkScp scp = new CkScp();

	    	    success = scp.UseSsh(ssh);
	    	    if (success != true) {
	    	        System.out.println(scp.lastErrorText());
	    	        return;
	    	        }

	    	    // This downloads a file from the "testApp/logs/" subdirectory (relative to the SSH user account's HOME directory).  
	    	    // For example, if the HOME directory is /Users/chilkat, then the following downloads
	    	    // /Users/chilkat/testApp/logs/test1.log
	    	    String remotePath = "/volume1/ECEP/song.nguyen/DW_2020/data/17130276_Sang_Nhom4.xlsx";
	    	    String localPath = "D:\\New folder\\ggg.xlsx";
	    	    success = scp.DownloadFile(remotePath,localPath);
	    	    if (success != true) {
	    	        System.out.println(scp.lastErrorText());
	    	        return;
	    	        }

	    	    // The following call to DownloadFile specifies an absolute file path on the SSH server:
//	    	    remotePath = "/Users/chilkat/Documents/gecko.jpg";
//	    	    localPath = "c:/temp/images/gecko.jpg";
//	    	    success = scp.DownloadFile(remotePath,localPath);
//	    	    if (success != true) {
//	    	        System.out.println(scp.lastErrorText());
//	    	        return;
//	    	        }

	    	    System.out.println("SCP download file success.");

	    	    // Disconnect
	    	    ssh.Disconnect();
	    	  }
	}

