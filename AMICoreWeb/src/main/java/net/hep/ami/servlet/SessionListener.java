package net.hep.ami.servlet;

import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;

public class SessionListener implements HttpSessionAttributeListener
{
	@Override
	public void attributeAdded(HttpSessionBindingEvent event)
	{
		String attributeName = event.getName();
		Object attributeValue = event.getValue();
		System.out.println("V2 -> Attribute added : " + attributeName + " : " + attributeValue);
	}

	@Override
	public void attributeRemoved(HttpSessionBindingEvent event)
	{
		String attributeName = event.getName();
		Object attributeValue = event.getValue();
		System.out.println("V2 -> Attribute removed : " + attributeName + " : " + attributeValue);
	}

	@Override
	public void attributeReplaced(HttpSessionBindingEvent event)
	{
		String attributeName = event.getName();
		Object attributeValue = event.getValue();
		System.out.println("V2 -> Attribute replaced : " + attributeName + " : " + attributeValue);
	}
}
