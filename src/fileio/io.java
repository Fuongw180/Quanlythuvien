/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fileio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 *
 * @author Admin-pc
 */
public class io {
    private static void writetoFile(ArrayList listObj, String file){
        FileOutputStream fos;
        ObjectOutputStream oos;
        try {
            fos = new FileOutputStream(file);
            oos = new ObjectOutputStream(fos); 
            for(Object o: listObj){
                oos.writeObject(o);
            }
            oos.close();
            fos.close();
        } catch (IOException e) {
        }
    }
    private static void readFromFile(ArrayList listObj, String file){
        FileInputStream fis;
        ObjectInputStream ois;
        try {
            fis = new FileInputStream(file);
            ois = new ObjectInputStream(fis); 
            Object obj = null;
            while((obj=ois.readObject())!=null){
                Object o = (Object) obj;
                listObj.add(o);
            }
            ois.close();
            fis.close();
        } catch (IOException | ClassNotFoundException e) {
        }
    }
}
