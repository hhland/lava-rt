package lava.rt.wrapper;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class FileWrapper extends BaseWrapper<File>{

	
	
	
	public FileWrapper(File self) {
		super(self);
		// TODO Auto-generated constructor stub
	}

	
	
   
    
    
    
	
	
	public Thread startWatchingDir(boolean subDir,
			final Function<File, Void> onEntryCreate,final Function<File, Void> onEntryModify,final Function<File, Void> onEntryDelete) throws Exception {
		if (!self.isDirectory())
            throw new Exception(self.getAbsolutePath() + "is not a directory!");
 
		final WatchService watcher = FileSystems.getDefault().newWatchService();
		final Map<WatchKey, Path> keys = new HashMap<WatchKey, Path>();
       
 
        Path dir = Paths.get(self.getAbsolutePath());
 
        if (subDir) {
            registerAll(dir,watcher,keys);
        } else {
            register(dir,watcher,keys);
        }
        
        Thread ret=new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				listen(subDir,watcher,keys,onEntryCreate,onEntryModify,onEntryDelete);
			}
		    	
		};
		return ret;
        
	}
	
	
	
	 @SuppressWarnings("unchecked")
	    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
	        return (WatchEvent<T>) event;
	    }
	 
	    /**
	     * 观察指定的目录
	     * 
	     * @param dir
	     * @param watcher 
	     * @param keys 
	     * @throws IOException
	     */
	    private void register(Path dir, WatchService watcher, Map<WatchKey, Path> keys) throws IOException {
	        WatchKey key = dir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
	        keys.put(key, dir);
	    }
	 
	    /**
	     * 观察指定的目录，并且包括子目录
	     */
	    private void registerAll(final Path start, WatchService watcher,Map<WatchKey, Path> keys) throws IOException {
	        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
	            @Override
	            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
	                register(dir,watcher,keys);
	                return FileVisitResult.CONTINUE;
	            }
	        });
	    }
	 
	    /**
	     * 发生文件变化的回调函数
	     * @param watcher 
	     * @param keys 
	     * @param onEntryCreate 
	     * @param onEntryModify 
	     * @param onEntryDelete 
	     */
	    @SuppressWarnings("rawtypes")
	    private void listen(boolean subDir, WatchService watcher, Map<WatchKey, Path> keys, Function<File, Void> onEntryCreate, Function<File, Void> onEntryModify, Function<File, Void> onEntryDelete) {
	        for (;;) {
	            WatchKey key;
	            try {
	                key = watcher.take();
	            } catch (InterruptedException x) {
	                return;
	            }
	            Path dir = keys.get(key);
	            if (dir == null) {
	                System.err.println("操作未识别");
	                continue;
	            }
	 
	            for (WatchEvent<?> event : key.pollEvents()) {
	                Kind kind = event.kind();
	 
	                // 事件可能丢失或遗弃
	                if (kind == StandardWatchEventKinds.OVERFLOW) {
	                    continue;
	                }
	 
	                // 目录内的变化可能是文件或者目录
	                WatchEvent<Path> ev = cast(event);
	                Path name = ev.context();
	                Path child = dir.resolve(name);
	                File file = child.toFile();
	                switch (kind.name()) {
	                
	                case "ENTRY_DELETE" : 
	                    onEntryDelete.apply(file);
	                    break;
	                case "ENTRY_CREATE" : 
	                    onEntryCreate.apply(file);
	                    break;
	                case "ENTRY_MODIFY" :
	                    onEntryModify.apply(file);
	                    break;
	                default : break;
	                }
	 
	                // if directory is created, and watching recursively, then
	                // register it and its sub-directories
	                if (subDir && (kind == StandardWatchEventKinds.ENTRY_CREATE)) {
	                    try {
	                        if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
	                            registerAll(child,watcher,keys);
	                        }
	                    } catch (IOException x) {
	                        // ignore to keep sample readbale
	                    }
	                }
	            }
	 
	            boolean valid = key.reset();
	            if (!valid) {
	                // 移除不可访问的目录
	                // 因为有可能目录被移除，就会无法访问
	                keys.remove(key);
	                // 如果待监控的目录都不存在了，就中断执行
	                if (keys.isEmpty()) {
	                    break;
	                }
	            }
	        }
	    }
	    
	    
	    public  List<String> readLines() throws FileNotFoundException, IOException{
			 List<String> ret=new ArrayList<>();
			   
			   try(    
					   FileReader fileReader=new FileReader(self);
					   BufferedReader reader = new BufferedReader(fileReader);){//换成你的文件名 
	          
	         String line = null;  
	         while((line=reader.readLine())!=null){  
	        
	             ret.add(line);  
	             
	          }
			   }
			   
			   return ret;
		}
		
		
		public void readLines(BiFunction<Integer,String,Integer> handler) throws FileNotFoundException, IOException{
			 
			   
			   try(    
					   FileReader fileReader=new FileReader(self);
					   BufferedReader reader = new BufferedReader(fileReader);){//换成你的文件名 
	         
	        String line = null;  
	        int lineIndex=0,lineStep=0;
	        while((line=reader.readLine())!=null&&lineStep>=0){  
	               if(lineStep>0) {
	            	   lineStep--;
	            	   continue;
	               }
	               lineStep=handler.apply(lineIndex,line);
	            
	         }
			}
			   
			   
		}
	    
	
}
