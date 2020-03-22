package com.rdc.p2p.event;


public class LinkSocketRequestEvent {
    private String targetIp;

    public LinkSocketRequestEvent(String targetIp){
        this.targetIp = targetIp;
    }

    public String getTargetIp() {
        return targetIp == null ? "" : targetIp;
    }

    public void setTargetIp(String targetIp) {
        this.targetIp = targetIp == null ? "" : targetIp;
    }
}
