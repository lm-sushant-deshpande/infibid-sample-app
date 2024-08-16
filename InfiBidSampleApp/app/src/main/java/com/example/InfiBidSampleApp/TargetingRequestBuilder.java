package com.example.InfiBidSampleApp;

public class TargetingRequestBuilder {

    private TargetingRequest request;

    public TargetingRequestBuilder() {
        request = new TargetingRequest();
    }

    public TargetingRequestBuilder setTest(String test) {
        request.setTest(test);
        return this;
    }

    public TargetingRequestBuilder setTmax(int tmax) {
        request.setTmax(tmax);
        return this;
    }

    public TargetingRequestBuilder setVw(int vw) {
        request.setVw(vw);
        return this;
    }

    public TargetingRequestBuilder setVh(int vh) {
        request.setVh(vh);
        return this;
    }

    public TargetingRequestBuilder setApid(String apid) {
        request.setApid(apid);
        return this;
    }

    public TargetingRequestBuilder setApdom(String apdom) {
        request.setApdom(apdom);
        return this;
    }

    public TargetingRequestBuilder setApbndl(String apbndl) {
        request.setApbndl(apbndl);
        return this;
    }

    public TargetingRequestBuilder setIp(String ip) {
        request.setIp(ip);
        return this;
    }

    public TargetingRequestBuilder setVmimes(String[] vmimes) {
        request.setVmimes(vmimes);
        return this;
    }

    public TargetingRequestBuilder setWpid(int wpid) {
        request.setWpid(wpid);
        return this;
    }

    public TargetingRequestBuilder setWaid(String waid) {
        request.setWaid(waid);
        return this;
    }

    public TargetingRequest build() {
        return request;
    }

    public String buildUrl(String baseUrl) {
        return request.buildUrl(baseUrl);
    }
}
