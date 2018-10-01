package lava.rt.linq;

import java.io.Serializable;

public abstract class Entry implements Serializable,Cloneable {

	 
	


	protected abstract Class<? extends Entry> thisClass();
     public abstract Object getPk();
     
     @Override
	 public boolean equals(Object obj) {return this.toString().equals(obj.toString());}
	
     @Override
     public String toString() {
		return this.thisClass().getName()+":"+getPk();
	 }
     
    
	
	 

     
     
}
