package lava.rt.linq;

import java.io.Serializable;

public abstract class Entry implements Serializable,Cloneable {

	

	protected abstract Class<? extends Entry> thisClass();
	 


     
     @Override
	 public boolean equals(Object obj) {return this.toString().equals(obj.toString());}
	
    
     
    
	 




	
     
     
}
