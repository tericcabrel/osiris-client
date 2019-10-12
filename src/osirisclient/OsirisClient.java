package osirisclient;

import com.sun.javacard.apduio.Apdu;
import com.sun.javacard.apduio.CadT1Client;
import com.sun.javacard.apduio.CadTransportException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author ZEGEEK
 */
public class OsirisClient {
    
    /* Constants */
    public static final byte CLA_OSIRIS = (byte) 0x3A;

    public static final byte INS_GET_DATA = 0x00;
    public static final byte INS_SET_DATA = 0x01;
    public static final byte INS_SET_NAME = 0x02;
    public static final byte INS_SET_BIRTHDATE = 0x03;
    public static final byte INS_RESET_DATA = 0x04;
    
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     * @throws com.sun.javacard.apduio.CadTransportException
     */
    public static void main(String[] args) throws IOException, CadTransportException {
        // Connect to Java Card
        CadT1Client cad;
        Socket sckCarte;
        try {
            sckCarte = new Socket("localhost", 9025);
            sckCarte.setTcpNoDelay(true);
            BufferedInputStream input = new BufferedInputStream(sckCarte.getInputStream());
            BufferedOutputStream output = new BufferedOutputStream(sckCarte.getOutputStream());
            cad = new CadT1Client(input, output);
        } catch (IOException e) {
            System.out.println("Error: Can not connect to Java Card");
            return;
        }		
		
        // Turning on the card
        try {
            cad.powerUp();
        } catch (CadTransportException | IOException e) {
            System.out.println("Error sending the powerUp command to the Java Card");
            return;
        }

        // Select the applet
        Apdu apdu = new Apdu();
        apdu.command[Apdu.CLA] = 0x00;
        apdu.command[Apdu.INS] = (byte) 0xA4;
        apdu.command[Apdu.P1] = 0x04;
        apdu.command[Apdu.P2] = 0x00;
        
        byte[] appletAID = { (byte) 0x61, (byte) 0xC1, (byte) 0x9D, (byte) 0x2B, (byte) 0x05, (byte) 0x35 };
        apdu.setDataIn(appletAID);
        cad.exchangeApdu(apdu);
        if (apdu.getStatus() != 0x9000) {
            System.out.println("Error while selecting the applet");
            System.exit(1);
        }
			
        // Main menu principal
        boolean end = false;
        while (!end) {
            System.out.println();
            System.out.println("ORISIS CLIENT");
            System.out.println("----------------------------");
            System.out.println();
            System.out.println("1 - GET DATA");
            System.out.println("2 - SET DATA");
            System.out.println("3 - SET NAME");
            System.out.println("4 - SET BIRTH DATE");
            System.out.println("5 - RESET");
            System.out.println("6 - QUIT");
            System.out.println();
            System.out.println("Your choice: ");

            int choice = System.in.read();
            while (!(choice >= '1' && choice <= '6')) {
                    choice = System.in.read();
            }

                apdu = new Apdu();
                apdu.command[Apdu.CLA] = OsirisClient.CLA_OSIRIS;
                apdu.command[Apdu.P1] = 0x00;
                apdu.command[Apdu.P2] = 0x00;

                switch (choice) {
                    case '1':
                        apdu.command[Apdu.INS] = OsirisClient.INS_GET_DATA;
                        cad.exchangeApdu(apdu);
                        if (apdu.getStatus() != 0x9000) {
                            System.out.println("An error occurred with status: " + apdu.getStatus());
                        } else {
                            System.out.print("Data : " + Utils.byteArrayToString(apdu.dataOut));
                        }
                        break;

                case '2':
                    /*apdu.command[Apdu.INS] = Jcardclient.INS_INCREMENTER_COMPTEUR;
                    cad.exchangeApdu(apdu);
                    if (apdu.getStatus() != 0x9000) {
                            System.out.println("Erreur : status word different de 0x9000");
                    } else {
                            System.out.println("OK");
                    }*/
                    break;
                case '3':
                    System.out.println("OK");
                break;
                    case '4':
                        /* apdu.command[Apdu.INS] = Jcardclient.INS_INITIALISER_COMPTEUR;
                        byte[] donnees = new byte[1];
                        donnees[0] = 0;
                        apdu.setDataIn(donnees);
                        cad.exchangeApdu(apdu);
                        if (apdu.getStatus() != 0x9000) {
                                System.out.println("Erreur : status word different de 0x9000");
                        } else {
                                System.out.println("OK");
                        }*/
                    break;
                case '5':
                        System.out.print("OK");
                    break;
                case '6':
                        end = true;
                    break;
            }
        }
		
        // Turning off the card
        try {
            cad.powerDown();
        } catch (CadTransportException | IOException e) {
            System.out.println("Error sending powerDown command to Java Card");
        }		
    }
}
