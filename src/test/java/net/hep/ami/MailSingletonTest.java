package net.hep.ami;

public class MailSingletonTest {
	/*---------------------------------------------------------------------*/

	public static void main(String[] args) {

		try {

			if(args.length > 1000) {
				MailSingleton.sendMessage("ami@lpsc.in2p3.fr", "odier.jerome@gmail.com", "", "test", "Hello World !");
			}

		} catch(Exception e) {
			System.out.println(e.getMessage());
		}

		System.exit(0);
	}

	/*---------------------------------------------------------------------*/
}
