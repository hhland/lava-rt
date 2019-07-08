package lava.rt.linq.xml;

import lava.rt.linq.Checkpoint;
import lava.rt.linq.CommandExecuteExecption;
import lava.rt.linq.DataContext;
import lava.rt.linq.Entity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes; 
import org.xml.sax.SAXException; 
import org.xml.sax.XMLReader; 
import org.xml.sax.helpers.DefaultHandler; 
import org.xml.sax.helpers.XMLReaderFactory; 

public abstract class DocumentContext implements XmlDataContext{

	DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
	XPathFactory factory = XPathFactory.newInstance();
    XPath xpath = factory.newXPath();
	
    abstract Document[] getDocuments(); 
	
	
	@Override
	public <M extends Entity> List<M> entityList(Class<M> cls, String cmd, Object... params)
			throws CommandExecuteExecption {
		// TODO Auto-generated method stub
		List<M> ret=new ArrayList<>();
		try {
			XPathExpression expr = xpath.compile("//"+cls.getSimpleName()+cmd);
			for(Document document:getDocuments()) {
				NodeList nodes = (NodeList)expr.evaluate(document, XPathConstants.NODESET);
				
				for(int i=0;i<nodes.getLength();i++) {
					nodes.item(i);
				}
				
			}
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Object[][] executeQueryArray(String cmd, Object... params) throws CommandExecuteExecption {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String executeQueryJsonList(String cmd, Object... params) throws CommandExecuteExecption {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <E extends Entity> E entityGet(Class<E> cls, Object pk) throws CommandExecuteExecption {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <E extends Entity> int entityAddAll(Collection<E> entrys) throws CommandExecuteExecption {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int entityAdd(Entity entry) throws CommandExecuteExecption {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int entityPut(Object pk, Entity entry) throws CommandExecuteExecption {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int entityUpdate(Entity entry) throws CommandExecuteExecption {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <E extends Entity> int entityUpdateAll(Collection<E> entrys) throws CommandExecuteExecption {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int entityRemove(Entity entry) throws CommandExecuteExecption {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <E extends Entity> int entityRemoveAll(Collection<E> entrys) throws CommandExecuteExecption {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void executeSetAutoCommit(boolean b) throws CommandExecuteExecption {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void executeCommit() throws CommandExecuteExecption {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void executeRollback(Checkpoint... points) throws CommandExecuteExecption {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Checkpoint[] executeSetCheckpoint(String... points) throws CommandExecuteExecption {
		// TODO Auto-generated method stub
		return null;
	}

	public static void main(String[] args) throws Exception { 
	    
		 DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		    domFactory.setNamespaceAware(true); // never forget this!
		    DocumentBuilder builder = domFactory.newDocumentBuilder();
		    Document doc = builder.parse("books.xml");
		 
		    XPathFactory factory = XPathFactory.newInstance();
		    XPath xpath = factory.newXPath();
		    XPathExpression expr 
		     = xpath.compile("//book[author='Neal Stephenson']/title/text()");
		 
		    Object result = expr.evaluate(doc, XPathConstants.NODESET);
		    NodeList nodes = (NodeList) result;
		    for (int i = 0; i < nodes.getLength(); i++) {
		        System.out.println(nodes.item(i).getNodeValue()); 
		    }
		
	  } 
	
}
