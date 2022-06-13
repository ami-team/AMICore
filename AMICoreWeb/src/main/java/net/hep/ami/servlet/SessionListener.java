package net.hep.ami.servlet;

import java.util.*;

import javax.servlet.http.*;

public class SessionListener implements HttpSessionAttributeListener
{
	@Override
	public void attributeAdded(HttpSessionBindingEvent event)
	{
		String attributeName = event.getName();
		Object attributeValue = event.getValue();
		System.out.println("V2 -> Attribute added : " + attributeName + " : " + attributeValue);

		HttpSession session = event.getSession();
		System.out.println("V2 -> Id: " + session.getId());
		Collections.list(session.getAttributeNames()).forEach(s -> { System.out.println("V2 -> " + s + " : " + session.getAttribute(s));});

		System.out.println("********************************************************************************************");
	}

	@Override
	public void attributeRemoved(HttpSessionBindingEvent event)
	{
		String attributeName = event.getName();
		Object attributeValue = event.getValue();
		System.out.println("V2 -> Attribute removed : " + attributeName + " : " + attributeValue);

		HttpSession session = event.getSession();
		System.out.println("V2 -> Id: " + session.getId());
		Collections.list(session.getAttributeNames()).forEach(s -> { System.out.println("V2 -> " + s + " : " + session.getAttribute(s));});

		System.out.println("********************************************************************************************");
	}

	@Override
	public void attributeReplaced(HttpSessionBindingEvent event)
	{
		String attributeName = event.getName();
		Object attributeValue = event.getValue();
		System.out.println("V2 -> Attribute replaced : " + attributeName + " : " + attributeValue);

		HttpSession session = event.getSession();
		System.out.println("V2 -> Id: " + session.getId());
		Collections.list(session.getAttributeNames()).forEach(s -> { System.out.println("V2 -> " + s + " : " + session.getAttribute(s));});

		System.out.println("********************************************************************************************");
	}
}
