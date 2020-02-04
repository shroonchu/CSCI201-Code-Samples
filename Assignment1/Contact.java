public class Contact {
	private String firstName;
	private String lastName;
	private String email;
	private int age;
	private boolean nearCampus;
	private String notes;
	public Contact(String fn, String ln, String em, int a, boolean nc, String n) {
		firstName = fn;
		lastName = ln;
		email = em;
		age = a;
		nearCampus = nc;
		notes = n;
	}
	public String getFullName() {
		return firstName + " " + lastName;
	}
	public String getLastName() {
		return lastName;
	}
	public String getEmail() {
		return email;
	}
	public String contactInfo() {
		String info = "";
		info += "Name: " + firstName + " " + lastName + "\n";
		info += "Email: " + email + "\n";
		info += "Age: " + age + "\n";
		String nc = (nearCampus) ? "Yes" : "No";
		info += "Near Campus: " + nc + "\n";
		info += "Notes: " + notes + "\n";
		info += "\n";
		return info;
	}
	
	
}
