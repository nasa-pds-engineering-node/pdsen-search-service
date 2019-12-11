package gov.nasa.pds.data.pds4.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.TreeMap;


public class IndexLatestFiles
{
    private static class FileInfo
    {
        String fileName;
        String lid;
        float vid;
    }

    
    private Map<String, FileInfo> fileInfoMap;
    
    
    public IndexLatestFiles()
    {
        fileInfoMap = new TreeMap<>();
    }
    
    
    public void crawl(Path folder) throws Exception
    {
        Files.walk(folder).filter(p -> p.toString().endsWith(".xml")).forEach(p -> 
        {
            String fileName = p.getFileName().toString();
            FileInfo fi = extractLidVid(fileName);
            if(fi == null)
            {
                System.out.println("WARNING: Unknown file name format: " + fileName);
            }
            else
            {
                updateMap(fi);
            }
        });
    }
    
    
    public void writeIndexFile(Path folder) throws Exception
    {
        
        File file = new File(folder.toFile(), "latest.idx");
        BufferedWriter wr = new BufferedWriter(new FileWriter(file));
        
        for(FileInfo fi: fileInfoMap.values())
        {
            wr.write(fi.fileName + "\n");
        }
        
        wr.close();
    }
    
    
    public static void main(String[] args) throws Exception
    {
        if(args.length != 1)
        {
            System.out.println("Usage: IndexLatestFiles <directory>");
            System.exit(1);
        }
        
        Path folder = Paths.get(args[0]);
        if(!Files.isDirectory(folder))
        {
            System.out.println(folder + " is not a directory.");
            System.exit(1);
        }

        IndexLatestFiles app = new IndexLatestFiles();
        app.crawl(folder);
        app.writeIndexFile(folder);
    }

    
    private static FileInfo extractLidVid(String fileName)
    {
        FileInfo fi = new FileInfo();
        fi.fileName = fileName;
        
        int idx = fileName.lastIndexOf('_');
        if(idx < 0) return null;

        fi.lid = fileName.substring(0, idx);
        
        String strVer = fileName.substring(idx+1, fileName.length()-4);
        fi.vid = Float.parseFloat(strVer);

        return fi;
    }


    private void updateMap(FileInfo fi)
    {
        FileInfo current = fileInfoMap.get(fi.lid);
        if(current == null || fi.vid > current.vid)
        {
            fileInfoMap.put(fi.lid, fi);
        }
    }
}
