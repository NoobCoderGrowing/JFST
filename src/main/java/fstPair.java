public class fstPair<t,v> {

    private t key;

    private v value;

    public fstPair(t key, v value) {
        this.key = key;
        this.value = value;
    }

    public void setKey(t key) {
        this.key = key;
    }

    public void setValue(v value) {
        this.value = value;
    }

    public t getKey() {
        return key;
    }

    public v getValue() {
        return value;
    }

    @Override
    public String toString() {
        return  key +
                "=" + value;
    }
}
