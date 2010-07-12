package de.hpi.visio;

import java.io.Reader;
import java.io.StringReader;

import org.json.JSONException;
import org.oryxeditor.server.diagram.Diagram;
import org.oryxeditor.server.diagram.JSONBuilder;
import org.xmappr.Xmappr;
import de.hpi.visio.data.VisioDocument;
               	
public class VisioToJSONConverter {
	
	private String contextPath;
	
	public VisioToJSONConverter(String contextPath) {
		this.contextPath = contextPath;
	}

	public String importVisioData(String xml, String action) {
		VisioXmlPreparator preparator = new VisioXmlPreparator();
		String preparedXml = preparator.prepareXML(xml);
		VisioDocument visioDocument = getVisioDocumentFromXML(action, preparedXml);
		VisioDataTransformator transformator = new VisioDataTransformator(contextPath, visioDocument.getStencilSet());
		Diagram diagram = transformator.createDiagramFromVisioData(visioDocument);
		String diagramJSONString = convertDiagramToJSON(diagram);
		return diagramJSONString;
	}

	private VisioDocument getVisioDocumentFromXML(String action, String preparedXml) {
		Reader reader = new StringReader(preparedXml);
		Xmappr xmappr = new Xmappr(VisioDocument.class);
		VisioDocument visioDocument = (VisioDocument) xmappr.fromXML(reader);
		if (action.toLowerCase().contains("bpmn")) {
			visioDocument.setStencilSet("bpmn");
		} else if (action.toLowerCase().contains("epc")) {
			visioDocument.setStencilSet("epc");
		}
		return visioDocument;
	}

	private String convertDiagramToJSON(Diagram diagram) {
		try {
			return JSONBuilder.parseModeltoString(diagram);
		} catch (JSONException e) {
			e.printStackTrace();
			throw new IllegalStateException("Wasn't possible to get a json representation " +
					"of the created diagram.", e);
		}
	}

}