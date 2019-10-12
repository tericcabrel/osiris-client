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
    private final static byte INS_PIN_AUTH = (byte) 0x05;
    private final static byte INS_PIN_UNBLOCK = (byte) 0x06;
    
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
            System.out.println("Error while selecting the applet: " + apdu.getStatus());
            System.exit(1);
        }
			
        // Main menu principal
        boolean end = false;
        while (!end) {
            System.out.println();
            System.out.println("ORISIS CLIENT");
            System.out.println("----------------------------");
            System.out.println();
            System.out.println("1 - AUTHENTICATE");
            System.out.println("2 - GET DATA");
            System.out.println("3 - SET DATA");
            System.out.println("4 - SET NAME");
            System.out.println("5 - SET BIRTH DATE");
            System.out.println("6 - RESET");
            System.out.println("7 - UNBLOCK");
            System.out.println("8 - QUIT");
            System.out.println();
            System.out.println("Your choice: ");

            int choice = System.in.read();
            while (!(choice >= '1' && choice <= '8')) {
                    choice = System.in.read();
            }

            apdu = new Apdu();
            apdu.command[Apdu.CLA] = OsirisClient.CLA_OSIRIS;
            apdu.command[Apdu.P1] = 0x00;
            apdu.command[Apdu.P2] = 0x00;

            switch (choice) {
                case '1':
                    apdu.command[Apdu.INS] = OsirisClient.INS_PIN_AUTH;
                    byte[] pinCode = "1234".getBytes();

                    apdu.setDataIn(pinCode);
                    cad.exchangeApdu(apdu);
                    handleResponse(apdu);
                    break;
                case '2':
                    apdu.command[Apdu.INS] = OsirisClient.INS_GET_DATA;
                    cad.exchangeApdu(apdu);

                    if (apdu.getStatus() != 0x9000) {
                        System.out.println("An error occurred with status: " + apdu.getStatus());
                    } else {
                        System.out.print("Data : " + Utils.byteArrayToString(apdu.dataOut));
                    }
                    break;
                case '3':
                    apdu.command[Apdu.INS] = OsirisClient.INS_SET_DATA;
                    byte[] data = "uid|name|birth".getBytes();

                    apdu.setDataIn(data);
                    cad.exchangeApdu(apdu);
                    handleResponse(apdu);
                    break;
                case '4':
                    apdu.command[Apdu.INS] = OsirisClient.INS_SET_NAME;
                    byte[] nameData = "tericcabrel".getBytes();

                    apdu.setDataIn(nameData);
                    cad.exchangeApdu(apdu);
                    handleResponse(apdu);
                    break;
                case '5':
                    apdu.command[Apdu.INS] = OsirisClient.INS_SET_BIRTHDATE;
                    byte[] birthData = "5991-30-14".getBytes();

                    apdu.setDataIn(birthData);
                    cad.exchangeApdu(apdu);
                    handleResponse(apdu);
                    break;
                case '6':
                    apdu.command[Apdu.INS] = OsirisClient.INS_RESET_DATA;
                    cad.exchangeApdu(apdu);
                    handleResponse(apdu);
                    break;
                case '7':
                    apdu.command[Apdu.INS] = OsirisClient.INS_PIN_UNBLOCK;
                    cad.exchangeApdu(apdu);
                    handleResponse(apdu);
                    break;
                case '8':
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
    
    public static void handleResponse(Apdu apdu) {
        if (apdu.getStatus() != 0x9000) {
            System.out.println("An error occurred with status: " + apdu.getStatus());
        } else {
            System.out.println("OK");
        }
    }
}
