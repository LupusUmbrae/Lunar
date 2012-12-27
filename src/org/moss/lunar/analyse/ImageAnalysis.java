package org.moss.lunar.analyse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.moss.lunar.database.DbConnection;
import org.moss.lunar.image.Image;
import org.moss.lunar.image.types.PixelDto;
import org.moss.lunar.palette.PaletteProcessor;

/**
 * Runs an analysis on the image to determine the number of pixels that are
 * likely to be convertible as well as what section they are within.
 * 
 * @author Robin
 * 
 */
public class ImageAnalysis
{

    private Image image;
    private PaletteProcessor paletteProc;

    public ImageAnalysis(Image image, PaletteProcessor paletteProc)
    {
        this.image = image;
        this.paletteProc = paletteProc;
    }

    public void analyseImage(String outFilePath) throws IOException,
                                                SQLException
    {

        Connection conn = DbConnection.getConnection();

        // File to contain
        // R, G, B, X, Y, Possible, Section, Diff
        for (int x = 0; x < image.getWidth(); x++)
        {
            for (int y = 0; y < image.getHeight(); y++)
            {
                Statement stmt = conn.createStatement();

                PixelDto pixel = image.getPixel(x, y);
                String results = paletteProc.testConvertPixel(pixel);
                stmt.execute(String.format("INSERT INTO analysis VALUES(%s)",
                                           results));
                stmt.close();
            }
        }
        DbConnection.closeConnection(conn);

    }
}
