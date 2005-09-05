package net.sf.jasperreports.engine.xml;

import java.awt.Color;
import java.util.Map;

import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.design.JRDesignStyle;
import net.sf.jasperreports.engine.design.JasperDesign;

import org.xml.sax.Attributes;

/**
 * @author Ionut Nedelcu (ionutned@users.sourceforge.net)
 * @version $Id
 */
public class JRStyleFactory extends JRBaseFactory
{
	private static final String ATTRIBUTE_name = "name";
	private static final String ATTRIBUTE_forecolor = "forecolor";
	private static final String ATTRIBUTE_backcolor = "backcolor";
	private static final String ATTRIBUTE_style = "style";


	/**
	 *
	 */
	public Object createObject(Attributes atts)
	{
		JRDesignStyle style = new JRDesignStyle();

		style.setName(atts.getValue(ATTRIBUTE_name));

		String forecolor = atts.getValue(ATTRIBUTE_forecolor);
		if (forecolor != null && forecolor.length() > 0)
		{
			char firstChar = forecolor.charAt(0);
			if (firstChar == '#')
			{
				style.setForecolor(new Color(Integer.parseInt(forecolor.substring(1), 16)));
			}
			else if ('0' <= firstChar && firstChar <= '9')
			{
				style.setForecolor(new Color(Integer.parseInt(forecolor)));
			}
			else
			{
				if (JRXmlConstants.getColorMap().containsKey(forecolor))
				{
					style.setForecolor((Color)JRXmlConstants.getColorMap().get(forecolor));
				}
				else
				{
					style.setForecolor(Color.black);
				}
			}
		}

		String backcolor = atts.getValue(ATTRIBUTE_backcolor);
		if (backcolor != null && backcolor.length() > 0)
		{
			char firstChar = backcolor.charAt(0);
			if (firstChar == '#')
			{
				style.setBackcolor(new Color(Integer.parseInt(backcolor.substring(1), 16)));
			}
			else if ('0' <= firstChar && firstChar <= '9')
			{
				style.setBackcolor(new Color(Integer.parseInt(backcolor)));
			}
			else
			{
				if (JRXmlConstants.getColorMap().containsKey(backcolor))
				{
					style.setBackcolor((Color)JRXmlConstants.getColorMap().get(backcolor));
				}
				else
				{
					style.setBackcolor(Color.white);
				}
			}
		}

		JRXmlLoader xmlLoader = (JRXmlLoader)digester.peek(digester.getCount() - 1);
		JasperDesign jasperDesign = (JasperDesign)digester.peek(digester.getCount() - 2);
		if (atts.getValue(ATTRIBUTE_style) != null)
		{
			Map stylesMap = jasperDesign.getStylesMap();

			if ( !stylesMap.containsKey(atts.getValue(ATTRIBUTE_style)) )
			{
				xmlLoader.addError(new Exception("Unknown report style : " + atts.getValue(ATTRIBUTE_style)));
			}

			style.setParentStyle((JRStyle) stylesMap.get(atts.getValue(ATTRIBUTE_style)));
		}

		return style;
	}
}
