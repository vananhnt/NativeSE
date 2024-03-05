package javax.xml.validation;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Schema.class */
public abstract class Schema {
    public abstract Validator newValidator();

    public abstract ValidatorHandler newValidatorHandler();

    protected Schema() {
    }
}