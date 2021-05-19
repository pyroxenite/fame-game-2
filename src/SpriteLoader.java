import java.util.ArrayList;
import java.util.Hashtable;
import javafx.scene.image.*;

import java.io.*;
import java.util.*;
import org.json.simple.*;
import org.json.simple.parser.*;

public class SpriteLoader {
    
    JSONParser parser = new JSONParser();

    public ArrayList<ParallaxSprite> loadBackground(String levelName) {
        ArrayList<ParallaxSprite> ret = new ArrayList<>();
        ArrayList<Image> TEST = new ArrayList<>();
        ArrayList<Image> TEST1 = new ArrayList<>();

        // ArrayList<Image> TEST = new ArrayList<>();
        // System.out.println(TEST.size());
        
        try {
            Object obj = parser.parse(new FileReader("config/levels.json"));
            JSONObject jsonObject = (JSONObject)obj;
            JSONObject spriteSheet = (JSONObject)jsonObject.get(levelName);
            
            for (Object k : spriteSheet.keySet()) {
                String key = (String)k;
                if (key.equals("biomeName")) {
                    String biomeName = (String)spriteSheet.get(key);
                    File folder = new File("images/bglayers/" + biomeName);
                    int images = folder.listFiles().length;
                    System.out.println(images);
                    for (int i = 1; i < images; i++) {
                        TEST.add(new Image("images/bglayers/" + biomeName + "/layer" + i + ".png"));
                    }
                    TEST1.add(new Image("images/bglayers/" + biomeName + "/layer0.png"));
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ParallaxSprite background = new ParallaxSprite(TEST);
        ParallaxSprite foreground = new ParallaxSprite(TEST1);
        ret.add(background);
        ret.add(foreground);

        return ret;
    }

    public Sprite loadAnimation(String assetName) {
        Hashtable<String, ArrayList<Image>> anims = new Hashtable<>();
        Hashtable<String, Double> deltas = new Hashtable<>();

        try {
            Object obj = parser.parse(new FileReader("config/assets.json"));
            JSONObject jsonObject = (JSONObject)obj;
            JSONObject spriteSheet = (JSONObject)jsonObject.get(assetName);

            for (Object k : spriteSheet.keySet()) {
                String key = (String)k;
                JSONObject data = (JSONObject)spriteSheet.get(key);
                for (Object v : data.keySet()) {
                    String dataName = (String)v;
                    
                    if (dataName.equals("path")) { //anim images
                        String path = (String)data.get(dataName);
                        int images = 0;
                        String folderPath = path.substring(0, path.lastIndexOf("/"));
                        String fileTemplate = path.substring(path.lastIndexOf("/") + 1, path.length());
                        File folder = new File(folderPath);
                        ArrayList<Image> anim = new ArrayList<>();
                        
                        //get total num of images
                        for (File img : folder.listFiles()) {
                            if (img.getName().startsWith(fileTemplate)) {
                                images++;
                            }
                        }
                        
                        //get actual images and add to list
                        for (int i = 0; i < images; i++) {
                            anim.add(new Image(path + i + ".png"));
                        }
                        anims.put(key, anim);
                    } else { //delta
                        deltas.put(key, (Double)data.get(dataName));
                    }
                }
                
                
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        return new Sprite(anims, deltas, "idle");
    }

}
