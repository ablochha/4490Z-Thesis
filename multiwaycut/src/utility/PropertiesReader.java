package utility;

import library.In;
import library.StdOut;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Bloch-Hansen on 2017-02-26.
 */
public class PropertiesReader {

    private InputStream input = null;
    private Properties prop = new Properties();

    public String get(String name, boolean windows) {

        try {

            if (windows) {

                input = getClass().getClassLoader().getResourceAsStream("benchmarks.properties");

            } //end if

            else {

                input = getClass().getClassLoader().getResourceAsStream("benchmarksLinux.properties");

            } //end else

            prop.load(input);
            return prop.getProperty(name);

        } catch (IOException ex) {

            ex.printStackTrace();

        } finally{

            if (input != null) {

                try {

                    input.close();

                } catch (IOException e) {

                    e.printStackTrace();

                }

            }

        }

        return null;

    } //end get

} //end PropertiesReader
