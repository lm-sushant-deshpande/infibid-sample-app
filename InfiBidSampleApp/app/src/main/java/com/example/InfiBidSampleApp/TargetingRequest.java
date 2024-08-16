package com.example.InfiBidSampleApp;

public class TargetingRequest {
    private String test;
    private int tmax;
    private int vw;
    private int vh;
    private String apid;
    private String apdom;
    private String apbndl;
    private String ip;
    private String[] vmimes;
    private int wpid;
    private String waid;

    // Getters and Setters

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public int getTmax() {
        return tmax;
    }

    public void setTmax(int tmax) {
        this.tmax = tmax;
    }

    public int getVw() {
        return vw;
    }

    public void setVw(int vw) {
        this.vw = vw;
    }

    public int getVh() {
        return vh;
    }

    public void setVh(int vh) {
        this.vh = vh;
    }

    public String getApid() {
        return apid;
    }

    public void setApid(String apid) {
        this.apid = apid;
    }

    public String getApdom() {
        return apdom;
    }

    public void setApdom(String apdom) {
        this.apdom = apdom;
    }

    public String getApbndl() {
        return apbndl;
    }

    public void setApbndl(String apbndl) {
        this.apbndl = apbndl;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String[] getVmimes() {
        return vmimes;
    }

    public void setVmimes(String[] vmimes) {
        this.vmimes = vmimes;
    }

    public int getWpid() {
        return wpid;
    }

    public void setWpid(int wpid) {
        this.wpid = wpid;
    }

    public String getWaid() {
        return waid;
    }

    public void setWaid(String waid) {
        this.waid = waid;
    }

    // Method to build the URL from the request parameters
    public String buildUrl(String baseUrl) {
        StringBuilder url = new StringBuilder(baseUrl);
        url.append("?test=").append(test)
                .append("&tmax=").append(tmax)
                .append("&vw=").append(vw)
                .append("&vh=").append(vh)
                .append("&apid=").append(apid)
                .append("&apdom=").append(encodeValue(apdom))
                .append("&apbndl=").append(encodeValue(apbndl))
                .append("&ip=").append(encodeValue(ip))
                .append("&vmimes=").append(encodeArray(vmimes))
                .append("&wpid=").append(wpid)
                .append("&waid=").append(encodeValue(waid));
        return url.toString();
    }

    private String encodeValue(String value) {
        return value == null ? "" : value.replace(" ", "%20"); // Simple encoding; consider using URLEncoder
    }

    private String encodeArray(String[] array) {
        if (array == null || array.length == 0) return "";
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < array.length; i++) {
            if (i > 0) sb.append(",");
            sb.append("\"").append(encodeValue(array[i])).append("\"");
        }
        sb.append("]");
        return sb.toString();
    }
}
