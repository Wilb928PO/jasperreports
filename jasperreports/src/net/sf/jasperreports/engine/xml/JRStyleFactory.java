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
	private static final String ATTRIBUTE_positionType = "positionType";
	private static final String ATTRIBUTE_stretchType = "stretchType";
	private static final String ATTRIBUTE_name = "name";
	private static final String ATTRIBUTE_forecolor = "forecolor";
	private static final String ATTRIBUTE_backcolor = "backcolor";
	private static final String ATTRIBUTE_style = "style";

	private static final String ATTRIBUTE_pen = "pen";
	private static final String ATTRIBUTE_fill = "fill";

	/**
	 *
	 */
	public Object createObject(Attributes atts)
	{
		JRDesignStyle style = new JRDesignStyle();
		JRXmlLoader xmlLoader = (JRXmlLoader)digester.peek(digester.getCount() - 1);
		JasperDesign jasperDesign = (JasperDesign)digester.peek(digester.getCount() - 2);

		style.setName(atts.getValue(ATTRIBUTE_name));

		if (atts.getValue(ATTRIBUTE_style) != null)
		{
			Map stylesMap = jasperDesign.getStylesMap();

			if ( !stylesMap.containsKey(atts.getValue(ATTRIBUTE_style)) )
			{
				xmlLoader.addError(new Exception("Unknown report style : " + atts.getValue(ATTRIBUTE_style)));
			}

			style.setParentStyle((JRStyle) stylesMap.get(atts.getValue(ATTRIBUTE_style)));
		}




		Byte positionType = (Byte)JRXmlConstants.getPositionTypeMap().get(atts.getValue(ATTRIBUTE_positionType));
		if (positionType != null)
		{
			style.setPositionType(positionType.byteValue());
		}

		Byte stretchType = (Byte)JRXmlConstants.getStretchTypeMap().get(atts.getValue(ATTRIBUTE_stretchType));
		if (stretchType != null)
		{
			style.setStretchType(stretchType.byteValue());
		}

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


		Byte pen = (Byte)JRXmlConstants.getPenMap().get(atts.getValue(ATTRIBUTE_pen));
		if (pen != null)
		{
			style.setPen(pen.byteValue());
		}

		Byte fill = (Byte)JRXmlConstants.getFillMap().get(atts.getValue(ATTRIBUTE_fill));
		if (fill != null)
		{
			style.setFill(fill.byteValue());
		}



		return style;
	}
}
