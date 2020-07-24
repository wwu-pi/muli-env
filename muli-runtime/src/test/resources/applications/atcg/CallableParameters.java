package applications.atcg;

class CallableParameters {
    public final Object[] params;
    public final Object result;
    public CallableParameters(Object result, Object... params) {
        this.params = params;
        this.result = result;
    }
}