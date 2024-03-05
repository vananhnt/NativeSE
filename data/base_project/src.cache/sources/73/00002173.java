package gov.nist.javax.sip.header;

import gov.nist.core.Separators;
import javax.sip.InvalidArgumentException;
import javax.sip.header.TimeStampHeader;

/* loaded from: TimeStamp.class */
public class TimeStamp extends SIPHeader implements TimeStampHeader {
    private static final long serialVersionUID = -3711322366481232720L;
    protected long timeStamp;
    protected int delay;
    protected float delayFloat;
    private float timeStampFloat;

    public TimeStamp() {
        super("Timestamp");
        this.timeStamp = -1L;
        this.delay = -1;
        this.delayFloat = -1.0f;
        this.timeStampFloat = -1.0f;
        this.delay = -1;
    }

    private String getTimeStampAsString() {
        if (this.timeStamp == -1 && this.timeStampFloat == -1.0f) {
            return "";
        }
        if (this.timeStamp != -1) {
            return Long.toString(this.timeStamp);
        }
        return Float.toString(this.timeStampFloat);
    }

    private String getDelayAsString() {
        if (this.delay == -1 && this.delayFloat == -1.0f) {
            return "";
        }
        if (this.delay != -1) {
            return Integer.toString(this.delay);
        }
        return Float.toString(this.delayFloat);
    }

    @Override // gov.nist.javax.sip.header.SIPHeader
    public String encodeBody() {
        StringBuffer retval = new StringBuffer();
        String s1 = getTimeStampAsString();
        String s2 = getDelayAsString();
        if (s1.equals("") && s2.equals("")) {
            return "";
        }
        if (!s1.equals("")) {
            retval.append(s1);
        }
        if (!s2.equals("")) {
            retval.append(Separators.SP).append(s2);
        }
        return retval.toString();
    }

    @Override // javax.sip.header.TimeStampHeader
    public boolean hasDelay() {
        return this.delay != -1;
    }

    @Override // javax.sip.header.TimeStampHeader
    public void removeDelay() {
        this.delay = -1;
    }

    @Override // javax.sip.header.TimeStampHeader
    public void setTimeStamp(float timeStamp) throws InvalidArgumentException {
        if (timeStamp < 0.0f) {
            throw new InvalidArgumentException("JAIN-SIP Exception, TimeStamp, setTimeStamp(), the timeStamp parameter is <0");
        }
        this.timeStamp = -1L;
        this.timeStampFloat = timeStamp;
    }

    @Override // javax.sip.header.TimeStampHeader
    public float getTimeStamp() {
        return this.timeStampFloat == -1.0f ? Float.valueOf((float) this.timeStamp).floatValue() : this.timeStampFloat;
    }

    @Override // javax.sip.header.TimeStampHeader
    public float getDelay() {
        return this.delayFloat == -1.0f ? Float.valueOf(this.delay).floatValue() : this.delayFloat;
    }

    @Override // javax.sip.header.TimeStampHeader
    public void setDelay(float delay) throws InvalidArgumentException {
        if (delay < 0.0f && delay != -1.0f) {
            throw new InvalidArgumentException("JAIN-SIP Exception, TimeStamp, setDelay(), the delay parameter is <0");
        }
        this.delayFloat = delay;
        this.delay = -1;
    }

    @Override // javax.sip.header.TimeStampHeader
    public long getTime() {
        return this.timeStamp == -1 ? this.timeStampFloat : this.timeStamp;
    }

    @Override // javax.sip.header.TimeStampHeader
    public int getTimeDelay() {
        return this.delay == -1 ? (int) this.delayFloat : this.delay;
    }

    @Override // javax.sip.header.TimeStampHeader
    public void setTime(long timeStamp) throws InvalidArgumentException {
        if (timeStamp < -1) {
            throw new InvalidArgumentException("Illegal timestamp");
        }
        this.timeStamp = timeStamp;
        this.timeStampFloat = -1.0f;
    }

    @Override // javax.sip.header.TimeStampHeader
    public void setTimeDelay(int delay) throws InvalidArgumentException {
        if (delay < -1) {
            throw new InvalidArgumentException("Value out of range " + delay);
        }
        this.delay = delay;
        this.delayFloat = -1.0f;
    }
}