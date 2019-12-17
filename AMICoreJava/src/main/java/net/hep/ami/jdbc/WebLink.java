package net.hep.ami.jdbc;

import java.util.*;
import java.util.stream.*;

import net.hep.ami.utility.parser.*;

import org.jetbrains.annotations.*;

public class WebLink
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public String m_id = "";
	public String m_class = "";
	public String m_style = "";
	public String m_unitName = "";
	public String m_unitFactor = "";
	public String m_unitBase = "";
	public String m_humanReadable = "";

	/*----------------------------------------------------------------------------------------------------------------*/

	public enum Location
	{
		NONE(""),
		BODY("body"),
		CONTAINER("container");

		private final String m_value;

		@Contract(pure = true)
		Location(@NotNull String value)
		{
			m_value = value;
		}

		@NotNull
		@Contract(pure = true)
		public String toString()
		{
			return m_value;
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static class LinkProperties
	{
		/*------------------------------------------------------------------------------------------------------------*/

		public String m_id = "";
		public String m_class = "";
		public String m_href = "";
		public String m_target = "";
		public String m_label = "";
		public String m_style = "";

		public String m_ctrl = "";
		public Location m_location = Location.NONE;
		public String m_params = "";
		public String m_settings = "";
		public String m_icon = "";
		public String m_title = "";

		/*------------------------------------------------------------------------------------------------------------*/

		public LinkProperties setId(@NotNull String id) {
			m_id = id;
			return this;
		}

		public LinkProperties setClass(@NotNull String clazz) {
			m_class = clazz;
			return this;
		}

		public LinkProperties setHRef(@NotNull String href) {
			m_href = href;
			return this;
		}

		public LinkProperties setTarget(@NotNull String target) {
			m_target = target;
			return this;
		}

		public LinkProperties setLabel(@NotNull String label) {
			m_label = label;
			return this;
		}

		public LinkProperties setStyle(@NotNull String style) {
			m_style = style;
			return this;
		}

		/*------------------------------------------------------------------------------------------------------------*/

		@NotNull
		public LinkProperties setCtrl(@NotNull String ctrl) {
			m_ctrl = ctrl;
			return this;
		}

		@NotNull
		public LinkProperties setLocation(@NotNull Location location) {
			m_location = location;
			return this;
		}

		@NotNull
		public LinkProperties setParams(@NotNull String params) {
			m_params = params;
			return this;
		}

		@NotNull
		public LinkProperties setSettings(@NotNull String settings) {
			m_settings = settings;
			return this;
		}

		@NotNull
		public LinkProperties setIcon(@NotNull String icon) {
			m_icon = icon;
			return this;
		}

		@NotNull
		public LinkProperties setTitle(@NotNull String title) {
			m_title = title;
			return this;
		}

		/*------------------------------------------------------------------------------------------------------------*/

		@NotNull
		@Override
		public String toString()
		{
			return new StringBuilder().append("<link")
			                          .append(" id=\"").append(Utility.escapeHTML(m_id)).append("\"")
			                          .append(" class=\"").append(Utility.escapeHTML(m_class)).append("\"")
			                          .append(" href=\"").append(Utility.escapeHTML(m_href)).append("\"")
			                          .append(" target=\"").append(Utility.escapeHTML(m_target)).append("\"")
			                          .append(" label=\"").append(Utility.escapeHTML(m_label)).append("\"")
			                          .append(" style=\"").append(Utility.escapeHTML(m_style)).append("\"")
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

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	private final List<LinkProperties> m_linkProperties = new ArrayList<>();

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public WebLink setId(@NotNull String id) {
		m_id = id;
		return this;
	}

	@NotNull
	public WebLink setClass(@NotNull String clazz) {
		m_class = clazz;
		return this;
	}

	@NotNull
	public WebLink setStyle(@NotNull String style) {
		m_style = style;
		return this;
	}

	@NotNull
	public WebLink setUnitName(@NotNull String unitName) {
		m_unitName = unitName;
		return this;
	}

	@NotNull
	public WebLink setUnitFactor(@NotNull String unitFactor) {
		m_unitFactor = unitFactor;
		return this;
	}

	@NotNull
	public WebLink setUnitBase(@NotNull String unitBase) {
		m_unitBase = unitBase;
		return this;
	}

	@NotNull
	public WebLink setHumanReadable(@NotNull String humanReadable) {
		m_humanReadable = humanReadable;
		return this;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public LinkProperties newLinkProperties()
	{
		LinkProperties result = new LinkProperties();

		m_linkProperties.add(result);

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public String toString()
	{
		return new StringBuilder().append("<properties")
		                          .append(" id=\"").append(Utility.escapeHTML(m_id)).append("\"")
		                          .append(" class=\"").append(Utility.escapeHTML(m_class)).append("\"")
				                  .append(" style=\"").append(Utility.escapeHTML(m_style)).append("\"")
		                          .append(" unitName=\"").append(Utility.escapeHTML(m_unitName)).append("\"")
		                          .append(" unitFactor=\"").append(Utility.escapeHTML(m_unitFactor)).append("\"")
		                          .append(" unitBase=\"").append(Utility.escapeHTML(m_unitBase)).append("\"")
		                          .append(" humanReadable=\"").append(Utility.escapeHTML(m_humanReadable)).append("\"")
		                          .append(" />")
		                          .append(m_linkProperties.stream().map(LinkProperties::toString).collect(Collectors.joining("")))
		                          .toString()
		;
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
