package com.findwise.hydra.stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.findwise.hydra.local.LocalDocument;

/**
 * Adds a field with a specified value to the document.
 * 
 * @author joel.westberg
 */
@Stage(description = "Modifies a field with a static value. Can append values to lists, and will create lists if configured to do so.")
public class SetStaticFieldStage extends AbstractProcessStage {

	public enum Policy {
		OVERWRITE, SKIP, THROW, ADD
	};

	@Parameter(required = true, name = "fieldNames", description = "A map of fields to modify, and the values to write to them")
	private Map<String, Object> fieldValueMap;
	@Parameter(name = "overwritePolicy", description = "Switch for behaviour when modifying. Available options: 0/OVERWRITE = overwrite content, 1/SKIP = skip if there is content, 2/THROW = throw exception if there is content, 3/ADD = append to content, converting the content to a list if necessary (default)")
	private Policy overwritePolicy = Policy.ADD;

	@Override
	public void process(LocalDocument doc) throws ProcessException {
		for (Map.Entry<String, Object> entry : fieldValueMap.entrySet()) {
			if (!doc.hasContentField(entry.getKey()) || overwritePolicy == Policy.OVERWRITE) {
				doc.putContentField(entry.getKey(), entry.getValue());
			} else if (overwritePolicy == Policy.ADD) {
				addValueToField(doc, entry.getKey(), entry.getValue());
			} else if (overwritePolicy == Policy.THROW) {
				throw new ProcessException("Field " + entry.getKey()
						+ " already has a value!");
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void addValueToField(LocalDocument doc, String fieldName,
			Object fieldValue) {
		if (!doc.hasContentField(fieldName)) {
			doc.putContentField(fieldName, fieldValue);
		} else {
			Object value = doc.getContentField(fieldName);
			List<Object> list;
			if (value instanceof List) {
				list = (List<Object>) value;
			} else {
				list = new ArrayList<Object>();
				list.add(value);
			}
			list.add(fieldValue);
			doc.putContentField(fieldName, list);
		}
	}
}
