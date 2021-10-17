package utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.image.*;
import java.io.*;


public class CopyImagetoClipBoard implements ClipboardOwner {
    public CopyImagetoClipBoard() {
//        captureFullScreen();
        copyPortionImage();
    }

    private void captureFullScreen() {
        try {
            Robot robot = new Robot();
            //FULL SCREEN
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();


            Rectangle screen = new Rectangle(screenSize);
            BufferedImage i = robot.createScreenCapture(screen);
            TransferableImage trans = new TransferableImage(i);
            Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
            c.setContents(trans, this);
        } catch (AWTException x) {
            x.printStackTrace();
            System.exit(1);
        }
    }

    public static void copyPortionImage() {
        try {
            PointerInfo a = MouseInfo.getPointerInfo();
            Point b = a.getLocation();
            int x = (int) b.getX();
            int y = (int) b.getY();

            Robot robot = new Robot();
            String format = "png";
            String fileName = "src/main/resources/clip/clip." + format;

            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            Rectangle captureRect = new Rectangle(x, y, 50, 50);
            BufferedImage screenFullImage = robot.createScreenCapture(captureRect);
            ImageIO.write(screenFullImage, format, new File(fileName));

            System.out.println("A partial screenshot saved!");
        } catch (AWTException | IOException ex) {
            System.err.println(ex);
        }
    }

    public static void main(String[] arg) {

        CopyImagetoClipBoard ci = new CopyImagetoClipBoard();

    }

    public void lostOwnership(Clipboard clip, Transferable trans) {
        System.out.println("Lost Clipboard Ownership");
    }

    private class TransferableImage implements Transferable {

        Image i;

        public TransferableImage(Image i) {
            this.i = i;
        }

        public Object getTransferData(DataFlavor flavor)
                throws UnsupportedFlavorException, IOException {
            if (flavor.equals(DataFlavor.imageFlavor) && i != null) {
                return i;
            } else {
                throw new UnsupportedFlavorException(flavor);
            }
        }

        public DataFlavor[] getTransferDataFlavors() {
            DataFlavor[] flavors = new DataFlavor[1];
            flavors[0] = DataFlavor.imageFlavor;
            return flavors;
        }

        public boolean isDataFlavorSupported(DataFlavor flavor) {
            DataFlavor[] flavors = getTransferDataFlavors();
            for (int i = 0; i < flavors.length; i++) {
                if (flavor.equals(flavors[i])) {
                    return true;
                }
            }

            return false;
        }
    }
}

