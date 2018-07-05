package com.microsoft.appcenter.ingestion.models.one;

import com.microsoft.appcenter.ingestion.models.AbstractLog;
import com.microsoft.appcenter.ingestion.models.json.JSONDateUtils;
import com.microsoft.appcenter.ingestion.models.json.JSONUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

/**
 * Common schema has 1 log type with extensions, everything is called an event.
 * Part B can be used in the future for domain specific typing (like reflecting AppCenter log type).
 */
public abstract class CommonSchemaLog extends AbstractLog {

    /**
     * Common schema version property.
     */
    private static final String VER = "ver";

    /**
     * Name property.
     */
    private static final String NAME = "name";

    /**
     * Time property.
     */
    private static final String TIME = "time";

    /**
     * popSample property.
     */
    private static final String POP_SAMPLE = "popSample";

    /**
     * iKey property.
     */
    private static final String IKEY = "iKey";

    /**
     * Flags property.
     */
    private static final String FLAGS = "flags";

    /**
     * Flags property.
     */
    private static final String CV = "cV";

    /**
     * Extensions property.
     */
    private static final String EXT = "ext";

    /**
     * Data property.
     */
    private static final String DATA = "data";

    /**
     * Common schema version.
     */
    private String ver;

    /**
     * Event name.
     */
    private String name;

    /**
     * The effective sample rate for this event at the time it was generated by a client.
     * The valid range is from a minimum value of one out of every 100 million devices which is "0.000001",
     * all the way up to all devices which is "100".
     * If this field does not exist then you should assume its value is 100.
     */
    private Double popSample;

    /**
     * An identifier used to identify applications or other logical groupings of events.
     */
    private String iKey;

    /**
     * Flags bitmask (latency, persistence, sensitivity).
     */
    private Long flags;

    /**
     * Correlation vector.
     */
    private String cV;

    /**
     * Part A Extensions.
     */
    private Extensions ext;

    /**
     * Data (parts B and C).
     */
    private Data data;

    /**
     * Get common schema version.
     *
     * @return common schema version.
     */
    public String getVer() {
        return ver;
    }

    /**
     * Set common schema version.
     *
     * @param ver common schema version.
     */
    public void setVer(String ver) {
        this.ver = ver;
    }

    /**
     * Get event name.
     *
     * @return event name.
     */
    public String getName() {
        return name;
    }

    /**
     * Set event name.
     *
     * @param name event name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get pop sample.
     *
     * @return pop sample.
     */
    @SuppressWarnings("WeakerAccess")
    public Double getPopSample() {
        return popSample;
    }

    /**
     * Set pop sample
     *
     * @param popSample pop sample.
     */
    public void setPopSample(Double popSample) {
        this.popSample = popSample;
    }

    /**
     * Get instrumentation key.
     *
     * @return instrumentation key.
     */
    @SuppressWarnings("WeakerAccess")
    public String getIKey() {
        return iKey;
    }

    /**
     * Set instrumentation key.
     *
     * @param iKey instrumentation key.
     */
    public void setIKey(String iKey) {
        this.iKey = iKey;
    }

    /**
     * Get flags.
     *
     * @return flags.
     */
    public Long getFlags() {
        return flags;
    }

    /**
     * Set flags.
     *
     * @param flags flags.
     */
    public void setFlags(Long flags) {
        this.flags = flags;
    }

    /**
     * Get correlation vector.
     *
     * @return correlation vector.
     */
    @SuppressWarnings("WeakerAccess")
    public String getCV() {
        return cV;
    }

    /**
     * Set correlation vector.
     *
     * @param cV correlation vector.
     */
    public void setCV(String cV) {
        this.cV = cV;
    }

    /**
     * Get Part A extensions.
     *
     * @return Part A extensions.
     */
    public Extensions getExt() {
        return ext;
    }

    /**
     * Set Part A extensions.
     *
     * @param ext Part A extensions.
     */
    public void setExt(Extensions ext) {
        this.ext = ext;
    }

    /**
     * Get Parts B and C.
     *
     * @return Parts B and C.
     */
    public Data getData() {
        return data;
    }

    /**
     * Set Parts B and C.
     *
     * @param data Parts B and C.
     */
    public void setData(Data data) {
        this.data = data;
    }

    @Override
    public void read(JSONObject object) throws JSONException {

        /* Override abstract log JSON since it's Common Schema and not App Center schema. */

        /* Read top level PART A simple fields. */
        setVer(object.getString(VER));
        setName(object.getString(NAME));
        setTimestamp(JSONDateUtils.toDate(object.getString(TIME)));
        if (object.has(POP_SAMPLE)) {
            setPopSample(object.getDouble(POP_SAMPLE));
        }
        setIKey(object.optString(IKEY, null));
        setFlags(JSONUtils.readLong(object, FLAGS));
        setCV(object.optString(CV, null));

        /* Read extensions. */
        if (object.has(EXT)) {
            Extensions extensions = new Extensions();
            extensions.read(object.getJSONObject(EXT));
            setExt(extensions);
        }

        /* Read Parts B&C. */
        if (object.has(DATA)) {
            Data data = new Data();
            data.read(object.getJSONObject(DATA));
            setData(data);
        }
    }

    @Override
    public void write(JSONStringer writer) throws JSONException {

        /* Override abstract log JSON since it's Common Schema and not App Center schema. */

        /* Part A. */
        writer.key(VER).value(getVer());
        writer.key(NAME).value(getName());
        writer.key(TIME).value(JSONDateUtils.toString(getTimestamp()));
        JSONUtils.write(writer, POP_SAMPLE, getPopSample());
        JSONUtils.write(writer, IKEY, getIKey());
        JSONUtils.write(writer, FLAGS, getFlags());
        JSONUtils.write(writer, CV, getCV());

        /* Part A extensions. */
        if (getExt() != null) {
            writer.key(EXT).object();
            getExt().write(writer);
            writer.endObject();
        }

        /* Parts B & C. */
        if (getData() != null) {
            writer.key(DATA).object();
            getData().write(writer);
            writer.endObject();
        }
    }

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        CommonSchemaLog that = (CommonSchemaLog) o;

        if (ver != null ? !ver.equals(that.ver) : that.ver != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (popSample != null ? !popSample.equals(that.popSample) : that.popSample != null)
            return false;
        if (iKey != null ? !iKey.equals(that.iKey) : that.iKey != null) return false;
        if (flags != null ? !flags.equals(that.flags) : that.flags != null) return false;
        if (cV != null ? !cV.equals(that.cV) : that.cV != null) return false;
        if (ext != null ? !ext.equals(that.ext) : that.ext != null) return false;
        return data != null ? data.equals(that.data) : that.data == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (ver != null ? ver.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (popSample != null ? popSample.hashCode() : 0);
        result = 31 * result + (iKey != null ? iKey.hashCode() : 0);
        result = 31 * result + (flags != null ? flags.hashCode() : 0);
        result = 31 * result + (cV != null ? cV.hashCode() : 0);
        result = 31 * result + (ext != null ? ext.hashCode() : 0);
        result = 31 * result + (data != null ? data.hashCode() : 0);
        return result;
    }
}
