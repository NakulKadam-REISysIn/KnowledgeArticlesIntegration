package com.translations.globallink.connect.sf.model.vendor.utility.xml;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.apache.commons.lang.StringUtils;

public class XmlCDATA extends XmlAdapter<String, String> {

    @Override
    public String unmarshal(String value) throws Exception {
	return value;
    }

    @Override
    public String marshal(String value) throws Exception {
	if (StringUtils.isNotBlank(value))
	    return "<![CDATA[" + value + "]]>";
	else
	    return "<![CDATA[]]>";
    }
    
   /* @Override
    public String marshal(String value) throws Exception {
	if (StringUtils.isNotBlank(value)){
		if(value.contains( "]]")){
			String[] arr= value.split("");
			
			return "";
		}else
			return "<![CDATA[" + value + "]]>";
	}else
	    return "<![CDATA[]]>";
    }
*/

}
