import java.io.IOException;

public class ContactReader {
	public static boolean parseName(String name){
		for(int i=0; i<name.length(); i++) {
			char c = name.charAt(i);
			if(!Character.isLetter(c) && !Character.isSpaceChar(c)) {
				return false;
			}
		}
		return true;
	}
	public static boolean parseEmail(String em) {
		//check that email only has letters or @ and .
		for(int i=0; i<em.length(); i++) {
			char c = em.charAt(i);
			if(c!='@' && c!='.' && !Character.isLetter(c)) {
				return false;
			}
		}
		//check xxx@yyy.zzz format
		int at = em.indexOf('@',0);
		if(at == -1) {
			return false;
		}
		String xxx = em.substring(0, at);
		int dot = em.indexOf('.',at);
		if(dot == -1) {
			return false;
		}
		String yyy = em.substring(at+1, dot);
		String zzz = em.substring(dot+1).trim();
		if(xxx.isEmpty() || yyy.isEmpty()) {
			return false;
		}
		if(zzz.compareTo("com")!=0 && zzz.compareTo("net")!=0 && zzz.compareTo("edu")!=0){
			return false;
		}
		return true;
	}
	//parseNC returns 0 if TRUE, 1 if FALSE, -1 if invalid
	public static int parseNC(String nc) {
		String nC = nc.trim();
		nC.toUpperCase();
		if(nC.compareTo("TRUE")==0) {
			return 0;
		}
		else if(nC.compareTo("FALSE")==0) {
			return 1;
		}
		else {
			return -1;
		}
	}
	
	public static Contact ReadContact(String line) throws IOException {
		if(line.isEmpty()) {return null;}
		String arr[] = new String[6];
		String fn;
		String ln;
		String em;
		int a;
		boolean nc;
		String n;
		//Check for enough parameters
		int index =0; 
		for(int i=0; i<6; i++) {
			int nextindex = line.indexOf(',', index);
			String message1 = "There are not enough parameters on line";
			String message2 = "'" + line + "'.";
			IOException ioe = new IOException(message1 + "\n" + message2);
			//if we are at notes, put the rest of line in
			if(i==5) {
				String not = line.substring(index+1);
				if(!not.isEmpty()) {
					arr[i] = not;
				}
				else {throw ioe;}
			}
			else if(nextindex == -1) {throw ioe;}
			else {
				arr[i] = line.substring(index,nextindex);
				index = nextindex+1;
				//make sure we don't get an out of bounds error
				if(index >= line.length()) {
					index = line.length()-1;
				}
			}
		}
		//Check for right data types
		//Check first name and last name
		for(int j=0; j<2; j++) {
			boolean validname = parseName(arr[j]);
			if(!validname) {
				String message = "The parameter '" + arr[j] +"' cannot be parsed as a name.";
				IOException ioe = new IOException(message);
				throw ioe;
			}
		}
		fn = arr[0].trim();
		ln = arr[1].trim();
		//Check email
		boolean validemail = parseEmail(arr[2]);
		if(!validemail) {
			String message = "The parameter '" + arr[2] +"' cannot be parsed as an email.";
			IOException ioe = new IOException(message);
			throw ioe;
		}
		else {
			em = arr[2].trim();
		}
		//Check age
		Integer tempa = null;
		try {
			tempa = new Integer(arr[3]);
		} catch(NumberFormatException e) {
			String message = "The parameter '" + arr[3] +"' is not a valid age.";
			IOException ioe = new IOException(message);
			throw ioe;
		}
		a = tempa;
		//Check nearCampus
		int validnc = parseNC(arr[4]);
		if(validnc==-1) {
			String message = "The parameter '" + arr[4] +"' must be TRUE or FALSE.";
			IOException ioe = new IOException(message);
			throw ioe;
		}
		else if(validnc==0) {
			nc = true;
		}
		else {
			nc = false;
		}
		n = arr[5];
		
		Contact con = new Contact(fn,ln,em,a,nc,n);
		return con;
	}
}
