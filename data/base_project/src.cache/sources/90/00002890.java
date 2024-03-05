package javax.xml.transform;

import java.util.Properties;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Templates.class */
public interface Templates {
    Transformer newTransformer() throws TransformerConfigurationException;

    Properties getOutputProperties();
}