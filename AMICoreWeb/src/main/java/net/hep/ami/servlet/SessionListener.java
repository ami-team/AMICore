package net.hep.ami.servlet;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import java.util.Collections;

public class SessionListener implements HttpSessionAttributeListener
{
	@Override
	public void attributeAdded(HttpSessionBindingEvent event)
	{
		String attributeName = event.getName();
		Object attributeValue = event.getValue();
		HttpSession session = event.getSession();

		System.out.println("V2 -> Attribute added : " + attributeName + " : " + attributeValue);
		Collections.list(session.getAttributeNames()).forEach(s -> { System.out.println(s + " : " + session.getAttribute(s));});
	}

	@Override
	public void attributeRemoved(HttpSessionBindingEvent event)
	{
		String attributeName = event.getName();
		Object attributeValue = event.getValue();
		HttpSession session = event.getSession();

		System.out.println("V2 -> Attribute removed : " + attributeName + " : " + attributeValue);
		Collections.list(session.getAttributeNames()).forEach(s -> { System.out.println(s + " : " + session.getAttribute(s));});

	}

	@Override
	public void attributeReplaced(HttpSessionBindingEvent event)
	{
		String attributeName = event.getName();
		Object attributeValue = event.getValue();
		HttpSession session = event.getSession();

		System.out.println("V2 -> Attribute replaced : " + attributeName + " : " + attributeValue);
		Collections.list(session.getAttributeNames()).forEach(s -> { System.out.println(s + " : " + session.getAttribute(s));});
	}
}
