package org.moss.lunar.database.threads;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


import org.apache.commons.io.FileUtils;
import org.moss.lunar.Lunar;
import org.moss.lunar.image.threads.ImageStorage;

public class ReadRowFilesThread implements Runnable
{

    @Override
    public void run()
    {
        File processedFileDir = new File(Lunar.OUT_DIR + Lunar.ROW_DIR
                                         + Lunar.PROCESSED_DIR);

        try
        {
            while (true)
            {
                File row = ImageStorage.rowFilesPoll();
                if (row != null)
                {
                    BufferedReader in = new BufferedReader(new FileReader(row));
                    String line;

                    String stmtBuilder = "INSERT INTO base_data2(LAT, LON, HEIGHT) VALUES ";
                    while ((line = in.readLine()) != null)
                    {
                        String[] item = line.split(",");
                        if (item.length == 2)
                        {
                            stmtBuilder += String.format("(%s, %s, %s),",
                                                         item[0], item[1], "''");
                        }
                        else if (item.length == 3)
                        {
                            stmtBuilder += String.format("(%s, %s, %s),",
                                                         item[0], item[1],
                                                         item[2]);
                        }

                    }
                    in.close();
                    in = null;
                    stmtBuilder = stmtBuilder.substring(0,
                                                        stmtBuilder.length() - 1);
                    stmtBuilder += ";";
                    ImageStorage.statementsPut(stmtBuilder);

                    // Move file
                    FileUtils.moveFileToDirectory(row, processedFileDir, false);
                }
                else
                {
                    break;
                }
            }
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("Statements created");

    }

}
