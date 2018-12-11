package net.hep.ami.jdbc;

import java.util.*;
import java.util.stream.*;

import net.hep.ami.utility.parser.*;

public class WebLink
{
	/*---------------------------------------------------------------------*/

	public String m_id = "";
	public String m_class = "";
	public String m_css = "";
	public String m_unitName = "";
	public String m_unitFactor = "";
	public String m_unitBase = "";
	public String m_humanReadable = "";

	/*---------------------------------------------------------------------*/

	public static enum Location
	{
		NONE(""),
		BODY("body"),
		CONTAINER("container");

		private final String m_value;

		private Location(String value)
		{
			m_value = value;
		}

		public String toString()
		{
			return m_value;
		}
	}

	/*---------------------------------------------------------------------*/

	public class LinkProperties
	{
		/*-----------------------------------------------------------------*/

		public String m_id = "";
		public String m_class = "";
		public String m_css = "";
		public String m_href = "";
		public String m_target = "";
		public String m_label = "";

		public String m_ctrl = "";
		public Location m_location = Location.NONE;
		public String m_params = "";
		public String m_settings = "";
		public String m_icon = "";
		public String m_title = "";

		/*-----------------------------------------------------------------*/

		public LinkProperties setId(String id) {
			m_id = id;
			return this;
		}

		public LinkProperties setClass(String clazz) {
			m_class = clazz;
			return this;
		}

		public LinkProperties setCSS(String css) {
			m_css = css;
			return this;
		}

		public LinkProperties setHRef(String href) {
			m_href = href;
			return this;
		}

		public LinkProperties setTarget(String target) {
			m_target = target;
			return this;
		}

		public LinkProperties setLabel(String label) {
			m_label = label;
			return this;
		}

		/*-----------------------------------------------------------------*/

		public LinkProperties setCtrl(String ctrl) {
			m_ctrl = ctrl;
			return this;
		}

		public LinkProperties setLocation(Location location) {
			m_location = location;
			return this;
		}

		public LinkProperties setParams(String params) {
			m_params = params;
			return this;
		}

		public LinkProperties setSettings(String settings) {
			m_settings = settings;
			return this;
		}

		public LinkProperties setIcon(String icon) {
			m_icon = icon;
			return this;
		}

		public LinkProperties setTitle(String title) {
			m_title = title;
			return this;
		}

		/*-----------------------------------------------------------------*/

		@Override
		public String toString()
		{
			return new StringBuilder().append("<link")
			                          .append(" id=\"").append(Utility.escapeHTML(m_id)).append("\"")
			                          .append(" class=\"").append(Utility.escapeHTML(m_class)).append("\"")
			                          .append(" css=\"").append(Utility.escapeHTML(m_css)).append("\"")
			                          .append(" href=\"").append(Utility.escapeHTML(m_href)).append("\"")
			                          .append(" target=\"").append(Utility.escapeHTML(m_target)).append("\"")
			                          .append(" label=\"").append(Utility.escapeHTML(m_label)).append("\"")
			                           /**/
			                          .append(" data-ctrl=\"").append(Utility.escapeHTML(m_ctrl)).append("\"")
			                          .append(" data-ctrl-location=\"").append(m_location.toString()).append("\"")
			                          .append(" data-params=\"").append(Utility.escapeHTML(m_params)).append("\"")
			                          .append(" data-settings=\"").append(Utility.escapeHTML(m_settings)).append("\"")
			                          .append(" data-icon=\"").append(Utility.escapeHTML(m_icon)).append("\"")
			                          .append(" data-title=\"").append(Utility.escapeHTML(m_title)).append("\"")
			                          .append(" />")
			                          .toString()
			;
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private final List<LinkProperties> m_linkProperties = new ArrayList<LinkProperties>();

	/*---------------------------------------------------------------------*/

	public WebLink setId(String id) {
		m_id = id;
		return this;
	}

	public WebLink setClass(String clazz) {
		m_class = clazz;
		return this;
	}

	public WebLink setCSS(String css) {
		m_css = css;
		return this;
	}

	public WebLink setUnitName(String unitName) {
		m_unitName = unitName;
		return this;
	}

	public WebLink setUnitFactor(String unitFactor) {
		m_unitFactor = unitFactor;
		return this;
	}

	public WebLink setUnitBase(String unitBase) {
		m_unitBase = unitBase;
		return this;
	}

	public WebLink setHumanReadable(String humanReadable) {
		m_humanReadable = humanReadable;
		return this;
	}

	/*---------------------------------------------------------------------*/

	public LinkProperties newLinkProperties()
	{
		LinkProperties result = new LinkProperties();

		m_linkProperties.add(result);

		return result;
	}

	/*---------------------------------------------------------------------*/

	@Override
	public String toString()
	{
		return new StringBuilder().append("<properties")
		        .append(" id=\"").append(Utility.escapeHTML(m_id)).append("\"")
		        .append(" class=\"").append(Utility.escapeHTML(m_class)).append("\"")
		        .append(" css=\"").append(Utility.escapeHTML(m_css)).append("\"")
		        .append(" unitName=\"").append(Utility.escapeHTML(m_unitName)).append("\"")
		        .append(" unitFactor=\"").append(Utility.escapeHTML(m_unitFactor)).append("\"")
		        .append(" unitBase=\"").append(Utility.escapeHTML(m_unitBase)).append("\"")
		        .append(" humanReadable=\"").append(Utility.escapeHTML(m_humanReadable)).append("\"")
		        .append(" />")
		        .append(m_linkProperties.stream().map(x -> x.toString()).collect(Collectors.joining("")))
		        .toString()
		;
	}

	/*---------------------------------------------------------------------*/
}
