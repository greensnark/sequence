<?xml version='1.0' encoding='ISO-8859-1' ?>
<!DOCTYPE helpset
  PUBLIC "-//Sun Microsystems Inc.//DTD JavaHelp HelpSet Version 2.0//EN"
         "http://java.sun.com/products/javahelp/helpset_2_0.dtd">
<helpset version="2.0">

    <title>SEQUENCE</title>

    <maps>
        <homeID>top</homeID>
        <mapref location="sequence.jhm"/>
    </maps>

    <view xml:lang="en" mergetype="javax.help.UniteAppendMerge">
        <name>TOC</name>
        <label>Table of Contents</label>
        <type>javax.help.TOCView</type>
        <data>sequence_toc.xml</data>
    </view>

    <view xml:lang="en" mergetype="javax.help.SortMerge">
        <name>Index</name>
        <label>Index</label>
        <type>javax.help.IndexView</type>
        <data>sequence_index.xml</data>
    </view>

    <presentation default="true">
        <name>main</name>
        <size width="790" height="400"/>
        <location x="10" y="200"/>
        <title>SEQUENCE Help</title>
        <toolbar>
            <helpaction>javax.help.BackAction</helpaction>
            <helpaction>javax.help.ForwardAction</helpaction>
            <helpaction image="homeicon" >javax.help.HomeAction</helpaction>
        </toolbar>
    </presentation>

    <impl>
        <helpsetregistry helpbrokerclass="javax.help.DefaultHelpBroker"/>
        <viewerregistry viewertype="text/html" viewerclass="com.sun.java.help.impl.CustomKit"/>
        <viewerregistry viewertype="text/xml" viewerclass="com.sun.java.help.impl.CustomXMLKit"/>
    </impl>
</helpset>